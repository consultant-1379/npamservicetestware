/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
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
import java.io.FileOutputStream;

import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;

public interface HttpToolsOperator {

    String executePostResponseBody(String uri, String json, boolean local, String username, String pswd);

    String executeGetResponseBody(String uri, String json, boolean local, String username, String pswd);

    HttpStatus executeGetResponseCode(String uri, String json, boolean local);

    HttpStatus executePostResponseCode(String uri, String json, boolean local);

    String executeRestPutMultipartFileCall(String uri, File fileData, boolean local, String username, String pswd);

    FileOutputStream executePostResponseForNpamExport(String uri, String json, boolean local, String username, String pswd);

}
