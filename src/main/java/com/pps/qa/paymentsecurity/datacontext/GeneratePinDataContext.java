package com.pps.qa.paymentsecurity.datacontext;

import io.cucumber.java.After;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Dependency-injected data container to transport state between step definitions
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
public class GeneratePinDataContext {

    public Map<Integer, GeneratePinData> items = new HashMap<>();

    public void addItemOriginal(String pinBlock, String pan, int pinLength) {

        GeneratePinData item = new GeneratePinData()
                .pinBlock(pinBlock)
                .pan(pan)
                .pinLength(pinLength);
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
    public static class GeneratePinData {

        private String pinBlock;
        private String pan;
        private int pinLength;
    }
}
