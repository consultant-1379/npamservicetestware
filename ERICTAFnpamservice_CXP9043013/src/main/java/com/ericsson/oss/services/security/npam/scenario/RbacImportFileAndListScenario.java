
package com.ericsson.oss.services.security.npam.scenario;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromCsv;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataDrivenScenario;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_RBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_LIST_FILE_NPAM_ONE_ROLE_RBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_GET_LIST_FILE_RBAC_NEGATIVE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_IMPORT_RBAC_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_IMPORT_RBAC_NEGATIVE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_BODY_FOR_IMPORT_WRONG_ROLE_JOB_DS;
import static com.ericsson.oss.services.security.npam.scenario.SetupAndTearDownUtil.dataDrivenDataSource;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.AVAILABLE_USERS;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ericsson.cifwk.taf.TafTestBase;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.annotations.TestSuite;
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.oss.services.security.npam.flows.NpamFunctionalTestFlows;
import com.ericsson.oss.services.security.npam.operators.HttpToolsOperatorImpl;
import com.ericsson.oss.services.security.npam.predicate.PredicateUtil;
import com.ericsson.oss.testware.enmbase.data.CommonDataSources;
import com.ericsson.oss.testware.security.authentication.flows.LoginLogoutRestFlows;
import com.google.common.collect.Iterables;

// Running in test suite
public class RbacImportFileAndListScenario extends TafTestBase {

