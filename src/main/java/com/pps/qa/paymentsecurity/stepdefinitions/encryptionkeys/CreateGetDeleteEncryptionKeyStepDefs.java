package com.pps.qa.paymentsecurity.stepdefinitions.encryptionkeys;

import static com.pps.qa.paymentsecurity.databuilders.CreateGetDeleteEncryptionKeysDataBuilder.getKeyblockValueByKeyId;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.getVariantValueByKeyId;

import com.pps.qa.paymentsecurity.utils.EncryptionKeyUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.security.InvalidParameterException;

/**
 * Holds the step definitions for the tests defined in create_get_delete_encryption_key.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */
public class CreateGetDeleteEncryptionKeyStepDefs {

    /**
     * Framework related variables
     */
    private final EncryptionKeyUtils utils;

    /**
     * Variables and constants for the class
     */
    private String keyType = "ZEK";
    private boolean isManagementKeyReferenced = false;


    public CreateGetDeleteEncryptionKeyStepDefs(EncryptionKeyUtils utils) {
        this.utils = utils;
    }

    @Given("an existing HSM managed with {string} algorithm and {string} encryption key")
    public void anExistingHSMManagedWithAlgorithmAndEncryptionKey(String algorithm, String keyType) {
        if (keyType.equals("ZEK") || keyType.equals("ZMK")) {
            utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        } else {
            utils.createDefaultPayloadEncryptionKeyNoManagementKeyVariant();
        }

        utils.setAlgorithm(algorithm);
        utils.setType(keyType);
        utils.setCheckValueByKeyType(keyType);
        this.keyType = keyType;

        if (keyType.equals("ZMK")) {
            utils.setManagementKeyIdByKeyType(keyType);
        }
    }

    @When("the key is created on the Payment Security service with status {string}")
    public void theKeyIsCreatedOnThePaymentSecurityServiceWithStatus(String status) {
        utils.setStatus(status);
        utils.createEncryptionKeyWithVariantValue(keyType);
    }

    @When("the key is created on the Payment Security service in {string} zone, {string} group and {string} bin")
    public void theKeyIsCreatedOnThePaymentSecurityServiceInZoneGroupAndBin(String zone, String group, String bin) {
        utils.setScope(zone, group, bin);
        utils.createEncryptionKeyWithVariantValue(keyType);

    }

    @Then("the request to create a Key Encryption returns an http status code of {int}")
    public void theRequestToCreateAKeyEncryptionReturnsAnHttpStatusCodeOf(int expectedStatus) {
        utils.validateHttpStatusCode(expectedStatus);
    }

    @Then("the request to retrieve a Key Encryption returns an http status code of {int}")
    public void theRequestToRetrieveAKeyEncryptionReturnsAnHttpStatusCodeOf(int expectedStatus) {
        utils.validateHttpStatusCode(expectedStatus);
    }

    @And("the key is stored in the database without the variant value field")
    public void theKeyIsStoredInTheDatabaseWithoutTheVariantValueField() {
        utils.validateIndex();
        utils.validateAlgorithmAndType();
        utils.validateVariantValueNull();
    }

    @And("the key is stored in the database with the expected variant value field")
    public void theKeyIsStoredInTheDatabase() {
        utils.validateIndex();
        utils.validateAlgorithmAndType();
        utils.validateVariantValue();

    }

    @Then("the key is stored in the database with the expected scope")
    public void theKeyIsStoredInTheDatabaseWithTheExpectedScope() {
        utils.validateScope();
    }

    @And("an error message {string} stating Missing required field is displayed")
    public void anErrorMessageStatingMissingRequiredFieldIsDisplayed(String expectedErrorMessage) {
        utils.validateMultipleErrorMessages(expectedErrorMessage);
    }

    @When("the key is retrieved by its unique id and the embed query parameter is encrypted_key_value")
    public void theKeyIsRetrievedByItsUniqueIdAndTheEmbedQueryParameterIsEncryptedKeyValue() {
        utils.retrieveEncryptionKeyByIdWithEmbedParam();
    }

    @Then("the request to delete a Key Encryption returns an http status code of {int}")
    public void theRequestToDeleteAKeyEncryptionReturnsAnHttpStatusCodeOf(int expectedStatus) {
        utils.validateHttpStatusCode(expectedStatus);
    }

    @When("the key is deleted")
    public void theKeyIsDeleted() {
        utils.deleteEncryptionKey();
    }

    @And("a successful No Content response is received")
    public void aSuccessfulNoContentResponseIsReceived() {
        utils.validateHttpStatusCode(204);
    }

