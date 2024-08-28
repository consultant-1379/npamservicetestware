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
//import net.sf.json.JSONObject;

public class RestResponse {

    private String jsonResponse;
    
//    private JSONObject jsonResponse;

    private int respCode;

    /**
     * @return the jsonResponse
     */
    public String getJsonResponse() {
        return jsonResponse;
    }

    /**
     * @param jsonResponse the jsonResponse to set
     */
    public void setJsonResponse(String jsonResponse) {
        this.jsonResponse = jsonResponse;
    }

    /**
     * @return the respCode
     */
    public int getRespCode() {
        return respCode;
    }

    /**
     * @param respCode the respCode to set
     */
    public void setRespCode(int respCode) {
        this.respCode = respCode;
    }

}
