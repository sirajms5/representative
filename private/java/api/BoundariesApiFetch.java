package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.management.RuntimeErrorException;

import org.json.JSONArray;
import org.json.JSONObject;

import utilities.Helpers;
import utilities.LogKeeper;

public class BoundariesApiFetch {

    private LogKeeper logKeeper = LogKeeper.getInstance();

    public String fetchBoundaryExternalIdByConstituency(String constituency) {
        String boundaryExternalId = null;
        try {
            String encodedConstituency = URLEncoder.encode(constituency, StandardCharsets.UTF_8.toString());
            String apiUrl = String.format("https://represent.opennorth.ca/boundaries/?name=%s", encodedConstituency);
            logKeeper.appendLog("Fetching boundary external id for: " + constituency);
            URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Accept", "application/json");

                // Check response code
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeErrorException(null, "Failed to reach the API");
                }

                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                conn.disconnect();

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray objectsArray = jsonResponse.getJSONArray("objects");

                if (objectsArray.length() > 0) {
                    JSONObject obj = objectsArray.getJSONObject(0); // Assume the first result is correct
                    boundaryExternalId = obj.optString("external_id", null);
                    logKeeper.appendLog("Found external id for " + constituency + ": " + boundaryExternalId);
                    Helpers.sleep(1);
                } else {
                    logKeeper.appendLog("Response 200 but no actual boundaries found");
                }

        } catch (Exception e) {
            logKeeper.appendLog(e.getMessage());
        }
        return boundaryExternalId;
    }
}
