package net.mmly.openminemap.map;

public class RequestManager {

    static int[] pendingRequest;

    public static void trySetRequest(int x, int y, int zoom) {
        if (zoom > 18) return;
        if (pendingRequest == null) pendingRequest = new int[] {x, y, zoom};
    }

}
