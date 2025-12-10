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
    private static WaypointObject[] waypoints = new WaypointObject[0];
    private static final File waypointsFile = new File(TileManager.getRootFile() + "openminemap/waypoints.json");
    private static WorldType worldType;
    private static String worldName;

    public static void save() {
        Gson gson = new Gson();

        try {

            try {

                /*
                WaypointObject waypoint = new WaypointObject(WorldType.SINGLEPLAYER, "pheenix", "abc", 10, 20, 0xFFFFFFFF, "diamond", 69.420);
                WaypointObject w2 = new WaypointObject(WorldType.SINGLEPLAYER, "pheenix", "def", 40, 50, 0xFFFF0000, "diamond", 12.345);
                WaypointObject[] waypoints = new WaypointObject[] {waypoint, w2};
                 */

                FileWriter writer = new FileWriter(waypointsFile);

                gson.toJson(waypoints, writer);

                writer.flush();
                writer.close();

                System.out.println("write call success to "+waypointsFile.getAbsolutePath());

            } catch (JsonSyntaxException e) {
                OpenMineMapClient.debugMessages.add("Waypoints file save failed: "+e.getMessage());
                throw new WaypointFileFormatException();
            }

        } catch (IOException | WaypointFileFormatException e) {
            System.out.println("Waypoints file save failed: "+e.getMessage());
        }
    }

    public static void load() {

        Gson gson = new Gson();

        File file = new File(TileManager.getRootFile() + "openminemap/waypoints.json");
        if (!file.exists()) createDefaultFile();

        try (FileReader reader = new FileReader(file)) {
            try {
                waypoints = gson.fromJson(reader, WaypointObject[].class);
                if (waypoints == null) {
                    waypoints = new WaypointObject[0];
                    return;
                }
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

    private static boolean establishWorld() {
        try {
            worldName = MinecraftClient.getInstance().getCurrentServerEntry().name;//gets the server name if multiplayer, otherwise will fail
            worldType = WorldType.MULTIPLAYER;
        } catch (NullPointerException e) { //if fails, that must mean the world is singleplayer
            try {
                //get the save file name for singleplayer
                worldName = MinecraftClient.getInstance().getServer().getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString();
                worldType = WorldType.SINGLEPLAYER;
            } catch (NullPointerException ex) { //if both checks fail to product a name, load no waypoints and give an error in chat
                OpenMineMapClient.debugMessages.add("OpenMineMap: Unable to determine level identifier.");
                return false;
            }
        }

        System.out.println("World Name resolved to \""+worldName+"\"");

        return true;
    }

    //for the server lifecycle events to call
    public static void setWaypointsOfThisWorld(MinecraftServer minecraftServer) {
        setWaypointsOfThisWorld(true);
    }

    public static void setWaypointsOfThisWorld(boolean establishWorld) {

        if (establishWorld) {
            if (!establishWorld()) {
                OmmMap.setWaypoints(new Waypoint[0]);
                return;
            }
        }

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

    public static void addWaypoint(String style, double lat, double lon, int color, double angle, String name, boolean pinned, boolean visible) {
        WaypointObject[] outputList = new WaypointObject[waypoints.length + 1];

        int i = 0;
        for (WaypointObject way : waypoints) {
            outputList[i] = way;
            i++;
        }

        outputList[outputList.length-1] = new WaypointObject(worldType, worldName, name, lon, lat, color, style, angle, pinned, visible);
        waypoints = outputList;
    }

    private static Waypoint synthesizeWaypoint(WaypointObject waypointObject) {
        return new Waypoint(
                waypointObject.style,
                waypointObject.latitude,
                waypointObject.longitude,
                waypointObject.color,
                waypointObject.angle,
                waypointObject.name,
                waypointObject.pinned,
                waypointObject.visible
        );
    }

    private static void createDefaultFile() {
        waypoints = new WaypointObject[0];
        save();
    }

    public static boolean setWaypointVisibility(String name, boolean visible) {
        //will always be executed only on waypoints for the given world
        //returns false is something failed, which should cause the entry widget to revert to the previous state and throw an error
        for (WaypointObject waypoint : waypoints) {
            if(isInThisWorld(waypoint) && waypoint.name.equals(name)) {
                waypoint.visible = visible;
                setWaypointsOfThisWorld(false);
                return true;
            }
        }
        return false;
    }

    public static boolean setWaypointPinned(String name, boolean pinned) {
        for (WaypointObject waypoint : waypoints) {
            if(isInThisWorld(waypoint) && waypoint.name.equals(name)) {
                waypoint.pinned = pinned;
                setWaypointsOfThisWorld(false);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteWaypoint(String name) {
        WaypointObject[] outputList = new WaypointObject[waypoints.length - 1];

        int i = 0;
        boolean found = false;

        for (WaypointObject way : waypoints) {
            if (way.name.equals(name) && !found) {
                found = true;
            } else {
                outputList[i - (found ? 1 : 0)] = way;
            }
            i++;
        }

        waypoints = outputList;
        return found;
    }

    public static boolean overwriteWaypoint(String originalName, String newName, double lat, double lon, int color, double snapAngle, String type) {
        int i = 0;
        for (WaypointObject waypoint : waypoints) {
            if(isInThisWorld(waypoint) && waypoint.name.equals(originalName)) {
                waypoints[i] = new WaypointObject(worldType, worldName, newName, lon, lat, color, type, snapAngle, waypoint.pinned, waypoint.visible);
                setWaypointsOfThisWorld(false);
                return true;
            }
            i++;
        }
        return false;
    }

    private static boolean isInThisWorld(WaypointObject waypointObject) {
        return (waypointObject.world_type.equals(worldType.toString().toLowerCase()) && waypointObject.world_name.equals(worldName));
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
    boolean pinned;
    boolean visible;

    public WaypointObject(WorldType worldType, String worldName, String name, double longitude, double latitude, int argb, String style, double angle, boolean pinned, boolean visible) {
        this.angle = angle;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.style = style;
        this.color = argb;
        this.world_type = worldType.name().toLowerCase();
        this.world_name = worldName;
        this.pinned = pinned;
        this.visible = visible;
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