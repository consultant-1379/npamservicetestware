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

package com.ericsson.oss.services.security.npam.scenario;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromCsv;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataDrivenScenario;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_DISABLE_REMOTE_MAN_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_ENABLE_REMOTE_MAN_JOB_WITH_RES_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_RBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_RBAC_NEGATIVE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_NE_RBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_NE_RBAC_NEGATIVE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_AC2_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_AC3_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_FAKE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_FAKE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_NEGATIVE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_DISABLE_REMOTE_MANAGEMENT_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_ENABLE_REMOTE_MANAGEMENT_WITH_RES_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_SPECIFIC_NE_ACCOUNT_AC2_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_SPECIFIC_NE_ACCOUNT_AC3_DS;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestSuite;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.oss.services.security.npam.flows.NpamFunctionalTestFlows;
import com.ericsson.oss.services.security.npam.operators.HttpToolsOperatorImpl;
import com.ericsson.oss.services.security.npam.predicate.PredicateUtil;
import com.ericsson.oss.testware.security.authentication.flows.LoginLogoutRestFlows;
import com.google.common.collect.Iterables;

// Running in test suite
public class RbacNeAccountManagementScenario extends TafTestBase {

    private static final Logger log = LoggerFactory.getLogger(RbacNeAccountManagementScenario.class);
    private static final String TEST_DS = "testDataSource";
    private static final String TEST_DS_CSV = "data/testDataSource.csv";

    @Inject
    LoginLogoutRestFlows loginLogoutRestFlows;

    @Inject
    HttpToolsOperatorImpl httpToolsOperatorImpl;

    @Inject
    NpamFunctionalTestFlows npamFunctionalTestFlows;

    @Inject
    TestContext context;

