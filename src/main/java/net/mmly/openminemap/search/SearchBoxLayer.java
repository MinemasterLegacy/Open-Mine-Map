package net.mmly.openminemap.search;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.mmly.openminemap.gui.FullscreenMapScreen;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.map.RequestManager;
import net.mmly.openminemap.map.TileManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.projection.CoordinateValueError;
import net.mmly.openminemap.projection.Projection;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;
import org.lwjgl.glfw.GLFW;

import java.time.Duration;
import java.util.Arrays;

public class SearchBoxLayer extends TextFieldWidget {

    private static SearchResult[] searchResults = new SearchResult[7]; //not sure about length yet, may need to be longer or be a more advanced array type (ex. arraylist)
    private static int scroll = 0;
    private static String previousText = "";
    private static int numResults;
    private static boolean searching = false;
    private static String valueStore;
    private static SearchBoxLayer instance;

    public SearchBoxLayer(TextRenderer textRenderer, int x, int y) {
        super(textRenderer, x, y, 250, 20, Text.of(""));
        this.setEditable(true);
        this.setMaxLength(1000);
        instance = this;
    }

    public static SearchBoxLayer getInstance() {
        return instance;
    }

    public static void toggleSearching(boolean toggle) {
        searching = toggle;
        if (searching) {
            valueStore = getInstance().getText();
            getInstance().setText("");
        } else {
            getInstance().setText(valueStore);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searching) return true;
        if (keyCode == GLFW.GLFW_KEY_ENTER) {
            FullscreenMapScreen.getInstance().jumpToBestOption();
            //RequestManager.setSearchRequest(FullscreenMapScreen.getInstance().getSearchBoxContents());
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public void drawWidget(DrawContext context) {
        if (RequestManager.searchResultReturn != null) {
            toggleSearching(false);
            searchResults = RequestManager.searchResultReturn;
            numResults = RequestManager.searchResultReturn.length;
            RequestManager.searchResultReturn = null;
            FullscreenMapScreen.getInstance().jumpToSearchBox();
            updateResultElements();
        } else if (!previousText.equals(getText()) && !searching) {
            previousText = getText();
            recalculateResults();
        }
        this.render(context, 0, 0, 0);
        if (searching) {
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, "Searching...", getX() + 4, getY() + 6, 0xFF404040);
            return;
        }
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
        numResults = 0;
    }

    private void addSearchResult(SearchResult result) {
        for (int i = 0; i < searchResults.length; i++) {
            if (searchResults[i] == null) {
                searchResults[i] = result;
                numResults++;
                return;
            }
        }

        if (result.resultType == SearchResultType.SEARCH) {
            searchResults[searchResults.length-1] = result;
        }

    }

    private static SearchResult[] getSearchHistory() {
        //TODO
        return new SearchResult[] {
                new SearchResult(SearchResultType.PLAYER, 40, 40, true, "Player301", "Distance: 31m")
        };
    }

    public void recalculateResults() {
        clearSearchResults();

        //if nothing has been typed yet, show recent search history
        if (this.getText().isBlank()) {
            for (SearchResult result : getSearchHistory()) {
                addSearchResult(result);
            }
            updateResultElements();
            return;
        }

        //If the search text is coordinates, add that as an option
        try {
            String[] coordinateStrings = this.getText().trim().replaceAll(",", " ").split(" ");
            coordinateStrings = removeExtra(coordinateStrings);
            double[] coordinateAttempt = UnitConvert.toDecimalDegrees(coordinateStrings[0], coordinateStrings[1]);
            if (coordinateAttempt != null && !OmmMap.geoCoordsOutOfBounds(coordinateAttempt[0], coordinateAttempt[1])) {
                addSearchResult(new SearchResult(
                        SearchResultType.COORDINATES,
                        coordinateAttempt[0],
                        coordinateAttempt[1],
                        false,
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
                        false,
                        waypoint.name
                ));
            }
        }

        //If the search text is a player, add them
        for (PlayerEntity player : PlayersManager.getNearPlayers()) {
            try {
                if (player.getDisplayName().getString().toLowerCase().contains(getText().toLowerCase()) && player != MinecraftClient.getInstance().player) {
                    double[] latLong = Projection.to_geo(player.getX(), player.getZ());
                    addSearchResult(new SearchResult(
                            SearchResultType.PLAYER,
                            latLong[0],
                            latLong[1],
                            false,
                            player.getDisplayName().getString(),
                            ((int) player.distanceTo(MinecraftClient.getInstance().player)) + " blocks away")); //TODO use geo distance instead of mc distance
                }
            } catch (NullPointerException | CoordinateValueError ignored) {}
        }

        for (SearchResult result : getSearchHistory()) {
            if (result.name.toLowerCase().contains(getText().toLowerCase())) {
                addSearchResult(result);
            }
        }

        if (getText().length() >= 3) addSearchResult(new SearchResult(SearchResultType.SEARCH, 0, 0, false,"Search Places", "Using photon search"));

        //check history for any matching results and add them
        //TODO

        /*
        System.out.println("-----= RESULTS =-----");
        for (SearchResult result : searchResults) {
            if (result == null) System.out.println("null");
            else System.out.println(result.name);
        }
        */

        //set result widgets
        updateResultElements();

    }

    private static void updateResultElements() {
        for (int i = 0; i < FullscreenMapScreen.searchResultLayers.length; i++) {
            FullscreenMapScreen.searchResultLayers[i].setResult(searchResults[i]);
        }
    }

    public static void setValueStore(String value) {
        valueStore = value;
    }

    public static int getNumResults() {
        return numResults;
    }

    private String[] removeExtra(String[] array) {
        String[] newArray = new String[] {" ", " "};
        for (String element : array) {
            if (!element.isBlank()) {
                if (newArray[0].isBlank()) {
                    newArray[0] = element;
                } else {
                    newArray[1] = element;
                    return newArray;
                }
            }
        }
        return new String[] {""}; // Does not have coordinates, so return a short array that will throw an OOB exception
    }

}
