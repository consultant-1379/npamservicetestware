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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.http.HttpResponse;
import com.ericsson.cifwk.taf.tools.http.HttpTool;
import com.ericsson.cifwk.taf.tools.http.HttpToolBuilder;
import com.ericsson.cifwk.taf.utils.FileFinder;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;

public final class HttpToolHelper {

    private static Logger log = LoggerFactory.getLogger(HttpToolHelper.class);

    public static final CloseableHttpClient buildApacheHttpClientWithAuthenticatedUser(
            final Host host, final String username, final String password) {

        KeyStore trustStore;

        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

            // Trust own CA and all self-signed certs
            final SSLContext sslcontext = SSLContexts
                    .custom()
                    .loadTrustMaterial(trustStore,
                            new TrustSelfSignedStrategy())
                    .build();
            // Allow TLSv1 protocol only
            final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[] { "TLSv1" },
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

            final CredentialsProvider credsProvider = new BasicCredentialsProvider();

            credsProvider.setCredentials(new AuthScope(host.getIp(), 443),
                    new UsernamePasswordCredentials(username, password));

            final BasicCookieStore cookieStore = new BasicCookieStore();
            final CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setDefaultCredentialsProvider(credsProvider)
                    .setDefaultCookieStore(cookieStore).build();

            final HttpUriRequest login = RequestBuilder.post()
                    .setUri(new URI("https://" + host.getIp() + "/login"))
                    .addParameter("IDToken1", username)
                    .addParameter("IDToken2", password).build();

            final CloseableHttpResponse response = httpclient.execute(login);

            final HttpEntity entity = response.getEntity();
            EntityUtils.consume(entity);

            cookieStore
                    .addCookie(new BasicClientCookie(
                            CredentialManagerServerTafConstants.X_TOR_USER_ID,
                            username));

            if (response != null) {
                response.close();
            }

            return httpclient;

        } catch (KeyStoreException | NoSuchAlgorithmException
                | KeyManagementException | URISyntaxException | IOException ex) {
            log.error("error building an http client", ex);
            throw new IllegalStateException(ex);
        }
    }

    public static final HttpTool buildHttpToolWithAuthenticatedUser(
            final Host restServer, final String username, final String password,
            final boolean local) {

        final HttpTool httpTool = HttpToolBuilder.newBuilder(restServer)
                .useHttpsIfProvided(!local).trustSslCertificates(!local)
                .followRedirect(false).build();
        final HttpResponse authenticationResponse = httpTool.request()
                .body("IDToken1", username).body("IDToken2", password)
                .post(CredentialManagerServerTafConstants.APACHE_LOGIN_URI);

        final String authErrorCode = authenticationResponse.getHeaders().get(
                "X-AuthErrorCode");

        if (local
                || CredentialManagerServerTafConstants.VALID_LOGIN
                        .equals(authErrorCode)) {

            httpTool.addCookie(
                    CredentialManagerServerTafConstants.X_TOR_USER_ID, username);
            return httpTool;
        } else {
            log.error("FAILED AUTHENTICATIO ERROR BODY\n"
                    + authenticationResponse.getBody());
            log.error("FAILED AUTHENTICATIO ERROR RESPONSE CODE\n"
                    + authenticationResponse.getResponseCode());
            log.error("AUTH ERROR CODE\n" + authErrorCode);
            throw new SecurityException("Cannot login over Apache with user["
                    + username + "]");
        }
    }

    public static final HttpTool buildHttpToolWithAuthenticatedUserAndCertificate(
            final Host restServer, final boolean local,
            final boolean isUserAuthenticated, final String username, final String password) {

        final HttpTool httpTool = buildHttpToolWithTrustAndCertificate(restServer, local);

        if (isUserAuthenticated) {
            addUserAuthentication(username, password, local, httpTool);
        }
        return httpTool;
    }

    public static final HttpTool buildHttpToolWithTrust(
            final Host restServer, final boolean local,
            final boolean isUserAuthenticated, final String username, final String password) {

        final HttpTool httpTool = buildHttpToolWithTrust(restServer, local);

        if (isUserAuthenticated) {
            addUserAuthentication(username, password, local, httpTool);
        }
        return httpTool;
    }

    private static void addUserAuthentication(final String username,
            final String password, final boolean local, final HttpTool httpTool) {
        final HttpResponse authenticationResponse = httpTool.request()
                .body("IDToken1", username).body("IDToken2", password)
                .post(CredentialManagerServerTafConstants.APACHE_LOGIN_URI);

        final String authErrorCode = authenticationResponse.getHeaders()
                .get("X-AuthErrorCode");

        if (local
                || CredentialManagerServerTafConstants.VALID_LOGIN
                        .equals(authErrorCode)) {
            httpTool.addCookie(
                    CredentialManagerServerTafConstants.X_TOR_USER_ID,
                    username);
            httpTool.addCookie(
                    CredentialManagerServerTafConstants.SCRIPT_ENGINE_USER_ID, username);

        } else {
            log.error("FAILED AUTHENTICATIO ERROR BODY\n"
                    + authenticationResponse.getBody());
            log.error("FAILED AUTHENTICATIO ERROR RESPONSE CODE\n"
                    + authenticationResponse.getResponseCode());
            log.error("AUTH ERROR CODE\n" + authErrorCode);
            throw new SecurityException(
                    "Cannot login over Apache with user[" + username + "]");
        }
    }

    private static HttpTool buildHttpToolWithTrustAndCertificate(final Host restServer, final boolean local) {

        final String keyStoreFilePath = FileFinder.findFile(CredentialManagerServerTafConstants.KEYSTORE)
                .get(0);

        final String trustStoreFilePath = FileFinder.findFile(CredentialManagerServerTafConstants.TRUSTSTORE)
                .get(0);

        log.debug("keyStoreFilePath " + keyStoreFilePath);
        log.debug("trustStoreFilePath " + trustStoreFilePath);

        final HttpTool httpTool = HttpToolBuilder
                .newBuilder(restServer)
                .useHttpsIfProvided(!local)
                .trustSslCertificates(!local)
                .setKeyStore(keyStoreFilePath,
                        CredentialManagerServerTafConstants.KEYPASSWD, null)
                .setTrustStore(trustStoreFilePath,
                        CredentialManagerServerTafConstants.KEYPASSWD)
                .followRedirect(false).build();
        return httpTool;
    }

    private static HttpTool buildHttpToolWithTrust(final Host restServer, final boolean local) {

        final String trustStoreFilePath = FileFinder.findFile(CredentialManagerServerTafConstants.TRUSTSTORE)
                .get(0);

        log.debug("trustStoreFilePath " + trustStoreFilePath);

        final HttpTool httpTool = HttpToolBuilder
                .newBuilder(restServer)
                .useHttpsIfProvided(!local)
                .trustSslCertificates(!local)
                .setTrustStore(trustStoreFilePath,
                        CredentialManagerServerTafConstants.KEYPASSWD)
                .followRedirect(false).build();
        return httpTool;
    }

    public static Host getNMServer() {
        final Host apacheHost = HostConfigurator.getApache();
        return apacheHost;
    }

}
