package droidefense.om.flow.stable;

import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.struct.generic.IAtomClass;
import droidefense.om.machine.base.struct.generic.IAtomField;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.base.struct.generic.IAtomMethod;
import droidefense.om.machine.base.struct.model.AndroidRField;
import droidefense.sdk.helpers.Util;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;
import droidefense.util.DroidefenseIntel;
import droidefense.vfs.model.impl.VirtualFile;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final strictfp class ReferencesResolverWorker extends AbstractDVMThread {

    private static final String ANDROID_R_ID_REGEX = "@android\\:(0x){0,1}([0-9a-zA-Z]{8})";
    private transient ArrayList<AndroidRField> references;

    public ReferencesResolverWorker(DroidefenseProject currentProject) {
        super(currentProject);
        references = new ArrayList<>();
    }

    @Override
    public void preload() {
        Log.write(LoggerType.DEBUG, "WORKER: ReferencesResolverWorker");
        this.setStatus(AbstractDVMThread.STATUS_NOT_STARTED);
        vm.setThreads(new Vector());
        vm.addThread(this);
    }

    @Override
    public void run() {
        try {
            execute(false);
        } catch (Throwable throwable) {
            Log.write(LoggerType.ERROR, throwable.getLocalizedMessage());
        }
    }

    @Override
    public void finish() {
        Log.write(LoggerType.DEBUG, "Android R references resolved!");
        Log.write(LoggerType.DEBUG, "Number of references resolved: "+this.references.size());
        Log.write(LoggerType.DEBUG, "Remapping old files...");
        this.remap();
    }

    private void remap() {
        this.currentProject.setAndroidReferences(this.references);
        ArrayList<VirtualFile> xmls = this.currentProject.getXmlFiles();
        for (VirtualFile vf : xmls) {
            Log.write(LoggerType.DEBUG, "Updating content of "+vf.getPath());
            singleRemap(vf);
        }
        //todo reparse AndroidManifest.xml
        Log.write(LoggerType.DEBUG, "Remapping [OK]");
    }

    private void singleRemap(VirtualFile xmlFile) {
        String data = new String(xmlFile.getContent());
        for(AndroidRField field : references) {
            String idOriginal = field.getAssembledID();
            String idReversed = field.reverseName();
            data = data.replaceAll(idOriginal, idReversed);
        }
        HashMap<String, String> keymap = new HashMap<>();

        //https://developer.android.com/reference/android/R.html

         //R.anim [DONE]
        keymap.put("@android:010a0004", "@anim/accelerate_interpolator");
        keymap.put("@android:010a0005", "@anim/anticipate_interpolator");
        keymap.put("@android:010a0007", "@anim/anticipate_overshoot_interpolator");
        keymap.put("@android:010a0009", "@anim/bounce_interpolator");
        keymap.put("@android:010a000a", "@anim/cycle_interpolator");
        keymap.put("@android:010a000c", "@anim/decelerate_interpolator");
        keymap.put("@android:010a0006", "@anim/fade_in");
        keymap.put("@android:010a0000", "@anim/fade_out");
        keymap.put("@android:010a0001", "@anim/linear_interpolator");
        keymap.put("@android:010a000b", "@anim/overshoot_interpolator");
        keymap.put("@android:010a0008", "@anim/slide_in_left");
        keymap.put("@android:010a0002", "@anim/slide_out_right");

        //R.animator [DONE]
        keymap.put("@android:010b0000", "@animator/fade_in");
        keymap.put("@android:010b0001", "@animator/fade_out");

        //R.array [DONE]
        keymap.put("@android:01070000", "@array/emailAddressTypes");
        keymap.put("@android:01070001", "@array/imProtocols");
        keymap.put("@android:01070002", "@array/organizationTypes");
        keymap.put("@android:01070003", "@array/phoneTypes");
        keymap.put("@android:01070004", "@array/postalAddressTypes");

        //R.attr

        //R.bool [DONE]

        //R.color [DONE]
        keymap.put("@android:0106000e", "@color/background_dark");
        keymap.put("@android:0106000f", "@color/background_light");
        keymap.put("@android:0106000c", "@color/black");
        keymap.put("@android:01060000", "@color/darker_gray");
        keymap.put("@android:0106001b", "@color/holo_blue_bright");
        keymap.put("@android:01060013", "@color/holo_blue_dark");
        keymap.put("@android:01060012", "@color/holo_blue_light");
        keymap.put("@android:01060015", "@color/holo_green_dark");
        keymap.put("@android:01060014", "@color/holo_green_light");
        keymap.put("@android:01060019", "@color/holo_orange_dark");
        keymap.put("@android:01060018", "@color/holo_orange_light");
        keymap.put("@android:0106001a", "@color/holo_purple");
        keymap.put("@android:01060017", "@color/holo_red_dark");
        keymap.put("@android:01060016", "@color/holo_red_light");
        keymap.put("@android:01060001", "@color/primary_text_dark");
        keymap.put("@android:01060002", "@color/primary_text_dark_nodisable");
        keymap.put("@android:01060003", "@color/primary_text_light");
        keymap.put("@android:01060004", "@color/primary_text_light_nodisable");
        keymap.put("@android:01060005", "@color/secondary_text_dark");
        keymap.put("@android:01060006", "@color/secondary_text_dark_nodisable");
        keymap.put("@android:01060007", "@color/secondary_text_light");
        keymap.put("@android:01060008", "@color/secondary_text_light_nodisable");
        keymap.put("@android:01060009", "@color/tab_indicator_text");
        keymap.put("@android:01060010", "@color/tertiary_text_dark");
        keymap.put("@android:01060011", "@color/tertiary_text_light");
        keymap.put("@android:0106000d", "@color/transparent");
        keymap.put("@android:0106000b", "@color/white");
        keymap.put("@android:0106000a", "@color/widget_edittext_dark");

        //R.dimen
        keymap.put("@android:01050000", "@dimen/app_icon_size");
        keymap.put("@android:01050003", "@dimen/dialog_min_width_major");
        keymap.put("@android:01050004", "@dimen/dialog_min_width_minor");
        keymap.put("@android:01050006", "@dimen/notification_large_icon_height");
        keymap.put("@android:01050005", "@dimen/notification_large_icon_width");
        keymap.put("@android:01050001", "@dimen/thumbnail_height");
        keymap.put("@android:01050002", "@dimen/thumbnail_width");

        //R.drawable

        //R.fraction [DONE]

        //R.id

        //R.integer [DONE]
        keymap.put("@android:010e0002", "@integer/config_longAnimTime");
        keymap.put("@android:010e0001", "@integer/config_mediumAnimTime");
        keymap.put("@android:010e0000", "@integer/config_shortAnimTime");
        keymap.put("@android:010e0003", "@integer/status_bar_notification_info_maxnum");

        //R.interpolar [DONE]
        keymap.put("@android:010c0002", "@interpolator/accelerate_cubic");
        keymap.put("@android:010c0006", "@interpolator/accelerate_decelerate");
        keymap.put("@android:010c0000", "@interpolator/accelerate_quad");
        keymap.put("@android:010c0004", "@interpolator/accelerate_quint");
        keymap.put("@android:010c0007", "@interpolator/anticipate");
        keymap.put("@android:010c0009", "@interpolator/anticipate_overshoot");
        keymap.put("@android:010c000a", "@interpolator/bounce");
        keymap.put("@android:010c000c", "@interpolator/cycle");
        keymap.put("@android:010c0003", "@interpolator/decelerate_cubic");
        keymap.put("@android:010c0001", "@interpolator/decelerate_quad");
        keymap.put("@android:010c0005", "@interpolator/decelerate_quint");
        keymap.put("@android:010c000f", "@interpolator/fast_out_linear_in");
        keymap.put("@android:010c000d", "@interpolator/fast_out_slow_in");
        keymap.put("@android:010c000b", "@interpolator/linear");
        keymap.put("@android:010c000e", "@interpolator/linear_out_slow_in");
        keymap.put("@android:010c0008", "@interpolator/overshoot");

        //R.layout [DONE]
        keymap.put("@android:01090000", "@layout/activity_list_item");
        keymap.put("@android:0109000e", "@layout/browser_link_context_header");
        keymap.put("@android:01090001", "@layout/expandable_list_content");
        keymap.put("@android:01090014", "@layout/list_content");
        keymap.put("@android:01090002", "@layout/preference_category");
        keymap.put("@android:01090011", "@layout/select_dialog_item");
        keymap.put("@android:01090013", "@layout/select_dialog_multichoice");
        keymap.put("@android:01090012", "@layout/select_dialog_singlechoice");
        keymap.put("@android:0109000a", "@layout/simple_dropdown_item_1line");
        keymap.put("@android:01090006", "@layout/simple_expandable_list_item_1");
        keymap.put("@android:01090007", "@layout/simple_expandable_list_item_2");
        keymap.put("@android:0109000b", "@layout/simple_gallery_item");
        keymap.put("@android:01090003", "@layout/simple_list_item_1");
        keymap.put("@android:01090004", "@layout/simple_list_item_2");
        keymap.put("@android:01090016", "@layout/simple_list_item_activated_1");
        keymap.put("@android:01090017", "@layout/simple_list_item_activated_2");
        keymap.put("@android:01090005", "@layout/simple_list_item_checked");
        keymap.put("@android:01090010", "@layout/simple_list_item_multiple_choice");
        keymap.put("@android:0109000f", "@layout/simple_list_item_single_choice");
        keymap.put("@android:01090015", "@layout/simple_selectable_list_item");
        keymap.put("@android:01090009", "@layout/simple_spinner_dropdown_item");
        keymap.put("@android:01090008", "@layout/simple_spinner_item");
        keymap.put("@android:0109000c", "@layout/test_list_item");
        keymap.put("@android:0109000d", "@layout/two_line_list_item");

        //R.menu [DONE]

        //R.mipmap
        keymap.put("@android:010d0000", "@layout/sym_def_app_icon");

        //R.plurals [DONE]

        //R.raw [DONE]

        //R.string [DONE]
        keymap.put("@android:01040010", "@string/VideoView_error_button");
        keymap.put("@android:01040015", "@string/VideoView_error_text_invalid_progressive_playback");
        keymap.put("@android:01040011", "@string/VideoView_error_text_unknown");
        keymap.put("@android:01040012", "@string/VideoView_error_title");
        keymap.put("@android:01040000", "@string/cancel");
        keymap.put("@android:01040001", "@string/copy");
        keymap.put("@android:01040002", "@string/copyUrl");
        keymap.put("@android:01040003", "@string/cut");
        keymap.put("@android:01040005", "@string/defaultMsisdnAlphaTag");
        keymap.put("@android:01040004", "@string/defaultVoiceMailAlphaTag");
        keymap.put("@android:01040014", "@string/dialog_alert_title");
        keymap.put("@android:01040006", "@string/emptyPhoneNumber");
        keymap.put("@android:01040018", "@string/fingerprint_icon_content_description");
        keymap.put("@android:01040007", "@string/httpErrorBadUrl");
        keymap.put("@android:01040008", "@string/httpErrorUnsupportedScheme");
        keymap.put("@android:01040009", "@string/no");
        keymap.put("@android:0104000a", "@string/ok");
        keymap.put("@android:0104000b", "@string/paste");
        keymap.put("@android:01040019", "@string/paste_as_plain_text");
        keymap.put("@android:0104000c", "@string/search_go");
        keymap.put("@android:0104000d", "@string/selectAll");
        keymap.put("@android:01040016", "@string/selectTextMode");
        keymap.put("@android:01040017", "@string/status_bar_notification_info_overflow");
        keymap.put("@android:0104000e", "@string/unknownName");
        keymap.put("@android:0104000f", "@string/untitled");
        keymap.put("@android:01040013", "@string/yes");

        //R.style

        //R.styleable

        //R.transition [DONE]
        keymap.put("@android:010f0003", "@transation/explode");
        keymap.put("@android:010f0002", "@transation/fade");
        keymap.put("@android:010f0001", "@transation/move");
        keymap.put("@android:010f0000", "@transation/no_transition");
        keymap.put("@android:010f0004", "@transation/slide_bottom");
        keymap.put("@android:010f0007", "@transation/slide_left");
        keymap.put("@android:010f0006", "@transation/slide_right");
        keymap.put("@android:010f0005", "@transation/slide_top");

        //R.xml [DONE]

        data = reverseAndroidRefs(data, keymap);

        //more remappings
        /*mapper = new String[]{
                "android:layout_height=\"-2\"",        "android:layout_height=\"wrap_content\"",
                "android:showAsAction=\"0x00000002\"", "android:showAsAction=\"SHOW_AS_ACTION_ALWAYS\"",
                "android:showAsAction=\"0x00000008\"", "android:showAsAction=\"SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW\"",
                "android:showAsAction=\"0x00000001\"", "android:showAsAction=\"SHOW_AS_ACTION_IF_ROOM\"",
                "android:showAsAction=\"0x00000000\"", "android:showAsAction=\"SHOW_AS_ACTION_NEVER\"",
                "android:showAsAction=\"0x00000004\"", "android:showAsAction=\"SHOW_AS_ACTION_WITH_TEXT\"",

                "android:configChanges=\"0x000000A0\"", "android:configChanges=\"0x000000A0\""
        };*/
        //data = reverseAndroidRefs(data, mapper);

        data = Util.prettyFormatXML(data);
        System.out.println(data);
    }

    private String reverseAndroidRefs(String data, HashMap<String, String> db) {
        List<String> allMatches = new ArrayList<String>();
        Matcher m = Pattern.compile(ANDROID_R_ID_REGEX)
                .matcher(data);
        while (m.find()) {
            allMatches.add(m.group());
        }
        //replace found data
        for (String match : allMatches){
            String decoded = (db.get(match) !=null) ? db.get(match) : match;
            data = data.replaceAll(match, decoded);
        }
        return data;
    }

    @Override
    public int getInitialArgumentCount(IAtomClass cls, IAtomMethod m) {
        return 0; //do not use arguments
    }

    @Override
    public Object getInitialArguments(IAtomClass cls, IAtomMethod m) {
        return null; //do not use arguments
    }

    @Override
    public IAtomClass[] getInitialDVMClass() {
        //only return developer class and skip known java jdk and android sdk classes

        IAtomClass[] alllist = currentProject.getInternalInfo().getAllClasses();
        ArrayList<IAtomClass> developerClasses = new ArrayList<>();
        for (IAtomClass cls : alllist) {
            if ( DroidefenseIntel.getInstance().isAndroidRclass(cls.getName()) )
                developerClasses.add(cls);
        }
        IAtomClass[] list = developerClasses.toArray(new IAtomClass[developerClasses.size()]);
        Log.write(LoggerType.TRACE, "Estimated node count: ");
        int nodes = 0;
        for (IAtomClass cls : list) {
            nodes += cls.getAllMethods().length;
        }
        Log.write(LoggerType.TRACE, nodes + " R nodes");
        return list;
    }

    @Override
    public IAtomMethod[] getInitialMethodToRun(IAtomClass dexClass) {
        return dexClass.getAllMethods();
    }

    @Override
    public strictfp void execute(boolean keepScanning) throws Throwable {

        Log.write(LoggerType.DEBUG, "Reading Android R references...");
        IAtomFrame frame = getCurrentFrame();
        IAtomMethod method = frame.getMethod();
        IAtomClass methodOwnerClass = method.getOwnerClass();

        Log.write(LoggerType.DEBUG, "Class name detected as: "+methodOwnerClass.getName());
        decodeFieldMap(methodOwnerClass.getStaticFieldMap());
    }

    private void decodeFieldMap(Hashtable staticFieldMap) {
        Iterator it = staticFieldMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, IAtomField> entry = (Map.Entry<String, IAtomField>) it.next();

            if(entry!=null){
                IAtomField field = entry.getValue();
                String name = field.getName();
                int value = field.getIntValue();
                String owner = field.getOwnerClass().getName();
                //clean owner from com/packagename/R$id to id
                owner = owner.split("\\$")[1];
                references.add(new AndroidRField(owner, name, value));
            }
        }
    }

    @Override
    public AbstractDVMThread reset() {
        //reset 'thread' status
        this.setStatus(STATUS_NOT_STARTED);
        this.removeFrames();
        this.timestamp = new ExecutionTimer();
        return this;
    }
}
