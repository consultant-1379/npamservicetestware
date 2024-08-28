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

package com.ericsson.oss.services.security.npam.data;

public class TestwareConstants {

    // Miscellaneous
    public static final String GET_RESPONSE_FOR_CREATE_JOB_WITH_EXPECTED_DS = "dataSourceForCreateJobWithExpected";
    public static final String GET_RESPONSE_FOR_GET_NOT_EXITING_JOB_DS = "dataSourceForGetNotExistingJob";
    public static final String VALID_WAIT_FILES = "validWaitFiles";

    public static final String CSV_CREATE_USER = "data/usersToCreate_Desp.csv";
    public static final String CUSTOM_ROLES_CSV = "data/Roles.csv";
    public static final String CSV_CREATE_FULL_USER = "data/usersToCreate_DespFull.csv";
    public static final String VALID_WAIT_FILES_CSV = "data/validWaitFiles.csv";
    public static final String VALID_WAIT_FILES_SETUP_CSV = "data/validWaitSetupFiles.csv";

    // JOB details management
    public static final String GET_RESPONSE_FOR_CREATE_JOB_DS = "dataSourceForCreateJob";
    public static final String GET_RESPONSE_FOR_GET_ALL_JOBS_DS = "datasourceForGetAllJobs";
    public static final String GET_RESPONSE_FOR_GET_SPECIFIC_JOB_DS = "datasourceForGetSpecificJob";

    public static final String GET_RESPONSE_FOR_GET_ALL_JOBS_CSV = "data/jobDetails/getAllJobs.csv";
    public static final String GET_RESPONSE_FOR_GET_ALL_JOBS_RBAC_CSV = "data/jobDetails/getAllJobsRbac.csv";
    public static final String GET_RESPONSE_FOR_GET_ALL_JOBS_RBAC_NEGATIVE_CSV = "data/jobDetails/getAllJobsNegativeRbac.csv";
    public static final String GET_RESPONSE_FOR_GET_NOT_EXISTING_JOB_CSV = "data/jobDetails/getNotExistingJob.csv";
    public static final String GET_RESPONSE_FOR_GET_SPECIFIC_JOB_CSV = "data/jobDetails/getSpecificJob.csv";

