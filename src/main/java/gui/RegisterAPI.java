package gui;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class RegisterAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://wuangvinh.id.vn/register.php"; // Real path to PHP file
        String urlParameters = "username=testuser&password=123456";
        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", String.valueOf(postData.length));
        try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
            wr.write(postData);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        System.out.println("Response: " + response.toString());
    }
} 