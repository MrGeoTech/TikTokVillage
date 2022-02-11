package com.github.mrgeotech;

import org.json.JSONObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class TikTokVillage {

    public static void main(String[] args) throws IOException {
        int count = 0;
        List<String> names = new ArrayList<>();

        File file = new File(System.getProperty("user.dir"), "/names.txt");
        try {
            if (!file.createNewFile()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                count = Integer.parseInt(reader.readLine());

                while (reader.ready()) {
                    names.add(reader.readLine());
                }
            }
        } catch (Exception ignored) {}

        for (int i = 0; i < 1; i++) {
            JSONObject json = readJsonFromUrl(count);
            if (json == null) break;
            json.getJSONArray("comments").forEach(o -> {
                JSONObject comment = new JSONObject(o.toString());
                names.add(comment.getJSONObject("user").getString("nickname"));
            });
            count += 30;
        }

        // Saving names to file
        StringBuilder builder = new StringBuilder();
        builder.append(count).append("\n");
        for (String name : names) {
            builder.append(name).append("\n");
        }
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/names.txt"));
        fileWriter.write(builder.toString());
        System.out.println(builder);
        fileWriter.close();
    }


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(int count) {
        try {
            Process process = Runtime.getRuntime().exec("curl --request GET " +
                            "--url 'https://api.tikapi.io/comment/list?media_id=7042543887659027759&count=30&cursor=" + count + "' " +
                            "--header 'X-ACCOUNT-KEY: D4hAHGSHyHIaUclIOpSy8YJY4SO1uEqX' " +
                            "--header 'X-API-KEY: JAfnjxNTdoMjMn57h8imBgsopFG6nBsG' " +
                            "--header 'accept: application/json'");

            process.getInputStream().transferTo(System.out);
            process.getErrorStream().transferTo(System.err);

            final StringBuilder jsonText = new StringBuilder();
            Thread thread = new Thread(() -> {
                try {
                    BufferedReader rd = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = rd.readLine()) != null)
                        jsonText.append(line);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            thread.start();
            process.waitFor();

            return new JSONObject(jsonText.toString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
