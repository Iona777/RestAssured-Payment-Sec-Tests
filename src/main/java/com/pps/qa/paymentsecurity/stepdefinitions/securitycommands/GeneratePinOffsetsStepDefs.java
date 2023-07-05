package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_GENERATE_PIN_OFFSET;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;
import static com.pps.dsl.paymentsecurity.domain.dto.PinOffsetType.UTA;
import static com.pps.qa.paymentsecurity.databuilders.GeneratePinOffsetDataBuilder.createEmptyGeneratePinOffsetSecurityCommand;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.PinVerificationKeyDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.GeneratePinOffsetCommandRequestDto;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;

/**
 * Holds the step definitions for the tests defined in generate_pin_offsets.feature.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */

public class GeneratePinOffsetsStepDefs {

    /**
     * Framework related variables
     */
    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;
    private final List<String> cardPinVerificationKeyIdList = new ArrayList<>();
    private final List<String> cardPinVerificationKeyIdAll = new ArrayList<>(
            Arrays.asList("4b5a2016-4d75-4229-9f1d-6eac4b548663", "c1fe4139-5285-4cbb-a483-04e9ed44164a"));

    /**
     * Request/Response values for these tests
     */
    private SecurityCommandRequestResource requestPayload;
    private SecurityCommandResponseResource response;
    private RestResponse restResponse;


    public GeneratePinOffsetsStepDefs(PaymentSecurityCommon paySecCommon, SoftAssertions softAssertions) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = softAssertions;
    }

    @Given("{int} PVK key(s) created on the Payment Security service")
    public void pvkKeyWasCreatedOnThePaymentSecurityService(int numberOfPvkKeys) {
        createPvkEncryptionKeys(numberOfPvkKeys);
    }

    @When("a request is made to generate a card PIN offset using the given number of keys")
    public void aRequestIsMadeToGenerateACardPINOffsetUsingTheGivenNumberOfKeys() {
        //Create empty request payload
        this.requestPayload = createEmptyGeneratePinOffsetSecurityCommand();
        //Object that points at the payload.generatePinOffsets array
        List<GeneratePinOffsetCommandRequestDto> pinOffset = requestPayload.generatePinOffsets(new ArrayList<>())
                .generatePinOffsets();

        for (String cardPinVerificationKeyId : cardPinVerificationKeyIdList) {
            String pan = "0495976110267163571";
            String pinBlock = "02305";

            //Populates the payload via the object that points at it
            pinOffset.add(new GeneratePinOffsetCommandRequestDto()
                    .pinVerificationKey(new PinVerificationKeyDto()
                            .id(cardPinVerificationKeyId))
                    .pan(pan)
                    .pinBlock(pinBlock)
                    .type(UTA));
        }
        //Make the request
        this.response = postSecurityCommandPayload(this.requestPayload, ENCRYPTION_COMMAND_GENERATE_PIN_OFFSET);

    }

    @Then("the request to the security-commands endpoint to request a Pin Offset Generation returns an http status code of {int}")
    public void theRequestToTheSecurityCommandsEndpointToRequestAPinOffsetGenerationReturnsAnHttpStatusCodeOf(
            int expectedStatus) {
        this.paySecCommon.theCallReturnsAnHttpStatusCodeOf(this.restResponse.httpCode(), expectedStatus);
    }

    @Then("the generated PIN offset response contains the expected values")
    public void theGeneratedPINOffsetResponseContainsTheExpectedValues() {

        for (int i = 0; i < response.generatePinOffsets().size(); i++) {
            softAssertions.assertThat(response.generatePinOffsets().get(i).pan()).as("Pin offset PAN")
                    .isEqualTo(requestPayload.generatePinOffsets().get(i).pan());

            softAssertions.assertThat(response.generatePinOffsets().get(i).pinBlock()).as("Pin offset Pin Block")
                    .isEqualTo(requestPayload.generatePinOffsets().get(i).pinBlock());

            softAssertions.assertThat(response.generatePinOffsets().get(i).type()).as("Pin offset Type")
                    .isEqualTo("UTA");

            softAssertions.assertThat(response.generatePinOffsets().get(i).pinVerificationKey().id())
                    .as("Pin offset Pin Verification Key ID")
                    .isEqualTo(requestPayload.generatePinOffsets().get(i).pinVerificationKey().id());

            softAssertions.assertThat(response.generatePinOffsets().get(i).pinOffset()).as("Pin offset")
                    .hasSizeBetween(4, 12);

        }

    }

    @And("the response only contains generate pin offset fields")
    public void theResponseOnlyContainsGeneratePinOffsetFields() {
        validatePinOffsetSectionIsPopulated(response);
        validateOtherSectionsInPostResponseCommandAreEmpty(response);
    }

    /**
     * Populates the cardPinVerificationKeyIdList with required number of ids from cardPinVerificationKeyIdAll list
     *
     * @param numberOfPvkIds The number of ids required
     */
    protected void createPvkEncryptionKeys(int numberOfPvkIds) {
        for (int i = 0; i < numberOfPvkIds; i++) {
            this.cardPinVerificationKeyIdList.add(cardPinVerificationKeyIdAll.get(i));
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
        softAssertions.assertThat(response.generatePins()).as("generate_pins array").isEmpty();
        softAssertions.assertThat(response.generateCvcs()).as("generate_cvcs array").isEmpty();
        softAssertions.assertThat(response.generateArpcs()).as("generate_arpcs array").isEmpty();
        softAssertions.assertThat(response.verifyArqcs()).as("verify_arqcs array").isEmpty();
        softAssertions.assertThat(response.encryptPins()).as("encrypt_pins array").isEmpty();
        softAssertions.assertThat(response.generateCvcs().size()).as("generate_cvcs array size")
                .isEqualTo(requestPayload.generatePinOffsets().size());

    }

    private void validatePinOffsetSectionIsPopulated(SecurityCommandResponseResource response) {
        //This is the one section that should be populated
        softAssertions.assertThat(response.generatePinOffsets()).as("generate_pin_offsets array").isNotEmpty();
    }
}