    @And("the key is not retrievable with its unique id")
    public void theKeyIsNotRetrievableWithItsUniqueId() {
        utils.validateGetResponseNotFoundMessage();
    }

    @Given("an HSM managed exists with {string} encryption key")
    public void anHSMManagedExistsWithEncryptionKey(String type) {
        utils.createDefaultPayloadEncryptionKeyNoValue();
        utils.setManagementKeyIdByKeyType(type);
        this.keyType = type;
    }

    @When("an {string} {string} encryption key is created identifying the ZMK as the management key and with no key value")
    public void anEncryptionKeyIsCreatedIdentifyingTheZMKAsTheManagementKeyAndWithNoKeyValue(String algorithm,
            String keyType) {
        utils.setAlgorithm(algorithm);
        utils.setType(keyType);

        utils.setStatus("ACTIVE");
        utils.createEncryptionKeyWithoutVariantValue();
    }

    @When("an {string} {string} encryption key is created identifying the ZMK as the management key and with no key value  and with the show_value query parameter set to true")
    public void anEncryptionKeyIsCreatedIdentifyingTheZMKAsTheManagementKeyAndWithNoKeyValueAndWithTheShowValueQueryParameterSetToTrue(
            String algorithm, String keyType) {
        utils.setAlgorithm(algorithm);
        utils.setType(keyType);
        utils.createEncryptionKeyWithVariantValueEmbedQueryWithoutValue();
    }

    @Then("the new encryption key is created in the database with a new generated key value for only the keyblock encoding and check value")
    public void theNewEncryptionKeyIsCreatedInTheDatabaseWithANewGeneratedKeyValueForOnlyTheKeyblockEncodingAndCheckValue() {
        utils.validateIndex();
        utils.validateAlgorithmAndType();
        utils.validateVariantValueNull();
        utils.validateKeyblockValueNotNullOnDB();
    }

    @Given("a key was created on the Payment Security service")
    public void aKeyWasCreatedOnThePaymentSecurityService() {
        anExistingHSMManagedWithAlgorithmAndEncryptionKey("3DES", "ZEK");
        theKeyIsCreatedOnThePaymentSecurityServiceWithStatus("ACTIVE");
        theRequestToCreateAKeyEncryptionReturnsAnHttpStatusCodeOf(201);
    }

    @And("the encryption key error message contains {string} and the error code is {string}")
    public void theEncryptionKeyErrorMessageContainsAndTheErrorCodeIs(String errorMessage, String errorCode) {
        utils.validateErrorMessageAndCode(errorMessage, errorCode);
    }

    @And("an {string} {string} encryption key is created identifying an unknown ZMK as the management key")
    public void anEncryptionKeyIsCreatedIdentifyingAnUnknownZMKAsTheManagementKey(String algorithm, String keyType) {
        utils.setAlgorithm(algorithm);
        utils.setType(keyType);
        utils.setManagementKeyIdByKeyType("random");
        utils.createEncryptionKeyWithoutVariantValue();
    }

    @And("the encryption key unknown ZMK error message contains {string} and the error code is {string}")
    public void theEncryptionKeyUnknownZMKErrorMessageContainsAndTheErrorCodeIs(String errorMessage, String errorCode) {
        String expectedErrorMessage = errorMessage + utils.getManagementKeyId();

        utils.validateErrorMessageAndCode(expectedErrorMessage, errorCode);
    }

    @When("an {string} {string} encryption key is created identifying an invalid ZMK as the management key")
    public void anEncryptionKeyIsCreatedIdentifyingAnInvalidZMKAsTheManagementKey(String algorithm, String keyType) {
        utils.setAlgorithm(algorithm);
        utils.setType(keyType);
        utils.setManagementKeyIdByKeyType("ZEK");
        utils.createEncryptionKeyWithoutVariantValue();
    }

    @Given("ZMK {string} exists in the database")
    public void zmkExistsInTheDatabase(String keyId) {
        utils.setEncryptionKeyId(keyId);
        utils.getEncryptionKeyFromDbByIdWithEmbedParam();
        utils.validateHttpStatusCode(200);

    }

    @Given("ZMK {string} exists in the database as default management key")
    public void zmkExistsInTheDatabaseAsDefaultManagementKey(String keyId) {
        utils.validateGetResponseEncryptionKeyIdCodeZoneAndGroup(keyId);
    }