    // ENABLE DISABLE REMOTE MANAGEMENT
    public static final String CREATE_DISABLE_REMOTE_MAN_JOB_DS = "dataSourceDisableRemoteManagement";
    public static final String CREATE_DISABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_DS = "dataSourceDisableRemoteManagementNegativeWithExpected";
    public static final String CREATE_ENABLE_REMOTE_MAN_JOB_DS = "dataSourceEnableRemoteManagement";
    public static final String CREATE_ENABLE_REMOTE_MAN_JOB_NEGATIVE_DS = "dataSourceEnableRemoteManagementNegative";
    public static final String CREATE_ENABLE_REMOTE_MAN_JOB_WITH_RES_DS = "dataSourceEnableRemoteManagementWithResults";
    public static final String CREATE_ENABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_DS =
            "dataSourceEnableRemoteManagementNegativeWithExpected";
    public static final String CANCEL_ENABLE_REMOTE_MANAGEMENT_JOB_DS = "datasourceCancelEnableRemoteManagementJob";
    public static final String GET_RESPONSE_ENABLE_REMOTE_MANAGEMENT_WITH_RES_CSV = "data/remoteManagement/createEnableRemoteJobwithResults.csv";
    public static final String GET_RESPONSE_ENABLE_REMOTE_MANAGEMENT_WITH_RES_TBAC_CSV =
            "data/remoteManagement/createEnableRemoteJobwithResultsTbac.csv";
    public static final String GET_RESPONSE_DISABLE_REMOTE_MANAGEMENT_CSV = "data/remoteManagement/getDisableRemoteManagement.csv";
    public static final String GET_RESPONSE_DISABLE_REMOTE_MANAGEMENT_BEFORE_AFTER_TEST_CSV =
            "data/remoteManagement/getDisableRemoteManagementBeforeAfterTest.csv";
    public static final String GET_RESPONSE_ENABLE_REMOTE_MANAGEMENT_CSV = "data/remoteManagement/getEnableRemoteManagement.csv";
    public static final String GET_RESPONSE_FOR_DISABLE_REMOTE_MAN_JOB_CREATE_WITH_EXPECTED_CSV =
            "data/remoteManagement/disableRemoteManagementCreateJobWithExpected.csv";
    public static final String CHECK_RESP_JOB_ENABLE_REMOTE_MANAGEMENT_NEGATIVE_CSV = "data/remoteManagement/createRemoteManagementNegative.csv";
    public static final String ENABLE_REMOTE_MAN_JOB_NEGATIVE_WITH_EXPECTED_CSV = "data/remoteManagement/createRbacNegative.csv";
    public static final String GET_RESPONSE_BODY_FOR_CANCEL_JOB_CSV = "data/remoteManagement/cancelJob.csv";
    public static final String GET_RESPONSE_FOR_CREATE_DISABLE_REMOTE_MANAGEMENT_JOB_CSV =
            "data/remoteManagement/disableRemoteManagementCreateJob.csv";
    public static final String GET_RESPONSE_FOR_CREATE_JOB_ENABLE_REMOTE_MANAGEMENT_CSV = "data/remoteManagement/enableRemoteManagementCreateJob.csv";
    public static final String GET_RESPONSE_FOR_CREATE_JOB_ENABLE_REMOTE_MANAGEMENT_TBAC_CSV =
            "data/remoteManagement/enableRemoteManagementCreateJobTbac.csv";
    public static final String GET_RESPONSE_FOR_CREATE_JOB_DISABLE_REMOTE_MANAGEMENT_TBAC_CSV =
            "data/remoteManagement/getDisableRemoteManagementTbac.csv";
    public static final String GET_RESPONSE_FOR_CREATE_ENABLE_REMOTE_MANAGEMENT_JOB_RBAC_CSV = "data/remoteManagement/createEnableRemoteRbac.csv";

    // UPDATE_PSWD
    public static final String UPDATE_PSWD_JOB_NEGATIVE_WITH_EXPECTED_DS = "dataSourceForUpdatePswdNegativeWithExpected";
    public static final String UPDATE_PASSWORD_RBAC_DS = "dataSourceForCreateJobUpdatePswdRbac";
    public static final String UPDATE_PSWD_DS = "dataSourceUpdatePassword";

    public static final String GET_RESPONSE_FOR_CREATE_UPDATE_PASSWORD_JOB_RBAC_CSV = "data/updatePswd/createUpdatePswdRbac.csv";
    public static final String GET_RESPONSE_FOR_CREATE_JOB_UPDATE_PSWD_CSV = "data/updatePswd/createUpdatePswdJob.csv";
    public static final String GET_RESPONSE_FOR_CREATE_JOB_UPDATE_PSWD_CHANGED_CSV = "data/updatePswd/createUpdatePswdJobChanged.csv";
    public static final String GET_RESPONSE_FOR_UPDATE_PSWD_JOB_CREATE_WITH_EXPECTED_CSV = "data/updatePswd/updatePswdCreateJobWithExpected.csv";

    // UPDATE_WITH_AUTO_GENERATED_PSWD
    public static final String UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_DS = "dataSourceUpdateWithAutoGeneratedPassword";
    public static final String GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_DS =
            "datasourceForGetResponseBodyForUpdateWithAutoGeneratedPswd";

    public static final String GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_CSV = "data/autoGenPswd/updateWithAutoGeneratedPswd.csv";
    public static final String GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_AUTO_GENERATED_PSWD_SCHED_CSV =
            "data/autoGenPswd/updateWithAutoGeneratedPswdScheduled.csv";
    public static final String GET_RESPONSE_FOR_CREATE_JOB_UPDATE_WITH_AUTO_GENERATED_PSWD_NOT_SCHED_CSV =
            "data/autoGenPswd/updateWithAutoGeneratedPswdNotScheduled.csv";

