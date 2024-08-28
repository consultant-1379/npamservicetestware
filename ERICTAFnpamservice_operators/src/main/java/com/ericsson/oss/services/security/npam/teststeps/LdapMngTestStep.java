/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.npam.teststeps;

import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.ADDED_NODES;
import static com.ericsson.oss.testware.nodesecurity.constant.AgnosticConstants.EXPECTED_MESSAGE;
import static com.ericsson.oss.testware.nodesecurity.constant.AgnosticConstants.FILE_NAME;
import static com.ericsson.oss.testware.nodesecurity.constant.AgnosticConstants.NETWORK_ELEMENT_ID;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.DataRecordImpl;
import com.ericsson.oss.services.security.npam.operators.LdapFactory;
import com.ericsson.oss.services.security.npam.util.DataSourceName;
import com.ericsson.oss.testware.enm.cli.EnmCliResponse;
import com.ericsson.oss.testware.nodesecurity.operators.RestImpl;
import com.ericsson.oss.testware.nodesecurity.steps.TestStepUtil;
import com.ericsson.oss.testware.nodesecurity.utils.JobIdUtils;
import com.ericsson.oss.testware.nodesecurity.utils.SecurityUtil;
import com.google.common.collect.Maps;

/**
 * <pre>
 * <b>Class Name</b>: AgatNetSimTestStep
 * <b>Description</b>: This class contains the test steps of the operations performed on the 'NetSim' node simulator..
 * </pre>
 */
