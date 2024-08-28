/*
 * ------------------------------------------------------------------------------
 * ******************************************************************************
 * COPYRIGHT Ericsson 2023
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 * ******************************************************************************
 * ----------------------------------------------------------------------------
 */

package com.ericsson.oss.services.security.npam.predicate;

import java.util.Arrays;

import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.oss.services.security.npam.teststeps.LdapMngTestStep;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Predicates used as filters in flows.
 */
public class PredicateUtility extends PredicateUtil {

    public static final String AUTHENTICATION_FDN = LdapMngTestStep.Param.AUTHENTICATIONFDN;
    public static final String LDAP_FDN = LdapMngTestStep.Param.LDAPFDN;

    public static Predicate<DataRecord> netsimNodePredicate() {
        return singleValuePredicate(NODE_OPER_TYPE, "NETSIM", true);
    }

    public static Predicate<DataRecord> ldapManagedPredicate() {
        return Predicates.and(
                Predicates.not(singleValuePredicate(AUTHENTICATION_FDN, "", true)),
                Predicates.not(singleValuePredicate(LDAP_FDN, "", true)));
    }

    public static Predicate<DataRecord> cmAdm() {
        return multiValuesPredicate(ROLES, Arrays.asList("Cmedit_Administrator"));
    }

    public static Predicate<DataRecord> suiteNamePredicate(final String suiteColName, final String suiteColValue) {
        return multiValuesPredicate(suiteColName, Arrays.asList(suiteColValue), true);
    }

}
