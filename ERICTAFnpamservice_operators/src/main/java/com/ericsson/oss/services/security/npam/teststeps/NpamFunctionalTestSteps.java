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

package com.ericsson.oss.services.security.npam.teststeps;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertTrue;

import static com.ericsson.oss.testware.remoteexecution.operators.PemFileUtilities.getHostnameOfDeployment;
import static com.ericsson.oss.testware.remoteexecution.operators.PemFileUtilities.getPrivateKey;
import static com.ericsson.oss.testware.remoteexecution.operators.PemFileUtilities.writePrivateKeyToFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.annotations.Input;
import com.ericsson.cifwk.taf.annotations.OptionalValue;
import com.ericsson.cifwk.taf.annotations.Output;
import com.ericsson.cifwk.taf.annotations.TestStep;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.data.Host;
import com.ericsson.cifwk.taf.tools.cli.TafCliToolShell;
import com.ericsson.cifwk.taf.tools.cli.handlers.impl.RemoteObjectHandler;
import com.ericsson.oss.services.security.npam.operators.HttpToolsOperatorImpl;
import com.ericsson.oss.services.security.npam.util.CredentialManagerSecurityTestUtil;
import com.ericsson.oss.services.security.npam.util.FileOperationHelper;
import com.ericsson.oss.services.security.npam.util.NPamNEAccountResponse;
import com.ericsson.oss.testware.hostconfigurator.HostConfigurator;
import com.ericsson.oss.testware.remoteexecution.operators.PibConnector;

public class NpamFunctionalTestSteps {
    private static final Logger log = LoggerFactory.getLogger(NpamFunctionalTestSteps.class);

    @Inject
    HttpToolsOperatorImpl httpToolsOperatorImpl;

    @Inject
    CredentialManagerSecurityTestUtil credentialManagerSecurityTestUtil;

    @Inject
    private PibConnector pibConnector;

    private static final String NPAM_PSWD = "npam.pswd";
    private static final int LIMIT_COUNTER = 60;

    /*
     * Npam Job Management
     */
    // It is mandatory to put in csv file "name":"jobName"
    @TestStep(id = StepIds.CREATE_NPAM_JOB)
    public String createNpamJobAndCheckResponse(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("jsonJob") final String jsonJob, @Input("jobType") final String jobType) {
        log.info("Verifying the Response Body for create a Job ");
        final String jobUniqueName = appendNanoSecToAString(jobType +"_Job");
        log.info("JobName: {}", jobUniqueName);
        final String jsonInput = jsonJob.replace("jobName", jobUniqueName);
        final String jsonOutput = "{\"name\":\"" + jobUniqueName + "\"}";
        log.info("Json input for create a job: {}", jsonInput + " jsonOut: {}", jsonOutput);
        final String getResponseForCreateJob = httpToolsOperatorImpl.executePostResponseBody(url, jsonInput, false, username, pswd);
        log.info("Body of response for create job: {}", getResponseForCreateJob);

        //I am waiting for a while for job completion
        log.info("I wait until the job is completed...");
        final String urlGetSpecificJob = "/npamservice/v1/job/list/";
        log.info("Url of get for job just created: {}", urlGetSpecificJob + jobUniqueName);
        final String getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(urlGetSpecificJob + jobUniqueName, "", false, username, pswd);
        log.info("Body of response for get job: {}", getResponseBody);
        assertThat(jobStateIsCompleted(username, pswd, jobUniqueName, urlGetSpecificJob, getResponseBody)).isTrue();

        assertThat(getResponseForCreateJob.equals(jsonOutput)).isTrue();
        return jobUniqueName;
    }

    // It is mandatory to put in csv file "name":"jobName"
    @TestStep(id = StepIds.CREATE_NPAM_JOB_WITH_EXPECTED)
    public void createNpamJobWithExpectedAndCheckResponse(
            @Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("jsonJob") final String jsonJob, @Input("jobType") final String jobType,
            @Input("expected") final String expected) {
        log.info("Verifying the Response Body for create a Job ");
        final String jobUniqueName = appendNanoSecToAString(jobType + "_Job");
        final String jsonInput = jsonJob.replace("jobName", jobUniqueName);
        log.info("jsonInput for getResponseForJobCreationWithExpected: {}, url: {}", jsonInput, url);
        final String getResponseForCreateJob = httpToolsOperatorImpl.executePostResponseBody(url, jsonInput, false, username, pswd);

        //I am waiting for a while for job completion
        log.info("I wait until the job is completed...");
        final String urlGetSpecificJob = "/npamservice/v1/job/list/";
        log.info("Url of get for job just created: {}", urlGetSpecificJob + jobUniqueName);
        final String getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(urlGetSpecificJob + jobUniqueName, "", false, username, pswd);
        log.info("Body of response for get job: {}", getResponseBody);
        assertThat(jobStateIsCompleted(username, pswd, jobUniqueName, urlGetSpecificJob, getResponseBody)).isTrue();

        log.info("Body of response for create job with expected tests: {}", getResponseForCreateJob);
        log.info("Expected response for job creation: {}", expected);

        assertThat(getResponseForCreateJob.contains(expected)).isTrue();
    }

    @TestStep(id = StepIds.CHECK_RESPONSE_NPAM_JOB_SPECIFIC_WITH_EXPECTED)
    public void getResponseForNpamSpecificJobWithExpected(@Input("Url") final String url, @Input("Expected") final String expected,
            @Input("user") final String username,
            @Input("pswd") final String pswd) {
        log.info("Verifying the Response for Npam specific Job with results");
        log.info("Expected response for Npam specific Job with results: {}", expected);
        final String getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(url, "", false, username, pswd);
        log.info("Response body for a specific job by name: {} ", getResponseBody);
        assertThat(getResponseBody.contains(expected)).isTrue();
    }

