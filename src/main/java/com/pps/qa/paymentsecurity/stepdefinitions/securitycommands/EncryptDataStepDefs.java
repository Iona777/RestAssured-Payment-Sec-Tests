package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_DATA_ENCRYPT;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.response.EncryptDataCommandResponseDto;
import com.pps.dsl.paymentsecurity.resources.SecurityCommandsClient;
import com.pps.dsl.rest.ErrorResponse;
import com.pps.dsl.rest.HttpStatusCode;
import com.pps.dsl.rest.RestResponse;
import com.pps.dsl.util.Arguments;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import com.pps.qa.paymentsecurity.utils.SecurityKey;
import com.pps.qa.paymentsecurity.databuilders.EncryptDataDataBuilder;
import com.pps.qa.paymentsecurity.datacontext.EncryptDecryptDataContext;
import com.pps.qa.paymentsecurity.validator.EncryptDataResponseValidator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.SoftAssertions;

/**
 * Step definitions for the encrypt/decrypt data Features.
 *
 * @author nick.ong
 * @version 1.2.0
 * @since 1.2.0
 */
public class EncryptDataStepDefs {

    private static final int CARD_PAN_LENGTH = 19;
    private final PaymentSecurityCommon paySecCommon;
    private final EncryptDataDataBuilder builder;
    private final SoftAssertions softAssertions;
    private final String keyId;
    private SecurityCommandRequestResource requestPayload;
    private RestResponse restResponse;
    private EncryptDecryptDataContext testData;

    public EncryptDataStepDefs(PaymentSecurityCommon paySecCommon, EncryptDataDataBuilder builder, EncryptDecryptDataContext testData) {

        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
        this.builder = builder;
        this.testData = testData;
        keyId = SecurityKey.ZEK_KEY_ID;
    }

    @When("^a request is made to encrypt a card PAN$")
    public void invokePanEncryption() {

        String cardPan = Arguments.anyNumericString(Arguments.ofLength(CARD_PAN_LENGTH));
        requestPayload = builder.createEncryptDataRequest(cardPan, keyId);
        testData.addItemOriginal(keyId, cardPan);
        sendEncryptCardPanRequest(true);
    }

    @When("^a request is made to encrypt multiple card PANs$")
    public void invokePansEncryption() {

        Map<String, String> items = new HashMap<>();
        for (int ii = 0; ii < 2; ii++) {
            String cardPan = Arguments.anyNumericString(Arguments.ofLength(CARD_PAN_LENGTH));
            items.put(cardPan, keyId);
            testData.addItemOriginal(keyId, cardPan);
        }

        requestPayload = builder.createEncryptDataRequest(items);
        sendEncryptCardPanRequest(true);
    }

    @When("^a request is made to encrypt the maximum allowed length data$")
    public void invokeEncryptionWithMaxLengthData() {
        invokeEncryptionWithLargeData(4000);
    }

    @When("^a request is made to encrypt data exceeding the maximum allowed length$")
    public void invokeEncryptionWithDataExceedsMaxLength() {
        invokeEncryptionWithLargeData(4001);
    }

    private void invokeEncryptionWithLargeData(int length) {

        String clearData = RandomStringUtils.randomNumeric(length);
        requestPayload = builder.createEncryptDataRequest(clearData, keyId);
        testData.addItemOriginal(keyId, clearData);
        sendEncryptCardPanRequest(true);
    }

    @Then("^a successful data encryption response is received$")
    public void verifyResponseSuccess() {
        paySecCommon.theCallReturnsAnHttpStatusCodeOf(restResponse.httpCode(), HttpStatusCode.OK);
    }

    @Then("^a failed data encryption response is received as bad request$")
    public void verifyFailedSuccessBadRequest() {
        paySecCommon.theCallReturnsAnHttpStatusCodeOf(restResponse.httpCode(), HttpStatusCode.BAD_REQUEST);
    }

    @And("^the returned encrypted data is as expected(?: and in the correct order)?$")
    public void verifyResponseData() {

        SecurityCommandResponseResource responseRes = restResponse.body(SecurityCommandResponseResource.class);
        populateTestDataContextWithEncryptedData(responseRes);

        EncryptDataResponseValidator.validateEmptyCommands(softAssertions, responseRes);
        EncryptDataResponseValidator.validateDataEncryptionCommands(softAssertions, requestPayload, responseRes);
    }

    @And("^the failed data encryption response has error code (.*)$")
    public void verifyFailedResponseErrorCode(String expectedErrorCode) {

        ErrorResponse actualError = restResponse.body(ErrorResponse.class);
        softAssertions.assertThat(actualError.code()).as("errorCode").isEqualTo(expectedErrorCode);
    }

    private void populateTestDataContextWithEncryptedData(SecurityCommandResponseResource responseRes) {

        for (int ii = 0; ii < responseRes.encryptData().size(); ii++) {
            EncryptDataCommandResponseDto edcResponse = responseRes.encryptData().get(ii);
            EncryptDecryptDataContext.EncryptDecryptData item = testData.items.get(ii);
            item.dataEncrypted(edcResponse.encryptedData());
        }
    }

    /**
     * Takes the request payload and builds a request to the Payment Security application with/without authorisation.
     *
     * @param includeAuth Boolean flag used to determine whether an authorisation header should be included.
     */
    private void sendEncryptCardPanRequest(boolean includeAuth) {
        SecurityCommandsClient clientRequest = PaymentSecurityDsl.app(paySecCommon.paymentSecurityUrl)
                .securityCommandsClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .with(requestPayload);

        if (includeAuth) {
            clientRequest.authorization(generateToken(ENCRYPTION_COMMAND_DATA_ENCRYPT));
        }

        restResponse = clientRequest.submitPost();
    }

}
