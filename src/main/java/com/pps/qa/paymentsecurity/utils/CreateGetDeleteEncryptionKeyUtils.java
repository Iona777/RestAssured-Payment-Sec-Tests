package com.pps.qa.paymentsecurity.utils;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_KEY_CREATE;
import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_KEY_DELETE;
import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_KEY_READ;
import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_KEY_READ_VALUE;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;
import static com.pps.qa.paymentsecurity.databuilders.CreateGetDeleteEncryptionKeysDataBuilder.createEncryptionKeyBothValueResource;
import static com.pps.qa.paymentsecurity.databuilders.CreateGetDeleteEncryptionKeysDataBuilder.createEncryptionKeyNoManagementBothValues;
import static com.pps.qa.paymentsecurity.databuilders.CreateGetDeleteEncryptionKeysDataBuilder.createEncryptionKeyNoManagementKeyKeyblock;
import static com.pps.qa.paymentsecurity.databuilders.CreateGetDeleteEncryptionKeysDataBuilder.createEncryptionKeyNoValueResource;
import static com.pps.qa.paymentsecurity.databuilders.CreateGetDeleteEncryptionKeysDataBuilder.createEncryptionKeyOnlyKeyblockResource;
import static com.pps.qa.paymentsecurity.databuilders.CreateGetDeleteEncryptionKeysDataBuilder.getKeyblockValueByKeyType;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptionKeyNoManagementKeyVariant;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptionKeyNoManagementNoValue;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptionKeyOnlyVariantResource;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.getVariantValueByKeyType;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.EncryptionKeyRequestResource;
import com.pps.dsl.paymentsecurity.domain.EncryptionKeyResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.ScopeDto;
import com.pps.dsl.paymentsecurity.domain.dto.StatusDto;
import com.pps.dsl.paymentsecurity.domain.dto.TypeDto;
import com.pps.dsl.rest.ErrorResponse;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.SoftAssertions;

/**
 * Holds the step definitions for the tests defined in create_get_delete_encryption_key.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */

public class CreateGetDeleteEncryptionKeyUtils {

    private static final String EMBED_QUERY_PARAM = "encrypted_key_value";
    private static final String DEFAULT_MANAGEMENT_KEY_ZONE = "ServicePlatform";
    private static final String DEFAULT_MANAGEMENT_KEY_GROUP = "KeyMigration";
    private static final String DEFAULT_MANAGEMENT_KEY_CODE = "hsmEncryptionZmk";
    private static final String[] ZMKs_IDs_FOR_ZEK = {"c3be4b32-fe86-473d-9166-da1d77a4a96b",
            "aaf1ee6b-216c-4d7b-aebb-45fa8685174a"};
    private static final String ZEK_ID = "fff3197c-bb42-4307-94d9-2a5694492612";
    private static final Map<String, String> keyblocksEncryptedUnderZmks = new HashMap<>();

    /**
     * Framework related variables
     */
    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;
    /**
     * Request/Response values for these tests
     */
    private EncryptionKeyRequestResource requestPayload;
    private EncryptionKeyResponseResource response;
    private RestResponse restResponse;
    /**
     * Variables and constants for the class
     */

    private String encryptionKeyId;
    private long index;
    private String variantValue;
    private String type;
    private String algorithm;
    private String zone;
    private String group;
    private String bin;
    private String managementKeyId;
    private String keyblockValue;

