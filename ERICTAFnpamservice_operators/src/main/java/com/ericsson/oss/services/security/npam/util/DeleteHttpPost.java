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

import org.apache.http.client.methods.HttpPost;

/**
 * The purpose of this class is to enable the test client 
 * to send DELETE rest API with request body 
 *
 */
public class DeleteHttpPost extends HttpPost {
    
    /**
     * Instantiates a new deletes request
     * 
     * @param url
     *            url of the API
     */
    public DeleteHttpPost(final String url) {
        super(url);
    }

    /* (non-Javadoc)
     * @see org.apache.http.client.methods.HttpPost#getMethod()
     */
    @Override
    public String getMethod() {
        return "DELETE";
    }
}
