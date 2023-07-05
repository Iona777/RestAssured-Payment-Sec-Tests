package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;


import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_GENERATE_PIN;
import static com.pps.qa.paymentsecurity.databuilders.DecryptPinDataBuilder.createEmptyGeneratePinSecurityCommand;
import static com.pps.qa.paymentsecurity.databuilders.GeneratePinDataBuilder.addRequestPayloadForGeneratePin;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.request.GeneratePinCommandRequestDto;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import com.pps.qa.paymentsecurity.datacontext.GeneratePinDataContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.SoftAssertions;

/**
 * Holds the step definitions for the tests defined in generate_card_pin.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.1.0
 */

public class GeneratePinStepDefs {

    /**
     * Framework related variables
     */
    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;
    private final GeneratePinDataContext testData;
    /**
     * Request/Response values for these tests
     */
    private SecurityCommandRequestResource requestPayloadForPostGeneratePin;
    private SecurityCommandResponseResource postGeneratePinResponse;
    private RestResponse restResponse;


    public GeneratePinStepDefs(PaymentSecurityCommon paySecCommon, GeneratePinDataContext testData) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
        this.testData = testData;
    }

    @When("a request is made to generate multiple card PINs")
    public void aRequestIsMadeToGenerateMultipleCardPINs() {
        generateMultipleCardPins(new Random().nextInt(9) + 1);
    }

    @Then("the response contains the generated pin blocks")
    public void theResponseContainsTheGeneratedPinBlocks() {
        softAssertions.assertThat(checkResponseContainsTheGeneratedPinBlocksOfCorrectLength()).as("Generated pin")
                .isTrue();
    }

    @Then("the request to the security-commands endpoint to request a Pin Generation returns an http status code of {int}")
    public void theRequestToTheSecurityCommandsEndpointToRequestAPinGenerationReturnsAnHttpStatusCodeOf(
            int expectedStatus) {
        this.paySecCommon.theCallReturnsAnHttpStatusCodeOf(this.restResponse.httpCode(), expectedStatus);
    }

    @And("the response only contains generate pin fields")
    public void theResponseOnlyContainsGeneratePinFields() {
        validateOtherSectionsInPostResponseCommandAreEmpty(postGeneratePinResponse);
    }

    /**
     * Creates a generate pins payload for payment security POST endpoint and calls that endpoint to generate pins Also
     * stores values from response in a GeneratePinDataContext variable (testData) so that is can be shared with other
     * requests as required.
     *
     * @param numberOfPins The number of pins to generate
     */
    protected void generateMultipleCardPins(int numberOfPins) {
        int pinLength = 4;

        //Create an empty request payload
        this.requestPayloadForPostGeneratePin = createEmptyGeneratePinSecurityCommand();
        //Create pins list object that points at the generate pins part of the request payload
        List<GeneratePinCommandRequestDto> pins = this.requestPayloadForPostGeneratePin.generatePins(new ArrayList<>())
                .generatePins();

        for (int i = 0; i < numberOfPins; i++) {
            String pan = RandomStringUtils.randomNumeric(19);
            //populates pin object with the pan and pinlength
            pins.add(addRequestPayloadForGeneratePin(pan, pinLength));
        }

        //Populates the request payload with the pins object
        //Then makes the call to the endpoint using common method. Sets restResponse for checking http code and
        //postGeneratePinResponse for checking response values
        this.requestPayloadForPostGeneratePin.generatePins(pins);
        this.restResponse = paySecCommon.postSecurityCommandPayload(this.requestPayloadForPostGeneratePin,
                ENCRYPTION_COMMAND_GENERATE_PIN);
        this.postGeneratePinResponse = this.restResponse.body(SecurityCommandResponseResource.class);

        for (int i = 0; i < numberOfPins; i++) {
            //This will store the values from the response so that they can be accessed by other requests
            String pinBlock = this.postGeneratePinResponse.generatePins().get(i).pinBlock();
            String pan = this.postGeneratePinResponse.generatePins().get(i).pan();

            testData.addItemOriginal(pinBlock, pan, pinLength);
        }
    }

    /**
     * Checks that the returned pin block is of the same length as specified in the request +1.
     *
     * @return boolean
     */
    private boolean checkResponseContainsTheGeneratedPinBlocksOfCorrectLength() {
        int size = this.requestPayloadForPostGeneratePin.generatePins().size();

        for (int i = 0; i < size; i++) {
            int pinLength = this.requestPayloadForPostGeneratePin.generatePins().get(i).pinLength();
            String generatedPinBlock = this.postGeneratePinResponse.generatePins().get(i).pinBlock();

            if (generatedPinBlock.length() != pinLength + 1) {
                return false;
            }
        }
        return true;
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
        //This is the one section that should be populated
        softAssertions.assertThat(response.generatePins()).as("generate_pins array").isNotEmpty();
        softAssertions.assertThat(response.generateCvcs()).as("generate_cvcs array").isEmpty();
        softAssertions.assertThat(response.generateArpcs()).as("generate_arpcs array").isEmpty();
        softAssertions.assertThat(response.verifyArqcs()).as("verify_arqcs array").isEmpty();
        softAssertions.assertThat(response.encryptPins()).as("encrypt_pins array").isEmpty();
        softAssertions.assertThat(response.generatePins().size()).as("generate pin array size")
                .isEqualTo(requestPayloadForPostGeneratePin.generatePins().size());

    }

    @When("a request is made to generate {int} PIN blocks for a card")
    public void aRequestIsMadeToGeneratePINBlocksForACard(int noOfPinBlocks) {
        generateMultipleCardPins(noOfPinBlocks);
    }
}
