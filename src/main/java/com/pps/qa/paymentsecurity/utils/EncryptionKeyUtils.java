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
import com.pps.dsl.paymentsecurity.domain.EncryptionMultipleKeysResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.ScopeDto;
import com.pps.dsl.paymentsecurity.domain.dto.StatusDto;
import com.pps.dsl.paymentsecurity.domain.dto.TypeDto;
import com.pps.dsl.rest.ErrorResponse;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import io.cucumber.java.After;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

public class EncryptionKeyUtils {

    /**
     * Variables and constants for the class
     */

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
    public String bin;
    /**
     * Request/Response values for these tests
     */
    private EncryptionKeyRequestResource requestPayload;
    private EncryptionKeyResponseResource response;
    private EncryptionMultipleKeysResponseResource multikeyResponse;
    private RestResponse restResponse;
    private String encryptionKeyId;
    private long index;
    private String variantValue;
    private String type;
    private String algorithm;
    private String zone;
    private String group;
    private String managementKeyId;
    private String keyblockValue;
    private List<String> codeList;

    public EncryptionKeyUtils(PaymentSecurityCommon paySecCommon) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;

        this.codeList = new ArrayList<>();
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

    public void getLatestEncryptionKeysByCode(String code) {
        multikeyResponse = getEncryptionKeysByCodeAndLatestIndexOnly(code);
    }


    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by a given
     * code and latest index only
     *
     * @param code The (well known) given identifier of the cryptographic key
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByCodeAndLatestIndexOnly(String code) {
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
                .latestIndexOnly(true)
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }


    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by a given
     * code and index
     *
     * @param code  The (well known) given identifier of the cryptographic key
     * @param index The index of the key where more than one version of the key exists with the same scope and code.
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByCodeAndIndex(String code, String index) {
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
                .index(index)
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }

    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by a given
     * status
     *
     * @param status The state of the encryption key to match i.e. specify ACTIVE to ignore all inactive keys.
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByStatus(String status) {
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
                .status(status)
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }

