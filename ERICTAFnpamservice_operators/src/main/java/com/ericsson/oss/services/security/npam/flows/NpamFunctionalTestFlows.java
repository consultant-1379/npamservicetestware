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

package com.ericsson.oss.services.security.npam.flows;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CANCEL_ENABLE_REMOTE_MANAGEMENT_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.COPY_EXPORT_FILE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.EXPORT_NE_ACCOUNT_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_NEGATIVE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_CREATE_JOB_WITH_EXPECTED_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_ALL_JOBS_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_FOR_JOB_CONFIGURATION_DS;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.scenario.TestScenarios;
import com.ericsson.cifwk.taf.scenario.api.TestStepFlowBuilder;
import com.ericsson.oss.services.security.npam.teststeps.NpamFunctionalTestSteps;

public class NpamFunctionalTestFlows {

    @Inject
    private NpamFunctionalTestSteps npamFuncTestSteps;

    /*
     * Create a generic Npam job
     */
    public TestStepFlowBuilder createGenericJob(final String testDataSource) {
        return flow("Create Npam Job flow.")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CREATE_NPAM_JOB))
                .withDataSources(dataSource(testDataSource).bindTo(GET_RESPONSE_FOR_CREATE_JOB_DS));
    }

    /*
     * Create a generic Npam job with expected return values to be used for negative tests
     */
    public TestStepFlowBuilder createGenericJobWithExpected(final String testDataSource) {
        return flow("Check response for Create Job flow with expected for negative tests")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CREATE_NPAM_JOB_WITH_EXPECTED))
                .withDataSources(dataSource(testDataSource).bindTo(GET_RESPONSE_FOR_CREATE_JOB_WITH_EXPECTED_DS));
    }

    public TestStepFlowBuilder checkResponseForCancelEnableRemoteManagementJob() {
        return flow("Check response body and status code for Cancel Job flow.")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_CANCEL_ENABLE_REMOTE_MANAGEMENT_JOB))
                .withDataSources(dataSource(CANCEL_ENABLE_REMOTE_MANAGEMENT_JOB_DS));
    }

    public TestStepFlowBuilder checkResponseForGetAllJobs() {
        return flow("Check response for Get All Jobs flow.")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CHECK_RESP_BODY_FOR_GET_ALL_JOBS))
                .withDataSources(dataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS));
    }

    public TestStepFlowBuilder checkResponseForGetAllJobsWithResults() {
        return flow("Check response for Get All Jobs flow with results.")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CHECK_RESP_BODY_FOR_GET_ALL_JOBS_WITH_RESULTS))
                .withDataSources(dataSource(GET_RESPONSE_FOR_GET_ALL_JOBS_DS));
    }

    public TestStepFlowBuilder checkResponseForNeAccountDataWithResults() {
        return flow("Check response for Get Ne Account flow with results.")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_WITH_RESULTS_POSITIVE))
                .withDataSources(dataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS));
    }

    public TestStepFlowBuilder checkResponseForNeAccountDataWithResultsNegative() {
        return flow("Check response for Get Ne Account flow with results negative.")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_WITH_RESULTS_NEGATIVE))
                .withDataSources(dataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS));
    }

    /*
     * Get the specific NeAccount data and retrieve the set password
     */
    public TestStepFlowBuilder checkResponseBodyAndResponseCodeForSpecificNeAccountData() {
        return flow("Check response for Get Ne Account for a specific Ne flow.")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_FOR_SPECIFIC_NE))
                .withDataSources(dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS));
    }

    public TestStepFlowBuilder checkResponseBodyAndResponseCodeForSpecificNeAccountDataNegative() {
        return flow("Check response for Get Ne Account for a specific Ne flow for negative tests.")
                .addTestStep(
                        annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_FOR_SPECIFIC_NE_NEGATIVE))
                .withDataSources(dataSource(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_NEGATIVE_DS));
    }

    public TestStepFlowBuilder checkResponseBodyAndResponseCodeForSpecificNeAccountDataWithIdAndStatus(final String testDataSource) {
        return flow("Check response for Get Ne Account with Ids and status for a specific Ne flow.")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_FOR_SPECIFIC_NE_WITH_ID_AND_STATUS))
                .withDataSources(dataSource(testDataSource).bindTo(GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS));
    }

    public TestStepFlowBuilder importFile() {
        return flow("Import file for Job creation flow.")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_IMPORT_FILE));
    }

    public TestStepFlowBuilder getNpamConfig() {
        return flow("Get Npam config flow.")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_GET_NPAM_CONFIG));
    }

    public TestStepFlowBuilder updateNpamConfig() {
        return flow("Update Npam config flow.")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_UPDATE_NPAM_CONFIG));
    }

    public TestStepFlowBuilder checkResponseForNeAccountWithWrongAccessRights() {
        return flow("Check response for Ne Account flow with wrong access rights")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_WRONG_ACCESS_RIGHTS))
                .withDataSources(dataSource(GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS));
    }

    public TestStepFlowBuilder getListFile() {
        return flow("Get List File for Npam config flow - Enabled.")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_ENABLED));
    }

    public TestStepFlowBuilder importFileWithWrongAccessRights() {
        return flow("Import file for Job creation flow wrong access rights.")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_IMPORT_FILE_WRONG_ACCESS_RIGHTS));
    }

    public TestStepFlowBuilder getListFileWithWrongAccessRights() {
        return flow("Get List File for Npam flow wrong access rights.")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_GET_LIST_FILE_WRONG_ACCESS_RIGHTS));
    }

    public TestStepFlowBuilder getSpecifiJobWithExpected() {
        return flow("Check response for Get specific Npam Job with expected.")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESPONSE_NPAM_JOB_SPECIFIC_WITH_EXPECTED))
                .withDataSources(dataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS));
    }

    /*
     * The flow create a job and retrieve its details on name base
     * e.g. it executes the rest "/npamservice/v1/job/list/jobName" for the job previously created
     * The flow is not checking if the job is completed
     */
    public TestStepFlowBuilder retrieveJobDetailsOnNameBase(final String testDatasource) {
        return flow("Retrieve Npam job details on name base")
                .addSubFlow(createGenericJob(testDatasource))
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_GET_SPECIFIC_JOB_FROM_JOBS_LIST).withParameter("jobName",
                                TestScenarios.fromTestStepResult(
                                        NpamFunctionalTestSteps.StepIds.CREATE_NPAM_JOB)))
                .withDataSources(dataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS));
    }

    /*
     * The flow create an UPDATE_WITH_AUTO_GENERATED_PASSWORD job and retrieve its configuration on name base
     */
    public TestStepFlowBuilder getJobConfiguration() {
        return flow("Get specific Npam Job and retrieve its configuration")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CREATE_NPAM_JOB))
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_JOB_CONFIGURATION).withParameter("jobName",
                                TestScenarios.fromTestStepResult(
                                        NpamFunctionalTestSteps.StepIds.CREATE_NPAM_JOB)))
                .withDataSources(dataSource(GET_RESPONSE_FOR_CREATE_JOB_DS),
                        dataSource(GET_RESPONSE_FOR_JOB_CONFIGURATION_DS));
    }

    /*
     * The flows create a job of type given by user retrieving jobName, then get the jobDetails from the list
     * on jobName base e.g npamservice/v1/job/list/<jobName> that returns jobInstanceId and on this execute the rest
     * /npamservice/v1/job/nedetails/<jobInstanceId>
     */
    public TestStepFlowBuilder getSpecificNeJob(final String testDataSource) {
        return flow("Check response for Get specific Npam Job and retrieve its configuration")
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CREATE_NPAM_JOB))
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_GET_SPECIFIC_JOB_FROM_JOBS_LIST).withParameter("jobName",
                                TestScenarios.fromTestStepResult(
                                        NpamFunctionalTestSteps.StepIds.CREATE_NPAM_JOB)))
                .addTestStep(annotatedMethod(npamFuncTestSteps,
                        NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_GET_SPECIFIC_JOB).withParameter("jobInstanceId", TestScenarios.fromTestStepResult(
                                NpamFunctionalTestSteps.StepIds.CHECK_RESP_FOR_GET_SPECIFIC_JOB_FROM_JOBS_LIST)))
                .withDataSources(dataSource(testDataSource).bindTo(GET_RESPONSE_FOR_CREATE_JOB_DS), dataSource(GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS));
    }

    /*
     * Export the file containing Ne accounts data and copy the file on remote to decrypt the included data
     * and remove the files after test execution
     */
    public TestStepFlowBuilder exportSpecificNeAccountData() {
        return flow("Export Ne accounts flow.")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.EXPORT_NE_ACCOUNT))
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.COPY_EXPORT_FILE_TO_REMOTE))
                .withDataSources(dataSource(EXPORT_NE_ACCOUNT_DS), dataSource(COPY_EXPORT_FILE_DS));
    }
}
