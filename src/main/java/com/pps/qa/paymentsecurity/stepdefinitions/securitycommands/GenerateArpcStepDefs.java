package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_GENERATE_ARPC;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;
import static com.pps.qa.paymentsecurity.databuilders.GenerateArpcDataBuilder.createSecurityCommandRequestResourceGenerateArpc;

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

public class GenerateArpcStepDefs {

    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;
    private final String validAcKey = "fad4a5c9-efde-43a5-92e5-c270fb951ab1";
    private SecurityCommandRequestResource request;
    private SecurityCommandResponseResource response;
    private RestResponse restResponse;

    public GenerateArpcStepDefs(PaymentSecurityCommon paySecCommon) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
    }

    @When("a POST request is made to the security-commands endpoint to request an ARPC generation")
    public void aPOSTRequestIsMadeToTheSecurityCommandsEndpointToRequestAnARPCGeneration() {
        restResponse = PaymentSecurityDsl.app(paySecCommon.paymentSecurityUrl)
                .securityCommandsClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType("application/json; charset=utf-8")
                .authorization(generateToken(ENCRYPTION_COMMAND_GENERATE_ARPC))
                .with(request)
                .submitPost();

        response = restResponse.body(SecurityCommandResponseResource.class);
    }

    @When("a POST request is made to the security-commands endpoint to request an ARPC generation without permission")
    public void aPOSTRequestIsMadeToTheSecurityCommandsEndpointToRequestAnARPCGenerationWithoutPermission() {
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

    @Given("the payload is set to a default valid security commands payload to request an ARPC generation")
    public void thePayloadIsSetToADefaultValidSecurityCommandsPayloadToRequestAnARPCGeneration() {
        request = createSecurityCommandRequestResourceGenerateArpc();
    }

    @Then("the request to the security-commands endpoint to request an ARPC generation returns an http status code of {int}")
    public void theRequestToTheSecurityCommandsEndpointToRequestAnARPCGenerationReturnsAnHttpStatusCodeOf(
            int expectedStatus) {
        paySecCommon.theCallReturnsAnHttpStatusCodeOf(restResponse.httpCode(), expectedStatus);
    }

    @And("the generate ARPC response contains the same values that are in the request")
    public void theResponseContainsTheSameValuesThatAreInTheRequest() {
        softAssertions.assertThat(response.generateArpcs().get(0).pan()).as("ARPC PAN")
                .isEqualTo(request.generateArpcs().get(0).pan());
        softAssertions.assertThat(response.generateArpcs().get(0).panSequenceNumber()).as("ARPC PAN Sequence No")
                .isEqualTo(request.generateArpcs().get(0).panSequenceNumber());
        softAssertions.assertThat(response.generateArpcs().get(0).scheme()).as("ARPC Scheme")
                .isEqualTo(request.generateArpcs().get(0).scheme());
        softAssertions.assertThat(response.generateArpcs().get(0).method()).as("ARPC Method")
                .isEqualTo(request.generateArpcs().get(0).method());
        softAssertions.assertThat(response.generateArpcs().get(0).applicationCryptogramKey().id()).as("AC key")
                .isEqualTo(request.generateArpcs().get(0).applicationCryptogramKey().id());
        softAssertions.assertThat(response.generateArpcs().get(0).arqc()).as("ARPC Arqc")
                .isEqualTo(request.generateArpcs().get(0).arqc());
        softAssertions.assertThat(response.generateArpcs().get(0).atc()).as("ARPC Atc")
                .isEqualTo(request.generateArpcs().get(0).atc());
        softAssertions.assertThat(response.generateArpcs().get(0).csu()).as("ARPC Csu")
                .isEqualTo(request.generateArpcs().get(0).csu());
    }

    @And("the ARPC application cryptogram key is set to a valid value which exists")
    public void theARPCApplicationCryptogramKeyIsSetToAValidValueWhichExists() {

        ApplicationCryptogramKeyDto key = new ApplicationCryptogramKeyDto();
        key.id(validAcKey);

        request.generateArpcs().get(0).applicationCryptogramKey(key);
    }

    @And("the ARPC application cryptogram key is set to {string} which does NOT exist")
    public void theArpcApplicationCryptogramKeyIsSetToWhichDoesNotExist(
            String invalidAcKey) {

        ApplicationCryptogramKeyDto key = new ApplicationCryptogramKeyDto();
        key.id(invalidAcKey);

        request.generateArpcs().get(0).applicationCryptogramKey(key);
    }

    @And("the generate ARPC PAN is set to {string}")
    public void thegenerateARPCPANIsSetTo(String panValue) {
        request.generateArpcs().get(0).pan(panValue);
    }

    @And("the generate ARQC PAN is set to length {int}")
    public void theVerifyARQCPANIsSetToLength(int length) {
        request.generateArpcs().get(0).pan(RandomStringUtils.randomNumeric(length));
    }

    @And("the generate ARPC PAN sequence number is set to {string}")
    public void thegenerateArpcPANSequenceNumberIsSetTo(String panSequenceNoValue) {
        request.generateArpcs().get(0).panSequenceNumber(panSequenceNoValue);
    }

    @And("the generate ARPC scheme is set to {string}")
    public void theGenerateArpcSchemeIsSetTo(String schemeValue) {
        request.generateArpcs().get(0).scheme(schemeValue);
    }

    @And("the generate ARPC method is set to {string}")
    public void theGenerateArpcMethodIsSetTo(String methodValue) {
        request.generateArpcs().get(0).method(methodValue);
    }

    @And("the generate ARPC arqc field is set to {string}")
    public void theGenerateARPCArqcFieldIsSetTo(String arqcValue) {
        request.generateArpcs().get(0).arqc(arqcValue);
    }

    @And("the generate ARPC atc field is set to {string}")
    public void theGenerateARPCAtcFieldIsSetTo(String atcValue) {
        request.generateArpcs().get(0).atc(atcValue);
    }

    @And("the generate ARPC csu field is set to {string}")
    public void theGenerateARPCCsuFieldIsSetTo(String csuValue) {
        request.generateArpcs().get(0).csu(csuValue);
    }

    @And("the arpc in the response is {string}")
    public void theArpcInTheResponseIs(String expectedArpc) {
        String actualArpc = response.generateArpcs().get(0).arpc();
        softAssertions.assertThat(actualArpc).as("Arpc").isEqualTo(expectedArpc);
    }

    @And("the ARPC generation error message contains {string} and the error code is {string}")
    public void theARPCGenerationErrorMessageContainsAndTheErrorCodeIs(String expectedErrorMessage,
            String expectedErrorCode) {
        ErrorResponse actualError = restResponse.body(ErrorResponse.class);
        ErrorResponse expectedError = ErrorResponse.builder()
                .code(expectedErrorCode)
                .message(expectedErrorMessage)
                .build();

        paySecCommon.theErrorMessagePayloadIsAsExpected(actualError, expectedError);
    }

    @And("the response only contains generate arpc fields")
    public void theResponseOnlyContainsGenerateArpcFields() {
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
        //This is the one section that should be populated
        softAssertions.assertThat(response.generateArpcs()).as("generate_arpcs array").isNotEmpty();
        softAssertions.assertThat(response.verifyArqcs()).as("verify_arqcs array").isEmpty();
        softAssertions.assertThat(response.encryptPins()).as("encrypt_pins array").isEmpty();
        softAssertions.assertThat(response.generateArpcs().size()).as("generate arpc array size")
                .isEqualTo(request.generateArpcs().size());

    }
}
