package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.EncryptionKeyRequestResource;
import com.pps.dsl.paymentsecurity.domain.dto.EncryptedKeyValueDto;
import com.pps.dsl.paymentsecurity.domain.dto.ManagementKeyDto;
import com.pps.dsl.paymentsecurity.domain.dto.ScopeDto;
import com.pps.dsl.paymentsecurity.domain.dto.TypeDto;
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
public class MigrateKeysToKeyBlockDataBuilder {

    /**
     * Creates an {@link EncryptionKeyRequestResource} object with no additional values
     */
    public static EncryptionKeyRequestResource createEncryptionKeyNoManagementNoValue() {
        return new EncryptionKeyRequestResource()
                .code("PanKey")
                .index(1L)
                .name("Pan Encryption Key")
                .description("Pan key rotation as per CM-XXXX")
                .scope(createScope())
                .type(TypeDto.ZEK)
                .algorithm("3DES");
    }

    /**
     * Creates an {@link EncryptionKeyRequestResource} object with encryptedKeyValue set
     */
    public static EncryptionKeyRequestResource createEncryptionKeyNoManagementKeyVariant() {
        return createEncryptionKeyNoManagementNoValue()
                .encryptedKeyValue(createEncryptedKeyNoManagementKeyVariant());
    }

    /**
     * Creates an {@link EncryptionKeyRequestResource} object with encryptedKeyValue and scope set
     */
    public static EncryptionKeyRequestResource createEncryptionKeyOnlyVariantResource() {
        return createEncryptionKeyNoManagementNoValue()
                .scope(createScope().zone("ServicePlatform"))
                .encryptedKeyValue(createEncryptedKeyValueVariantOnly());
    }

    /**
     * Creates a {@link EncryptedKeyValueDto} object with the key block value but tno management field set
     */
    public static EncryptedKeyValueDto createEncryptedKeyNoManagementKeyVariantKeyblock() {
        return createEncryptedKeyValueKeyblockOnly().managementKey(null);
    }

    /**
     * Creates a {@link EncryptedKeyValueDto} object with the managementKey field populated
     */
    private static EncryptedKeyValueDto createEncryptedKeyNoValue() {
        return new EncryptedKeyValueDto()
                .managementKey(new ManagementKeyDto().id("c3be4b32-fe86-473d-9166-da1d77a4a96b"));
    }

    /**
     * Creates a {@link EncryptedKeyValueDto} o object with the managementKey, variantValue and checkValue fields set
     */
    private static EncryptedKeyValueDto createEncryptedKeyValueVariantOnly() {
        return createEncryptedKeyNoValue()
                .variantValue("TCB7460529E808315D8BA45E7E0683A10ED351895E5DF0533")
                .checkValue("62D017");
    }

    /**
     * Creates a {@link EncryptedKeyValueDto} object with the variant Value but no management key set
     */
    private static EncryptedKeyValueDto createEncryptedKeyNoManagementKeyVariant() {
        return createEncryptedKeyValueVariantOnly().managementKey(null);
    }

    /**
     * Creates a {@link EncryptedKeyValueDto} object with the key block value set
     */
    public static EncryptedKeyValueDto createEncryptedKeyValueKeyblockOnly() {
        return createEncryptedKeyNoValue()
                .keyblockValue(
                        "S0008822TB00S00FFED11918E19C4396BA57AE54940AFC76BFF2BF7E7101EAA26334A0DB6D2FEEF4C52DE372D")
                .checkValue("62D017");
    }

    /**
     * Creates a {@link EncryptedKeyValueDto} object with the key block value field populated
     */
    public static EncryptedKeyValueDto createEncryptedKeyBothValue() {
        return createEncryptedKeyValueKeyblockOnly()
                .keyblockValue(
                        "S0008822TB00S00FFED11918E19C4396BA57AE54940AFC76BFF2BF7E7101EAA26334A0DB6D2FEEF4C52DE372D");
    }

    /**
     * Creates a {@link EncryptedKeyValueDto} object with the key block value field populated, but with management Key
     * field set to null
     */
    public static EncryptedKeyValueDto createEncryptedKeyNoManagementBothValues() {
        return createEncryptedKeyBothValue().managementKey(null);
    }

    /**
     * Create a default scope object
     *
     * @return ScopeDto
     */
    private static ScopeDto createScope() {
        return new ScopeDto().zone("PACASSO")
                .group("Card Keys")
                .bin("665542");
    }

    /**
     * Returns a variant value dependant on the given keyType
     *
     * @param keyType type of key
     * @return variant value
     */
    public static String getVariantValueByKeyType(String keyType) {
        switch (keyType) {
            case "ZMK":
                return "T500314AFB9E5EDEA484DB6E3BC126D8781DB92DE7C3AF22D";
            case "ZEK":
                return "TCB7460529E808315D8BA45E7E0683A10ED351895E5DF0533";
            case "ZPK":
                return "UBCEAA506E196274EE8F0833780555060";
            case "CVK":
                return "U1F1D69D22B0382B950854984D74A5D3C";
            case "PVK":
                return "U3AD4784F23712882FAC7F0D0EE6A4FE3";
            default:
                return "Key type not set.";
        }
    }

    /**
     * Returns a variant value dependant on the given keyId
     *
     * @param keyId id of key
     * @return variant value
     */
    public static String getVariantValueByKeyId(String keyId)
    {
        switch (keyId) {
            case "c3be4b32-fe86-473d-9166-da1d77a4a96b":
                return "TCB7460529E808315D8BA45E7E0683A10ED351895E5DF0533";
            case "aaf1ee6b-216c-4d7b-aebb-45fa8685174a":
                return "T500314AFB9E5EDEA484DB6E3BC126D8781DB92DE7C3AF22D";
            default:
                return "Key with id " + keyId + " does not longer exist in the database.";
        }
    }
}
