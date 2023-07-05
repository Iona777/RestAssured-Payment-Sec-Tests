package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_ENCRYPT_PIN;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.ZonePinBlockFormatDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.EncryptPinCommandRequestDto;
import com.pps.dsl.paymentsecurity.domain.dto.response.EncryptPinCommandResponseDto;
import com.pps.dsl.paymentsecurity.resources.SecurityCommandsClient;
import com.pps.dsl.rest.ErrorResponse;
import com.pps.dsl.rest.HttpStatusCode;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import com.pps.qa.paymentsecurity.databuilders.EncryptPinDataBuilder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.assertj.core.api.SoftAssertions;

/**
 * Holds the step definitions for the tests defined in encrypt_card_pin.feature.
 *
 * @author cedmunds
 * @version 1.2.0
 * @since 1.2.0
 */
public class EncryptPinStepDefs {

    /**
     * Constants for these tests
     */
    protected static final String ALTERNATIVE_ZPK_ID_1 = "67c17efb-c88a-4b4e-96d7-ba59931ce79a";
    protected static final String NON_VALID_ZPK_ID = "INVALID-" + ALTERNATIVE_ZPK_ID_1;

    /**
     * Framework related variables
     */
    private final PaymentSecurityCommon paySecCommon;
    private final SoftAssertions softAssertions;
    private final EncryptPinDataBuilder builder;

    /**
     * Request/Response values for these tests
     */
    private SecurityCommandRequestResource requestPayload;
    private RestResponse restResponse;

    public EncryptPinStepDefs(PaymentSecurityCommon paySecCommon, EncryptPinDataBuilder builder) {
        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
        this.builder = builder;
    }

    @Given("^a request is made to encrypt multiple card PINs$")
    public void aRequestIsMadeToEncryptMultipleCardPINs() {
        requestPayload = builder.createEncryptCardPinSecurityCommandWithMultiplePins();
        sendEncryptCardPinRequest(true);
    }

    @Given("^a request is made to encrypt a card PIN '(.*)'$")
    public void encryptCardPin(String requestType) {
        String zpk = ALTERNATIVE_ZPK_ID_1;
        String zonePinBlockFormat = ZonePinBlockFormatDto.ISO_ANSI_FORMAT_0.name();
        boolean includePan = true;

        switch (requestType) {
            case "with specific ZPK and default pin block format":
                break;
            case "with specific ZPK and alternative pin block format":
                zonePinBlockFormat = ZonePinBlockFormatDto.ISO_FORMAT_1.name();
                break;
            case "with specific ZPK only":
                zonePinBlockFormat = null;
                break;
            case "but the card pan is missing":
                includePan = false;
                break;
            case "with a non-existent ZPK":
                zpk = NON_VALID_ZPK_ID;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + requestType);
        }