    // UPDATE_WITH_FILE
    public static final String UPDATE_WITH_FILE_DS = "dataSourceForCreateJobUpdateWithFile";

    public static final String GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_FILE_JOB_CSV = "data/updateWithFile/updateWithFileJob.csv";
    public static final String GET_RESPONSE_BODY_FOR_CREATE_UPDATE_WITH_FILE_CHANGED_JOB_CSV = "data/updateWithFile/updateWithFileJobChanged.csv";

    // LIST FILES
    public static final String CHECK_RESP_FOR_GET_LIST_FILE_AFTER_IMPORT_ADMIN_DS = "dataSourceForGetListFileWithAdministrator";
    public static final String CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_DS = "dataSourceForGetListFile";

    public static final String CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_CSV = "data/listFile/getListFile.csv";
    public static final String CHECK_RESP_FOR_GET_LIST_FILE_CHANGED_NPAM_CONFIG_CSV = "data/listFile/getListFileChanged.csv";
    public static final String CHECK_RESP_FOR_GET_LIST_FILE_NPAM_CONFIG_RBAC_CSV = "data/listFile/getListFileRbac.csv";
    public static final String CHECK_RESP_FOR_GET_LIST_FILE_NPAM_ONE_ROLE_RBAC_CSV = "data/listFile/getListFileRbacOnlyRoleNotFiltering.csv";
    public static final String GET_RESPONSE_BODY_FOR_GET_LIST_FILE_RBAC_NEGATIVE_CSV = "data/listFile/getListFileRbacNegative.csv";

    // NE_ACCOUNT management
    public static final String GET_SPECIFIC_NE_ACCOUNT_AC3_DS = "dataSourceNeAccountAC3";
    public static final String GET_SPECIFIC_NE_ACCOUNT_AC2_DS = "dataSourceNeAccountAC2";
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_FAKE_DS = "dataSourceNeAccountFake";
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_DS = "dataSourceNeAccountIdsStatus";

    // JOB CONFIG
    public static final String GET_RESPONSE_FOR_JOB_CONFIGURATION_DS = "dataSourceforJobConfiguration";
    public static final String GET_RESPONSE_FOR_JOB_CONFIGURATION_CSV = "data/jobConfiguration.csv";

    // NPAM_CONFIG
    public static final String CHECK_RESP_FOR_GET_NPAM_CONFIG_POSITIVE_DS = "dataSourceForGetNpamConfigPositive";
    public static final String CHECK_RESP_FOR_GET_NPAM_CONFIG_NEGATIVE_DS = "dataSourceForGetNpamConfigNegative";
    public static final String CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_POSITIVE_DS = "dataSourceForUpdateNpamConfigPositive";
    public static final String CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_NEGATIVE_DS = "dataSourceForUpdateNpamConfigNegative";
    public static final String CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_WRONG_ACCESS_DS = "dataSourceForUpdateNpamConfigWrongAccess";
    public static final String CHECK_RESP_FOR_GET_NPAM_CONFIG_DS = "dataSourceForGetNpamConfig";
    public static final String CHECK_RESP_FOR_GET_NPAM_CONFIG_SETUP_DS = "dataSourceForGetNpamConfigForSetup";
    public static final String CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SETUP_DS = "dataSourceForUpdateNpamConfigForSetup";
    public static final String CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_DS = "dataSourceForUpdateNpamConfig";

    public static final String CHECK_RESP_FOR_GET_NPAM_CONFIG_SECURITY_ADMIN_CSV = "data/npamConfig/getNpamConfigSecurityAdmin.csv";
    public static final String CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SECURITY_ADMIN_CSV = "data/npamConfig/updateNpamConfigSecurityAdmin.csv";
    public static final String CHECK_RESP_FOR_GET_NPAM_CONFIG_NEGATIVE_CSV = "data/npamConfig/getNpamConfigNegative.csv";
    public static final String CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_NEGATIVE_CSV = "data/npamConfig/updateNpamConfigNegative.csv";
    public static final String CHECK_RESP_FOR_UPDATE_NPAM_CONFIG_SETUP_CSV = "data/npamConfig/updateNpamConfigSetup.csv";
    public static final String CHECK_RESP_FOR_GET_NPAM_CONFIG_SETUP_CSV = "data/npamConfig/getNpamConfigSetUp.csv";