    public CreateGetDeleteEncryptionKeyUtils(PaymentSecurityCommon paySecCommon) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
    }

    /**
     * Populates the request payload with an {@link EncryptionKeyRequestResource} object with Variant Resource set
     */
    public void createDefaultPayloadEncryptionKeyOnlyVariant() {
        requestPayload = createEncryptionKeyOnlyVariantResource();
    }

    /**
     * Populates the request payload with an {@link EncryptionKeyRequestResource} object with No Management Key Variant
     * set
     */
    public void createDefaultPayloadEncryptionKeyNoManagementKeyVariant() {
        requestPayload = createEncryptionKeyNoManagementKeyVariant();
    }

    /**
     * Populates the request payload with an {@link EncryptionKeyRequestResource} object with Keyblock Resource set
     */
    public void createDefaultPayloadEncryptionKeyOnlyKeyblock() {
        requestPayload = createEncryptionKeyOnlyKeyblockResource();
    }

    /**
     * Populates the request payload with an {@link EncryptionKeyRequestResource} object with encryptedKeyValue, scope
     * and keyblockValue set
     */
    public void createDefaultPayloadEncryptionKeyBothValues() {
        requestPayload = createEncryptionKeyBothValueResource();
    }

    /**
     * Populates the request payload with an {@link EncryptionKeyRequestResource} object with encryptedKeyValue, scope
     * and keyblockValue set and managementKey set to null
     */
    public void createDefaultPayloadEncryptionKeyNoManagementKeyKeyblock() {
        requestPayload = createEncryptionKeyNoManagementKeyKeyblock();
    }

    /**
     * Populates the request payload with an {@link EncryptionKeyRequestResource} object with  encryptedKeyValue, scope
     * and keyblockValue set
     */
    public void createDefaultPayloadEncryptionKeyNoManagementKeyBothValues() {
        requestPayload = createEncryptionKeyNoManagementBothValues();
    }

    /**
     * Populates the request payload with an {@link EncryptionKeyRequestResource} object with  no additional values set
     */
    public void createDefaultPayloadEncryptionKeyNoManagementNoValues() {
        requestPayload = createEncryptionKeyNoManagementNoValue();
    }

    /**
     * Checks that the index in the response matches what was sent.
     */
    public void validateIndex() {
        softAssertions.assertThat(response.index()).as("Check Index").isEqualTo(this.index);
    }

    /**
     * Checks that the variant value in the response is null
     */
    public void validateVariantValueNull() {
        softAssertions.assertThat(response.encryptedKeyValue().variantValue()).as("Check Variant Value is Null")
                .isNull();
    }

    /**
     * Checks that the algorithm and type in the response match the values request
     */
    public void validateAlgorithmAndType() {
        softAssertions.assertThat(response.algorithm()).as("Check Algorithm").isEqualTo(this.algorithm);
        softAssertions.assertThat(response.type().toString()).as("Check Type").isEqualTo(this.type);
    }

    /**
     * Checks that the scope in the response matches the value on the request
     */
    public void validateScope() {
        //Gets the response for the given ID
        response = getEncryptionKeyFromDbById();

        softAssertions.assertThat(response.scope().zone()).as("Check Scope Zone").isEqualTo(this.zone);
        softAssertions.assertThat(response.scope().bin()).as("Check Scope Bin").isEqualTo(this.bin);
        softAssertions.assertThat(response.scope().group()).as("Check Scope Group").isEqualTo(this.group);
    }

    /**
     * Checks that the VariantValue in the response matches what was sent.
     */
    public void validateVariantValue() {
        softAssertions.assertThat(response.encryptedKeyValue().variantValue()).as(" variant value")
                .isEqualTo(this.variantValue);
    }

    /**
     * Checks that the management key id  in the response is not null
     */
    public void validateManagementKeyIdNotNull() {
        softAssertions.assertThat(response.encryptedKeyValue().managementKey().id())
                .as("Check Management key ID Not Null")
                .isNotNull();
    }

    /**
     * Checks that the management key Id in the response matches the value given expectedId
     */
    public void validateManagementKeyMatchesGivenId(String expectedId) {
        softAssertions.assertThat(response.encryptedKeyValue().managementKey().id())
                .as("Check Management key ID Matches Given Value").isEqualTo(expectedId);
    }

    /**
     * Checks that the key block value in the response is not null
     */
    public void validateKeyBlockValueNotNull() {
        softAssertions.assertThat(response.encryptedKeyValue().keyblockValue()).as("Check Key Block Value is Not Null")
                .isNotNull();
    }

    /**
     * Checks that the Management Key Id returned in the getEncryptionKeyFromDbByIdWithEmbedParam() response matches the
     * expected id value
     *
     * @param expectedId expected id value
     */
    public void validateGetResponseEncryptionKeyManagementKeyId(String expectedId) {
        response = getEncryptionKeyFromDbByIdWithEmbedParam();
        validateManagementKeyMatchesGivenId(expectedId);
    }

    /**
     * Checks that the given encryption ID exists and that the code, zone and group fields for that Id match their
     * default values
     *
     * @param keyId encryption ID to check against
     */
    public void validateGetResponseEncryptionKeyIdCodeZoneAndGroup(String keyId) {
        setEncryptionKeyId(keyId);
        response = getEncryptionKeyFromDbByIdWithEmbedParam();
        validateHttpStatusCode(200);

        softAssertions.assertThat(response.code()).as("Check Code matches default value")
                .isEqualTo(DEFAULT_MANAGEMENT_KEY_CODE);
        softAssertions.assertThat(response.scope().zone()).as("Check Zone matches default value")
                .isEqualTo(DEFAULT_MANAGEMENT_KEY_ZONE);
        softAssertions.assertThat(response.scope().group()).as("Check Group matches default value")
                .isEqualTo(DEFAULT_MANAGEMENT_KEY_GROUP);
    }

    public void validateCheckValue(String keyId, String expectedCheckValue) {
        setEncryptionKeyId(keyId);
        response = getEncryptionKeyFromDbByIdWithEmbedParam();
        validateHttpStatusCode(200);

        softAssertions.assertThat(response.encryptedKeyValue().checkValue()).
                as("Check that Check Value has expected value").isEqualTo(expectedCheckValue);
    }

    /**
     * Checks that the Http Status code is as expected
     *
     * @param expectedStatus Expected Http Status code
     */
    public void validateHttpStatusCode(int expectedStatus) {
        this.paySecCommon.theCallReturnsAnHttpStatusCodeOf(this.restResponse.httpCode(), expectedStatus);
    }

    /**
     * Checks that the error messages returned are as expected. Takes into account situation where one of the messages
     * Can can contain both "Zone cannot be null Group cannot be null" and "Group cannot be null Zone cannot be null" in
     * either order.
     *
     * @param expectedErrorMessage the error message that should be returned
     */
    public void validateMultipleErrorMessages(String expectedErrorMessage) {
        ErrorResponse actualError = restResponse.body(ErrorResponse.class);

        if (expectedErrorMessage.contains("Zone cannot be null Group cannot be null") &&
                actualError.toString().contains("Group cannot be null Zone cannot be null")) {
            //Same 2 errors, but returned in different order, so swap order in expectedErrorMessage
            expectedErrorMessage = "Group cannot be null Zone cannot be null";
        }

        ErrorResponse expectedError = ErrorResponse.builder()
                .code("BAD_REQUEST")
                .message(expectedErrorMessage)
                .build();

        paySecCommon.theErrorMessagePayloadIsAsExpected(actualError, expectedError);
    }

    /**
     * Checks that the actual error message and error code match the expected ones
     */
    public void validateErrorMessageAndCode(String expectedErrorMessage, String expectedErrorCode) {
        ErrorResponse actualError = restResponse.body(ErrorResponse.class);
        ErrorResponse expectedError = ErrorResponse.builder()
                .code(expectedErrorCode)
                .message(expectedErrorMessage)
                .build();

        paySecCommon.theErrorMessagePayloadIsAsExpected(actualError, expectedError);
    }

    /**
     * Checks that the ENCRYPTION_KEY_NOT_FOUND error code is returned and the error message is as expected for a GET
     * request to Payment Security application encryption keys endpoint for a given key and with the EMBED_QUERY_PARAM =
     * "encrypted_key_value"
     */
    public void validateGetResponseNotFoundMessage() {
        response = getEncryptionKeyFromDbByIdWithEmbedParam();
        String expectedErrorMessage = "No encryption key found for id " + this.encryptionKeyId;

        ErrorResponse actualError = restResponse.body(ErrorResponse.class);

        ErrorResponse expectedError = ErrorResponse.builder()
                .code("ENCRYPTION_KEY_NOT_FOUND")
                .message(expectedErrorMessage)
                .build();

        paySecCommon.theErrorMessagePayloadIsAsExpected(actualError, expectedError);
    }

    /**
     * Makes a GET request to Payment Security application encryption keys endpoint for the current encryption key
     *
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionKeyResponseResource getEncryptionKeyFromDbById() {
        this.restResponse = PaymentSecurityDsl.app(this.paySecCommon.paymentSecurityUrl)
                .encryptionKeysClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .authorization(generateToken(ENCRYPTION_KEY_READ))
                .submitGet(this.encryptionKeyId);

        return this.restResponse.body(EncryptionKeyResponseResource.class);
    }

    /**
     * Makes a GET request to Payment Security application encryption keys endpoint for a given key and with the
     * EMBED_QUERY_PARAM = "encrypted_key_value"
     *
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionKeyResponseResource getEncryptionKeyFromDbByIdWithEmbedParam() {
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
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()

                .submitGet(this.encryptionKeyId);

        return this.restResponse.body(EncryptionKeyResponseResource.class);
    }

    /**
     * Sets the value for algorithm
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm.equals("none") ? null : algorithm;
        requestPayload.algorithm(algorithm);
    }

    /**
     * Sets the value for type
     */
    public void setType(String type) {
        this.type = type;
        requestPayload.type(TypeDto.valueOf(type));
    }

    /**
     * Sets the value of check value based on the given keyType
     *
     * @param keyType type of key
     */
    public void setCheckValueByKeyType(String keyType) {
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
     */
    public void setCheckValue(String checkValue) {
        requestPayload.encryptedKeyValue().checkValue(checkValue);
    }

    /**
     * Removes the value of checkValue
     */
    public void removeCheckValue() {
        requestPayload.encryptedKeyValue().checkValue(null);
    }

    /**
     * Sets the value of status
     *
     * @param status The state of the encryption key to match i.e. specify ACTIVE to ignore all inactive keys.
     */
    public void setStatus(String status) {
        requestPayload.status(StatusDto.valueOf(status));
    }

    /**
     * Sets the value of Scope
     *
     * @param zone  scope defining the zone in which the encryption key may be used where keys are partitioned for
     *              different systems
     * @param group roup: A scope defining the group for the key. If specified other groups and keys with no group will
     *              be ignored.
     * @param bin   bin: A scope defining the Bank Identifier Number (first 6 digits) and up to 2 further digits
     *              (segments) of the PAN for which the key is applicable.
     */
    public void setScope(String zone, String group, String bin) {
        ScopeDto scope = new ScopeDto();

        this.zone = zone.equals("none") ? null : zone;
        scope.zone(this.zone);

        this.group = group.equals("none") ? null : group;
        scope.group(this.group);

        this.bin = bin.equals("none") ? null : bin;
        scope.bin(this.bin);

        requestPayload.scope(scope);
    }

    /**
     * Creates the request payload and calls postEncryptionKeys() to create an encryption key with a variant value.
     *
     * @param keyType type of key
     */
    public void createEncryptionKeyWithVariantValue(String keyType) {
        //These variables are required later
        variantValue = getVariantValueByKeyType(keyType);
        index = Long.parseLong(RandomStringUtils.randomNumeric(6));

        requestPayload.encryptedKeyValue().variantValue(variantValue);
        requestPayload.index(index);

        response = postEncryptionKeys();
        this.encryptionKeyId = response.id();
    }

    /**
     * Makes a POST request to Payment Security application encryption keys endpoint with an encryption key payload
     *
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionKeyResponseResource postEncryptionKeys() {
        this.restResponse = PaymentSecurityDsl.app(this.paySecCommon.paymentSecurityUrl)
                .encryptionKeysClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .authorization(generateToken(ENCRYPTION_KEY_CREATE))
                .with(requestPayload)
                .submitPost();

        return this.restResponse.body(EncryptionKeyResponseResource.class);
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
     * Makes a DELETE request to Payment Security application encryption keys endpoint
     */
    public void deleteEncryptionKey() {

        this.restResponse = PaymentSecurityDsl.app(this.paySecCommon.paymentSecurityUrl)
                .encryptionKeysClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .authorization(generateToken(ENCRYPTION_KEY_DELETE))
                .submitDelete(this.encryptionKeyId);
    }

    /**
     * Calls postEncryptionKeys() to create an encryption key with a management key but no variant value.
     */
    public void createEncryptionKeyWithoutVariantValue() {
        index = Long.parseLong(RandomStringUtils.randomNumeric(6));
        requestPayload.index(index);

        response = postEncryptionKeys();
        this.encryptionKeyId = response.id();
    }

    /**
     * Calls postEncryptionKeysWithEmbedQuery() to create an encryption key with the given payload and an index and with
     * the EMBED_QUERY_PARAM set.
     */
    public void createEncryptionKeyWithVariantValueEmbedQueryWithoutValue() {
        index = Long.parseLong(RandomStringUtils.randomNumeric(6));
        requestPayload.index(index);

        response = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = response.id();
    }

    /**
     * Calls postEncryptionKeysWithEmbedQuery() to create an encryption key with the given payload and an index and a
     * variant value and with the EMBED_QUERY_PARAM set.
     */
    public void createEncryptionKeyWithVariantValueEmbedQuery(String keyType) {
        this.variantValue = getVariantValueByKeyType(keyType);
        index = Long.parseLong(RandomStringUtils.randomNumeric(6));
        requestPayload.encryptedKeyValue().variantValue(variantValue);
        requestPayload.index(index);

        response = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = response.id();
    }

    /**
     * Calls postEncryptionKeysWithEmbedQuery() to create an encryption key with the given payload an index and a key
     * block value and with the EMBED_QUERY_PARAM set.
     */
    public void createEncryptionKeyWithKeyblockValueEmbedQuery(String keyType) {
        keyblockValue = getKeyblockValueByKeyType(keyType);
        index = Long.parseLong(RandomStringUtils.randomNumeric(6));
        requestPayload.encryptedKeyValue().keyblockValue(keyblockValue);
        requestPayload.index(index);

        response = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = response.id();
    }

    /**
     * Calls postEncryptionKeysWithEmbedQuery() to create an encryption key  with the given payload an index, variant
     * value and a key block value and with the EMBED_QUERY_PARAM set.
     */
    public void createEncryptionKeyWithVariantAndKeyblockValueEmbedQuery(String keyType) {
        variantValue = getVariantValueByKeyType(keyType);
        keyblockValue = getKeyblockValueByKeyType(keyType);
        index = Long.parseLong(RandomStringUtils.randomNumeric(6));

        requestPayload.encryptedKeyValue().variantValue(variantValue);
        requestPayload.encryptedKeyValue().keyblockValue(keyblockValue);
        requestPayload.index(index);

        response = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = response.id();
    }

    /**
     * Calls postEncryptionKeysWithEmbedQuery() to create an encryption key  with the given payload, an index, a random
     * variant value and a key block value and with the EMBED_QUERY_PARAM set.
     */
    public void createEncryptionKeyWithRandomVariantValue() {
        variantValue = RandomStringUtils.randomAlphanumeric(49);
        index = Long.parseLong(RandomStringUtils.randomNumeric(6));

        requestPayload.encryptedKeyValue().variantValue(variantValue);
        requestPayload.index(index);

        response = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = response.id();
    }

    /**
     * Calls postEncryptionKeysWithEmbedQuery() to create an encryption key with the given payload an index, a random
     * key block value and with the EMBED_QUERY_PARAM set.
     */
    public void createEncryptionKeyWithRandomKeyblockValue() {
        keyblockValue = RandomStringUtils.randomAlphanumeric(89);
        index = Long.parseLong(RandomStringUtils.randomNumeric(6));
        requestPayload.encryptedKeyValue().keyblockValue(keyblockValue);
        requestPayload.index(index);

        response = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = response.id();
    }

    /**
     * Calls postEncryptionKeysWithEmbedQuery() to create an encryption key with the given payload an index,plus a
     * random variant and key block values and with the EMBED_QUERY_PARAM set.
     */
    public void createEncryptionKeyWithRandomVariantAndKeyblockValue() {
        variantValue = RandomStringUtils.randomAlphanumeric(49);
        keyblockValue = RandomStringUtils.randomAlphanumeric(89);
        index = Long.parseLong(RandomStringUtils.randomNumeric(6));

        requestPayload.encryptedKeyValue().variantValue(variantValue);
        requestPayload.encryptedKeyValue().keyblockValue(keyblockValue);
        requestPayload.index(index);

        response = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = response.id();
    }

    /**
     * Calls postEncryptionKeysWithEmbedQuery() to create an encryption key  with the given payload and key block values
     * and with the EMBED_QUERY_PARAM set.
     */
    public void createEncryptionKeyWithKeyblockValueEncryptedUnderSpecificZmk(String zmkId) {
        keyblockValue = keyblocksEncryptedUnderZmks.get(zmkId);
        requestPayload.encryptedKeyValue().keyblockValue(keyblockValue);

        response = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = response.id();
    }

    /**
     * Checks that the key block value in the response is not null
     */
    public void validateKeyblockValueNotNullOnDB() {
        response = getEncryptionKeyFromDbByIdWithEmbedParam();
        softAssertions.assertThat(response.encryptedKeyValue().keyblockValue()).as("Check key block vale not null")
                .isNotNull();
    }

    /**
     * Performs a GET on encryption keys endpoint for a given key Id with MBED_QUERY_PARAM set
     */
    public void retrieveEncryptionKeyByIdWithEmbedParam() {
        response = getEncryptionKeyFromDbByIdWithEmbedParam();
    }

    /**
     * Creates a default pauloaud with no management key value
     */
    public void createDefaultPayloadEncryptionKeyNoValue() {
        requestPayload = createEncryptionKeyNoValueResource();
    }

    /**
     * Sets the management key id based on the given key type
     *
     * @param keyType Tyoe of encryption key
     */
    public void setManagementKeyIdByKeyType(String keyType) {
        switch (keyType) {
            case "ZMK": {
                this.managementKeyId = ZMKs_IDs_FOR_ZEK[0];
                break;
            }
            case "ZEK": {
                this.managementKeyId = ZEK_ID;
                break;
            }
            case "random": {
                this.managementKeyId = UUID.randomUUID().toString();
                break;
            }
            default: {
                this.managementKeyId = "Key type not set.";
                break;
            }
        }
        requestPayload.encryptedKeyValue().managementKey().id(managementKeyId);
    }

    /**
     * Sets the check value based on the given managementbKey Id
     */
    public void setCheckValueByManagementId(String managementKeyId) {
        switch (managementKeyId) {
            case "c3be4b32-fe86-473d-9166-da1d77a4a96b":
                setCheckValue("62D017");
                break;
            case "aaf1ee6b-216c-4d7b-aebb-45fa8685174a":
                setCheckValue("6087FC");
                break;
            default:
                setCheckValue("random");
                break;
        }
    }

    public String getManagementKeyId() {
        return this.managementKeyId;
    }

    public void setManagementKeyId(String zmkId) {
        this.managementKeyId = zmkId;
        requestPayload.encryptedKeyValue().managementKey().id(managementKeyId);
    }

    public String getEncryptionKeyId() {
        return this.encryptionKeyId;
    }

    public void setEncryptionKeyId(String keyId) {
        this.encryptionKeyId = keyId;
    }

    public void setVariantValue(String variantValue) {
        this.variantValue = variantValue;
        requestPayload.encryptedKeyValue().variantValue(variantValue);
    }

    public void setKeyblockValue(String keyblockValue) {
        this.keyblockValue = keyblockValue;
        requestPayload.encryptedKeyValue().keyblockValue(keyblockValue);
    }

}
