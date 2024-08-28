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

package com.ericsson.oss.services.security.npam.constant;

/**
 * Set of constants.
 */
@SuppressWarnings({ "PMD.ClassNamingConventions" })
public final class ProfilesConstants {

    // "PROFILE_*" here are the allowed values of the 'taf.profiles' property
    public static final String PROFILE_MAINTRACK_GATONCLOUD = "maintrack_gatoncloud";
    public static final String PROFILE_MAINTRACK = "maintrack";
    public static final String PROFILE_LOCAL_INFO = "localinfo";
    public static final String PROFILE_TDM_INFO = "tdminfo";
    public static final String PROFILE_REMOTE = "remoteinfo";
    public static final String PROFILE_TESTARNL_INFO = "testarnlinfo";
    public static final String PROFILE_CREATE_NODES = "createnodeinfo";
    public static final String NO_PROFILE = "noprofile";
    public static final String PROFILE_REAL_NODE = "realnode";

}
