package external.plugins.collection.dynmc;

import droidefense.handler.FileIOHandler;
import droidefense.sdk.AbstractDynamicPlugin;
import droidefense.sdk.helpers.Util;
import droidefense.sdk.model.holder.StringInfo;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by sergio on 31/5/16.
 */
public class StringAnalyzerPlugin extends AbstractDynamicPlugin {

    private static HashMap<String, Integer> methodNames;
    private transient final StringInfo stringContent;
    private transient String[] baseList;

    public StringAnalyzerPlugin() {
        stringContent = new StringInfo();
        if (methodNames == null) {
            try {
                methodNames = (HashMap<String, Integer>) FileIOHandler.readAsRAW(
                        FileIOHandler.getResourceFolder(
                                "map/method-names-weighted.map"
                        )
                );
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPreExecute() {
        this.baseList = this.getCurrentProject().getInternalStrings();
        if (baseList != null) {
            this.stringContent.setInitialLength(this.baseList.length);
        }
        stringContent.setClassified(new HashMap<>());
        stringContent.setIrrelevant(0);
    }

    @Override
    public void onExecute() {
        //look for numeric strings, emails, urls
        if (baseList == null)
            return;
        for (String s : baseList) {
            if (isJunk(s)) {
                stringContent.getClassified().put(s, "Irrelevant");
                stringContent.setIrrelevant(stringContent.getIrrelevant() + 1);
                continue;
            }
            try {
                Util.toNumber(s);
                stringContent.getClassified().put(s, "Numeric");
                stringContent.setNumeric(stringContent.getNumeric() + 1);
                continue;
            } catch (NumberFormatException e) {
            }
            if (methodNames.get(s) != null) {
                stringContent.getClassified().put(s, "API Method");
                stringContent.setMethodName(stringContent.getMethodName() + 1);
            } else if (s.matches("\\d(\\.\\d+)+")) {
                stringContent.getClassified().put(s, "Numeric");
                stringContent.setNumeric(stringContent.getNumeric() + 1);
            } else if (s.matches("[\\|,!,\",@,·,#,$,¢,%,&,¬,/,\\(,\\),=,\\?,',¡,¿,^,`,\\[,\\],´,\\{,¨,\\},;,\\,,\\.,:,-,_,<,>,/,\\*,-,\\+]+")) {
                stringContent.getClassified().put(s, "Symbol");
            } else if (s.matches("\\[?L?(\\w+/)+\\w+;?")) {
                stringContent.getClassified().put(s, "Classname");
                stringContent.setClassname(stringContent.getClassname() + 1);
            } else if (s.endsWith(".java")) {
                stringContent.getClassified().put(s, "Java filename");
                stringContent.setJavaName(stringContent.getJavaName() + 1);
            } else if (s.matches("L?(\\w+/)+\\w+(\\$\\w+)+;?")) {
                stringContent.getClassified().put(s, "Inner classname");
                stringContent.setInnerclass(stringContent.getInnerclass() + 1);
            } else if (s.matches("\\w+\\$\\d+")) {
                stringContent.getClassified().put(s, "Var accessor");
                stringContent.setAccessor(stringContent.getAccessor() + 1);
            } else if (s.matches("&\\w+=")) {
                stringContent.getClassified().put(s, "URL param");
                stringContent.setParam(stringContent.getParam() + 1);
            } else if (s.matches("[a-fA-F0-9]{2,}+")) {
                stringContent.getClassified().put(s, "Hex string");
                stringContent.setHexstring(stringContent.getHexstring() + 1);
            } else if (s.matches("[a-zA-Z0-9]{2,}+")) {
                stringContent.getClassified().put(s, "Word");
                stringContent.setWord(stringContent.getWord() + 1);
            } else if (s.matches("([a-zA-Z]{2,}\\s)*[a-zA-Z]{2,}(\\.|,|;|:|!|\\?)?")) {
                stringContent.getClassified().put(s, "Sentence");
                stringContent.setSentences(stringContent.getSentences() + 1);
            } else if (s.matches("[a-zA-Z0-9_]+")) {
                stringContent.getClassified().put(s, "Underscored word");
                stringContent.setUnderword(stringContent.getUnderword() + 1);
            } else if (s.matches("[a-zA-Z0-9]((-)[a-zA-Z0-9])+")) {
                stringContent.getClassified().put(s, "Separator word");
                stringContent.setSeparatorWord(stringContent.getSeparatorWord() + 1);
            } else if (s.matches("(http(s)?://)?[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}:[0-9]{1,5}")) {
                stringContent.getClassified().put(s, "IPv4 with port");
                stringContent.setIpv4(stringContent.getIpv4() + 1);
            } else if (s.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$")) {
                stringContent.getClassified().put(s, "IPv4");
                stringContent.setIpv4(stringContent.getIpv4() + 1);
            } else if (s.matches("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$")) {
                stringContent.getClassified().put(s, "IPv4");
                stringContent.setIpv4(stringContent.getIpv4() + 1);
            } else if (s.matches("^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])\\\n" +
                    "(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$")) {
                stringContent.getClassified().put(s, "DNS");
                stringContent.setDns(stringContent.getDns() + 1);
            } else if (s.matches("\"^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$\"")) {
                stringContent.getClassified().put(s, "IPv6 STD");
                stringContent.setIpv6(stringContent.getIpv6() + 1);
            } else if (s.matches("\"^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$\"")) {
                stringContent.getClassified().put(s, "IPv6 HEX");
                stringContent.setIpv6(stringContent.getIpv6() + 1);
            } else if (s.matches("^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})")) {
                stringContent.getClassified().put(s, "Email");
                stringContent.setEmail(stringContent.getEmail() + 1);
            } else if (s.matches("^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*")) {
                stringContent.getClassified().put(s, "webURL");
                stringContent.setUrl(stringContent.getUrl() + 1);
            } else if (s.matches("[a-zA-Z0-9]{1,2}+")) {
                stringContent.getClassified().put(s, "Ofuscated");
                stringContent.setOfuscatedString(stringContent.getOfuscatedString() + 1);
            } else if(s.matches("[a-zA-Z0-9]+(\\.|,|(\\s[a-zA-Z0-9]+))*")) {
                stringContent.getClassified().put(s, "sentence");
                stringContent.setSentences(stringContent.getSentences() + 1);
            } else {
                stringContent.getClassified().put(s, "unknown");
                stringContent.setUnknown(stringContent.getUnknown() + 1);
            }
        }
        //we calculate that app is ofuscated if the ammount of unkwown strings is bigger than 1/3 all string set.
        stringContent.setOfuscated(stringContent.getUnknown() > (stringContent.getInitialLength() - stringContent.getIrrelevant() - stringContent.getClassname()) / 3);
        this.currentProject.setStringAnalysisResult(stringContent);
    }

    private boolean isJunk(String s) {
        if (s.trim().length() == 0) {
            return true;
        } else if (s.equals("<clinit>"))
            return true;
        else if (s.equals("<init>"))
            return true;
        else if (s.matches("\\)?\\[?[A-Z]{1}"))
            //[A, [J, )[B, ...
            return true;
        else return s.matches("[[I,L,V, Z, J, F, C]]{1,8}");
    }

    @Override
    protected void postExecute() {
        System.out.println("\tString analysis finish");
    }

    @Override
    public String getPluginName() {
        return "String analyzer plugin";
    }
}