    /**
     * Makes a GET request to Payment Security application encryption keys endpoint without filers except for setting
     * limit to 1000000 to make sure all the records are in the response as we are also bringing back records with a
     * status of INACTIVE, of which there could be thousands.
     *
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysWithoutFilters() {
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
                .limit(1000000)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }


    /**
     * Maks a GET request to Payment Security application encryption keys endpoint with only the EMBED_QUERY_PARAM
     * filter and setting limit to 1000000 to make sure all the records are in the response as we are also bringing back
     * records with a status of INACTIVE, of which there could be thousands.
     *
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysWithOnlyEmbedFilter() {
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
                .embed(EMBED_QUERY_PARAM)
                .limit(1000000)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }


    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by a given
     * code and Scope (comprising Zone, bin and group)
     *
     * @param code  The (well known) given identifier of the cryptographic key
     * @param zone  A scope defining the zone in which the encryption key may be used where keys are partitioned for
     *              different systems.
     * @param bin   A scope defining the Bank Identifier Number (first 6 digits) and up to 2 further digits (segments)
     *              of the PAN for which the key is applicable.
     * @param group A scope defining the group for the key
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByCodeZoneGroupAndBin(String code, String zone,
            String group, String bin) {
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
                .zone(zone)
                .bin(bin)
                .group(group)
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }

    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by a given
     * code and Scope (comprising Zone and group)
     *
     * @param code  The (well known) given identifier of the cryptographic key
     * @param zone  A scope defining the zone in which the encryption key may be used where keys are partitioned for
     *              different systems.
     * @param group A scope defining the group for the key
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByCodeZoneAndGroup(String code, String zone,
            String group) {
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
                .zone(zone)
                .group(group)
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }


    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by a given
     * code and Scope (comprising Zone only)
     *
     * @param code The (well known) given identifier of the cryptographic key
     * @param zone A scope defining the zone in which the encryption key may be used where keys are partitioned for
     *             different systems.
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByCodeAndZone(String code, String zone) {
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
                .zone(zone)
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }

    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by a given
     * code and Scope (comprising Group only)
     *
     * @param code  The (well known) given identifier of the cryptographic key
     * @param group A scope defining the group for the key
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByCodeAndGroup(String code, String group) {
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
                .group(group)
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }


    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by a given
     * code and Scope (comprising Bin only)
     *
     * @param code The (well known) given identifier of the cryptographic key
     * @param bin  A scope defining the Bank Identifier Number (first 6 digits) and up to 2 further digits (segments) of
     *             the PAN for which the key is applicable.
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByCodeAndBin(String code, String bin) {
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
                .bin(bin)
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }

    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filers the results by the given
     * zones
     *
     * @param zone1, zone2 A scope defining the zone in which the encryption key may be used where keys are partitioned
     *               for different systems.
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByZoneList(String zone1, String zone2) {
        List<String> permissionList = new ArrayList<>();
        permissionList.add(ENCRYPTION_KEY_READ);
        permissionList.add(ENCRYPTION_KEY_READ_VALUE);

        String zoneParm = zone1 + "," + zone2;

        this.restResponse = PaymentSecurityDsl.app(this.paySecCommon.paymentSecurityUrl)
                .encryptionKeysClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .authorization(generateToken(permissionList, null))
                .queryParams()
                .zone(zoneParm)
                .status("ACTIVE")
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
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

    public void setCode(String code) {
        this.codeList.add(code);
        requestPayload.code(code);
    }

    public void setZone(String zone) {
        this.zone = zone.equals("none") ? null : zone;
        requestPayload.scope().zone(this.zone);
    }

    public void setGroup(String group) {
        this.group = group.equals("none") ? null : group;
        requestPayload.scope().group(this.group);
    }

    public void setBin(String bin) {
        this.bin = bin.equals("none") ? null : bin;
        requestPayload.scope().bin(this.bin);
    }

    public void getEncryptionKeysForGivenCode(String code) {
        multikeyResponse = getEncryptionKeysByCode(code);
    }


    public void getLatestEncryptionKeysByCodeAndIndex(String code, String index) {
        multikeyResponse = getEncryptionKeysByCodeAndIndex(code, index);
    }

    public void retrieveEncryptionKeysByStatus(String status) {
        multikeyResponse = getEncryptionKeysByStatus(status);
    }

    public void retrieveEncryptionKeysWithoutParameters() {
        multikeyResponse = getEncryptionKeysWithoutFilters();
    }

    public void retrieveEncryptionKeysWithOnlyEmbedParameter() {
        multikeyResponse = getEncryptionKeysWithOnlyEmbedFilter();
    }

    public void retrieveEncryptionKeysByZoneList(String zone1, String zone2) {
        multikeyResponse = getEncryptionKeysByZoneList(zone1, zone2);
    }

    public void retrieveEncryptionKeysByCodeAndScope(String codeValue, Map<String, String> queryParams) {
        //Need to pass codeValue to this method and extract other queryParams from the queryParms variable
        String queryParamZone = queryParams.get("zone");
        String queryParamBin = queryParams.get("bin");
        String queryParamGroup = queryParams.get("group");

        String paramsPopulated = null;

        boolean queryParamZoneNull = false;
        boolean queryParamGroupNull = false;
        boolean queryParamBinNull = false;

        if (queryParamZone == null) {
            queryParamZoneNull = true;
        }

        if (queryParamGroup == null) {
            queryParamGroupNull = true;
        }

        if (queryParamBin == null) {
            queryParamBinNull = true;
        }

        if (queryParamZoneNull == false && queryParamGroupNull == false && queryParamBinNull == true) {
            paramsPopulated = "Zone Group";
        }
        if (queryParamZoneNull == false && queryParamGroupNull == true && queryParamBinNull == true) {
            paramsPopulated = "Zone";
        }
        if (queryParamZoneNull == true && queryParamGroupNull == false && queryParamBinNull == true) {
            paramsPopulated = "Group";
        }
        if (queryParamZoneNull == true && queryParamGroupNull == true && queryParamBinNull == false) {
            paramsPopulated = "Bin";
        }
        if (queryParamZoneNull == false && queryParamGroupNull == false && queryParamBinNull == false) {
            paramsPopulated = "Zone Group Bin";
        }

        switch (paramsPopulated) {
            case "Zone Group":
                multikeyResponse = getEncryptionKeysByCodeZoneAndGroup(codeValue, queryParamZone, queryParamGroup);
                break;
            case "Zone Group Bin":
                multikeyResponse = getEncryptionKeysByCodeZoneGroupAndBin(codeValue, queryParamZone, queryParamBin,
                        queryParamGroup);
                break;
            case "Zone":
                multikeyResponse = getEncryptionKeysByCodeAndZone(codeValue, queryParamZone);
                break;
            case "Group":
                multikeyResponse = getEncryptionKeysByCodeAndGroup(codeValue, queryParamGroup);
                break;
            case "Bin":
                multikeyResponse = getEncryptionKeysByCodeAndBin(codeValue, queryParamBin);
                break;
            default:
                throw new InvalidParameterException("No valid scope combination supplied");
        }
    }

    /**
     * Makes a GET request to Payment Security application encryption keys endpoint and filters the results by a given
     * code
     *
     * @param code The (well known) given identifier of the cryptographic key
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByCode(String code) {
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
     * Makes a GET request to Payment Security application encryption keys endpoint and filters the results by a given
     * code, and ingnores status
     *
     * @param code The (well known) given identifier of the cryptographic key
     * @return an EncryptionKeyResponseResource
     */
    public EncryptionMultipleKeysResponseResource getEncryptionKeysByCodeIgnoreStatus(String code) {
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
                .embed(EMBED_QUERY_PARAM)
                .add()
                .submitGet();

        return this.restResponse.body(EncryptionMultipleKeysResponseResource.class);
    }