    private static final Logger log = LoggerFactory.getLogger(RbacImportFileAndListScenario.class);
    private static final String TEST_DS = "testDataSource";
    private static final String TEST_DS_CSV = "data/testDataSource.csv";
    private static final String NPAM_USERS_TO_CREATE_CSV = "data/npamUsersToCreate.csv";

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
        context.addDataSource(CommonDataSources.AVAILABLE_USERS,
                TafDataSources.shared(TafDataSources.fromCsv(NPAM_USERS_TO_CREATE_CSV)));
    }
    /*
     * TORF-631941
     * AC1: Login as a user with ADMINISTRATOR role and execute the rest call for importing file
     * AC2: Execute the rest call for listing files
     */
    @Test(priority = 1, groups = { "RFA" })
    @TestSuite
    public void importFileNpamAndListFileWithAdministratorRole() {
        dataDrivenDataSource("importFileNpamAndListFileWithAdministratorRole", "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                Iterables.filter(context.dataSource(AVAILABLE_USERS), PredicateUtil.administrator()));
        final String ds = Iterables.toString(context.dataSource("importFileNpamAndListFileWithAdministratorRole"));
        log.debug("importFileNpamAndListFileWithAdministratorRole datasource: {}", ds);
        context.addDataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS, fromCsv(GET_RESPONSE_BODY_FOR_IMPORT_RBAC_CSV));
        context.addDataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS, fromCsv(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_RBAC_CSV));
        final TestScenario scenario = dataDrivenScenario("Import File RBAC positive scenario with Administrator Role")
                .addFlow(npamFunctionalTestFlows.importFile()
                        .withDataSources(dataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS).withFilter(PredicateUtil.administrator())))
                .addFlow(npamFunctionalTestFlows.getListFile()
                        .withDataSources(dataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS).withFilter(PredicateUtil.administrator())))
                .withScenarioDataSources(dataSource("importFileNpamAndListFileWithAdministratorRole"))
                .build();
        start(scenario);
    }

    /*
     * AC4 - Login as a user with NPAM_Administrator role and execute the rest call for importing file
     * AC5 - Execute the rest call for listing files
     * This test could fail as the the directory /ericsson/config_mgt/npam/import/ is not created yet in automatic way
     * Momentary wa to test the related test, create this directory under proper SG
     */
    @Test(priority = 2, groups = { "RFA" })
    @TestSuite
    public void importFileNpamAndListFileWithNPAM_AdministratorRole() {
        dataDrivenDataSource("importFileNpamAndListFileWithNPAM_AdministratorRole", "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                Iterables.filter(context.dataSource(AVAILABLE_USERS), PredicateUtil.npamAdministrator()));
        final String ds = Iterables.toString(context.dataSource("importFileNpamAndListFileWithNPAM_AdministratorRole"));
        log.debug("importFileNpamAndListFileWithNPAM_AdministratorRole datasource: {}", ds);
        context.addDataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS, fromCsv(GET_RESPONSE_BODY_FOR_IMPORT_RBAC_CSV));
        context.addDataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS, fromCsv(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_RBAC_CSV));
        final TestScenario scenario = dataDrivenScenario("Import File RBAC positive scenario with NPAM_Administrator Role")
                .addFlow(npamFunctionalTestFlows.importFile()
                        .withDataSources(dataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS).withFilter(PredicateUtil.npamAdministrator())))
                .addFlow(npamFunctionalTestFlows.getListFile()
                        .withDataSources(dataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS).withFilter(PredicateUtil.npamAdministrator())))
                .withScenarioDataSources(dataSource("importFileNpamAndListFileWithNPAM_AdministratorRole"))
                .build();
        start(scenario);
    }

    /*
     * AC.13 - Login as a user with custom role with only <neaccout_import, execute> and execute the rest call for importing file
     */
    @Test(priority = 3, groups = { "RFA" })
    @TestSuite
    public void importFileNpamWithNeAccountImportCustomRole() {
        dataDrivenDataSource("importFileNpamWithNeAccountImportCustomRole", "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                Iterables.filter(context.dataSource(AVAILABLE_USERS), PredicateUtil.neaccountImportRole()));
        final String ds = Iterables.toString(context.dataSource("importFileNpamWithNeAccountImportCustomRole"));
        log.debug("importFileNpamWithNeAccountImportCustomRole datasource: {}", ds);
        context.addDataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS, fromCsv(GET_RESPONSE_BODY_FOR_IMPORT_RBAC_CSV));
        final TestScenario scenario =
                dataDrivenScenario("Import File RBAC positive scenario with Custom Role with <neaccout_import, execute> capability")
                        .addFlow(npamFunctionalTestFlows.importFile()
                                .withDataSources(
                                        dataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS).withFilter(PredicateUtil.neaccountImportRole())))
                        .withScenarioDataSources(dataSource("importFileNpamWithNeAccountImportCustomRole"))
                        .build();
        start(scenario);
    }

    /*
     * AC.14 - Login as a user with custom role with only <neaccout_import, query> and execute the rest call for listing files
     * custom role filtering to be verified
     * custom role --> this was failing using the same csv of other ACs - a specific file has been passed then
     */
    @Test(priority = 4, groups = { "RFA" })
    @TestSuite
    public void getResponseForListFileWithCustomRole() {
        dataDrivenDataSource("getResponseForListFileWithCustomRole", "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                Iterables.filter(context.dataSource(AVAILABLE_USERS), PredicateUtil.neaccountImportQueryRole()));
        final String ds = Iterables.toString(context.dataSource("getResponseForListFileWithCustomRole"));
        log.debug("getResponseForListFileWithCustomRole datasource: {}", ds);
        context.addDataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS, fromCsv(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_ONE_ROLE_RBAC_CSV));
        final TestScenario scenario = dataDrivenScenario("Listing file RBAC positive scenario - custom role")
                .addFlow(npamFunctionalTestFlows.getListFile()
                        .withDataSources(
                                dataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS).withFilter(PredicateUtil.neaccountImportQueryRole())))
                .withScenarioDataSources(dataSource("getResponseForListFileWithCustomRole"))
                .build();
        start(scenario);
    }

    /*
     * /*
     * AC7 - Login as a user with CredM_Administrator role and execute the rest call for importing file - Security Violation*
     */
    @Test(priority = 5, groups = { "RFA" })
    @TestSuite
    public void getResponseForImportingFileWithWrongRole() {
        dataDrivenDataSource("getResponseForImportingFileWithWrongRole", "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                Iterables.filter(context.dataSource(AVAILABLE_USERS), PredicateUtil.credmAdmin()));
        final String ds = Iterables.toString(context.dataSource("getResponseForImportingFileWithWrongRole"));
        log.debug("getResponseForImportingFileWithWrongRole datasource: {}", ds);
        context.addDataSource(GET_RESPONSE_BODY_FOR_IMPORT_WRONG_ROLE_JOB_DS, fromCsv(GET_RESPONSE_BODY_FOR_IMPORT_RBAC_NEGATIVE_CSV));
        final TestScenario scenario = dataDrivenScenario("Listing file RBAC positive scenario- custom role")
                .addFlow(npamFunctionalTestFlows.importFileWithWrongAccessRights()
                        .withDataSources(
                                dataSource(GET_RESPONSE_BODY_FOR_IMPORT_WRONG_ROLE_JOB_DS).withFilter(PredicateUtil.credmAdmin())))
                .withScenarioDataSources(dataSource("getResponseForImportingFileWithWrongRole"))
                .build();
        start(scenario);
    }

    /*
     * AC7bis - Login as a user with CredM_Administrator role and execute rest call listing file - Security Violation
     */
    @Test(priority = 6, groups = { "RFA" })
    @TestSuite
    public void getResponseForListingFileWithWrongRole() {
        dataDrivenDataSource("getResponseForListingFileWithWrongRole", "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                Iterables.filter(context.dataSource(AVAILABLE_USERS), PredicateUtil.credmAdmin()));
        final String ds = Iterables.toString(context.dataSource("getResponseForListingFileWithWrongRole"));
        log.info("getResponseForListingFileWithWrongRole datasource: {}", ds);
        context.addDataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS, fromCsv(GET_RESPONSE_BODY_FOR_GET_LIST_FILE_RBAC_NEGATIVE_CSV));
        final TestScenario scenario = dataDrivenScenario("Listing file RBAC negative scenario")
                .addFlow(npamFunctionalTestFlows.getListFileWithWrongAccessRights()
                        .withDataSources(
                                dataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS).withFilter(PredicateUtil.credmAdmin())))
                .withScenarioDataSources(dataSource("getResponseForListingFileWithWrongRole"))

                .build();
        start(scenario);
    }

    /*
     * AC.10 - Login as a user with custom role without <neaccout_import, execute> and execute the rest call for importing file
     * Security violation
     */
    @Test(priority = 7, groups = { "RFA" })
    @TestSuite
    public void getResponseForImportingFileWithWrongCustomRole() {
        dataDrivenDataSource("getResponseForImportingFileWithWrongCustomRole", "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                Iterables.filter(context.dataSource(AVAILABLE_USERS), PredicateUtil.neaccountReadRole()));
        final String ds = Iterables.toString(context.dataSource("getResponseForImportingFileWithWrongCustomRole"));
        log.info("getResponseForImportingFileWithWrongCustomRole datasource: {}", ds);
        context.addDataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS, fromCsv(GET_RESPONSE_BODY_FOR_IMPORT_RBAC_NEGATIVE_CSV));
        final TestScenario scenario = dataDrivenScenario("Importing RBAC negative scenario")
                .addFlow(npamFunctionalTestFlows.importFileWithWrongAccessRights()
                        .withDataSources(dataSource(GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS).withFilter(PredicateUtil.neaccountReadRole())))
                .withScenarioDataSources(dataSource("getResponseForImportingFileWithWrongCustomRole"))
                .build();
        start(scenario);
    }

    /*
     * AC.11 - Login as a user with custom role without <neaccout_import, query> and execute the rest call for listing files
     * Security violation
     */
    @Test(priority = 8, groups = { "RFA" })
    @TestSuite
    public void getResponseForListFileWithWrongCustomRole() {
        dataDrivenDataSource("getResponseForListFileWithWrongCustomRole", "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                "TORF-631941_RBAC_for_import_file_and_list_imported_file",
                Iterables.filter(context.dataSource(AVAILABLE_USERS), PredicateUtil.snmpV3Role()));
        final String ds = Iterables.toString(context.dataSource("getResponseForListFileWithWrongCustomRole"));
        log.info("getResponseForListFileWithWrongCustomRole datasource: {}", ds);
        context.addDataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS, fromCsv(GET_RESPONSE_BODY_FOR_GET_LIST_FILE_RBAC_NEGATIVE_CSV));
        final TestScenario scenario = dataDrivenScenario("Listing file RBAC negative scenario - custom role")
                .addFlow(npamFunctionalTestFlows.getListFileWithWrongAccessRights()
                        .withDataSources(dataSource(CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS).withFilter(PredicateUtil.snmpV3Role())))
                .withScenarioDataSources(dataSource("getResponseForListFileWithWrongCustomRole"))
                .build();
        start(scenario);
    }

    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }

}