        requestPayload = builder.createEncryptCardPinRequest(zpk, zonePinBlockFormat, includePan);
        sendEncryptCardPinRequest(true);
    }

    @Given("^a valid request is made to encrypt a card PIN that has no authorisation details$")
    public void encryptCardPinWithNoAuthorisationDetails() {
        requestPayload = builder.createEncryptCardPinRequest(ALTERNATIVE_ZPK_ID_1,
                ZonePinBlockFormatDto.ISO_ANSI_FORMAT_0.name(), false);
        sendEncryptCardPinRequest(false);
    }

    @Then("^a successful 'Ok' response is received with '.*'$")
    public void verifyOkResponseWithValues() {
        paySecCommon.theCallReturnsAnHttpStatusCodeOf(restResponse.httpCode(), HttpStatusCode.OK);
        validatePostResponseCommand(restResponse.body(SecurityCommandResponseResource.class));
    }

    @Then("^a failed response for '(.*)' is received with response code (\\d+) and error code '(.*)'$")
    public void validateFailedResponse(String responseType, int expectedResponseCode, String errorCode) {
        String errorMessage;

        switch (responseType) {
            case "missing card pan":
                errorMessage = "Pan cannot be null";
                break;
            case "non-existent ZPK":
                errorMessage = "Encryption key does not exist with id " + NON_VALID_ZPK_ID;
                break;
            case "authorisation":
                errorMessage = "Authorisation failed. The JWT token is invalid.";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + responseType);
        }

        paySecCommon.theCallReturnsAnHttpStatusCodeOf(restResponse.httpCode(), expectedResponseCode);

        ErrorResponse actualError = restResponse.body(ErrorResponse.class);
        ErrorResponse expectedError = ErrorResponse.builder()
                .code(errorCode)
                .message(errorMessage)
                .build();
        paySecCommon.theErrorMessagePayloadIsAsExpected(actualError, expectedError);
    }

    /**
     * Takes the request payload and builds a request to the Payment Security application with/without authorisation.
     *
     * @param includeAuth Boolean flag used to determine whether an authorisation header should be included.
     */
    private void sendEncryptCardPinRequest(boolean includeAuth) {
        SecurityCommandsClient clientRequest = PaymentSecurityDsl.app(paySecCommon.paymentSecurityUrl)
                .securityCommandsClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType("application/json; charset=utf-8")
                .with(requestPayload);

        if (includeAuth) {
            clientRequest.authorization(generateToken(ENCRYPTION_COMMAND_ENCRYPT_PIN));
        }

        restResponse = clientRequest.submitPost();
    }

    /**
     * Used to validate the fields in a successful response.
     *
     * @param response The {@link SecurityCommandRequestResource} object returned from the Payment Security service.
     */
    private void validatePostResponseCommand(SecurityCommandResponseResource response) {
        softAssertions.assertThat(response.decryptData()).as("decrypt_data array").isEmpty();
        softAssertions.assertThat(response.translatePinToZones()).as("translate_pin_to_zones array").isEmpty();
        softAssertions.assertThat(response.decryptPins()).as("decrypt_pins array").isEmpty();
        softAssertions.assertThat(response.encryptData()).as("encrypt_data array").isEmpty();
        softAssertions.assertThat(response.generatePinOffsets()).as("generate_pin_offsets array").isEmpty();
        softAssertions.assertThat(response.generatePins()).as("generate_pins array").isEmpty();
        softAssertions.assertThat(response.generateCvcs()).as("generate_cvcs array").isEmpty();
        softAssertions.assertThat(response.generateArpcs()).as("generate_arpcs array").isEmpty();
        softAssertions.assertThat(response.verifyArqcs()).as("verify_arqcs array").isEmpty();

        softAssertions.assertThat(response.encryptPins()).as("encrypt_pins array").isNotEmpty();
        softAssertions.assertThat(response.encryptPins().size()).as("encrypt_pins array size")
                .isEqualTo(requestPayload.encryptPins().size());

        for (int i = 0; i < response.encryptPins().size(); i++) {
            EncryptPinCommandResponseDto encryptPinCmdRsp = response.encryptPins().get(i);
            EncryptPinCommandRequestDto encryptPinCmdReq = requestPayload.encryptPins().get(i);

            validateEncryptionPinCommand(encryptPinCmdRsp, encryptPinCmdReq);
        }
    }

    /**
     * Used to validate the fields of the particular encryption commands within a successful response.
     *
     * @param response The {@link EncryptPinCommandResponseDto} object from the response payload
     * @param request  The {@link EncryptPinCommandResponseDto} object from the request payload
     */
    private void validateEncryptionPinCommand(EncryptPinCommandResponseDto response,
            EncryptPinCommandRequestDto request) {
        softAssertions.assertThat(response.pin()).as("PIN no.").isEqualTo(request.pin());
        softAssertions.assertThat(response.pan()).as("PAN").isEqualTo(request.pan());
        softAssertions.assertThat(response.zonePinBlockFormat()).as("Zone Pin Block format")
                .isEqualTo(request.zonePinBlockFormat());
        softAssertions.assertThat(response.zonePinKey()).as("Zone Pin Key").isEqualTo(request.zonePinKey());

        if (request.zonePinKey() != null) {
            softAssertions.assertThat(response.zonePinKey().id()).as("Zone Pin Key ID")
                    .isEqualTo(request.zonePinKey().id());

            // A generated value by the service which we cannot mock - So just ensure it's been populated
            softAssertions.assertThat(response.zonePinBlock()).as("Zone Pin Block").isNotNull();
        }

        // A generated value by the service which we cannot mock - So just ensure it's been populated
        softAssertions.assertThat(response.pinBlock()).as("Pin Block").isNotNull();
    }

}
