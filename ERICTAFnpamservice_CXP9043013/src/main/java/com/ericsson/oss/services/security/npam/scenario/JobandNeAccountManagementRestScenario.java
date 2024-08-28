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

package com.ericsson.oss.services.security.npam.scenario;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromCsv;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataDrivenScenario;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_LIST_FILE_AFTER_IMPORT_ADMIN_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_LIST_FILE_CHANGED_NPAM_CONFIG_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.COPY_EXPORT_FILE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.COPY_EXPORT_FILE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_ENABLE_REMOTE_MAN_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.EXPORT_NE_ACCOUNT_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.EXPORT_NE_ACCOUNT_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_FILE_CHANGED_JOB_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_IMPORT_JOB_CHANGED_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_INTEGRATED_SCENARIO_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_ROTATE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_ENABLE_REMOTE_MANAGEMENT_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_UPDATE_PSWD_CHANGED_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.UPDATE_PSWD_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.UPDATE_WITH_FILE_DS;

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
import com.ericsson.oss.services.security.npam.flows.GenericTestFlows;
import com.ericsson.oss.services.security.npam.flows.NpamFunctionalTestFlows;
import com.ericsson.oss.services.security.npam.operators.HttpToolsOperatorImpl;
import com.ericsson.oss.services.security.npam.predicate.PredicateUtil;
import com.ericsson.oss.testware.security.authentication.flows.LoginLogoutRestFlows;
import com.google.common.collect.Iterables;

public class JobandNeAccountManagementRestScenario extends TafTestBase {

    private static final Logger log = LoggerFactory.getLogger(JobandNeAccountManagementRestScenario.class);

    private static final String TEST_DS = "testDataSource";
    private static final String TEST_DS_CSV = "data/testDataSource.csv";

    @Inject
    LoginLogoutRestFlows loginLogoutRestFlows;

    @Inject
    HttpToolsOperatorImpl httpToolsOperatorImpl;

    @Inject
    NpamFunctionalTestFlows npamFunctionalTestFlows;

    @Inject
    GenericTestFlows genericTestFlows;

    @Inject
    TestContext context;

    @BeforeClass(groups = { "RFA" })
    public void beforeClass() {
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        context.addDataSource(CREATE_ENABLE_REMOTE_MAN_JOB_DS, fromCsv(GET_RESPONSE_FOR_CREATE_JOB_ENABLE_REMOTE_MANAGEMENT_CSV));
        context.addDataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS, fromCsv(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_CSV));
        context.addDataSource(UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_DS,
                fromCsv(GET_RESPONSE_FOR_CREATE_JOB_UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_CSV));
        context.addDataSource(UPDATE_PSWD_DS, fromCsv(GET_RESPONSE_FOR_CREATE_JOB_UPDATE_PSWD_CHANGED_CSV));
        context.addDataSource(UPDATE_WITH_FILE_DS, fromCsv(GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_FILE_CHANGED_JOB_CSV));
        final String updateWithfileDs = Iterables.toString(context.dataSource(UPDATE_WITH_FILE_DS));
        log.info("updateWithfileDs datasource: {}", updateWithfileDs);
        context.addDataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS, fromCsv(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_INTEGRATED_SCENARIO_CSV));
        context.addDataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS, fromCsv(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_ROTATE_CSV));
        context.addDataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS, fromCsv(GET_RESPONSE_BODY_FOR_IMPORT_JOB_CHANGED_CSV));
        context.addDataSource(CHECK_RESP_FOR_GET_LIST_FILE_AFTER_IMPORT_ADMIN_DS, fromCsv(CHECK_RESP_FOR_GET_LIST_FILE_CHANGED_NPAM_CONFIG_CSV));
        context.addDataSource(EXPORT_NE_ACCOUNT_DS, fromCsv(EXPORT_NE_ACCOUNT_CSV));
        context.addDataSource(COPY_EXPORT_FILE_DS, fromCsv(COPY_EXPORT_FILE_CSV));
    }

    @Test(priority = 1, groups = { "RFA" })
    @TestSuite
    public void integratedJobManagement() {
        // context.dataSource(AVAILABLE_USERS), PredicateUtil.npamAdministrator())
        final TestScenario scenario = dataDrivenScenario("updateNpamConfig RBAC positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("47")))
                .addFlow(loginLogoutRestFlows.loginDefaultUser())
                .addFlow(npamFunctionalTestFlows.getSpecificNeJob(CREATE_ENABLE_REMOTE_MAN_JOB_DS))
                // NeAccount creation check
                .addFlow(npamFunctionalTestFlows.checkResponseForNeAccountDataWithResults())
                // get specific NeAccount with password
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountData())
                // export specific NeAccount
                .addFlow(npamFunctionalTestFlows.exportSpecificNeAccountData())
                .addFlow(npamFunctionalTestFlows.getSpecificNeJob(UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_DS))
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountData())
                .addFlow(npamFunctionalTestFlows.getSpecificNeJob(UPDATE_PSWD_DS))
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountData())
                .addFlow(npamFunctionalTestFlows.importFile().withDataSources(dataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS)))
                .addFlow(npamFunctionalTestFlows.getListFile().withDataSources(dataSource(CHECK_RESP_FOR_GET_LIST_FILE_AFTER_IMPORT_ADMIN_DS)))
                .addFlow(npamFunctionalTestFlows.getSpecificNeJob(UPDATE_WITH_FILE_DS))
                .addFlow(npamFunctionalTestFlows.checkResponseBodyAndResponseCodeForSpecificNeAccountData())
                .build();
        start(scenario);
    }

    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }
}
