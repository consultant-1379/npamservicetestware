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
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_ALL_JOBS_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_ALL_JOBS_RBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_ALL_JOBS_RBAC_NEGATIVE_CSV;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

public class RbacNpamJobListScenario extends TafTestBase {
    private static final Logger log = LoggerFactory.getLogger(RbacNpamJobListScenario.class);
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
     * AC1 - TORF-626594 executing a rest call to retrieve all Npam jobs https://enmapache.athtem.eei.ericsson.se/npamservice/v1/job/list
     * with a user with NPAM_Administrator role
     * the list of Npam jobs is returned with the following fields "creationTime", "endTime", "errorDetails", "iterationId","name": "",
     * "numberOfNetworkElements",
     * "progressPercentage", "result", "startTime", "state", "type"
     */
    @Test(priority = 1, groups = { "RFA" })
    @TestSuite
    public void getResponseForAllJobsWithNpamAdministrator() {
        context.addDataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS, fromCsv(GET_RESPONSE_FOR_GET_ALL_JOBS_RBAC_CSV));
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        final TestScenario scenario = dataDrivenScenario("Retrieve all NPAM jobs positive scenario with NPAM_Administrator")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("38")),
                        dataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS).withFilter(PredicateUtil.npamAdministrator()))
                .addFlow(npamFunctionalTestFlows.checkResponseForGetAllJobs())
                .build();
        start(scenario);
    }

    /*
     * AC2 - TORF-626594 executing a rest call to retrieve the list of Npam jobs
     * https://enmapache.athtem.eei.ericsson.se/npamservice/v1/job/list
     * with custom role with<neaccount_job, read> the list is correctly returned
     */
    @Test(priority = 2, groups = { "RFA" })
    @TestSuite
    public void getResponseForAllJobsWithNeAccountJobReadCustomRole() {
        context.removeDataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS);
        context.addDataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS, fromCsv(GET_RESPONSE_FOR_GET_ALL_JOBS_RBAC_CSV));
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        final TestScenario scenario =
                dataDrivenScenario("Retrieve all NPAM jobs by a user with custom role with capability <neaccount_job, read> positive scenario")
                        .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("39")),
                                dataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS).withFilter(PredicateUtil.neaccountJobReadRole()))
                        .addFlow(npamFunctionalTestFlows.checkResponseForGetAllJobs())
                        .build();
        start(scenario);
    }

    /*
     * AC3 - TORF-626594 executing a rest call to retrieve the list of Npam jobs
     * https://enmapache.athtem.eei.ericsson.se/npamservice/v1/job/list with a user with Credm_Administrator role
     * security violation is returned
     */
    @Test(priority = 3, groups = { "RFA" })
    @TestSuite
    public void getResponseForNotExistingJob() {
        context.removeDataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS);
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        context.addDataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS, fromCsv(GET_RESPONSE_FOR_GET_ALL_JOBS_RBAC_NEGATIVE_CSV));
        final TestScenario scenario = dataDrivenScenario("Retrieve all NPAM jobs by a user with wrong role negative scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("40")),
                        dataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS).withFilter(PredicateUtil.credmAdmin()))
                .addFlow(npamFunctionalTestFlows.checkResponseForGetAllJobsWithResults())
                .build();
        start(scenario);
    }

    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }
}
