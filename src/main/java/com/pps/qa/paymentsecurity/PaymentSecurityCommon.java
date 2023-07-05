package com.pps.qa.paymentsecurity;

import static com.pps.dsl.apisecurity.util.AuthorizationUtil.apiSecurityMockBaseUrl;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.audience;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.componentClientId;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.generateToken;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.issuer;
import static com.pps.dsl.apisecurity.util.AuthorizationUtil.resetAllApiSecurityStubMappings;
import static com.pps.dsl.apisecurity.util.ComponentAuthorisationUtil.API_SECURITY;
import static org.testng.Assert.assertEquals;

import com.pps.dsl.paymentsecurity.PaymentSecurityDsl;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.rest.ErrorResponse;
import com.pps.dsl.rest.RestResponse;
import com.pps.qa.environmentconfiglib.ConfigManager;
import com.pps.qa.environmentconfiglib.model.EnvironmentConfig;
import com.pps.qa.environmentconfiglib.model.Microservice;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import java.io.IOException;
import org.assertj.core.api.SoftAssertions;

/**
 * Common class that holds any common steps as well as configuration for the components used in the tests.
 *
 * @author cedmunds
 * @version 1.2.0
 * @since 1.2.0
 */
public class PaymentSecurityCommon {

    /**
     * Dummy header values to be used throughout tests.
     */
    public static final String X_REQUEST_ID = "abcd1234-abcd-1234-abcd-abcdef123456";
    public static final String X_CORRELATION_ID = "9999abcd-9999-abcd-9999-999999abcdef";
    public static final String X_TENANT_ID = "1234567890";
    public static final String CONTENT_TYPE_JSON = "application/json; charset=utf-8";

    /**
     * Framework related variables to be used throughout tests.
     */
    public final String paymentSecurityUrl;
    public SoftAssertions softAssertions;

    public PaymentSecurityCommon() throws IOException {
        final EnvironmentConfig environmentConfig = ConfigManager.loadConfig();

        paymentSecurityUrl = "http://localhost:8888";

       /* paymentSecurityUrl = environmentConfig.getMicroservices()
                .get("payment-security")
                .getComponents()
                .get("app")
                .getFullUrl();*/

        // api-security mock setup
        Microservice apiSecurityMock = environmentConfig.getMicroservices().get(API_SECURITY);
        apiSecurityMockBaseUrl(apiSecurityMock.getComponents().get("internalMock").getFullUrl());
        audience(apiSecurityMock.getProperties().get("audience"));
        issuer(apiSecurityMock.getProperties().get("issuer"));
        componentClientId("PatsyTokenClientId");
    }

    @Before
    public void setup() {
        softAssertions = new SoftAssertions();
    }

    @After
    public void tearDown() {
        resetAllApiSecurityStubMappings();
        softAssertions.assertAll();
    }

    /**
     * Sends a POST request to Payment Security application with supplied payload and permission
     *
     * @param requestPayload The payload to send to the request
     * @param permission     The permission to send to the request
     * @return RestResponse
     */
    public RestResponse postSecurityCommandPayload(SecurityCommandRequestResource requestPayload,
            String permission) {

        return PaymentSecurityDsl.app(paymentSecurityUrl)
                .securityCommandsClient()
                .xCorrelationIdHeader(PaymentSecurityCommon.X_CORRELATION_ID)
                .xRequestIdHeader(PaymentSecurityCommon.X_REQUEST_ID)
                .xTenantId(PaymentSecurityCommon.X_TENANT_ID)
                .contentType(CONTENT_TYPE_JSON)
                .authorization(generateToken(permission))
                .with(requestPayload)
                .submitPost();
    }

    /**
     * Assertions against the HTTP status code of the returned response.
     *
     * @param actualHttpStatus   The actual HTTP status returned by the Payment Security service.
     * @param expectedHttpStatus The expected HTTP status.
     */
    public void theCallReturnsAnHttpStatusCodeOf(int actualHttpStatus, int expectedHttpStatus) {
        assertEquals(actualHttpStatus, expectedHttpStatus, "HTTP Status was not as expected");
    }

    /**
     * Assertions against a returned {@link ErrorResponse} payload.
     *
     * @param actualError   The actual {@link ErrorResponse} returned by the Payment Security service.
     * @param expectedError The expected {@link ErrorResponse}.
     */
    public void theErrorMessagePayloadIsAsExpected(ErrorResponse actualError, ErrorResponse expectedError) {
        softAssertions.assertThat(actualError.message()).as("Error message").contains(expectedError.message());
        softAssertions.assertThat(actualError.code()).as("Error code").isEqualTo(expectedError.code());
    }

}
