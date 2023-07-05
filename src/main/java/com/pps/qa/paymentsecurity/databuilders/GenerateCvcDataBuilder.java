package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import java.util.ArrayList;
import lombok.NoArgsConstructor;

/**
 * Helper class that can be used to build various payloads required in the step definitions to send CVC generation
 * commands.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */

@NoArgsConstructor
public class GenerateCvcDataBuilder {

    /**
     * Creates an empty {@link SecurityCommandRequestResource} object with a generateCvc section
     */
    public static SecurityCommandRequestResource createEmptyGenerateCvcSecurityCommand() {
        return new SecurityCommandRequestResource()
                .generateCvcs(new ArrayList<>());
    }
}
