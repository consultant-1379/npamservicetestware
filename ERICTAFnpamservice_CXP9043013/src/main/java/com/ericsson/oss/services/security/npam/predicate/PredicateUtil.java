/*
 * ------------------------------------------------------------------------------
 * ******************************************************************************
 * COPYRIGHT Ericsson 2023
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 * ******************************************************************************
 * ----------------------------------------------------------------------------
 */

package com.ericsson.oss.services.security.npam.predicate;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.cifwk.taf.datasource.DataRecord;
import com.google.common.base.Predicate;

/**
 * PredicateUtil necessary operations that must be executed before and after every test suite.
 */
@SuppressWarnings({ "checkstyle:hideutilityclassconstructor", "PMD.UseSingleton", "PMD.LawOfDemeter", "PMD.UseUtilityClass" })
public class PredicateUtil {
    public static final String COLUMN_ADD_REM_NODES = "addRemoveNodes";
    public static final String COLUMN_NODETYPE = "nodeType";
    public static final String ROLES = "roles";
    public static final String NODE_OPER_TYPE = "nodeOperatorType";
    private static final String COLUMN_TYPE = "type";
    private static final String OR = "or";

    private static final Logger LOGGER = LoggerFactory.getLogger(PredicateUtil.class);

    public static Predicate<DataRecord> genericField(final String columnName, final String columnValue) {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord input) {
                if (input == null) {
                    return false;
                }
                final Object value = input.getFieldValue(columnName);
                if (value == null) {
                    return false;
                } else if (value instanceof String) {
                    return columnValue.equals(value);
                }
                return false;
            }

