package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.dto.request.GeneratePinCommandRequestDto;
import java.util.ArrayList;
import lombok.NoArgsConstructor;

/**
 * Helper class that can be used to build various payloads required in the step definitions to send pin decryption
 * commands.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.1.0
 */

@NoArgsConstructor
public class DecryptPinDataBuilder {

    /**
     * Creates an empty {@link SecurityCommandRequestResource} object with a generatePins section
     */
    public static SecurityCommandRequestResource createEmptyGeneratePinSecurityCommand() {
        return new SecurityCommandRequestResource()
                .generatePins(new ArrayList<>());
    }

    /**
     * Creates an empty {@link SecurityCommandRequestResource} object with a decryptPins section
     */
    public static SecurityCommandRequestResource createEmptyDecryptCardPinSecurityCommand() {
        return new SecurityCommandRequestResource()
                .decryptPins(new ArrayList<>());
    }

    /**
     * Creates a new GeneratePinCommandRequestDto with specified pan and pin length
     *
     * @param pan       The pan to add to dto
     * @param pinLength the pin length to add to dto
     * @return GeneratePinCommandRequestDto
     */
    public static GeneratePinCommandRequestDto addRequestPayloadForGeneratePin(String pan, int pinLength) {
        return new GeneratePinCommandRequestDto()
                .pan(pan)
                .pinLength(pinLength);
    }
}
