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

        //R.drawable

        //R.fraction

        //R.id

        //R.integer

        //R.interpolar

        //R.layout

        //R.menu

        //R.mipmap

        //R.plurals

        //R.raw

        //R.string

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
