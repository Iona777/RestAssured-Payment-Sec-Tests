package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.dto.ApplicationCryptogramKeyDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.VerifyArqcCommandRequestDto;
import java.util.Arrays;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Helper class that can be used to build various payloads required in the step definitions to send ARQC verification
 * commands.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.1.0
 */

@NoArgsConstructor
public class VerifyArqcDataBuilder {

    private static final String DEFAULT_PAN = "5090000000158100";
    private static final String DEFAULT_PAN_SEQUENCE_NO = "00";
    private static final String DEFAULT_SCHEME = "EMV_OPTION_A_CKD_CSK";
    private static final String DEFAULT_METHOD = "DPAS_METHOD_1";
    private static final String DEFAULT_AC_KEY_ID = "011424fa-67e5-4d99-9176-1c5242860763";
    private static final String DEFAULT_ARQC = "4982EFAFBFDDF4D0";
    private static final String DEFAULT_ATC = "002E";
    private static final String DEFAULT_CSU = "0123";
    private static final String DEFAULT_TRANSACTION_DATA = "0000000021000000000000000076208004E0000986221229000BABDDCA7900002E0105A000030000000100100000000000";
    private static final String MIN_TRANSACTION_DATA = "00";
    private static final String MAX_TRANSACTION_DATA = RandomStringUtils.randomAlphanumeric(100);

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with a single Verify Arqc command request with
     * default values
     */
    public static SecurityCommandRequestResource createSecurityCommandRequestResourceVerifyArqc() {
        return new SecurityCommandRequestResource()
                .verifyArqcs(Arrays.asList(
                        new VerifyArqcCommandRequestDto()
                                .pan(DEFAULT_PAN)
                                .panSequenceNumber(DEFAULT_PAN_SEQUENCE_NO)
                                .scheme(DEFAULT_SCHEME)
                                .applicationCryptogramKey(new ApplicationCryptogramKeyDto().id(DEFAULT_AC_KEY_ID))
                                .arqc(DEFAULT_ARQC)
                                .atc(DEFAULT_ATC)
                                .transactionData(DEFAULT_TRANSACTION_DATA)
                ));
    }

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with a single Verify Arqc command request with
     * values with minimum allowed field lengths.
     */
    public static SecurityCommandRequestResource createSecurityCommandRequestResourceVerifyArqcMinFieldLengths() {
        return new SecurityCommandRequestResource()
                .verifyArqcs(Arrays.asList(
                        new VerifyArqcCommandRequestDto()
                                .pan(DEFAULT_PAN)
                                .panSequenceNumber(DEFAULT_PAN_SEQUENCE_NO)
                                .scheme(DEFAULT_SCHEME)
                                .applicationCryptogramKey(new ApplicationCryptogramKeyDto().id(DEFAULT_AC_KEY_ID))
                                .arqc(DEFAULT_ARQC)
                                .atc(DEFAULT_ATC)
                                .transactionData(MIN_TRANSACTION_DATA)
                ));
    }

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with a single Verify Arqc command request with
     * values with maximum allowed field lengths.
     */
    public static SecurityCommandRequestResource createSecurityCommandRequestResourceVerifyArqcMaxFieldLengths() {
        return new SecurityCommandRequestResource()
                .verifyArqcs(Arrays.asList(
                        new VerifyArqcCommandRequestDto()
                                .pan(DEFAULT_PAN)
                                .panSequenceNumber(DEFAULT_PAN_SEQUENCE_NO)
                                .scheme(DEFAULT_SCHEME)
                                .applicationCryptogramKey(new ApplicationCryptogramKeyDto().id(DEFAULT_AC_KEY_ID))
                                .arqc(DEFAULT_ARQC)
                                .atc(DEFAULT_ATC)
                                .transactionData(MAX_TRANSACTION_DATA)
                ));
    }

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with a single Verify Arqc command request with
     * Arpc generation and default values
     */
    public static SecurityCommandRequestResource createSecurityCommandRequestResourceVerifyArqcWithArpcGeneration() {
        return new SecurityCommandRequestResource()
                .verifyArqcs(Arrays.asList(
                        new VerifyArqcCommandRequestDto()
                                .pan(DEFAULT_PAN)
                                .panSequenceNumber(DEFAULT_PAN_SEQUENCE_NO)
                                .scheme(DEFAULT_SCHEME)
                                .applicationCryptogramKey(new ApplicationCryptogramKeyDto().id(DEFAULT_AC_KEY_ID))
                                .arqc(DEFAULT_ARQC)
                                .atc(DEFAULT_ATC)
                                .transactionData(DEFAULT_TRANSACTION_DATA)
                                .arpcGeneration(DEFAULT_METHOD)
                                .csu(DEFAULT_CSU)
                ));
    }

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with a single Verify Arqc command request with
     * Arpc generation and default values
     */
    public static SecurityCommandRequestResource createSecurityCommandRequestResourceVerifyArqcWithArpcGenerationWithNoCsuAttribute() {
        return new SecurityCommandRequestResource()
                .verifyArqcs(Arrays.asList(
                        new VerifyArqcCommandRequestDto()
                                .pan(DEFAULT_PAN)
                                .panSequenceNumber(DEFAULT_PAN_SEQUENCE_NO)
                                .scheme(DEFAULT_SCHEME)
                                .applicationCryptogramKey(new ApplicationCryptogramKeyDto().id(DEFAULT_AC_KEY_ID))
                                .arqc(DEFAULT_ARQC)
                                .atc(DEFAULT_ATC)
                                .transactionData(DEFAULT_TRANSACTION_DATA)
                                .arpcGeneration(DEFAULT_METHOD)
                ));
    }
}
