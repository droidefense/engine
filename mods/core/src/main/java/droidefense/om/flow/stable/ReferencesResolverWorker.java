package droidefense.om.flow.stable;

import droidefense.om.machine.base.AbstractDVMThread;
import droidefense.om.machine.base.struct.fake.DVMTaintMethod;
import droidefense.om.machine.base.struct.generic.IAtomClass;
import droidefense.om.machine.base.struct.generic.IAtomField;
import droidefense.om.machine.base.struct.generic.IAtomFrame;
import droidefense.om.machine.base.struct.generic.IAtomMethod;
import droidefense.om.machine.base.struct.model.AndroidRField;
import droidefense.om.machine.inst.DalvikInstruction;
import droidefense.om.machine.inst.InstructionReturn;
import droidefense.rulengine.nodes.EntryPointNode;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.sdk.model.base.DroidefenseProject;
import droidefense.sdk.model.base.ExecutionTimer;
import droidefense.util.DroidefenseIntel;

import java.util.*;

public final strictfp class ReferencesResolverWorker extends AbstractDVMThread {

    private ArrayList<AndroidRField> references;

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
                //clean owner from com/haxor/R$id to id
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
