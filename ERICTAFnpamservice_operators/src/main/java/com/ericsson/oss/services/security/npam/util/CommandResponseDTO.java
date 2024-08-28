/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.npam.util;

import com.ericsson.oss.services.scriptengine.spi.dtos.CommandResponseDto;

public class CommandResponseDTO extends CommandResponseDto {

    private static final long serialVersionUID = 1L;

    private String dtoType;

    private String dtoName;

    public String getdtoType() {
        return dtoType;
    }

    public String getdtoName() {
        return dtoName;
    }

}
