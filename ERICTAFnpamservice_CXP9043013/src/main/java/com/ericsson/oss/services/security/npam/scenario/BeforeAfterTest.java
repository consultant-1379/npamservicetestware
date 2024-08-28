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
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.scenario;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_NPAM_CONFIG_SETUP_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_NPAM_CONFIG_SETUP_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SETUP_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SETUP_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CREATE_DISABLE_REMOTE_MAN_JOB_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CUSTOM_ROLES_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.GET_RESPONSE_DISABLE_REMOTE_MANAGEMENT_BEFORE_AFTER_TEST_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.VALID_WAIT_FILES;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.VALID_WAIT_FILES_CSV;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.ROLE_TO_CLEAN_UP;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.ROLE_TO_CREATE;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.SYNCED_NODES;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.TARGET_GROUP_TO_CREATE;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.TARGET_TO_ASSIGN;
import static com.google.common.truth.Truth.assertThat;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.configuration.TafConfiguration;
import com.ericsson.cifwk.taf.data.DataHandler;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.scenario.TestScenario;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import com.ericsson.cifwk.taf.scenario.api.TestScenarioBuilder;
import com.ericsson.cifwk.taf.scenario.impl.LoggingScenarioListener;
import com.ericsson.oss.services.security.npam.flows.GenericTestFlows;
import com.ericsson.oss.services.security.npam.flows.NpamFunctionalTestFlows;
import com.ericsson.oss.services.security.npam.flows.UtilityFlows;
import com.ericsson.oss.services.security.npam.predicate.PredicateUtility;
import com.ericsson.oss.testware.enmbase.data.CommonDataSources;
import com.ericsson.oss.testware.security.authentication.flows.LoginLogoutRestFlows;
import com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows;
import com.ericsson.oss.testware.security.gim.flows.GimCleanupFlows.EnmObjectType;
import com.ericsson.oss.testware.security.gim.flows.RoleManagementTestFlows;
import com.ericsson.oss.testware.security.gim.flows.TargetGroupManagementTestFlows;
import com.ericsson.oss.testware.security.gim.flows.UserManagementTestFlows;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

// Running in test suite
public class BeforeAfterTest extends SetupAndTearDownUtil {
    private static final String NODES_TO_ADD_CSV = "data/nodesToAdd.csv";
    private static final String NPAM_USERS_TO_CREATE_CSV = "data/npamUsersToCreate.csv";
    private static final String TARGET_GROUPS_CSV = "data/createTg.csv";
    private static final String TARGET_CSV = "data/targets.csv";
    private static final String NPAM_USERS_TO_UPDATE_CSV = "data/npamUsersToUpdate.csv";
    private static final String NPAM_USERS_TO_UNASSIGN_CSV = "data/npamUsersToUnassign.csv";
    private static final String TEST_DS_CSV = "data/testDataSource.csv";
    private static final String TARGET_GROUP_TO_CLEAN_UP = "targetGroupToCleanUp";
    private static final String TEST_DS = "testDataSource";

    @Inject
    private RoleManagementTestFlows roleManagementTestFlows;
    @Inject
    private GenericTestFlows genericTestFlows;
    @Inject
    private TestContext context;
    @Inject
    private GimCleanupFlows gimCleanupFlows;
    @Inject
    private UserManagementTestFlows userManagementTestFlows;
    @Inject
    private LoginLogoutRestFlows loginLogoutRestFlows;
    @Inject
    private TargetGroupManagementTestFlows targetGroupManagementTestFlows;
    @Inject
    private NpamFunctionalTestFlows npamFunctionalTestFlows;
    @Inject
    protected UtilityFlows utilityFlows;

    public Predicate<DataRecord> netSimTest() {
        return PredicateUtility.netsimNodePredicate();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(BeforeAfterTest.class);

    @BeforeSuite
    public void setUp(final ITestContext suiteContext) {
        LOGGER.info("Setup for Npam feature");
        onBeforeSuiteMethod(suiteContext);
        context.addDataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_SETUP_DS,
                fromCsv(CHECK_RESP_FOR_GET_NPAM_CONFIG_SETUP_CSV));
        context.addDataSource(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SETUP_DS,
                fromCsv(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SETUP_CSV));
        context.addDataSource(TEST_DS, fromCsv(TEST_DS_CSV));
        final TestScenario scenario = scenario("Before scenario - Setup")
                .addFlow(npamFunctionalTestFlows.updateNpamConfig()
                        .withDataSources(dataSource(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SETUP_DS)))
                .addFlow(npamFunctionalTestFlows.getNpamConfig()
                        .withDataSources(dataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_SETUP_DS)))
                .addFlow(loginLogoutRestFlows.logout())
                .withExceptionHandler(ScenarioExceptionHandler.PROPAGATE)
                .build();

