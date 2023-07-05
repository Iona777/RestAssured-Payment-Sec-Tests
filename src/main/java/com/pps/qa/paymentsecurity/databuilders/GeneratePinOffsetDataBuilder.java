package com.pps.qa.paymentsecurity.databuilders;

import com.pps.dsl.paymentsecurity.domain.SecurityCommandRequestResource;
import java.util.ArrayList;
import lombok.NoArgsConstructor;

/**
 * Helper class that can be used to build various payloads required in the step definitions to send pin offset
 * generation commands.
 *
 * @author gmacdonald
 * @version 1.2.0
 * @since 1.2.0
 */

@NoArgsConstructor
public class GeneratePinOffsetDataBuilder {

    /**
     * Creates an empty {@link SecurityCommandRequestResource} object with a generatePinOffset section
     */
    public static SecurityCommandRequestResource createEmptyGeneratePinOffsetSecurityCommand() {
        return new SecurityCommandRequestResource()
                .generatePinOffsets(new ArrayList<>());
    }

}
