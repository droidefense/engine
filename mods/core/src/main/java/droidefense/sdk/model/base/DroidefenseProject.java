package droidefense.sdk.model.base;

import droidefense.analysis.base.AbstractAndroidAnalysis;
import droidefense.handler.DirScannerHandler;
import droidefense.handler.FileIOHandler;
import droidefense.handler.base.DirScannerFilter;
import droidefense.ml.MachineLearningResult;
import droidefense.om.helper.DexFileStatistics;
import droidefense.om.machine.base.DalvikVM;
import droidefense.om.machine.base.struct.generic.IAtomClass;
import droidefense.om.machine.base.struct.model.AndroidRField;
import droidefense.om.machine.base.struct.model.SharedPool;
import droidefense.om.machine.reader.DexHeaderReader;
import droidefense.reporting.AbstractReporter;
import droidefense.reporting.HTMLReporter;
import droidefense.reporting.BeautifiedJSONReporter;
import droidefense.rulengine.Rule;
import droidefense.rulengine.map.BasicCFGFlowMap;
import droidefense.rulengine.base.AbstractFlowMap;
import droidefense.sdk.AbstractDynamicPlugin;
import droidefense.sdk.AbstractStaticPlugin;
import droidefense.sdk.helpers.DroidDefenseParams;
import droidefense.sdk.helpers.InternalConstant;
import droidefense.sdk.helpers.Util;
import droidefense.sdk.log4j.Log;
import droidefense.sdk.log4j.LoggerType;
import droidefense.sdk.manifest.Manifest;
import droidefense.sdk.manifest.UsesPermission;
import droidefense.sdk.manifest.base.AbstractManifestClass;
import droidefense.sdk.model.certificate.CertificateModel;
import droidefense.sdk.model.dex.DexBodyModel;
import droidefense.sdk.model.dex.OpcodeInformation;
import droidefense.sdk.model.enums.MalwareResultEnum;
import droidefense.sdk.model.enums.OverallResultEnum;
import droidefense.sdk.model.enums.PrivacyResultEnum;
import droidefense.sdk.model.enums.SDK_VERSION;
import droidefense.sdk.model.holder.DynamicInfo;
import droidefense.sdk.model.holder.InternalInfo;
import droidefense.sdk.model.holder.StaticInfo;
import droidefense.sdk.model.holder.StringInfo;
import droidefense.sdk.model.io.AbstractHashedFile;
import droidefense.sdk.model.io.LocalApkFile;
import droidefense.util.JsonStyle;
import droidefense.vfs.model.impl.VirtualFile;
import droidefense.vfs.model.impl.VirtualFileSystem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergio on 16/2/16.
 */
public final class DroidefenseProject implements Serializable {

    private static final Map<LocalApkFile, DroidefenseProject> projectMap = new HashMap<>();
    private static final String VFS_ROOT_FOLDER = "";

    /**
     * Virtual file system for sample files
     */
    private transient VirtualFileSystem vfs;

    /**
     * Timestamp from creation to end
     */
    private final ExecutionTimer scanTime;

    /**
     * Sample info
     */
    private LocalApkFile sample;

    /**
     * Currently used analyzers on this .apk
     */
    private final ArrayList<AbstractAndroidAnalysis> usedAnalyzers;

    /**
     * Current .apk static information holder
     */
    private final StaticInfo staticInfo;
    /**
     * Current .apk static plugin information holder
     */
    private final ArrayList<AbstractStaticPlugin> staticInfoPlugins;
    /**
     * Current .apk dynamic plugin information holder
     */
    private final ArrayList<AbstractDynamicPlugin> dynamicInfoPlugins;
    /**
     * Current .apk internal information holder
     */
    private InternalInfo internalInfo;

    /**
     * Dynamic .apk dynamuc information holder
     */
    private DynamicInfo dynamicInfo;

    /**
     * Result of malware prediction
     */
    private MalwareResultEnum malwareResult;

    /**
     * Result of privacy prediction
     */
    private PrivacyResultEnum privacyResult;

    private OverallResultEnum overallResult;

    /**
     * Result of JADX decompilation
     */
    private boolean successfullDecompilation;

    /**
     * Dex file basic counting statistics
     */
    private DexFileStatistics statistics;

    /**
     * Result of opcode data
     */
    private transient OpcodeInformation opcodeInfo;

    /**
     * Result of decode android.R references
     */
    private transient ArrayList<AndroidRField> androidReferences;

    /**
     * Natural language summary section
     */
    private String summary;

    //set flow maps as transient
    private transient AbstractFlowMap normalControlFlowMap;
    private transient AbstractFlowMap reflectedFlowMap;
    private transient AbstractFlowMap multiFlowMap;
    private transient AbstractFlowMap followCallsMap;

    private MachineLearningResult machineLearningResult;
    private boolean headerReaded;
    private boolean correctUnpacked;
    private boolean correctDecoded;
    private boolean staticAnalysisDone;
    private boolean dynamicAnalysisDone;

    private transient DexHeaderReader dexHeaderReader;
    private transient IAtomClass[] dynamicEntryPoints;
    private transient HashMap<String, IAtomClass> classMap;