    @TestStep(id = StepIds.CHECK_RESP_FOR_CANCEL_ENABLE_REMOTE_MANAGEMENT_JOB)
    public void cancelEnableRemoteManagementJob(@Input("createUrl") final String createUrl,
                                                @Input("cancelUrl") final String cancelUrl,
                                                @Input("user") final String username,
                                                @Input("pswd") final String pswd,
                                                @Input("json") final String json) {
        log.info("Cancel Ne Account creation Job: Verifying the Response Body for cancel Scheduled CREATE_NE_ACCOUNT Job");

        //Calculate and replace the start date of the scheduled job
        final LocalDateTime date = LocalDateTime.now().plusDays(1);
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String scheduledDate = date.format(formatter);
        log.info("Date: {}", scheduledDate);
        String jsonInputWithDate = json.replace("date", scheduledDate);

        //Calculate and replace the jobUniqueName of the scheduled job
        final String jobUniqueName = appendNanoSecToAString("CREATE_NE_ACCOUNT_Job");
        log.info("JobName: {}", jobUniqueName);
        String jsonInput = jsonInputWithDate.replace("jobName", jobUniqueName);

        log.info("Delete NE Account Job jsonInput: {}", jsonInput);
        final String jsonOutput = "{\"name\":\"" + jobUniqueName + "\"}";
        final String getResponseForCreateJob = httpToolsOperatorImpl
                .executePostResponseBody(createUrl, jsonInput, false, username, pswd);
        log.info("Delete NE Account Job: Body of response for create CREATE_NE_ACCOUNT job: {}", getResponseForCreateJob);

        //I am waiting for a while for job completion
        log.info("I wait until the job is completed...");
        final String urlGetSpecificJob = "/npamservice/v1/job/list/";
        log.info("Url of get for job just created: {}", urlGetSpecificJob + jobUniqueName);
        final String getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(urlGetSpecificJob + jobUniqueName, "", false, username, pswd);
        log.info("Body of response for get job: {}", getResponseBody);
        assertThat(jobStateIsCompleted(username, pswd, jobUniqueName, urlGetSpecificJob, getResponseBody)).isTrue();

        assertThat(getResponseForCreateJob.equals(jsonOutput)).isTrue();
        //Cancel scheduled job
        final String jsonCancelOutput = "Job " + jobUniqueName + " correctly deleted.";
        final String getResponseForCancelJob = httpToolsOperatorImpl
                .executePostResponseBody(cancelUrl.concat(jobUniqueName), "", false, username, pswd);
        log.info("Delete NE Account Job: Body of response for cancel Job: {}", getResponseForCancelJob);

        try {
            log.info("I am waiting for a while for cancel job completion");
            Thread.sleep(10000);
        } catch (final InterruptedException e) {
            log.warn(e.getMessage());
        }

        assertThat(getResponseForCancelJob.equals(jsonCancelOutput)).isTrue();
    }

    /*
     * Response is like
     * [{"jobInstanceId":514014,
     * "name":"UPDATE_WITH_AUTO_GENERATED_PASSWORD_Job70918268000000",
     * "state":"RUNNING",
     * "result":"",
     * "startTime":"23-04-04 17:41:59+0000",
     * "endTime":"23-04-04 17:42:01+0000",
     * "type":"UPDATE_WITH_AUTO_GENERATED_PASSWORD",
     * "numberOfNetworkElements":2,
     * "progressPercentage":0.0,
     * "errorDetails":"",
     * "owner":"administrator"}]
     */
    /*
     * This test step execute the rest /npamservice/v1/job/list/<jobName> and retrieve the related jobInstanceId
     */
    @TestStep(id = StepIds.CHECK_RESP_FOR_GET_SPECIFIC_JOB_FROM_JOBS_LIST)
    public String getResponseForNpamSpecificJobFromJobsList(@Input("user") final String username,
            @Input("pswd") final String pswd, @Input("scheduled") @OptionalValue("false") final boolean scheduled,
            @Input("jobName") final String jobName) {
        log.info("Verifying the Response for Npam specific Job");
        final String urlGetSpecificJob = "/npamservice/v1/job/list/";
        final String getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(urlGetSpecificJob + jobName, "", false, username, pswd);
        log.info("Response body for a specific job by name {}: {} ", jobName, getResponseBody);
        final String job = "\"name\":\"" + jobName + "\"";
        assertThat(getResponseBody.contains("jobInstanceId") && getResponseBody.contains("state") &&
                getResponseBody.contains("result") && getResponseBody.contains("endTime") &&
                getResponseBody.contains("startTime") &&
                getResponseBody.contains("jobType") && getResponseBody.contains("numberOfNetworkElements")
                && getResponseBody.contains("progressPercentage")
                && getResponseBody.contains("errorDetails") && getResponseBody.contains("owner")
                && getResponseBody.contains(job)).isTrue();
        final JSONArray jsonArray = new JSONArray(getResponseBody);
        return ((JSONObject) jsonArray.get(0)).optString("jobInstanceId");
    }