    public void validateCodeFromMultiKeyResponse(String expectedCode) {
        int numberORecords = (int) multikeyResponse.size();
        String actualCode = multikeyResponse.items().get(numberORecords - 1).code();

        softAssertions.assertThat(actualCode).as("Check created code").isEqualTo(expectedCode);
    }

    public void validateCodeAndLastIndexFromMultiKeyResponse(String expectedCode, Long expectedIndex) {
        int numberORecords = (int) multikeyResponse.size();

        String actualCode = multikeyResponse.items().get(numberORecords - 1).code();
        long actualIndex = multikeyResponse.items().get(numberORecords - 1).index();

        softAssertions.assertThat(actualCode).as("Check created code").isEqualTo(expectedCode);
        softAssertions.assertThat(actualIndex).as("Check created index").isEqualTo(expectedIndex);
    }

    public void validateCodeAndSecondFromLastIndexFromMultiKeyResponse(String expectedCode, Long expectedIndex) {
        int numberORecords = (int) multikeyResponse.size();
        String actualCode = multikeyResponse.items().get(numberORecords - 2).code();
        long actualIndex = multikeyResponse.items().get(numberORecords - 2).index();

        softAssertions.assertThat(actualCode).as("Check created code").isEqualTo(expectedCode);
        softAssertions.assertThat(actualIndex).as("Check created index").isEqualTo(expectedIndex);
    }

    public void validateStatusOfAllRecordsFromMultiKeyResponse(String expectedStatus) {
        int numberORecords = (int) multikeyResponse.size();
        String actualStatus;

        for (int i = 0; i < numberORecords; i++) {
            actualStatus = multikeyResponse.items().get(i).status().toString();
            softAssertions.assertThat(actualStatus).as("Check status").isEqualTo(expectedStatus);
        }
    }

    public void validateMultiKeyResponseContainsEncryptionKeyCodeAndIndex(String expectedCode, String expectedIndex) {
        int numberORecords = (int) multikeyResponse.size();
        String actualCode;
        String actualIndex;
        boolean matchFound = false;

        for (int i = 0; i < numberORecords; i++) {
            actualCode = multikeyResponse.items().get(i).code();
            actualIndex = multikeyResponse.items().get(i).index().toString();
            if (Objects.equals(actualCode, expectedCode) && Objects.equals(actualIndex, expectedIndex)) {
                matchFound = true;
                break;
            }
        }

        softAssertions.assertThat(matchFound).as("Check matching code and index found").isTrue();
    }

