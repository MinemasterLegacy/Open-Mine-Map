package net.mmly.openminemap.search;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;

import java.util.Arrays;

public class SearchBoxLayer extends TextFieldWidget {

    private static SearchResult[] searchResults = new SearchResult[7]; //not sure about length yet, may need to be longer or be a more advanced array type (ex. arraylist)
    private static int scroll = 0;
    private static String previousText = "";

    public SearchBoxLayer(TextRenderer textRenderer, int x, int y) {
        super(textRenderer, x, y, 200, 20, Text.of(""));
        this.setEditable(true);
    }

    public void drawWidget(DrawContext context) {
        if (!previousText.equals(getText())) {
            previousText = getText();
            recalculateResults();
        }
        this.render(context, 0, 0, 0);
        if (getText().isEmpty() && isVisible()) { //<Translation>
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, "Search anything...", getX() + 4, getY() + 6, 0xFF404040);
        }
        //context.drawBorder(getX(), getY(), getX() + width, getY() + height, 0xFF00FF00);
    }

    private void clearSearchResults() {
        Arrays.fill(searchResults, null);
        for (int i = 0; i < FullscreenMapScreen.searchResultLayers.length; i++) {
            FullscreenMapScreen.searchResultLayers[i].setResult(null);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        setEditable(true);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        System.out.println("click");
    }

    private void addSearchResult(SearchResult result) {
        for (int i = 0; i < searchResults.length; i++) {
            if (searchResults[i] == null) {
                searchResults[i] = result;
                return;
            }
        }
    }

    private SearchResult[] getSearchHistory() {
        //TODO
        return new SearchResult[0];
    }

    public void recalculateResults() {
        clearSearchResults();

        //if nothing has been typed yet, show reent search history
        if (this.getText().isBlank()) {
            for (SearchResult result : getSearchHistory()) {
                addSearchResult(result);
            }
            return;
        }

        //If the search text is coordinates, add that as an option
        try {
            String[] coordinateStrings = this.getText().trim().replaceAll(",", " ").split(" ");
            double[] coordinateAttempt = UnitConvert.toDecimalDegrees(coordinateStrings[0], coordinateStrings[1]);
            if (coordinateAttempt != null) {
                addSearchResult(new SearchResult(
                        SearchResultType.COORDINATES,
                        coordinateAttempt[0],
                        coordinateAttempt[1],
                        UnitConvert.floorToPlace(coordinateAttempt[0], 7) + ", " + UnitConvert.floorToPlace(coordinateAttempt[1], 7)
                ));
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        //If the search text references any waypoints, add those
        for (Waypoint waypoint : OmmMap.getWaypoints()) {
            if (waypoint.name.toLowerCase().contains(getText().toLowerCase())) {
                addSearchResult(new SearchResult(
                        SearchResultType.WAYPOINT,
                        waypoint.latitude,
                        waypoint.longitude,
                        waypoint.name,
                        UnitConvert.floorToPlace(waypoint.latitude, 7) + ", " + UnitConvert.floorToPlace(waypoint.longitude, 7)
                ));
            }
        }

        //If the search text is a player, add them
        for (PlayerEntity player : PlayersManager.getNearPlayers()) {
            try {
                if (player.getDisplayName().contains(Text.of(this.getText())) && player != MinecraftClient.getInstance().player) {
                    double[] latLong = Projection.to_geo(player.getX(), player.getZ());
                    addSearchResult(new SearchResult(
                            SearchResultType.PLAYER,
                            latLong[0],
                            latLong[1],
                            player.getDisplayName().getString(),
                            ((int) player.distanceTo(MinecraftClient.getInstance().player)) + " blocks away")); //TODO use geo distance instead of mc distance
                }
            } catch (NullPointerException | CoordinateValueError ignored) {}
        }

        //check history for any matching results and add them
        //TODO

        //temp
        System.out.println("-----= RESULTS =-----");
        for (SearchResult result : searchResults) {
            if (result == null) System.out.println("null");
            else System.out.println(result.name);
        }

        //set result widgets
        for (int i = 0; i < FullscreenMapScreen.searchResultLayers.length; i++) {
            FullscreenMapScreen.searchResultLayers[i].setResult(searchResults[i]);
        }

    }

}