        start(scenario);
    }

    @AfterSuite
    public void tearDown() throws InterruptedException {
        LOGGER.info("Teardown for Npam feature");
        context.addDataSource(ROLE_TO_CREATE, TafDataSources.shared(TafDataSources.fromCsv(CUSTOM_ROLES_CSV)));
        context.addDataSource(ROLE_TO_CLEAN_UP, TafDataSources.shared(TafDataSources.fromCsv(CUSTOM_ROLES_CSV)));
        context.addDataSource(CommonDataSources.USER_TO_CLEAN_UP, TafDataSources.shared(TafDataSources.fromCsv(NPAM_USERS_TO_CREATE_CSV)));
        context.addDataSource(CommonDataSources.ADDED_NODES, TafDataSources.fromCsv(NODES_TO_ADD_CSV));
        context.addDataSource(TARGET_GROUP_TO_CLEAN_UP, TafDataSources.shared(TafDataSources.fromCsv(TARGET_GROUPS_CSV)));
        context.addDataSource(CREATE_DISABLE_REMOTE_MAN_JOB_DS,
                fromCsv(GET_RESPONSE_DISABLE_REMOTE_MANAGEMENT_BEFORE_AFTER_TEST_CSV));
        final String disableRemoteManagemenDs = Iterables.toString(context.dataSource(CREATE_DISABLE_REMOTE_MAN_JOB_DS));
        LOGGER.info("DisableRemoteManagemenDs datasource: {}", disableRemoteManagemenDs);
        final TestScenario scenario = scenario("After scenario - Teardown")
                .addFlow(loginLogoutRestFlows.loginDefaultUser())
                .addFlow(npamFunctionalTestFlows.getSpecificNeJob(CREATE_DISABLE_REMOTE_MAN_JOB_DS))
                .addFlow(utilityFlows.deleteNodes(netSimTest(), 1)).alwaysRun()
                .addFlow(gimCleanupFlows.cleanUp(EnmObjectType.USER, EnmObjectType.ROLE, EnmObjectType.TARGET_GROUP)).alwaysRun()
                .addFlow(loginLogoutRestFlows.logout())
                .withExceptionHandler(ScenarioExceptionHandler.LOGONLY).build();

        start(scenario);
        context.removeDataSource(CommonDataSources.AVAILABLE_USERS);
    }


    /**
     * Sequence of operations to be performed before the execution of the scenario.
     */
    protected void onBeforeSuiteMethod(final ITestContext suiteContext) {
        setSuiteContext(suiteContext);
        LOGGER.info("onBeforeSuiteMethod START{}", SEPARATOR);

        // Fetch current profile
        final TafConfiguration tafConfiguration = DataHandler.getConfiguration();
        final String profile = tafConfiguration.getProperty("taf.profiles", "", String.class).toLowerCase();
        LOGGER.info("The test is running with profile: {}", profile);
        // Initialize DataSources
        standardDataSourceFromProfileConfiguration(profile);

        // BeforeSuiteScenarioBuilder
        final TestScenario scenario = beforeSuiteScenarioBuilder(profile).build();
        final TestScenarioRunner runner = SetupAndTearDownUtil.getScenarioRunner();
        runner.start(scenario);
        assertThat(Iterables.size(context.dataSource(SYNCED_NODES))).isNotEqualTo(0);
    }

    protected TestScenarioBuilder beforeSuiteScenarioBuilder(final String profile) {
        LOGGER.debug("beforeSuiteScenarioBuilder profile: {} numberOfNodes: {}", profile, getNumberOfNodes());
        context.addDataSource(TARGET_GROUP_TO_CREATE, TafDataSources.shared(TafDataSources.fromCsv(TARGET_GROUPS_CSV)));
        context.addDataSource(TARGET_TO_ASSIGN, TafDataSources.shared(TafDataSources.fromCsv(TARGET_CSV)));
        context.addDataSource(TARGET_GROUP_TO_CLEAN_UP, TafDataSources.shared(TafDataSources.fromCsv(TARGET_GROUPS_CSV)));
        context.addDataSource(ROLE_TO_CREATE, TafDataSources.shared(TafDataSources.fromCsv(CUSTOM_ROLES_CSV)));
        context.addDataSource(ROLE_TO_CLEAN_UP,
                TafDataSources.shared(TafDataSources.fromCsv(CUSTOM_ROLES_CSV)));
        context.addDataSource(VALID_WAIT_FILES, TafDataSources.fromCsv(VALID_WAIT_FILES_CSV));
        context.addDataSource(CommonDataSources.ADDED_NODES, TafDataSources.fromCsv(NODES_TO_ADD_CSV));
        context.addDataSource(CommonDataSources.USER_TO_CLEAN_UP, TafDataSources.shared(TafDataSources.fromCsv(NPAM_USERS_TO_CREATE_CSV)));
        context.addDataSource(CommonDataSources.USERS_TO_CREATE, TafDataSources.shared(TafDataSources.fromCsv(NPAM_USERS_TO_CREATE_CSV)));
        context.addDataSource(CommonDataSources.USER_TO_UNASSIGN, TafDataSources.shared(TafDataSources.fromCsv(NPAM_USERS_TO_UNASSIGN_CSV)));
        context.addDataSource(CommonDataSources.USERS_TO_UPDATE, TafDataSources.shared(TafDataSources.fromCsv(NPAM_USERS_TO_UPDATE_CSV)));
        return scenario("Before Suite Scenario ")
                .addFlow(gimCleanupFlows.cleanUp(EnmObjectType.USER, EnmObjectType.ROLE, EnmObjectType.TARGET_GROUP))
                .addFlow(roleManagementTestFlows.createRole())
                .addFlow(genericTestFlows.sleep(VALID_WAIT_FILES))
                .addFlow(userManagementTestFlows.createUser())
                .addFlow(utilityFlows.startNetsimNodes(netSimTest(), 1))
                .addFlow(loginLogoutRestFlows.loginDefaultUser())
                .addFlow(targetGroupManagementTestFlows.createTargetGroupBasic())
                .addFlow(utilityFlows.createNodes(netSimTest(), 1))
                .addFlow(utilityFlows.syncNodes(netSimTest(), 1))
                .addFlow(targetGroupManagementTestFlows.assignTargetsToTargetGroup())
                .addFlow(userManagementTestFlows.updateUserTBAC())
                .alwaysRun();
    }

    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }
}
