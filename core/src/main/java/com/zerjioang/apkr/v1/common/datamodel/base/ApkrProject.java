package com.zerjioang.apkr.v1.common.datamodel.base;


import apkr.external.module.datamodel.manifest.Manifest;
import apkr.external.module.datamodel.manifest.UsesPermission;
import apkr.external.module.datamodel.manifest.base.AbstractManifestClass;
import com.zerjioang.apkr.v1.common.analysis.base.AbstractAndroidAnalysis;
import com.zerjioang.apkr.v1.common.datamodel.certificate.CertificateModel;
import com.zerjioang.apkr.v1.common.datamodel.dex.OpcodeInformation;
import com.zerjioang.apkr.v1.common.datamodel.enums.MalwareResultEnum;
import com.zerjioang.apkr.v1.common.datamodel.enums.PrivacyResultEnum;
import com.zerjioang.apkr.v1.common.datamodel.holder.DynamicInfo;
import com.zerjioang.apkr.v1.common.datamodel.holder.InternalInfo;
import com.zerjioang.apkr.v1.common.datamodel.holder.StaticInfo;
import com.zerjioang.apkr.v1.common.datamodel.holder.StringAnalysis;
import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.common.helpers.Util;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.base.struct.generic.IAtomClass;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.reader.DexClassReader;
import com.zerjioang.apkr.v1.core.analysis.dynmc.machine.reader.DexHeaderReader;
import com.zerjioang.apkr.v1.core.cfg.DexFileStatistics;
import com.zerjioang.apkr.v1.core.cfg.map.BasicCFGFlowMap;
import com.zerjioang.apkr.v1.core.cfg.map.base.AbstractFlowMap;
import com.zerjioang.apkr.v1.core.ml.MachineLearningResult;
import com.zerjioang.apkr.v1.core.rulengine.Rule;
import com.zerjioang.apkr.v1.httpserver.apimodel.HashChecking;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;
import com.zerjioang.apkr.v2.plugins.collection.sttc.PrivacyPlugin;
import com.zerjioang.apkr.v2.plugins.sdk.AbstractApkrDynamicPlugin;
import com.zerjioang.apkr.v2.plugins.sdk.AbstractApkrStaticPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

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
    private DexFileStatistics statistics;

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
    private transient DexHeaderReader dexHeaderReader;

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

    private void setSummary(String data) {
        if (data == null)
            data = "No summary available!";
        summary = Base64.getEncoder().encodeToString(data.getBytes());
    }

    //DELEGATE METHODS

    //STATIC INFORMATION GETTERS & SETTERS

    public void analyze(AbstractAndroidAnalysis analyzer) {
        //add this analyzer to used analyzer stack
        usedAnalyzers.add(analyzer);
        analyzer.setApkFile(sourceFile);
        analyzer.analyzeCode();
    }

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

    public ArrayList<ResourceFile> getAppFiles() {
        return staticInfo.getAppFiles();
    }

    public void setAppFiles(ArrayList<ResourceFile> files) {
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

    public void setManifestFile(ResourceFile manifest) {
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

    public ArrayList<ResourceFile> getDexList() {
        return this.staticInfo.getDexList();
    }

    public void setDexList(ArrayList<ResourceFile> dexList) {
        this.staticInfo.setDexList(dexList);
        //read files and save their byte array
        for (ResourceFile dex : dexList)
            try {
                this.staticInfo.addDexData(dex, FileIOHandler.readBytes(dex));
                this.staticInfo.setDexFileReaded(true);
            } catch (IOException e) {
                Log.write(LoggerType.FATAL, e, e.getLocalizedMessage());
                this.staticInfo.setDexFileReaded(true);
            }
    }

    //DYNAMIC INFORMATION GETTERS & SETTERS

    public void addDexData(ResourceFile file, byte[] data) {
        this.staticInfo.addDexData(file, data);
    }

    public byte[] getDexData(ResourceFile file) {
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

    //GETTERS AND SETTERS

    public String[] getDescriptors() {
        return this.internalInfo.getDexDescriptors();
    }

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

    public void setRawFiles(ArrayList<ResourceFile> rawFiles) {
        this.staticInfo.setRawFiles(rawFiles);
    }

    public void setAssetsFiles(ArrayList<ResourceFile> assetFiles) {
        this.staticInfo.setAssetFiles(assetFiles);
    }

    public void setLibFiles(ArrayList<ResourceFile> libFiles) {
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

    public void addDexClass(String name, IAtomClass newClass) {
    }

    public IAtomClass[] getListClasses() {
        //return this.internalInfo.getListClasses();
        return new IAtomClass[0];
    }

    /*
        public void addDexClass(String name, IAtomClass newClass) {
            this.internalInfo.addDexClass(name, newClass);
        }
     */

    public void setEntryPoints(ArrayList<AbstractManifestClass> entryArray) {
        this.internalInfo.setEntryPoints(entryArray);
    }

    public boolean isDexFileReaded() {
        return this.staticInfo.isDexFileReaded();
    }

    public void setDexFileReaded(boolean b) {
        this.staticInfo.setDexFileReaded(b);
    }

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

    public void setOpCodesCount(int[] codes) {
        this.opcodeInfo.setOpcodesCount(codes);
    }

    public String getOpCodeStats() {
        return Util.toJson(this.opcodeInfo);
    }


    public void updateMetadata() {
        //save metadata info
        try {
            File meta = new File(FileIOHandler.getUnpackOutputFile().getAbsolutePath() + File.separator + getSourceFile().getSha256() + File.separator + ApkrConstants.ANALYSIS_METADATA_FILE);
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
    }

    public void addDexFileStatistics(DexFileStatistics statistics) {
        this.statistics = statistics;
    }

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

    public void generateResult() {
        try {
            PrivacyPlugin plugin = new PrivacyPlugin(this);
            plugin.onPreExecute();
            plugin.onExecute();

            if (plugin.getSteal() == 0) {
                privacyResult = PrivacyResultEnum.SAFE;
            } else if (plugin.getSteal() > 0 && plugin.getComms() == 0) {
                privacyResult = PrivacyResultEnum.SUSPICIOUS;
            } else if (plugin.getSteal() > 0 && plugin.getComms() > 0) {
                privacyResult = PrivacyResultEnum.DATA_LEAK;
            }

            //TODO overwrite malware values with ML apimodel
            if (plugin.getInterestingCount() == 0) {
                malwareResult = MalwareResultEnum.GOODWARE;
            } else {
                malwareResult = MalwareResultEnum.MALWARE;
            }
        } catch (Exception e) {
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

    public void setCertificateFile(ResourceFile certFile) {
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

        //save report .json to file
        Log.write(LoggerType.TRACE, "Saving report file...");
        this.generateResult();
        this.writeNaturalReport();

        this.stop();

        //update analysis metadataFile
        this.updateMetadata();
        this.save();

        FileIOHandler.saveProjectReport(this);

        Log.write(LoggerType.TRACE, "Sample scan done");
    }

    public boolean isHeaderReaded() {
        return headerReaded;
    }

    public void setHeaderReaded(boolean headerReaded) {
        this.headerReaded = headerReaded;
    }

    public DexHeaderReader getDexHeaderReader() {
        return dexHeaderReader;
    }

    public void setDexHeaderReader(DexHeaderReader dexHeaderReader) {
        this.dexHeaderReader = dexHeaderReader;
    }
}