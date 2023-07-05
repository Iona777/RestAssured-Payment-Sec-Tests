package com.pps.qa.paymentsecurity.validator;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.SecurityCommandResponseResource;
import com.pps.dsl.paymentsecurity.domain.dto.request.DecryptDataCommandRequestDto;
import com.pps.dsl.paymentsecurity.domain.dto.response.DecryptDataCommandResponseDto;
import com.pps.qa.paymentsecurity.datacontext.EncryptDecryptDataContext.EncryptDecryptData;
import java.util.Map;
import org.assertj.core.api.SoftAssertions;

/**
 * Utility to validate response from the decrypt data feature.
 *
 * @author nick.ong
 * @version 1.2.0
 * @since 1.2.0
 */
public class DecryptDataResponseValidator {

    /**
     * Validate expected empty fields in a successful response, i.e. all except decryptData
     *
     * @param responseRes The {@link SecurityCommandResponseResource} object returned from the Payment Security service.
     */
    public static void validateEmptyCommands(SoftAssertions softAssertions, SecurityCommandResponseResource responseRes) {

        softAssertions.assertThat(responseRes.decryptPins()).as("decrypt_pins array").isEmpty();
        softAssertions.assertThat(responseRes.encryptData()).as("encrypt_data array").isEmpty();
        softAssertions.assertThat(responseRes.generateArpcs()).as("generate_arpcs array").isEmpty();
        softAssertions.assertThat(responseRes.generateCvcs()).as("generate_cvcs array").isEmpty();
        softAssertions.assertThat(responseRes.generatePinOffsets()).as("generate_pin_offsets array").isEmpty();
        softAssertions.assertThat(responseRes.generatePins()).as("generate_pins array").isEmpty();
        softAssertions.assertThat(responseRes.translatePinToZones()).as("translate_pin_to_zones array").isEmpty();
        softAssertions.assertThat(responseRes.verifyArqcs()).as("verify_arqcs array").isEmpty();
    }

    /**
     * Validate the data decryption commands within a successful response.
     *
     * @param requestRes The {@link SecurityCommandRequestResource} object returned from the Payment Security service.
     * @param responseRes The {@link SecurityCommandResponseResource} object returned from the Payment Security service.
     */
    public static void validateDataDecryptionCommands(SoftAssertions softAssertions, SecurityCommandRequestResource requestRes,
            SecurityCommandResponseResource responseRes, Map<Integer, EncryptDecryptData> originalDataItems) {

        for (int ii = 0; ii < responseRes.encryptData().size(); ii++) {
            DecryptDataCommandResponseDto ddcResponse = responseRes.decryptData().get(ii);
            DecryptDataCommandRequestDto ddcRequest = requestRes.decryptData().get(ii);

            EncryptDecryptData originalDataItem = originalDataItems.get(ii);
            validateDataDecryptionCommand(softAssertions, ddcResponse, ddcRequest, originalDataItem.dataClear());
        }
    }

    /**
     * Validate the fields of the data decryption command within a successful response.
     *
     * @param response The {@link DecryptDataCommandResponseDto} object from the response payload
     * @param request  The {@link DecryptDataCommandRequestDto} object from the request payload
     * @param originalClearData The original data in clear text
     */
    private static void validateDataDecryptionCommand(SoftAssertions softAssertions, DecryptDataCommandResponseDto response,
            DecryptDataCommandRequestDto request, String originalClearData) {

        softAssertions.assertThat(response.encryptedData()).as("encryptedData").isEqualTo(request.encryptedData());
        softAssertions.assertThat(response.encryptionKey().id()).as("encryptionKeyId").isEqualTo(request.encryptionKey().id());
        softAssertions.assertThat(response.data()).as("data").isEqualTo(originalClearData);
    }

}
