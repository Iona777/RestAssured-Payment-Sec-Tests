package com.pps.qa.paymentsecurity.stepdefinitions.encryptionkeys;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_KEY_CREATE;
import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_KEY_DELETE;
import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_KEY_READ;
import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_KEY_READ_VALUE;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptionKeyNoManagementKeyVariant;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptionKeyOnlyVariantResource;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.getVariantValueByKeyType;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.EncryptionKeyRequestResource;
import com.pps.dsl.paymentsecurity.domain.EncryptionKeyResponseResource;
import com.pps.dsl.paymentsecurity.domain.EncryptionMultipleKeysResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.TypeDto;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.SoftAssertions;


/**
 * Holds the step definitions for the tests defined in migrate_keys_variant_to_key_block.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */
@Slf4j
public class MigrateKeysToKeyBlockStepDefs {

    private static final String[] ZMKs_IDs_FOR_ZEK = {"c3be4b32-fe86-473d-9166-da1d77a4a96b",
            "aaf1ee6b-216c-4d7b-aebb-45fa8685174a"};
    private static final String ZEK_ID = "fff3197c-bb42-4307-94d9-2a5694492612";
    private static final String DEFAULT_MANAGEMENT_KEY_ID = "c3be4b32-fe86-473d-9166-da1d77a4a96b";
    private static final String EMBED_QUERY_PARAM = "encrypted_key_value";

    /**
     * Framework related variables
     */
    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;

    /**
     * Request/Response values for these tests
     */
    private EncryptionKeyRequestResource requestPayload;
    private EncryptionKeyResponseResource singleKeyResponse;
    private EncryptionMultipleKeysResponseResource multikeyResponse;
    private RestResponse restResponse;
    private String code;
    private String keyType = "ZEK"; //default value, can be overridden
    private String encryptionKeyId;

