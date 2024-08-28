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
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_ALL_JOBS_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_ALL_JOBS_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_NOT_EXISTING_JOB_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS;

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

public class NpamJobListScenario extends TafTestBase {
    private static final Logger log = LoggerFactory.getLogger(NpamJobListScenario.class);
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

    @BeforeClass
    public void setupDataSources() {
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
    }
    /*
     * AC1 - TORF-626584 executing a rest call to retrieve all Npam jobs https://enmapache.athtem.eei.ericsson.se/npamservice/v1/job/list
     * the list of Npam jobs is returned with the following fields "creationTime", "endTime", "errorDetails", "iterationId","name": "",
     * "numberOfNetworkElements",
     * "progressPercentage", "result", "startTime", "state", "type"
     */
    @Test(priority = 1, groups = { "RFA" })
    @TestSuite
    public void getResponseForAllJobs() {
        context.addDataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS, fromCsv(GET_RESPONSE_FOR_GET_ALL_JOBS_CSV));
        final TestScenario scenario = dataDrivenScenario("Retrieve all NPAM jobs positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("35")))
                .addFlow(npamFunctionalTestFlows.checkResponseForGetAllJobs())
                .build();
        start(scenario);
    }

    /*
     * AC3 - TORF-626584 executing a rest call to retrieve a Npam job
     * https://enmapache.athtem.eei.ericsson.se/npamservice/v1/job/list/<not_existing_job>
     * Job Name <not_existing_job> does not exist
     */
    @Test(priority = 3, groups = { "RFA" })
    @TestSuite
    public void getResponseForNotExistingJob() {
        context.addDataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS, fromCsv(GET_RESPONSE_FOR_GET_NOT_EXISTING_JOB_CSV));
        final TestScenario scenario = dataDrivenScenario("Retrieve a NPAM job negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("36")))
                .addFlow(npamFunctionalTestFlows.getSpecifiJobWithExpected())
                .build();
        start(scenario);
    }

    /*
     * TORF-626584_Funct1 executing a rest call to retrieve a Npam job
     * https://enmapache.athtem.eei.ericsson.se/npamservice/v1/job/list/<jobName>
     * The job created is of type UPDATE_WITH_AUTO_GENERATED_PSWD not scheduled
     */
    @Test(priority = 4, groups = { "RFA" })
    @TestSuite
    public void getResponseForSpecificJob() {
        context.addDataSource(GET_RESPONSE_FOR_CREATE_JOB_DS, fromCsv(GET_RESPONSE_FOR_CREATE_JOB_UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_CSV));
        context.addDataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS, fromCsv(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_CSV));
        final TestScenario scenario = dataDrivenScenario("Retrieve details for a specific NPAM job")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("9")))
                .addFlow(npamFunctionalTestFlows.retrieveJobDetailsOnNameBase(GET_RESPONSE_FOR_CREATE_JOB_DS))
                .build();
        start(scenario);
    }

    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }
}
