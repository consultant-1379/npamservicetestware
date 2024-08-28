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

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.oss.services.scriptengine.spi.dtos.Command;
import com.ericsson.oss.services.security.npam.util.CommandResponseDTO;
import com.ericsson.oss.services.security.npam.util.FileManagementUtility;
import com.ericsson.oss.testware.enm.cli.EnmCliOperator;
import com.ericsson.oss.testware.enm.cli.EnmCliResponse;
import com.ericsson.oss.testware.security.authentication.tool.TafToolProvider;

public class ScriptEngineOperatorImpl implements ScriptEngineOperator {

    private static Logger logger = LoggerFactory.getLogger(ScriptEngineOperatorImpl.class);

    @Inject
    TestContext context;

    @Inject
    private EnmCliOperator enmCliOperator;

    @Inject
    TafToolProvider tafToolProvider;

    @Override
    public EnmCliResponse runCommand(final String commandString) {
        logger.info("Command sent : " + commandString);
        final EnmCliResponse enmCliResponse = sendHttpRequests(commandString);
        return enmCliResponse;
    }

    @Override
    public CommandResponseDTO getcommandResponseDto(final String response) {
        final ObjectMapper mapper = new ObjectMapper();
        CommandResponseDTO commandResponseDto = null;
        try {
            commandResponseDto = mapper.readValue(response, CommandResponseDTO.class);
        } catch (final IOException e) {

            logger.info(e.getMessage());
        }
        return commandResponseDto;
    }

    private EnmCliResponse sendHttpRequests(final String commandString) {
        return enmCliOperator.executeCliCommand(commandString, tafToolProvider.getHttpTool());
    }

    @Override
    public EnmCliResponse runCommand(final String commandString, final File file) {
        return sendHttpRequestsWithFile(commandString, file);
    }

    private EnmCliResponse sendHttpRequestsWithFile(final String commandString, final File file) {

        logger.info("Absolute path of file : " + file.getAbsolutePath());
        final EnmCliResponse enmCliResponse = enmCliOperator.executeCliCommandWithFile(commandString, file, tafToolProvider.getHttpTool());
        final boolean deleted = file.delete();
        assertThat("Deletion of file failed", deleted);
        return enmCliResponse;

    }

    @Override
    public String executeWithFile(final Command command) {
        logger.info("Inside ScriptEngineRestOperator -> executeWithFile");
        final String fileName = (String) command.getProperties().get("fileName");
        final String cmd = command.getCommand();
        final File file = FileManagementUtility.getFileFromFileFinder(fileName);

        final EnmCliResponse enmCliResponse = enmCliOperator.executeCliCommandWithFile(cmd, file, tafToolProvider.getHttpTool());
        logger.info("Response of the command with file : " + enmCliResponse.getAllDtos());
        logger.info("Response of the command with file : " + enmCliResponse.getAllLineDtos());
        logger.info("Response of the command with file : " + enmCliResponse.getAllTableRows());
        logger.info("Response of the command with file : " + enmCliResponse.getCommandDto());
        logger.info("Response of the command with file : " + enmCliResponse.getSummaryDto());
        return enmCliResponse != null ? enmCliResponse.toString() : null;
    }

}
