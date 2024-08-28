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
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_JOB_ENABLE_REMOTE_MANAGEMENT_NEGATIVE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_DISABLE_REMOTE_MAN_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_DISABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_ENABLE_REMOTE_MAN_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_ENABLE_REMOTE_MAN_JOB_NEGATIVE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_ENABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.ENABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_SCHED_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_DISABLE_REMOTE_MANAGEMENT_JOB_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_ENABLE_REMOTE_MANAGEMENT_JOB_RBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_UPDATE_PASSWORD_JOB_RBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_DISABLE_REMOTE_MAN_JOB_CREATE_WITH_EXPECTED_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_JOB_CONFIGURATION_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_JOB_CONFIGURATION_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_UPDATE_PSWD_JOB_CREATE_WITH_EXPECTED_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.UPDATE_PASSWORD_RBAC_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.UPDATE_PSWD_JOB_NEGATIVE_WITH_EXPECTED_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_DS;

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
public class RbacNpamJobCreationScenario extends TafTestBase {

    private static final Logger log = LoggerFactory.getLogger(RbacNpamJobCreationScenario.class);
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

    @BeforeClass(groups = {"RFA"})
    public void beforeClass() {
        context.addDataSource(CREATE_ENABLE_REMOTE_MAN_JOB_DS, fromCsv(GET_RESPONSE_FOR_CREATE_ENABLE_REMOTE_MANAGEMENT_JOB_RBAC_CSV));
        final String createEnableRemoteManagementDs = Iterables.toString(context.dataSource(CREATE_ENABLE_REMOTE_MAN_JOB_DS));
        log.debug("CreateEnableRemoteManagement dataSource: {}", createEnableRemoteManagementDs);

        context.addDataSource(UPDATE_PASSWORD_RBAC_DS, fromCsv(GET_RESPONSE_FOR_CREATE_UPDATE_PASSWORD_JOB_RBAC_CSV));
        context.addDataSource(CREATE_DISABLE_REMOTE_MAN_JOB_DS, fromCsv(GET_RESPONSE_FOR_CREATE_DISABLE_REMOTE_MANAGEMENT_JOB_CSV));
        context.addDataSource(UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_DS,
                fromCsv(GET_RESPONSE_FOR_CREATE_JOB_UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_CSV));
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        context.addDataSource(CREATE_ENABLE_REMOTE_MAN_JOB_NEGATIVE_DS,
                fromCsv(CHECK_RESP_JOB_ENABLE_REMOTE_MANAGEMENT_NEGATIVE_CSV));
        context.addDataSource(CREATE_DISABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_DS,
                fromCsv(GET_RESPONSE_FOR_DISABLE_REMOTE_MAN_JOB_CREATE_WITH_EXPECTED_CSV));
        context.addDataSource(GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_DS,
                fromCsv(GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_CSV));
        context.addDataSource(CREATE_ENABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_DS,
                fromCsv(ENABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_CSV));
        context.addDataSource(UPDATE_PSWD_JOB_NEGATIVE_WITH_EXPECTED_DS, fromCsv(GET_RESPONSE_FOR_UPDATE_PSWD_JOB_CREATE_WITH_EXPECTED_CSV));
    }