            @Override
            public boolean test(final DataRecord input) {
                if (input == null) {
                    return false;
                }
                final Object value = input.getFieldValue(columnName);
                if (value == null) {
                    return false;
                } else if (value instanceof String) {
                    return columnValue.equals(value);
                }
                return false;
            }
        };
    }

    /**
     * Predicate to filter a data source with respect to a single column values.
     *
     * @param columnName
     *            The column to look for
     * @param columnValue
     *            The value to filter
     * @param isIncluded
     *            Choose false to use the predicate to rule out the value
     * @return A {@link Predicate} to filter a datasource
     */
    public static Predicate<DataRecord> singleValuePredicate(final String columnName, final String columnValue, final boolean isIncluded) {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord input) {
                if (input == null || columnName == null) {
                    return true;
                }
                final Object value = input.getFieldValue(columnName);
                if (value == null) {
                    return true;
                } else if (value instanceof String) {
                    return columnValue.equals(value) && isIncluded;
                }
                return true;
            }

            @Override
            public boolean test(final DataRecord input) {
                if (input == null || columnName == null) {
                    return true;
                }
                final Object value = input.getFieldValue(columnName);
                if (value == null) {
                    return true;
                } else if (value instanceof String) {
                    return columnValue.equals(value) && isIncluded;
                }
                return true;
            }
        };
    }

    /**
     * Predicate to filter a data source with respect to a single column values.
     *
     * @param columnName
     *            The column to look for
     * @param columnValue
     *            The value to filter
     * @return A {@link Predicate} to filter a datasource
     */
    public static Predicate<DataRecord> singleValuePredicate(final String columnName, final String columnValue) {
        return singleValuePredicate(columnName, columnValue, true);
    }

    private static boolean findValue(final DataRecord input, final String columnName, final List<String> columnValues,
            final boolean forcedTrueNotFound) {
        boolean find = false;
        if (input == null || columnName == null || columnValues == null || columnValues.isEmpty()) {
            return find;
        }
        final Object value = input.getFieldValue(columnName);
        LOGGER.debug(String.format("findValue List %s value %s", value.toString(), columnValues.toString()));
        // FROM TDM (TDM BUG)
        if (value == null && forcedTrueNotFound) {
            find = true;
        } else if (value instanceof String) {
            if (!((String) value).contains(",")) {
                for (int i = 0; i < columnValues.size() && !find; i++) {
                    if (value.equals(columnValues.get(i))) {
                        find = true;
                    }
                }
            } else {
                final List<String> valueList = Arrays.asList(((String) value).split(","));
                for (int i = 0; i < valueList.size() && !find; i++) {
                    for (int ii = 0; ii < columnValues.size() && !find; ii++) {
                        if (valueList.get(i).equals(columnValues.get(ii))) {
                            find = true;
                        }
                    }
                }
            }
        } else if (value instanceof String[]) {
            final List<String> valueList = Arrays.asList((String[]) value);
            for (int i = 0; i < valueList.size() && !find; i++) {
                for (int ii = 0; ii < columnValues.size() && !find; ii++) {
                    if (valueList.get(i).equals(columnValues.get(ii))) {
                        find = true;
                    }
                }
            }
        }
        LOGGER.debug(String.format("findValue found %s", find));
        return find;
    }

    private static boolean findValueWithOr(final DataRecord input, final String columnName, final List<String> columnValues,
            final boolean forcedTrueNotFound) {
        boolean find = false;
        if (input == null || columnName == null || columnValues == null || columnValues.isEmpty()) {
            return find;
        }
        final Object value = input.getFieldValue(columnName);
        // FROM TDM (TDM BUG)
        if (value == null && forcedTrueNotFound) {
            find = true;
        } else if (value instanceof String) {
            final String valueString = (String) value;
            if (!valueString.contains(OR)) {
                for (int i = 0; i < columnValues.size() && !find; i++) {
                    final String elementList = columnValues.get(i);
                    if (!elementList.contains(OR)) {
                        if (valueString.equals(elementList)) {
                            find = true;
                        }
                    } else {
                        final List<String> valueList = Arrays.asList(elementList.replaceAll("'", "").split(OR));
                        for (int iii = 0; iii < valueList.size() && !find; iii++) {
                            if (valueString.equals(valueList.get(iii))) {
                                find = true;
                            }
                        }
                    }
                }
            } else {
                final List<String> valueList = Arrays.asList(((String) value).replaceAll("'", "").split(OR));
                for (int i = 0; i < valueList.size() && !find; i++) {
                    for (int ii = 0; ii < columnValues.size() && !find; ii++) {
                        if (valueList.get(i).equals(columnValues.get(ii))) {
                            find = true;
                        }
                    }
                }
            }
        } else if (value instanceof String[]) {
            final List<String> valueList = Arrays.asList((String[]) value);
            for (int i = 0; i < valueList.size() && !find; i++) {
                for (int ii = 0; ii < columnValues.size() && !find; ii++) {
                    if (valueList.get(i).equals(columnValues.get(ii))) {
                        find = true;
                    }
                }
            }
        }
        return find;
    }

    public static Predicate<DataRecord> multiValuesPredicateWithOr(final String columnName, final List<String> columnValues) {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord input) {
                return findValueWithOr(input, columnName, columnValues, false);
            }

            @Override
            public boolean test(final DataRecord input) {
                return findValueWithOr(input, columnName, columnValues, false);
            }
        };
    }

    public static Predicate<DataRecord> multiValuesPredicate(final String columnName, final List<String> columnValues) {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord input) {
                return findValue(input, columnName, columnValues, false);
            }

            @Override
            public boolean test(final DataRecord input) {
                return findValue(input, columnName, columnValues, false);
            }
        };
    }

    public static Predicate<DataRecord> multiValuesPredicate(final String columnName, final List<String> columnValues,
            final boolean forcedTrueNotFound) {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord input) {
                return findValue(input, columnName, columnValues, forcedTrueNotFound);
            }

            @Override
            public boolean test(final DataRecord input) {
                return findValue(input, columnName, columnValues, forcedTrueNotFound);
            }
        };
    }

    public static Predicate<DataRecord> contextFilter(final String i) {
        final Predicate<DataRecord> getIthRecord = new Predicate<DataRecord>() {
            @Override
            public boolean test(final DataRecord dataRecord) {
                final String context = dataRecord.getFieldValue("context");
                return context.equals(i);
            }

            @Override
            public boolean apply(final DataRecord dataRecord) {
                final String context = dataRecord.getFieldValue("context");
                return context.equals(i);
            }
        };
        return getIthRecord;
    }

    public static Predicate<DataRecord> testCaseIdFilter(final String i) {

        final Predicate<DataRecord> getIthRecord = new Predicate<DataRecord>() {
            @Override
            public boolean test(final DataRecord dataRecord) {
                final String testId = dataRecord.getFieldValue("testId");
                return testId.equals(i);
            }

            @Override
            public boolean apply(final DataRecord dataRecord) {
                final String testId = dataRecord.getFieldValue("testId");
                return testId.equals(i);
            }
        };
        return getIthRecord;
    }

    /**
     * nodeType is specifc predicate for the "nodeType" field.
     *
     * @param columnValue
     *            expected nodeType
     */
    public static Predicate<DataRecord> nodeType(final String columnValue) {
        return multiValuesPredicate(COLUMN_NODETYPE, Arrays.asList(columnValue));
    }

    /**
     * addRemNodes is specifc predicate for the "AddRemoveNodes" field.
     *
     * @param columnValue
     *            expected AddRemoveNodes
     */
    public static Predicate<DataRecord> addRemNodes(final String columnValue) {
        return genericField(COLUMN_ADD_REM_NODES, columnValue);
    }

    /**
     * subscriptionType is specifc predicate for the "type" field.
     *
     * @param columnValue
     *            expected AddRemoveNodes
     */
    public static Predicate<DataRecord> subscriptionType(final String columnValue) {
        return multiValuesPredicate(COLUMN_TYPE, Arrays.asList(columnValue));
    }

    public static Predicate<DataRecord> suiteNamePredicate(final String columnName, final String columnValues) {
        return new com.google.common.base.Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord input) {
                if (input == null || columnName == null) {
                    return true;
                }
                final Object value = input.getFieldValue(columnName);
                if (value == null) {
                    return true;
                } else if (value instanceof String) {
                    if (!((String) value).isEmpty()) {
                        if (((String) value).contains(",")) {
                            final String nodeTypeValue = (String) value;
                            final List<String> nodeTypeList = Arrays.asList(nodeTypeValue.split(","));
                            return nodeTypeList.contains(columnValues);
                        } else {
                            return columnValues.equals(value);
                        }
                    } else {
                        return true;
                    }
                }
                return true;
            }

            @Override
            public boolean test(final DataRecord input) {
                if (input == null || columnName == null) {
                    return true;
                }
                final Object value = input.getFieldValue(columnName);
                if (value == null) {
                    return true;
                } else if (value instanceof String) {
                    if (!((String) value).isEmpty()) {
                        if (((String) value).contains(",")) {
                            final String nodeTypeValue = (String) value;
                            final List<String> nodeTypeList = Arrays.asList(nodeTypeValue.split(","));
                            return nodeTypeList.contains(columnValues);
                        } else {
                            return columnValues.equals(value);
                        }
                    } else {
                        return true;
                    }
                }
                return true;
            }
        };
    }

    public static Predicate<DataRecord> userRolePredicate(final String columnName, final List<String> columnValues) {
        return new Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord input) {
                final boolean find = false;
                if (input == null || columnName == null || columnValues == null || columnValues.isEmpty()) {
                    return find;
                }
                final Object value = input.getFieldValue(columnName);
                // FROM TDM (TDM BUG)
                if (value instanceof String) {
                    for (int i = 0; i < columnValues.size(); i++) {
                        if (((String) value).contains(columnValues.get(i))) {
                            return true;
                        }
                    }
                }
                // FROM LOCAL CSV
                else if (value instanceof String[]) {
                    final List<String> valueValue = Arrays.asList((String[]) value);
                    for (int i = 0; i < valueValue.size(); i++) {
                        for (int ii = 0; ii < columnValues.size(); ii++) {
                            if (valueValue.get(i).equals(columnValues.get(ii))) {
                                return true;
                            }
                        }
                    }
                }
                return find;
            }

            @Override
            public boolean test(final DataRecord input) {
                final boolean find = false;
                if (input == null || columnName == null || columnValues == null || columnValues.isEmpty()) {
                    return find;
                }
                final Object value = input.getFieldValue(columnName);
                // FROM TDM (TDM BUG)
                if (value instanceof String) {
                    for (int i = 0; i < columnValues.size(); i++) {
                        if (((String) value).contains(columnValues.get(i))) {
                            return true;
                        }
                    }
                }
                // FROM LOCAL CSV
                else if (value instanceof String[]) {
                    final List<String> valueValue = Arrays.asList((String[]) value);
                    for (int i = 0; i < valueValue.size(); i++) {
                        for (int ii = 0; ii < columnValues.size(); ii++) {
                            if (valueValue.get(i).equals(columnValues.get(ii))) {
                                return true;
                            }
                        }
                    }
                }
                return find;
            }
        };
    }

    public static Predicate<DataRecord> genericPredicate(final String columnName, final List<String> columnValues) {
        return new com.google.common.base.Predicate<DataRecord>() {
            @Override
            public boolean apply(final DataRecord input) {
                final boolean find = false;
                if (input == null || columnName == null || columnValues == null || columnValues.isEmpty()) {
                    return find;
                }

                final Object value = input.getFieldValue(columnName);
                if (value instanceof String) {
                    if (!((String) value).contains(",")) {
                        for (int i = 0; i < columnValues.size(); i++) {
                            if (value.equals(columnValues.get(i))) {
                                return true;
                            }
                        }
                    } else {
                        final String[] valueList = ((String) value).split(",");
                        for (int ii = 0; ii < valueList.length; ii++) {
                            for (int i = 0; i < columnValues.size(); i++) {
                                if (valueList[ii].equals(columnValues.get(i))) {
                                    return true;
                                }
                            }
                        }
                    }
                } else if (value instanceof String[]) {
                    final String[] valueList = (String[]) value;
                    for (int ii = 0; ii < valueList.length; ii++) {
                        for (int i = 0; i < columnValues.size(); i++) {
                            if (valueList[ii].equals(columnValues.get(i))) {
                                return true;
                            }
                        }
                    }
                }
                return find;
            }

            @Override
            public boolean test(final DataRecord input) {
                final boolean find = false;
                if (input == null || columnName == null || columnValues == null || columnValues.isEmpty()) {
                    return find;
                }

                final Object value = input.getFieldValue(columnName);
                if (value instanceof String) {
                    if (!((String) value).contains(",")) {
                        for (int i = 0; i < columnValues.size(); i++) {
                            if (value.equals(columnValues.get(i))) {
                                return true;
                            }
                        }
                    } else {
                        final String[] valueList = ((String) value).split(",");
                        for (int ii = 0; ii < valueList.length; ii++) {
                            for (int i = 0; i < columnValues.size(); i++) {
                                if (valueList[ii].equals(columnValues.get(i))) {
                                    return true;
                                }
                            }
                        }
                    }
                } else if (value instanceof String[]) {
                    final String[] valueList = (String[]) value;
                    for (int ii = 0; ii < valueList.length; ii++) {
                        for (int i = 0; i < columnValues.size(); i++) {
                            if (valueList[ii].equals(columnValues.get(i))) {
                                return true;
                            }
                        }
                    }
                }
                return find;
            }
        };
    }

    public static Predicate<DataRecord> neaccountImportRole() {
        LOGGER.info("Custom role with <neaccount_import, execute> capability");
        return userRolePredicate("roles", Arrays.asList("BlueSky_neaccount_import_role"));
    }

    public static Predicate<DataRecord> neaccountImportQueryRole() {
        LOGGER.info("Custom role with <neaccount_import, query> capability");
        return userRolePredicate("roles", Arrays.asList("BlueSky_neaccount_import_query_role"));
    }

    public static Predicate<DataRecord> neaccountReadRole() {
        return userRolePredicate("roles", Arrays.asList("BlueSky_neaccount_read_role"));
    }

    public static Predicate<DataRecord> neaccountJobRole() {
        return userRolePredicate("roles", Arrays.asList("BlueSky_neaccount_job_role"));
    }

    public static Predicate<DataRecord> snmpV3Role() {
        return userRolePredicate("roles", Arrays.asList("BlueSky_snpmv3role"));
    }

    public static Predicate<DataRecord> neaccountJobReadRole(){
        return userRolePredicate("roles", Arrays.asList("BlueSky_neaccount_job_read_role"));
    }

    public static Predicate<DataRecord> neaccountExportRole() {
        return userRolePredicate("roles", Arrays.asList("BlueSky_neaccount_export_role"));
    }

    public static Predicate<DataRecord> administrator() {
        return userRolePredicate("roles", Arrays.asList("ADMINISTRATOR"));
    }

    public static Predicate<DataRecord> npamAdministrator() {
        return userRolePredicate("roles", Arrays.asList("NPAM_Administrator"));
    }

    public static Predicate<DataRecord> credmAdmin() {
        return userRolePredicate("roles", Arrays.asList("Credm_Administrator"));
    }

    // TO BE verified

    public static Predicate<DataRecord> enabledContext1() {
        return genericPredicate("context1", Arrays.asList("enabled"));
    }

    public static Predicate<DataRecord> disabledContext1() {
        return genericPredicate("context1", Arrays.asList("disabled"));
    }

    public static Predicate<DataRecord> positiveContext() {
        return genericPredicate("context", Arrays.asList("positive"));
    }

    public static Predicate<DataRecord> negativeContext() {
        return genericPredicate("context", Arrays.asList("negative"));
    }
}
