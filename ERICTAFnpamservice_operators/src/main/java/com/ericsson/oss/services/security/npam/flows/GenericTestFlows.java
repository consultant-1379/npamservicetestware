/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2023
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* ----------------------------------------------------------------------------
 */

package com.ericsson.oss.services.security.npam.flows;

import static com.ericsson.cifwk.taf.scenario.TestScenarios.annotatedMethod;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.dataSource;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.flow;
import static com.ericsson.oss.services.security.npam.data.TestwareConstants.VALID_WAIT_FILES;

import javax.inject.Inject;

import com.ericsson.cifwk.taf.scenario.api.TestStepFlowBuilder;
import com.ericsson.oss.services.security.npam.teststeps.NpamFunctionalTestSteps;

public class GenericTestFlows {
    @Inject
    private NpamFunctionalTestSteps npamFuncTestSteps;

    public TestStepFlowBuilder sleep(final String testDataSource) {
        return flow("Wait a while for custom roles creation until their usage")
                .addTestStep(annotatedMethod(npamFuncTestSteps, NpamFunctionalTestSteps.StepIds.TEST_STEP_WAIT_FOR_A_WHILE).alwaysRun())
                .withDataSources(dataSource(testDataSource).bindTo(VALID_WAIT_FILES));
    }

}
