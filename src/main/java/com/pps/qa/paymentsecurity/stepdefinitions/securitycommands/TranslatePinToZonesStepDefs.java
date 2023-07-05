package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_TRANSLATE_PIN;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.ZonePinBlockFormatDto;
import com.pps.dsl.paymentsecurity.domain.dto.ZonePinKeyDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.TranslatePinToZonesCommandRequestDto;
import com.pps.dsl.rest.ErrorResponse;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import com.pps.qa.paymentsecurity.datacontext.GeneratePinDataContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;

/**
 * Holds the step definitions for the tests defined in translate-card-pinblock-to-zpk.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */

public class TranslatePinToZonesStepDefs {

    /**
     * Framework related variables
     */
    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;
    private final GeneratePinDataContext testData;
    private final List<String> zonePinKeyIdAll = new ArrayList<>(
            Arrays.asList("011424fa-67e5-4d99-9176-1c5242860763", "67c17efb-c88a-4b4e-96d7-ba59931ce79a",
                    "45c2b392-2c7a-4177-af86-e8614f464c44"));
    private final List<String> zonePinKeyIdVariantAll = new ArrayList<>(
            Arrays.asList("b896b79e-907c-43a6-9717-886256edbd5e", "c3c3a3ff-3c01-4455-a274-50f3b5e73c11",
                    "f0056e00-d278-4c1d-9759-0ab1102d4b6b"));
    /**
     * Request/Response values for these tests
     */
    private SecurityCommandRequestResource requestPayload;
    private SecurityCommandResponseResource response;
    private RestResponse restResponse;
    private List<String> zonePinKeyIdList = new ArrayList<>();


