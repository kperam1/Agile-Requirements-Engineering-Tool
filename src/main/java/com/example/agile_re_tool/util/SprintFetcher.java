package com.example.agile_re_tool.util;

import com.example.agile_re_tool.session.ProjectSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;

public class SprintFetcher {

    public static LinkedHashMap<String, Long> fetchSprints() {
        LinkedHashMap<String, Long> map = new LinkedHashMap<>();

        try {
            long projectId = ProjectSession.getProjectId();

            String url = "http://localhost:8080/api/sprints/" + projectId;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            JSONArray arr = new JSONArray(resp.body());

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);

                long id = obj.getLong("id");
                String name = obj.getString("name");

                map.put(name, id);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;
    }
}
