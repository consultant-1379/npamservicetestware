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
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_ENABLE_REMOTE_MAN_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_TBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_DATA_TBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_DATA_TBAC_NEGATIVE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_DATA_TBAC_TG_ALL_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_NEGATIVE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_DISABLE_REMOTE_MANAGEMENT_TBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_ENABLE_REMOTE_MANAGEMENT_TBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestSuite;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.oss.services.security.npam.flows.NpamFunctionalTestFlows;
import com.ericsson.oss.services.security.npam.operators.HttpToolsOperatorImpl;
import com.ericsson.oss.services.security.npam.predicate.PredicateUtil;
import com.ericsson.oss.testware.security.authentication.flows.LoginLogoutRestFlows;
import com.ericsson.oss.testware.security.gim.flows.UserManagementTestFlows;

// Running in test suite
public class TbacNeAccountManagementScenario extends TafTestBase {

    private static final Logger log = LoggerFactory.getLogger(TbacNeAccountManagementScenario.class);
    private static final String TEST_DS = "testDataSource";
    private static final String TEST_DS_CSV = "data/testDataSource.csv";

    @Inject
    LoginLogoutRestFlows loginLogoutRestFlows;

    @Inject
    HttpToolsOperatorImpl httpToolsOperatorImpl;

    @Inject
    NpamFunctionalTestFlows npamFunctionalTestFlows;

    @Inject
    UserManagementTestFlows userManagementTestFlows;

    @Inject
    TestContext context;

    @BeforeClass(groups = { "Functional" })
    public void setupToEnableRemoteManagement() {
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        context.addDataSource(CREATE_ENABLE_REMOTE_MAN_JOB_DS, fromCsv(GET_RESPONSE_FOR_CREATE_JOB_ENABLE_REMOTE_MANAGEMENT_TBAC_CSV));
        context.addDataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS, fromCsv(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_CSV));
        final TestScenario scenario = scenario("Before Class to enable remote management for Ne account Scenario")
                .addFlow(npamFunctionalTestFlows.getSpecificNeJob(CREATE_ENABLE_REMOTE_MAN_JOB_DS))

                .build();
        start(scenario);
    }
    /*
     * TORF-625965_TBAC_NEAccount AC1 Login with a user with NPAM_Administrator role and execute the rest call to retrieve the NEAccounts for 2
     * nodes
     * but the user has access to only one node as in assigned TG
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountTBACWithNPAM_Administrator_Role() {
        context.addDataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS, fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_TBAC_CSV));
        final TestScenario scenario = dataDrivenScenario("Ne account TBAC NPAM_administrator role negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("28")),
                        dataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS).withFilter(PredicateUtil.npamAdministrator()))
                .addFlow(npamFunctionalTestFlows.checkResponseForNeAccountDataWithResultsNegative())
                .build();
        start(scenario);
    }

    /*
     * TORF-628605_TBAC_NEAccount_attributes AC1 Login with a user with NPAM_Administrator role and a TG=ALL and execute the rest call to retrieve
     * the
     * NEAccount specific for a node - the attributes and ipAddress are returned with password in plain-text
     */
    @Test(groups = { "RFA" })
    @TestSuite
     public void getResponseForNeAccountSpecificNeTBACWithNPAM_Administrator_RoleAndTGAll() {
         context.addDataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS, fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_DATA_TBAC_TG_ALL_CSV));
     final TestScenario scenario = dataDrivenScenario("Ne account specifc NE TBAC NPAM_administrator role and TG=ALL positive scenario")
             .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("32")),
     dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS).withFilter(PredicateUtil.npamAdministrator()))
     .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountData())
     .build();
     start(scenario);
     }

    /*
     * TORF-628605_TBAC_NEAccount_attributes AC2 Login with a user with NPAM_Administrator role and a TG=TG1 and execute the rest call to retrieve
     * the
     * NEAccount specific for a node in TG1 - the attributes and ipAddress are returned with password in plain-text
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountSpecificNeTBACWithNPAM_Administrator_RoleAndTG() {
        context.addDataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS, fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_DATA_TBAC_CSV));
        final TestScenario scenario = dataDrivenScenario("Ne account specifc NE TBAC NPAM_administrator role and TG=TG1 positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("33")),
                        dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS).withFilter(PredicateUtil.npamAdministrator()))
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountData())
                .build();
        start(scenario);
    }

    /*
     * TORF-628605_TBAC_NEAccount_attributes AC3 Login with a user with NPAM_Administrator role and TG=TG1 (BS_TBAC_ESUM_TG),
     * and NetworkElement is
     * not in TG1 and
     * execute the call to retrieve the NEAccount
     * specific for that node the error NE not exists is returned
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void getResponseForNeAccountSpecificTBAC_NPAM_Administrator_RoleAndTG_NE_notInTG() {
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        context.addDataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_NEGATIVE_DS,
                fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_DATA_TBAC_NEGATIVE_CSV));

        final TestScenario scenario = dataDrivenScenario("Ne account specific NE TBAC NPAM_administrator role and TG=TG1 negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("34")),
                        dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_NEGATIVE_DS))
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountDataNegative())
                .build();
        start(scenario);
    }

    @AfterClass(groups = { "Functional" })
    public void disableRemoteManagement() {
        context.addDataSource(CREATE_DISABLE_REMOTE_MAN_JOB_DS, fromCsv(GET_RESPONSE_FOR_CREATE_JOB_DISABLE_REMOTE_MANAGEMENT_TBAC_CSV));
        final TestScenario scenario = scenario("After class to disable remote management for Ne account Scenario")
                .addFlow(npamFunctionalTestFlows.getSpecificNeJob(CREATE_DISABLE_REMOTE_MAN_JOB_DS))
                .withExceptionHandler(ScenarioExceptionHandler.LOGONLY).build();
        start(scenario);
    }

    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }

}
