package droidefense.analysis;

import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.sdk.enums.PrivacyResultEnum;
import droidefense.sdk.manifest.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by .local on 23/10/2016.
 */
public class PermissionAnalysis extends AbstractAndroidAnalysis {

    private static final String[] harmlessPermission = {
            "android.permission.ACCESS_SURFACE_FLINGER",
            "android.permission.ACCOUNT_MANAGER",
            "android.permission.ADD_VOICEMAIL",
            "android.permission.CONTROL_LOCATION_UPDATES",
            "android.permission.DEVICE_POWER",
            "android.permission.EXPAND_STATUS_BAR",
            "android.permission.FLASHLIGHT",
            "android.permission.FORCE_BACK",
            "android.permission.GET_PACKAGE_SIZE",
            "android.permission.GET_TOP_ACTIVITY_INFO",
            "android.permission.GLOBAL_SEARCH",
            "android.permission.INSTALL_SHORTCUT",
            "android.permission.MANAGE_DOCUMENTS",
            "android.permission.MEDIA_CONTENT_CONTROL",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.READ_USER_DICTIONARY",
            "android.permission.REORDER_TASKS",
            "android.permission.SEND_RESPOND_VIA_MESSAGE",
            "android.permission.SET_ALARM",
            "android.permission.SET_ANIMATION_SCALE",
            "android.permission.SET_ORIENTATION",
            "android.permission.SET_POINTER_SPEED",
            "android.permission.SET_TIME",
            "android.permission.SET_TIME_ZONE",
            "android.permission.SET_WALLPAPER",
            "android.permission.UNINSTALL_SHORTCUT",
            "android.permission.VIBRATE",
            "android.permission.WAKE_LOCK",
            "android.permission.WRITE_CALENDAR",
            "android.permission.WRITE_CALL_LOG",
            "android.permission.WRITE_CONTACTS",
            "android.permission.WRITE_USER_DICTIONARY"
    };

