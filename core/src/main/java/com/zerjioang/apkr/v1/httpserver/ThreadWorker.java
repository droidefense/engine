package com.zerjioang.apkr.v1.httpserver;

import com.zerjioang.apkr._main.ApkrScanner;
import com.zerjioang.apkr.v1.common.handlers.FileIOHandler;
import com.zerjioang.apkr.v1.common.helpers.Util;
import com.zerjioang.apkr.v1.httpserver.apimodel.*;
import com.zerjioang.apkr.v2.exception.InvalidScanParametersException;
import com.zerjioang.apkr.v2.helpers.config.ApkrConstants;
import com.zerjioang.apkr.v2.helpers.log4j.Log;
import com.zerjioang.apkr.v2.helpers.log4j.LoggerType;
import com.zerjioang.apkr.v2.helpers.system.CheckSumGen;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ThreadWorker implements Runnable {

    //class variables
    private static final String NONE = "";
    private static final String API_PREFIX = "api/v1/";
    private static final String STREAMED_FILE = "file";
    private static final String STREAMED_NAME = "filename";
    private static final String STREAMED_HASH = "hash";
    private static final String MULTIPART = "multipart";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String BOUNDARY = "boundary=";
    private static final String LINE_FEED = "\r\n";
    private static final String DOUBLE_LINE_FEED = LINE_FEED + LINE_FEED;
    private static final String errorHtml = getErrorHtml();

    //object variables
    private Socket clientSocket = null;
    private InputStream input;
    private OutputStream output;
    private String headerString;
    private Map<String, String> headerData;
    private String requestedResourceName;
    private byte[] requestedResource;
    private String requestType;

    private File fread;
    private String freadStr;
    private Map<String, String> params;

    public ThreadWorker(Socket clientSocket) throws SocketException {
        if (clientSocket == null) {
            throw new SocketException("Received client socket is null");
        }
        this.clientSocket = clientSocket;
        this.params = new HashMap<>();
        this.headerData = new HashMap<>();
    }

    private static String getErrorHtml() {
        try {
            String data = Util.loadFileAsString((FileInputStream) FileIOHandler.getResourceFileInputStream(ApkrConstants.SERVER_FOLDER + File.separator + "error.html"));
            if (data == null || data.isEmpty())
                return "";
            return data;
        } catch (Exception e) {
            Log.write(LoggerType.ERROR, "General exception occurred", e.getLocalizedMessage());
            return null;
        }
    }

    public void run() {
        try {
            this.input = this.clientSocket.getInputStream();
            this.output = this.clientSocket.getOutputStream();
            this.headerData.clear();
            this.headerString = this.getHeaderData();
            System.out.println(this.headerString);

            String response = "";
            try {
                if (this.requestedResourceName != null) {
                    int params = this.requestedResourceName.indexOf("?");
                    if (params != -1) {
                        this.requestedResourceName = this.requestedResourceName.substring(0, params);
                    }
                }
                boolean fileRequested = handleResourceRequest(this.requestedResourceName);
                if (fileRequested) {
                    this.output.write("HTTP/1.1 200 OK\r\n".getBytes());
                    //requested resource can be a physical file or data from sql,...
                    if (fread != null && fread.exists()) {
                        //physical file requested
                        String mime = Util.getMIME(fread);
                        this.output.write(("Content-Type: " + mime + "\r\n").getBytes());
                        if (mime.contains("image")) {
                            this.output.write(("Content-Length: " + requestedResource.length + "\r\n").getBytes());
                        }
                    }
                    //if requested thing is not a physical file, it is data from sql, ...

                    //todo convert dates in dynamic
                    //caching
                    this.output.write("date:Tue, 20 Sep 2016 14:51:03 GMT".getBytes());
                    this.output.write("expires:Tue, 20 Sep 2016 15:51:03 GMT".getBytes());
                    this.output.write("last-modified:Fri, 11 Apr 2014 20:26:30 GMT".getBytes());

                    this.output.write("cache-control:must_revalidate, public, max-age=3600".getBytes());
                    this.output.write("content-encoding:gzip".getBytes());
                    this.output.write("content-language:en".getBytes());
                    this.output.write("content-type:text/html; charset=utf-8".getBytes());

                    this.output.write("server:Apkr Frontend".getBytes());
                    this.output.write("strict-transport-security:max-age=31536000; includeSubdomains".getBytes());
                    this.output.write("vary:Accept-Encoding".getBytes());
                    this.output.write("vary:Accept-Language".getBytes());

                    this.output.write("Content-Disposition: inline;\r\n".getBytes());
                    this.output.write("Access-Control-Allow-Origin:*\r\n".getBytes());
                    this.output.write("Timing-Allow-Origin:*\r\n".getBytes());

                    //xss protection always
                    this.output.write("X-Content-Type-Options:nosniff\r\n".getBytes());
                    this.output.write("X-Frame-Options:SAMEORIGIN\r\n".getBytes());
                    this.output.write("X-XSS-Protection:1; mode=block\r\n".getBytes());

                    this.output.write(("Content-Length: " + requestedResource.length + "\r\n").getBytes());
                    this.output.write("Connection: keep-alive\r\n".getBytes());

                    this.output.write("\r\n".getBytes());
                    this.output.write(requestedResource, 0, requestedResource.length);
                } else {
                    defaultErrorMsg();
                }
            } catch (IOException error) {
                error.printStackTrace();
                defaultErrorMsg();
            }

            this.output.close();
            this.input.close();
            System.out.println("Request processed: " + this.toString());

        } catch (IOException var8) {
            var8.printStackTrace();
        }

    }

    private void defaultErrorMsg() throws IOException {
        /*if (errorHtml != null) {
            this.output.write(("HTTP/1.1 404 ERROR\r\n").getBytes());
            //this.output.write(errorHtml.getBytes());
        } else {
            String data = "File [" + this.requestedResourceName + "] was not found on the http_server\r\n";
            this.output.write(data.getBytes());
        }
        */
        this.output.write(("HTTP/1.1 404 ERROR\r\n").getBytes());
    }

    private boolean handleResourceRequest(String url) throws IOException {
        //static links
        if (url == null)
            return false;
        url = url.replace(API_PREFIX, NONE);
        if (url != null) {
            switch (url) {
                case "/verify/version": {
                    requestedResource = Util.toJson(new ServerStatus()).getBytes();
                    return true;
                }
                case "/report/example/1": {
                    this.fread = new File(ApkrConstants.SERVER_FOLDER + File.separator + "example1.html");
                    requestedResource = Files.readAllBytes(Paths.get(fread.getAbsolutePath()));
                    return true;
                }
                case "/upload": {
                    //decode data from base64
                    byte[] decoded = new byte[0];
                    String filename;
                    String hash;
                    try {
                        decoded = Base64.getDecoder().decode(params.get(STREAMED_FILE));
                        filename = params.get(STREAMED_NAME);
                        hash = params.get(STREAMED_HASH).toUpperCase();
                    } catch (Exception e) {
                        requestedResource = Util.toJson(new JsonErrorMsg("Error while receiving streamed file")).getBytes();
                        return true;
                    }

                    //save file to disk
                    String fileHash = CheckSumGen.getInstance().calculateSHA256(decoded);
                    String hdPath = FileIOHandler.getUploadsFolderPath();
                    if (!FileIOHandler.getUploadsFolder().exists())
                        FileIOHandler.getUploadsFolder().mkdirs();
                    saveFile(decoded, hdPath, filename);
                    FileUploadResponse response = new FileUploadResponse(hdPath + File.separator + filename, decoded.length, hash, fileHash, System.currentTimeMillis());
                    requestedResource = Util.toJson(response).getBytes();
                    //start analysis automatically
                    if (response.isIntegrity()) {
                        FileIOHandler.saveAsRAW(new HashChecking(hash), ApkrConstants.ANALYSIS_METADATA_FILE, new File(FileIOHandler.getUnpackOutputFile().getAbsolutePath() + File.separator + hash));
                        //file was successfully uploaded. Start new scan with it
                        new Thread(() -> {
                            try {
                                new ApkrScanner(new String[]{response.getPath(), response.getFileHash()});
                            } catch (InvalidScanParametersException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    return true;
                }
                case "/latest/uploads": {
                    requestedResource = Util.toJson(new LatestUploads(10)).getBytes();
                    return true;
                }
                case "/status": {
                    String requestHash = params.get("sha256");
                    if (requestHash != null && requestHash.length() == 64) {
                        requestedResource = Util.toJson(new HashChecking(requestHash)).getBytes();
                        return true;
                    } else {
                        requestedResource = Util.toJson(new JsonErrorMsg("You need to make a POST request specifying the hash of the sample to be informed")).getBytes();
                        return true;
                    }
                }
                case "/profile": {
                    //save user data and time into db.
                    if (!ApkrConstants.DB_STORAGE) {
                        requestedResource = Util.toJson(new JsonErrorMsg("Database storage is currently disabled")).getBytes();
                        return true;
                    }
                    String jsonPayload = decodeEncapsulatedContent(params.get("payload"));
                    try {
                        /*
                        UserProfile profile = (UserProfile) Util.toObjectFromJson(jsonPayload, UserProfile.class);
                        //save in db
                        profile.setIp(headerData.get("Host"));
                        profile.setUseragent(headerData.get("User-Agent"));
                        profile.setTime(params.get("time"));
                        */
                        //return positiveMatch code
                    } catch (Exception e) {
                        Log.write(LoggerType.ERROR, "Could not parse user profile information", "User json info", jsonPayload);
                    }
                    requestedResource = "{\"status\":\"ok\"}".getBytes();
                    return true;
                }
                case "/search/": {
                    //sample search
                    String samplehash = params.get("id");
                    if (samplehash != null && samplehash.length() == 64) {
                        String path = FileIOHandler.getUnpackOutputFile().getAbsolutePath();
                        File projectFile = new File(path + File.separator + samplehash);
                        if (projectFile.exists()) {
                            //search founded a result
                            requestedResource = Util.toJson(new JsonErrorMsg("Found" + samplehash)).getBytes();
                        } else {
                            //no result
                            requestedResource = Util.toJson(new JsonErrorMsg("No results matched with sha256 " + samplehash)).getBytes();
                        }
                    }
                    requestedResource = Util.toJson(new JsonErrorMsg("You need to make a POST request specifying the hash of the sample to be informed")).getBytes();
                    return true;
                }
                default: {
                    //1 check for url with request
                    //dynamic links
                    String serverFolder = ApkrConstants.SERVER_FOLDER;
                    if (url.equals("/")) {
                        //index redirect
                        freadStr = serverFolder + "/index.html";
                        fread = new File(freadStr);
                        requestedResource = Files.readAllBytes(Paths.get(freadStr));
                        return true;
                    } else if (url.matches("/report/[0-9A-F]{64}/?")) {
                        //return html generated file as static content
                        String id = url.replace("/report/", ApkrConstants.NONE);
                        freadStr = ApkrConstants.STATIC_REPORT_FOLDER + File.separator + id + ".html";
                        fread = new File(freadStr);
                        requestedResource = Files.readAllBytes(Paths.get(freadStr));
                        return true;
                    } else if (url.equals("/report/")) {
                        //sample report requested
                        //check if file exist on server hdd
                        String path = FileIOHandler.getUnpackOutputFile().getAbsolutePath();
                        String samplehash = params.get("id");
                        if (samplehash != null && samplehash.length() == 64) {
                            File projectFile = new File(path + File.separator + samplehash + File.separator + ApkrConstants.PROJECT_JSON_FILE);
                            if (projectFile.exists()) {
                                String project = Util.loadFileAsString(projectFile);
                                requestedResource = project.getBytes();
                                return true;
                            }
                        }
                        requestedResource = Util.toJson(new JsonErrorMsg("You need to make a POST request specifying the hash of the sample to be informed")).getBytes();
                        return true;
                    } else if (url.equals("/report/cfg/json")) {
                        //sample report requested
                        //check if file exist on server hdd
                        String path = FileIOHandler.getUnpackOutputFile().getAbsolutePath();
                        String samplehash = params.get("id");
                        if (samplehash != null && samplehash.length() == 64) {
                            File projectFile = new File(path + File.separator + samplehash + File.separator + "normal-flowmap.json");
                            if (projectFile.exists()) {
                                String project = "";
                                project = Util.loadFileAsString(projectFile);
                                requestedResource = Util.toJson(project).getBytes();
                                return true;
                            }
                        }
                        requestedResource = Util.toJson(new JsonErrorMsg("You need to make a POST request specifying the hash of the sample to be informed")).getBytes();
                        return true;
                    } else if (url.equals("/report/cfg/graph")) {
                        //sample report requested
                        //check if file exist on server hdd
                        String path = FileIOHandler.getUnpackOutputFile().getAbsolutePath();
                        String samplehash = params.get("id");
                        if (samplehash != null && samplehash.length() == 64) {
                            File projectFile = new File(path + File.separator + samplehash + File.separator + "normal-graphviz.dot");
                            if (projectFile.exists()) {
                                String project = "";
                                project = Util.loadFileAsString(projectFile);
                                requestedResource = Util.toJson(project).getBytes();
                                return true;
                            }
                        }
                        requestedResource = Util.toJson(new JsonErrorMsg("You need to make a POST request specifying the hash of the sample to be informed")).getBytes();
                        return true;
                    } else {
                        //DEFAULT ACTION: return requested file
                        freadStr = serverFolder + this.requestedResourceName;
                        fread = new File(freadStr);
                        if (fread.exists() && fread.isFile() && fread.canRead()) {
                            requestedResource = Files.readAllBytes(Paths.get(freadStr));
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void saveFile(byte[] data, String folderPath, String filename) {
        FileIOHandler.saveFile(folderPath, filename, data);
    }

    private String decodeEncapsulatedContent(String payload) {
        System.out.println(payload);
        String data = Encapsulation.getInstance().removeEncapsulation(payload);
        System.out.println("Decoded: " + data);
        return data;
    }

    private String getHeaderData() {
        //application/vnd.android.package-archive
        BufferedReader source = new BufferedReader(new InputStreamReader(this.input));

        String ret = "";
        try {
            //code to read and print headers
            String headerLine = null;
            do {
                headerLine = source.readLine();
                if (headerLine != null) {
                    String[] val = headerLine.split(":");
                    if (val.length >= 2) {
                        String value = "";
                        for (int i = 1; i < val.length; i++)
                            value += ":" + val[i];
                        headerData.put(val[0].replace(": ", "").trim(), value.replace(": ", "").trim());
                    } else {
                        val = headerLine.split(" ");
                        if (val.length >= 2) {
                            headerData.put(val[0].replace(": ", "").trim(), val[1].replace(": ", "").trim());
                        } else {
                            headerData.put(val[0].replace(": ", "").trim(), val[0].replace(": ", "").trim());
                        }
                        //requested resource name
                        if (requestedResourceName == null)
                            this.requestedResourceName = val[1].trim();
                    }
                    ret += headerLine + "\n";
                }
            }
            while (headerLine != null && headerLine.length() != 0);

            //code to read the post payload data
            StringBuilder payload = new StringBuilder();
            while (source.ready()) {
                payload.append((char) source.read());
            }
            savePayload(payload.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    private void savePayload(String data) {
        boolean cont = data.trim().isEmpty();
        if (cont)
            return;
        String contentText = headerData.get(CONTENT_TYPE);
        if (contentText != null && contentText.contains(MULTIPART)) {
            int len = data.length();
            int index = contentText.indexOf(BOUNDARY);
            String bound = contentText.substring(index + BOUNDARY.length());
            String[] parts = data.split(bound);
            for (String part : parts) {
                if (part.contains("name=")) {
                    part = part.substring(0, part.length());
                    int dataStart = part.indexOf(DOUBLE_LINE_FEED);
                    String head = part.substring(0, dataStart);
                    //get the name
                    Pattern p = Pattern.compile("name=\".+\"");
                    Matcher m = p.matcher(head);
                    String key = null;
                    if (m.find()) {
                        key = m.group().replace("\"", "").replace("name=", "");
                    }

                    //get the body
                    part = part.substring(dataStart + DOUBLE_LINE_FEED.length());
                    part = part.substring(0, part.length() - 4);
                    params.put(key, part);
                }
            }
        } else {
            if (data != null && !data.isEmpty() && data.contains("=")) {
                String[] items = data.split("&");
                for (String s : items) {
                    String[] item = s.split("=");
                    try {
                        if (item.length >= 2 && item[0] != null && item[1] != null) {
                            params.put(item[0], URLDecoder.decode(item[1], "UTF-8"));
                            System.out.println("params --> " + item[0] + ": " + URLDecoder.decode(item[1], "UTF-8"));
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