    /*
     * This enables remote management on nodes as in GET_RESPONSE_ENABLE_REMOTE_MANAGEMENT_WITH_RES_CSV file
     * i.e. ${nodes.radio.node1},${nodes.radio.node4},${nodes.radio.node3} and disable on ${nodes.radio.node3}
     */
    @BeforeClass
    public void setupDataSources() {
        context.addDataSource(CREATE_ENABLE_REMOTE_MAN_JOB_WITH_RES_DS,
                fromCsv(GET_RESPONSE_ENABLE_REMOTE_MANAGEMENT_WITH_RES_CSV));

        final String enableRemoteManagemenWithResDs = Iterables.toString(context.dataSource(CREATE_ENABLE_REMOTE_MAN_JOB_WITH_RES_DS));
        log.debug("EnableRemoteManagemenWithResDs datasource: {}", enableRemoteManagemenWithResDs);

        context.addDataSource(CREATE_DISABLE_REMOTE_MAN_JOB_DS,
                fromCsv(GET_RESPONSE_DISABLE_REMOTE_MANAGEMENT_CSV));
        final String disableRemoteManagemenDs = Iterables.toString(context.dataSource(CREATE_DISABLE_REMOTE_MAN_JOB_DS));
        log.debug("DisableRemoteManagemenDs datasource: {}", disableRemoteManagemenDs);

        context.addDataSource(GET_SPECIFIC_NE_ACCOUNT_AC3_DS, fromCsv(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_AC3_CSV));
        final String specificNeAccountAC3Ds = Iterables.toString(context.dataSource(GET_SPECIFIC_NE_ACCOUNT_AC3_DS));
        log.debug("SpecificNeAccountAC3Ds datasource: {}", specificNeAccountAC3Ds);

        context.addDataSource(GET_SPECIFIC_NE_ACCOUNT_AC2_DS, fromCsv(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_AC2_CSV));
        final String specificNeAccountAC2Ds = Iterables.toString(context.dataSource(GET_SPECIFIC_NE_ACCOUNT_AC2_DS));
        log.debug("SpecificNeAccountAC2Ds datasource: {}", specificNeAccountAC2Ds);

        context.addDataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_FAKE_DS,
                fromCsv(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_FAKE_CSV));
        final String specificNeAccountFakeDs = Iterables.toString(context.dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_FAKE_DS));
        log.debug("SpecificNeAccountFakeDs datasource: {}", specificNeAccountFakeDs);

        context.addDataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_DS,
                fromCsv(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_CSV));
        final String specificNeAccountIdsStatusDs = Iterables.toString(context.dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_DS));
        log.debug("SpecificNeAccountIdsStatusDs datasource: {}", specificNeAccountIdsStatusDs);

        context.addDataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS, fromCsv(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_CSV));
        final String specificJobDs = Iterables.toString(context.dataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS));
        log.info("specificJobDs datasource: {}", specificJobDs);

        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        final String testDs = Iterables.toString(context.dataSource(TEST_DS));
        log.debug("TestDs datasource: {}", testDs);

        final TestScenario scenario = scenario("Before Class to enable remote management for Ne account Scenario")
                .addFlow(npamFunctionalTestFlows.getSpecificNeJob(CREATE_ENABLE_REMOTE_MAN_JOB_WITH_RES_DS))
                .addFlow(npamFunctionalTestFlows.getSpecificNeJob(CREATE_DISABLE_REMOTE_MAN_JOB_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-625964_Funct1 AC1 Login with a user with NPAM_Administrator role and execute the rest call to retrieve all the NEAccounts
     */
    @Test(priority = 1, groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountDataWithNPAM_Administrator_Role() {
        context.addDataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS, fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_RBAC_CSV));
        final TestScenario scenario = dataDrivenScenario("Ne account RBAC NPAM_administrator role positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("12")),
                        dataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS).withFilter(PredicateUtil.npamAdministrator()))
                .addFlow(npamFunctionalTestFlows.checkResponseForNeAccountDataWithResults())
                .build();
        start(scenario);
    }

    /*
     * TORF-625964_Funct1 AC2 Login with a user with Credm_Administrator role role and execute the rest call to retrieve all the NEAccounts
     * security violation is returned
     */
    @Test(priority = 2, groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountDataWithWrongRole() {
        context.addDataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS, fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_RBAC_NEGATIVE_CSV));
        final TestScenario scenario = dataDrivenScenario("Ne account RBAC custom role negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("13")),
                        dataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS).withFilter(PredicateUtil.credmAdmin()))
                .addFlow(npamFunctionalTestFlows.checkResponseForNeAccountWithWrongAccessRights())
                .build();
        start(scenario);
    }

    /*
     * TORF-625964_Funct1 AC3 Login with a user with custom role with <neaccount,read> capability
     */
    @Test(priority = 3, groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountDataWithCustomRole() {
        context.addDataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS, fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_RBAC_CSV));
        final TestScenario scenario = dataDrivenScenario("Ne account RBAC custom role positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("14")),
                        dataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS).withFilter(PredicateUtil.neaccountReadRole()))
                .addFlow(npamFunctionalTestFlows.checkResponseForNeAccountDataWithResults())
                .build();
        start(scenario);
    }

    /*
     * TORF-628603_Funct1_RBAC_on_REST_for_NEAccounts AC1 Login with a user with NPAM_Administrator role and execute the rest call to retrieve
     * specific NEAccount on a NE -
     * attributes and ipAddress are returned with password in plain-text
     */
    @Test(priority = 4, groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountOnSpecifiNeWithNPAM_Administrator_Role() {
        context.addDataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS, fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_NE_RBAC_CSV));
        final TestScenario scenario = dataDrivenScenario("RBAC get for specific Ne Account with NPAM_administrator role positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("29")),
                        dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS).withFilter(PredicateUtil.npamAdministrator()))
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountData())
                .build();
        start(scenario);
    }

    /*
     * TORF-628603_Funct1_RBAC_on_REST_for_NEAccounts AC2 Login with a user with custom role with only <neaccount, read>capability and execute the
     * rest call to retrieve specific NEAccount on a NE
     * -
     * attributes and ipAddress are returned, but the password is filled with "********"
     */
    @Test(priority = 5, groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountOnSpecifiNeWithCustom_Role() {
        context.addDataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS, fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_NE_RBAC_CSV));
        final TestScenario scenario = dataDrivenScenario("RBAC get for specific Ne Account with NPAM_administrator role positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("30")),
                        dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS).withFilter(PredicateUtil.neaccountReadRole()))
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountData())
                .build();
        start(scenario);
    }

    /*
     * TORF-628603_Funct1_RBAC_on_REST_for_NEAccounts AC3 Login with a user with Credm_Administrator role and execute the rest call to retrieve
     * specific NEAccount on a NE -
     * a SecurityViolation is returned
     */
    @Test(priority = 6, groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountOnSpecifiNeWithCredmAdmin_Role() {
        context.addDataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_NEGATIVE_DS,
                fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_NE_RBAC_NEGATIVE_CSV));
        final TestScenario scenario = dataDrivenScenario("RBAC get for specific Ne Account with NPAM_administrator role negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("31")),
                        dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_NEGATIVE_DS).withFilter(PredicateUtil.credmAdmin()))
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountDataNegative())
                .build();
        start(scenario);
    }

    /*
     * TORF-639561_Funct1 AC1
     */
    @Test(priority = 7, groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountValidRadioNodeAC1() {
        final TestScenario scenario = dataDrivenScenario("RBAC get for specific Ne Account valid radio node positive scenario - AC1")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("37")))
                .addFlow(npamFunctionalTestFlows
                        .checkResponseBodyAndResponseCodeForSpecificNeAccountDataWithIdAndStatus(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_FAKE_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-639561_Funct1 AC3 - This requires to disable remote management on node ${nodes.radio.node3}
     */
    @Test(priority = 7, groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountValidRadioNodeAC3() {
        final TestScenario scenario = dataDrivenScenario("RBAC get for specific Ne Account valid radio node positive scenario - AC3")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("42")))
                .addFlow(npamFunctionalTestFlows
                        .checkResponseBodyAndResponseCodeForSpecificNeAccountDataWithIdAndStatus(GET_SPECIFIC_NE_ACCOUNT_AC3_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-639561_Funct1 AC2 - This requires a radio node where remote management has never been enabled - e.g.
     * NetworkElement=${nodes.radio.node5}
     * body: {"neNames":["${nodes.radio.node5}"],"collectionNames":[],"savedSearchIds":[]}
     */
    @Test(priority = 8, groups = { "RFA" })
    @TestSuite // NOT OK - sometimes this fails - it is due to something in the node 19: this should never been used - to be checked in the csv files
    public void getResponseForNeAccountValidRadioNodeAC2() {
        final TestScenario scenario = dataDrivenScenario("RBAC get for specific Ne Account valid radio node positive scenario - AC2")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("44")))
                .addFlow(npamFunctionalTestFlows
                        .checkResponseBodyAndResponseCodeForSpecificNeAccountDataWithIdAndStatus(GET_SPECIFIC_NE_ACCOUNT_AC2_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-639561_Funct1 AC5
     */
    @Test(priority = 10, groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountInvalidStatus() {
        final TestScenario scenario = dataDrivenScenario("NE account Negative scenario with invalid status")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("41")))
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountDataWithIdAndStatus(
                        GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_DS))
                .build();
        start(scenario);
    }

    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }
}
