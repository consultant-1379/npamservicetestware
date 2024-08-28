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

import java.util.*;

/**
 * Object for ENM Common JSON Response, used for parsing JSON response into JAVA
 * objects in RestUtil
 * 
 */
public class CommonResponseForTest {
    private boolean messageExist;
    private boolean errorMessageExist;
    private boolean warningMessageExist;
    private boolean infoMessageExist;
    private boolean successMessageExist;
    private List<String> errorMessages;
    private List<String> warningMessages;
    private List<String> infoMessages;
    private List<String> successMessages;

    private List<Object> value;

    public boolean isMessageExist() {
        return messageExist;
    }

    public void setMessageExist(final boolean messageExist) {
        this.messageExist = messageExist;
    }

    public boolean isErrorMessageExist() {
        return errorMessageExist;
    }

    public void setErrorMessageExist(final boolean errorMessageExist) {
        this.errorMessageExist = errorMessageExist;
    }

    public boolean isWarningMessageExist() {
        return warningMessageExist;
    }

    public void setWarningMessageExist(final boolean warningMessageExist) {
        this.warningMessageExist = warningMessageExist;
    }

    public boolean isInfoMessageExist() {
        return infoMessageExist;
    }

    public void setInfoMessageExist(final boolean infoMessageExist) {
        this.infoMessageExist = infoMessageExist;
    }

    public boolean isSuccessMessageExist() {
        return successMessageExist;
    }

    public void setSuccessMessageExist(final boolean successMessageExist) {
        this.successMessageExist = successMessageExist;
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(final List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<String> getWarningMessages() {
        return warningMessages;
    }

    public void setWarningMessages(final List<String> warningMessages) {
        this.warningMessages = warningMessages;
    }

    public List<String> getInfoMessages() {
        return infoMessages;
    }

    public void setInfoMessages(final List<String> infoMessages) {
        this.infoMessages = infoMessages;
    }

    public List<String> getSuccessMessages() {
        return successMessages;
    }

    public void setSuccessMessages(final List<String> successMessages) {
        this.successMessages = successMessages;
    }

    public List<Object> getValue() {
        return value;
    }

    public void setValue(final List<Object> values) {
        this.value = values;
    }

}
