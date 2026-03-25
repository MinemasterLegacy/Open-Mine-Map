package net.mmly.openminemap.map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DrawableClaim {

    public final boolean finished;
    public final double[][] vertices;

    DrawableClaim(double[][] vertices, boolean finished) {
        this.finished = finished;
        this.vertices = vertices;
    }

    public static DrawableClaim[] of(InputStream stream) {
        Gson gson = new Gson();
        Map returnedResult;

        try {
            returnedResult = gson.fromJson(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)), Map.class);
        } catch (NullPointerException e) {
            return null;
        }
        if (!returnedResult.get("type").equals("FeatureCollection")) return null;

        JsonArray features = gson.toJsonTree(returnedResult, Map.class).getAsJsonObject().get("features").getAsJsonArray();
        DrawableClaim[] claims = new DrawableClaim[features.size()];

        for (int i = 0; i < claims.length; i++) {
            JsonObject feature = features.get(i).getAsJsonObject();

            boolean finished = feature.get("properties").getAsJsonObject().get("finished").getAsBoolean();
            JsonArray verticesArray = feature.get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonArray();
            double[][] vertices = new double[verticesArray.size()][2];

            for (int j = 0; j < vertices.length; j++) {
                for (int k = 0; k < 2; k++) {
                    vertices[j][k] = verticesArray.get(j).getAsJsonArray().get(k).getAsDouble();
                }
            }

            claims[i] = new DrawableClaim(vertices, finished);
        }

        return claims;

    }

}