    public MigrateKeysToKeyBlockStepDefs(PaymentSecurityCommon paySecCommon) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
    }

    @Before()
    public void initPaymentSecurityDB() {
        cleanUpDatabase("PanKey");
        cleanUpDatabase("Rom2Enk");
    }

    @Given("a key with Zone A, Group B, and Code {string} does not exist")
    public void aKeyWithZoneAGroupBAndCodeDoesNotExist(String code) {

        //Get all the keys with given code. We need a multikeyResponse for this as this endpoint returns a list of items
        //of type EncryptionKeyResponseResource
        multikeyResponse = getEncryptionKeysByCode(code);

        softAssertions.assertThat(multikeyResponse.size()).as("Check response size is zero").isEqualTo(0);
    }

    @When("a key is created with Zone {string}, Group {string}, Code {string} and type {string}")
    public void aKeyIsCreatedWithZoneGroupCodeAndType(String zone, String group, String code, String keyType) {
        if (keyType.equals("ZEK")) {
            requestPayload = createEncryptionKeyOnlyVariantResource();
        } else {
            requestPayload = createEncryptionKeyNoManagementKeyVariant();
        }
        setZone(zone);
        setGroup(group);
        setCode(code);
        setType(keyType);
        setCheckValueByKeyType(keyType);
        this.keyType = keyType;

    }

    @And("a POST request is made to the encryption-keys endpoint with a variant value as the key")
    public void aPOSTRequestIsMadeToTheEncryptionKeysEndpointWithAVariantValueAsTheKey() {
        createEncryptionKeyWithVariantValueEmbedQuery(keyType);
    }

    @Then("the request to the encryption-keys endpoint to request a Key Encryption returns an http status code of {int}")
    public void theRequestToTheEncryptionKeysEndpointToRequestAKeyEncryptionReturnsAnHttpStatusCodeOf(
            int expectedStatus) {
        this.paySecCommon.theCallReturnsAnHttpStatusCodeOf(this.restResponse.httpCode(), expectedStatus);
    }

    @Then("the key should be successfully created with code {string}")
    public void theKeyShouldBeSuccessfullyCreatedWithCode(String code) {
        multikeyResponse = getEncryptionKeysByCode(code);
        int numberOfKeys = (int) multikeyResponse.size();

        String keyOnDB = multikeyResponse.items().get(numberOfKeys - 1).id();
        String createdKeyId = this.encryptionKeyId;

        softAssertions.assertThat(keyOnDB).as("Check created key matches key on DB").isEqualTo(createdKeyId);

    }

    @And("Keyblock value for the key should have been generated through migration")
    public void keyblockValueForTheKeyShouldHaveBeenGeneratedThroughMigration() {
        softAssertions.assertThat(verifyKeyForGivenCode(this.code)).
                as("check for keyblock and variant value").isTrue();
    }

    @And("create a ZMK with both Variant and Keyblock values if one does not exist already")
    public void createAZMKWithBothVariantAndKeyblockValuesIfOneDoesNotExistAlready() {
        if (verifyKeyForGivenCode("hsmEncryptionZmk") == false) {
            aKeyIsCreatedWithZoneGroupCodeAndType("A", "B", "hsmEncryptionZmk", "ZMK");
        }
    }

    @And("the key value is under the variant management ZMK key")
    public void theKeyValueIsUnderTheVariantManagementZMKKey() {
        setManagementKeyIdByKeyType("ZMK");
    }

    @And("Keyblock value for the key should have been generated through migration using the existing ZMK")
    public void keyblockValueForTheKeyShouldHaveBeenGeneratedThroughMigrationUsingTheExistingZMK() {
        softAssertions.assertThat(verifyKeyForGivenCode(this.code)).
                as("check for keyblock and variant value").isTrue();

        softAssertions.assertThat(verifyManagementKey(this.code)).
                as("check management key").isTrue();
    }

    /**
     * Sets the management key id. The keyType should be one of {ZMK, ZEK or random}
     */
    protected void setManagementKeyIdByKeyType(String keyType) {
        String managementKeyId;

        switch (keyType) {
            case "ZMK": {
                managementKeyId = ZMKs_IDs_FOR_ZEK[0];
                break;
            }
            case "ZEK": {
                managementKeyId = ZEK_ID;
                break;
            }
            case "random": {
                managementKeyId = UUID.randomUUID().toString();
                break;
            }
            default: {
                managementKeyId = "Key type not set.";
                break;
            }
        }
        requestPayload.encryptedKeyValue().managementKey().id(managementKeyId);
    }

    /**
     * Delete all keys from DB that have the given code
     *
     * @param code The (well known) given identifier of the cryptographic key.
     */
    public void cleanUpDatabase(String code) {
        //Get all the keys that currently exist with given code

        multikeyResponse = getEncryptionKeysByCode(code);

        for (int i = 0; i < multikeyResponse.size(); i++) {
            String currentId = multikeyResponse.items().get(i).id();
            deleteEncryptionKeyByCode(currentId);
        }

    }

    /**
     * Sets the value for zone
     *
     * @param zone A scope defining the zone in which the encryption key may be used where keys are partitioned for
     *             different systems
     */
    private void setZone(String zone) {
        requestPayload.scope().zone(zone);
    }

    /**
     * Sets the value for group
     *
     * @param group A scope defining the group for the key
     */
    private void setGroup(String group) {
        String setGroup = group.equals("none") ? null : group;

        requestPayload.scope().group(setGroup);
    }

    /**
     * Sets the value for code
     *
     * @param code The (well known) given identifier of the cryptographic key
     */
    private void setCode(String code) {
        this.code = code;
        requestPayload.code(code);
    }

    /**
     * Sets the value for type
     */
    private void setType(String type) {
        requestPayload.type(TypeDto.valueOf(type));
    }

    /**
     * Sets the value of check value based on the given keyType
     *
     * @param keyType type of key
     */
    private void setCheckValueByKeyType(String keyType) {
        switch (keyType) {
            case "ZMK":
                setCheckValue("0A5687");
                break;
            case "ZEK":
                setCheckValue("62D017");
                break;
            case "ZPK":
                setCheckValue("8E190F");
                break;
            case "CVK":
                setCheckValue("870376");
                break;
            case "PVK":
                setCheckValue("");
                break;
            default:
                setCheckValue("random");
                break;
        }
    }

    /**
     * Sets the value of checkValue
     *
     * @param checkValue check value
     */
    private void setCheckValue(String checkValue) {
        requestPayload.encryptedKeyValue().checkValue(checkValue);
    }

    /**
     * Makes a POST request to Payment Security application encryption keys endpoint with an encryption key payload
     *
     * @return an EncryptionKeyResponseResource
     */
    private EncryptionKeyResponseResource postEncryptionKeysWithEmbedQuery() {
        List<String> permissionList = new ArrayList<>();

        permissionList.add(ENCRYPTION_KEY_CREATE);
        permissionList.add(ENCRYPTION_KEY_READ_VALUE);

        this.restResponse = PaymentSecurityDsl.app(this.paySecCommon.paymentSecurityUrl)
                .encryptionKeysClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .authorization(generateToken(permissionList, null))
                .with(requestPayload)
                .queryParams()
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitPost();

        return this.restResponse.body(EncryptionKeyResponseResource.class);
    }

    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by a given
     * code
     *
     * @param code The (well known) given identifier of the cryptographic key
     * @return an EncryptionKeyResponseResource
     */
    private EncryptionMultipleKeysResponseResource getEncryptionKeysByCode(String code) {
        List<String> permissionList = new ArrayList<>();
        permissionList.add(ENCRYPTION_KEY_READ);
        permissionList.add(ENCRYPTION_KEY_READ_VALUE);

        this.restResponse = PaymentSecurityDsl.app(this.paySecCommon.paymentSecurityUrl)
                .encryptionKeysClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .authorization(generateToken(permissionList, null))
                .queryParams()
                .code(code)
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }

    /**
     * Makes a DELETE request to Payment Security application encryption keys endpoint for a given encryption key
     *
     * @param id The unique id of the encryption key.
     */
    private void deleteEncryptionKeyByCode(String id) {
        this.restResponse = PaymentSecurityDsl.app(this.paySecCommon.paymentSecurityUrl)
                .encryptionKeysClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .authorization(generateToken(ENCRYPTION_KEY_DELETE))
                .submitDelete(id);

    }

    /**
     * Creates the request payload and calls postEncryptionKeysWithEmbedQuery() to create an encryption key with a
     * variant value and embedded query parameter.
     *
     * @param keyType type of key
     */
    protected void createEncryptionKeyWithVariantValueEmbedQuery(String keyType) {
        String variantValue = getVariantValueByKeyType(keyType);
        requestPayload.encryptedKeyValue().variantValue(variantValue);
        requestPayload.index(Long.parseLong(RandomStringUtils.randomNumeric(6)));

        this.singleKeyResponse = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = singleKeyResponse.id();
    }

    /**
     * Verify that, for the given code, the management key set earlier matches the default management key id.
     *
     * @param codeToVerifyWith filter response by this code
     * @return true or false
     */
    private boolean verifyManagementKey(String codeToVerifyWith) {
        multikeyResponse = getEncryptionKeysByCode(codeToVerifyWith);
        int numberOfKeys = (int) multikeyResponse.size();

        String actualkey;
        actualkey = multikeyResponse.items().get(numberOfKeys - 1).encryptedKeyValue().managementKey().id();

        return actualkey.equals(DEFAULT_MANAGEMENT_KEY_ID);
    }

    /**
     * Verify that, for the given code, the variantValue and keyblockValue within the encryptedKeyValue are not empty.
     *
     * @param codeToVerifyWith filter response by this code
     * @return true or false
     */
    private boolean verifyKeyForGivenCode(String codeToVerifyWith) {
        multikeyResponse = getEncryptionKeysByCode(codeToVerifyWith);
        int numberOfKeys = (int) multikeyResponse.size();

        String variantValue = multikeyResponse.items().get(numberOfKeys - 1).encryptedKeyValue().variantValue();
        String keyblockValue = multikeyResponse.items().get(numberOfKeys - 1).encryptedKeyValue().keyblockValue();

        if (variantValue.isEmpty() || keyblockValue.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
}
