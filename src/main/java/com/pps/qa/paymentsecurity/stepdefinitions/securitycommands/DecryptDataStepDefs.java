package com.pps.qa.paymentsecurity.stepdefinitions.securitycommands;

import static com.pps.dsl.apisecurity.permission.Permission.Id.ENCRYPTION_COMMAND_DATA_DECRYPT;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.resources.SecurityCommandsClient;
import com.pps.dsl.rest.HttpStatusCode;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.paymentsecurity.PaymentSecurityCommon;
import com.pps.qa.paymentsecurity.databuilders.DecryptDataDataBuilder;
import com.pps.qa.paymentsecurity.datacontext.EncryptDecryptDataContext;
import com.pps.qa.paymentsecurity.datacontext.EncryptDecryptDataContext.EncryptDecryptData;
import com.pps.qa.paymentsecurity.validator.DecryptDataResponseValidator;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.HashMap;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;
import org.testng.Assert;

/**
 * Step definitions for the encrypt/decrypt data Features.
 *
 * @author nick.ong
 * @version 1.2.0
 * @since 1.2.0
 */
public class DecryptDataStepDefs {

    private final PaymentSecurityCommon paySecCommon;
    private final DecryptDataDataBuilder builder;
    private final SoftAssertions softAssertions;
    private SecurityCommandRequestResource requestPayload;
    private RestResponse restResponse;
    private EncryptDecryptDataContext testData;

    public DecryptDataStepDefs(PaymentSecurityCommon paySecCommon, DecryptDataDataBuilder builder, EncryptDecryptDataContext testData) {

        this.paySecCommon = paySecCommon;
        this.softAssertions = paySecCommon.softAssertions;
        this.builder = builder;
        this.testData = testData;
        Assert.assertFalse(testData.items.isEmpty(), "expected to have data for decryption");
    }

    @When("^a request is made to decrypt the data item$")
    public void invokeDataItemDecryption() {

        EncryptDecryptDataContext.EncryptDecryptData item = testData.items.get(0);
        requestPayload = builder.createDecryptDataRequest(item.dataEncrypted(), item.keyId());
        sendDecryptDataRequest(true);
    }

    @When("^a request is made to decrypt multiple data items$")
    public void invokeDataItemsDecryption() {

        Map<String, String> items = new HashMap<>();
        for (EncryptDecryptData testDataItem : testData.items.values()) {
            items.put(testDataItem.dataEncrypted(), testDataItem.keyId());
        }

        requestPayload = builder.createDecryptDataRequest(items);
        sendDecryptDataRequest(true);
    }

    @Then("^a successful data decryption response is received$")
    public void verifyResponseSuccess() {
        paySecCommon.theCallReturnsAnHttpStatusCodeOf(restResponse.httpCode(), HttpStatusCode.OK);
    }

    @And("^the returned decrypted data is as expected(?: and in the correct order)?$")
    public void verifyResponseData() {

        SecurityCommandResponseResource responseRes = restResponse.body(SecurityCommandResponseResource.class);
        DecryptDataResponseValidator.validateEmptyCommands(softAssertions, responseRes);
        DecryptDataResponseValidator.validateDataDecryptionCommands(softAssertions, requestPayload, responseRes, testData.items());
    }

    /**
     * Takes the request payload and builds a request to the Payment Security application with/without authorisation.
     *
     * @param includeAuth Boolean flag used to determine whether an authorisation header should be included.
     */
    private void sendDecryptDataRequest(boolean includeAuth) {
        SecurityCommandsClient clientRequest = PaymentSecurityDsl.app(paySecCommon.paymentSecurityUrl)
                .securityCommandsClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(PaymentSecurityCommon.CONTENT_TYPE_JSON)
                .with(requestPayload);

        if (includeAuth) {
            clientRequest.authorization(generateToken(ENCRYPTION_COMMAND_DATA_DECRYPT));
        }

        restResponse = clientRequest.submitPost();
    }

}
