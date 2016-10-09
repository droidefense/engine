package com.zerjioang.apkr.sdk.model.base;

import apkr.external.module.datamodel.manifest.Manifest;
import apkr.external.module.datamodel.manifest.UsesPermission;
import apkr.external.module.datamodel.manifest.base.AbstractManifestClass;
import apkr.external.modules.controlflow.model.map.BasicCFGFlowMap;
import apkr.external.modules.controlflow.model.map.base.AbstractFlowMap;
import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import apkr.external.modules.ml.MachineLearningResult;
import apkr.external.modules.rulengine.Rule;
import com.zerjioang.apkr.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.handler.DirScannerHandler;
import com.zerjioang.apkr.handler.FileIOHandler;
import com.zerjioang.apkr.handler.base.DirScannerFilter;
import com.zerjioang.apkr.sdk.AbstractApkrDynamicPlugin;
import com.zerjioang.apkr.sdk.AbstractApkrStaticPlugin;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.helpers.Util;
import com.zerjioang.apkr.sdk.model.certificate.CertificateModel;
import com.zerjioang.apkr.sdk.model.dex.OpcodeInformation;
import com.zerjioang.apkr.sdk.model.enums.MalwareResultEnum;
import com.zerjioang.apkr.sdk.model.enums.PrivacyResultEnum;
import com.zerjioang.apkr.sdk.model.holder.DynamicInfo;
import com.zerjioang.apkr.sdk.model.holder.InternalInfo;
import com.zerjioang.apkr.sdk.model.holder.StaticInfo;
import com.zerjioang.apkr.sdk.model.holder.StringAnalysis;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sergio on 16/2/16.
 */
public class ApkrProject implements Serializable {

    private static final Map<APKFile, ApkrProject> projectMap = new HashMap<>();

    /**
     * Timestamp from creation to end
     */
    private final AtomTimeStamp timeStamp;
    /**
     * Reference to current .apk
     */
    private final APKFile sourceFile;
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
    private final ArrayList<AbstractApkrStaticPlugin> staticInfoPlugins;
    /**
     * Current .apk dynamic plugin information holder
     */
    private final ArrayList<AbstractApkrDynamicPlugin> dynamicInfoPlugins;
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

    /**
     * Result of JADX decompilation
     */
    private boolean successfullDecompilation;

    /**
     * Dex file basic counting statistics
     */
    //private DexFileStatistics statistics;

    /**
     * Result of opcode data
     */
    private transient OpcodeInformation opcodeInfo;

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
    private transient boolean headerReaded;
    //private transient DexHeaderReader dexHeaderReader;

    public ApkrProject(final APKFile file) {
        //create new timestamp now
        timeStamp = new AtomTimeStamp();

        //create holders
        this.staticInfo = new StaticInfo();
        this.staticInfoPlugins = new ArrayList<>();
        this.dynamicInfoPlugins = new ArrayList<>();

        this.internalInfo = new InternalInfo();
        this.opcodeInfo = new OpcodeInformation();
        this.dynamicInfo = new DynamicInfo();

        //init data structs
        usedAnalyzers = new ArrayList<>();

        //save apk reference
        sourceFile = file;

        setSummary("No summary created yet!");

        //add this currentProject to running projects holder
        projectMap.put(file, this);
    }

    public static ApkrProject getProject(APKFile apk) {
        return projectMap.get(apk);
    }

    public static ApkrProject getProject(String hash) {
        return null;
    }

