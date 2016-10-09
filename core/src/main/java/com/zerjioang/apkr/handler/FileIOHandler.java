package com.zerjioang.apkr.handler;


import apkr.external.modules.helpers.log4j.Log;
import apkr.external.modules.helpers.log4j.LoggerType;
import apkr.external.modules.helpers.system.SystemCallReturn;
import com.zerjioang.apkr.sdk.helpers.ApkrConstants;
import com.zerjioang.apkr.sdk.helpers.Util;
import com.zerjioang.apkr.sdk.model.base.ApkrFile;
import com.zerjioang.apkr.sdk.model.base.ApkrProject;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by sergio on 16/2/16.
 */
public class FileIOHandler {

    public static File getUnpackOutputFile() {
        return new File(ApkrConstants.UNPACK_FOLDER);
    }

    public static File getBaseDirFile() {
        return new File(new File("").getAbsolutePath());
    }

    public static File getUnpackOutputFile(ApkrFile source) {
        return new File(getUnpackOutputFile() + File.separator + source.getSha256());
    }

    public static InputStream getApkrFileInputStream(String name) throws FileNotFoundException {
        return new FileInputStream(ApkrConstants.RESOURCE_FOLDER + File.separator + name);
    }

    public static File getResourceFolder() {
        return new File(ApkrConstants.RESOURCE_FOLDER);
    }

    public static File getResourceFolder(String path) {
        return new File(getResourceFolder().getAbsolutePath() + File.separator + path);
    }

    public static ObjectInputStream getResourceObjectStream(String name) throws NullPointerException, IOException {
        return new ObjectInputStream(getApkrFileInputStream(name));
    }

    public static File getStaticPluginsFolderFile() {
        return new File(ApkrConstants.STATIC_PLG_FOLDER_NAME);
    }

    public static File getDynamicPluginsFolderFile() {
        return new File(ApkrConstants.DYNAMIC_PLG_FOLDER_NAME);
    }

    public static File getPluginsFile(String pluginName) {
        return new File(getPluginsFolderPath() + File.separator + pluginName);
    }

    public static String getBaseDirPath() {
        return getBaseDirFile().getAbsolutePath();
    }

    public static String getUnpackOutputPath(ApkrFile source) {
        return getUnpackOutputFile(source).getAbsolutePath();
    }

    public static String getPluginsFolderPath() {
        return getStaticPluginsFolderFile().getAbsolutePath();
    }

    public static String getPluginsPath(String pluginName) {
        return getPluginsFile(pluginName).getAbsolutePath();
    }

    public static String getProjectFolderPath(ApkrProject project) {
        return getUnpackOutputPath(project.getSourceFile());
    }

    public static byte[] readBytes(ApkrFile file) throws IOException {
        return Files.readAllBytes(Paths.get(file.getAbsolutePath()));
    }

