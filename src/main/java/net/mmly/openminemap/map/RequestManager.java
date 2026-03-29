package net.mmly.openminemap.map;

import net.mmly.openminemap.search.SearchBoxLayer;
import net.mmly.openminemap.search.SearchResult;

import java.io.InputStream;

public class RequestManager {

    static RequestableTile pendingRequest;
    static RequestableTile candidateRequest;
    static int mapCenterX = 64;
    static int mapCenterY = 64;
    static boolean hudMapIsPrimary = true;
    static String searchString = null;
    static double searchPriorityLat = Double.NaN;
    static double searchPriorityLon = Double.NaN;
    static double reverseSearchLat = Double.NaN;
    static double reverseSearchLong = Double.NaN;
    public static SearchResult[] searchResultReturn = null;

    public static boolean claimsLoaded = true;
    public static InputStream claims = null;

    public static boolean claimsLoaded() {
        return claimsLoaded;
    }

    public static void setClaims(InputStream claimStream) {
        claims = claimStream;
        claimsLoaded = true;
    }

    public static void loadClaims() {
        claimsLoaded = false;
    }

    public static boolean needToLoadClaims() {
        return !claimsLoaded;
    }

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
            candidateRequest = new RequestableTile(x, y, zoom, proximityScore, TileManager.cacheName);
            return;
        }
        if (candidateRequest.proximityScore > proximityScore) {
            candidateRequest = new RequestableTile(x, y, zoom, proximityScore, TileManager.cacheName);
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

    public static void setSearchRequest(String query, double latFocus, double lonFocus) {
        searchPriorityLat = latFocus;
        searchPriorityLon = lonFocus;
        searchString = query.replace("&", "");
        SearchBoxLayer.toggleSearching(true);
    }

    public static void setSearchRequest(String query) {
       setSearchRequest(query, Double.NaN, Double.NaN);
    }

    public static void setReverseSearchRequest(double latitude, double longitude) {
        if (Double.isNaN(latitude) || Double.isNaN(longitude)) return;
        reverseSearchLat = latitude;
        reverseSearchLong = longitude;
    }

    public static void resetReverseSearchCandidate() {
        reverseSearchLong = Double.NaN;
        reverseSearchLat = Double.NaN;
    }

}