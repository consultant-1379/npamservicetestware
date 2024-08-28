/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.security.npam.scenario;

import static com.ericsson.cifwk.taf.datasource.TafDataSources.copy;
import static com.ericsson.cifwk.taf.datasource.TafDataSources.fromTafDataProvider;
import static com.ericsson.cifwk.taf.datasource.TafDataSources.merge;
import static com.ericsson.cifwk.taf.datasource.TafDataSources.shared;
import static com.ericsson.cifwk.taf.scenario.TestScenarios.runner;
import static com.ericsson.cifwk.taf.scenario.api.DataDrivenTestScenarioBuilder.TEST_CASE_ID;
import static com.ericsson.cifwk.taf.scenario.api.DataDrivenTestScenarioBuilder.TEST_CASE_TITLE;
import static com.ericsson.oss.services.security.npam.constant.ProfilesConstants.NO_PROFILE;
import static com.ericsson.oss.services.security.npam.constant.ProfilesConstants.PROFILE_MAINTRACK;
import static com.ericsson.oss.services.security.npam.constant.ProfilesConstants.PROFILE_REAL_NODE;
import static com.ericsson.oss.services.security.npam.constant.ProfilesConstants.PROFILE_TDM_INFO;
import static com.ericsson.oss.testware.enmbase.data.CommonDataSources.NODES_TO_ADD;
import static com.google.common.collect.Iterables.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

import com.ericsson.cifwk.taf.TafTestContext;
import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.data.pool.DataPoolStrategy;
import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.ericsson.cifwk.taf.datasource.MapSource;
import com.ericsson.cifwk.taf.datasource.TafDataSourceFactory;
import com.ericsson.cifwk.taf.datasource.TafDataSources;
import com.ericsson.cifwk.taf.datasource.TestDataSource;
import com.ericsson.cifwk.taf.datasource.TestDataSourceFactory;
import com.ericsson.cifwk.taf.datasource.UnknownDataSourceTypeException;
import com.ericsson.cifwk.taf.scenario.TestScenarioRunner;
import com.ericsson.cifwk.taf.scenario.impl.LoggingSecurityScenarioListener;
import com.ericsson.oss.services.security.npam.datasource.UsersToCreateTimeStampDataSource;
import com.ericsson.oss.services.security.npam.predicate.PredicateUtil;
import com.ericsson.oss.testware.scenario.ScenarioUtilities;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * SetupAndTearDownUtil necessary operations that must be executed before and after every test suite.
 */
public abstract class SetupAndTearDownUtil extends ScenarioUtilities {

    public static final String SEPARATOR = System.lineSeparator();
    private static int numberOfUsers;
    private static int numberOfNodes;
    protected static ITestContext suiteContext;
    private static final String CLASS_FIELD = "class";
    private static final String LOGGER_INFO_PARAM_ONLY = "\n \t %s: PARAM = %s\n";
    private static final String SUITE_NAME = "suiteName";
    private static final String nodeTypes = "RadioNode";
    private static final Logger LOGGER = LoggerFactory.getLogger(SetupAndTearDownUtil.class);

    @Inject
    protected TestContext context;

    @Override
    protected String getSuiteName() {
        return getSuiteContext().getSuite().getName();
    }

    public static ITestContext getSuiteContext() {
        return suiteContext;
    }

    public static void setSuiteContext(final ITestContext suiteContextValue) {
        suiteContext = suiteContextValue;
    }

    /**
     * Returns the number of users.
     *
     * @return the number of users
     */
    public static int getNumberOfUsers() {
        return numberOfUsers;
    }

    /**
     * Set number of users.
     */
    public static void setNumberOfUsers(final int value) {
        numberOfUsers = value;
    }

    public static TestScenarioRunner getScenarioRunner() {
        return runner()
                .withListener(new LoggingSecurityScenarioListener()).build();
    }

    public static void removeAndCreateTestDataSource(final String dataSourceName, final Iterable<DataRecord> nodesFiltered) {
        TafTestContext.getContext().removeDataSource(dataSourceName);
        final Iterator<DataRecord> localNameIterator = nodesFiltered.iterator();
        while (localNameIterator.hasNext()) {
            final DataRecord node = localNameIterator.next();
            TafTestContext.getContext().dataSource(dataSourceName).addRecord().setFields(node);
        }
    }

