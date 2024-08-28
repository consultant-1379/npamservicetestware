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

import static org.hamcrest.MatcherAssert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.HttpToolBuilder;
import com.ericsson.cifwk.taf.tools.http.constants.ContentType;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class RestUtil {

    public static enum HttpMethod {
        PUT, GET, POST, POST_MULTIPART, DELETE
    }

    private static final String NOT_SUPPORTED = "` not supported.";
    private static final String MULTIPART_FORM_DATA = "multipart/form-data";
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String ACCEPT = "Accept";
    private static final int HTTP_RESPONSE_OK_CODE = 200;
    private static final Logger LOGGER = LoggerFactory.getLogger(RestUtil.class);

    private RestUtil() {}

    /**
     * Call rest api in Content-Type of application/json and parse json response
     * into list
     *
     * @param urlString
     *            : url of rest api
     * @param method
     *            : GET, POST or DELETE
     * @param body
     *            : request body
     * @return: list of elements in json response
     * @throws IOException
     */
    public static List<JsonElement> sendJsonRequest(final String urlString,
            final String method, final String body) throws IOException {
        final RestResponse jsonResp = sendJsonRequestInit(urlString, method,
                body);
        final List<JsonElement> list = parseJson(jsonResp.getJsonResponse());
        LOGGER.info(list.size() + " elements in reponse");
        return list;
    }

    public HttpTool getHttpTool() {

        final Host host = HttpToolHelper.getNMServer();
        final HttpTool httpTool = HttpToolBuilder.newBuilder(host)
                .followRedirect(false).useHttpsIfProvided(true)
                .trustSslCertificates(true).build();
        return httpTool;
    }

    /**
     * Call rest api in Content-Type of application/json
     *
     * @param urlString
     *            : url of rest api
     * @param method
     *            : GET, POST or DELETE
     * @param body
     *            : request body
     * @return: json response string
     * @throws IOException
     */
    public static RestResponse sendJsonRequestInit(final String urlString,
            final String method, final String body) throws IOException {
        HttpURLConnection conn = null;
        final RestResponse restResponse = new RestResponse();
        try {
            final URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            if (body != null) {
                final OutputStreamWriter writer = new OutputStreamWriter(
                        conn.getOutputStream());
                writer.write(body);
                writer.close();
            }
            restResponse.setRespCode(conn.getResponseCode());
            if (restResponse.getRespCode() != HttpURLConnection.HTTP_OK
                    && restResponse.getRespCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                final String err = readFromStream(conn.getErrorStream());
                LOGGER.error(err);
                throw new IOException(err);
            } else {
                restResponse.setJsonResponse(readFromStream(conn
                        .getInputStream()));
            }
        } catch (final IOException e) {
            LOGGER.error(e.toString());
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return restResponse;
    }

    /**
     * Call rest api in Content-Type of text/plain
     *
     * @param urlString
     *            : url of rest api
     * @param method
     *            : GET, POST or DELETE
     * @param body
     *            : request body
     * @return: json response string
     * @throws IOException
     */
    public static String sendPlainRequestInit(final String urlString,
            final String method, final String body) throws IOException {
        HttpURLConnection conn = null;
        String jsonResp = null;
        try {
            final URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "text/plain");
            if (body != null) {
                final OutputStreamWriter writer = new OutputStreamWriter(
                        conn.getOutputStream());
                writer.write(body);
                writer.close();
            }
            final int respCode = conn.getResponseCode();
            if (respCode != HttpURLConnection.HTTP_OK
                    && respCode != HttpURLConnection.HTTP_NO_CONTENT) {
                final String err = readFromStream(conn.getErrorStream());
                LOGGER.error(err);
                throw new IOException(err);
            } else {
                jsonResp = readFromStream(conn.getInputStream());
            }
        } catch (final IOException e) {
            LOGGER.error(e.toString());
            e.printStackTrace();
            throw e;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return jsonResp;
    }

    /**
     * Read inputStream into String
     *
     * @param inputStream
     * @return a string from inputStream
     */
    public static String readFromStream(final InputStream inputStream) {
        final BufferedReader br = new BufferedReader(new InputStreamReader(
                inputStream));
        final StringBuilder output = new StringBuilder();
        String chunk;
        try {
            while ((chunk = br.readLine()) != null) {
                output.append(chunk);
            }
        } catch (final IOException e) {
            LOGGER.error(e.toString());
            e.printStackTrace();
        }
        return output.toString();
    }

    /**
     * Parse json string into a list of Json element
     *
     * @param jsonString
     * @return a list of Json elements
     */
    public static List<JsonElement> parseJson(final String jsonString) {
        final JsonElement root = new JsonParser().parse(jsonString);
        final JsonArray jarray = root.getAsJsonArray();
        final List<JsonElement> list = new ArrayList<JsonElement>();
        final int size = jarray.size();
        for (int i = 0; i < size; i++) {
            list.add(jarray.get(i));
        }
        return list;
    }

    /**
     * Post delete with Apache httpComponents library
     *
     * @param deleteUrl
     *            the delete url
     * @param jsonRequest
     *            the json request
     * @return server output
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ClientProtocolException
     *             the client protocol exception
     */
    public static String postDelete(final String deleteUrl,
            final String jsonRequest) throws UnsupportedEncodingException,
            IOException, ClientProtocolException {
        final CloseableHttpClient httpclient = HttpClients.createDefault();

        final DeleteHttpPost postRequest = new DeleteHttpPost(deleteUrl);
        final StringEntity input = new StringEntity(jsonRequest);
        input.setContentType("application/json");
        postRequest.setEntity(input);

        final HttpResponse response = httpclient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Failed : HTTP does not return 200 ok : "
                    + response.getStatusLine().getStatusCode());
        }

        final BufferedReader br = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));

        final StringBuilder builder = new StringBuilder();
        String output = "";
        while ((output = br.readLine()) != null) {
            builder.append(output);
        }

        httpclient.close();

        return builder.toString();
    }

    public static String postDeleteAll(final String deleteUrl)
            throws UnsupportedEncodingException, IOException,
            ClientProtocolException {
        final CloseableHttpClient httpclient = HttpClients.createDefault();

        final DeleteHttpPost postRequest = new DeleteHttpPost(deleteUrl);
        final StringEntity input = new StringEntity("");
        input.setContentType("application/json");
        postRequest.setEntity(input);

        final HttpResponse response = httpclient.execute(postRequest);

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new IOException("Failed : HTTP does not return 200 ok : "
                    + response.getStatusLine().getStatusCode());
        }

        final BufferedReader br = new BufferedReader(new InputStreamReader(
                response.getEntity().getContent()));

        final StringBuilder builder = new StringBuilder();
        String output = "";
        while ((output = br.readLine()) != null) {
            builder.append(output);
        }

        httpclient.close();

        return builder.toString();
    }

    public static String executeRestCall(final String uri,
            final HttpMethod sHttpMethod, final String jsonData, final File fileData, final boolean local, final String username, final String pswd) {

        com.ericsson.cifwk.taf.tools.http.HttpResponse response = null;
        final Host host = HttpToolHelper.getNMServer();
        final HttpTool httpTool = HttpToolHelper.buildHttpToolWithAuthenticatedUser(host, username, pswd, local);

        switch (sHttpMethod) {
            case DELETE:
                executeDeleteRestCall(uri, jsonData);
                break;

            case GET:
                response = httpTool
                        .request()
                        .header(CredentialManagerServerTafConstants.X_TOR_USER_ID,
                                username)
                        .contentType(ContentType.APPLICATION_JSON)
                        .body(jsonData).get(uri);
                httpTool.close();
                break;

            case POST:
                response = httpTool
                        .request()
                        .header(CredentialManagerServerTafConstants.X_TOR_USER_ID,
                                username)
                        .contentType(ContentType.APPLICATION_JSON)
                        .body(jsonData).post(uri);
                httpTool.close();
                break;

            case POST_MULTIPART:
                if (jsonData == null) {
                    response = httpTool.request()
                            .header(CredentialManagerServerTafConstants.X_TOR_USER_ID,
                                    username)
                            .header(CONTENT_TYPE, MULTIPART_FORM_DATA)
                            .contentType(MULTIPART_FORM_DATA)
                            .file("File", fileData)
                            .post(uri);

                    LOGGER.debug("Response code is: {}", response.getResponseCode());
                    LOGGER.debug("Response body is: {}", response.getBody());
                    httpTool.close();
                } else {
                    LOGGER.error("- REST command not sent!! Method ` {} ` not supported.", sHttpMethod);
                    assertThat("REST command not sent!! Method `" + sHttpMethod + NOT_SUPPORTED, false);
                }
                break;

            default:
                break;

        }

        if (response == null) {
            return "ERROR";
        } else {
            return response.getBody();
        }
    }

    public static FileOutputStream executeNpamExportRestCall(final String uri,
            final String jsonData, final boolean local, final String username,
            final String pswd) {

        com.ericsson.cifwk.taf.tools.http.HttpResponse response = null;
        final Host host = HttpToolHelper.getNMServer();
        final HttpTool httpTool = HttpToolHelper.buildHttpToolWithAuthenticatedUser(host, username, pswd, local);
        boolean isTheFile = false;
        FileOutputStream downloadedFile = null;
        if (jsonData != null) {
            response = httpTool.request()
                    .header(CredentialManagerServerTafConstants.X_TOR_USER_ID,
                            username)
                    .header(ACCEPT, APPLICATION_OCTET_STREAM)
                    .header(ACCEPT, APPLICATION_JSON)
                    .contentType(APPLICATION_JSON)
                    .body(jsonData).post(uri);
            httpTool.close();
            LOGGER.info("Response code is: {}", response.getResponseCode());
            LOGGER.info("Response body for export rest is: {}", response.getBody());
            if (response.getBody().isEmpty()) {
                LOGGER.error("Response is empty");
            }
            if (response.getResponseCode().getCode() != HTTP_RESPONSE_OK_CODE) {
                LOGGER.error("Response code is not OK");
            }
            isTheFile = getHeaderValue(response.getHeaders(), "Content-Disposition") != null;
            if (isTheFile) {
                LOGGER.info("The produced file with extension .enc has been found: {}",
                        getHeaderValue(response.getHeaders(), "Content-Disposition").endsWith(".enc\""));
            }
            httpTool.close();
            try {
                downloadedFile = new FileOutputStream("ESUM_TAF_export_NeAccount");
                IOUtils.copy(response.getContent(), downloadedFile);
                return downloadedFile;
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return downloadedFile;
    }

    /**
     * @param headers
     *            map containing header name and values
     * @param headerName
     *            header name
     * @return value of specified header from headers map and null if not found.
     */
    private static String getHeaderValue(final Map<String, String> headers, final String headerName) {
        String value = null;
        for (final String header : headers.keySet()) {
            if (headerName.equalsIgnoreCase(header)) {
                value = headers.get(header);
                break;
            }
        }
        LOGGER.debug("Header value is: {}", value);
        return value;
    }

    private static String executeDeleteRestCall(final String deleteUrl,
            final String jsonRequest) {

        final Host host = HostConfigurator.getApache();
        final String username = host.getUser();
        final String password = host.getPass();
        final String hostName = host.getIp();
        final String protocol = "https";

        CloseableHttpClient httpclient = null;

        try {

            httpclient = HttpToolHelper
                    .buildApacheHttpClientWithAuthenticatedUser(host, username,
                            password);

            final DeleteHttpPost postRequest = new DeleteHttpPost(protocol
                    + "://" + hostName + deleteUrl);

            final StringEntity input = new StringEntity(jsonRequest);
            input.setContentType("application/json");
            postRequest.setEntity(input);

            final HttpResponse response = httpclient.execute(postRequest);

            final BufferedReader br = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));

            final StringBuilder builder = new StringBuilder();
            String output = "";
            while ((output = br.readLine()) != null) {
                builder.append(output);
            }

            httpclient.close();

            return builder.toString();

        } catch (final IOException ex) {
            LOGGER.error("invalid request", ex);
            throw new IllegalArgumentException(ex);
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (final IOException ex) {
                    LOGGER.error("invalid request", ex);
                    throw new IllegalArgumentException(ex);
                }
            }
        }
    }

}
