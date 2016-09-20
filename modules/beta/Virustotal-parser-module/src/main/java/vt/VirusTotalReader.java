package vt;

import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import vt.model.AntivirusResult;
import vt.model.VirusTotalReport;

import java.io.*;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by B328316 on 09/05/2016.
 */
public class VirusTotalReader implements Serializable{

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";
    public static boolean DEBUG = true;
    private static final int TIMEOUT = 3000;

    //this is the same value for all instances
    private static ArrayList<VirusTotalPair> valueList;
    private static int antivirusCount = 57;

    private String scanUrl;
    private String domain;
    private VirusTotalPair antivirusNamePair,antivirusResultPair, antivirusUpdatePair;
    private String html;
    //report
    private VirusTotalReport report;

    public VirusTotalReader(final String scanUrl, String domain) throws IllegalArgumentException{
        System.setProperty("java.net.preferIPv4Stack" , "true");
        if(scanUrl==null) {
            throw new IllegalArgumentException("VirusTotal scan url must be specified!");
        }
        if(domain==null) {
            throw new IllegalArgumentException("VirusTotal domain name must be specified!");
        }
        this.report = new VirusTotalReport();
        this.domain = domain;
        this.scanUrl = scanUrl;
        this.html = "";
        valueList = new ArrayList<VirusTotalPair>();
        //init value list
        valueList.add(new VirusTotalPair("sha256", "#basic-info > div > div.span8.columns > table > tbody > tr:nth-child(1) > td:nth-child(2)") {
            @Override
            public void process(Document doc) {
                Elements item = doc.select(getValue());
                String value = item.get(0).text();
                debug(getKey()+"\t"+value);
                report.setSha256(value);
            }
        });
        valueList.add(new VirusTotalPair("filename", "#basic-info > div > div.span8.columns > table > tbody > tr:nth-child(2) > td:nth-child(2)") {
            @Override
            public void process(Document doc) {
                Elements item = doc.select(getValue());
                String value = item.get(0).text();
                debug(getKey()+"\t"+value);
                report.setName(value);
            }
        });
        valueList.add(new VirusTotalPair("detections", "#basic-info > div > div.span8.columns > table > tbody > tr:nth-child(3) > td.text-red") {
            @Override
            public void process(Document doc) {
                Elements item = doc.select(getValue());
                String value = item.get(0).text();
                String[] data = value.split("/");
                report.setDetections(Integer.valueOf(data[0].trim()));
                report.setTotalAntivirus(Integer.valueOf(data[1].trim()));
                report.setRatio(report.getDetections()/(float)report.getTotalAntivirus());
            }
        });
        valueList.add(new VirusTotalPair("date", "#basic-info > div > div.span8.columns > table > tbody > tr:nth-child(4) > td:nth-child(2)") {
            @Override
            public void process(Document doc) {
                Elements item = doc.select(getValue());
                String value = item.get(0).text();
                debug(getKey()+"\t"+value);
                report.setDate(value);
            }
        });
        valueList.add(new VirusTotalPair("negative votes", "#malicious-votes") {
            @Override
            public void process(Document doc) {
                Elements item = doc.select(getValue());
                String value = item.get(0).text();
                debug(getKey()+"\t"+value);
                report.setNegativeVotes(Integer.valueOf(value));
            }
        });
        valueList.add(new VirusTotalPair("positive votes", "#harmless-votes") {
            @Override
            public void process(Document doc) {
                Elements item = doc.select(getValue());
                String value = item.get(0).text();
                debug(getKey()+"\t"+value);
                report.setPositiveVotes(Integer.valueOf(value));
            }
        });

        //iterate over all available antivirus engines
        antivirusNamePair = new VirusTotalPair("antivirus name", "#antivirus-results > tbody > tr:nth-child($id) > td:nth-child(1)") {
            @Override
            public void process(Document doc) {
            }
        };
        antivirusResultPair = new VirusTotalPair("antivirus result", "#antivirus-results > tbody > tr:nth-child($id) > td:nth-child(2)") {
            @Override
            public void process(Document doc) {
            }
        };
        antivirusUpdatePair = new VirusTotalPair("antivirus update", "#antivirus-results > tbody > tr:nth-child($id) > td:nth-child(3)") {
            @Override
            public void process(Document doc) {
            }
        };
    }

    public boolean downloadData() throws IOException, IllegalAccessException {
        //boolean online = pingFirst();
        boolean online = true;
        if(!online)
            throw new IllegalAccessException("Could not contact with remote host!");
        debug("VirusTotalReader is downloading data...");
        Connection conn = Jsoup.connect(scanUrl)
                .followRedirects(true)
                .header(":authority", "www.virustotal.com")
                .header(":method", "GET")
                .header(":path", scanUrl.replace("https://www.virustotal.com", ""))
                .header(":scheme", "https")
                .header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .header("accept-encoding", "gzip, deflate, sdch")
                .header("accept-language", "es-ES,es;q=0.8,en;q=0.6")
                .header("cache-control", "no-cache")
                .header("pragma", "no-cache")
                .header("upgrade-insecure-requests", "1")
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .userAgent(USER_AGENT);
        Document doc = conn.get();
        System.out.println(doc);
        //for normal webs ok
        /*URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        try {
            url = new URL(scanUrl);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                html+=line;
            }
            return true;
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }*/
        return false;
    }

    private boolean pingFirst() throws IOException {
        InetAddress address = InetAddress.getByName(domain);
        debug("Name: " + address.getHostName());
        debug("Addr: " + address.getHostAddress());
        boolean reach = address.isReachable(TIMEOUT);
        debug("Reach: " + reach);
        return reach;
    }

    public String getParsedData() throws IOException, IllegalAccessException {
        debug("Downloading...");
        downloadData();
        debug(html);
        debug("Parsing...");
        Document doc = Jsoup.parse(html);
        //basic info
        for(int i=0; i< valueList.size(); i++){
            VirusTotalPair pair = valueList.get(i);
            pair.process(doc);
        }
        //get each engine info
        for(int i=0; i < antivirusCount; i++){
            Elements name = doc.select(antivirusNamePair.getValue().replace("$id", ""+i));
            Elements result = doc.select(antivirusResultPair.getValue().replace("$id", ""+i));
            Elements updated = doc.select(antivirusUpdatePair.getValue().replace("$id", ""+i));
            debug(name.text()+"\t"+result.text()+"\t"+updated.text());
            report.addAntivirusResult(new AntivirusResult(name.text(), result.text(), updated.text()));
        }
        debug("Generating json...");
        return new Gson().toJson(report);
    }

    private void debug(String str) {
        if(DEBUG)
            System.out.println(str);
    }

    private String readFile() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("temp.html"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(System.lineSeparator());
                    line = br.readLine();
                }
                String everything = sb.toString();
                return everything;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
}
