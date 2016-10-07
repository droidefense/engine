package com.zerjioang.apkr.batch.task;


import apkr.external.module.batch.base.IWekaGenerator;
import apkr.external.module.datamodel.manifest.UsesPermission;

import java.io.Serializable;
import java.util.*;

public class OutPutResult extends HashMap<String, String> implements IWekaGenerator, Serializable {

    private static final HashSet<String> globalPermissionList = load();

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
    private ArrayList<UsesPermission> permList;

    public OutPutResult(ArrayList<UsesPermission> permList) {
        this.permList = permList;
        if (permList == null)
            throw new IllegalArgumentException("Permission list can not be NULL!");

        //populate hash
        for (String s : getGlobalPermissionList()) {
            this.put(s, "0");
        }
        parse();
    }

    private static HashSet<String> load() {
        HashSet<String> names = new HashSet<>();
        names.add("ACCESS_ALL_DOWNLOADS");
        names.add("ACCESS_COARSE_LOCATION");
        names.add("ACCESS_DOWNLOAD_MANAGER_ADVANCED");
        names.add("ACCESS_DRM");
        names.add("ACCESS_FINE_LOCATION");
        names.add("ACCESS_KEYGUARD_SECURE_STORAGE");
        names.add("ACCESS_LOCATION_EXTRA_COMMANDS");
        names.add("ACCESS_MOCK_LOCATION");
        names.add("ACCESS_NETWORK_STATE");
        names.add("ACCESS_NOTIFICATIONS");
        names.add("ACCESS_WIFI_STATE");
        names.add("AUTHENTICATE_ACCOUNTS");
        names.add("BACKUP");
        names.add("BATTERY_STATS");
        names.add("BIND_DEVICE_ADMIN");
        names.add("BLUETOOTH");
        names.add("BLUETOOTH_ADMIN");
        names.add("BROADCAST_NETWORK_PRIVILEGED");
        names.add("BROADCAST_SCORE_NETWORKS");
        names.add("CALL_PRIVILEGED");
        names.add("CAPTURE_AUDIO_OUTPUT");
        names.add("CAPTURE_SECURE_VIDEO_OUTPUT");
        names.add("CAPTURE_VIDEO_OUTPUT");
        names.add("CHANGE_COMPONENT_ENABLED_STATE");
        names.add("CHANGE_NETWORK_STATE");
        names.add("CHANGE_WIFI_MULTICAST_STATE");
        names.add("CHANGE_WIFI_STATE");
        names.add("CLEAR_APP_CACHE");
        names.add("CLEAR_APP_USER_DATA");
        names.add("CONFIGURE_WIFI_DISPLAY");
        names.add("CONNECTIVITY_INTERNAL");
        names.add("CONTROL_VPN");
        names.add("DELETE_CACHE_FILES");
        names.add("DELETE_PACKAGES");
        names.add("DEVICE_POWER");
        names.add("DISABLE_KEYGUARD");
        names.add("DOWNLOAD_CACHE_NON_PURGEABLE");
        names.add("DUMP");
        names.add("EXPAND_STATUS_BAR");
        names.add("FILTER_EVENTS");
        names.add("FRAME_STATS");
        names.add("FREEZE_SCREEN");
        names.add("GET_ACCOUNTS");
        names.add("GET_APP_OPS_STATS");
        names.add("GET_PACKAGE_SIZE");
        names.add("GLOBAL_SEARCH");
        names.add("GRANT_REVOKE_PERMISSIONS");
        names.add("INSTALL_DRM");
        names.add("INSTALL_LOCATION_PROVIDER");
        names.add("INSTALL_PACKAGES");
        names.add("INTERACT_ACROSS_USERS");
        names.add("INTERACT_ACROSS_USERS_FULL");
        names.add("INTERNAL_SYSTEM_WINDOW");
        names.add("INTERNET");
        names.add("LOCATION_HARDWARE");
        names.add("MAGNIFY_DISPLAY");
        names.add("MANAGE_ACCOUNTS");
        names.add("MANAGE_APP_TOKENS");
        names.add("MANAGE_CA_CERTIFICATES");
        names.add("MANAGE_DEVICE_ADMINS");
        names.add("MANAGE_DOCUMENTS");
        names.add("MANAGE_NETWORK_POLICY");
        names.add("MANAGE_USB");
        names.add("MANAGE_USERS");
        names.add("MARK_NETWORK_SOCKET");
        names.add("MEDIA_CONTENT_CONTROL");
        names.add("MODIFY_AUDIO_ROUTING");
        names.add("MODIFY_AUDIO_SETTINGS");
        names.add("MODIFY_NETWORK_ACCOUNTING");
        names.add("MODIFY_PHONE_STATE");
        names.add("MOVE_PACKAGE");
        names.add("NFC");
        names.add("PACKAGE_USAGE_STATS");
        names.add("PACKAGE_VERIFICATION_AGENT");
        names.add("READ_CONTACTS");
        names.add("READ_DREAM_STATE");
        names.add("READ_FRAME_BUFFER");
        names.add("READ_LOGS");
        names.add("READ_NETWORK_USAGE_HISTORY");
        names.add("READ_PHONE_STATE");
        names.add("READ_PRIVILEGED_PHONE_STATE");
        names.add("READ_PROFILE");
        names.add("READ_SOCIAL_STREAM");
        names.add("READ_SYNC_SETTINGS");
        names.add("READ_SYNC_STATS");
        names.add("REBOOT");
        names.add("RECEIVE_SMS");
        names.add("REMOTE_AUDIO_PLAYBACK");
        names.add("RETRIEVE_WINDOW_INFO");
        names.add("SCORE_NETWORKS");
        names.add("SEND_SMS");
        names.add("SERIAL_PORT");
        names.add("SET_ANIMATION_SCALE");
        names.add("SET_INPUT_CALIBRATION");
        names.add("SET_KEYBOARD_LAYOUT");
        names.add("SET_ORIENTATION");
        names.add("SET_POINTER_SPEED");
        names.add("SET_PREFERRED_APPLICATIONS");
        names.add("SET_WALLPAPER");
        names.add("SET_WALLPAPER_COMPONENT");
        names.add("SET_WALLPAPER_HINTS");
        names.add("SHUTDOWN");
        names.add("STATUS_BAR");
        names.add("STATUS_BAR_SERVICE");
        names.add("TRANSMIT_IR");
        names.add("UPDATE_APP_OPS_STATS");
        names.add("UPDATE_DEVICE_STATS");
        names.add("UPDATE_LOCK");
        names.add("USE_CREDENTIALS");
        names.add("USE_SIP");
        names.add("VIBRATE");
        names.add("WAKE_LOCK");
        names.add("WRITE_APN_SETTINGS");
        names.add("WRITE_CONTACTS");
        names.add("WRITE_DREAM_STATE");
        names.add("WRITE_PROFILE");
        names.add("WRITE_SECURE_SETTINGS");
        names.add("WRITE_SETTINGS");
        names.add("WRITE_SMS");
        names.add("WRITE_SOCIAL_STREAM");
        names.add("WRITE_SYNC_SETTINGS");
        names.add("com.android.email.permission.ACCESS_PROVIDER");
        names.add("com.android.printspooler.permission.ACCESS_ALL_PRINT_JOBS");
        names.add("com.android.voicemail.permission.ADD_VOICEMAIL");
        names.add("com.android.voicemail.permission.READ_WRITE_ALL_VOICEMAIL");
        names.add("ACCESS_CHECKIN_PROPERTIES");
        names.add("ACCESS_NOTIFICATION_POLICY");
        names.add("ACCOUNT_MANAGER");
        names.add("ADD_VOICEMAIL");
        names.add("BIND_ACCESSIBILITY_SERVICE");
        names.add("BIND_APPWIDGET");
        names.add("BIND_CARRIER_MESSAGING_SERVICE");
        names.add("BIND_CARRIER_SERVICES");
        names.add("BIND_CHOOSER_TARGET_SERVICE");
        names.add("BIND_DREAM_SERVICE");
        names.add("BIND_INCALL_SERVICE");
        names.add("BIND_INPUT_METHOD");
        names.add("BIND_MIDI_DEVICE_SERVICE");
        names.add("BIND_NFC_SERVICE");
        names.add("BIND_NOTIFICATION_LISTENER_SERVICE");
        names.add("BIND_PRINT_SERVICE");
        names.add("BIND_REMOTEVIEWS");
        names.add("BIND_TELECOM_CONNECTION_SERVICE");
        names.add("BIND_TEXT_SERVICE");
        names.add("BIND_TV_INPUT");
        names.add("BIND_VOICE_INTERACTION");
        names.add("BIND_VPN_SERVICE");
        names.add("BIND_WALLPAPER");
        names.add("BLUETOOTH_PRIVILEGED");
        names.add("BODY_SENSORS");
        names.add("BROADCAST_PACKAGE_REMOVED");
        names.add("BROADCAST_SMS");
        names.add("BROADCAST_STICKY");
        names.add("BROADCAST_WAP_PUSH");
        names.add("CALL_PHONE");
        names.add("CAMERA");
        names.add("CHANGE_CONFIGURATION");
        names.add("CONTROL_LOCATION_UPDATES");
        names.add("DIAGNOSTIC");
        names.add("FACTORY_TEST");
        names.add("FLASHLIGHT");
        names.add("GET_ACCOUNTS_PRIVILEGED");
        names.add("GET_TASKS");
        names.add("INSTALL_SHORTCUT");
        names.add("KILL_BACKGROUND_PROCESSES");
        names.add("MASTER_CLEAR");
        names.add("MOUNT_FORMAT_FILESYSTEMS");
        names.add("MOUNT_UNMOUNT_FILESYSTEMS");
        names.add("PERSISTENT_ACTIVITY");
        names.add("PROCESS_OUTGOING_CALLS");
        names.add("READ_CALENDAR");
        names.add("READ_CALL_LOG");
        names.add("READ_EXTERNAL_STORAGE");
        names.add("READ_INPUT_STATE");
        names.add("READ_SMS");
        names.add("READ_VOICEMAIL");
        names.add("RECEIVE_BOOT_COMPLETED");
        names.add("RECEIVE_MMS");
        names.add("RECEIVE_WAP_PUSH");
        names.add("RECORD_AUDIO");
        names.add("REORDER_TASKS");
        names.add("REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
        names.add("REQUEST_INSTALL_PACKAGES");
        names.add("RESTART_PACKAGES");
        names.add("SEND_RESPOND_VIA_MESSAGE");
        names.add("SET_ALARM");
        names.add("SET_ALWAYS_FINISH");
        names.add("SET_DEBUG_APP");
        names.add("SET_PROCESS_LIMIT");
        names.add("SET_TIME");
        names.add("SET_TIME_ZONE");
        names.add("SIGNAL_PERSISTENT_PROCESSES");
        names.add("SYSTEM_ALERT_WINDOW");
        names.add("UNINSTALL_SHORTCUT");
        names.add("USE_FINGERPRINT");
        names.add("WRITE_CALENDAR");
        names.add("WRITE_CALL_LOG");
        names.add("WRITE_EXTERNAL_STORAGE");
        names.add("WRITE_GSERVICES");
        names.add("WRITE_VOICEMAIL");
        names.add("READ_WRITE_ALL_VOICEMAIL");
        //add my extra permissions
        names.add("harmlessPermission");
        names.add("canStealDataPermissions");
        names.add("communication");
        names.add("dangerours");
        names.add("dangerousSpecial");
        return names;
    }

    public static String[] getGlobalPermissionList() {
        return globalPermissionList.toArray(new String[globalPermissionList.size()]);
    }

    private void parse() {
        for (UsesPermission p : permList) {
            String key = p.getName().replace("android.permission.", "");
            if (globalPermissionList.contains(key))
                this.put(key, "1");
        }
        //calculate next values
        //harmlessPermission, canStealDataPermissions, communication, dangerours, dangerousSpecial
        String[] keys = {"harmlessPermission", "canStealDataPermissions", "communication", "dangerours", "dangerousSpecial"};
        for (String key : keys) {
            if (checkPermissionsWithKey(key, permList)) {
                continue;
            }
        }
    }

    private boolean checkPermissionsWithKey(String key, ArrayList<UsesPermission> permList) {
        switch (key) {
            case "harmlessPermission":
                return checkPermissionsWithKeyArray("harmlessPermission", harmlessPermission, permList);
            case "canStealDataPermissions":
                return checkPermissionsWithKeyArray("canStealDataPermissions", canStealDataPermissions, permList);
            case "communication":
                return checkPermissionsWithKeyArray("communication", communication, permList);
            case "dangerours":
                return checkPermissionsWithKeyArray("dangerours", dangerours, permList);
            case "dangerousSpecial":
                return checkPermissionsWithKeyArray("dangerousSpecial", dangerousSpecial, permList);
        }
        return false;
    }

    private boolean checkPermissionsWithKeyArray(String key, String[] dataArray, ArrayList<UsesPermission> permList) {
        List<String> asList = Arrays.asList(dataArray);
        for (UsesPermission p : permList) {
            //get current permission name
            String currentPermName = p.getName();
            //check if is on dataArray
            boolean contains = asList.contains(currentPermName);
            if (contains) {
                //savae result and return true
                this.put(key, "1");
                return true;
            }
        }
        //if does not match with no elements, set as false
        this.put(key, "0");
        return false;
    }

    public String[] getValues(String[] names) {
        String[] output = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            output[i] = this.get(names[i]).equals("1") ? "1" : "0";
        }
        return output;
    }

    @Override
    public String toWekaData() {
        String data = "";
        String names[] = getGlobalPermissionList();
        data += this.get(names[0]).equals("1") ? "true" : "false";
        for (int i = 1; i < names.length; i++) {
            data += "," + (this.get(names[i]).equals("1") ? "true" : "false");
        }
        return data;
    }
}