    // Export Management
    public static final String EXPORT_NE_ACCOUNT_DS = "dataSourceForNeAccountsExport";
    public static final String COPY_EXPORT_FILE_DS = "dataSourceForCopyExportNeAccounts";
    public static final String EXPORT_NE_ACCOUNT_CSV = "data/exportManagement/exportNeAccounts.csv";
    public static final String COPY_EXPORT_FILE_CSV = "data/exportManagement/filesToCopy.csv";

    // Import Management
    public static final String GET_RESPONSE_BODY_FOR_IMPORT_JOB_DS = "datasourceForImportJob";
    public static final String GET_RESPONSE_BODY_FOR_IMPORT_FILE_JOB_ADMIN_DS = "dataSourceForImportJobAdmin";
    public static final String GET_RESPONSE_BODY_FOR_IMPORT_WRONG_ROLE_JOB_DS = "dataSourceForImportJobWrongRole";

    public static final String GET_RESPONSE_BODY_FOR_IMPORT_JOB_CSV = "data/import/importJob.csv";
    public static final String GET_RESPONSE_BODY_FOR_IMPORT_JOB_CHANGED_CSV = "data/import/importJobChanged.csv";
    public static final String GET_RESPONSE_BODY_FOR_IMPORT_RBAC_CSV = "data/import/importRbacJob.csv";
    public static final String GET_RESPONSE_BODY_FOR_IMPORT_RBAC_NEGATIVE_CSV = "data/import/importRbacNegative.csv";

    // NE account Management
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_DS = "datasourceForGetNeAccoutnOnSpecificNe";
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_NEGATIVE_DS = "datasourceForGetNeAccoutnOnSpecificNeNegative";
    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_DS = "datasourceForGetNeAccount";

    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_INTEGRATED_SCENARIO_CSV =
            "data/neAccount/getNeAccountIntegratedScenario.csv";
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_ROTATE_CSV = "data/neAccount/getSpecificNeAccountRotate.csv";
    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_RBAC_CSV = "data/neAccount/getNeAccountRbac.csv";
    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_NE_RBAC_CSV = "data/neAccount/getSpecificNeAccountRbac.csv";
    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_NE_RBAC_NEGATIVE_CSV = "data/neAccount/getSpecificNeAccountRbacNegative.csv";
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_CSV = "data/neAccount/getSpecificNeAccountIdAndStatus.csv";
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_IDS_STATUS_FAKE_CSV =
            "data/neAccount/getSpecificNeAccountIdAndStatusFake.csv";
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_AC3_CSV = "data/neAccount/getSpecificNeaccountAC3.csv";
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_AC2_CSV = "data/neAccount/getSpecificNeaccountAC2.csv";
    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_TBAC_CSV = "data/neAccount/getNeAccountTbac.csv";
    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_DATA_TBAC_TG_ALL_CSV =
            "data/neAccount/getNeAccountSpecificTbacTGAllPositive.csv";
    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_DATA_TBAC_CSV = "data/neAccount/getNeAccountSpecificTbacPositive.csv";
    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_SPECIFIC_DATA_TBAC_NEGATIVE_CSV =
            "data/neAccount/getNeAccountSpecificTbacNegative.csv";
    public static final String GET_RESPONSE_BODY_FOR_NE_ACCOUNT_DATA_RBAC_NEGATIVE_CSV = "data/neAccount/neAccountReadNegative.csv";
    public static final String GET_RESPONSE_BODY_FOR_SPECIFIC_NE_ACCOUNT_CSV = "data/neAccount/getSpecificNeAccount.csv";

}
