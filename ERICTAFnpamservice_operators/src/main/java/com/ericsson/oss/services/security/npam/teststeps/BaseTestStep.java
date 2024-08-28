/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.npam.teststeps;

import com.ericsson.oss.services.scriptengine.spi.dtos.AbstractDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.LineDto;
import com.ericsson.oss.services.scriptengine.spi.dtos.RowCell;
import com.ericsson.oss.services.scriptengine.spi.dtos.RowDto;
import com.ericsson.oss.testware.enm.cli.EnmCliResponse;

/**
 * <pre>
 * <b>Class Name</b>: AgatNetSimTestStep
 * <b>Description</b>: This class contains the test steps of the operations performed on the 'NetSim' node simulator..
 * </pre>
 */
public abstract class BaseTestStep {

    protected final String NETWORKELEMENTID = "networkElementId";
    /**
     * checks if any of the AbstractDtos contain the String passed into this
     * method.
     *
     * @param entry string to search
     * @return true if @{code entry} is in response
     */
    public boolean containsString(final EnmCliResponse response, final String entry) {
        for (final AbstractDto dto : response.getAllDtos()) {
            if (dto instanceof LineDto) {
                final LineDto lineDto = (LineDto) dto;
                if (lineDto.getValue() != null && lineDto.getValue().toLowerCase().contains(entry.toLowerCase())) {
                    return true;
                }
            } else if (dto instanceof RowDto) {
                final RowDto rowDto = (RowDto) dto;
                for (final RowCell cell : rowDto.getElements()) {
                    if (cell.getValue().contains(entry)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