    /*
     * AC1 - Create job with a user with NPAM_Administrator role
     * AC2 - Create job with a user with custom role with<neaccount_job, create> capability
     * AC3 - Create job with a user with ADMINISTRATOR role
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void createEnableRemoteManagementPositive() {
        final TestScenario scenario = dataDrivenScenario("Create Enable Remote management Job - RBAC positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("1")))
                .addFlow(npamFunctionalTestFlows
                        .createGenericJob(CREATE_ENABLE_REMOTE_MAN_JOB_DS))
                .build();
        start(scenario);
    }

    /*
     * AC4 - Create job with a user with Credm_Administrator role
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void createEnableRemoteManagementJobNegative() {
        final TestScenario scenario = dataDrivenScenario("Create ENABLE_REMOTE_MANAGEMENT job RBAC negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("8")))
                .addFlow(npamFunctionalTestFlows
                        .createGenericJobWithExpected(CREATE_ENABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-631793_Funct1 AC3 - UPDATE_PASSWORD with exec-mode=IMMEDIATE, NE_INFO with a NetworkElement,
     * verify that the Job is corrected scheduled
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void createUpdatePswdJob() {
        final TestScenario scenario = dataDrivenScenario("Create UPDATE_PASSWORD Job scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("22")))
                .addFlow(npamFunctionalTestFlows.createGenericJob(UPDATE_PASSWORD_RBAC_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-631793_Funct1 AC2 - UPDATE_PASSWORD with exec-mode=IMMEDIATE, NE_INFO with a NetworkElement, invalid password
     * TORF-631793_Funct1 AC9 - UPDATE_PASSWORD with exec-mode=SCHEDULED, missing start date
     * TORF-631793_Funct1 AC10 - UPDATE_PASSWORD RECURRENT
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void createUpdatePswdJobNegative() {
        final TestScenario scenario = dataDrivenScenario("Create UPDATE_PASSWORD Job negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("23")))
                .addFlow(npamFunctionalTestFlows.createGenericJobWithExpected(UPDATE_PSWD_JOB_NEGATIVE_WITH_EXPECTED_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-626801_Funct1 AC1 - REMOTE_MANAGEMENT_DISABLE with exec-mode=IMMEDIATE, NE_INFO with a NetworkElement,
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void disableRemoteManagementJob() {
        final TestScenario scenario = dataDrivenScenario("DISABLE_REMOTE_MANAGEMENT Job positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("24")))
                .addFlow(npamFunctionalTestFlows.createGenericJob(CREATE_DISABLE_REMOTE_MAN_JOB_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-626801_Funct1 AC2, AC4, AC6, AC8
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void disableRemoteManagementNegativeJob() {
        final TestScenario scenario = dataDrivenScenario("DISABLE_REMOTE_MANAGEMENT Job negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("25")))
                .addFlow(
                        npamFunctionalTestFlows.createGenericJobWithExpected(CREATE_DISABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-632005_Funct1 AC1
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void generatedPswdRotationJob() {
        final TestScenario scenario = dataDrivenScenario("UPDATE_WITH_AUTO_GENERATED_PASSWORD Job positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("26")))
                .addFlow(npamFunctionalTestFlows
                        .createGenericJob(UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-632005_Funct1 AC2, AC4, AC6
     */
    @Test(groups = { "RFA" })
    @TestSuite // NOT OK
    public void generatedPswdRotationNegativeJob() {
        final TestScenario scenario = dataDrivenScenario("UPDATE_WITH_AUTO_GENERATED_PASSWORD Job negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("27")))
                .addFlow(npamFunctionalTestFlows
                        .createGenericJobWithExpected(GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-644275_Rest_For_Job_Configuration_Data executing the rest to get the configuration of a job
     * https://enmapache.athtem.eei.ericsson.se/npamservice/v1/job/configuration/<jobName>
     * scheduled with recurrent parameters using a user with custom role with<neaccount_job, read>,
     * the job configuration is returned with the correct scheduled data
     * - the user here executing the test has 2 roles including <neaccount_job, create> too in order to create a job
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void getJobConfigurationForAutoGeneratedPswdScheduledWithCustomRole() {
        context.addDataSource(GET_RESPONSE_FOR_CREATE_JOB_DS, fromCsv(GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_SCHED_CSV));
        context.addDataSource(GET_RESPONSE_FOR_JOB_CONFIGURATION_DS, fromCsv(GET_RESPONSE_FOR_JOB_CONFIGURATION_CSV));
        final TestScenario scenario = dataDrivenScenario("Retrieve UPDATE_WITH_AUTO_GENERATED_PASSWORD job configuration scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("45")))
                .addFlow(npamFunctionalTestFlows.getJobConfiguration())
                .build();
        start(scenario);
    }

    @Test(groups = { "RFA" })
    @TestSuite
    public void getSpecificNeScheduledJobWithCustomRole() {
        context.addDataSource(GET_RESPONSE_FOR_CREATE_JOB_DS,
                fromCsv(GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_SCHED_CSV));
        context.addDataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS, fromCsv(GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_SCHED_CSV));
        final TestScenario scenario =
                dataDrivenScenario("Get specific UPDATE_WITH_AUTO_GENERATED_PASSWORD Ne Job Scheduled with custom role scenario")
                        .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("46")))
                        .addFlow(npamFunctionalTestFlows.getSpecificNeJob(GET_RESPONSE_FOR_CREATE_JOB_DS))
                        .build();
        start(scenario);
    }

    @Test(groups = { "RFA" })
    @TestSuite
    public void getSpecificNeNotScheduledJobWithCustomRole() {
        context.addDataSource(GET_RESPONSE_FOR_CREATE_JOB_DS,
                fromCsv(GET_RESPONSE_FOR_CREATE_JOB_UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_CSV));
        context.addDataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS,
                fromCsv(GET_RESPONSE_FOR_CREATE_JOB_UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_CSV));
        final TestScenario scenario =
                dataDrivenScenario("Get specific UPDATE_WITH_AUTO_GENERATED_PASSWORD Ne Job No scheduled with custom role scenario")
                        .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("46")))
                        .addFlow(npamFunctionalTestFlows.getSpecificNeJob(GET_RESPONSE_FOR_CREATE_JOB_DS))
                        .build();
        start(scenario);
    }

    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }

}
