package droidefense.xmodel.base;


import droidefense.xmodel.manifest.*;
import droidefense.xmodel.manifest.enums.*;
import org.xml.sax.Attributes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zerjioang on 03/03/2016.
 */
public enum ManifestID implements Serializable {

    ACTION {
        @Override
        public Class getClassName() {
            return Action.class;
        }
    },
    ACTIVITY {
        @Override
        public Class getClassName() {
            return Activity.class;
        }
    },
    ACTIVITY_ALIAS {
        @Override
        public Class getClassName() {
            return ActivityAlias.class;
        }
    },
    APPLICATION {
        @Override
        public Class getClassName() {
            return Application.class;
        }
    },
    CATEGORY {
        @Override
        public Class getClassName() {
            return Category.class;
        }
    },
    COMPATIBLE_SCREENS {
        @Override
        public Class getClassName() {
            return CompatibleScreens.class;
        }
    },
    DATA {
        @Override
        public Class getClassName() {
            return Data.class;
        }
    },
    GRANT_URI_PERMISSION {
        @Override
        public Class getClassName() {
            return GrantURIPermission.class;
        }
    },
    INTRUMENTATION {
        @Override
        public Class getClassName() {
            return Instrumentation.class;
        }
    },
    INTENT_FILTER {
        @Override
        public Class getClassName() {
            return IntentFilter.class;
        }
    },
    MANIFEST {
        @Override
        public Class getClassName() {
            return Manifest.class;
        }
    },
    METADATA {
        @Override
        public Class getClassName() {
            return Metadata.class;
        }
    },
    PERMISSION {
        @Override
        public Class getClassName() {
            return Permission.class;
        }
    },
    PATH_PERMISSION {
        @Override
        public Class getClassName() {
            return PathPermission.class;
        }
    },
    PERMISSION_GROUP {
        @Override
        public Class getClassName() {
            return PermissionGroup.class;
        }
    },
    PERMISSION_TREE {
        @Override
        public Class getClassName() {
            return PermissionTree.class;
        }
    },
    PROVIDER {
        @Override
        public Class getClassName() {
            return Provider.class;
        }
    },
    RECEIVER {
        @Override
        public Class getClassName() {
            return Receiver.class;
        }
    },
    SERVICE {
        @Override
        public Class getClassName() {
            return Service.class;
        }
    },
    SUPPORT_GL_TEXTURES {
        @Override
        public Class getClassName() {
            return SupportGLTextures.class;
        }
    },
    SUPPORTS_SCREENS {
        @Override
        public Class getClassName() {
            return SupportsScreens.class;
        }
    },
    USES_CONFIGURATION {
        @Override
        public Class getClassName() {
            return UsesConfiguration.class;
        }
    },
    USES_FEATURE {
        @Override
        public Class getClassName() {
            return UsesFeature.class;
        }
    },
    USES_LIBRARY {
        @Override
        public Class getClassName() {
            return UsesLibrary.class;
        }
    },
    USES_PERMISSION {
        @Override
        public Class getClassName() {
            return UsesPermission.class;
        }
    },
    USES_PERMISSION_SDK23 {
        @Override
        public Class getClassName() {
            return UsesPermissionSDK23.class;
        }
    },
    USES_SDK {
        @Override
        public Class getClassName() {
            return UsesSDK.class;
        }
    },
    CONFIG_CHANGES {
        @Override
        public Class getClassName() {
            return ConfigChanges.class;
        }
    },
    DOCUMENT_LAUNCH_MODE {
        @Override
        public Class getClassName() {
            return LaunchMode.class;
        }
    },
    INSTALL_LOCATION {
        @Override
        public Class getClassName() {
            return InstallLocation.class;
        }
    },
    LAUNCH_MODE {
        @Override
        public Class getClassName() {
            return LaunchMode.class;
        }
    },
    PROTECTION_LEVEL {
        @Override
        public Class getClassName() {
            return ProtectionLevel.class;
        }
    },
    REQ_NAVIGATION {
        @Override
        public Class getClassName() {
            return ReqNavigation.class;
        }
    },
    REQ_TOUCH_SCREEN {
        @Override
        public Class getClassName() {
            return ReqTouchScreen.class;
        }
    },
    RQ_KEYBOARD_TYPE {
        @Override
        public Class getClassName() {
            return RqKeyboardType.class;
        }
    },
    SCREEN_ORIENTATION {
        @Override
        public Class getClassName() {
            return ScreenOrientation.class;
        }
    },
    UI_OPTIONS {
        @Override
        public Class getClassName() {
            return UiOptions.class;
        }
    },
    WINDOW_SOFT_INPUT_MODE {
        @Override
        public Class getClassName() {
            return WindowSoftInputMode.class;
        }
    };