    private static final String[] normalProtection = {
            "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.ACCESS_NOTIFICATION_POLICY",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.BROADCAST_STICKY",
            "android.permission.CHANGE_NETWORK_STATE",
            "android.permission.CHANGE_WIFI_MULTICAST_STATE",
            "android.permission.CHANGE_WIFI_STATE",
            "android.permission.DISABLE_KEYGUARD",
            "android.permission.EXPAND_STATUS_BAR",
            "android.permission.GET_PACKAGE_SIZE",
            "android.permission.INSTALL_SHORTCUT",
            "android.permission.INTERNET",
            "android.permission.KILL_BACKGROUND_PROCESSES",
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.NFC",
            "android.permission.READ_SYNC_SETTINGS",
            "android.permission.READ_SYNC_STATS",
            "android.permission.RECEIVE_BOOT_COMPLETED",
            "android.permission.REORDER_TASKS",
            "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",
            "android.permission.REQUEST_INSTALL_PACKAGES",
            "android.permission.SET_ALARM",
            "android.permission.SET_TIME_ZONE",
            "android.permission.SET_WALLPAPER",
            "android.permission.SET_WALLPAPER_HINTS",
            "android.permission.TRANSMIT_IR",
            "android.permission.UNINSTALL_SHORTCUT",
            "android.permission.USE_FINGERPRINT",
            "android.permission.VIBRATE",
            "android.permission.WAKE_LOCK",
            "android.permission.WRITE_SYNC_SETTINGS"
    };
    private static final String[] canStealDataPermissions = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS",
            "android.permission.ACCESS_MOCK_LOCATION",
            "android.permission.ACCESS_WIFI_STATE",
            "android.permission.CAMERA",
            "android.permission.CAPTURE_AUDIO_OUTPUT",
            "android.permission.CAPTURE_SECURE_VIDEO_OUTPUT",
            "android.permission.CAPTURE_VIDEO_OUTPUT",
            "android.permission.DIAGNOSTIC",
            "android.permission.DUMP",
            "android.permission.GET_ACCOUNTS",
            "android.permission.GET_TASKS",
            "android.permission.LOCATION_HARDWARE",
            "android.permission.READ_CALENDAR",
            "android.permission.READ_CALL_LOG",
            "android.permission.READ_CONTACTS",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.READ_HISTORY_BOOKMARKS",
            "android.permission.READ_PHONE_STATE",
            "android.permission.READ_PROFILE",
            "android.permission.READ_SMS",
            "android.permission.READ_SOCIAL_STREAM",
            "android.permission.READ_SYNC_SETTINGS",
            "android.permission.RECEIVE_MMS",
            "android.permission.RECEIVE_SMS",
            "android.permission.RECORD_AUDIO"
    };
    private static final String[] communication = {
            "android.permission.INTERNET",
            "android.permission.ACCESS_NETWORK_STATE",
            "android.permission.CHANGE_NETWORK_STATE",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.BLUETOOTH",
            "android.permission.WRITE_APN_SETTINGS",
            "android.permission.NETWORK",
            "android.permission.SUBSCRIBED_FEEDS_WRITE",
            "android.permission.NFC",
            "android.permission.NETWORK_PROVIDER",
            "android.permission.WRITE_SOCIAL_STREAM",
            "android.permission.SEND_SMS",
            "android.permission.USE_SIP"
    };
    private static final String[] dangerours = {
            "android.permission.ACCESS_SUPERUSER",
            "android.permission.BLUETOOTH_PRIVILEGED",
            "android.permission.BRICK",
            "android.permission.CHANGE_COMPONENT_ENABLED_STATE",
            "android.permission.CLEAR_APP_USER_DATA",
            "android.permission.DELETE_CACHE_FILES",
            "android.permission.DELETE_PACKAGES",
            "android.permission.DISABLE_KEYGUARD",
            "android.permission.FACTORY_TEST",
            "android.permission.INSTALL_PACKAGES",
            "android.permission.INJECT_EVENTS",
            "android.permission.INTERNAL_SYSTEM_WINDOW",
            "android.permission.KILL_BACKGROUND_PROCESSES",
            "android.permission.MASTER_CLEAR",
            "android.permission.MODIFY_PHONE_STATE",
            "android.permission.MOUNT_FORMAT_FILESYSTEM",
            "android.permission.MOUNT_UNMOUNT_FILESYSTEM",
            "android.permission.PROCESS_OUTGOING_CALLS",
            "android.permission.READ_LOGS",
            "android.permission.REBOOT",
            "android.permission.RECEIVE_BOOT_COMPLETED",
            "android.permission.STATUS_BAR",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.WRITE_HISTORY_BOOKMARKS",
            "android.permission.WRITE_PROFILE",
            "android.permission.WRITE_SECURE_SETTINGS"
    };
    private static final String[] dangerousSpecial = {
            "android.permission.SYSTEM_ALERT_WINDOW",
            "android.permission.WRITE_SETTINGS"
    };
    private static final String[] systemProtection = {
            "android.permission.READ_CALENDAR",
            "android.permission.WRITE_CALENDAR",
            "android.permission.CAMERA",
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS",
            "android.permission.GET_ACCOUNTS",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.RECORD_AUDIO",
            "android.permission.READ_PHONE_STATE",
            "android.permission.CALL_PHONE",
            "android.permission.READ_CALL_LOG",
            "android.permission.WRITE_CALL_LOG",
            "android.permission.ADD_VOICEMAIL",
            "android.permission.USE_SIP",
            "android.permission.PROCESS_OUTGOING_CALLS",
            "android.permission.BODY_SENSORS",
            "android.permission.SEND_SMS",
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_SMS",
            "android.permission.RECEIVE_WAP_PUSH",
            "android.permission.RECEIVE_MMS",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    private transient ArrayList<UsesPermission> usesPermissionsList;
    private transient ArrayList<UsesPermissionSDK23> usesPermissions23List;
    private transient ArrayList<Permission> permissionsList;
    private transient ArrayList<PermissionGroup> permissionGroupList;
    private transient ArrayList<PermissionTree> permissionTreeList;

    private double risk;

    private int comms;
    private int steal;

    private int initialPermissionCount;
    private int interestingCount;

    public PermissionAnalysis() {
        super();
        this.risk = 0;
        this.comms = 0;
        this.usesPermissionsList = new ArrayList<>();
        this.usesPermissions23List = new ArrayList<>();
        this.permissionsList = new ArrayList<>();
        this.permissionGroupList = new ArrayList<>();
        this.permissionTreeList = new ArrayList<>();
    }

    private boolean canSendYourDataRemote(int communication) {
        //TODO we should also take into account if the application has any kind of provider or whether the app can save data on hdd.
        //This is because some malware can be designed in multi app style. One app steals data, and other app only reads them and send to a remote server
        return communication > 0;
    }

    private boolean canGetYourData(int canStealDataPermissions) {
        return canStealDataPermissions > 0;
    }

    private int getMatchedPermissions(ArrayList<String> totalPerms, ArrayList<String> data) {
        int matched = 0;
        //todo optimize code. if inisde for not optimus
        for (String str : data) {
            boolean contains = totalPerms.contains(str);
            if (contains) {
                System.out.println(str);
            }
            matched += contains ? 1 : 0;
        }
        return matched;
    }

    public int getComms() {
        return comms;
    }

    public int canStealData() {
        return steal;
    }

    public PrivacyResultEnum getPrivacyResult() {
        if (canStealData() == 0) {
            return PrivacyResultEnum.SAFE;
        } else if (canStealData() > 0 && getComms() == 0) {
            //can steal data but it has no way of sending if out of the phone
            return PrivacyResultEnum.SUSPICIOUS;
        } else if (canStealData() > 0 && getComms() > 0) {
            //can steal data and it has ine or more ways of sending if out of the phone
            return PrivacyResultEnum.DATA_LEAK;
        }
        return PrivacyResultEnum.UNKNOWN;
    }

    //Analysis object overwrite

    @Override
    protected boolean analyze() {
        //getting data
        if (currentProject != null && currentProject.getManifestInfo() != null) {
            this.usesPermissionsList = currentProject.getManifestInfo().getUsesPermissionList();
            this.usesPermissions23List = currentProject.getManifestInfo().getUsesPermissionSdk23List();
            //not used by now
            this.permissionsList = currentProject.getManifestInfo().getPermissionList();
            this.permissionGroupList = currentProject.getManifestInfo().getPermissionGroupList();
            this.permissionTreeList = currentProject.getManifestInfo().getPermissionTreeList();
        }

        //calculate
        this.initialPermissionCount = usesPermissions23List.size() + usesPermissionsList.size();
        log("Total permissions detected: " + initialPermissionCount, 1);
        ArrayList<String> totalPerms = new ArrayList<>();
        //add user permissions
        for (UsesPermission u : usesPermissionsList) {
            totalPerms.add(u.getName());
        }
        //add user permissions sdk 23
        for (UsesPermissionSDK23 u : usesPermissions23List) {
            totalPerms.add(u.getName());
        }
        //add user permissions
        for (Permission u : permissionsList) {
            totalPerms.add(u.getName());
        }
        //add user permissions group
        for (PermissionGroup u : permissionGroupList) {
            totalPerms.add(u.getName());
        }
        //add user permissions tree
        for (PermissionTree u : permissionTreeList) {
            totalPerms.add(u.getName());
        }

        //remove harmless permissions
        int idx = 0;
        List<String> harmless = Arrays.asList(harmlessPermission);
        while (idx < totalPerms.size()) {
            if (harmless.contains(totalPerms.get(idx))) {
                totalPerms.remove(idx);
            } else {
                idx++;
            }
        }
        this.interestingCount = totalPerms.size();

        comms = getMatchedPermissions(totalPerms, new ArrayList<>(Arrays.asList(communication)));
        steal = getMatchedPermissions(totalPerms, new ArrayList<>(Arrays.asList(canStealDataPermissions)));

        //calculate risk
        if (interestingCount > 0 && canGetYourData(steal) && canSendYourDataRemote(comms)) {
            //compute risk score
            this.risk = ((comms + steal) / (double) initialPermissionCount) * 100;
        } else {
            this.risk = 0.0;
        }
        this.executionSuccessful = true;

        log("Total permissions detected: " + interestingCount, 1);

        log("Communication: " + comms, 2);
        log("Data: " + steal, 2);

        log("Risk index: " + risk, 3);

        //set result to project
        currentProject.setPrivacyResult(getPrivacyResult());
        return executionSuccessful;
    }

    @Override
    public String getName() {
        return "Permission checker module";
    }
}
