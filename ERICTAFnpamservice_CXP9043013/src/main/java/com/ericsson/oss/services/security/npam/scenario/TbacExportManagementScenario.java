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
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.COPY_EXPORT_FILE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.COPY_EXPORT_FILE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_ENABLE_REMOTE_MAN_JOB_WITH_RES_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.EXPORT_NE_ACCOUNT_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.EXPORT_NE_ACCOUNT_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_ENABLE_REMOTE_MANAGEMENT_WITH_RES_TBAC_CSV;

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
import com.ericsson.oss.testware.security.gim.flows.UserManagementTestFlows;

public class TbacExportManagementScenario extends TafTestBase {

    private static final Logger log = LoggerFactory.getLogger(TbacExportManagementScenario.class);
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
    public void setupDataSourceForExport() {
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        context.addDataSource(CREATE_ENABLE_REMOTE_MAN_JOB_WITH_RES_DS,
                fromCsv(GET_RESPONSE_ENABLE_REMOTE_MANAGEMENT_WITH_RES_TBAC_CSV));

        final TestScenario scenario = scenario("Before Class to enable remote management for Ne account Scenario")
                .addFlow(npamFunctionalTestFlows.createGenericJob(CREATE_ENABLE_REMOTE_MAN_JOB_WITH_RES_DS))
                .build();
        start(scenario);
    }

    /*
     * TORF-647509_Rbac_Tbac_Export_NEAccounts with a user with custom role with <neaccout_export, execute> capability and a TG=TG1 and
     * execute the rest call to export the NEAccount specific for node in TG1 to a file
     */
    @Test(groups = { "RFA" })
    @TestSuite
    public void exportNeAccountWithTbac_CustomRoleAndTG() {
        context.addDataSource(EXPORT_NE_ACCOUNT_DS, fromCsv(EXPORT_NE_ACCOUNT_CSV));
        context.addDataSource(COPY_EXPORT_FILE_DS, fromCsv(COPY_EXPORT_FILE_CSV));
        final TestScenario scenario = dataDrivenScenario("Ne accounts export TBAC with custom role and TG=TG1 positive scenario")
                .withScenarioDataSources(dataSource(TEST_DS).withFilter(PredicateUtil.testCaseIdFilter("48")),
                        dataSource(COPY_EXPORT_FILE_DS),
                        dataSource(EXPORT_NE_ACCOUNT_DS).withFilter(PredicateUtil.neaccountExportRole()))
                .addFlow(npamFunctionalTestFlows.exportSpecificNeAccountData())
                .build();
        start(scenario);
    }

    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }

}