    // This step executes the specific rest "job/nedetails/JobInstanceId" on jobInstanceId base
    /*
     * The response is like
     * [
     * {
     * "neName": "${nodes.radio.node1}",
     * "state": "COMPLETED",
     * "startTime": "2023-05-08 15:54:30+0000",
     * "endTime": "2023-05-08 15:54:42+0000",
     * "result": "SUCCESS",
     * "errorDetails": ""
     * },
     * {
     * "neName": "${nodes.radio.node4}",
     * "state": "COMPLETED",
     * "startTime": "2023-05-08 15:54:30+0000",
     * "endTime": "2023-05-08 15:54:30+0000",
     * "result": "SKIPPED",
     * "errorDetails": "Warning: operation skipped cause remoteManagement attribute is already true"
     * },
     * {
     * "neName": "${nodes.radio.node3}",
     * "state": "COMPLETED",
     * "startTime": "2023-05-08 15:54:30+0000",
     * "endTime": "2023-05-08 15:54:31+0000",
     * "result": "SUCCESS",
     * "errorDetails": ""
     * }
     * ]
     */
    @TestStep(id = StepIds.CHECK_RESP_FOR_GET_SPECIFIC_JOB)
    public void getSpecificJobOnJobInstanceId(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("scheduled") @OptionalValue("false") final boolean scheduled,
            @Input("jobInstanceId") final String jobInstanceId) {
        log.info("Verifying the Response for Npam NeJob on jobInstanceId base");
        final String urlGetSpecificJob = "/npamservice/v1/job/nedetails/";
        final String getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(urlGetSpecificJob + jobInstanceId, "", false, username, pswd);
        log.info("Response body for a specific job by jobInstanceId {}: {} ", jobInstanceId, getResponseBody);
        final String responseWithoutSquares = getResponseBody.replaceAll("[\\[\\]]", "");
        if (scheduled) {
            assertThat(responseWithoutSquares.isEmpty()).isTrue();
        } else {
            assertThat(getResponseBody.contains("neName") && getResponseBody.contains("state") && getResponseBody.contains("startTime")
                    && getResponseBody.contains("endTime") && getResponseBody.contains("result") && getResponseBody.contains("errorDetails"))
                            .isTrue();
            assertThat(jobStateIsCompleted(username, pswd, jobInstanceId, urlGetSpecificJob, getResponseBody)).isTrue();
        }
    }

    private boolean jobStateIsCompleted(final String username, final String pswd, final String jobInstanceId,
            final String urlGetSpecificJob, String getResponseBody) {
        log.info("I am checking if the job state is COMPLETED. The response for job to check its state is: {}", getResponseBody);
        int counter = 0;
        while (getResponseBody.contains("\"state\"") &&
                !getResponseBody.contains("\"state\":\"COMPLETED\"") &&
                !getResponseBody.contains("\"state\":\"SCHEDULED\"") &&
                counter <= LIMIT_COUNTER) {
            getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(urlGetSpecificJob + jobInstanceId, "", false, username, pswd);
            log.info("The Body of response for get job number {} is: {}", counter, getResponseBody);
            try {
                log.info("I am waiting for a while for job completion");
                Thread.sleep(10000);
            } catch (final InterruptedException e) {
                log.warn(e.getMessage());
            }
            counter++;
        }
        log.info("The current state for job completion is: {}", counter <= LIMIT_COUNTER);
        return counter <= LIMIT_COUNTER;
    }

    /*
     * The response for rest npamservice/v1/job/configuration/myJob1 is like
     * {
     * "name": "myJob1",
     * "description": "Update with auto generate job",
     * "jobType": "UPDATE_WITH_AUTO_GENERATED_PASSWORD",
     * "jobProperties": [],
     * "selectedNEs": {
     * "collectionNames": [],
     * "neNames": [
     * "LTE11dg2ERBS00001"
     * ],
     * "savedSearchIds": []
     * },
     * "mainSchedule": {
     * "execMode": "SCHEDULED",
     * "scheduleAttributes": [
     * {
     * "name": "REPEAT_TYPE",
     * "value": "Weekly"
     * },
     * {
     * "name": "REPEAT_COUNT",
     * "value": "2"
     * },
     * {
     * "name": "START_DATE",
     * "value": "2025-02-23 12:18:00"
     * },
     * {
     * "name": "END_DATE",
     * "value": "2025-03-23 12:18:00"
     * }
     * ],
     * "wrongImmediate": false,
     * "wrongNonPeriodic": true,
     * "periodic": true
     * },
     * "owner": "administrator",
     * "creationTime": "23-04-07 09:43:55+0000"
     * }
     */
    // The values in input are the ones of UPDATE_WITH_AUTO_GENERATED_PSWD - to be improved customizing the jobType that can be used
    @TestStep(id = StepIds.CHECK_RESP_FOR_JOB_CONFIGURATION)
    public void getResponseForJobConfiguration(@Input("Url") final String url,
            @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("scheduled") @OptionalValue("false") final boolean scheduled,
            @Input("jobName") final String jobName) {
        log.info("Verifying the Response for Npam specific configuration Job");
        final String urlForJobConfiguration = "/npamservice/v1/job/configuration/";
        final String getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(urlForJobConfiguration + jobName, "", false, username, pswd);
        log.info("Response body for job configuration by name {}: {} ", jobName, getResponseBody);
        final String owner = "\"owner\":\"" + username + "\"";
        final String job = "\"name\":\"" + jobName + "\"";
        assertThat(getResponseBody.contains(owner)
                && getResponseBody.contains(job)
                && getResponseBody.contains("description") &&
                getResponseBody.contains("jobType") &&
                getResponseBody.contains("creationTime") && getResponseBody.contains("jobProperties") &&
                getResponseBody.contains("mainSchedule") && getResponseBody.contains("selectedNEs")).isTrue();
    }

