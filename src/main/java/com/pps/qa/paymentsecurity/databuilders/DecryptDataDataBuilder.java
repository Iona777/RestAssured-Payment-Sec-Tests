package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.dto.EncryptionKeyDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.DecryptDataCommandRequestDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.EncryptDataCommandRequestDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Helper class to build payloads for data decryption commands
 *
 * @author nick.ong
 * @version 1.2.0
 * @since 1.2.0
 */
public class DecryptDataDataBuilder {

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with a single data item for decryption.
     *
     * @param dataToDecrypt Some data to be decrypted.
     * @param keyId The unique ID of some cryptographic key.
     */
    public SecurityCommandRequestResource createDecryptDataRequest(String dataToDecrypt, String keyId) {

        DecryptDataCommandRequestDto decryptDataCommandRequest = createDecryptDataCommandRequest(dataToDecrypt, keyId);
        return new SecurityCommandRequestResource()
                .decryptData(Collections.singletonList(decryptDataCommandRequest));
    }

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with multiple data items for decryption.
     *
     * @param itemsToDecrypt A collection of a) some data to be decrypted, b) unique ID of some cryptographic key.
     */
    public SecurityCommandRequestResource createDecryptDataRequest(Map<String, String> itemsToDecrypt) {

        List<DecryptDataCommandRequestDto> data = new ArrayList<>();

        itemsToDecrypt.forEach((k, v) -> {
            DecryptDataCommandRequestDto dto = createDecryptDataCommandRequest(k, v);
            data.add(dto);
        });

        return new SecurityCommandRequestResource()
                .decryptData(data);
    }

    /**
     * Creates a {@link DecryptDataCommandRequestDto} object with the given input values.
     *
     * @param dataToDecrypt Some data to be decrypted.
     * @param keyId The unique ID of some cryptographic key.
     * @return A Payment Security {@link EncryptDataCommandRequestDto} object.
     */
    public DecryptDataCommandRequestDto createDecryptDataCommandRequest(String dataToDecrypt, String keyId) {

        return new DecryptDataCommandRequestDto()
                .encryptionKey(new EncryptionKeyDto().id(keyId))
                .encryptedData(dataToDecrypt);
    }

}
