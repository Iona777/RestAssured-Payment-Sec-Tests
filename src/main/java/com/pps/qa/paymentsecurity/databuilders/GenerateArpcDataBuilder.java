package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import com.pps.dsl.paymentsecurity.domain.dto.ApplicationCryptogramKeyDto;
import com.pps.dsl.paymentsecurity.domain.dto.request.GenerateArpcCommandRequestDto;
import java.util.Arrays;
import lombok.NoArgsConstructor;

/**
 * Helper class that can be used to build various payloads required in the step definitions to send ARPC generation
 * commands.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */
@NoArgsConstructor
public class GenerateArpcDataBuilder {

    private static final String DEFAULT_PAN = "5090000000158100";
    private static final String DEFAULT_PAN_SEQUENCE_NO = "00";
    private static final String DEFAULT_SCHEME = "EMV_OPTION_A_CKD_CSK";
    private static final String DEFAULT_METHOD = "DPAS_METHOD_1";
    private static final String DEFAULT_AC_KEY_ID = "011424fa-67e5-4d99-9176-1c5242860763";
    private static final String DEFAULT_ARQC = "4982EFAFBFDDF4D0";
    private static final String DEFAULT_ATC = "002E";
    private static final String DEFAULT_CSU = "0123";

    /**
     * Creates a {@link SecurityCommandRequestResource} object populated with a single Generate Arpc command request
     * with default values
     */
    public static SecurityCommandRequestResource createSecurityCommandRequestResourceGenerateArpc() {
        return new SecurityCommandRequestResource()
                .generateArpcs(Arrays.asList(
                        new GenerateArpcCommandRequestDto()
                                .pan(DEFAULT_PAN)
                                .panSequenceNumber(DEFAULT_PAN_SEQUENCE_NO)
                                .scheme(DEFAULT_SCHEME)
                                .method(DEFAULT_METHOD)
                                .applicationCryptogramKey(new ApplicationCryptogramKeyDto().id(DEFAULT_AC_KEY_ID))
                                .arqc(DEFAULT_ARQC)
                                .atc(DEFAULT_ATC)
                                .csu(DEFAULT_CSU)
                ));
    }
}