    /*
     * The response is a list
     * jobInstanceId": 1437002,
     * "name": "CreateNEAccountJob_administrator_26042023155102",
     * "state": "USER_CANCELLED",
     * "result": "",
     * "startTime": "2024-04-25 23:00:00+0000",
     * "endTime": null,
     * "type": "ENABLE_REMOTE_MANAGEMENT",
     * "numberOfNetworkElements": 0,
     * "progressPercentage": 0.0,
     * "errorDetails": "",
     * "owner": "administrator"
     */
    @TestStep(id = StepIds.CHECK_RESP_BODY_FOR_GET_ALL_JOBS)
    public void getResponseBodyForGetAllJobs(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("Expected") final String expected) {
        log.info("Verifying the Response Body for get All Jobs");
        final String getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(url, "", false, username, pswd);
        log.debug("Body of response for getAllJobs: {}", getResponseBody);
        assertThat(getResponseBody.contains("jobInstanceId") && getResponseBody.contains("name") &&
                getResponseBody.contains("state") && getResponseBody.contains("result")
                && getResponseBody.contains("startTime") && getResponseBody.contains("endTime")
                && getResponseBody.contains("jobType") && getResponseBody.contains("numberOfNetworkElements")
                && getResponseBody.contains("progressPercentage")
                && getResponseBody.contains("errorDetails") && getResponseBody.contains("owner")).isTrue();
    }

    @TestStep(id = StepIds.CHECK_RESP_BODY_FOR_GET_ALL_JOBS_WITH_RESULTS)
    public void getResponseForGetAllJobsWithResults(@Input("Url") final String url,
            @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("Expected") final String expected) {
        log.info("Verifying the Response Body for get All Jobs with results");
        final String getResponseBody = httpToolsOperatorImpl.executeGetResponseBody(url, "", false, username, pswd);
        log.debug("Body of response for getAllJobs: {}", getResponseBody);
        assertThat(getResponseBody.equals(expected));
    }

    /*
     * NeAccount management
     */
    @TestStep(id = StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_WITH_RESULTS_POSITIVE)
    public void getResponseBodyForNeAccount(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("json") final String json, @Input("expectedNeName") final String expectedNeName,
            @Input("expectedExtStatus") final String expectedExtStatus, @Input("expectedStatus") final String expectedStatus) {
        log.info("Verifying the Response for Ne Account with expected");
        final String getResponseForNeAccount = httpToolsOperatorImpl.executePostResponseBody(url, json, false, username, pswd);
        log.info("Body of response for get NE account with results: \n {}", getResponseForNeAccount);
        final Map<String, NPamNEAccountResponse> neAccountResults = neAccountParsing(getResponseForNeAccount);
        for (final Entry<String, NPamNEAccountResponse> entry : neAccountResults.entrySet()) {
         final String neName = entry.getValue().getNeName();
            final String neStatus = entry.getValue().getNeNpamStatus();
            final String neAccountStatus = entry.getValue().getNeAccountStatus();
            log.info("neName: {}, neStatus: {}, neAccountStatus: {}", neName, neStatus, neAccountStatus);
             assertThat(neName.equals(expectedNeName)).isTrue();
             assertThat(neStatus.equals(expectedExtStatus)).isTrue();
             assertThat(neAccountStatus.equals(expectedStatus)).isTrue();
        }
    }

    @TestStep(id = StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_WITH_RESULTS_NEGATIVE)
    public void getResponseForNeAccountNegative(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("json") final String json, @Input("expected") final String expected) {
        log.info("Verifying the Response Body for Ne Account with expected negative");
        final String getResponseForNeAccount = httpToolsOperatorImpl.executePostResponseBody(url, json, false, username, pswd);
        log.info("Body of response for get NE account: \n {}", getResponseForNeAccount);
        assertThat(getResponseForNeAccount.contains(expected)).isTrue();
    }

    // This is a GET to retrieve a specific NE account including password in plain text or encrypted
    // https://enmapache.athtem.eei.ericsson.se/npamservice/v1/neaccount/LTE11dg2ERBS00025
    // Possible response
    // [{"neName":"${nodes.radio.node1}",
    // "ipAddress":"192.168.104.189",
    // "currentUser":"test1",
    // "currentPswd":"-4UsLos1I2uU",
    // "id":"1",
    // "errorDetails":"null",
    // "status":"CONFIGURED",
    // "lastUpdate":"2023-05-04 17:15:46+0000"}]
    // --> status value is ignored in this case such as ipAddress
    @TestStep(id = StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_FOR_SPECIFIC_NE)
    public String getResponseForNeAccountForSpecificNeAndPswd(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("ne") final String NE,
            @Input("plainText") @OptionalValue("true") final boolean plainText,
            @Input("checkRotate") @OptionalValue("false") final boolean checkRotate) {
        log.info("Verifying the Response Body for Ne Account for a specific NE ");
        log.debug("URL get Ne Account for the specific NE: {} ", url + NE);
        final String urlForNe = url + NE;
        String getResponseForSpecificNeAccount = httpToolsOperatorImpl.executeGetResponseBody(urlForNe, "", false, username, pswd);
        log.info("Body of response for NE {} account: {}", NE, getResponseForSpecificNeAccount);
        if (!getResponseForSpecificNeAccount.contains("NPamNEAccount not found for selected NE") && !getResponseForSpecificNeAccount.isEmpty()) {
            assertTrue(getResponseForSpecificNeAccount.contains("\"neName\":\"" + NE + "\""));
            assertTrue(getResponseForSpecificNeAccount.contains("currentUser"));
            assertTrue(getResponseForSpecificNeAccount.contains("\"id\":\"1\""));
            assertTrue(getResponseForSpecificNeAccount.contains("ipAddress"));
            assertTrue(getResponseForSpecificNeAccount.contains("status"));
            assertTrue(getResponseForSpecificNeAccount.contains("currentPswd"));

            if (plainText == false) {
                log.info("It is expected to have hidden password");

                assertTrue(getResponseForSpecificNeAccount.contains("\"currentPswd\":\"********\""));
            }
        } else {
            log.info("Trying to get for NEAccount for a while....");
            int counter = 0;
            while (!getResponseForSpecificNeAccount.contains("currentPswd") && counter <= 6) {
                getResponseForSpecificNeAccount = httpToolsOperatorImpl.executeGetResponseBody(url + NE, "", false, username, pswd);
                try {
                    log.info("I am waiting for a while for Ne Account creation");
                    Thread.sleep(10000);
                } catch (final InterruptedException e) {
                    log.warn(e.getMessage());
                }
                counter++;
            }
            log.info("The current state for Ne Account creation is: {}", counter <= 6);
        }
        final String responseWithoutSquares = getResponseForSpecificNeAccount.replaceAll("[\\[\\]]", "");
        final JSONObject json = new JSONObject(responseWithoutSquares);
        final String currentPswd = json.getString("currentPswd");
        log.debug("currentPswd is: {}", currentPswd);
        if (DataHandler.getAttribute(NPAM_PSWD) != "" && checkRotate) {
            assertThat(!currentPswd.equals(DataHandler.getAttribute(NPAM_PSWD))).isTrue();
            log.info("Password correctly rotated");
        }
        DataHandler.setAttribute(NPAM_PSWD, currentPswd);
        log.info("Saved current credentials in DataHandler");
        return currentPswd;
    }

