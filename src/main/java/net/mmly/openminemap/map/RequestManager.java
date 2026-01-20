package net.mmly.openminemap.map;

public class RequestManager {

    static int[] pendingRequest;
    static LoadableTile candidateRequest;
    static int mapCenterX = 64;
    static int mapCenterY = 64;
    static boolean hudMapIsPrimary = true;

    @Deprecated
    public static void trySetRequest(int x, int y, int zoom) {
        if (zoom > 18) return;
        if (pendingRequest == null) pendingRequest = new int[] {x, y, zoom};
    }

    //there has to be a better way of doing this then passing booleans around
    public static void setMapCenter(int x, int y, boolean isHudMap) {
        if (!(hudMapIsPrimary == isHudMap)) return;
        mapCenterX = x;
        mapCenterY = y;
    }

    public static void resetCandidate() {
        candidateRequest = new LoadableTile();
    }

    public static void consider(int x, int y, int zoom, int tileRenderSize, boolean isHudMap) {
        if (!(hudMapIsPrimary == isHudMap)) return;
        int proximityScore = (int) Math.sqrt(Math.pow(mapCenterX - ((x + 0.5) * tileRenderSize) , 2) + Math.pow(mapCenterY - ((y + 0.5) * tileRenderSize) , 2));
        if (!candidateRequest.isValidTile || candidateRequest.proximityScore > proximityScore) {
            candidateRequest = new LoadableTile(x, y, zoom, proximityScore);
        }
    };

    public static void pushRequest(boolean isHudMap) {
        if (candidateRequest == null) return;
        if (pendingRequest == null && hudMapIsPrimary == isHudMap) {
            pendingRequest = candidateRequest.getDataArray();
        }
    }

    public static void setMapType(boolean isHudMap) {
        hudMapIsPrimary = isHudMap;
    }

}