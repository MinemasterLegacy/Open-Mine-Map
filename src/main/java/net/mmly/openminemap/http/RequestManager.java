package net.mmly.openminemap.http;

import net.minecraft.client.MinecraftClient;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.LoadableTile;
import net.mmly.openminemap.map.RequestableTile;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.search.SearchBoxLayer;
import net.mmly.openminemap.search.SearchResult;
import net.mmly.openminemap.util.NamedLocation;
import net.mmly.openminemap.util.RasterProvider;
import net.mmly.openminemap.util.TileUrl;
import net.mmly.openminemap.util.TileUrlFile;

import java.awt.image.Raster;
import java.io.InputStream;

public class RequestManager {

    protected static SearchResult[] searchResults = null;
    protected static boolean claimsLoaded = true;
    protected static InputStream claims = null;
    protected static TileRequester tileRequester;
    protected static RequestableTile candidateTile = null;
    private static boolean rastersInitialized = false;

    public static void reverseSearch(double lat, double lon) {
        ReverseSearchRequester requester = new ReverseSearchRequester();
        requester.setPendingRequest(new double[]{lat, lon});
        requester.start();
    }

    public static void search(String term) {
        search(term, Double.NaN, Double.NaN);
    }

    public static void search(String term, double latFocus, double lonFocus) {
        SearchBoxLayer.toggleSearching(true);
        SearchRequester requester = new SearchRequester();
        requester.setPendingRequest(new NamedLocation(term, latFocus, lonFocus, -1));
        requester.start();
    }

    public static SearchResult[] getSearchResults() {
        return searchResults;
    }

    public static void clearSearchResults() {
        searchResults = null;
    }

    public static boolean claimsLoaded() {
        return claimsLoaded;
    }

    public static InputStream getClaims() {
        return claims;
    }

    public static void loadClaims() {
        claimsLoaded = false;
        new Requester<>(false, 1000, "Claims") {
            @Override
            public void request() {
                claims = get("https://api.buildtheearth.net/api/v1/claims/geojson?active=true");
                claimsLoaded = true;
            }
        }.start();
    }

    public static void startTileRequester() {
        tileRequester = new TileRequester(true);
        tileRequester.start();
    }

    public static void clearCandidateTile() {
        candidateTile = null;
    }

    private static int getMapCenterX() {
        return currentPriorityMapType() == MapType.HUD ?
                (int) HudMap.map.getMapCenterX() :
                (int) MapScreen.map.getMapCenterX();
    }

    private static int getMapCenterY() {
        return currentPriorityMapType() == MapType.HUD ?
                (int) HudMap.map.getMapCenterY() :
                (int) MapScreen.map.getMapCenterY();
    }

    public static void considerTile(int x, int y, int zoom, int tileRenderSize, MapType mapType, TileUrl raster) {
        if (mapType != currentPriorityMapType()) return;
        if (raster == null) return;
        int proximityScore;
        if (x == 0 && y == 0 && zoom == 0) proximityScore = 0;
        else proximityScore = (int) Math.sqrt(Math.pow(getMapCenterX() - ((x + 0.5) * tileRenderSize) , 2) + Math.pow(getMapCenterY() - ((y + 0.5) * tileRenderSize) , 2));
        if (candidateTile == null) {
            candidateTile = new RequestableTile(x, y, zoom, proximityScore, raster);
            return;
        }
        if (candidateTile.proximityScore > proximityScore) {
            candidateTile = new RequestableTile(x, y, zoom, proximityScore, raster);
        }
    }

    public static void pushTileRequest(MapType mapType) {
        if (!rastersInitialized) {
            TileUrlFile.loadRastersFromFile();
            rastersInitialized = true;
        }
        if (candidateTile == null) return;
        if (ConfigOptions.__DISABLE_WEB_REQUESTS.getAsBoolean()) return;
        if (tileRequester.pendingRequest == null && currentPriorityMapType() == mapType) {
            tileRequester.setPendingRequest(candidateTile);
        }
    }

    public static RequestableTile getPendingRequest() {
        return tileRequester.pendingRequest;
    }

    public static MapType currentPriorityMapType() {
        return MinecraftClient.getInstance().currentScreen == null ? MapType.HUD : MapType.FULLSCREEN;
    }
}