    @TestStep(id = StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_FOR_SPECIFIC_NE_NEGATIVE)
    public void getResponseBodyForNeAccountForSpecificNe(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("ne") final String NE,
            @Input("expected") final String expected) {
        log.info("Verifying the Response Body for Ne Account for a specific NE negative");
        log.debug("URL get Ne Account for the specific NE {}: {} ", NE, url);
        final String UrlToSend = url + NE;
        log.info("Expected is:{}", expected);
        final String getResponseForSpecificNeAccount = httpToolsOperatorImpl.executeGetResponseBody(UrlToSend, "", false, username, pswd);
        log.info("Body of response for NE {} account: {}", NE, getResponseForSpecificNeAccount);
        assertThat(getResponseForSpecificNeAccount.contains(expected)).isTrue();
    }

  //Example of response
   /* [{"neName":"${nodes.radio.node3}",
   "neNpamStatus":"NOT_MANAGED",
   "neAccounts":
   [{"neName":"${nodes.radio.node3}",
   "currentUser":"${nodes.radio.node3}",
   "id":"1",
   "errorDetails":"null",
   "status":"DETACHED",
   "lastUpdate":"23-04-16 09:51:59+0000"}]}]
*/
    @TestStep(id = StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_FOR_SPECIFIC_NE_WITH_ID_AND_STATUS)
    public void getResponseBodyForNeAccountForSupportedRadioNodeWithIdsAndStatus(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("json") final String json, @Input("expected") final String expected,
            @Input("expectedNeName") final String expectedNeName, @Input("expectedId") final int expectedId, @Input("expectedStatus") final String expectedStatus,
           @Input("expectedExtStatus") final String expectedExtStatus)
            throws ParseException, IOException {
        log.info("Verifying the Response Body for Ne Account for a specific NE");
        log.info("URL get Ne Account for NE account for supported radio node: {}", url);
        log.info("JSON input for NE account: {}", json);
        final String getResponseForNeAccountWithIdsAndStatus = httpToolsOperatorImpl.executePostResponseBody(url, json, false, username, pswd);
        log.info("Body of response for NE account: {}", getResponseForNeAccountWithIdsAndStatus);
        final String responseWithoutSquares = getResponseForNeAccountWithIdsAndStatus.replaceAll("[\\[\\]]", "");
        if (!responseWithoutSquares.isEmpty() && !responseWithoutSquares.contains("Internal Server Error")) {
            final Map<String, NPamNEAccountResponse> mappa = neAccountParsing(getResponseForNeAccountWithIdsAndStatus);
            for (final Entry<String, NPamNEAccountResponse> entry : mappa.entrySet()) {
                final String neName = entry.getValue().getNeName();
                final String neStatus = entry.getValue().getNeNpamStatus();
                final String neAccountStatus = entry.getValue().getNeAccountStatus();
                final int id = entry.getValue().getId();
                log.info("The id of neAccounts is not in the response");
                log.info("neStatus: {}, neAccountStatus: {} ", neStatus, neAccountStatus);
                log.info("expectedNeName: {}, expectedExtStatus: {}", expectedNeName, expectedExtStatus);
                assertThat(neName.equals(expectedNeName)).isTrue(); // not ok for node 19
                assertThat(neStatus.equals(expectedExtStatus)).isTrue();
                if (id != 0) { // NeName
                log.info("neAccountStatus: {}, expectedStatus: {}", neAccountStatus, expectedStatus);
                log.info("id: {}, expectedId: {}", id, expectedId);
                assertThat(neAccountStatus.contains(expectedStatus)).isTrue();
                   assertThat(id == expectedId).as("Value for id of neAccounts is wrong").isTrue();
                }
            }
        } else {
            log.info("The neAccounts response is empty or contains Internal Server Error userMessage");
            if (!responseWithoutSquares.isEmpty()) {
                assertThat(getResponseForNeAccountWithIdsAndStatus.contains(expected)).isTrue();
            } else {
                assertThat(responseWithoutSquares.trim().isEmpty()).isTrue();
            }

        }
    }

    @TestStep(id = StepIds.CHECK_RESP_BODY_FOR_NE_ACCOUNT_WRONG_ACCESS_RIGHTS)
    public void getResponseBodyForNeAccountWithWrongAccessRights(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("expected") final String expected, @Input("jsonInput") final String jsonInput) {
        log.info("Verifying the Response Body for Ne Account with wrong access rights");
        log.debug("URL get Ne Account: {} ", url);
        log.debug("JSON input: {}", jsonInput);
        final String getResponseForNeAccount = httpToolsOperatorImpl.executePostResponseBody(url, jsonInput, false, username, pswd);
        log.info("Body of response for get NE account: {}", getResponseForNeAccount);
        assertThat(getResponseForNeAccount.contains(expected)).isTrue();
    }

