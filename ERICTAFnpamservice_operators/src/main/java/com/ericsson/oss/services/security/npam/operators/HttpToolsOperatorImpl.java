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
import java.io.FileOutputStream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.constants.ContentType;
import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;
import com.ericsson.oss.services.security.npam.util.HttpToolHelper;
import com.ericsson.oss.services.security.npam.util.RestUtil;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;

public class HttpToolsOperatorImpl implements HttpToolsOperator {

    private static final Logger logger = LoggerFactory
            .getLogger(HttpToolsOperatorImpl.class);
    private static final Host host = HostConfigurator.getApache();

    @Inject
    TestContext context;

    @Override
    public String executePostResponseBody(final String uri, final String json, final boolean local, final String username, final String pswd) {
        final String pageBody = RestUtil.executeRestCall(uri,
                RestUtil.HttpMethod.POST, json, null, local, username, pswd);
        logger.debug("The body returned from rest request is: \n {}", pageBody);
        return pageBody;
    }

    @Override
    public FileOutputStream executePostResponseForNpamExport(final String uri, final String json, final boolean local, final String username,
            final String pswd) {
        return RestUtil.executeNpamExportRestCall(uri, json, local, username, pswd);
    }

    @Override
    public String executeGetResponseBody(final String uri, final String json, final boolean local, final String username, final String pswd) {
        final String pageBody = RestUtil.executeRestCall(uri,
                RestUtil.HttpMethod.GET, json, null, local, username, pswd);
        logger.debug("The body returned from rest request is: \n {}", pageBody);
        return pageBody;
    }

    @Override
    public HttpStatus executeGetResponseCode(final String uri, final String json,
            final boolean local) {
        final HttpTool httpTool = HttpToolHelper
                .buildHttpToolWithAuthenticatedUserAndCertificate(host, local,
                        false, "", "");
        final HttpResponse response = httpTool.request()
                .contentType(ContentType.APPLICATION_JSON).body(json).get(uri);
        final HttpStatus httpStatus = response.getResponseCode();
        return httpStatus;
    }

    @Override
    public HttpStatus executePostResponseCode(final String uri, final String json,
            final boolean local) {
        final HttpTool httpTool = HttpToolHelper
                .buildHttpToolWithAuthenticatedUserAndCertificate(host, local,
                        false, "", "");
        final HttpResponse response = httpTool.request()
                .contentType(ContentType.APPLICATION_JSON).body(json).post(uri);
        final HttpStatus httpStatus = response.getResponseCode();
        return httpStatus;
    }

    @Override
    public String executeRestPutMultipartFileCall(final String uri, final File fileData, final boolean local, final String username,
            final String pswd) {
        final String pageBody = RestUtil.executeRestCall(uri,
                RestUtil.HttpMethod.POST_MULTIPART, null, fileData, local, username, pswd);
        logger.debug("The body returned from rest request is: \n {}", pageBody);
        return pageBody;
    }

}
