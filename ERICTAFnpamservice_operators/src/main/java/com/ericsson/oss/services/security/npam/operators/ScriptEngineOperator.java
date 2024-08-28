
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

package com.ericsson.oss.services.security.npam.operators;

import java.io.File;

import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.security.npam.util.CommandResponseDTO;
import com.ericsson.oss.testware.enm.cli.EnmCliResponse;

public interface ScriptEngineOperator {

    /**
     * Run the command provided
     *
     * @param commandString
     *            Command as a string
     * @return EnmCliResponse containing the status code and the body.
     */
    EnmCliResponse runCommand(final String commandString);

    CommandResponseDTO getcommandResponseDto(String response);

    /**
     * Run command with file as input.
     *
     * @param commandString
     *            Command as a string
     * @param file
     *            file name sent in the command
     * @return EnmCliResponse containing the status code and the body.
     */
    EnmCliResponse runCommand(final String commandString, File file);

    /**
     * Execute command containing file as input.
     *
     * @param command
     *            Command as a string
     * @return String returning the response body.
     */
    String executeWithFile(Command command);
}
