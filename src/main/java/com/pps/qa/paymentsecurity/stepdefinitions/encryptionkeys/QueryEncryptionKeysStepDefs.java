package com.pps.qa.paymentsecurity.stepdefinitions.encryptionkeys;


import com.pps.qa.paymentsecurity.utils.EncryptionKeyUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Holds the step definitions for the tests defined in query_encryption_keys.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */
public class QueryEncryptionKeysStepDefs {

    private static final String DEFAULT_CODE_PREFIX = "Rom2Enk";
    private final EncryptionKeyUtils utils;
    private String keyType = "ZEK";
    private String code1;
    private String code2;
    private String code3;
    private String code4;
    private Long index1;
    private Long index2;
    private String zone1;
    private String zone2;
    private String zone3;
    private String group1;
    private String bin1;
    private String uniqueIdA;
    private String uniqueIdB;

    public QueryEncryptionKeysStepDefs(EncryptionKeyUtils utils) {
        this.utils = utils;
    }

    @Given("two encryption keys are created with different codes")
    public void twoEncryptionKeysAreCreatedWithDifferentCodes() {
        code1 = DEFAULT_CODE_PREFIX + RandomStringUtils.randomAlphabetic(6);
        code2 = DEFAULT_CODE_PREFIX + RandomStringUtils.randomAlphabetic(6);

        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code1);
        utils.createEncryptionKeyWithVariantValue(keyType);
        utils.validateHttpStatusCode(201);

        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code2);
        utils.createEncryptionKeyWithVariantValue(keyType);
        utils.validateHttpStatusCode(201);
    }

    @When("the encryption keys are queried for a specific code")
    public void theEncryptionKeysAreQueriedForASpecificCode() {
        utils.getEncryptionKeysForGivenCode(code1);
    }


    @Then("a successful OK response is received with the single encryption key with the specified code")
    public void aSuccessfulOkResponseIsReceivedWithTheSingleEncryptionKeyWithTheSpecifiedCode() {
        utils.validateHttpStatusCode(200);
        utils.validateCodeFromMultiKeyResponse(code1);
    }

    @Given("two encryption keys are created with different codes and a specific index")
    public void twoEncryptionKeysAreCreatedWithDifferentCodesAndASpecificIndex() {
        code1 = DEFAULT_CODE_PREFIX + RandomStringUtils.randomAlphabetic(6);
        code2 = DEFAULT_CODE_PREFIX + RandomStringUtils.randomAlphabetic(6);
        index1 = Long.parseLong(RandomStringUtils.randomNumeric(6));

        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code1);
        utils.createEncryptionKeyWithIndex(keyType, index1);
        utils.validateHttpStatusCode(201);

        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code2);
        utils.createEncryptionKeyWithIndex(keyType, index1);
        utils.validateHttpStatusCode(201);
    }

    @And("two encryption keys are created with the same codes as before and a higher index")
    public void twoEncryptionKeysAreCreatedWithTheSameCodesAsBeforeAndAHigherIndex() {
        index2 = index1 + 1;
        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code1);
        utils.createEncryptionKeyWithIndex(keyType, index2);
        utils.validateHttpStatusCode(201);

        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code2);
        utils.createEncryptionKeyWithIndex(keyType, index2);
        utils.validateHttpStatusCode(201);
    }

    @When("the encryption keys are queried for one of the codes with latest index")
    public void theEncryptionKeysAreQueriedForOneOfTheCodesWithLatestIndex() {
        utils.getLatestEncryptionKeysByCode(code1);
    }

    @Then("a successful OK response is received with the single encryption key with the queried code and the higher index")
    public void aSuccessfulOkResponseIsReceivedWithTheSingleEncryptionKeyWithTheQueriedCodeAndTheHigherIndex() {
        utils.validateHttpStatusCode(200);
        utils.validateCodeAndLastIndexFromMultiKeyResponse(code1, index2);
    }

    @When("the encryption keys are queried for one of the codes without latest index")
    public void theEncryptionKeysAreQueriedForOneOfTheCodesWithoutLatestIndex() {
        utils.getEncryptionKeysForGivenCode(code1);
    }

    @When("the encryption keys are queried for one of the codes and the smaller index")
    public void theEncryptionKeysAreQueriedForOneOfTheCodesAndTheSmallerIndex() {
        utils.getLatestEncryptionKeysByCodeAndIndex(code1, index1.toString());
    }

    @Then("a successful OK response is received with both encryption key with the queried code and all indexes")
    public void aSuccessfulOkResponseIsReceivedWithBothEncryptionKeyWithTheQueriedCodeAndAllIndexes() {
        utils.validateHttpStatusCode(200);
        utils.validateCodeAndSecondFromLastIndexFromMultiKeyResponse(code1, index1);
        utils.validateCodeAndLastIndexFromMultiKeyResponse(code1, index2);
    }

    @Then("a successful OK response is received with the single encryption key with the queried code and the smaller index")
    public void aSuccessfulOkResponseIsReceivedWithTheSingleEncryptionKeyWithTheQueriedCodeAndTheSmallerIndex() {
        utils.validateHttpStatusCode(200);
        utils.validateCodeAndLastIndexFromMultiKeyResponse(code1, index1);
    }

    @Given("two encryption keys with the same code and {string} and {string}")
    public void twoEncryptionKeysWithTheSameCodeAndScopeAndScope(String scope1, String scope2) {
        List<String> scopes1 = Arrays.asList(scope1.split(","));
        List<String> scopes2 = Arrays.asList(scope2.split(","));
        Map<String, String> scopeA = new HashMap<>();
        Map<String, String> scopeB = new HashMap<>();
        uniqueIdA = RandomStringUtils.randomAlphabetic(6).toUpperCase();
        uniqueIdB = RandomStringUtils.randomAlphabetic(6).toUpperCase();
        scopes1.forEach(s -> scopeA.put(s.trim().split(" ")[0], s.trim().split(" ")[1]));
        scopes2.forEach(s -> scopeB.put(s.trim().split(" ")[0], s.trim().split(" ")[1]));

        code1 = DEFAULT_CODE_PREFIX + RandomStringUtils.randomAlphabetic(6);

        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code1);

        if (scopeA.containsKey("zone")) {
            utils.setZone(scopeA.get("zone").equalsIgnoreCase("A") ? uniqueIdA : uniqueIdB);
        }
        if (scopeA.containsKey("group")) {
            utils.setGroup(scopeA.get("group").equalsIgnoreCase("A") ? uniqueIdA : uniqueIdB);
        }
        if (scopeA.containsKey("bin")) {
            utils.setBin(scopeA.get("bin").equalsIgnoreCase("A") ? uniqueIdA : uniqueIdB);
        }
        utils.setStatus("ACTIVE");
        utils.createEncryptionKeyWithVariantValue(keyType);
        utils.validateHttpStatusCode(201);

        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code1);

        if (scopeB.containsKey("zone")) {
            utils.setZone(scopeB.get("zone").equalsIgnoreCase("A") ? uniqueIdA : uniqueIdB);
        }
        if (scopeB.containsKey("group")) {
            utils.setGroup(scopeB.get("group").equalsIgnoreCase("A") ? uniqueIdA : uniqueIdB);
        }
        if (scopeB.containsKey("bin")) {
            utils.setBin(scopeB.get("bin").equalsIgnoreCase("A") ? uniqueIdA : uniqueIdB);
        }

        utils.setStatus("ACTIVE");
        utils.createEncryptionKeyWithVariantValue(keyType);
        utils.validateHttpStatusCode(201);
    }

    @When("the encryption keys are queried for the code and the scope of {string}")
    public void theEncryptionKeysAreQueriedForTheCodeAndTheScopeOf(String query) {
        List<String> queryScopes = Arrays.asList(query.split(","));
        Map<String, String> queryScope = new HashMap<>();
        queryScopes.forEach(s -> queryScope.put(s.trim().split(" ")[0], s.trim().split(" ")[1]));
        queryScope.replaceAll((k, v) -> v = v.equals("A") ? uniqueIdA : uniqueIdB);

        utils.retrieveEncryptionKeysByCodeAndScope(code1, queryScope);
    }

    @Then("a successful OK response is received with the single encryption key with the code and scope of {string}")
    public void aSuccessfulOkResponseIsReceivedWithTheSingleEncryptionKeyWithTheCodeAndScopeOf(String scope) {
        utils.validateHttpStatusCode(200);

        List<String> queryScopes = Arrays.asList(scope.split(","));
        Map<String, String> queryScope = new HashMap<>();
        queryScopes.forEach(s -> queryScope.put(s.trim().split(" ")[0], s.trim().split(" ")[1]));
        queryScope.replaceAll((k, v) -> v = v.equals("A") ? uniqueIdA : uniqueIdB);

        utils.validateGetResponseEncryptionKeyItemScope(0, queryScope);
    }

    @And("two inactive encryption keys are created with different codes")
    public void twoInactiveEncryptionKeysAreCreatedWithDifferentCodes() {
        code3 = DEFAULT_CODE_PREFIX + RandomStringUtils.randomAlphabetic(6);
        code4 = DEFAULT_CODE_PREFIX + RandomStringUtils.randomAlphabetic(6);
        index1 = Long.parseLong(RandomStringUtils.randomNumeric(6));
        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code3);
        utils.setStatus("INACTIVE");
        utils.createEncryptionKeyWithIndex(keyType, index1);
        utils.validateHttpStatusCode(201);

        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code4);
        utils.setStatus("INACTIVE");
        utils.createEncryptionKeyWithIndex(keyType, index1);
        utils.validateHttpStatusCode(201);

    }

    @When("the encryption keys are queried with status = {string}")
    public void theEncryptionKeysAreQueriedWithStatus(String status) {
        utils.retrieveEncryptionKeysByStatus(status);
    }

    @Then("a successful OK response is received only with active encryption keys")
    public void aSuccessfulOKResponseIsReceivedOnlyWithActiveEncryptionKeys() {
        utils.validateHttpStatusCode(200);
        utils.validateStatusOfAllRecordsFromMultiKeyResponse("ACTIVE");
    }

    @And("an active encryption key with a different code and specific zone, group and bin")
    public void anActiveEncryptionKeyWithADifferentCodeAndSpecificZoneGroupAndBin() {
        code3 = DEFAULT_CODE_PREFIX + RandomStringUtils.randomAlphabetic(6);
        String uniqueId = RandomStringUtils.randomAlphabetic(6);
        zone1 = uniqueId;
        group1 = uniqueId;
        bin1 = uniqueId;
        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code3);
        utils.setZone(zone1);
        utils.setGroup(group1);
        utils.setBin(bin1);
        utils.setStatus("ACTIVE");
        utils.createEncryptionKeyWithVariantValue(keyType);
        utils.validateHttpStatusCode(201);

    }

    @Given("active keys are created with three different zones")
    public void activeKeysAreCreatedWithThreeDifferentZones() {
        zone1 = RandomStringUtils.randomAlphabetic(6);
        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setZone(zone1);
        utils.setStatus("ACTIVE");
        utils.createEncryptionKeyWithVariantValue(keyType);
        utils.validateHttpStatusCode(201);

        zone2 = RandomStringUtils.randomAlphabetic(6);
        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setZone(zone2);
        utils.setStatus("ACTIVE");
        utils.createEncryptionKeyWithVariantValue(keyType);
        utils.validateHttpStatusCode(201);

        zone3 = RandomStringUtils.randomAlphabetic(6);
        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setZone(zone3);
        utils.setStatus("ACTIVE");
        utils.createEncryptionKeyWithVariantValue(keyType);
        utils.validateHttpStatusCode(201);
    }


    @And("an inactive encryption key with a different code and group but the same zone")
    public void anInactiveEncryptionKeyWithADifferentCodeAndGroupButTheSameZone() {
        code4 = DEFAULT_CODE_PREFIX + RandomStringUtils.randomAlphabetic(6);
        String uniqueId = RandomStringUtils.randomAlphabetic(6);
        utils.createDefaultPayloadEncryptionKeyOnlyVariant();
        utils.setCode(code4);
        utils.setZone(zone1);
        utils.setGroup(uniqueId);
        utils.setBin(bin1);
        utils.setStatus("INACTIVE");
        utils.createEncryptionKeyWithVariantValue(keyType);
        utils.validateHttpStatusCode(201);
    }

    @When("the encryption keys are queried without query parameters")
    public void theEncryptionKeysAreQueriedWithoutQueryParameters() {
        utils.retrieveEncryptionKeysWithoutParameters();
    }

    @Then("a successful OK response is received only with inactive encryption keys")
    public void aSuccessfulOKResponseIsReceivedOnlyWithInactiveEncryptionKeys() {

        utils.validateHttpStatusCode(200);
        utils.validateStatusOfAllRecordsFromMultiKeyResponse("INACTIVE");
    }

    @And("a successful OK response is received and the keys are ordered ascending on creation date")
    public void aSuccessfulOKResponseIsReceivedAndTheKeysAreOrderedAscendingOnCreationDate() {
        utils.validateHttpStatusCode(200);
        utils.validateEncryptionKeysAreOrdered();
    }

    @Then("a successful OK response is received with all keys with highest index both active and inactive")
    public void aSuccessfulOKResponseIsReceivedWithAllKeysWithHighestIndexBothActiveAndInactive() {
        utils.validateMultiKeyResponseContainsEncryptionKeyCodeAndIndex(code1, index2.toString());
        utils.validateMultiKeyResponseContainsEncryptionKeyCodeAndIndex(code2, index2.toString());

        utils.validateMultiKeyResponseContainsEncryptionKeyCodeAndStatus(code4, "INACTIVE");
    }

    @When("the encryption keys are queried with the embed query parameter set to encrypted_key_value")
    public void theEncryptionKeysAreQueriedWithTheEmbedQueryParameterSetToEncryptedKeyValue() {
        utils.retrieveEncryptionKeysWithOnlyEmbedParameter();
    }

    @When("a request is made to search for encryption keys for zone1 and zone2")
    public void aRequestIsMadeToSearchForEncryptionKeysForZoneAndZone() {
        utils.retrieveEncryptionKeysByZoneList(zone1, zone2);
    }

    @Then("Keys for only zone1 and zone2 should be returned")
    public void keysForOnlyZoneAndZoneShouldBeReturned() {
        utils.validateMultiKeyResponseContainsEncryptionKeyZones1And2Only(zone1, zone2);
    }
}
