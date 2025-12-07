package net.mmly.openminemap.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import net.mmly.openminemap.OpenMineMapClient;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.waypoint.WaypointStyle;

import java.io.*;
import java.util.Objects;

public class WaypointFile {

    public static boolean loadWasFailed = false;
    private static WaypointObject[] waypoints;
    private static final File waypointsFile = new File(TileManager.getRootFile() + "openminemap/waypoints.json");

    public static void save() {
        Gson gson = new Gson();

        try (FileReader reader = new FileReader(waypointsFile)) {

            try {
                //WorldCategoriesList categoriesList = gson.fromJson(reader, WorldCategoriesList.class);
                //System.out.println("from wp: "+categoriesList.categories.length);

                WaypointObject waypoint = new WaypointObject(WorldType.SINGLEPLAYER, "pheenix", "abc", 10, 20, 0xFFFFFFFF, "diamond", 69.420);
                WaypointObject w2 = new WaypointObject(WorldType.SINGLEPLAYER, "pheenix", "def", 40, 50, 0xFFFF0000, "diamond", 12.345);

                WaypointObject[] waypoints = new WaypointObject[] {waypoint, w2};

                FileWriter writer = new FileWriter(waypointsFile);

                gson.toJson(waypoints, writer);

                writer.flush();
                writer.close();

                System.out.println("write call success to "+waypointsFile.getAbsolutePath());

            } catch (JsonSyntaxException e) {
                OpenMineMapClient.debugMessages.add("Waypoints file load failed: "+e.getMessage());
                throw new WaypointFileFormatException();
            }

        } catch (IOException | WaypointFileFormatException e) {
            System.out.println("save fail"); //TODO
        }
    }

    public static void load() {

        Gson gson = new Gson();

        File file = new File(TileManager.getRootFile() + "openminemap/waypoints.json");
        if (!file.exists()) createDefaultFile();

        try (FileReader reader = new FileReader(file)) {
            try {
                waypoints = gson.fromJson(reader, WaypointObject[].class);
                return;
            } catch (JsonSyntaxException e) {
                throw new WaypointFileFormatException();
            }
        } catch (IOException | WaypointFileFormatException e) {
            OpenMineMapClient.debugMessages.add("OpenMineMap: Error while loading waypoints.json, Waypoints will not appear");
            loadWasFailed = true;
        }

        waypoints = new WaypointObject[0];

    }

    public static void setWaypointsOfThisWorld(MinecraftServer minecraftServer) {

        String worldName;
        WorldType worldType;

        try {
            worldName = MinecraftClient.getInstance().getCurrentServerEntry().name;//gets the server name if multiplayer, otherwise will fail
            worldType = WorldType.MULTIPLAYER;
        } catch (NullPointerException e) { //if fails, that must mean the world is singleplayer
            try {
                //get the save file name for singleplayer
                worldName = MinecraftClient.getInstance().getServer().getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString();
                worldType = WorldType.SINGLEPLAYER;
            } catch (NullPointerException ex) { //if both checks fail to product a name, load no waypoints and give an error in chat
                OmmMap.setWaypoints(new Waypoint[0]);
                OpenMineMapClient.debugMessages.add("OpenMineMap: Waypoints failed to load: Unable to determine level identifier.");
                return;
            }
        }

        System.out.println("World Name resolved to \""+worldName+"\"");

        int amount = 0;
        for (int i = 0; i < waypoints.length; i++) {
            if (waypoints[i].world_name.equals(worldName) && worldType.name().toLowerCase().equals(waypoints[i].world_type)) amount++;
        }

        Waypoint[] wps = new Waypoint[amount];
        int j = 0;
        for (int i = 0; i < waypoints.length; i++) {
            if (waypoints[i].world_name.equals(worldName) && worldType.name().toLowerCase().equals(waypoints[i].world_type)) {
                wps[j] = synthesizeWaypoint(waypoints[i]);
                j++;
            }
        }

        OmmMap.setWaypoints(wps);
    }

    private static Waypoint synthesizeWaypoint(WaypointObject waypointObject) {
        return new Waypoint(
                waypointObject.style,
                waypointObject.latitude,
                waypointObject.longitude,
                waypointObject.color,
                waypointObject.angle,
                waypointObject.name
        );
    }

    private static void createDefaultFile() {
        //TODO
    }

}

class WaypointObject {
    String world_type;
    String world_name;
    String name;
    double longitude;
    double latitude;
    int color;
    String style;
    double angle;

    public WaypointObject(WorldType worldType, String worldName, String name, double longitude, double latitude, int argb, String style, double angle) {
        this.angle = angle;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.style = style;
        this.color = argb;
        this.world_type = worldType.name().toLowerCase();
        this.world_name = worldName;
    }
}

class WaypointFileFormatException extends Exception{
    public WaypointFileFormatException() {
        super("Formatting error while reading waypoints.json");
    }
}

enum WorldType {
    SINGLEPLAYER,
    MULTIPLAYER
}