    //config params
    private transient boolean settingAutoOpen;
    private transient String settingsReportType;
    private transient DalvikVM dalvikMachine;

    public DroidefenseProject() {
        //create new timestamp now
        scanTime = new ExecutionTimer();

        //create holders
        this.staticInfo = new StaticInfo();
        this.staticInfoPlugins = new ArrayList<>();
        this.dynamicInfoPlugins = new ArrayList<>();

        this.internalInfo = new InternalInfo();
        this.opcodeInfo = new OpcodeInformation();
        this.dynamicInfo = new DynamicInfo();

        //init data structs
        usedAnalyzers = new ArrayList<>();
        vfs = new VirtualFileSystem();

        //init enums
        this.overallResult = OverallResultEnum.UNKNOWN;
        this.malwareResult = MalwareResultEnum.UNKNOWN;

        this.classMap = new HashMap<>();

        setSummary("No summary created yet!");

        //save apk reference
        //sampleApk = file;
        //add this currentProject to running projects holder
        //projectMap.put(file, this);
    }

    public static DroidefenseProject getProject(LocalApkFile apk) {
        return projectMap.get(apk);
    }

    public void analyze(AbstractAndroidAnalysis analyzer) {
        //add this analyzer to used analyzer stack
        usedAnalyzers.add(analyzer);
        //set base project and target app
        analyzer.setApkFile(sample);
        analyzer.setCurrentProject(this);
        //start analyzer
        analyzer.analyzeCode();
    }

    //DELEGATE METHODS

    //STATIC INFORMATION GETTERS & SETTERS

    public boolean hasMainClass() {
        return staticInfo.getMainClassName() != null;
    }

    public String getMainClassName() {
        return staticInfo.getMainClassName();
    }

    public int getNumberOfDexFiles() {
        return staticInfo.getNumberOfDexFiles();
    }

    public void setNumberOfDexFiles(int numberOfDexFiles) {
        staticInfo.setNumberOfDexFiles(numberOfDexFiles);
    }

    public ArrayList<VirtualFile> getAppFiles() {
        return staticInfo.getAppFiles();
    }

    public void setAppFiles(ArrayList<VirtualFile> files) {
        staticInfo.setAppFiles(files);
    }

    public long getStartTime() {
        return scanTime.getStart();
    }

    public long getEndTime() {
        return scanTime.getEnd();
    }

    public ArrayList<AbstractAndroidAnalysis> getUsedAnalyzers() {
        return usedAnalyzers;
    }

    public void setCertNumber(int certNumber) {
        this.staticInfo.setCertNumber(certNumber);
    }

    public void addCertInfo(CertificateModel certInfo) {
        staticInfo.addCertInfo(certInfo);
    }

    public byte[] getManifestFile() {
        return this.vfs.get(VFS_ROOT_FOLDER).getItem("AndroidManifest.xml").getContent();
    }

    public void setManifestFile(AbstractHashedFile manifest) {
        this.staticInfo.setManifestFile(manifest);
    }

    public Manifest getManifestInfo() {
        Manifest man = this.staticInfo.getManifestInfo();
        return (man == null) ? new Manifest() : man;
    }

    public void setManifestInfo(Manifest manifestInfo) {
        this.staticInfo.setManifestInfo(manifestInfo);
    }

    public void stop() {
        this.scanTime.stop();
    }

    public void printProjectInfo() {
        Log.write(LoggerType.TRACE, " -- PROJECT OUTPUT -- ");
        Log.write(LoggerType.TRACE, "");
        Log.write(LoggerType.TRACE, getProjectAsJson());
    }

    public void setMainClass(String name) {
        this.staticInfo.setMainClassName(name);
    }

    public void setNumberofDex(int i) {
        this.staticInfo.setNumberOfDexFiles(i);
    }

    public ArrayList<AbstractHashedFile> getDexList() {
        return this.staticInfo.getDexList();
    }

    public void setDexList(ArrayList<AbstractHashedFile> dexList) {
        this.staticInfo.setDexList(dexList);
        this.staticInfo.setDexFileReaded(true);
    }

    public void addDexData(String filePath, AbstractHashedFile file) {
        this.staticInfo.addDexData(filePath, file);
    }

    //DYNAMIC INFORMATION GETTERS & SETTERS

    public byte[] getDexData(AbstractHashedFile file) throws IOException {
        return this.staticInfo.getDexData(file);
    }

    public StaticInfo getStaticInfo() {
        return staticInfo;
    }

    public InternalInfo getInternalInfo() {
        return internalInfo;
    }

    public void setProjectFolderName(String folder) {
        this.staticInfo.setProjectFolderName(folder);
    }

    public void setFolderCount(int nfolder) {
        this.staticInfo.setFoldersNumber(nfolder);
    }

    public void setFilesCount(int nfiles) {
        this.staticInfo.setFilesNumber(nfiles);
    }

    public void addStaticPlugin(AbstractStaticPlugin plugin) {
        staticInfoPlugins.add(plugin);
    }

    public void addDynamicPlugin(AbstractDynamicPlugin plugin) {
        dynamicInfoPlugins.add(plugin);
    }

