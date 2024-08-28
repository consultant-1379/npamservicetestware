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
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_NPAM_CONFIG_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_NPAM_CONFIG_NEGATIVE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_NPAM_CONFIG_NEGATIVE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_NPAM_CONFIG_POSITIVE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_GET_NPAM_CONFIG_SECURITY_ADMIN_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_NEGATIVE_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_POSITIVE_DS;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SECURITY_ADMIN_CSV;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_WRONG_ACCESS_DS;
import static com.ericsson.oss.services.security.npam.scenario.SetupAndTearDownUtil.dataDrivenDataSource;

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
public class RbacNpamConfigScenario extends TafTestBase {

    private static final Logger log = LoggerFactory.getLogger(RbacNpamConfigScenario.class);
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

        context.addDataSource(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_POSITIVE_DS, fromCsv(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SECURITY_ADMIN_CSV));
        final String getNpamConfigUpdatePositiveDs = Iterables.toString(context.dataSource(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_POSITIVE_DS));
        log.debug("getNpamConfigUpdatePositiveDs datasource: {}", getNpamConfigUpdatePositiveDs);

        context.addDataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_POSITIVE_DS, fromCsv(CHECK_RESP_FOR_GET_NPAM_CONFIG_SECURITY_ADMIN_CSV));
        context.addDataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_NEGATIVE_DS, fromCsv(CHECK_RESP_FOR_GET_NPAM_CONFIG_NEGATIVE_CSV));
        context.addDataSource(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_WRONG_ACCESS_DS, fromCsv(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_NEGATIVE_CSV));
    }

    /*
     * AC4 - Get npamConfig with user with SECURITY_ADMIN role
     * AC5 - Update npamConfig with user with SECURITY_ADMIN role
     */
    @TestSuite
    @Test(priority = 1, groups = { "RFA" })
    public void updateNpamConfigPositive() {
        dataDrivenDataSource("updateNpamConfigPositive", "TORF-626610_Funct1",
                "TORF-626610_Funct1",
                Iterables.filter(context.dataSource(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_POSITIVE_DS), PredicateUtil.enabledContext1()));
        final String ds = Iterables.toString(context.dataSource("updateNpamConfigPositive"));

        dataDrivenDataSource("getNpamConfigPositive", "TORF-626610_Funct1",
                "TORF-626610_Funct1",
                Iterables.filter(context.dataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_POSITIVE_DS), PredicateUtil.enabledContext1()));
        final String ds2 = Iterables.toString(context.dataSource("getNpamConfigPositive"));

        log.debug("updateNpamConfigPositive datasource context1: {}", ds);
        log.debug("getNpamConfigPositive datasource context1: {}", ds2);
        context.addDataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_DS, fromCsv(CHECK_RESP_FOR_GET_NPAM_CONFIG_SECURITY_ADMIN_CSV));
        final TestScenario scenario = dataDrivenScenario("updateNpamConfig RBAC positive scenario AC3 e AC4")
                .addFlow(npamFunctionalTestFlows.updateNpamConfig()
                        .withDataSources(dataSource(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_POSITIVE_DS).withFilter(PredicateUtil.enabledContext1())))
                .withScenarioDataSources(dataSource("updateNpamConfigPositive"))
                .addFlow(npamFunctionalTestFlows.getNpamConfig()
                        .withDataSources(dataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_POSITIVE_DS).withFilter(PredicateUtil.enabledContext1())))
                .build();
        start(scenario);
    }

    /*
     * AC6 - Get npamConfig with user with NPAM_Administrator or Credm_Administrator role
     */
    @Test(priority = 2, groups = { "RFA" })
    @TestSuite
    public void getNpamConfigNegative() {
        dataDrivenDataSource("getNpamConfigNegative", "TORF-626610_Funct1",
                "TORF-626610_Funct1",
                Iterables.filter(context.dataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_NEGATIVE_DS), PredicateUtil.negativeContext()));
        final String ds = Iterables.toString(context.dataSource("getNpamConfigNegative"));
        log.debug("getNpamConfigNegative datasource negative context: {}", ds);
        context.addDataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_DS, fromCsv(CHECK_RESP_FOR_GET_NPAM_CONFIG_SECURITY_ADMIN_CSV));
        final TestScenario scenario = dataDrivenScenario("getNpamConfig RBAC negative scenario AC6")
                .addFlow(npamFunctionalTestFlows.getNpamConfig()
                        .withDataSources(dataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_NEGATIVE_DS).withFilter(PredicateUtil.negativeContext())))
                .withScenarioDataSources(dataSource("getNpamConfigNegative"))
                .build();
        start(scenario);
    }

    /*
     * AC2 - Update npamConfig with user with NPAM_Administrator role
     */
    @Test(priority = 3, groups = { "RFA" })
    @TestSuite
    public void updateNpamConfigNegative() {
        dataDrivenDataSource("updateNpamConfigNegative", "TORF-626610_Funct1",
                "TORF-626610_Funct1",
                Iterables.filter(context.dataSource(CHECK_RESP_FOR_GET_NPAM_CONFIG_NEGATIVE_DS), PredicateUtil.negativeContext()));
        final String ds = Iterables.toString(context.dataSource("getNpamConfigNegative"));
        log.debug("updateNpamConfigNegative datasource negative context: {}", ds);
        final TestScenario scenario = dataDrivenScenario("UpdateNpamConfig RBAC negative scenario AC2")
                .addFlow(npamFunctionalTestFlows.updateNpamConfig()
                        .withDataSources(dataSource(CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_WRONG_ACCESS_DS).withFilter(PredicateUtil.negativeContext())))
                .withScenarioDataSources(dataSource("updateNpamConfigNegative"))
                .build();
        start(scenario);
    }


    public static void start(final TestScenario scenario) {
        final TestScenarioRunner runner = runner().withListener(new LoggingScenarioListener()).build();
        runner.start(scenario);
    }
}