    @When("a valid request that contains the {string} value encrypted under the {string} management ZMK key is made to create a new {string} encryption key")
    public void aValidRequestThatContainsTheValueEncryptedUnderTheManagementZMKKeyIsMadeToCreateANewEncryptionKey(
            String valueType, String managementKeyReference, String keyType) {

        isManagementKeyReferenced = managementKeyReference.equals("referenced");
        switch (valueType) {
            case "Variant":
                utils.createDefaultPayloadEncryptionKeyOnlyVariant();
                utils.setType(keyType);
                if (isManagementKeyReferenced) {
                    utils.setManagementKeyIdByKeyType("ZMK");
                    this.isManagementKeyReferenced = true;
                }
                utils.createEncryptionKeyWithVariantValueEmbedQuery(keyType);
                break;
            case "Keyblock":
                utils.createDefaultPayloadEncryptionKeyOnlyKeyblock();
                utils.setType(keyType);
                if (isManagementKeyReferenced) {
                    utils.setManagementKeyIdByKeyType("ZMK");
                    this.isManagementKeyReferenced = true;
                }
                utils.createEncryptionKeyWithKeyblockValueEmbedQuery(keyType);
                break;
            case "Variant and Keyblock":
                utils.createDefaultPayloadEncryptionKeyBothValues();
                utils.setType(keyType);
                if (isManagementKeyReferenced) {
                    utils.setManagementKeyIdByKeyType("ZMK");
                    this.isManagementKeyReferenced = true;
                }
                utils.createEncryptionKeyWithVariantAndKeyblockValueEmbedQuery(keyType);
                break;
            default:
                throw new InvalidParameterException(
                        "No value (Variant, Keyblock or both) was provided for the creation of the encryption key.");
        }
    }

    @When("a valid request that contains the {string} value not encrypted under the {string} management ZMK key is made to create a new ZEK encryption key")
    public void aValidRequestThatContainsTheValueNotEncryptedUnderTheManagementZMKKeyIsMadeToCreateANewZEKEncryptionKey(
            String valueType, String managementKeyReference) {

        isManagementKeyReferenced = managementKeyReference.equals("referenced");
        switch (valueType) {
            case "Variant":
                if (isManagementKeyReferenced) {
                    utils.createDefaultPayloadEncryptionKeyOnlyVariant();
                } else {
                    utils.createDefaultPayloadEncryptionKeyNoManagementKeyVariant();
                }
                utils.createEncryptionKeyWithRandomVariantValue();
                break;
            case "Keyblock":
                if (isManagementKeyReferenced) {
                    utils.createDefaultPayloadEncryptionKeyOnlyKeyblock();
                } else {
                    utils.createDefaultPayloadEncryptionKeyNoManagementKeyKeyblock();
                }
                utils.createEncryptionKeyWithRandomKeyblockValue();

                break;
            case "Variant and Keyblock":
                if (isManagementKeyReferenced) {
                    utils.createDefaultPayloadEncryptionKeyBothValues();
                } else {
                    utils.createDefaultPayloadEncryptionKeyNoManagementKeyBothValues();
                }
                utils.createEncryptionKeyWithRandomVariantAndKeyblockValue();
                break;
            default:
                throw new InvalidParameterException(
                        "No value (Variant, Keyblock or both) was provided for the creation of the encryption key.");
        }
    }

    @When("a valid request that contains {string} value encrypted under the referenced management {string} ZMK key is made to create a new ZEK encryption key")
    public void aValidRequestThatContainsValueEncryptedUnderTheReferencedManagementZMKKeyIsMadeToCreateANewZEKEncryptionKey(
            String valueType, String zmkId) {
        switch (valueType) {
            case "Keyblock":
                utils.createDefaultPayloadEncryptionKeyOnlyKeyblock();
                utils.setManagementKeyIdByKeyType(zmkId);
                utils.setManagementKeyId(zmkId);
                isManagementKeyReferenced = true;
                utils.setCheckValueByManagementId(zmkId);
                utils.createEncryptionKeyWithKeyblockValueEncryptedUnderSpecificZmk(zmkId);
                break;
            case "no":
                utils.createDefaultPayloadEncryptionKeyNoValue();
                utils.setManagementKeyId(zmkId);
                this.isManagementKeyReferenced = true;
                utils.createEncryptionKeyWithVariantValueEmbedQueryWithoutValue();
                break;
            default:
                throw new InvalidParameterException(
                        "No Keyblock was provided for the creation of the encryption key or ZMK id " + zmkId
                                + " is invalid.");
        }
    }