    private static final Map<String, ManifestID> map = new HashMap<>();

    private static ManifestID getResolvedManifestEntry(String id) {
        if (map.isEmpty()) {
            map.put("action", ManifestID.ACTION);
            map.put("activity", ManifestID.ACTIVITY);
            map.put("activity-alias", ManifestID.ACTIVITY_ALIAS);
            map.put("application", ManifestID.APPLICATION);
            map.put("category", ManifestID.CATEGORY);
            map.put("compatible-screens", ManifestID.COMPATIBLE_SCREENS);
            map.put("data", ManifestID.DATA);
            map.put("grant-uri-permission", ManifestID.GRANT_URI_PERMISSION);
            map.put("instrumentation", ManifestID.INTRUMENTATION);
            map.put("intent-filter", ManifestID.INTENT_FILTER);
            map.put("manifest", ManifestID.MANIFEST);
            map.put("meta-data", ManifestID.METADATA);
            map.put("permission", ManifestID.PERMISSION);
            map.put("path-permission", ManifestID.PATH_PERMISSION);
            map.put("permission-group", ManifestID.PERMISSION_GROUP);
            map.put("permission-tree", ManifestID.PERMISSION_TREE);
            map.put("provider", ManifestID.PROVIDER);
            map.put("receiver", ManifestID.RECEIVER);
            map.put("service", ManifestID.SERVICE);
            map.put("support-gl-textures", ManifestID.SUPPORT_GL_TEXTURES);
            map.put("supports-screens", ManifestID.SUPPORTS_SCREENS);
            map.put("uses-configuration", ManifestID.USES_CONFIGURATION);
            map.put("uses-feature", ManifestID.USES_FEATURE);
            map.put("uses-library", ManifestID.USES_LIBRARY);
            map.put("uses-permission", ManifestID.USES_PERMISSION);
            map.put("uses-permission-sdk23", ManifestID.USES_PERMISSION_SDK23);
            map.put("uses-sdk", ManifestID.USES_SDK);
            //add enum maps
            map.put("configChanges", ManifestID.CONFIG_CHANGES);
            map.put("protectionLevel", ManifestID.PROTECTION_LEVEL);
            map.put("ccreenOrientation", ManifestID.SCREEN_ORIENTATION);
            map.put("documentLaunchMode", ManifestID.DOCUMENT_LAUNCH_MODE);
            map.put("reqNavigation", ManifestID.REQ_NAVIGATION);
            map.put("uiOptions", ManifestID.UI_OPTIONS);
            map.put("installLocation", ManifestID.INSTALL_LOCATION);
            map.put("reqTouchScreen", ManifestID.REQ_TOUCH_SCREEN);
            map.put("windowSoftInputMode", ManifestID.WINDOW_SOFT_INPUT_MODE);
            map.put("launchMode", ManifestID.LAUNCH_MODE);
            map.put("rqKeyboardType", ManifestID.RQ_KEYBOARD_TYPE);

        }
        return map.get(id);
    }

    public static Class getResolvedObject(String tag) throws InstantiationException {
        //just in case...
        tag = tag.replace("android:", "");
        ManifestID id = getResolvedManifestEntry(tag);
        if (id == null) {
            throw new InstantiationException("Atom Manifest parser could not map <" + tag + "> tag");
        } else {
            Class classToInstantiate = id.getClassName();
            if (classToInstantiate == null) {
                throw new InstantiationException("Atom Manifest parser could not map <" + tag + "> tag");
            }
            return classToInstantiate;
        }
    }

    public static String getJavaMethodName(Attributes attributes, int i, String data) {
        String name = attributes.getLocalName(i);

        //convert tag to method name
        String methodName = name.replace("android:", "");
        methodName = methodName.replace(":android", "");
        methodName = Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
        methodName = "set" + methodName;

        //control some exceptions due to java protected words

        //word package
        switch (methodName) {
            case "setPackage":
                methodName = "setPackageName";
                break;
        }
        return methodName;
    }

    //ABSTRACT FUNCTION
    protected abstract Class getClassName();
}