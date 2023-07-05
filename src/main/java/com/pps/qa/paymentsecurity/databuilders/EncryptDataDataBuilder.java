package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.dto.EncryptionKeyDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.EncryptDataCommandRequestDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Helper class to build payloads for data encryption commands
 *
 * @author nick.ong
 * @version 1.2.0
 * @since 1.2.0
 */
public class EncryptDataDataBuilder {

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with a single data item for encryption.
     *
     * @param dataToEncrypt Some data to be encrypted.
     * @param keyId The unique ID of some cryptographic key.
     */
    public SecurityCommandRequestResource createEncryptDataRequest(String dataToEncrypt, String keyId) {

        EncryptDataCommandRequestDto encryptDataCommandRequest = createEncryptDataCommandRequest(dataToEncrypt, keyId);
        return new SecurityCommandRequestResource()
                .encryptData(Collections.singletonList(encryptDataCommandRequest));
    }

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with multiple data items for encryption.
     *
     * @param itemsToEncrypt A collection of a) some data to be encrypted, b) unique ID of some cryptographic key.
     */
    public SecurityCommandRequestResource createEncryptDataRequest(Map<String, String> itemsToEncrypt) {

        List<EncryptDataCommandRequestDto> data = new ArrayList<>();

        itemsToEncrypt.forEach((k, v) -> {
            EncryptDataCommandRequestDto dto = createEncryptDataCommandRequest(k, v);
            data.add(dto);
        });

        return new SecurityCommandRequestResource()
                .encryptData(data);
    }

    /**
     * Creates a {@link EncryptDataCommandRequestDto} object with the given input values.
     *
     * @param dataToEncrypt Some data to be encrypted.
     * @param keyId The unique ID of some cryptographic key.
     * @return A Payment Security {@link EncryptDataCommandRequestDto} object.
     */
    public EncryptDataCommandRequestDto createEncryptDataCommandRequest(String dataToEncrypt, String keyId) {

        return new EncryptDataCommandRequestDto()
                .encryptionKey(new EncryptionKeyDto().id(keyId))
                .data(dataToEncrypt);
    }

}