    /*
     * File Management
     */
    /**
     * A multipart/form-data POST rest call on with file
     *
     * @param url
     *            https://enmapache.athtem.eei.ericsson.se/npamservice/v1/importJobFile?overwrite=true
     */
    @TestStep(id = StepIds.CHECK_RESP_FOR_IMPORT_FILE)
    public void getResponseForImportFile(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("fileName") final String fileName) {
        log.info("Verifying the Import file action to be used for UPDATE_WITH_FILE Job creation");
        final File file = FileOperationHelper.getFileFromFileFinder(fileName);
        log.info("File to be imported: {}", file);
        final String getResponseBodyForImportFile = httpToolsOperatorImpl.executeRestPutMultipartFileCall(url, file, false, username, pswd);
        log.info("Response body for import file: {}", getResponseBodyForImportFile);
        final String expectedOutput = "File " + fileName + " correctly imported.";
        assertThat(getResponseBodyForImportFile.contains(expectedOutput)).isTrue();
    }

    @TestStep(id = StepIds.CHECK_RESP_FOR_IMPORT_FILE_WRONG_ACCESS_RIGHTS)
    public void getResponseForImportFileWrongAccess(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("expected") final String expected, @Input("fileName") final String fileName) {
        log.info("Verifying the Import file action to be used for UPDATE_WITH_FILE Job creation with wrong access rights");
        final File file = FileOperationHelper.getFileFromFileFinder(fileName);
        final String getResponseBodyForImportFile = httpToolsOperatorImpl.executeRestPutMultipartFileCall(url, file, false, username, pswd);
        log.info("Response for import file with wrong access rights: {}", getResponseBodyForImportFile);
        assertThat(getResponseBodyForImportFile.contains(expected)).isTrue();
    }