    public String getProjectAsJson() {
        return Util.toJson(this, JsonStyle.JSON_COMPRESSED);
    }

    public String getProjectName() {
        return getSample().getThisFile().getName().replace(".apk", "");
    }

    public void setRawFiles(ArrayList<AbstractHashedFile> rawFiles) {
        this.staticInfo.setRawFiles(rawFiles);
    }

    public void setAssetsFiles(ArrayList<AbstractHashedFile> assetFiles) {
        this.staticInfo.setAssetFiles(assetFiles);
    }

    public void setLibFiles(ArrayList<AbstractHashedFile> libFiles) {
        this.staticInfo.setLibFiles(libFiles);
    }

    public void setClassWithPackageName(boolean b) {
        this.staticInfo.setClassNameWithPkgName(b);
    }

    public boolean isSuccessfullDecompilation() {
        return successfullDecompilation;
    }

    public void setSuccessfullDecompilation(boolean successfullDecompilation) {
        this.successfullDecompilation = successfullDecompilation;
    }

    /*
    public void addDexClass(String name, IAtomClass newClass) {
    }

    public IAtomClass[] getAllClasses() {
        //return this.internalInfo.getAllClasses();
        return new IAtomClass[0];
    }
    */

    public void setEntryPoints(ArrayList<AbstractManifestClass> entryArray) {
        this.internalInfo.setEntryPoints(entryArray);
    }

    /*
        public void addDexClass(String name, IAtomClass newClass) {
            this.internalInfo.addDexClass(name, newClass);
        }
     */

    public boolean isDexFileReaded() {
        return this.staticInfo.isDexFileReaded();
    }

    public void setDexFileReaded(boolean b) {
        this.staticInfo.setDexFileReaded(b);
    }

    /*

    public boolean hasDexClass(String name) {
        return this.internalInfo.hasDexClass(name);
    }

    public IAtomClass getDexClass(String name) {
        return null;
        //return this.internalInfo.getDexClass(name);
    }

    public void addDexInfo(DexClassReader dexClassReader) {
        this.internalInfo.addDexInfo(dexClassReader);
    }

    public void setDynamicEntryPoints(IAtomClass[] entryArray) {
        this.internalInfo.setDynamicEntryPoints(entryArray);
    }
    */

    public void setOpCodesCount(int[] codes) {
        this.opcodeInfo.setOpcodesCount(codes);
    }

    public String getOpCodeStats() {
        return Util.toJson(this.opcodeInfo, JsonStyle.JSON_BEAUTY);
    }

    public void updateMetadata() {
        //save metadata info
        //try {
        File meta = new File(FileIOHandler.getUnpackOutputFile().getAbsolutePath() + File.separator + this.sample.getSha256() + File.separator + InternalConstant.ANALYSIS_METADATA_FILE);
            /*
            if (!meta.exists()) {
                HashChecking hc = new HashChecking(getSample().getSha256());
                hc.setDate(new Date(System.currentTimeMillis()));
                hc.setResult("Not calculated");
                FileIOHandler.saveAsRAW(hc, InternalConstant.ANALYSIS_METADATA_FILE, meta.getParentFile());
            } else {
                HashChecking metaFile = (HashChecking) FileIOHandler.readAsRAW(meta);
                if (metaFile != null) {
                    metaFile.setDate(new Date(System.currentTimeMillis()));
                    metaFile.setResult("Not calculated");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        */
    }

    public void addDexFileStatistics(DexFileStatistics statistics) {
        this.statistics = statistics;
    }