    public static SystemCallReturn callSystemExec(String command) throws IOException {

        if (ApkrConstants.IS_WINDOWS_HOST)
            return new SystemCallReturn();

        Process p = null;
        SystemCallReturn ret = new SystemCallReturn();
        String[] calls = command.split(";");

        for (String cmd : calls) {
            // run the command using the Runtime run method:
            p = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            // read the output from the command
            String s, answer = "", err = "";
            while ((s = stdInput.readLine()) != null) {
                answer += s;
            }

            //clean answer
            int idx = answer.indexOf(":");
            answer = answer.substring(idx + 1).trim();

            // read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                err += s;
            }
            stdError.close();
            stdInput.close();

            ret.addAnswer(answer);
            ret.addError(err);
            ret.addCommand(cmd);
        }
        //destroy process
        if (p != null)
            p.destroy();
        //return result;
        return ret;
    }

    public static void saveProjectReport(ApkrProject project) {

        String name, data, outputPath;

        outputPath = FileIOHandler.getUnpackOutputPath(project.getSourceFile());

        //REPORT

        //OPCODES DATA

        //save opcodes count as json
        //save project
        name = "opcodes" + ".json";
        data = project.getOpCodeStats();
        boolean savedOpcodes = saveFile(name, outputPath, data, "Could not save .json opcodes");

        //NORMAL FLOWMAP

        //save call graph as dot format
        name = "normal-graphviz" + ".dot";
        data = project.getNormalControlFlowMap().getAsDotGraph();
        boolean savedNormalDot = saveFile(name, outputPath, data, "Could not save .dot normal flowmap");

        //save call graph as .json
        //save project
        name = "normal-flowmap" + ".json";
        data = Util.toJson(project.getNormalControlFlowMap());
        boolean savedNormalFlowmap = saveFile(name, outputPath, data, "Could not save .json normal flowmap");

        //REFLECTED FLOWMAP

        //save call graph as dot format
        name = "reflected-graphviz" + ".dot";
        data = project.getReflectedFlowMap().getAsDotGraph();
        boolean savedReflectedDot = saveFile(name, outputPath, data, "Could not save .dot reflected flowmap");

        //save call graph as .json
        //save project
        name = "reflected-flowmap" + ".json";
        data = Util.toJson(project.getReflectedFlowMap());
        boolean savedReflectedFlowmap = saveFile(name, outputPath, data, "Could not save .json reflected flowmap");

        //FOLLOW INSTRUCTION FLOWMAP

        //save call graph as dot format
        name = "follow-graphviz" + ".dot";
        data = project.getFollowCallsMap().getAsDotGraph();
        boolean followDot = saveFile(name, outputPath, data, "Could not save .dot follow flowmap");

        //save call graph as .json
        //save project
        name = "follow-flowmap" + ".json";
        data = Util.toJson(project.getFollowCallsMap());
        boolean followJson = saveFile(name, outputPath, data, "Could not save .json follow flowmap");

        //save report as json
        //save project
        name = "report" + ".json";
        data = project.getProjectAsJson();
        boolean savedReport = saveFile(name, outputPath, data, "Could not save .json report");

    }

    private static boolean saveFile(String name, String outpath, String data, String msg) {
        try {
            FileOutputStream out = new FileOutputStream(outpath + File.separator + name);
            out.write(data.getBytes());
            out.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.write(LoggerType.FATAL, msg, e, e.getStackTrace());
        }
        return false;
    }

    public static String getWebsiteContent(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            String line;
            while (null != (line = br.readLine())) {
                buffer.append(line);
            }
            return buffer.toString();
        } catch (Exception ex) {
            Log.write(LoggerType.ERROR, "Could not retrieve Google Play Store information", ex.getLocalizedMessage());
        }
        return null;
    }

    public static File getDecompiledPath(ApkrProject currentProject) {
        return new File(getUnpackOutputPath(currentProject.getSourceFile()) + File.separator + "code");
    }

    public static String getUploadsFolderPath() {
        return getUploadsFolder().getAbsolutePath();
    }

    public static File getUploadsFolder() {
        File f = new File(ApkrConstants.UPLOAD_FOLDER);
        if (!f.exists())
            f.mkdirs();
        return f;
    }

    public static void saveFile(String folder, String filename, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(folder + File.separator + filename);
            fos.write(data);
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void saveFile(File f, byte[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data);
            fos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void saveFile(File f, String data) {
        saveFile(f, data.getBytes());
    }

    public static void saveAsRAW(Object o, String name, File outputDir) throws IOException {
        if (!outputDir.exists())
            outputDir.mkdirs();
        FileOutputStream fout = new FileOutputStream(outputDir.getAbsolutePath() + File.separator + name);
        ObjectOutputStream oos = new ObjectOutputStream(fout);
        oos.writeObject(o);
        oos.close();
    }

    public static Object readAsRAW(File file) throws IOException, ClassNotFoundException {
        FileInputStream fout = new FileInputStream(file);
        ObjectInputStream oos = new ObjectInputStream(fout);
        return oos.readObject();
    }

    public static Object readAsRAW(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        return ois.readObject();
    }

    public static File getRuleEngineDir() {
        return new File(ApkrConstants.RULE_FOLDER);
    }

    public static File getToolsDir() {
        return new File(getBaseDirPath() + File.separator + "resources" + File.separator + "tools");
    }

    public static File getModelsDir() {
        return new File(ApkrConstants.MODEL_FOLDER);
    }

    public static File getReportFolder(String projectId) {
        return new File(ApkrConstants.STATIC_REPORT_FOLDER + File.separator + projectId);
    }
}
