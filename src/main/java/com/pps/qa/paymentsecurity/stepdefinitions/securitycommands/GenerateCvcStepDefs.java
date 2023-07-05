package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_GENERATE_CVC;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;
import static com.pps.qa.paymentsecurity.databuilders.GenerateCvcDataBuilder.createEmptyGenerateCvcSecurityCommand;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.CardVerificationKeyDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.GenerateCvcCommandRequestDto;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;

/**
 * Holds the step definitions for the tests defined in generate_cvc_values.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */

public class GenerateCvcStepDefs {

    /**
     * Framework related variables
     */
    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;
    private String cardVerificationId;
    private List<String> cvcIdAll = new ArrayList<>(
            Arrays.asList("ddd3197c-bb42-4307-94d9-2a5694492632", "ddd3197c-bb42-4307-94d9-2a5694492631",
                    "f9ce5de3-8467-45f1-9989-8279bd756550", "3b0817d7-d169-49fa-9db2-00e6dbba88ca"));

    private String pan;
    private String serviceCode;
    private LocalDate expiryDate;

    /**
     * Request/Response values for these tests
     */
    private SecurityCommandRequestResource requestPayloadForPostGenerateCvc;
    private SecurityCommandResponseResource postGenerateCvcResponse;
    private RestResponse restResponse;


    public GenerateCvcStepDefs(PaymentSecurityCommon paySecCommon) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
    }

    @Given("a card verification key exists for {string}")
    public void aCardVerificationKeyExistsFor(String cvk) {
        setCardVerificationId(cvk);
    }

    @Given("only VARIANT of a card verification key exists for {string}")
    public void onlyVARIANTOfACardVerificationKeyExistsFor(String cvk) {
        setVariantCardVerificationId(cvk);
    }

    @And("the pan and expiry date are provided as {string} and {string}")
    public void thePanAndExpiryDateAreProvidedAsAnd(String pan, String expiryDate) {
        this.pan = pan;
        this.expiryDate = LocalDate.parse(expiryDate);
    }

    @And("the service code is provided as {string}")
    public void theServiceCodeIsProvidedAs(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    @When("a request is made to generate a CVC of type {string} using the card's verification key")
    public void aRequestIsMadeToGenerateACVCOfTypeUsingTheCardSVerificationKey(String cvcType) {
        this.requestPayloadForPostGenerateCvc = createEmptyGenerateCvcSecurityCommand();

        requestPayloadForPostGenerateCvc.generateCvcs(Arrays.asList(new GenerateCvcCommandRequestDto()
                .pan(this.pan)
                .type(GenerateCvcCommandRequestDto.Type.valueOf(cvcType))
                .expiryDate(this.expiryDate)
                .serviceCode(this.serviceCode)
                .cardVerificationKey(new CardVerificationKeyDto().id(cardVerificationId))
        ));

        this.postGenerateCvcResponse = postSecurityCommandPayload(this.requestPayloadForPostGenerateCvc,
                ENCRYPTION_COMMAND_GENERATE_CVC);
    }

    @Then("the request to the security-commands endpoint to request a Cvc Generation returns an http status code of {int}")
    public void theRequestToTheSecurityCommandsEndpointToRequestACvcGenerationReturnsAnHttpStatusCodeOf(
            int expectedStatus) {
        this.paySecCommon.theCallReturnsAnHttpStatusCodeOf(this.restResponse.httpCode(), expectedStatus);
    }

    @And("the response contains the generated CVC value of {string}")
    public void theResponseContainsTheGeneratedCVCValueOf(String expectedCvc) {
        String actualCVC = this.postGenerateCvcResponse.generateCvcs().get(0).cvc();

        softAssertions.assertThat(actualCVC).as("Cvc value")
                .isEqualTo(expectedCvc);
    }

    @And("the response only contains generate cvcs fields")
    public void theResponseOnlyContainsGenerateCvcsFields() {
        validateOtherSectionsInPostResponseCommandAreEmpty(postGenerateCvcResponse);
    }

    /**
     * Sets the value of cardVerificationId from list in cvcIdAll array
     *
     * @param type the type of card verification, either chip or magstripe
     */
    protected void setCardVerificationId(String type) {
        switch (type) {
            case ("chip"):
                this.cardVerificationId = this.cvcIdAll.get(0);
                break;
            case ("magstripe"):
                this.cardVerificationId = this.cvcIdAll.get(1);
                break;
            default:
                throw new InvalidParameterException(type + " is not a valid value for type");
        }
    }

    /**
     * Sets the value of cardVerificationId from list in cvcIdAll array for variant
     *
     * @param type the type of card verification, either chp or magstripe
     */
    protected void setVariantCardVerificationId(String type) {
        switch (type) {
            case ("chip"):
                this.cardVerificationId = this.cvcIdAll.get(2);
                break;
            case ("magstripe"):
                this.cardVerificationId = this.cvcIdAll.get(3);
                break;
            default:
                throw new InvalidParameterException(type + " is not a valid value for type");
        }
    }

    /**
     * Sends a POST request to Payment Security application with supplied payload and permission
     *
     * @param requestPayload The payload to send to the request
     * @param permission     The permission to send to the request
     * @return SecurityCommandResponseResource
     */
    private SecurityCommandResponseResource postSecurityCommandPayload(SecurityCommandRequestResource requestPayload,
            String permission) {
        this.restResponse = PaymentSecurityDsl.app(this.paySecCommon.paymentSecurityUrl)
                .securityCommandsClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .authorization(generateToken(permission))
                .with(requestPayload)
                .submitPost();

        return this.restResponse.body(SecurityCommandResponseResource.class);
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
        //This is the one section that should be populated
        softAssertions.assertThat(response.generateCvcs()).as("generate_cvcs array").isNotEmpty();
        softAssertions.assertThat(response.generateArpcs()).as("generate_arpcs array").isEmpty();
        softAssertions.assertThat(response.verifyArqcs()).as("verify_arqcs array").isEmpty();
        softAssertions.assertThat(response.encryptPins()).as("encrypt_pins array").isEmpty();
        softAssertions.assertThat(response.generateCvcs().size()).as("generate_cvcs array size")
                .isEqualTo(requestPayloadForPostGenerateCvc.generateCvcs().size());

    }


}