public class LdapMngTestStep extends BaseTestStep {
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapMngTestStep.class);
    @Inject
    private TestContext context;
    @Inject
    private Provider<RestImpl> provider;

    private final String AUTHENTICATION_GET_COMMAND = "cmedit get %s LdapAuthenticationMethod";
    private final String LDAP_GET_COMMAND = "cmedit get %s Ldap";
    private final String AUTHENTICATION_SET__COMMAND = "cmedit set %s administrativeState=%s";

    private final String LDAP_SET__COMMAND = "cmedit set %s profileFilter=ERICSSON_FILTER";

    @Inject
    private LdapFactory ldapFactory;

    /**
     * <pre>
     * <b>Name</b>: authenticanGet (<u>StepIds.AUTHENTICATION_GET</u>)           <i>[public]</i>
     * <b>Description</b>: Test Case to create LdapDataSource.
     * </pre>
     *
     * @param node
     *            - NetSim DataSource
     */
    @TestStep(id = StepIds.AUTHENTICATION_GET)
    public DataRecord authenticationGet(@Input(ADDED_NODES) final DataRecord node) {
        final String nodeName = node.getFieldValue(NETWORKELEMENTID);
        LOGGER.debug("authenticationGet nodeName {}", nodeName);
        final String command = String.format(AUTHENTICATION_GET_COMMAND, nodeName);
        LOGGER.debug("authenticationGet command {}", command);
        final EnmCliResponse enmCliResponse = provider.get().sendCommand(command);
        LOGGER.debug("authenticationGet response {}", enmCliResponse.toString());
        String authenticanFdn = "";
        if (enmCliResponse.isCommandSuccessful()) {
            final Map<String, Map<String, String>> mapFdnValue = enmCliResponse.getAttributesPerFdn();
            if (!mapFdnValue.isEmpty() && mapFdnValue.values().size() != 0) {
                LOGGER.debug("authenticationGet enmCliResponse.getAttributesPerFdn() {} values() {}", mapFdnValue, mapFdnValue.values());
                final Map<String, String> map = (Map<String, String>) mapFdnValue.values().toArray()[0];
                LOGGER.debug("authenticationGet map {}", map);
                if (map != null) {
                    authenticanFdn = map.get("FDN");
                }
            }
        }
        final Map<String, Object> data = Maps.newHashMap(node.getAllFields());
        data.put(Param.AUTHENTICATIONFDN, authenticanFdn);
        final DataRecord newNode = new DataRecordImpl(data);
        return newNode;
    }

    /**
     * <pre>
     * <b>Name</b>: ldapget (<u>StepIds.LDAP_GET</u>)           <i>[public]</i>
     * <b>Description</b>: Test Case to create LdapDataSource.
     * </pre>
     *
     * @param node
     *            - NetSim DataSource
     */
    @TestStep(id = StepIds.LDAP_GET)
    public DataRecord ldapGet(@Input(ADDED_NODES) final DataRecord node) {
        final String nodeName = node.getFieldValue(NETWORKELEMENTID);
        LOGGER.debug("ldapGet nodeName {}", nodeName);
        final String command = String.format(LDAP_GET_COMMAND, nodeName);
        LOGGER.debug("ldapGet command {}", command);
        final EnmCliResponse enmCliResponse = provider.get().sendCommand(command);
        LOGGER.debug("ldapGet response {}", enmCliResponse.toString());
        String ldapFdn = "";
        if (enmCliResponse.isCommandSuccessful()) {
            final Map<String, Map<String, String>> mapFdnValue = enmCliResponse.getAttributesPerFdn();
            if (!mapFdnValue.isEmpty() && mapFdnValue.size() != 0) {
                LOGGER.debug("ldapGet enmCliResponse.getAttributesPerFdn() {} values() {}", mapFdnValue, mapFdnValue.values());
                final Map<String, String> map = (Map<String, String>) mapFdnValue.values().toArray()[0];
                LOGGER.debug("ldapGet map %s values() {}", map);
                if (map != null) {
                    ldapFdn = map.get("FDN");
                }
            }
        }
        final Map<String, Object> data = Maps.newHashMap(node.getAllFields());
        data.put(Param.LDAPFDN, ldapFdn);
        final DataRecord newNode = new DataRecordImpl(data);
        context.dataSource(DataSourceName.LDAPNODESTOADD).addRecord().setFields(newNode);
        return newNode;
    }

    /**
     * <pre>
     * <b>Name</b>: authenticationSetAdministrativeState (<u>StepIds.AUTHENTICATION_SET_ADMINISTRATIVESTATE</u>)           <i>[public]</i>
     * <b>Description</b>: Test Case to create LdapDataSource.
     * </pre>
     *
     * @param node
     *            - NetSim DataSource
     */
    @TestStep(id = StepIds.AUTHENTICATION_SET_ADMINISTRATIVESTATE)
    public void authenticationSetAdministrativeState(@Input(ADDED_NODES) final DataRecord node, @Input(Param.VALUE) final String value) {
        final String ldapFDN = node.getFieldValue(Param.AUTHENTICATIONFDN);
        LOGGER.debug("authenticationSetAdministrativeState nodeName {} value {}", ldapFDN, value);
        final String command = String.format(AUTHENTICATION_SET__COMMAND, ldapFDN, value);
        final EnmCliResponse enmCliResponse = provider.get().sendCommand(command);
        Assertions.assertThat(enmCliResponse.isCommandSuccessful())
                .as(String.format("Set Authentication failure on node: %s", ldapFDN)).isTrue();
    }

    /**
     * <pre>
     * <b>Name</b>: authenticationSetAdministrativeState (<u>StepIds.AUTHENTICATION_SET_ADMINISTRATIVESTATE</u>)           <i>[public]</i>
     * <b>Description</b>: Test Case to create LdapDataSource.
     * </pre>
     *
     * @param node
     *            - NetSim DataSource
     */
    @TestStep(id = StepIds.LDAP_SET_PROFILEFILTER)
    public void ldapSetprofileFilter(@Input(ADDED_NODES) final DataRecord node) {
        final String fdnAuthentication = node.getFieldValue(Param.AUTHENTICATIONFDN);
        final String fdnLdap = node.getFieldValue(Param.LDAPFDN);
        if (!fdnAuthentication.isEmpty() && !fdnLdap.isEmpty()) {
            LOGGER.debug("ldapSetprofileFilter Ldap FDN {}", fdnLdap);
            final String command = String.format(LDAP_SET__COMMAND, fdnLdap);
            final EnmCliResponse enmCliResponse = provider.get().sendCommand(command);
            Assertions.assertThat(enmCliResponse.isCommandSuccessful())
                    .as(String.format("Set Ldap failure on node: %s", fdnLdap)).isTrue();
        } else {
            LOGGER.debug("ldapSetprofileFilter Ldap FDN is empty");
        }
    }

    /**
     * <pre>
     * <b>Name</b>: authenticationSetAdministrativeState (<u>StepIds.AUTHENTICATION_SET_ADMINISTRATIVESTATE</u>)           <i>[public]</i>
     * <b>Description</b>: Test Case to create LdapDataSource.
     * </pre>
     *
     * @param node
     *            - NetSim DataSource
     */
    @TestStep(id = StepIds.LDAP_BASE)
    public DataRecord ldapsBase(@Input(ADDED_NODES) final DataRecord node,
            @Input(DataSource.LDAP_DATASOURCE) final DataRecord value) {
        final DataRecord newNode = TestStepUtil.mergeMap(node, value);
        final String nodeName = newNode.getFieldValue(NETWORK_ELEMENT_ID);
        final String fdnAuthentication = node.getFieldValue(Param.AUTHENTICATIONFDN);
        final String transportType = node.getFieldValue(Param.TRANSPORTTYPE);
        final String fdnLdap = node.getFieldValue(Param.LDAPFDN);
        LOGGER.info("ldapsBase node {}", nodeName);
        if (!fdnAuthentication.isEmpty() && !fdnLdap.isEmpty()) {
            LOGGER.info("ldapsBase config");
            final String filename = newNode.getFieldValue(FILE_NAME);
            LOGGER.info("ldapsBase nodeName: {} filename: {}", nodeName, filename);
            String xmlContent = SecurityUtil.readResourceFile(filename);
            LOGGER.info("ldapsBase before xmlContent: {}", xmlContent);
            final String transportTypeValue = transportType != null && "TLS".equals(transportType) ? "LDAPS" : "STARTTLS";
            xmlContent = String.format(xmlContent, nodeName, transportTypeValue).replaceAll("\r\n", "\n");
            ;
            LOGGER.info("ldapsBase after xmlContent: {}", xmlContent);
            final byte[] fileContents = xmlContent.getBytes();
            final String targetFile = String.valueOf(nodeName) + String.valueOf(filename);
            final String commandString = ldapFactory.ldapConfigure(newNode);
            final RestImpl restImpl = provider.get();
            LOGGER.info("ldapsBase sendCommandWithFile: {} {} {}", commandString, targetFile, fileContents);
            final EnmCliResponse response = restImpl.sendCommandWithFile(commandString, targetFile, fileContents);
            LOGGER.info("ldapsBase response: {}", response.toString());
            SecurityUtil.checkResponseDto(response, (String) newNode.getFieldValue(EXPECTED_MESSAGE));
            return JobIdUtils.fillJobIdDataRecord(response, (String) newNode.getFieldValue(NETWORK_ELEMENT_ID),
                    (String) newNode.getFieldValue(EXPECTED_MESSAGE));
        } else {
            LOGGER.info("ldapsBase node is empty");
            return newNode;
        }
    }

    /**
     * <pre>
     * <b>Class Name</b>: StepIds
     * <b>Description</b>: This subclass contains the test steps identifiers contained in this class.
     * </pre>
     */
    public static final class StepIds {
        public static final String AUTHENTICATION_GET = "authenticatorGet";
        public static final String LDAP_GET = "ldapGet";
        public static final String AUTHENTICATION_SET_ADMINISTRATIVESTATE = "ldapAdministrativeState";
        public static final String LDAP_SET_PROFILEFILTER = "profileFilter";

        public static final String LDAP_BASE = "ldapBase";

        private StepIds() {}
    }

    /**
     * <pre>
     * <b>Class Name</b>: StepIds
     * <b>Description</b>: This subclass contains the test steps identifiers contained in this class.
     * </pre>
     */
    public static final class Param {
        public static final String LDAPDATASOURCE = "LdapDataSource";
        public static final String AUTHENTICATIONFDN = "authenticatonFdn";

        public static final String TRANSPORTTYPE = "transportType";
        public static final String LDAPFDN = "ldapFdn";
        public static final String VALUE = "value";
        public static final String ADMSTATE_LOCKED = "LOCKED";
        public static final String ADMSTATE_UNLOCKED = "UNLOCKED";

        private Param() {}
    }

    /**
     * DataSource: constants used in testSteps.
     */
    public static final class DataSource {
        public static final String LDAP_DATASOURCE = "LdapDataSource";

        private DataSource() {}
    }
}