    public void analyze(AbstractAndroidAnalysis analyzer) {
        //add this analyzer to used analyzer stack
        usedAnalyzers.add(analyzer);
        analyzer.setApkFile(sourceFile);
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

    public ArrayList<ApkrFile> getAppFiles() {
        return staticInfo.getAppFiles();
    }

    public void setAppFiles(ArrayList<ApkrFile> files) {
        staticInfo.setAppFiles(files);
    }

    public long getStartTime() {
        return timeStamp.getStart();
    }

    public long getEndTime() {
        return timeStamp.getEnd();
    }

    public APKFile getSourceFile() {
        return sourceFile;
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

    public File getManifestFile() {
        return this.staticInfo.getManifestFile().getThisFile();
    }

    public void setManifestFile(ApkrFile manifest) {
        this.staticInfo.setManifestFile(manifest);
    }

    public Manifest getManifestInfo() {
        return this.staticInfo.getManifestInfo();
    }

    public void setManifestInfo(Manifest manifestInfo) {
        this.staticInfo.setManifestInfo(manifestInfo);
    }

    public void stop() {
        this.timeStamp.stop();
    }

    public void printProjectInfo() {
        Log.write(LoggerType.TRACE, " -- PROJECT OUTPUT -- ");
        Log.write(LoggerType.TRACE, getProjectAsJson());
    }

    public void setMainClass(String name) {
        this.staticInfo.setMainClassName(name);
    }

    public void setNumberofDex(int i) {
        this.staticInfo.setNumberOfDexFiles(i);
    }

    public ArrayList<ApkrFile> getDexList() {
        return this.staticInfo.getDexList();
    }

    public void setDexList(ArrayList<ApkrFile> dexList) {
        this.staticInfo.setDexList(dexList);
        //read files and save their byte array
        for (ApkrFile dex : dexList)
            try {
                this.staticInfo.addDexData(dex, FileIOHandler.readBytes(dex));
                this.staticInfo.setDexFileReaded(true);
            } catch (IOException e) {
                Log.write(LoggerType.FATAL, e, e.getLocalizedMessage());
                this.staticInfo.setDexFileReaded(true);
            }
    }

    public void addDexData(ApkrFile file, byte[] data) {
        this.staticInfo.addDexData(file, data);
    }

    //DYNAMIC INFORMATION GETTERS & SETTERS

    public byte[] getDexData(ApkrFile file) {
        return this.staticInfo.getDexData(file);
    }

    public void setFieldClasses(String[] fieldClasses) {
        this.internalInfo.setDexFieldClasses(fieldClasses);
    }

    public void setFieldTypes(String[] fieldTypes) {
        this.internalInfo.setDexFieldTypes(fieldTypes);
    }

    public void setFieldNames(String[] fieldNames) {
        this.internalInfo.setDexFieldNames(fieldNames);
    }

    public void setMethodClasses(String[] methodClasses) {
        this.internalInfo.setDexMethodClasses(methodClasses);
    }

    public void setMethodTypes(String[] methodTypes) {
        this.internalInfo.setDexMethodTypes(methodTypes);
    }

    public void setMethodNames(String[] methodNames) {
        this.internalInfo.setDexMethodNames(methodNames);
    }

    public void printInfo() {
        this.internalInfo.printDexInfo();
    }

    public String[] getTypes() {
        return this.internalInfo.getDexTypes();
    }

    public void setTypes(String[] strings) {
        this.internalInfo.setDexTypes(strings);
    }

    public String[] getStrings() {
        return this.internalInfo.getDexStrings();
    }

    public void setStrings(String[] strings) {
        this.internalInfo.setDexStrings(strings);
    }

    public String[] getDescriptors() {
        return this.internalInfo.getDexDescriptors();
    }

    //GETTERS AND SETTERS

    public void setDescriptors(String[] strings) {
        this.internalInfo.setDexDescriptors(strings);
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

    public void addStaticPlugin(AbstractApkrStaticPlugin plugin) {
        staticInfoPlugins.add(plugin);
    }

    public void addDynamicPlugin(AbstractApkrDynamicPlugin plugin) {
        dynamicInfoPlugins.add(plugin);
    }

    public String getProjectAsJson() {
        return Util.toJson(this);
    }

    public String getProjectName() {
        return getSourceFile().getThisFile().getName().replace(".apk", "");
    }

    public void setRawFiles(ArrayList<ApkrFile> rawFiles) {
        this.staticInfo.setRawFiles(rawFiles);
    }

    public void setAssetsFiles(ArrayList<ApkrFile> assetFiles) {
        this.staticInfo.setAssetFiles(assetFiles);
    }

    public void setLibFiles(ArrayList<ApkrFile> libFiles) {
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

    public void setMagicNumber(String magic) {
        this.internalInfo.setMagicNumber(magic);
    }

    public void setHeaderChecksum(String checksum) {
        this.internalInfo.setHeaderChecksum(checksum);
    }

    public void setFileSignature(String hash) {
        this.internalInfo.setFileSignature(hash);
    }

    public void setHeaderFileSize(int fileSize) {
        this.internalInfo.setHeaderFileSize(fileSize);
    }

    public void setHeaderSize(String headerSize) {
        this.internalInfo.setHeaderSize(headerSize);
    }

    public void setEndianTag(byte[] endian_tag) {
        this.internalInfo.setEndianTag(endian_tag);
    }

    public void setLinkSize(byte[] link_size) {
        this.internalInfo.setLinkSize(link_size);
    }

    public void setLinkOffset(byte[] link_offset) {
        this.internalInfo.setLinkOffset(link_offset);
    }

    /*
    public void addDexClass(String name, IAtomClass newClass) {
    }

    public IAtomClass[] getListClasses() {
        //return this.internalInfo.getListClasses();
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
        return Util.toJson(this.opcodeInfo);
    }

    public void updateMetadata() {
        //save metadata info
        //try {
            File meta = new File(FileIOHandler.getUnpackOutputFile().getAbsolutePath() + File.separator + getSourceFile().getSha256() + File.separator + ApkrConstants.ANALYSIS_METADATA_FILE);
            /*
            if (!meta.exists()) {
                HashChecking hc = new HashChecking(getSourceFile().getSha256());
                hc.setDate(new Date(System.currentTimeMillis()));
                hc.setResult("Not calculated");
                FileIOHandler.saveAsRAW(hc, ApkrConstants.ANALYSIS_METADATA_FILE, meta.getParentFile());
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

    /*
    public void addDexFileStatistics(DexFileStatistics statistics) {
            this.statistics = statistics;
    }
    */

    public void save() {
        try {
            String path = FileIOHandler.getUnpackOutputFile().getAbsolutePath();
            String samplehash = getSourceFile().getSha256();
            File projectFile = new File(path + File.separator + samplehash + File.separator + ApkrConstants.PROJECT_DATA_FILE);
            FileIOHandler.saveAsRAW(this, ApkrConstants.PROJECT_DATA_FILE, projectFile.getParentFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateEnumResults() {
        if (staticInfoPlugins != null && !staticInfoPlugins.isEmpty()) {
            for (int i = 0; i < staticInfoPlugins.size(); i++) {
                AbstractApkrStaticPlugin plugin = staticInfoPlugins.get(i);
                if (plugin.getClass().getSimpleName().equals("PrivacyPlugin")) {
                    //TODO fix this issue with dynamic loaded class.
                    /*
                    PrivacyPlugin pplug = (PrivacyPlugin) plugin;
                    privacyResult = pplug.getPrivacyResult();
                    */
                }

            }
        } else {
            malwareResult = MalwareResultEnum.UNKNOWN;
            privacyResult = PrivacyResultEnum.UNKNOWN;
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
        return sourceFile.getSha256();
    }

    public OpcodeInformation getOpcodeInfo() {
        return opcodeInfo;
    }

    public void writeNaturalReport() {
        String data;
        String url = "https://www.virustotal.com/es/file/" + getProjectId().toLowerCase() + "/analysis/";
        int entries = getInternalInfo().getEntryPoints().size();
        data = "<p>Analyzed application is called <strong>" + getSourceFile().getFilename() + "</strong> but it's internal name is <tt>" + getManifestInfo().getPackageName() + "</tt></p>\n" +
                "\n" +
                "<p>Its file signature is <tt>" + getProjectId() + "</tt></p>\n" +
                "\n" +
                "<p>and VirusTotal result can be found at: <a href=\"" + url + "\" target=\"_blank\">" + url + "</a></p>\n" +
                "\n" +
                "<p>The application has access to:</p>\n" +
                "<ul>";
        for (UsesPermission p : getManifestInfo().getUsesPermissionList()) {
            data +=
                    "<li>" +
                            p.getName()
                            +
                            "</li>";
        }
        data +=
                "</ul>" +
                        "\n" +
                        "<p>We have detected " + entries + " entry points in this application, which means that it runs from " + entries + " different main points.</p>" +
                        "\n" +
                        "<p>Please, take a closer look to analysis result to have a deep understanding of what the application attempts to do.</p>";
        setSummary(data);
    }

    public void setEndianString(String endianString) {
        this.internalInfo.setEndianString(endianString);
    }

    public void setCertificateFile(ApkrFile certFile) {
        this.staticInfo.setCertFile(certFile);
    }

    public void setMatchedRules(ArrayList<Rule> matchedRules) {
        this.dynamicInfo.setMatchedRules(matchedRules);
    }

    public void setStringAnalysisResult(StringAnalysis stringContent) {
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
    }

    public void finish() {
        Log.write(LoggerType.TRACE, "apkr project finished");

        Log.write(LoggerType.TRACE, "Generating scan results...");

        this.generateEnumResults();

        Log.write(LoggerType.TRACE, "Generating natural description...");

        this.writeNaturalReport();

        Log.write(LoggerType.TRACE, "Generating report template...");

        this.generateReportTemplate();

        //stop timer
        this.stop();

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

        /*try {
            byte[] data = Files.readAllBytes(Paths.get("src/main/resources/templates/report.vm"));
            String content = new String(data, "utf-8");
            content = content.replace("jsonData", Util.toJson(this).replace("\"", "\\\""));
            FileIOHandler.saveFile(FileIOHandler.getReportFolder(getProjectId() + ".html"), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        try {
            VelocityEngine ve = new VelocityEngine();
            ve.init();
            Template t = ve.getTemplate("src/main/resources/templates/report.html", "UTF-8");
            VelocityContext context = new VelocityContext();

            context.put("app_name", this.getProjectName());
            context.put("package_name", this.getManifestInfo().getPackageName());
            context.put("sha_256", this.getProjectId());
            context.put("app_size", this.getSourceFile().getBeautyFilesize());
            context.put("app_logo_base_64", this.getAppLogoasB64());
            context.put("total_permissions", this.getManifestInfo().getUsesPermissionList().size());
            context.put("matched_rules", this.dynamicInfo.getMatchedRules().size());
            context.put("goodware", this.machineLearningResult.getTotal() - this.machineLearningResult.getPositives());
            context.put("malware", this.machineLearningResult.getPositives());
            context.put("expert_info", this.getSummary());

            StringWriter writer = new StringWriter();
            t.merge(context, writer);
            String data = writer.toString();
            FileIOHandler.saveFile(FileIOHandler.getReportFolder(getProjectId() + ".html"), data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getAppLogoasB64() {
        String logoName = this.getManifestInfo().getApplication().getIcon();
        logoName = logoName.replace("@", "");
        String unpackPath = FileIOHandler.getUnpackOutputPath(this.getSourceFile());
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
        ArrayList<ApkrFile> files = handler.getFiles();
        if (files != null && !files.isEmpty()) {
            //read logo and convert to b64 string
            try {
                byte[] imageBytes = Files.readAllBytes(Paths.get(files.get(0).getThisFile().getAbsolutePath()));
                return Base64.getEncoder().encodeToString(imageBytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //otherwise, return default-logo.png
        return "iVBORw0KGgoAAAANSUhEUgAAAJYAAACWCAIAAACzY+a1AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6OTMxMjBCRDk4Rjk3MTFFMjhBQkREMDg4MEE1N0Q0QTUiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6OTMxMjBCREE4Rjk3MTFFMjhBQkREMDg4MEE1N0Q0QTUiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo5MzEyMEJENzhGOTcxMUUyOEFCREQwODgwQTU3RDRBNSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo5MzEyMEJEODhGOTcxMUUyOEFCREQwODgwQTU3RDRBNSIvPiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PooYpqoAABCpSURBVHja7F15dJvV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";
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
}