    /**
     * <pre>
     * <b>Name</b>: reduceDataSource            <i>[public]</i>
     * <b>Description</b>: This static method is used to eliminate dataRecords that have values from the selected field repeated in the datasource..
     * </pre>
     *
     * @param originalDataSource - Input DataSource (Working DataSource)
     * @param checkingField      - field to use for filtering
     * @return - filtered DataSource.
     */
    public static TestDataSource<DataRecord> reduceDataSource(final TestDataSource<DataRecord> originalDataSource,
            final String checkingField) {
        final List<String> keywordListValues = new ArrayList<>();
        final List<Map<String, Object>> reorderedDataSource = Lists.newArrayList();
        final Iterator<DataRecord> originalDatasourceIterator = originalDataSource.iterator();
        while (originalDatasourceIterator.hasNext()) {
            final DataRecord originalRecord = originalDatasourceIterator.next();
            // Please review the logic here
            if (!keywordListValues.contains(originalRecord.getFieldValue(checkingField))) {
                keywordListValues.add((String) originalRecord.getFieldValue(checkingField));
                reorderedDataSource.add(originalRecord.getAllFields());
            }
        }
        return TestDataSourceFactory.createDataSource(reorderedDataSource);
    }

    /**
     * <pre>
     * Name: reorderDatasourceWithOriginAlone()       [public]
     * Description: This method could be used to reorder 'reorderingDataSource' with 'originalDataSource' sequence, using 'orderingKeyWord' keyword.
     *              It follows each DataRecord of first DataSource (originalDataSource) search in second DataSource (reorderingDataSource) a record
     *              with same 'orderingKeyWord' field and put this one in New DataSource (reorderedDataSource).
     * </pre>
     *
     * @param originalDataSource   DataSource from which the order of the records must be copied using the KeyWord #orderingKeyWord.
     * @param reorderingDataSource DataSource to reorder.
     * @param orderingKeyWord      Keyword to use for reordering (it must be unique)
     * @return Reordered DataSource
     */
    public static TestDataSource<DataRecord> reorderDatasourceWithOriginAlone(final TestDataSource<? extends DataRecord> originalDataSource,
            final TestDataSource<? extends DataRecord> reorderingDataSource, final String orderingKeyWord) {
        final List<Map<String, Object>> reorderedDataSource = Lists.newArrayList();
        final Iterator<? extends DataRecord> originalDatasourceIterator = originalDataSource.iterator();
        while (originalDatasourceIterator.hasNext()) {
            final DataRecord originalRecord = originalDatasourceIterator.next();
            final Iterator<? extends DataRecord> reorderingDatasourceIterator = reorderingDataSource.iterator();
            for (final DataRecord reorderingDataRecord : Lists.newArrayList(reorderingDatasourceIterator)) {
                if (reorderingDataRecord.getFieldValue(orderingKeyWord).equals(originalRecord.getFieldValue(orderingKeyWord))) {
                    reorderedDataSource.add(originalRecord.getAllFields());
                }
            }
        }
        return TestDataSourceFactory.createDataSource(reorderedDataSource);
    }

    public static void dataDrivenDataSource(final String dataSourceNew, final String testId, final String testName,
            final Iterable<? extends DataRecord> values) {
        TafTestContext.getContext().removeDataSource(dataSourceNew);
        for (final DataRecord value : values) {
            TafTestContext.getContext().dataSource(dataSourceNew).addRecord()
                    .setFields(value).setField(TEST_CASE_ID, testId)
                    .setField(TEST_CASE_TITLE, testName);
        }
        TafTestContext.getContext().addDataSource(dataSourceNew, shared(TafTestContext.getContext().dataSource(dataSourceNew)));
    }

    /**
     * Returns the number of nodes.
     *
     * @return the number of nodes
     */
    public static int getNumberOfNodes() {
        return numberOfNodes;
    }

    /**
     * Set number of nodes.
     */
    public static void setNumberOfNodes(final int value) {
        numberOfNodes = value;
    }

    static void printdataSource(final Logger log, final String tag, final Iterable<DataRecord> data) {
        log.info(String.format("%s \n %s Size=%s\n", tag, Iterables.size(data),
                Iterables.toString(data).replace(", Data value: ", ",\nData value: ")));

    }

    /**
     * Method to fill in users and nodes datasources depending on the current profile.
     */
    public void standardDataSourceFromProfileConfiguration(final String profile) {
        LOGGER.info("\nprofile.toLowerCase()[{}]\n", profile);
        //This switch is disabled, it can be used in case you want to create a management based on profiles
        switch (profile) {
            case PROFILE_MAINTRACK:
                createNodesToAdd(NODES_TO_ADD);
                break;
            case PROFILE_TDM_INFO:
            case PROFILE_REAL_NODE:
                createNodesToAdd(NODES_TO_ADD);
                break;
            case NO_PROFILE:
                createNodesToAdd(NODES_TO_ADD);
                break;
            default:
                createNodesToAdd(NODES_TO_ADD);
                break;
        }
        int numOfRowint = Iterables.size(context.dataSource(NODES_TO_ADD));
        numOfRowint = numOfRowint != 0 ? numOfRowint : 1;
        setNumberOfNodes(numOfRowint);
    }

