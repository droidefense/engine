package vt.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by B328316 on 09/05/2016.
 */
public class VirusTotalReport implements Serializable{
    private String sha256, name;
    private String date;
    private int totalAntivirus, detections;
    private float ratio;
    private int positiveVotes, negativeVotes;
    private ArrayList<AntivirusResult> antivirus;

    public VirusTotalReport() {
        this.antivirus = new ArrayList<AntivirusResult>();
    }

    public VirusTotalReport(String sha256) {
        this();
        this.sha256 = sha256;
    }

    public String getSha256() {
        return sha256;
    }

    public void setSha256(String sha256) {
        this.sha256 = sha256;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotalAntivirus() {
        return totalAntivirus;
    }

    public void setTotalAntivirus(int totalAntivirus) {
        this.totalAntivirus = totalAntivirus;
    }

    public int getDetections() {
        return detections;
    }

    public void setDetections(int detections) {
        this.detections = detections;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public int getPositiveVotes() {
        return positiveVotes;
    }

    public void setPositiveVotes(int positiveVotes) {
        this.positiveVotes = positiveVotes;
    }

    public int getNegativeVotes() {
        return negativeVotes;
    }

    public void setNegativeVotes(int negativeVotes) {
        this.negativeVotes = negativeVotes;
    }

    public ArrayList<AntivirusResult> getAntivirus() {
        return antivirus;
    }

    public void setAntivirus(ArrayList<AntivirusResult> antivirus) {
        this.antivirus = antivirus;
    }

    public void addAntivirusResult(AntivirusResult result){
        this.antivirus.add(result);
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }
}