    public void save() {
        try {
            String path = FileIOHandler.getUnpackOutputFile().getAbsolutePath();
            String samplehash = this.sample.getSha256();
            File projectFile = new File(path + File.separator + samplehash + File.separator + DroidDefenseParams.getInstance().PROJECT_DATA_FILE);
            FileIOHandler.saveAsRAW(this, DroidDefenseParams.getInstance().PROJECT_DATA_FILE, projectFile.getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInstructionCount(int total) {
        this.opcodeInfo.setInstructionCount(total);
    }

    public BasicCFGFlowMap getNormalControlFlowMap() {
        //return empty object if not exists.
        if (normalControlFlowMap == null)
            return new BasicCFGFlowMap();
        return (BasicCFGFlowMap) normalControlFlowMap;
    }

    public void setNormalControlFlowMap(AbstractFlowMap normalControlFlowMap) {
        this.normalControlFlowMap = normalControlFlowMap;
    }

    public BasicCFGFlowMap getReflectedFlowMap() {
        //return empty object if not exists.
        if (reflectedFlowMap == null)
            return new BasicCFGFlowMap();
        return (BasicCFGFlowMap) reflectedFlowMap;
    }

    public void setReflectedFlowMap(AbstractFlowMap reflectedFlowMap) {
        this.reflectedFlowMap = reflectedFlowMap;
    }

    public void setMultiFlowMap(AbstractFlowMap multiFlowMap) {
        this.multiFlowMap = multiFlowMap;
    }

    public String getProjectId() {
        return this.sample.getSha256();
    }

    public OpcodeInformation getOpcodeInfo() {
        return opcodeInfo;
    }

    public void writeNaturalReport() {
        StringBuffer data = new StringBuffer();
        String pkg = getManifestInfo().getPackageName() == null ? "unknown" : getManifestInfo().getPackageName();
        String url = "https://www.virustotal.com/es/file/" + getProjectId().toLowerCase() + "/analysis/";
        int entries = getInternalInfo().getEntryPoints().size();
        data.append("<p>Analyzed application is called <strong>" + this.sample.getFilename() + "</strong> but it's internal name is <tt>" + pkg + "</tt></p>\n");
        data.append("\n");
        data.append("<p>Its file signature as unique SHA 256 bits identifier is <tt>" + getProjectId() + "</tt></p>\n");
        data.append("\n");
        data.append("<p>and VirusTotal result can be found at: <a href=\"" + url + "\" target=\"_blank\">" + url + "</a></p>\n");
        data.append("\n");

        Manifest info = getManifestInfo();
        if (info != null) {
            ArrayList<UsesPermission> permissionList = info.getUsesPermissionList();
            if (permissionList != null && !permissionList.isEmpty()) {
                data.append("<p>A quick overview of the application shows current declared permissions:\n");
                data.append("<ul>");
                for (UsesPermission p : permissionList) {
                    data.append("<li>");
                    data.append(p.getName());
                    data.append("</li>");
                }
                data.append("</ul>");
            } else {
                data.append("<p>This application has no declared permissions. This usually is a clear indicator of not being a security nor privacy risk application.\n");
            }
        } else {
            //no permissions found
            data.append("<p>The application has no declared permissions which, in most cases, means that this applications is safe to use it and to install it. However be aware that this application can have inside files that may share to other applications leaking information or sharing viruses, trojans and malware.</p>\n");
        }
        //set entry points
        data.append("\n");

        if (entries == 0) {
            data.append("<p>We have DO NOT have detected any valid entry point in this application<p>");
            data.append("<p>Since this is not the usual behaviour of android apps, we have flagged it as suspicious or corrupt sample</p>");
        } else if (entries == 1) {
            data.append("<p>We have detected 1 entry point in this application, which means that it runs from only one main execution point.</p>");
        } else if (entries > 1) {
            data.append("<p>We have detected ");
            data.append(entries);
            data.append(" entry points in this application, which means that it runs from ");
            data.append(entries);
            data.append(" different main execution points.</p>");
        }
        data.append("\n");
        data.append("<p>Please, take a closer look to analysis result to have a deep understanding of what the application attempts to do.</p>");
        setSummary(data.toString());
    }

    public void setCertificateFile(AbstractHashedFile certFile) {
        this.staticInfo.setCertFile(certFile);
    }

    public void setMatchedRules(ArrayList<Rule> matchedRules) {
        this.dynamicInfo.setMatchedRules(matchedRules);
    }

    public void setStringAnalysisResult(StringInfo stringContent) {
        this.dynamicInfo.setStringAnalysisResult(stringContent);
    }

    public AbstractFlowMap getFollowCallsMap() {
        //return empty object if not exists.
        if (followCallsMap == null)
            return new BasicCFGFlowMap();
        return followCallsMap;
    }

    public void setFollowCallsMap(AbstractFlowMap followCallsMap) {
        this.followCallsMap = followCallsMap;
    }

    public MachineLearningResult getMachineLearningResult() {
        return machineLearningResult;
    }

    public void setMachineLearningResult(MachineLearningResult machineLearningResult) {
        this.machineLearningResult = machineLearningResult;
        if(this.machineLearningResult.getRatio()>0.7){
            this.malwareResult = MalwareResultEnum.MALWARE;
        }
        else{
            this.malwareResult = MalwareResultEnum.GOODWARE;
        }
    }

    public void finish() {
        Log.write(LoggerType.TRACE, "Droidefense project finished");
        Log.write(LoggerType.TRACE, "Generating report template...");

        //cleanup
        internalInfo.cleanup();

        //stop timer
        this.stop();

        //generate template
        this.generateReportTemplate();

        Log.write(LoggerType.TRACE, "Saving scan results...");

        //update analysis metadata file
        this.updateMetadata();

        //save info as jsons
        this.save();

        //save report as java object
        FileIOHandler.saveProjectReport(this);

        Log.write(LoggerType.TRACE, "Sample analysis done.");
    }

    private void generateReportTemplate() {

        AbstractReporter reporter;
        switch (getSettingsReportType()) {
            case "html":
                reporter = new HTMLReporter();
                break;
            case "json": {
                String jsonData = Util.toJson(this, JsonStyle.JSON_BEAUTY);
                File reportFile = FileIOHandler.getReportFolder(getProjectId() + ".json");
                reporter = new BeautifiedJSONReporter(reportFile, jsonData);
                break;
            }
            default: {
                //default output as beautified json
                String jsonData = Util.toJson(this, JsonStyle.JSON_BEAUTY);
                File reportFile = FileIOHandler.getReportFolder(getProjectId() + ".json");
                reporter = new BeautifiedJSONReporter(reportFile, jsonData);
                break;
            }
        }
        reporter.generateReport();

        if (isSettingAutoOpen()) {
            reporter.open();
        }
    }

    private String getAppLogoasB64() {
        String logoName = this.getManifestInfo().getApplication().getIcon();
        logoName = logoName.replace("@", "");
        String unpackPath = FileIOHandler.getUnpackOutputPath(this.sample);
        String[] data = logoName.split("/");
        String baseDir = unpackPath + File.separator + "res" + File.separator + data[0];
        String finalLogoName = data[1];
        DirScannerHandler handler = new DirScannerHandler(new File(baseDir), false, new DirScannerFilter() {
            @Override
            public boolean addFile(File f) {
                String name = f.getName();
                return name.equalsIgnoreCase(finalLogoName + ".png")
                        || name.equalsIgnoreCase(finalLogoName + ".jpg")
                        || name.equalsIgnoreCase(finalLogoName + ".gif");
            }
        });
        handler.doTheJob();
        ArrayList<AbstractHashedFile> files = handler.getFiles();
        if (files != null && !files.isEmpty()) {
            //read logo and convert to b64 string
            try {
                AbstractHashedFile file = (AbstractHashedFile) files.get(0).getThisFile();
                byte[] imageBytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                return Base64.getEncoder().encodeToString(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //otherwise, return default-logo.png
        return "iVBORw0KGgoAAAANSUhEUgAAAJYAAACWCAIAAACzY+a1AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OTMxMjBCRDk4Rjk3MTFFMjhBQkREMDg4MEE1N0Q0QTUiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OTMxMjBCREE4Rjk3MTFFMjhBQkREMDg4MEE1N0Q0QTUiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo5MzEyMEJENzhGOTcxMUUyOEFCREQwODgwQTU3RDRBNSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo5MzEyMEJEODhGOTcxMUUyOEFCREQwODgwQTU3RDRBNSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PooYpqoAABCpSURBVHja7F15dJvVlf9W7ZK1Wd6XOFYcx3HihGw04GSSA6UNpGRoShPmwCmHHuhyDgVKOxym05npAqVQprO0p4W2BxqGtswUSlhamAaSNGASsGNncex4ibfYkq19+fTp2+ZKohnH8fJJsWT58/vhP2Lz9N599/fuffc+vQWXJAlDWMzAEYWIQoQFBpGfYrmCbYe7Ho1x3vwRCYQBkUCwfNMVlZ8UHu169Hj/myw3cePqn+WJSEe6HvnfMz8fD7V9dsMbyArnRrl1a5lZ3+36/UV/Sz7IA2KAMGUWfbllK3KksrCh+iG7cU2cDx3v+0E+yANigDAgEgiGKJTn30nNhmUPkLhq0PNOj/vVhRUGBAAxQBgQCQRDFMpFXfHecuv1vBg70fekJAkLJQY0DQKAGCAMiIQi0vSwueabNGlwBVs7hn6xUDJA0yAAiAHCoKQig6CmebnjZlHi2wb/M8b7FyCR4P3QNAgAYoAwiMIMDVFLF/oi3a0XfpzWBwUxzvL+COsKx0bgB/4Bv8If06oEGoWmQYC8NcH8zQsvwWaoX1W676OBfz89/FxD2V0F2uppizHxCX+01xftCTADUXYMfo1xPl5kBJFLzaM4TpIETRFaDW3Rquw6dXGBttKic5p1y+HXaesMMBegUQnDQAAQI29VtAgW2KJx14st20ChjeV339Dwkynp2qi/xR086Y2cj8bHWD6Eg2PBaZJQww+BkxhO4BieiEoS/4miJAgiCz+ixEG31ZRRpyq26lc4TE2l5i0l5s2TK3/7zJdPDf8SBs2+LYd1qiJE4VXhRP/Tf+l+VEUa925+y2FcOx7q6Bt/c9h7xBM+y/JBmtSrqQKK1CU4SwfAKC9EWT7ACRGowWZYBWFnTeGnCo1r3KH2lz64MS6ErlvxvY3LHshn5SyaZe5XWvcMeQ9XWneYdNW9roMMN66hrVraRuBUwsKuUgsYDjELw3linBdmvtqiWwLRC4PeQxXW5lvXv5LnmqGwRQK7YbUr0DoRPgPOU68u1qkLJUn82ENeffKHSTBZ6lQOqJkXYp0Xf6OijGD0dkNj/mtmEVjhqP/4if6nwGfSlBHPZVKPSRwfthtXbah+qMS8CVGYCSDoOHb+n08P/wqiygJdDY4TKcvLkWoSzQn+aD9MsavLv7DV+W0IlBCFaQAc5uGub7oDbWZ9LQQsC7XGBg4Wgh1/pMdRsG5b3ROll0etiMIZcXLwp+/1fAfUZ9ZWiwnLW1ghITUh/MwFGEafqP1WU+WXEIVz4FDnAx1DzxZol2loMwSKeSIVhL4xzh9g+tdU3LOj/mlE4YyJ2uvtd/S6X7MbGgiCzuXMJ3N2FEUOouLljpt3rX0h3TRU+RQKIvtK623DvqOFhsZkqpCfkzQOSeR4uKPCsu0z6/+HJFSIwr9G8JL4cuutkLw7jGvyYPKbe2p0hzoqrNv2XPOH1AIeohB7vf3OHtcrhaa1s0SeCb+K47lRWXJNFfJ9YpZIdTzY7ize8+k1zyEKsWPnv32878kiU5OU0t4VcUScD0XYMfBagsRRhNaoKc2apSYsLBS7yIsMidOCGNeri1WUcbqoKjGS3MGTm2oe3ur8pyVN4XnXy6+3/50N4pdE4jwNf9G4G8eIxoq7i0zXMPHxztHfjvpbINPPRqYIthWI9pWat6wsuV2rKhwLfnR66JcSJupUjitZBH8gSHFPuHPX2gPOoluXKIVBZvC/Wq5TUQYNbZ02/uTFGGTWn17z/KWcGhzc6yfvGPb9xaSpBOXOpy4wIhgbKLc072o6cMldX/S3vNFxl4o0kIR62hiViXs5Ibx/yzGTtmJhsp2FTgHvh0BUS9tnyh/CseHG8rsnr4mAcretfJwiNMDu/AoDFVKEDiqfPN2CRYIAodjQTNOzTmWHLhzq/NqCJawLyN/Zi7/uH3/LonPOnL+Dj5CKTOum/NWoqbToV8DYn195oEKo1qiZakwgQNJXSTPksjx0oX/8j2cvvrC0KGQ5f0vP4wXaqlmjErAGKcxenPLXuBBORjfzvKUTKoywo1eOjKQAEjZbJCyZtJUtvd9nucASorB14N/AO2lVttm/8IM4omPw2Slq/aj/6XBshCb18ysSVAjVftj/r5eZJh9uH3wGxJg9AwF3GmKGoFNLJZwBG3qxpZkidTSpm2OI4VSQGbAZGzYte9hubIBhfm70N+1Dz0CsP20Ee5XaECUOZFtb8cWVJZ9X0wUToTPH+3/oCZ0xaavmXK3lhCgvMPu2HAbZlE9hS+9jH/Q9XmhcIycxABbBOGCkGzRlXNKFwnQF8WGWkgqITcA9AA00aYB2IbSBduWstieS/VDHlppHNi//e4VTCEnCb4/vhGxdTRXIDvchA+N5IUokNxJmf/kNh9ReFDnwE2Q6e3NYPqCijJ/fdIiay7ss7rmwb/wNb/ichraktdxF4CRoh0qEMDkYcxI0BM1Bo2ntzdFQFm+4Czqo8HCmf/xPMEgVeUAc+KZILSQYSqYwwrrGAh/q1UX5/V1E5iRC16CD0bhbsRSO+I9BjJCcz5QJ6FooNjLiO6ZYCt2Btr/m7EpFomuuQJtiKfSEO1WUSaFe9GNfCh30RDqVSSETH4eUS0UZMEUDOhhihpj4hAIpDDADDOeZ97XNfANJqBluIsBcUCCF4dgoLzAETiibQsgmoZsRdlSBFMa4ieQ5WxxTOHDoJhP35CgGzmXP4nwYy9/dhfMc1MSFkAIpDDD9/qiHIGjFEwjdDERzNBfmdJl72Ht0PNSups2Kp5Dl/IXGteXW6xcXhdISmOTm399evdKuikJwjL2ug5DGRliXKPFqymzW1VTYtldatyNyZsGg590h77v+aB/L+wmc0quLbPr65UW7Z7rPI1sUtvQ+dmbkeQidaVJPkTocIwSRZfkAZEVV9h3XO79XoFs25SOuYNtE6FRydUbhiPNBu7Hxyo1bgWj/0e5HBzyHIGRVU6bEF9eYyAtRTojo1SUNZXduWf5ILigEg3vt5P4e90GLrhYmtsnfnifP/vC+aDdY5C1NL065BuSdzoeOnf+R1VCieAq94dGtzgf/pv6pyX8c9X9w8OQ+GOUWnZMgqMn7LnGchOnTF+2pdey+uekFMM3sRqRvnb631/1asWm9lLxl7jLXLonAot2wOsgMvNp2++2bD4FrvfR/tSq7Re9I7lpT+hQnCVNuJAK3+Ye2z+EYbjc0gA1M2TcL5cE5gUp73QffOn3fTY3PZjG1P3vxwNmRA44Zzj9cMlOTtgqcw7vnvn6l3S+Zn8vw7rmHwGGatJWzbZrFMFDs2ZFfp7UlNT0KYbZrH/yZUVuR3O8sze5sLXrnoOfQwMTbKH4BJQx43gGFzLWNSgLFgnpByaDqrFA45D3ijXTrVA45O0ogwIEsvsf9GqIQ4gaSoOUcq0tuSXV4I13D3iNZodAVbBUlQeYJP4i1tLTdG+7Mn/PyCwLovid8LnFuRN7KYuoqqjHZV/GnRyFETTCa5G/qgsJxIQyfWsoUQvc5IUzKXlYE9ZKEiuV8WaFQSn81QUoOwyVuhZnk67LtJD0KMzgkjWM4vrQX3vDpAtR5VDWBISxyIAoRhQiIQgREIaIQAVGIgChEQBQiChEQhQiIQgREIaIQAVGIgChEQBQiChEQhQiIQgREIaIQAVGIgChEQBQiChEQhQiIQgREIaIQAVGIgChEQBQiChEUTqGUyW2wkrQk7pCdXWlSRp/KAoVz3hg0fRs4taStJJPuS9k6a6+mLYIYl1+7IHIqyqCmC5YyhdB9UAKoQr6dgJLlP0uWHoVFBesJ2Y/BgSgMN2E11CffilzKVkhb9fWgCtlXLkmgZFB1ViissDRb9SujrEvmXVSiyDsdu1HEUVu0WxQ5ebee4aBeUHK5pTkrFJKEqqny3hA7POcdQjCOvJHuKvvOSttORGGVbSeowhfpnmteTNwYFIqNNFXdB6rOVlJRX7q/oewud7ANTw2aGfgLMBfUlHH7yicRfylsX/kUTRlBLTOzmHyvK9i2uvyu+pJ92c0Lb2z4qbPo1rFgK8sHcJy8TAqckCRhPHSKxFW7172U2WXTigSo4jPrXgK1gHJARfjlT+YkbgXmA+7gRyuK9tzQ8JO0as4k3If2bm568XjfE6dHnpsInUo9jI1jBC/G4nyIIjW1jluuW/Fdk7YSMTcZxQUbbt/856Pd3xrwvM0LsdS7thImJp/ijho0ZVud39lU83C61WaesW2q+UZ96b4e16uecGckPgaRC6QcZt2yCuv2CmszImxamLRVu9Y+P+Q9krwhv5/lfARB6VXFNkM9hDxGTUUGdV5V0g1Nrqv6CiImXcAQn8dRjtZIF3/eiVSAKEwDS2q9O2edzSmFma2SL1IGc3aRbk4p1NDm5C3Hyn+CErqpydUDcTml0GZYRRJqDBOVTqEI3bQZGhRIYal5i0VXm3whVcnv2kMHLTpn6eXvVSmEQhib9aX7g7EhXLkvMkPXoIOrSvcn/U1OWszlK6IJFyMJ/33iJneo3aavFyVOYfwROO0JdzpMTXs3vjll9Vg5FAKCzMDvTnyS5bxWfZ2YeEBMUoj54YQv0qWhrXs3/smUw9fhFoBCLPESXO+bp+4e839o0lZCnz/OoxYteVjivXdviBksNm+8qfEXZt3ynDa/IBRiyRf7jvc90e16GYxSkkQi4Xamxjg6tQNC8zR2nSS+6hKDsUFJFGYrRpAmTWWqsOxZnI5x/ijrvrIfYvKbIzC7FUV/u6nmG7mf5heMwhRinG/Yd9QX6YZ/pJ6vnCzbROhUgBmgSZ3siZYncKrMci1JaGYpJoixEd/7qcIya+aEaIG2ym5snOwtUgKDF7HoneWW6+VvWFIUhbPjvfP/0jr4H5CHyCwfF4IqsuCOa4/NWfKF97fGhYCKlPuqsC/as77yq59w/mM+xlD5PMnwYiytTZh4sjtMfHz2YqkCeDp9BzFAmDwNg/M7UsjEQ8w5w8mfAq9eGEQhAqIQUYiAKERAFCIgChGFCIhCBEQhAqIQUYiAKERAFCIgChGFCIjCDJHZZVMyvtjL7nVMiML/R/onSxI3RsgjUEp3RzmerzvQ85pCmtSLie1rcnUnSgJFqFTUHDtioABJqKCwfPpADJrSIwrThtVQJ2Gi7MGPs5zPpK2ec8cbFCjQVrO8X+bgSJ6ok6z6OkRh2qiy7TBqKlg+KEfXOE5AySp5NxVVWXewXEDenk8cqjVqyvP2DqS8plBD2+qKP+uL9hBznU/AcTIcGwFDWVF8m5yaV5TcBoXhI3OefICmQYC64r3aj3edIwrTxMZlDxab1nsjXbPcxQc0xPlghHU11z0G06e8WdbQXPf9SHwszodmYREahaZBABADJRUZgiJ1u9YeAHMcD3Vg02z+xMFKIuyoN9K9Y9WPqu03yK+52n7jjpVPA0MRduzK4wCphqBRLW0DAShSm79xez7v5r6EUGzk0Nn7Bzx/Jgm1TmVPHt1LHIYG44vGJ8y6ZdfW/gP4ugxq7hr93Xu93w1GL2hVdohUkzuPJV5kmfiEILIws+5c9WODpiyvU69FQWEK3a7f97oO+qM9Mc4vSQJE+Xp1SZll65qKe8BWMq6W4TwdQ8+M+N4Da+b4CPhVDW0262pri3Y7i/bkv1oWE4UpsHwgHBuVpMR9YcZ5tQ+wdUhLcJwyaErU1KK5yXjxUYiAKFQa/k+AAQBjUKg4+Emi3QAAAABJRU5ErkJggg==";
    }

    public boolean isHeaderReaded() {
        return headerReaded;
    }

    public void setHeaderReaded(boolean headerReaded) {
        this.headerReaded = headerReaded;
    }

    /*
    public DexHeaderReader getDexHeaderReader() {
        return dexHeaderReader;
    }

    public void setDexHeaderReader(DexHeaderReader dexHeaderReader) {
        this.dexHeaderReader = dexHeaderReader;
    }
    */
    public String getSummary() {
        return summary;
    }

    private void setSummary(String data) {
        if (data == null)
            data = "No expert information provided";
        // /summary = Base64.getEncoder().encodeToString(data.getBytes());
        summary = data;
    }

    public VirtualFileSystem getVFS() {
        return this.vfs;
    }

    public void setVFS(VirtualFileSystem vfs) {
        this.vfs = vfs;
    }

    public ArrayList<AbstractHashedFile> getOtherFiles() {
        return this.staticInfo.getOtherFiles();
    }

    public void setOtherFiles(ArrayList<AbstractHashedFile> otherFiles) {
        this.staticInfo.setOtherFiles(otherFiles);
    }

    public void setDefaultFiles(ArrayList<AbstractHashedFile> defaultFiles) {
        this.staticInfo.setDefaultFiles(defaultFiles);
    }

    public void setPrivacyResult(PrivacyResultEnum privacyResult) {
        this.privacyResult = privacyResult;
    }

    public boolean isCorrectUnpacked() {
        return correctUnpacked;
    }

    public void setCorrectUnpacked(boolean correctUnpacked) {
        this.correctUnpacked = correctUnpacked;
    }

    public boolean isCorrectDecoded() {
        return correctDecoded;
    }

    public void setCorrectDecoded(boolean correctDecoded) {
        this.correctDecoded = correctDecoded;
    }

    public boolean isStaticAnalysisDone() {
        return staticAnalysisDone;
    }

    public void setStaticAnalysisDone(boolean staticAnalysisDone) {
        this.staticAnalysisDone = staticAnalysisDone;
    }

    public boolean isDynamicAnalysisDone() {
        return dynamicAnalysisDone;
    }

    public void setDynamicAnalysisDone(boolean dynamicAnalysisDone) {
        this.dynamicAnalysisDone = dynamicAnalysisDone;
    }

    public void setMinVersionWindow(int minSdkVersion) {
        this.staticInfo.setMinimum(SDK_VERSION.getSdkVersion(minSdkVersion));
    }

    public void setMaxVersionWindow(int maxSdkVersion) {
        this.staticInfo.setMaximum(SDK_VERSION.getSdkVersion(maxSdkVersion));
    }

    public void setTargetVersionWindow(int targetSdkVersion) {
        this.staticInfo.setTarget(SDK_VERSION.getSdkVersion(targetSdkVersion));
    }

    public DexHeaderReader getDexHeaderReader() {
        return dexHeaderReader;
    }

    public void setDexHeaderReader(DexHeaderReader dexHeaderReader) {
        this.dexHeaderReader = dexHeaderReader;
    }

    public void setMagicNumber(String s) {

    }

    public String[] getInternalStrings() {
        //todo
        return null;
    }

    public void addDexBodyModel(DexBodyModel dexBodyModel) {
        this.staticInfo.addDexBodyModel(dexBodyModel);
    }

    public IAtomClass[] getDynamicEntryPoints() {
        return dynamicEntryPoints;
    }

    public void setDynamicEntryPoints(IAtomClass[] dynamicEntryPoints) {
        this.dynamicEntryPoints = dynamicEntryPoints;
    }

    public IAtomClass[] getListClasses() {
        return (IAtomClass[]) this.classMap.values().toArray();
    }

    public void addDexClass(String name, IAtomClass newClass) {
        this.classMap.put(name, newClass);
    }

    public void setPool(SharedPool pool) {
        this.internalInfo.setPool(pool);
    }

    public void setSample(LocalApkFile sample) {
        this.sample = sample;
    }

    public LocalApkFile getSample() {
        return sample;
    }


    public void setSettingAutoOpen(boolean settingAutoOpen) {
        this.settingAutoOpen = settingAutoOpen;
    }

    public boolean isSettingAutoOpen() {
        return settingAutoOpen;
    }

    public String getSettingsReportType() {
        if(settingsReportType==null)
            return "json";
        return settingsReportType;
    }

    public void setSettingsReportType(String settingsReportType) {
        if(settingsReportType!=null){
            this.settingsReportType = settingsReportType;
        }
    }

    public DalvikVM getDalvikMachine() {
        if(dalvikMachine == null){
            this.dalvikMachine = new DalvikVM(this);
        }
        return dalvikMachine;
    }

    public void setDalvikMachine(DalvikVM dalvikMachine) {
        this.dalvikMachine = dalvikMachine;
    }

    public void setAndroidReferences(ArrayList<AndroidRField> androidReferences) {
        this.androidReferences = androidReferences;
    }

    public ArrayList<AndroidRField> getAndroidReferences() {
        return androidReferences;
    }

    public ArrayList<VirtualFile> getXmlFiles() {
        return this.staticInfo.getXmlFiles();
    }

    public void setXmlFiles(ArrayList<VirtualFile> xmlFileList) {
        this.staticInfo.setXmlFiles(xmlFileList);
    }
}