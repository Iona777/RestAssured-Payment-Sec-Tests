package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_VERIFY_ARQC;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;
import static com.pps.qa.paymentsecurity.databuilders.VerifyArqcDataBuilder.createSecurityCommandRequestResourceVerifyArqc;
import static com.pps.qa.paymentsecurity.databuilders.VerifyArqcDataBuilder.createSecurityCommandRequestResourceVerifyArqcMaxFieldLengths;
import static com.pps.qa.paymentsecurity.databuilders.VerifyArqcDataBuilder.createSecurityCommandRequestResourceVerifyArqcMinFieldLengths;
import static com.pps.qa.paymentsecurity.databuilders.VerifyArqcDataBuilder.createSecurityCommandRequestResourceVerifyArqcWithArpcGeneration;
import static com.pps.qa.paymentsecurity.databuilders.VerifyArqcDataBuilder.createSecurityCommandRequestResourceVerifyArqcWithArpcGenerationWithNoCsuAttribute;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.ApplicationCryptogramKeyDto;
import com.pps.dsl.rest.ErrorResponse;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.SoftAssertions;

/**
 * Holds the step definitions for the tests defined in generate_cvc_values.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.1.0
 */

public class VerifyArqcStepDefs {

    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;
    private SecurityCommandRequestResource request;
    private SecurityCommandResponseResource response;
    private RestResponse restResponse;

    private String validAcKey = "fad4a5c9-efde-43a5-92e5-c270fb951ab1";