    public TranslatePinToZonesStepDefs(PaymentSecurityCommon paySecCommon, GeneratePinDataContext testData) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
        this.testData = testData;
    }

    @Given("a request is made to create {int} ZPK keys on the Payment Security service")
    public void aRequestIsMadeToCreateZPKKeysOnThePaymentSecurityService(int noOfZonePinKeyIds) {
        if (noOfZonePinKeyIds <= zonePinKeyIdAll.size()) {
            createZpkEncryptionKey(noOfZonePinKeyIds);
        } else {
            throw new InvalidParameterException("noOfZonePinKeyIds must not exceed " + zonePinKeyIdAll.size());

        }
    }

    @And("{int} ZPK keys are created")
    public void zpkKeysAreCreated(int noOfZonePinKeyIds) {
        if (noOfZonePinKeyIds <= zonePinKeyIdAll.size()) {
            createZpkEncryptionKey(noOfZonePinKeyIds);
        } else {
            throw new InvalidParameterException("noOfZonePinKeyIds must not exceed " + zonePinKeyIdAll.size());

        }
    }

    @When("a request is made to translate each of the card PIN block to the ZPK in {string}")
    public void aRequestIsMadeToTranslateEachOfTheCardPINBlockToTheZPKIn(String format) {
        translateAllPinBlocksToAllZonesZpkFormat(format);
    }

    @Then("the translate pin to zones response contains the expected values")
    public void theGeneratedPINOffsetResponseContainsTheExpectedValues() {

        for (int i = 0; i < response.translatePinToZones().size(); i++) {
            softAssertions.assertThat(response.translatePinToZones().get(i).pan()).as("Translate pin to zones PAN")
                    .isEqualTo(requestPayload.translatePinToZones().get(i).pan());

            softAssertions.assertThat(response.translatePinToZones().get(i).pinBlock())
                    .as("Translate pin to zones Pin Block")
                    .isEqualTo(requestPayload.translatePinToZones().get(i).pinBlock());

            softAssertions.assertThat(response.translatePinToZones().get(i).zonePinBlockFormat())
                    .as("Translate pin to zones pin block format")
                    .isEqualTo(requestPayload.translatePinToZones().get(i).zonePinBlockFormat());

            softAssertions.assertThat(response.translatePinToZones().get(i).zonePinKey().id())
                    .as("Translate pin to zones pin Key ID")
                    .isEqualTo(requestPayload.translatePinToZones().get(i).zonePinKey().id());

            softAssertions.assertThat(response.translatePinToZones().get(i).zonePinBlock())
                    .as("Translate pin to zones pin block").hasSizeGreaterThanOrEqualTo(4);
            softAssertions.assertThat(response.translatePinToZones().get(i).zonePinBlock())
                    .as("Translate pin to zones pin block").hasSizeLessThanOrEqualTo(32);
        }
    }

    @Then("the request to the security-commands endpoint to translate the card PIN block to the ZPK returns an http status code of {int}")
    public void theRequestToTheSecurityCommandsEndpointToTranslateTheCardPINBlockToTheZPKReturnsAnHttpStatusCodeOf(
            int expectedStatus) {
        this.paySecCommon.theCallReturnsAnHttpStatusCodeOf(this.restResponse.httpCode(), expectedStatus);
    }

    @And("the response only contains translate pin to zones fields")
    public void theResponseOnlyContainsTranslatePinToZonesFields() {
        validateOtherSectionsInPostResponseCommandAreEmpty(this.response);
    }

    @And("a ZPK key does not have a keyblock value, but has a variant value")
    public void aZPKKeyDoesNotHaveAKeyblockValueButHasAVariantValue() {
        for (int i = 0; i < 1; i++) {
            this.zonePinKeyIdList.add(zonePinKeyIdVariantAll.get(i));
        }
    }

    @And("the translate pin to zones error message contains {string} and the error code is {string}")
    public void theTranslatePinToZonesErrorMessageContainsAndTheErrorCodeIs(String expectedErrorMessage,
            String expectedErrorCode) {
        ErrorResponse actualError = restResponse.body(ErrorResponse.class);
        ErrorResponse expectedError = ErrorResponse.builder()
                .code(expectedErrorCode)
                .message(expectedErrorMessage)
                .build();

        paySecCommon.theErrorMessagePayloadIsAsExpected(actualError, expectedError);
    }

    /**
     * Creates one or more zpk keys to use in test from a pre-determined list
     *
     * @param noZonePinKeyIds number of keys required
     */
    private void createZpkEncryptionKey(int noZonePinKeyIds) {
        for (int i = 0; i < noZonePinKeyIds; i++) {
            this.zonePinKeyIdList.add(zonePinKeyIdAll.get(i));
        }
    }

    /**
     * Creates a translatePinToZones payload then makes request to Payment Security application
     *
     * @param format sets value for the zonePinBlockFormat
     */
    private void translateAllPinBlocksToAllZonesZpkFormat(String format) {
        requestPayload = createTranslatePinToZonesSecurityCommand();
        List<TranslatePinToZonesCommandRequestDto> pinToZones = requestPayload.translatePinToZones(new ArrayList<>())
                .translatePinToZones();

        //Get these values from the GeneratePinDataContext that was set by an earlier POST request
        for (String zoneId : zonePinKeyIdList) {
            for (int i = 0; i < testData.items.size(); i++) {
                pinToZones.add(new TranslatePinToZonesCommandRequestDto()
                        .pan(testData.items.get(i).pan())
                        .pinBlock(testData.items.get(i).pinBlock())
                        .zonePinBlockFormat(ZonePinBlockFormatDto.valueOf(format))
                        .zonePinKey(new ZonePinKeyDto()
                                .id(zoneId)));
            }
        }

        //Populates the request payload with the pins object
        //Then makes the call to the endpoint using common method.
        //Sets restResponse for checking http code and response for checking response values
        this.requestPayload.translatePinToZones(pinToZones);
        this.restResponse = paySecCommon.postSecurityCommandPayload(this.requestPayload,
                ENCRYPTION_COMMAND_TRANSLATE_PIN);
        this.response = this.restResponse.body(SecurityCommandResponseResource.class);
    }

    /**
     * Creates an empty {@link SecurityCommandRequestResource} object with a translatePinToZones section
     */
    private SecurityCommandRequestResource createTranslatePinToZonesSecurityCommand() {
        return new SecurityCommandRequestResource()
                .translatePinToZones(new ArrayList<>());
    }

    /**
     * Used to validate the fields in the sections not required in a successful response are empty.
     *
     * @param response The {@link SecurityCommandRequestResource} object returned from the Payment Security service.
     */
    private void validateOtherSectionsInPostResponseCommandAreEmpty(SecurityCommandResponseResource response) {
        softAssertions.assertThat(response.decryptData()).as("decrypt_data array").isEmpty();
        //This is the one section that should be populated
        softAssertions.assertThat(response.translatePinToZones()).as("translate_pin_to_zones array").isNotEmpty();
        softAssertions.assertThat(response.decryptPins()).as("decrypt_pins array").isEmpty();
        softAssertions.assertThat(response.encryptData()).as("encrypt_data array").isEmpty();
        softAssertions.assertThat(response.generatePinOffsets()).as("generate_pin_offsets array").isEmpty();
        softAssertions.assertThat(response.generatePins()).as("generate_pins array").isEmpty();
        softAssertions.assertThat(response.generateCvcs()).as("generate_cvcs array").isEmpty();
        softAssertions.assertThat(response.generateArpcs()).as("generate_arpcs array").isEmpty();
        softAssertions.assertThat(response.verifyArqcs()).as("verify_arqcs array").isEmpty();
        softAssertions.assertThat(response.encryptPins()).as("encrypt_pins array").isEmpty();

        softAssertions.assertThat(response.translatePinToZones().size()).as("translate pin to zones array size")
                .isEqualTo(requestPayload.translatePinToZones().size());

    }
}
