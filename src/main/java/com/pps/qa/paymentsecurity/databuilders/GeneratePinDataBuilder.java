package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.dto.request.GeneratePinCommandRequestDto;
import lombok.NoArgsConstructor;

/**
 * Helper class that can be used to build various payloads required in the step definitions to send pin generation
 * commands.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */

@NoArgsConstructor
public class GeneratePinDataBuilder {

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