    public VerifyArqcStepDefs(PaymentSecurityCommon paySecCommon) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
    }

    @When("a POST request is made to the security-commands endpoint to request an ARQC verification")
    public void aPOSTRequestIsMadeToTheSecurityCommandsEndpointToRequestAnARQCVerification() {
        restResponse = PaymentSecurityDsl.app(paySecCommon.paymentSecurityUrl)
                .securityCommandsClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType("application/json; charset=utf-8")
                .authorization(generateToken(ENCRYPTION_COMMAND_VERIFY_ARQC))
                .with(request)
                .submitPost();

        response = restResponse.body(SecurityCommandResponseResource.class);
    }

    @When("a POST request is made to the security-commands endpoint to request an ARQC verification without permission")
    public void aPOSTRequestIsMadeToTheSecurityCommandsEndpointToRequestAnARQCVerificationWithoutPermission() {
        restResponse = PaymentSecurityDsl.app(paySecCommon.paymentSecurityUrl)
                .securityCommandsClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType("application/json; charset=utf-8")
                .authorization(generateToken(""))
                .with(request)
                .submitPost();

        response = restResponse.body(SecurityCommandResponseResource.class);

    }

    @Given("the payload is set to a default valid security commands payload to request an ARQC verification")
    public void thePayloadIsSetToADefaultValidSecurityCommandsPayloadToRequestAnARQCVerification() {
        request = createSecurityCommandRequestResourceVerifyArqc();
    }

    @Given("the payload is set to a valid security commands payload to request an ARQC verification with minimum field lengths")
    public void thePayloadIsSetToAValidSecurityCommandsPayloadToRequestAnARQCVerificationWithMinimumFieldLengths() {
        request = createSecurityCommandRequestResourceVerifyArqcMinFieldLengths();
    }

    @Given("the payload is set to a valid security commands payload to request an ARQC verification with maximum field lengths")
    public void thePayloadIsSetToAValidSecurityCommandsPayloadToRequestAnARQCVerificationWithMaximumFieldLengths() {
        request = createSecurityCommandRequestResourceVerifyArqcMaxFieldLengths();

    }

    @Given("the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation")
    public void thePayloadIsSetToADefaultValidSecurityCommandsPayloadToRequestAnARQCVerificationWithARPCGeneration() {
        request = createSecurityCommandRequestResourceVerifyArqcWithArpcGeneration();
    }

    @Given("the payload is set to a default valid security commands payload to request an ARQC verification with ARPC generation with no CSU attribute")
    public void thePayloadIsSetToADefaultValidSecurityCommandsPayloadToRequestAnARQCVerificationWithARPCGenerationWithNoCSUAttribute() {
        request = createSecurityCommandRequestResourceVerifyArqcWithArpcGenerationWithNoCsuAttribute();
    }

    @Then("the request to the security-commands endpoint to request an ARQC verification returns an http status code of {int}")
    public void theRequestToTheSecurityCommandsEndpointToRequestAnARQCVerificationReturnsAnHttpStatusCodeOf(
            int expectedStatus) {
        paySecCommon.theCallReturnsAnHttpStatusCodeOf(restResponse.httpCode(), expectedStatus);
    }

    @And("the verify ARQC response contains the same values that are in the request")
    public void theVerifyARQCResponseContainsTheSameValuesThatAreInTheRequest() {
        softAssertions.assertThat(response.verifyArqcs().get(0).pan()).as("ARQC PAN")
                .isEqualTo(request.verifyArqcs().get(0).pan());
        softAssertions.assertThat(response.verifyArqcs().get(0).panSequenceNumber()).as("ARQC PAN Sequence No")
                .isEqualTo(request.verifyArqcs().get(0).panSequenceNumber());
        softAssertions.assertThat(response.verifyArqcs().get(0).scheme()).as("ARQC Scheme")
                .isEqualTo(request.verifyArqcs().get(0).scheme());
        softAssertions.assertThat(response.verifyArqcs().get(0).applicationCryptogramKey().id()).as("ARQC AC key")
                .isEqualTo(request.verifyArqcs().get(0).applicationCryptogramKey().id());
        softAssertions.assertThat(response.verifyArqcs().get(0).arqc()).as("ARQC Arqc")
                .isEqualTo(request.verifyArqcs().get(0).arqc());
        softAssertions.assertThat(response.verifyArqcs().get(0).atc()).as("ARQC Atc")
                .isEqualTo(request.verifyArqcs().get(0).atc());
        softAssertions.assertThat(response.verifyArqcs().get(0).transactionData()).as("ARPC Transaction Data")
                .isEqualTo(request.verifyArqcs().get(0).transactionData());

        if (request.verifyArqcs().get(0).arpcGeneration() != null &&
                !request.verifyArqcs().get(0).arpcGeneration().equalsIgnoreCase("NOT_REQUIRED")) {
            softAssertions.assertThat(response.verifyArqcs().get(0).arpcGeneration()).as("ARPC Generation")
                    .isEqualTo(request.verifyArqcs().get(0).arpcGeneration());
        } else {
            softAssertions.assertThat(response.verifyArqcs().get(0).arpcGeneration()).as("ARPC Generation")
                    .isNull();
        }

        softAssertions.assertThat(response.verifyArqcs().get(0).csu()).as("ARPC CSU")
                .isEqualTo(request.verifyArqcs().get(0).csu());

    }

    @And("the verify ARQC result is returned as {string}")
    public void theResultIsReturnedAs(String expectedResult) {
        String actualResult = response.verifyArqcs().get(0).result();

        softAssertions.assertThat(actualResult).as("Verify ARQC result").isEqualTo(expectedResult);
    }

    @And("there is no arpc in the verify ARQC response")
    public void thereIsNoArpcInTheVerifyARQCResponse() {
        String actualArpc = response.verifyArqcs().get(0).arpc();
        softAssertions.assertThat(actualArpc).as("Arpc").isNull();
    }

    @And("the ARQC application cryptogram key is set to a valid value which exists")
    public void theARQCApplicationCryptogramKeyIsSetToAValidValueWhichExists() {
        ApplicationCryptogramKeyDto key = new ApplicationCryptogramKeyDto();
        key.id(validAcKey);

        request.verifyArqcs().get(0).applicationCryptogramKey(key);
    }

    @And("the ARQC application cryptogram key is set to {string} which does NOT exist")
    public void theARQCApplicationCryptogramKeyIsSetToWhichDoesNotExist(
            String invalidAcKey) {
        ApplicationCryptogramKeyDto key = new ApplicationCryptogramKeyDto();
        key.id(invalidAcKey);

        request.verifyArqcs().get(0).applicationCryptogramKey(key);
    }

    @And("the verify ARQC PAN is set to length {int}")
    public void theVerifyARQCPANIsSetToLength(int length) {
        request.verifyArqcs().get(0).pan(RandomStringUtils.randomNumeric(length));
    }

    @And("the verify ARQC PAN sequence number is set to {string}")
    public void theVerifyArqcPANSequenceNumberIsSetTo(String panSequenceNoValue) {
        request.verifyArqcs().get(0).panSequenceNumber(panSequenceNoValue);
    }

    @And("the verify ARQC scheme is set to {string}")
    public void theVerifyARQCSchemeIsSetTo(String schemeValue) {
        request.verifyArqcs().get(0).scheme(schemeValue);
    }

    @And("the verify ARQC arqc field is set to {string}")
    public void theVerifyARQCArqcFieldIsSetTo(String arqcValue) {
        request.verifyArqcs().get(0).arqc(arqcValue);
    }

    @And("the verify ARQC atc field is set to {string}")
    public void theVerifyARQCAtcFieldIsSetTo(String atcValue) {
        request.verifyArqcs().get(0).atc(atcValue);
    }

    @And("the verify ARQC transaction data field is set to length {int}")
    public void theVerifyARQCTransactionDataFieldIsSetToLength(int length) {
        request.verifyArqcs().get(0).transactionData(RandomStringUtils.randomNumeric(length));
    }

    @And("the verify ARPC csu field is set to {string}")
    public void theVerifyARPCCsuFieldIsSetTo(String csuValue) {
        request.verifyArqcs().get(0).csu(csuValue);
    }

    @And("the verify ARQC generation type is set to {string}")
    public void theVerifyARQCGenerationTypeIsSetTo(String generationTypeValue) {
        request.verifyArqcs().get(0).arpcGeneration(generationTypeValue);
    }

    @And("the ARPQ verification error message contains {string} and the error code is {string}")
    public void theARPQVerificationErrorMessageContainsAndTheErrorCodeIs(String expectedErrorMessage,
            String expectedErrorCode) {
        ErrorResponse actualError = restResponse.body(ErrorResponse.class);
        ErrorResponse expectedError = ErrorResponse.builder()
                .code(expectedErrorCode)
                .message(expectedErrorMessage)
                .build();

        paySecCommon.theErrorMessagePayloadIsAsExpected(actualError, expectedError);
    }

    @And("the response only contains verify arqc fields")
    public void theResponseOnlyContainsVerifyArqcFields() {
        validateOtherSectionsInPostResponseCommandAreEmpty(response);

    }

    /**
     * Used to validate the fields in the sections not required in a successful response are empty.
     *
     * @param response The {@link SecurityCommandRequestResource} object returned from the Payment Security service.
     */
    private void validateOtherSectionsInPostResponseCommandAreEmpty(SecurityCommandResponseResource response) {
        softAssertions.assertThat(response.decryptData()).as("decrypt_data array").isEmpty();
        softAssertions.assertThat(response.translatePinToZones()).as("translate_pin_to_zones array").isEmpty();
        softAssertions.assertThat(response.decryptPins()).as("decrypt_pins array").isEmpty();
        softAssertions.assertThat(response.encryptData()).as("encrypt_data array").isEmpty();
        softAssertions.assertThat(response.generatePinOffsets()).as("generate_pin_offsets array").isEmpty();
        softAssertions.assertThat(response.generatePins()).as("generate_pins array").isEmpty();
        softAssertions.assertThat(response.generateCvcs()).as("generate_cvcs array").isEmpty();
        softAssertions.assertThat(response.generateArpcs()).as("generate_arpcs array").isEmpty();
        //This is the one section that should be populated
        softAssertions.assertThat(response.verifyArqcs()).as("verify_arqcs array").isNotEmpty();
        softAssertions.assertThat(response.encryptPins()).as("encrypt_pins array").isEmpty();
        softAssertions.assertThat(response.verifyArqcs().size()).as("verify arqc array size")
                .isEqualTo(request.verifyArqcs().size());

    }

}
