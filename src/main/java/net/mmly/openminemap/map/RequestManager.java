package net.mmly.openminemap.map;

public class RequestManager {

    static RequestableTile pendingRequest;
    static RequestableTile candidateRequest;
    static int mapCenterX = 64;
    static int mapCenterY = 64;
    static boolean hudMapIsPrimary = true;

    //there has to be a better way of doing this then passing booleans around
    public static void setMapCenter(int x, int y, boolean isHudMap) {
        if (!(hudMapIsPrimary == isHudMap)) return;
        mapCenterX = x;
        mapCenterY = y;
    }

    public static void resetCandidate() {
        candidateRequest = null;
    }

    public static void consider(int x, int y, int zoom, int tileRenderSize, boolean isHudMap) {
        if (!(hudMapIsPrimary == isHudMap)) return;
        int proximityScore;
        if (x == 0 && y == 0 && zoom == 0) proximityScore = 0;
        else proximityScore = (int) Math.sqrt(Math.pow(mapCenterX - ((x + 0.5) * tileRenderSize) , 2) + Math.pow(mapCenterY - ((y + 0.5) * tileRenderSize) , 2));
        if (candidateRequest == null) {
            candidateRequest = new RequestableTile(x, y, zoom, proximityScore);
            return;
        }
        if (candidateRequest.proximityScore > proximityScore) {
            candidateRequest = new RequestableTile(x, y, zoom, proximityScore);
        }
    };

    public static void pushRequest(boolean isHudMap) {
        if (candidateRequest == null) return;
        if (pendingRequest == null && hudMapIsPrimary == isHudMap) {
            pendingRequest = candidateRequest;
        }
    }

    public static void setMapType(boolean isHudMap) {
        hudMapIsPrimary = isHudMap;
    }

}