    /*
     * Generic step
     */
    @TestStep(id = StepIds.TEST_STEP_WAIT_FOR_A_WHILE)
    public void waitTimeToProceed(@Input("wait") final long wait) {
        try {
            log.info("Waiting {} millisecond(s).", wait);
            Thread.sleep(wait);
        } catch (final InterruptedException ie) {
            log.warn("Could not wait {} millisecond(s)", wait, ie);
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }

    /*
     * NpamConfig management
     */
    @TestStep(id = StepIds.CHECK_RESP_FOR_GET_NPAM_CONFIG)
    public void getResponseForGetNpamConfig(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("expected") final String expected, @Input("context") final String context) {
        log.info("Verifying the Response Body for get NpamConfig");
        log.info("URL get npamConfig: {} ", url);
        log.info("JSON enabled expected: {} ", expected);
        final String getResponseForGetNpamConfig = httpToolsOperatorImpl.executeGetResponseBody(url, "", false, username, pswd);
        log.info("Body of response for get npamConfig with context {}: {}", context, getResponseForGetNpamConfig);
        if (context == "positive") {
            assertThat(getResponseForGetNpamConfig.contains("npam") && getResponseForGetNpamConfig.contains("cbrs")).isTrue();
        } else if (context == "negative") {
            assertThat(getResponseForGetNpamConfig.contains(expected));
        }
    }

    @TestStep(id = StepIds.CHECK_RESP_FOR_UPDATE_NPAM_CONFIG)
    public void getResponseForUpdateNpamConfig(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("json") final String jsonInput, @Input("context") final String context,
            @Input("expected") final String expected) {
        log.info("Verifying the Response Body for update NpamConfig");
        log.debug("URL get npamConfig: {} ", url);
        final String getResponseForUpdateNpamConfig = httpToolsOperatorImpl.executePostResponseBody(url, jsonInput, false, username, pswd);
        log.info("Body of response for update npamConfig: {}", getResponseForUpdateNpamConfig);
        if (context == "positive") {
            assertThat(getResponseForUpdateNpamConfig.contains("npam") && getResponseForUpdateNpamConfig.contains("cbrs")).isTrue();
        } else if (context == "negative") {
            assertThat(getResponseForUpdateNpamConfig.contains(expected));
        }
    }

    /*
     * List file management
     */
    @TestStep(id = StepIds.CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_ENABLED)
    public void getResponseBodyForGetListFileNpamConfigEnabled(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("fileName") final String fileName) {
        log.info("Verifying the Response Body for get List File NpamConfig Enabled");
        log.info("URL get list file npamConfig: {} ", url);
        final String getResponseForGetListFile = httpToolsOperatorImpl.executeGetResponseBody(url, "", false, username, pswd);
        log.info("Body of response for get list file npamConfig Enabled: {}", getResponseForGetListFile);
        if (!getResponseForGetListFile.isEmpty()) {
            final String str = getResponseForGetListFile.replaceAll("[\\[\\]]", "");
            final List<String> list = Arrays.asList(str.split(","));
            final String toCheck = "\"" + fileName + "\"";
            assertThat(list.contains(toCheck)).isTrue();
        } else {
            log.info("Something may be wrong if an import has been done first");
        }
    }

    @TestStep(id = StepIds.CHECK_RESP_FOR_GET_LIST_FILE_WRONG_ACCESS_RIGHTS)
    public void getResponseForGetListWithWrongAccessRights(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("expected") final String expected) {
        log.info("Verifying the Response Body for Get List with wrong access rights");
        log.info("URL get List: {} ", url);
        final String getResponseForGetListFile = httpToolsOperatorImpl.executeGetResponseBody(url, "", false, username, pswd);
        log.info("Body of response for get list file with wrong access rights: {}", getResponseForGetListFile);
        assertThat(getResponseForGetListFile.contains(expected)).isTrue();
    }

    @TestStep(id = StepIds.EXPORT_NE_ACCOUNT)
    public void exportNeAccounts(@Input("Url") final String url, @Input("user") final String username,
            @Input("pswd") final String pswd, @Input("json") final String json) {
        log.info("Executing NE accounts export");
        log.info("URL execute NE accounts export: {} ", url);
        try {
            log.info("I am waiting for a while for job CREATE_NE_ACCOUNT completion before execute the export");
            Thread.sleep(20000);
        } catch (final InterruptedException e) {
            log.warn(e.getMessage());
        }
        final FileOutputStream fileNameForExport = httpToolsOperatorImpl.executePostResponseForNpamExport(url, json, false, username, pswd);
    }

    @TestStep(id = StepIds.COPY_EXPORT_FILE_TO_REMOTE)
    public void exportFileToRemote(@Input("filesToCopy") final String filesToCopy, @Output("destinationPath") final String destinationPath,
            @Output("destinationFile") final String destinationFile, @Output("nes") final String nes, @Input("myKey") final String myKey) {
        log.info("Copying export file to MS-1 or EMP and check the content");
        final TafCliToolShell cliToolShell = pibConnector.getConnection();
        final Host pibHost = HostConfigurator.getPibHost();
        final RemoteObjectHandler remoteObjectHandler = new RemoteObjectHandler(pibHost);
        cliToolShell.execute("sudo -s mkdir -p " + destinationPath);
        cliToolShell.execute("sudo -s chmod -R 777 " + destinationPath);
        if (HostConfigurator.isPhysicalEnvironment()) {
            final boolean isCopiedSuccessfully =
                    remoteObjectHandler.copyLocalFileToRemote(filesToCopy, destinationPath);
            log.info("Is it copied successfully on physical environment? : {}", isCopiedSuccessfully);
            assertThat("File not copied to " + destinationPath,
                    remoteObjectHandler.copyLocalFileToRemote(filesToCopy, destinationPath));
        } else {
            final boolean isCopiedSuccessfully =
                    remoteObjectHandler.copyLocalFileToRemoteWithSshKeyFile(filesToCopy,
                            destinationPath, getPemFileForCloud().getAbsolutePath());
            log.info("Is it copied successfully? : {}", isCopiedSuccessfully);
            assertThat("File not copied to " + destinationPath,
                    remoteObjectHandler.copyLocalFileToRemoteWithSshKeyFile(filesToCopy,
                            destinationPath, getPemFileForCloud().getAbsolutePath()));
        }
        cliToolShell.execute("cd " + destinationPath);
        cliToolShell.execute("openssl aes-256-cbc -in " + filesToCopy + " -out " + destinationFile + " -d -k " + myKey + " -md md5");
        final String fileContent = cliToolShell.execute("cat " + destinationFile).getOutput();
        log.info("The file content is: {}", fileContent);
        final List<String> neList = Arrays.asList(nes.split(","));
        final String[] fileLines = fileContent.split("\n");
        final List<String> neInFileList = new ArrayList<>();
            for (final String line : fileLines) {
                final String[] splitteLine = line.split(";");
                neInFileList.add(splitteLine[0]);
        }
        // the test step will fail in the case the export is not done due to an exception
        assertThat(neInFileList.containsAll(neList)).isTrue(); // qui not ok
        log.info("Now I'm removing the files");
        final String listDir = "ls -latr ";
        final String outputCheckFileIsPresent = cliToolShell.execute(listDir).getOutput();
        log.info("Checking the files to be removed are in the remote directory: {}", outputCheckFileIsPresent);
        cliToolShell.execute("cd ..");
        cliToolShell.execute("sudo -s rm -rf " + destinationPath).getOutput();
        final String checkFileIsRemoved = "ls -latr " + destinationPath;
        final String outputCommand = cliToolShell.execute(checkFileIsRemoved).getOutput();
        if (!outputCommand.contains("cannot access " + destinationPath + ": No such file or directory")) {
            log.error("Unable to remove files");
        }
        cliToolShell.close();
    }

    private File getPemFileForCloud() {
        final String hostname = getHostnameOfDeployment();
        final String contents = getPrivateKey(hostname);
        return writePrivateKeyToFile(hostname, contents);
    }


    private String appendNanoSecToAString(final String label) {
        final LocalTime localTime = LocalTime.now();
        final long nanoseconds = localTime.toNanoOfDay();
        final String str = String.valueOf(nanoseconds);
        final String jobUniqueName = label.concat(str);
        return jobUniqueName;
    }

    /*
     * Expample of response
     * [
     * {
     * "neAccounts": [],
     * "neName": "${nodes.radio.node2}",
     * "status": "NOT_MANAGED"
     * },
     * {
     * "neAccounts": [
     * {
     * "currentUser": "${nodes.radio.node1}",
     * "errorDetails": "null",
     * "id": "1",
     * "lastUpdate": "Wed Mar 29 10:17:15 IST 2023",
     * "neName": "${nodes.radio.node1}",
     * "status": "CONFIGURED"
     * }
     * ],
     * "neName": "${nodes.radio.node1}",
     * "status": "MANAGED"
     * }
     * ]
     */

    //Example of response
    /*
     * [{"neName":"${nodes.radio.node1}",
     * "neNpamStatus":"MANAGED",
     * "neAccounts":
     * [{"neName":"${nodes.radio.node1}",
     * "currentUser":"administrator",
     * "id":"1",
     * "errorDetails":"null",
     * "status":"DETACHED",
     * "lastUpdate":"23-04-16 09:51:59+0000"}]}]
     */
    private Map<String, NPamNEAccountResponse> neAccountParsing(final String response) {
        JSONObject neAccountElement = null;
        JSONArray neAccounts = null;
        final Map<String, NPamNEAccountResponse> map = new HashMap<>();
        // Complete object
        final JSONArray completeObj = new JSONArray(response);
        for (int j = 0; j < completeObj.length(); j++) {
            neAccountElement = completeObj.getJSONObject(j);
            neAccounts = neAccountElement.getJSONArray("neAccounts");
            // neName into main object is the same of the "neAccounts" ones
            if (neAccounts.length() > 0) {
                for (int i = 0; i < neAccounts.length(); i++) {
                    final NPamNEAccountResponse npamNeAccountResp = new NPamNEAccountResponse();
                    final String neName = neAccounts.getJSONObject(i).getString("neName");
                    npamNeAccountResp.setCurrentUser(neAccounts.getJSONObject(i).getString("currentUser"));
                    npamNeAccountResp.setId(neAccounts.getJSONObject(i).getInt("id"));
                    npamNeAccountResp.setNeName(neName);
                    npamNeAccountResp.setNeAccountStatus(neAccounts.getJSONObject(i).getString("status"));
                    // key node name
                    map.put(neName, npamNeAccountResp);
                }
            }

            NPamNEAccountResponse npamNeAccountResp =
                    // if neName key is not existing null is returned
            map.get(neAccountElement.getString("neName"));
            if (npamNeAccountResp != null) { // neAccount object is not null
               npamNeAccountResp.setNeNpamStatus(neAccountElement.getString("neNpamStatus"));
            } else {
                npamNeAccountResp = new NPamNEAccountResponse();
                npamNeAccountResp.setNeNpamStatus(neAccountElement.getString("neNpamStatus"));
                npamNeAccountResp.setNeName(neAccountElement.getString("neName"));
                map.put(neAccountElement.getString("neName"), npamNeAccountResp);
            }
        }
        return map;
    }

    public static final class StepIds {

        private StepIds() {
            throw new IllegalStateException("StepIds class");
        }

        public static final String CHECK_RESP_FOR_CREATE_ENABLE_REMOTE_MANAGEMENT_JOB =
                "Check response for enable remote management job creation";
        public static final String CHECK_RESP_FOR_CANCEL_ENABLE_REMOTE_MANAGEMENT_JOB =
                "Check response for cancel remote management job";
        public static final String CHECK_RESP_BODY_FOR_GET_ALL_JOBS = "Check response body for Get All Jobs";
        public static final String CHECK_RESP_BODY_FOR_GET_ALL_JOBS_WITH_RESULTS = "Check response body for Get All Jobs with results";
        public static final String CHECK_RESP_BODY_FOR_NE_ACCOUNT_WITH_RESULTS_POSITIVE = "Check response body for Ne Account with results";
        public static final String CHECK_RESP_BODY_FOR_NE_ACCOUNT_WITH_RESULTS_NEGATIVE = "Check response body for Ne Account for negative tests";
        public static final String CHECK_RESP_BODY_FOR_NE_ACCOUNT_FOR_SPECIFIC_NE = "Check response for Ne Account for a specific NE";
        public static final String CHECK_RESP_BODY_FOR_NE_ACCOUNT_FOR_SPECIFIC_NE_NEGATIVE =
                "Check response body for Ne Account for a specific NE for negative tests";
        public static final String CHECK_RESP_BODY_FOR_NE_ACCOUNT_FOR_SPECIFIC_NE_WITH_ID_AND_STATUS =
                "Check response for Ne account with Ids and status";
        public static final String CHECK_RESP_FOR_IMPORT_FILE = "Check response for Import file";
        public static final String TEST_STEP_WAIT_FOR_A_WHILE = "Wait for a while";
        public static final String CHECK_RESP_FOR_GET_NPAM_CONFIG = "Check response for get NpamConfig Enabled";
        public static final String CHECK_RESP_BODY_FOR_NE_ACCOUNT_WRONG_ACCESS_RIGHTS = "Check response for NeAccount for negative tests";
        public static final String CHECK_RESP_FOR_IMPORT_FILE_WRONG_ACCESS_RIGHTS = "Check response for Import file for negative tests";
        public static final String CHECK_RESP_FOR_GET_LIST_FILE_WRONG_ACCESS_RIGHTS = "Check response for get list file for negative tests";
        public static final String CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_ENABLED = "Check response for get list file with NpamConfig enabled";
        public static final String CREATE_NPAM_JOB_WITH_EXPECTED = "Check response for job creation for negative tests";
        public static final String CHECK_RESPONSE_NPAM_JOB_SPECIFIC_WITH_EXPECTED = "Check response for specific Npam job with expected";
        public static final String CHECK_RESP_FOR_GET_SPECIFIC_JOB_FROM_JOBS_LIST = "Check response for specific Npam job";
        public static final String CHECK_RESP_FOR_JOB_CONFIGURATION = "Check response for job configuration";
        public static final String CHECK_RESP_FOR_GET_SPECIFIC_JOB = "Get response for job based on jobInstanceId";
        public static final String CREATE_NPAM_JOB = "Get response for job creation";
        public static final String CHECK_RESP_FOR_UPDATE_NPAM_CONFIG = "Get response for npamConfig update";
        public static final String EXPORT_NE_ACCOUNT = "Export Ne accounts";
        public static final String COPY_EXPORT_FILE_TO_REMOTE = "Copy export file to remote";
        public static final String REMOVE_EXPORT_FILE_TO_REMOTE = "Remove export file from remote";

    }
}