    @When("a valid request that contains no values and no referenced management ZMK key is made to create a new ZEK encryption key")
    public void aValidRequestThatContainsNoValuesAndNoReferencedManagementZMKKeyIsMadeToCreateANewZEKEncryptionKey() {
        utils.createDefaultPayloadEncryptionKeyNoManagementNoValues();
        utils.createEncryptionKeyWithVariantValueEmbedQueryWithoutValue();
    }

    @And("the {string} value should be encrypted under the given ZMK")
    public void theValueShouldBeEncryptedUnderTheGivenZMK(String valueType) {
        switch (valueType) {
            case "Variant":
                utils.validateVariantValue();
                if (isManagementKeyReferenced) {
                    utils.validateManagementKeyIdNotNull();
                }
                break;
            case "Keyblock":
                utils.validateKeyBlockValueNotNull();
                if (isManagementKeyReferenced) {
                    utils.validateManagementKeyIdNotNull();
                }
                break;
            case "Variant and Keyblock":
                utils.validateVariantValue();
                utils.validateKeyBlockValueNotNull();
                if (isManagementKeyReferenced) {
                    utils.validateManagementKeyIdNotNull();
                }
                break;
            default:
                throw new InvalidParameterException("No value (Variant, Keyblock or both) present in the response.");
        }
    }

    @And("ZMK {string} should be linked as the management key")
    public void zmkShouldBeLinkedAsTheManagementKey(String keyId) {
        utils.validateGetResponseEncryptionKeyManagementKeyId(keyId);
    }

    @Given("key {string} has Check Value {string}")
    public void keyHasCheckValue(String keyId, String checkValue) {
        utils.validateCheckValue(keyId, checkValue);
    }

    @When("a valid request that contains the {string} value encrypted under the above key and Check Value {string} is made to create a new {string} encryption key")
    public void aValidRequestThatContainsTheValueEncryptedUnderTheAboveKeyAndCheckValueIsMadeToCreateANewEncryptionKey(
            String valueType, String checkValue, String keyType) {
        switch (valueType) {
            case "Variant":
                utils.createDefaultPayloadEncryptionKeyOnlyVariant();
                utils.setType(keyType);
                utils.setCheckValue(checkValue);
                utils.createEncryptionKeyWithVariantValueEmbedQuery(keyType);
                break;
            case "Keyblock":
                utils.createDefaultPayloadEncryptionKeyOnlyKeyblock();
                utils.setType(keyType);
                utils.setCheckValue(checkValue);
                utils.createEncryptionKeyWithKeyblockValueEmbedQuery(keyType);
                break;
            case "Variant and Keyblock":
                utils.createDefaultPayloadEncryptionKeyBothValues();
                utils.setType(keyType);
                utils.setCheckValue(checkValue);
                utils.createEncryptionKeyWithVariantAndKeyblockValueEmbedQuery(keyType);
                break;
            default:
                throw new InvalidParameterException(
                        "No value (Variant, Keyblock or both) was provided for the creation of the encryption key.");
        }
    }

    @When("a valid request is made to create a new encryption key")
    public void aValidRequestIsMadeToCreateANewEncryptionKey() {
        utils.createDefaultPayloadEncryptionKeyNoManagementKeyBothValues();
    }

    @And("the {string} value for key {string} is provided")
    public void theValueForKeyIsProvided(String value, String keyId) {
        switch (value) {
            case "Variant": {
                String variantValue = getVariantValueByKeyId(keyId);
                utils.setVariantValue(variantValue);
                break;
            }
            case "Keyblock": {
                String keyblockValue = getKeyblockValueByKeyId(keyId);
                utils.setKeyblockValue(keyblockValue);
                break;
            }
            case "Variant and Keyblock": {
                String variantValue = getVariantValueByKeyId(keyId);
                utils.setVariantValue(variantValue);
                String keyblockValue = getKeyblockValueByKeyId(keyId);
                utils.setKeyblockValue(keyblockValue);
                break;
            }
            default:
                throw new InvalidParameterException(
                        "No value (Variant, Keyblock or both) was provided for the creation of the encryption key.");
        }
    }

    @And("the {string} Check Value is provided")
    public void theCheckValueIsProvided(String checkValue) {
        utils.setCheckValue(checkValue);
    }

    @And("the post encryption key request is sent")
    public void thePostEncryptionKeyRequestIsSent() {
        utils.createEncryptionKeyWithVariantValueEmbedQueryWithoutValue();
    }

    @And("no Check Value is provided")
    public void noCheckValueIsProvided() {
        utils.removeCheckValue();
    }
}
