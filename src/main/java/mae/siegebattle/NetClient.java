package mae.siegebattle;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class NetClient {
    public static void post(String location, String content) {
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();

        try {

            URLConnection connection = new URL(location).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Accept","*/*");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            try (OutputStream output = connection.getOutputStream()) {
                output.write(content.getBytes(charset));
            }

//            if (conn.getResponseCode() < 200 || conn.getResponseCode() >= 300) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
//            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (connection.getInputStream())));

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
