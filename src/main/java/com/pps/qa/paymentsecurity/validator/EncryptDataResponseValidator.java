package com.pps.qa.paymentsecurity.validator;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.request.EncryptDataCommandRequestDto;
import com.pps.dsl.paymentsecurity.domain.dto.response.EncryptDataCommandResponseDto;
import com.pps.dsl.paymentsecurity.domain.dto.response.EncryptPinCommandResponseDto;
import org.assertj.core.api.SoftAssertions;

/**
 * Utility to validate response from the encrypt data feature.
 *
 * @author nick.ong
 * @version 1.2.0
 * @since 1.2.0
 */
public class EncryptDataResponseValidator {

    /**
     * Validate expected empty fields in a successful response, i.e. all except encryptData
     *
     * @param responseRes The {@link SecurityCommandResponseResource} object returned from the Payment Security service.
     */
    public static void validateEmptyCommands(SoftAssertions softAssertions, SecurityCommandResponseResource responseRes) {

        softAssertions.assertThat(responseRes.decryptData()).as("decrypt_data array").isEmpty();
        softAssertions.assertThat(responseRes.decryptPins()).as("decrypt_pins array").isEmpty();
        softAssertions.assertThat(responseRes.generateArpcs()).as("generate_arpcs array").isEmpty();
        softAssertions.assertThat(responseRes.generateCvcs()).as("generate_cvcs array").isEmpty();
        softAssertions.assertThat(responseRes.generatePinOffsets()).as("generate_pin_offsets array").isEmpty();
        softAssertions.assertThat(responseRes.generatePins()).as("generate_pins array").isEmpty();
        softAssertions.assertThat(responseRes.translatePinToZones()).as("translate_pin_to_zones array").isEmpty();
        softAssertions.assertThat(responseRes.verifyArqcs()).as("verify_arqcs array").isEmpty();
    }

    /**
     * Validate the data encryption commands within a successful response.
     *
     * @param requestRes The {@link SecurityCommandRequestResource} object returned from the Payment Security service.
     * @param responseRes The {@link SecurityCommandResponseResource} object returned from the Payment Security service.
     */
    public static void validateDataEncryptionCommands(SoftAssertions softAssertions, SecurityCommandRequestResource requestRes,
            SecurityCommandResponseResource responseRes) {

        for (int ii = 0; ii < responseRes.encryptData().size(); ii++) {
            EncryptDataCommandResponseDto edcResponse = responseRes.encryptData().get(ii);
            EncryptDataCommandRequestDto edcRequest = requestRes.encryptData().get(ii);

            validateDataEncryptionCommand(softAssertions, edcResponse, edcRequest);
        }
    }

    /**
     * Validate the fields of the data encryption command within a successful response.
     *
     * @param response The {@link EncryptDataCommandResponseDto} object from the response payload
     * @param request  The {@link EncryptPinCommandResponseDto} object from the request payload
     */
    private static void validateDataEncryptionCommand(SoftAssertions softAssertions, EncryptDataCommandResponseDto response,
            EncryptDataCommandRequestDto request) {

        softAssertions.assertThat(response.data()).as("data").isEqualTo(request.data());
        softAssertions.assertThat(response.encryptionKey().id()).as("encryptionKeyId").isEqualTo(request.encryptionKey().id());

        //a generated value by the service which we cannot mock - so just ensure it's been populated
        softAssertions.assertThat(response.encryptedData()).as("encryptedData").isNotNull();
    }

}
