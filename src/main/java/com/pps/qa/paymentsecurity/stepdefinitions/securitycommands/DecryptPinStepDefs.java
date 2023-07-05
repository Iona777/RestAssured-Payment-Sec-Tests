package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;


import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_DECRYPT_PIN;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;
import static com.pps.qa.paymentsecurity.databuilders.DecryptPinDataBuilder.createEmptyDecryptCardPinSecurityCommand;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.request.DecryptPinCommandRequestDto;
import com.pps.dsl.rest.ErrorResponse;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import com.pps.qa.paymentsecurity.datacontext.GeneratePinDataContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import org.assertj.core.api.SoftAssertions;

/**
 * Holds the step definitions for the tests defined in decrypt_card_pin.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.1.0
 */

public class DecryptPinStepDefs {

    /**
     * Framework related variables
     */
    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;
    private final GeneratePinDataContext testData;
    /**
     * Request/Response values for these tests
     */
    private SecurityCommandRequestResource requestPayloadForPostDecryptPin;
    private RestResponse restResponse;
    private SecurityCommandResponseResource postDecryptPinResponse;

    public DecryptPinStepDefs(PaymentSecurityCommon paySecCommon, GeneratePinDataContext testData) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
        this.testData = testData;
    }

    @When("a request is made to decrypt the card PIN")
    public void aRequestIsMadeToDecryptTheCardPIN() {
        decryptCardPins(1);
    }

    @When("a request is made to decrypt the PIN blocks")
    public void aRequestIsMadeToDecryptThePINBlocks() {
        decryptCardPins(4);
    }

    @Then("the response contains the clear PIN value")
    public void theResponseContainsTheClearPINValue() {
        int size = this.requestPayloadForPostDecryptPin.decryptPins().size();

        for (int i = 0; i < size; i++) {
            softAssertions.assertThat(this.postDecryptPinResponse.decryptPins().get(i).pin().length() == 4)
                    .as("Decrypted pin data").isTrue();
        }
    }

    @Then("the request to the security-commands endpoint to request a Pin Decryption returns an http status code of {int}")
    public void theRequestToTheSecurityCommandsEndpointToRequestAPinDecryptionReturnsAnHttpStatusCodeOf(
            int expectedStatus) {
        this.paySecCommon.theCallReturnsAnHttpStatusCodeOf(this.restResponse.httpCode(), expectedStatus);
    }

    @Given("a card PIN block was was generated through the Payment Security service using a Variant key")
    public void aCardPINBlockWasWasGeneratedThroughThePaymentSecurityServiceUsingAVariantKey() {
        this.requestPayloadForPostDecryptPin = createEmptyDecryptCardPinSecurityCommand();
        addRequestPayloadForDecryptPin("1234567890123", "14765");
    }

    @When("a request is made to decrypt the Variant card PIN")
    public void aRequestIsMadeToDecryptTheVariantCardPIN() {
        this.postDecryptPinResponse = postSecurityCommandPayload(this.requestPayloadForPostDecryptPin,
                "encryption-command:decrypt-pin");
    }

    @And("the decrypt card pin error message contains {string} and the error code is {string}")
    public void theDecryptCardPinErrorMessageContainsAndTheErrorCodeIs(String expectedErrorMessage,
            String expectedErrorCode) {
        ErrorResponse actualError = this.restResponse.body(ErrorResponse.class);
        ErrorResponse expectedError = ErrorResponse.builder()
                .code(expectedErrorCode)
                .message(expectedErrorMessage)
                .build();

        this.paySecCommon.theErrorMessagePayloadIsAsExpected(actualError, expectedError);
    }

    @And("the response only contains decrypt pin fields")
    public void theResponseOnlyContainsDecryptPinFields() {
        validateOtherSectionsInPostResponseCommandAreEmpty(postDecryptPinResponse);
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
                .contentType("application/json; charset=utf-8")
                .authorization(generateToken(permission))
                .with(requestPayload)
                .submitPost();

        return this.restResponse.body(SecurityCommandResponseResource.class);
    }

    /**
     * Creates a decrypt pin request payload and sends it in a POST request to Payment Security application
     *
     * @param number The number of pins to decrypt
     */
    private void decryptCardPins(int number) {
        //Create an empty payload
        this.requestPayloadForPostDecryptPin = createEmptyDecryptCardPinSecurityCommand();
        for (int i = 0; i < number; i++) {
            DecryptPinCommandRequestDto decryptPinItem = new DecryptPinCommandRequestDto();
            //Get these values from the GeneratePinDataContext that was set by an earlier POST request
            GeneratePinDataContext.GeneratePinData item = testData.items.get(i);
            String pan = item.pan();
            String pinBlock = item.pinBlock();

            decryptPinItem.pan(pan);
            decryptPinItem.pinBlock(pinBlock);

            this.requestPayloadForPostDecryptPin.decryptPins().add(i, decryptPinItem);
        }
        this.postDecryptPinResponse = postSecurityCommandPayload(this.requestPayloadForPostDecryptPin,
                ENCRYPTION_COMMAND_DECRYPT_PIN);
    }


    /**
     * Creates an empty {@link SecurityCommandRequestResource} object with a decryptPins section
     */
    private void addRequestPayloadForDecryptPin(String pan, String pinBlock) {
        this.requestPayloadForPostDecryptPin.decryptPins(Arrays.asList(
                new DecryptPinCommandRequestDto()
                        .pan(pan)
                        .pinBlock(pinBlock)));
    }

    /**
     * Used to validate the fields in the sections not required in a successful response are empty.
     *
     * @param response The {@link SecurityCommandRequestResource} object returned from the Payment Security service.
     */
    private void validateOtherSectionsInPostResponseCommandAreEmpty(SecurityCommandResponseResource response) {
        softAssertions.assertThat(response.decryptData()).as("decrypt_data array").isEmpty();
        softAssertions.assertThat(response.translatePinToZones()).as("translate_pin_to_zones array").isEmpty();
        //This is the one section that should be populated
        softAssertions.assertThat(response.decryptPins()).as("decrypt_pins array").isNotEmpty();
        softAssertions.assertThat(response.encryptData()).as("encrypt_data array").isEmpty();
        softAssertions.assertThat(response.generatePinOffsets()).as("generate_pin_offsets array").isEmpty();
        softAssertions.assertThat(response.generatePins()).as("generate_pins array").isEmpty();
        softAssertions.assertThat(response.generateCvcs()).as("generate_cvcs array").isEmpty();
        softAssertions.assertThat(response.generateArpcs()).as("generate_arpcs array").isEmpty();
        softAssertions.assertThat(response.verifyArqcs()).as("verify_arqcs array").isEmpty();
        softAssertions.assertThat(response.encryptPins()).as("encrypt_pins array").isEmpty();
        softAssertions.assertThat(response.decryptPins().size()).as("decrypt pin array size")
                .isEqualTo(requestPayloadForPostDecryptPin.decryptPins().size());

    }

}
