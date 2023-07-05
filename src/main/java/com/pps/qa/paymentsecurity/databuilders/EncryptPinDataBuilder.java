package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.dto.ZonePinKeyDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.EncryptPinCommandRequestDto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Helper class that can be used to build various payloads required in the step definitions to send pin encryption
 * commands.
 *
 * @author cedmunds
 * @version 1.2.0
 * @since 1.2.0
 */
public class EncryptPinDataBuilder {

    /**
     * Used when generating random pans
     */
    private final Random random;

    public EncryptPinDataBuilder() {
        this.random = new Random();
    }

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with a single pin encryption command request.
     *
     * @param zpk                The unique ID of a cryptographic key for this zone PIN key.
     * @param zonePinBlockFormat The PIN block format for the zone.
     * @param includePan         Boolean flag to determine whether a pan should be included in the request.
     */
    public SecurityCommandRequestResource createEncryptCardPinRequest(String zpk, String zonePinBlockFormat,
            boolean includePan) {
        String pan = createRandomPan();
        String pin = createRandomPin();

        EncryptPinCommandRequestDto encryptPinCommandRequest = createEncryptPinCommandRequest(pin, zonePinBlockFormat,
                zpk);

        if (includePan) {
            encryptPinCommandRequest = createEncryptPinCommandRequest(pan, pin, zonePinBlockFormat, zpk);
        }

        return new SecurityCommandRequestResource()
                .encryptPins(Collections.singletonList(encryptPinCommandRequest));
    }

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with multiple pin encryption command requests.
     *
     * @return A Payment Security {@link SecurityCommandRequestResource} object.
     */
    public SecurityCommandRequestResource createEncryptCardPinSecurityCommandWithMultiplePins() {
        List<EncryptPinCommandRequestDto> encryptPinCommands = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String pan = createRandomPan();
            String pin = createRandomPin();

            EncryptPinCommandRequestDto encryptPinCommandRequest = createEncryptPinCommandRequest(pan, pin);
            encryptPinCommands.add(encryptPinCommandRequest);
        }

        return new SecurityCommandRequestResource()
                .encryptPins(encryptPinCommands);
    }

    /**
     * Creates a {@link EncryptPinCommandRequestDto} object with the given input values.
     *
     * @param pan The card pan value to add to the request.
     * @param pin The card PIN value to add to the request.
     * @return A Payment Security {@link EncryptPinCommandRequestDto} object.
     */
    public EncryptPinCommandRequestDto createEncryptPinCommandRequest(String pan, String pin) {
        return new EncryptPinCommandRequestDto()
                .pan(pan)
                .pin(pin);
    }

    /**
     * Creates a {@link EncryptPinCommandRequestDto} object with the given input values.
     *
     * @param pin                The card PIN value to add to the request.
     * @param zonePinBlockFormat The PIN block format for the zone.
     * @param zpk                The unique ID of a cryptographic key for this zone PIN key.
     * @return A Payment Security {@link EncryptPinCommandRequestDto} object.
     */
    public EncryptPinCommandRequestDto createEncryptPinCommandRequest(String pin, String zonePinBlockFormat,
            String zpk) {
        return new EncryptPinCommandRequestDto()
                .pin(pin)
                .zonePinBlockFormat(zonePinBlockFormat)
                .zonePinKey(new ZonePinKeyDto().id(zpk));
    }

    /**
     * Creates a {@link EncryptPinCommandRequestDto} object with the given input values.
     *
     * @param pan                The card pan value to add to the request.
     * @param pin                The card PIN value to add to the request.
     * @param zonePinBlockFormat The PIN block format for the zone.
     * @param zpk                The unique ID of a cryptographic key for this zone PIN key.
     * @return A Payment Security {@link EncryptPinCommandRequestDto} object.
     */
    public EncryptPinCommandRequestDto createEncryptPinCommandRequest(String pan, String pin, String zonePinBlockFormat,
            String zpk) {
        return new EncryptPinCommandRequestDto()
                .pan(pan)
                .pin(pin)
                .zonePinBlockFormat(zonePinBlockFormat)
                .zonePinKey(new ZonePinKeyDto().id(zpk));
    }

    /**
     * Helper method to create a randomly generated 4 digit card PIN.
     *
     * @return A randomly generated 4 digit PIN string.
     */
    public String createRandomPin() {
        return RandomStringUtils.randomNumeric(4);
    }

    /**
     * Helper method to create a randomly generated 18 digit card pan.
     *
     * @return A randomly generated 18 digit card pan string.
     */
    public String createRandomPan() {
        int minPanLength = 17;
        int maxPanLength = 19;
        int randomPan = random.nextInt(maxPanLength - minPanLength + 1) + minPanLength;
        return RandomStringUtils.randomNumeric(randomPan);
    }

}
