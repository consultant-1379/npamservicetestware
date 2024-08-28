/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.npam.operators;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.oss.testware.nodesecurity.operators.factory.CredentialFactory;

/**
 * LdapConfigureFactory to configure Ldap.
 */
@SuppressWarnings({ "PMD.LawOfDemeter" })
public class LdapFactory {
    static final String LDAP_CONFIGURE_COMMAND = "secadm ldap configure -xf %s";
    @Inject
    private CredentialFactory credentialFactory;

    /**
     * Create Ldap secadm command.
     *
     * @param value
     *            ldapConfigure Value
     * @return the command string
     */
    public String ldapConfigure(final DataRecord value) {
        return String.format(LDAP_CONFIGURE_COMMAND, credentialFactory.getTargetFileCmd(value));
    }
}
