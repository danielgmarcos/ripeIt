package com.appspot;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

/**
 * RipeIt get from ripe.net allocated IP's for country and parse this file to retrieve
 * only Portuguese IPV4 IP's with mask.
 */
public class RipeIt {
    private static final String DELIMITER = "\\|";
    private static final String PROTOCOL_STRING = "ipv4";
    private static final int COUNTRY = 1;
    private static final int IP = 3;
    private static final int PROTOCOL = 2;
    private static final int ADDRESSES_NUMBER = 4;
    private static final String URL = "ftp://ftp.ripe.net/pub/stats/ripencc/delegated-ripencc-latest";
    private static final String COUNTRY_STRING = "PT";
    static final ImmutableMap<String, String> MAX_ADDRESSES =
            new ImmutableMap.Builder<String, String>()
                    .put("256", "24")
                    .put("512", "23")
                    .put("1024", "22")
                    .put("2048", "21")
                    .put("4096", "20")
                    .put("8192", "19")
                    .put("16384", "18")
                    .put("32768", "17")
                    .put("65536", "16")
                    .put("131072", "15")
                    .put("262144", "14")
                    .build();

    public static void main(String[] args) throws Exception {

        //TODO receive arguments and static preferences

        BufferedReader reader = null;
        InputStream inputStream = null;
        try {
            //TODO consider receiving other urls with similar information
            URL url = new URL(URL);
            URLConnection connection = url.openConnection();
            inputStream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            //ripencc|country|protocol|ip|hosts|time|allocates
            //TODO consider using one parser that will allow using other urls instead of ripe
            while ((line = reader.readLine()) != null) {

                String[] tokens = line.split(DELIMITER);

                if (COUNTRY_STRING.equals(tokens[COUNTRY])
                        && PROTOCOL_STRING.equals(tokens[PROTOCOL].toLowerCase())
                        && MAX_ADDRESSES.containsKey(tokens[ADDRESSES_NUMBER])) {
                    //TODO look for another strategy to get ISP provider, this one only work on a few cases
                    System.out.println(InetAddress.getByName(tokens[IP]).getCanonicalHostName().concat(" - ")
                            .concat(tokens[IP].concat("/")
                                    .concat(MAX_ADDRESSES.get(tokens[ADDRESSES_NUMBER]))));
                }
            }
        } finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(inputStream);
        }
    }
}
