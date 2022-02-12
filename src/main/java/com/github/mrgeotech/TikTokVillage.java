package com.github.mrgeotech;

import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class TikTokVillage {

    public static void main(String[] args) throws IOException {
        int count = 0;
        int errors = 0;
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

        for (int i = 0; i < 1000; i++) {
            try {
                System.out.println(i);
                JSONObject json = readJsonFromUrl(count);
                if (json == null) break;
                json.getJSONArray("comments").forEach(o -> {
                    JSONObject comment = new JSONObject(o.toString());
                    names.add(comment.getJSONObject("user").getString("nickname"));
                });
                count += 30;
                if (i % 10 == 0) {
                    save(count, names);
                }
            } catch (Exception e) {
                e.printStackTrace();
                errors++;
            }
        }

        save(count, names);
        System.err.println("Total Errors: " + errors);
    }

    public static void save(int count, List<String> names) throws IOException {
        // Saving names to file
        StringBuilder builder = new StringBuilder();
        builder.append(count).append("\n");
        for (String name : names) {
            builder.append(name).append("\n");
        }
        BufferedWriter fileWriter = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "/names.txt"));
        fileWriter.write(builder.toString());
        fileWriter.close();
    }

    public static JSONObject readJsonFromUrl(int count) {
        try {
            Process process = Runtime.getRuntime().exec("C:/Users/themr/Downloads/curl-7.81.0-win64-mingw/curl-7.81.0-win64-mingw/bin/curl.exe " +
                    "https://api.tikapi.io/comment/list?media_id=7042543887659027759&count=30&cursor=" + count +
                    " -X GET" +
                    " -H \"X-ACCOUNT-KEY: PAXmq8UEfa67d02WBVPhWAoVYFFM8uU2\"" +
                    " -H \"X-API-KEY: Nu574o4oFcRbBtmouV0pF3zbxM0q1XWS\"" +
                    " -H \"accept: application/json\"");

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
