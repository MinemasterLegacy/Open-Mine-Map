package net.mmly.openminemap.search;

import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.util.NamedLocation;

public class SearchResult {

    public SearchResultType resultType;
    public double longitude;
    public double latitude;
    public String name;
    public String context = "";
    public double zoom = -1;
    public boolean historic;
    public double[] bounds = null; //y1, y2, x1, x2

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, String context, double[] bounds) {
        this(type, latitude, longitude, historic, name, context);
        this.bounds = bounds;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, double[] bounds) {
        this(type, latitude, longitude, historic, name);
        this.bounds = bounds;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, String context) {
        this(type, latitude, longitude, historic, name);
        this.context = context;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, double zoom) {
        this(type, latitude, longitude, historic, name);
        this.zoom = zoom;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name, String context, double zoom) {
        this(type, latitude, longitude, historic, name, context);
        this.zoom = zoom;
    }

    public SearchResult(SearchResultType type, double latitude, double longitude, boolean historic, String name) {
        this.resultType = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.historic = historic;
    }

    public static SearchResult historic(String term, boolean bounded) {
        return new SearchResult(bounded ? SearchResultType.SEARCHLOCAL : SearchResultType.SEARCH, 0, 0, true, term);
    }

    public NamedLocation asLocation() {
        return new NamedLocation(name, latitude, longitude, -1);
    }

    public void focusOnMapViaSearchMenu() {
        MapScreen.followPlayer(false);

        if (bounds != null) {
            MapScreen.map.goAndZoomToBounds(bounds, true);
            return;
        }

        MapScreen.map.setMapLatLong(latitude, longitude);
        MapScreen.map.setMapCenterX(MapScreen.map.getMapCenterX() - ((double) SearchBoxLayer.getInstance().getRight() / 2));

        if (zoom > 0) {
            MapScreen.map.setMapZoom(zoom);
        }
        MapScreen.map.clampZoom();
    }
}