    public void validateMultiKeyResponseContainsEncryptionKeyCodeAndStatus(String expectedCode, String expectedStatus) {
        int numberORecords = (int) multikeyResponse.size();
        String actualCode;
        String actualStatus;
        boolean matchFound = false;

        for (int i = 0; i < numberORecords; i++) {
            actualCode = multikeyResponse.items().get(i).code();
            actualStatus = multikeyResponse.items().get(i).status().toString();
            if (Objects.equals(actualCode, expectedCode) && Objects.equals(actualStatus, expectedStatus)) {
                matchFound = true;
                break;
            }
        }

        softAssertions.assertThat(matchFound).as("Check matching code and index found").isTrue();
    }

    public void validateMultiKeyResponseContainsEncryptionKeyZones1And2Only(String zone1, String zone2) {
        int numberORecords = (int) multikeyResponse.size();
        String actualZone;
        boolean invalidZoneFound = false;

        for (int i = 0; i < numberORecords; i++) {
            actualZone = multikeyResponse.items().get(i).scope().zone();

            if (!actualZone.equals(zone1) && !actualZone.equals(zone2)) {
                invalidZoneFound = true;
                break;
            }
        }

        softAssertions.assertThat(invalidZoneFound).as("Check invalid zone not found").isFalse();
    }

    public void validateEncryptionKeysAreOrdered() {
        int numberORecords = (int) multikeyResponse.size();
        List<String> createdDateList = new ArrayList<>();

        for (int i = 0; i < numberORecords; i++) {
            createdDateList.add(multikeyResponse.items().get(i).audit().createdDateTime());
        }
        softAssertions.assertThat(createdDateList).as("Check ordred by created date").isSorted();
    }

    public void validateGetResponseEncryptionKeyItemScope(int index, Map<String, String> expectedScope) {

        String expectedZone = expectedScope.get("zone");
        String expectedBin = expectedScope.get("bin");
        String expectedGroup = expectedScope.get("group");

        String actualZone = multikeyResponse.items().get(index).scope().zone();
        String actualBin = multikeyResponse.items().get(index).scope().bin();
        String actualGroup = multikeyResponse.items().get(index).scope().group();

        if (expectedZone != null) {
            softAssertions.assertThat(actualZone).as("Check created zone").isEqualTo(expectedZone);
        }

        if (expectedBin != null) {
            softAssertions.assertThat(actualBin).as("Check created bin").isEqualTo(expectedBin);
        }

        if (expectedGroup != null) {
            softAssertions.assertThat(actualGroup).as("Check created bin").isEqualTo(expectedGroup);
        }
    }

    public void createEncryptionKeyWithIndex(String keyType, Long index) {
        variantValue = getVariantValueByKeyType(keyType);
        this.index = index;
        requestPayload.encryptedKeyValue().variantValue(variantValue);
        requestPayload.index(this.index);

        response = postEncryptionKeysWithEmbedQuery();
        this.encryptionKeyId = response.id();
    }

    public PaymentSecurityCommon getPaySecCommon() {
        return paySecCommon;
    }

    /**
     * Makes a DELETE request to Payment Security application encryption keys endpoint for a given encryption key
     *
     * @param id The unique id of the encryption key.
     */
    void deleteEncryptionKeyByCode(String id) {
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
     * Deletes all keys from DB that have have been created in current test
     */
    public void cleanUpDatabaseForThisCode() {
        for (String code : codeList) {
            multikeyResponse = getEncryptionKeysByCodeIgnoreStatus(code);

            for (int i = 0; i < multikeyResponse.size(); i++) {
                String currentId = multikeyResponse.items().get(i).id();
                deleteEncryptionKeyByCode(currentId);
            }
        }
    }

    //This method needs to be called in an @after rather than an @Before since the code is randomly generated and so
    //is not known until after the test has run.
    @After
    public void tearDown() {
        cleanUpDatabaseForThisCode();
    }
}
