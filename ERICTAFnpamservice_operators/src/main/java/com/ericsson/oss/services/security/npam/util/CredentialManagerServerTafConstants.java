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

public class CredentialManagerServerTafConstants {

    public static final String USER = "user";
    public static final String VALID_LOGIN = "0";

    public static final String X_TOR_USER_ID = "TorUserID";
    public static final String SCRIPT_ENGINE_USER_ID = "iPlanetDirectoryPro";

    public static final String CATEGORY_PRIVATE = "Private";
    public static final String CATEGORY_PUBLIC = "Public";

    public static final String APACHE_LOGIN_URI = "/login";

    public static final String KEYSTORE = "CredMClient.jks";
    public static final String TRUSTSTORE = "ENMManagementCA-chain.jks";
    public static final String KEYPASSWD = "secret";

}
