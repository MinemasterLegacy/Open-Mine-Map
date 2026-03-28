package net.mmly.openminemap.map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.event.CommandHander;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.gui.WebAppSelectLayer;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.Notification;
import net.mmly.openminemap.util.PolygonTriangulator;
import net.mmly.openminemap.util.UnitConvert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class DrawableClaim {

    private static long lastReloaded = -60000;
    public static int succeededTriangulations = 0;

    public final boolean finished;
    public final double[][] vertices;
    public final float leftmost;
    public final float rightmost;
    public final float topmost;
    public final float bottommost;
    public final double[][][] triangles;

    DrawableClaim(double[][] vertices, boolean finished, double leftmost, double rightmost, double topmost, double bottommost) {
        this.finished = finished;
        this.vertices = vertices;
        this.leftmost = (float) leftmost;
        this.rightmost = (float) rightmost;
        this.topmost = (float) topmost;
        this.bottommost = (float) bottommost;
        this.triangles = PolygonTriangulator.triangulate(vertices);
        if (this.triangles != null) succeededTriangulations++;
    }

    public static DrawableClaim[] of(InputStream stream) {
        Gson gson = new Gson();
        Map returnedResult;

        try {
            returnedResult = gson.fromJson(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)), Map.class);
        } catch (NullPointerException e) {
            return new DrawableClaim[0];
        }
        if (!returnedResult.get("type").equals("FeatureCollection")) return new DrawableClaim[0];
        //coordinates are stored as [lon, lat]

        JsonArray features = gson.toJsonTree(returnedResult, Map.class).getAsJsonObject().get("features").getAsJsonArray();
        DrawableClaim[] claims = new DrawableClaim[features.size()];

        for (int i = 0; i < claims.length; i++) {
            JsonObject feature = features.get(i).getAsJsonObject();

            boolean finished = feature.get("properties").getAsJsonObject().get("finished").getAsBoolean();
            JsonArray verticesArray = feature.get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonArray();
            double[][] vertices = new double[verticesArray.size()][2];

            double leftmost = 180;
            double rightmost = -180;
            double topmost = -90;
            double bottommost = 90;

            for (int j = 0; j < vertices.length; j++) {
                for (int k = 0; k < 2; k++) {
                    vertices[j][k] = verticesArray.get(j).getAsJsonArray().get(k).getAsDouble();
                }

                rightmost = Math.max(rightmost, vertices[j][0]);
                leftmost = Math.min(leftmost, vertices[j][0]);
                topmost = Math.max(topmost, vertices[j][1]);
                bottommost = Math.min(bottommost, vertices[j][1]);

            }

            claims[i] = new DrawableClaim(vertices, finished, leftmost, rightmost, topmost, bottommost);
        }

        return claims;

    }

    public boolean triangulationSucceeded() {
        return triangles != null;
    }

    public boolean inBoundsOf(double mapPosX, double mapPosY, int mapRenderWidth, int mapRenderHeight, double zoom, int scaledSize) {
        double mapLeftBorder = (-mapRenderWidth / 2) + mapPosX;
        double mapRightBorder = (mapRenderWidth / 2) + mapPosX;
        double mapTopBorder = (-mapRenderHeight / 2) + mapPosY;
        double mapBottomBorder = (mapRenderHeight / 2) + mapPosY;
        return
                UnitConvert.longToMapX(leftmost, zoom, scaledSize) < mapRightBorder &&
                        UnitConvert.longToMapX(rightmost, zoom, scaledSize) > mapLeftBorder &&
                        UnitConvert.latToMapY(topmost, zoom, scaledSize) < mapBottomBorder &&
                        UnitConvert.latToMapY(bottommost, zoom, scaledSize) > mapTopBorder
                ;
    }

    public static void reloadClaimData(boolean notifyMapScreen, boolean notifyChat, boolean considerTimeLimit) {
        if (considerTimeLimit) {
            long neededTime = (lastReloaded + 60000);
            if (Util.getMeasuringTimeMs() < neededTime) {
                MutableText waitNotifyText = Text.literal(
                        Text.translatable("omm.claims.wait-start").getString() +
                                ((int) (neededTime - Util.getMeasuringTimeMs() + 1000) / 1000) +
                                Text.translatable("omm.claims.wait-end").getString()
                );
                if (notifyMapScreen) MapScreen.addNotification(new Notification(waitNotifyText));
                if (notifyChat) MinecraftClient.getInstance().player.sendMessage(waitNotifyText.formatted(CommandHander.ERROR_COLOR), false);
                return;
            }
        }
        if (notifyMapScreen) MapScreen.addNotification(new Notification(Text.translatable("omm.claims.reloading")));
        if (notifyChat) MinecraftClient.getInstance().player.sendMessage(Text.translatable("omm.claims.reloading").formatted(CommandHander.FEEDBACK_COLOR), false);
        new Loader().start();
        if (considerTimeLimit) lastReloaded = Util.getMeasuringTimeMs();
    }

    public static class Loader extends Thread {
        @Override
        public void run() {
            loadClaims();
        }

        private void loadClaims() {
            try {
                DrawableClaim.succeededTriangulations = 0;
                OmmMap.claims = null;
                RequestManager.loadClaims();
                while (!RequestManager.claimsLoaded()) {
                    Thread.sleep(100);
                }
                if (RequestManager.claims == null) throw new Exception();
                OmmMap.claims = DrawableClaim.of(RequestManager.claims);
                //if (ConfigFile.readParameter(ConfigOptions.__SHOW_DEVELOPER_OPTIONS).equals("true")) MinecraftClient.getInstance().player.sendMessage(Text.literal("Claim triangulation success rate: " + (((double) DrawableClaim.succeededTriangulations / OmmMap.claims.length) * 100) + "%"), false);
            } catch (Exception e) {
                MinecraftClient.getInstance().player.sendMessage(Text.translatable("omm.error.load-claims"), false);
            }
        }
    }

}
