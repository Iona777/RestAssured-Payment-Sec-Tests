package com.pps.qa.paymentsecurity.databuilders;

import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptedKeyBothValue;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptedKeyNoManagementBothValues;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptedKeyNoManagementKeyVariantKeyblock;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptedKeyValueKeyblockOnly;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptionKeyNoManagementNoValue;
import static com.pps.qa.paymentsecurity.databuilders.MigrateKeysToKeyBlockDataBuilder.createEncryptionKeyOnlyVariantResource;

import com.pps.dsl.paymentsecurity.domain.EncryptionKeyRequestResource;
import com.pps.dsl.paymentsecurity.domain.dto.EncryptedKeyValueDto;
import com.pps.dsl.paymentsecurity.domain.dto.ManagementKeyDto;
import lombok.NoArgsConstructor;

/**
 * Helper class that can be used to help build various payloads required in the step definitions to migrate keys to key
 * block commands.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */

@NoArgsConstructor
public class CreateGetDeleteEncryptionKeysDataBuilder {

    /**
     * Creates a {@link EncryptionKeyRequestResource} object with encryptedKeyValue, scope, key block value and
     * checkValue set
     */
    public static EncryptionKeyRequestResource createEncryptionKeyNoValueResource() {
        return createEncryptionKeyOnlyVariantResource()
                .encryptedKeyValue(createEncryptedKeyNoValue());
    }

    /**
     * Creates a {@link EncryptedKeyValueDto} object with only the management key id field set
     */
    public static EncryptedKeyValueDto createEncryptedKeyNoValue() {
        return new EncryptedKeyValueDto()
                .managementKey(new ManagementKeyDto().id("c3be4b32-fe86-473d-9166-da1d77a4a96b"));
    }

    /**
     * Returns a key block value based on the given keyType
     */
    public static String getKeyblockValueByKeyType(String keyType) {
        switch (keyType) {
            case "ZMK":
                return "S0008852TN00S000151F99F8C406D0620BB849CC1CF18D7BA7A6A7FFE21991348B85F52AC65278849E7645658";
            case "ZEK":
                return "S0008822TN00S00FFED11918E19C4396BA57AE54940AFC76BFF2BF7E7101EAA2689FD75983BAC12C303CD30BF";
            case "ZPK":
                return "S0007272TN00S0001BDA1880282106CF19F4349D623C5D419C1226E2836A8A02DD0727C05";
            case "CVK":
                return "S00072C0TC00S0001B625070F112301C95C63E2A47170AAC507AC1720E509C1A8729664B1";
            case "PVK":
                return "S00072V0TN00S0001FA32845A0E8E480B32CCDDEF8291F955FE1BDE4160DAC0575643C611";
            default:
                return "Key type not set.";
        }
    }

    /**
     * Returns a key block value based on the given keyId
     */
    public static String getKeyblockValueByKeyId(String keyId) {
        switch (keyId) {
            case "c3be4b32-fe86-473d-9166-da1d77a4a96b":
                return "S0008822TB00S00FFED11918E19C4396BA57AE54940AFC76BFF2BF7E7101EAA26334A0DB6D2FEEF4C52DE372D";
            case "aaf1ee6b-216c-4d7b-aebb-45fa8685174a":
                return "S0008822TB00E00FFC3BCA9009928673F71BE8A2BDAF6346F4A09B417E164F3C74501013B68DBE837EDFD2784";
            default:
                return "Key with id " + keyId + " does not longer exist in the database.";
        }
    }

    /**
     * Creates a {@link EncryptionKeyRequestResource} object with encryptedKeyValue, scope, keyblockValue and checkValue
     * set
     */
    public static EncryptionKeyRequestResource createEncryptionKeyOnlyKeyblockResource() {
        return createEncryptionKeyOnlyVariantResource().encryptedKeyValue(createEncryptedKeyValueKeyblockOnly());
    }

    /**
     * Creates a {@link EncryptionKeyRequestResource} object with encryptedKeyValue, key block Value and variant value
     * set
     */
    public static EncryptionKeyRequestResource createEncryptionKeyBothValueResource() {
        return createEncryptionKeyOnlyVariantResource()
                .encryptedKeyValue(createEncryptedKeyBothValue());
    }

    /**
     * Creates a {@link EncryptionKeyRequestResource} object with the managementKey field set to null
     */
    public static EncryptionKeyRequestResource createEncryptionKeyNoManagementKeyKeyblock() {
        return createEncryptionKeyNoManagementNoValue()
                .encryptedKeyValue(createEncryptedKeyNoManagementKeyVariantKeyblock());
    }

    /**
     * Creates a {@link EncryptionKeyRequestResource} object ith the key block value field populated, but with
     * management Key field set to null
     */
    public static EncryptionKeyRequestResource createEncryptionKeyNoManagementBothValues() {
        return createEncryptionKeyNoManagementNoValue()
                .encryptedKeyValue(createEncryptedKeyNoManagementBothValues());
    }
}
