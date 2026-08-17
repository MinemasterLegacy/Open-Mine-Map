package net.mmly.openminemap.http;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.search.SearchResult;
import net.mmly.openminemap.util.Notification;

import java.awt.*;
import java.io.InputStream;

public class ReverseSearchRequester extends LocationJsonRequester<double[]> {

    public ReverseSearchRequester() {
        super("Reverse Search");
    }

    @Override
    public void request() {
        doReverseSearch();
    }

    private boolean noResults = false;

    SearchResult reverseSearchRequest(double lat, double lon) {
        String urlPattern = "https://photon.komoot.io/reverse?lon=" + lon + "&lat=" + lat;

        InputStream stream = get(urlPattern);
        SearchResult[] results = parseLocationJson(stream);
        if (results == null) return null;
        if (results.length == 0) noResults = true;
        if (results[0] == null) return null;
        if (Double.isNaN(results[0].latitude)) return null;

        return results[0];
    }

    private void doReverseSearch() {
        SearchResult result = reverseSearchRequest(pendingRequest[0], pendingRequest[1]);
        if (noResults) {
            MapScreen.addNotification(new Notification(Text.translatable("omm.search.no-results")));
        }
        else if (result == null) {
            MapScreen.addNotification(new Notification(Text.translatable("omm.notification.something-wrong")));
        } else {
            try {
                String location = "";
                if (result.name != null && MapScreen.map.getTileZoom() >= 14) location += result.name + ", ";
                if (result.context != null) location += result.context + ", ";
                if (!location.isEmpty()) location = location.substring(0, location.length() - 2);

                MinecraftClient.getInstance().keyboard.setClipboard(location);
                MapScreen.addNotification(new Notification(Text.translatable("omm.notification.location-copied")));
            } catch (HeadlessException e) {
                MapScreen.addNotification(new Notification(Text.translatable("omm.notification.something-wrong")));
            }
        }
    }

}
