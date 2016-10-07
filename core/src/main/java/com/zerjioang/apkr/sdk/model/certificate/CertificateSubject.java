package com.zerjioang.apkr.sdk.model.certificate;

import java.io.Serializable;

/**
 * Created by sergio on 28/5/16.
 */
public class CertificateSubject implements Serializable {

    //https://docs.oracle.com/cd/E24191_01/common/tutorials/authz_cert_attributes.html

    private static final String SEPARATOR = ",";
    private static final String TUPLE_SEPARATOR = "=";
    private static final int PAIR_LENGTH = 2;

    /*
    CN: CommonName
    OU: OrganizationalUnit
    O: Organization
    L: Locality
    S: StateOrProvinceName
    C: CountryName
     */
    private final transient String data;
    private String commonName;
    private String organizationalUnit;
    private String organization;
    private String locality;
    private String stateOrProvinceName;
    private String countryName;

    public CertificateSubject(String unparsedData) {
        data = unparsedData;

        //initialize data for appearing in json
        commonName = "";
        organizationalUnit = "";
        organization = "";
        locality = "";
        stateOrProvinceName = "";
        countryName = "";

        String[] tags = data.split(SEPARATOR);
        for (String tuple : tags) {
            tuple = tuple.trim();
            String[] pair = tuple.split(TUPLE_SEPARATOR);
            if (pair.length == PAIR_LENGTH) {
                String key = pair[0];
                String value = pair[1];
                switch (key) {
                    case "CN":
                        commonName = value;
                        break;
                    case "OU":
                        organizationalUnit = value;
                        break;
                    case "O":
                        organization = value;
                        break;
                    case "L":
                        locality = value;
                        break;
                    case "S":
                        stateOrProvinceName = value;
                        break;
                    case "C":
                        countryName = value;
                        break;
                }
            }
        }
    }

    //GETTERS ONLY

    public String getCommonName() {
        return commonName;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public String getOrganization() {
        return organization;
    }

    public String getLocality() {
        return locality;
    }

    public String getStateOrProvinceName() {
        return stateOrProvinceName;
    }

    public String getCountryName() {
        return countryName;
    }

    @Override
    public String toString() {
        return "CertificateSubject{" +
                "commonName='" + commonName + '\'' +
                ", organizationalUnit='" + organizationalUnit + '\'' +
                ", organization='" + organization + '\'' +
                ", locality='" + locality + '\'' +
                ", stateOrProvinceName='" + stateOrProvinceName + '\'' +
                ", countryName='" + countryName + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CertificateSubject that = (CertificateSubject) o;

        return data != null ? data.equals(that.data) : that.data == null;

    }
}
