package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.SearchResultType;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.SearchResult;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;

import java.util.Arrays;

public class SearchBoxLayer extends TextFieldWidget {

    private static SearchResult[] searchResults = new SearchResult[7]; //not sure about length yet, may need to be longer or be a more advanced array type (ex. arraylist)

    public SearchBoxLayer(TextRenderer textRenderer, int x, int y) {
        super(textRenderer, x, y, 200, 20, Text.of(""));
        this.setEditable(true);
    }

    public void drawWidget(DrawContext context) {
        this.render(context, 0, 0, 0);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.setEditable(true);
        recalculateResults();
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void clearSearchResults() {
        Arrays.fill(searchResults, null);
    }

    private void addSearchResult(SearchResult result) {
        for (int i = 0; i < searchResults.length; i++) {
            if (searchResults[i] != null) searchResults[i] = result;
        }
    }

    private SearchResult[] getSearchHistory() {
        //TODO
        return new SearchResult[0];
    }

    private void recalculateResults() {
        clearSearchResults();

        if (this.getText().isBlank()) {
            for (SearchResult result : getSearchHistory()) {
                addSearchResult(result);
            }
            return;
        }

        String[] coordinateStrings = this.getText().trim().replaceAll(",", " ").split(" ");
        double[] coordinateAttempt = UnitConvert.toDecimalDegrees(coordinateStrings[0], coordinateStrings[1]);
        if (coordinateAttempt != null) {
            addSearchResult(new SearchResult(SearchResultType.COORDINATES, coordinateAttempt[0], coordinateAttempt[1], "Go To Coordinates"));
        }

        for (Waypoint waypoint : OmmMap.getWaypoints()) {
            if (this.getText().contains(waypoint.name)) {
                addSearchResult(new SearchResult(SearchResultType.WAYPOINT, waypoint.latitude, waypoint.longitude, waypoint.name));
            }
        }

        for (PlayerEntity player : PlayersManager.getNearPlayers()) {
            try {
                if (player.getDisplayName().contains(Text.of(this.getText()))) {
                    double[] latLong = Projection.to_geo(player.getX(), player.getZ());
                    addSearchResult(new SearchResult(SearchResultType.PLAYER, latLong[0], latLong[1], player.getDisplayName().getString(), ((int) player.distanceTo(MinecraftClient.getInstance().player)) + " blocks away"));
                }
            } catch (NullPointerException | CoordinateValueError ignored) {}
        }

        //TODO location search
    }

}
