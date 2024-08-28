/*
 * *******************************************************************************
 * * COPYRIGHT Ericsson 2023
 * *
 * * The copyright to the computer program(s) herein is the property of
 * * Ericsson Inc. The programs may be used and/or copied only with written
 * * permission from Ericsson Inc. or in accordance with the terms and
 * * conditions stipulated in the agreement/contract under which the
 * * program(s) have been supplied.
 * *******************************************************************************
 */

package com.ericsson.oss.services.security.npam.flows;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.shareDataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.resetDataSource;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.ADDED_NODES;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.AVAILABLE_USERS;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.NODES_TO_ADD;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.SYNCED_NODES;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.USERS_TO_DELETE;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.scenario.TestStepFlow;
import com.ericsson.cifwk.taf.scenario.api.ScenarioExceptionHandler;
import com.ericsson.cifwk.taf.scenario.api.TestStepFlowBuilder;
import com.ericsson.oss.testware.network.teststeps.NetworkElementTestSteps;
import com.ericsson.oss.testware.network.teststeps.NetworkElementTestSteps.StepIds;
import com.ericsson.oss.testware.nodeintegration.flows.NodeIntegrationFlows;
import com.ericsson.oss.testware.security.authentication.flows.LoginLogoutRestFlows;
import com.ericsson.oss.testware.security.gim.flows.UserManagementTestFlows;
import com.google.common.base.Predicate;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Flows for common utilities.
 */
@SuppressFBWarnings
@SuppressWarnings("PMD.DoNotUseThreads")
public class UtilityFlows {

    private static final Logger log = LoggerFactory.getLogger(UtilityFlows.class);
    @Inject
    private UserManagementTestFlows userManagementTestFlows;
    @Inject
    private LoginLogoutRestFlows loginLogoutRestFlows;
    @Inject
    private NetworkElementTestSteps networkElementTestSteps;
    @Inject
    private NodeIntegrationFlows nodeIntegrationFlows;

    /**
     * Adds the datasource for 'delete users' operation.
     *
     * @return runnable
     */
    public static Runnable beforeDeleteUsers() {
        return new Runnable() {
            @Override
            public void run() {
                final TestContext context = TafTestContext.getContext();
                context.removeDataSource(USERS_TO_DELETE);
                context.addDataSource(USERS_TO_DELETE, context.dataSource(AVAILABLE_USERS));
            }
        };
    }

    /**
     * Flow for user creation.
     *
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlowBuilder createUsers(final int vUser) {
        return flow("Create Users").addSubFlow(userManagementTestFlows.createUser()).withVusers(vUser);
    }

    /**
     * Flow for user deletion.
     *
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlowBuilder deleteUsers(final int vUser) {
        return flow("Delete Users").beforeFlow(beforeDeleteUsers()).addSubFlow(userManagementTestFlows.deleteUser()).withVusers(vUser);
    }

    /**
     * Flow for login user with filter.
     *
     * @param user
     *            the filter
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlow login(final Predicate user, final int vUser) {
        return loginLogoutRestFlows.loginBuilder().beforeFlow(resetDataSource(AVAILABLE_USERS)).beforeFlow(shareDataSource(AVAILABLE_USERS))
                .withDataSources(dataSource(AVAILABLE_USERS).withFilter(user)).withVusers(vUser).build();
    }

    /**
     * Flow for login user with filter.
     *
     * @param user
     *            the filter
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlow logout(final Predicate user, final int vUser) {
        return loginLogoutRestFlows.logoutBuilder().beforeFlow(resetDataSource(AVAILABLE_USERS)).beforeFlow(shareDataSource(AVAILABLE_USERS))
                .withDataSources(dataSource(AVAILABLE_USERS).withFilter(user)).withVusers(vUser).build();
    }

    /**
     * Flow for starting netsim nodes after creation.
     *
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlow startNetsimNodes(final Predicate user, final int vUser) {
        return flow("Start Netsim Nodes").beforeFlow(shareDataSource(NODES_TO_ADD)).beforeFlow(resetDataSource(NODES_TO_ADD))
                .addTestStep(annotatedMethod(networkElementTestSteps, StepIds.START_NODE))
                .withDataSources(dataSource(NODES_TO_ADD).withFilter(user).allowEmpty()).withExceptionHandler(ScenarioExceptionHandler.LOGONLY)
                .withVusers(vUser).build();
    }

    /**
     * Flow for creating nodes.
     *
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlow createNodes(final Predicate user, final int vUser) {
        return nodeIntegrationFlows.addNodeBuilder().beforeFlow(shareDataSource(NODES_TO_ADD)).beforeFlow(resetDataSource(NODES_TO_ADD))
                .withDataSources(dataSource(NODES_TO_ADD).withFilter(user).allowEmpty()).withExceptionHandler(ScenarioExceptionHandler.LOGONLY)
                .withVusers(vUser).build();
    }

    /**
     * Flow for synch nodes.
     *
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlow syncNodes(final Predicate user, final int vUser) {
        return nodeIntegrationFlows.syncNodeBuilder().beforeFlow(shareDataSource(NODES_TO_ADD)).beforeFlow(resetDataSource(NODES_TO_ADD))
                .afterFlow(shareDataSource(SYNCED_NODES)).afterFlow(resetDataSource(SYNCED_NODES))
                .withDataSources(dataSource(NODES_TO_ADD).withFilter(user).allowEmpty()).withExceptionHandler(ScenarioExceptionHandler.LOGONLY)
                .withVusers(vUser).build();
    }

    /**
     * Flow for synch nodes.
     *
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlow verifySyncNodes(final int vUser) {
        return nodeIntegrationFlows.verifySynchNodeBuilder().beforeFlow(shareDataSource(ADDED_NODES)).beforeFlow(resetDataSource(ADDED_NODES))
                .withDataSources(dataSource(ADDED_NODES)).withVusers(vUser).build();
    }

    /**
     * Flow for delete nodes.
     *
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlow deleteNodes(final Predicate user, final int vUser) {
        return nodeIntegrationFlows.deleteNodeBuilder().beforeFlow(shareDataSource(ADDED_NODES)).beforeFlow(resetDataSource(ADDED_NODES))
                .withDataSources(dataSource(ADDED_NODES).withFilter(user).allowEmpty()).withVusers(vUser).build();
    }

    /**
     * Test step for user update for TBAC.
     *
     * @param vUser
     *            the user
     * @return a TestStepFlow
     */
    public TestStepFlow updateUsersForTbac(final int vUser) {
        return flow("Update Users For TBAC").addSubFlow(userManagementTestFlows.updateUserTBAC()).withVusers(vUser).build();
    }
}
