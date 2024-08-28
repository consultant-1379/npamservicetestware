/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.npam.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.data.Ports;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.HttpToolBuilder;
import com.ericsson.cifwk.taf.tools.http.RequestBuilder;
import com.ericsson.cifwk.taf.tools.http.constants.ContentType;
import com.ericsson.cifwk.taf.tools.http.constants.HttpStatus;
import com.google.gson.Gson;

public class RestHandler {
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";
    public static final String GET = "GET";
    private static final Logger LOGGER = LoggerFactory.getLogger(RestHandler.class);

    /**
     * Call a REST API with JSON body
     *
     * @param host
     *            : the host of the REST API
     * @param path
     *            : the url of REST API with host information trimmed, such as
     *            template-manager-service/enm/1.0/templates/
     * @param method
     *            : HTTP method, such as PUT, POST, DELETE, GET
     * @param requestBody
     *            : body of the request in JSON format, use null if you don't
     *            need body
     * @return: response of the REST API call
     * @throws IOException
     */
    public static String sendJsonRequest(final Host host, final String path, final String method, final String requestBody) throws IOException {
        LOGGER.info("Calling REST API -- " + path);

        final HttpTool httpTool = HttpToolBuilder.newBuilder(host).build();

        RequestBuilder builder = httpTool.request().contentType(ContentType.APPLICATION_JSON).header("Accept", "application/json");

        if (requestBody != null) {
            builder = builder.body(requestBody);
        }

        final HttpResponse response;

        switch (method) {
            case GET:
                response = builder.get(path);
                break;
            case PUT:
                response = builder.put(path);
                break;
            case POST:
                response = builder.post(path);
                break;
            case DELETE:
                response = builder.delete(path);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method " + method);
        }

        final HttpStatus responseCode = response.getResponseCode();

        if (responseCode.equals(HttpStatus.OK) && responseCode.equals(HttpStatus.NO_CONTENT)) {
            final String err = response.getBody();
            LOGGER.error(err);
            throw new IOException(err);
        } else {
            final String responseBody = response.getBody();
            LOGGER.debug(responseBody);
            return responseBody;
        }
    }

    public static String sendJsonRequest(final HttpTool httpTool, final String path, final String method, final String requestBody)
            throws IOException {
        LOGGER.info("Calling REST API -- " + path);

        RequestBuilder builder = httpTool.request().contentType(ContentType.APPLICATION_JSON).header("Accept", "application/json");

        if (requestBody != null) {
            builder = builder.body(requestBody);
        }

        final HttpResponse response;

        switch (method) {
            case GET:
                response = builder.get(path);
                break;
            case PUT:
                response = builder.put(path);
                break;
            case POST:
                response = builder.post(path);
                break;
            case DELETE:
                response = builder.delete(path);
                break;
            default:
                throw new IllegalArgumentException("Unsupported HTTP method " + method);
        }

        final HttpStatus responseCode = response.getResponseCode();

        if (responseCode.equals(HttpStatus.OK) && responseCode.equals(HttpStatus.NO_CONTENT)) {
            final String err = response.getBody();
            LOGGER.error(err);
            throw new IOException(err);
        } else {
            final String responseBody = response.getBody();
            LOGGER.debug(responseBody);
            return responseBody;
        }
    }

    /**
     * Parse data objects from ENM common JSON response. Error & warning & info
     * & success messages are removed, only data objects will be returned
     *
     * @param jsonResponse
     * @return
     */
    public static List<Object> getValueListFromResponse(final String jsonResponse) {
        final CommonResponseForTest response = new Gson().fromJson(jsonResponse, CommonResponseForTest.class);
        return response.getValue();

    }

    /**
     * Call a REST API with DELETE method and JSON body
     *
     * @param host
     *            : the host of the REST API
     * @param path
     *            : the url of REST API with host information trimmed, such as
     *            template-manager-service/enm/1.0/templates/
     * @param requestBody
     *            : body of the request in JSON format, use null if you don't
     *            need body
     * @return: response of the REST API call
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws ClientProtocolException
     */
    public static String postDelete(final Host host, final String path, final String requestBody) throws UnsupportedEncodingException, IOException,
            ClientProtocolException {
        final String baseUrl = "http://" + host.getIp() + ":" + host.getPort().get(Ports.HTTP);
        final String deleteUrl = String.format("%s/%s", baseUrl, path);
        final CloseableHttpClient httpclient = HttpClients.createDefault();

        final DeleteHttpPost postRequest = new DeleteHttpPost(deleteUrl);
        final StringEntity input = new StringEntity(requestBody);
        input.setContentType("application/json");
        postRequest.setEntity(input);

        final CloseableHttpResponse response = httpclient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Failed : HTTP does not return 200 ok : " + response.getStatusLine().getStatusCode());
        }

        final BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        final StringBuilder builder = new StringBuilder();
        String output = "";
        while ((output = br.readLine()) != null) {
            builder.append(output);
        }

        httpclient.close();

        return builder.toString();
    }

}
