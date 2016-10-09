package com.zerjioang.apkr.temp;

import apkr.external.module.batch.exception.EmptyDataSetException;
import apkr.external.module.batch.exception.NoFilesFoundException;
import com.zerjioang.apkr.batch.helper.DirScanner;
import com.zerjioang.apkr.batch.helper.DirectoryFilter;
import com.zerjioang.apkr.handler.FileIOHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sergio on 4/9/16.
 */
public class FileScan {

    public static void main(String[] args) throws IOException, EmptyDataSetException, NoFilesFoundException {
        String sources = "C:\\Users\\.local\\Documents\\projects\\apkr\\apkr-no-git\\apkr-needs\\android-5.1.1_r1-sources";
        String support = "/android/support";
        String java = "/temp/jdk8";

        HashSet<String> set1 = new DirScanner(sources, DirectoryFilter.PATH_FILTER).scan();
        //HashSet<String> set1 = new DirScanner(sources, DirectoryFilter.ANDROID_SDK_FILTER).scan();
        /*
        HashSet<String> set2 = new DirScanner(support, DirectoryFilter.ANDROID_SUPPORT_FILTER).scan();
        HashSet<String> set3 = new DirScanner(java, DirectoryFilter.JAVA_SDK_FILTER).scan();

        set1.addAll(set2);
        set1.addAll(set3);
        */

        //read set1 files looking for method names
        String methodDetectRegex = "[\\s\\.\\(][a-z][a-zA-Z_0-9]+\\(";

        //save results here
        HashMap<String, Integer> results = new HashMap<>();

        //iterate
        Iterator entries = set1.iterator();

        Pattern p = Pattern.compile(methodDetectRegex);

        while (entries.hasNext()) {
            String path = (String) entries.next();
            System.out.println(path);
            byte[] data = Files.readAllBytes(Paths.get(path));
            String content = new String(data, "utf-8");
            //detect method names
            Matcher m = p.matcher(content);
            while (m.find()) {
                String name = m.group();
                name = name.substring(1, name.length() - 1);
                Integer value = results.get(name);
                if (value != null) {
                    //increase
                    results.put(name, ++value);
                } else {
                    //add
                    results.put(name, 1);
                }
            }
        }
        //String export = results.toString().replace(", ", "\n").replace("=", ", ");
        //FileIOHandler.saveFile(new File("methodnames.txt"), export);
        FileIOHandler.saveAsRAW(results, "method-names-weighted.map", new File(""));
    }
}