    private void createNodesToAdd(final String dataSourceName) {
        LOGGER.info("DS name: {}", dataSourceName);

        Iterable<DataRecord> nodesFiltered;
        TestDataSource<DataRecord> file = copy(fromTafDataProvider(dataSourceName));
        printdataSource(LOGGER, "DUMP LOCAL FILE FULL", file);
//        /* MT FILE MERGE */
//        This code is to use in case you want use a local nodesToAdd.csv file, for istance for rear node
//        TestDataSource<DataRecord> nodesListReadFromDataProvider = null;
//
//        final HashMap<String, String> configuration = new HashMap<String, String>();
//        configuration.put(CLASS_FIELD, "com.ericsson.oss.testware.network.operators.netsim.NetsimDataProvider");
//        if (isRemoteCsv) {
//            LOGGER.info("Enter in remote CSV, suiteName {}:", "ESUM_LongLoop_Blue_Sky");
//            configuration.put("nodes.csv", "nodesToAdd.csv");
//            //in case you want to read the remote csv, replace the above line with the one commented below
//            //configuration.put("nodes.maintrack.id", "ESUM_LongLoop_Blue_Sky");
//            //The MT_CSV_FILE_URI is passed by maintrack
//            //configuration.put("MT_CSV_FILE_URI","https://arm1s11-eiffel004.eiffel.gic.ericsson.se:8443/nexus/content/sites/tor/enm-maintrack-central-test-datasource/17.6.9/maintrack/csv/nodeToAdd_15K.csv");
//        } else {
//            LOGGER.info("Enter in local CSV");
//            configuration.put("nodes.csv", "nodesToAdd.csv");
//        }
//
//        final MapSource configurationSource = new MapSource(configuration);
//        try {
//            nodesListReadFromDataProvider = TafDataSourceFactory.dataSourceOfType(CLASS_FIELD, configurationSource, DataPoolStrategy.STOP_ON_END);
//        } catch (final UnknownDataSourceTypeException e) {
//            e.printStackTrace();
//        }
//        file = merge(file, nodesListReadFromDataProvider);
//        printdataSource(LOGGER, "DUMP LOCAL FILE AFTER MT FILE MERGE", file);

        /* FILE FILTERED BY MVEL */
        LOGGER.info(String.format(LOGGER_INFO_PARAM_ONLY, "LOCAL FILE FILTERED BY MVEL", getFilterMval()));
        file = TafDataSources.filter(file, mvelFilter(getFilterMval()));
        Assertions.assertThat(0)
                .as("LOCAL FILE FILTERED BYLOCAL FILE FILTERED BY MVEL").isNotEqualTo(Iterables.size(file));
        printdataSource(LOGGER, "DUMP LOCAL FILE FILTERED BYLOCAL FILE FILTERED BY MVEL", file);
        /* FILE FILTERED BY NODETYPES */
        LOGGER.info(String.format(LOGGER_INFO_PARAM_ONLY, "LOCAL FILE FILTERED BY NODETYPES", nodeTypes));
        nodesFiltered = filter(file, correctNodeType(nodeTypes));
        Assertions.assertThat(0)
                .as("LOCAL FILE FILTERED BY NODETYPES EMPTY").isNotEqualTo(Iterables.size(nodesFiltered));
        printdataSource(LOGGER, "DUMP LOCAL FILE FILTERED BY NODETYPES", nodesFiltered);
        final int nodeSize = Iterables.size(nodesFiltered);
        printdataSource(LOGGER, "DUMP LOCAL FILE FILE WITH IP UPDATED", nodesFiltered);
        SetupAndTearDownUtil.removeAndCreateTestDataSource(NODES_TO_ADD, nodesFiltered);
        LOGGER.info("NUMBER OF NODES {}", nodeSize);
        System.setProperty(UsersToCreateTimeStampDataSource.NUM_OF_NODES, String.valueOf(nodeSize));
    }

    private Predicate<DataRecord> correctNodeType(final String nodeTypes) {
        LOGGER.info("{}SetupAndTeardownScenario correctNodeType{}", SEPARATOR, SEPARATOR);
        return PredicateUtil.multiValuesPredicate("nodeType", Arrays.asList(nodeTypes.split(",")));
    }

}
