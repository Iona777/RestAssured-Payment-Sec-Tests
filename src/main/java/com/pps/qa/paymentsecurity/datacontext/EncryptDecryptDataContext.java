package com.pps.qa.paymentsecurity.datacontext;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.assertj.core.api.SoftAssertions;

import static com.pps.dsl.apisecurity.util.AuthorizationUtil.resetAllApiSecurityStubMappings;

/**
 * Dependency-injected data container to transport state between step definitions
 *
 * @author nick.ong
 * @version 1.2.0
 * @since 1.2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class EncryptDecryptDataContext {

    public Map<Integer, EncryptDecryptData> items = new HashMap<>();

    public void addItemOriginal(String keyId, String dataClear) {

        EncryptDecryptDataContext.EncryptDecryptData item = new EncryptDecryptDataContext.EncryptDecryptData()
                .keyId(keyId)
                .dataClear(dataClear);
        items.put(items.size(), item);
    }

    @After
    public void tearDown() {
        items.clear();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(fluent = true)
    public static class EncryptDecryptData {
        private String keyId;
        private String dataClear;
        private String dataEncrypted;
    }
}
