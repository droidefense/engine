package com.zerjioang.apkr.sdk.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by sergio on 18/2/16.
 */
public class Util {

    private static final long K = 1024;
    private static final long M = K * K;
    private static final long G = M * K;
    private static final long T = G * K;
    private static Gson jsonBuilder = new GsonBuilder().serializeSpecialFloatingPointValues().create();

    public static String beautifyFileSize(final long value) {
        final long[] dividers = new long[]{T, G, M, K, 1};
        final String[] units = new String[]{"TB", "GB", "MB", "KB", "B"};
        if (value < 1)
            throw new IllegalArgumentException("Invalid file size: " + value);
        String result = null;
        for (int i = 0; i < dividers.length; i++) {
            final long divider = dividers[i];
            if (value >= divider) {
                result = format(value, divider, units[i]);
                break;
            }
        }
        return result;
    }

    public static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }


    public static void writeToFile(File file, String str) throws IOException {
        generateDirIfNt(file);
        FileWriter fw = new FileWriter(file);
        fw.write(str);
        fw.close();

    }

    public static String loadFileAsString(String path) throws IOException {
        File f = new File(path);
        if (f.exists() && f.canRead() && f.isFile()) {
            return new String(loadFileAsBytes(path));
        } else {
            throw new IOException("Cant access to file " + path);
        }
    }

    public static byte[] loadFileAsBytes(String path) throws IOException {
        File f = new File(path);
        if (f.exists() && f.canRead() && f.isFile()) {
            return Files.readAllBytes(Paths.get(f.getPath()));
        } else {
            throw new IOException("Expected an readable file.");
        }
    }

    private static void generateDirIfNt(File file) {
        if (file != null && !file.exists()) {
            File parent = file.getParentFile();
            if (parent != null)
                parent.mkdirs();
        }
    }

    public static long calculateDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static String loadFileAsString(File xmlFile) throws IOException {
        return loadFileAsString(xmlFile.getAbsolutePath());
    }

    public static String readFully(InputStream inputStream, String encoding) throws IOException {
        return new String(readFully(inputStream), encoding);
    }

    private static byte[] readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }

    /**
     * @param content                   file content as byte array
     * @param signature                 signature content as byte array
     * @param checkInOrderFromBeginning check signature bytes from beginning or in any part of the content array. Use true for checking from the beginning.
     * @return True is signature array is within content array
     */
    public static boolean checkHexSignature(byte[] content, int[] signature, boolean checkInOrderFromBeginning) {
        if (checkInOrderFromBeginning) {
            //check file header. only the beginning
            boolean b = true;
            for (int i = 0; i < signature.length; i++) {
                if (signature[i] != (0x00ff & content[i])) {
                    b = false;
                    break;
                }
            }
            return b;
        } else {
            return Collections.indexOfSubList(Arrays.asList(content), Arrays.asList(signature)) != -1;
        }
    }

    public static int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle) {
                count++;
            }
        }
        return count;
    }

    public static boolean isHEXString(String s) {
        s = s.toUpperCase();
        s = s.replace("A", "");
        s = s.replace("B", "");
        s = s.replace("C", "");
        s = s.replace("D", "");
        s = s.replace("E", "");
        s = s.replace("F", "");
        s = s.replace("0", "");
        s = s.replace("1", "");
        s = s.replace("2", "");
        s = s.replace("3", "");
        s = s.replace("4", "");
        s = s.replace("5", "");
        s = s.replace("6", "");
        s = s.replace("7", "");
        s = s.replace("8", "");
        s = s.replace("9", "");
        return s.isEmpty();
    }

    public static String getMIME(File fread) {
        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
        // only by file name

        String name = fread.getName();

        if (name.endsWith(".css")) {
            return "text/css";
        } else if (name.endsWith(".js")) {
            return "application/javascript; charset=utf-8";
        } else if (name.endsWith(".eot")) {
            return "application/vnd.ms-fontobject";
        } else if (name.endsWith(".ttf")) {
            return "application/octet-stream";
        } else if (name.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (name.endsWith(".woff")) {
            return "font/woff";
        } else if (name.endsWith(".woff2")) {
            return "font/woff2";
        } else
            return mimeTypesMap.getContentType(fread.getAbsolutePath());
    }

    public static String getFileExtension(File resource) {
        if (resource.getName().contains(".")) {
            String[] data = resource.getName().split("\\.");
            String extension = data[data.length - 1];
            return extension.toUpperCase();
        }
        return "";
    }

    public static byte[] readBytes(String path) throws IOException {
        return readFully(new FileInputStream(path));
    }

    public static String format(final long value, final long divider, final String unit) {
        final double result = divider > 1 ? (double) value / (double) divider : (double) value;
        return String.format("%.1f %s", result, unit);
    }

    public static String toJson(Object o) {
        if (o == null)
            return "{}";
        return jsonBuilder.toJson(o);
    }

    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "";
        }

        char c = 0;
        int i;
        int len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;
        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
                case '/':
                    //                if (b == '<') {
                    sb.append('\\');
                    //                }
                    sb.append(c);
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '<':
                    sb.append("\\<");
                    break;
                case '>':
                    sb.append("\\>");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u" + t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static Object toObjectFromJson(String jsonPayload, Class cls) throws Exception {
        if (jsonPayload == null) {
            throw new NullPointerException("Json string is null.");
        } else if (cls == null) {
            throw new NullPointerException("Not given a object class to deserialize.");
        }
        return new Gson().fromJson(jsonPayload, cls);
    }

    public static String extractText(String webdata, String inicio, String fin) {
        int startIdx = webdata.indexOf(inicio);
        if (startIdx != -1) {
            webdata = webdata.substring(startIdx + inicio.length());
            int endIdx = webdata.indexOf(fin);
            if (endIdx != -1) {
                webdata = webdata.substring(0, endIdx);
                return webdata;
            }
        }
        return "";
    }

    public static String calculateDateTime(long time) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;
        final int MS_IN_A_SECOND = 1000;

        int totalSeconds = (int) (time / MS_IN_A_SECOND);
        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        if (hours == 0)
            if (minutes == 0)
                return seconds + " seconds";
            else
                return minutes + " minutes " + seconds + " seconds";
        else
            return hours + " hours " + minutes + " minutes " + seconds + " seconds";
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    public static boolean searchInStringArray(String key, String[] data) {
        Set<String> values = new HashSet<String>(Arrays.asList(data));
        return values.contains(key);
    }

    public static String getClassNameForFullPath(String fullname) {
        if (fullname == null)
            return "";
        fullname = fullname.replace(".", "/");
        int idx = fullname.lastIndexOf("/");
        if (idx != -1)
            return fullname.substring(idx + 1);
        return fullname.replace("/", ".");
    }

    public static String toHexString(int value) {
        return Integer.toHexString(value).toUpperCase();
    }

    public static String toHexString(long longValue) {
        return Long.toHexString(longValue).toUpperCase();
    }

    public static int toNumber(String s) throws NumberFormatException {
        return Integer.valueOf(s);
    }

    public static String loadFileAsString(FileInputStream ApkrFileInputStream) {
        BufferedReader br = null;
        String line;
        String data = "";
        try {
            br = new BufferedReader(new InputStreamReader(ApkrFileInputStream));
            while ((line = br.readLine()) != null) {
                data += line;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }
}
