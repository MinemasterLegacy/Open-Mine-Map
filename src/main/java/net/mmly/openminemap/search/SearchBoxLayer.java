package net.mmly.openminemap.search;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.gui.MapScreen;
import net.mmly.openminemap.map.MappablePlayer;
import net.mmly.openminemap.map.PlayersManager;
import net.mmly.openminemap.map.RequestManager;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.UnitConvert;
import net.mmly.openminemap.util.Waypoint;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchBoxLayer extends TextFieldWidget {

    public static final int MAX_RESULTS = 15; //max number of thing sto be shown, controls the number of widgets to be created
    public static int maxDisplayedResults = 0; //max number of displayed results, controls the max amount that is rendered
    private static int searchScroll = 0;
    private static boolean displayingSearch = false;
    private static int numDisplayedResults = 0; //current number of displayed results, controls the current amount that is rendered

    private static SearchResult[] searchResults = new SearchResult[MAX_RESULTS];
    private static int numResults;
    private static String previousText = "";
    private static boolean searching = false;
    private static String valueStore;
    private static SearchBoxLayer instance;


    public SearchBoxLayer(TextRenderer textRenderer, int x, int y) {
        super(textRenderer, x, y, 250, 20, Text.of(""));
        this.setEditable(true);
        this.setMaxLength(1000);
        instance = this;
        this.setUneditableColor(MapScreen.getDarkTextColor());
    }

    public static int getNumDisplayedResults() {
        return numDisplayedResults;
    }

    public static boolean isResultVisible(int resultNum) {
        return resultNum == Math.clamp(resultNum, searchScroll, searchScroll + maxDisplayedResults - 1);
    }

    public static void setMaxDisplayedResults(int availableSpace) {
        maxDisplayedResults = availableSpace / 20;
        if (maxDisplayedResults < 1) maxDisplayedResults = 1;
        numDisplayedResults = Math.min(maxDisplayedResults, numDisplayedResults);
    }

    private static int getNumResultsOf(SearchResult[] results) {
        int num = 0;
        for (SearchResult result : results) {
            if (result == null) break;
            num++;
        }
        return num;
    }

    public static void showHistoricResult(SearchResult result) {
        searchResults = SearchHistoryFile.getResultsOf(result);
        previousText = result.name;
        numResults = getNumResultsOf(searchResults);
        getInstance().setText(result.name);
        MapScreen.map.displaySearchResults(searchResults);
        MapScreen.getInstance().jumpToSearchBox();
        updateResultElements();
    }

    public static SearchBoxLayer getInstance() {
        return instance;
    }

    public static void toggleSearching(boolean toggle) {
        searching = toggle;
        if (searching) {
            getInstance().setEditable(false);
            valueStore = getInstance().getText();
            getInstance().setText("");
            //SearchHistoryFile.writeToFile();
        } else {
            getInstance().setEditable(true);
            getInstance().setText(valueStore);
            resetScroll();
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searching) return true;
        if (keyCode == GLFW.GLFW_KEY_ENTER && !getText().isEmpty()) {
            MapScreen.getInstance().jumpToBestOption();
            //RequestManager.setSearchRequest(FullscreenMapScreen.getInstance().getSearchBoxContents());
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    private void drawTopScrollIndicator(DrawContext context) {
        if (searchScroll == 0) return;
        for (int i = 0; i < width; i += 2) {
            context.fill(getX() + i, getY() + height, getX() + i + 1, getY() + height + 1, 0xFFFFFFFF);
        }
    }

    private void drawBottomScrollIndicator(DrawContext context) {
        if (numDisplayedResults + searchScroll == numResults || numDisplayedResults < maxDisplayedResults) return;
        int yOffset = numDisplayedResults * 20;
        for (int i = 0; i < width; i += 2) {
            context.fill(getX() + i, getBottom() + yOffset - 1, getX() + i + 1, getBottom() + yOffset, 0xFFFFFFFF);
        }
    }

    /*
    private void drawSearchSeperator(DrawContext context) {
        if (numDisplayedResults <= 2 || !displayingSearch) return;
        int yOffset = (numDisplayedResults - 2) * 20;
        context.fill(getX(), getBottom() + yOffset, getRight(), getBottom() + yOffset + 1, 0xFFFFFFFF);
    }

     */

    public void drawWidget(DrawContext context) {
        if (isFocused()) MapScreen.map.setFocusedResult(-1);
        numDisplayedResults = Math.min(numResults, maxDisplayedResults);

        if (RequestManager.searchResultReturn != null) {
            toggleSearching(false);
            Arrays.fill(searchResults, null);
            System.arraycopy(RequestManager.searchResultReturn, 0, searchResults, 0, RequestManager.searchResultReturn.length);
            numResults = RequestManager.searchResultReturn.length;
            numDisplayedResults = Math.min(maxDisplayedResults, numResults);
            RequestManager.searchResultReturn = null;
            if (!searchResults[0].name.isEmpty()) MapScreen.map.displaySearchResults(searchResults);
            MapScreen.getInstance().jumpToSearchBox();
            updateResultElements();
        } else if (!previousText.equals(getText()) && !searching) {
            previousText = getText();
            recalculateResults();
        }

        setEditableColor(getText().isEmpty() && !isFocused() ? MapScreen.getDarkTextColor() : MapScreen.getPlainTextColor());
        if (searching) setPlaceholder(Text.translatable("omm.notification.searching"));
        else setPlaceholder(Text.translatable("omm.search.anything"));

        this.render(context, 0, 0, 0);

        if (!visible) return;
        drawTopScrollIndicator(context);
        drawBottomScrollIndicator(context);
        //drawSearchSeperator(context);

        /*
        if (searching) {
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.translatable("omm.notification.searching"), getX() + 4, getY() + 6, 0xFF404040);
            return;
        }

        if (getText().isEmpty() && isVisible()) { //<Translation>
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, Text.translatable("omm.search.anything").getString(), getX() + 4, getY() + 6, 0xFF404040);
        }
         */
        //context.drawBorder(getX(), getY(), getX() + width, getY() + height, 0xFF00FF00);
    }

    private void clearSearchResults() {
        Arrays.fill(searchResults, null);
        for (int i = 0; i < MapScreen.searchResultLayers.length; i++) {
            MapScreen.searchResultLayers[i].setResult(null);
        }
        numResults = 0;
    }

    private void addSearchResult(SearchResult result) {
        for (int i = 0; i < MAX_RESULTS; i++) {
            if (searchResults[i] == null) {
                searchResults[i] = result;
                numResults++;
                return;
            }
        }

        /*
        if (result.historic) return;
        if (result.resultType == SearchResultType.SEARCHLOCAL) {
            searchResults[Math.max(numResults, maxDisplayedResults - 2)] = result;
        }
        if (result.resultType == SearchResultType.SEARCH) {
            searchResults[Math.max(numResults, maxDisplayedResults - 1)] = result;
        }

         */
    }

    private static ArrayList<SearchResult> getSearchHistory() {
        return SearchHistoryFile.getHistoryAsResults();
    }

    public void recalculateResults() {
        clearSearchResults();
        resetScroll();
        MapScreen.map.disableSearchResults();

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
        for (MappablePlayer player : PlayersManager.getMappablePlayers()) {
            try {
                if (player.outOfBounds) continue;
                if (player.stylizedName.getString().toLowerCase().contains(getText().toLowerCase()) && player.uuid != MinecraftClient.getInstance().player.getUuid()) {
                    addSearchResult(new SearchResult(
                            SearchResultType.PLAYER,
                            player.latitude,
                            player.longitude,
                            false,
                            player.stylizedName.getString(),
                            ((int) player.distanceTo(MinecraftClient.getInstance().player)) + Text.translatable("omm.search.blocks-away").getString()));
                }
            } catch (NullPointerException ignored) {}
        }

        for (SearchResult result : getSearchHistory()) {
            if (result.name.toLowerCase().contains(getText().toLowerCase())) {
                addSearchResult(result);
            }
        }

        if (getText().length() >= 3) {
            displayingSearch = true;
            addSearchResult(new SearchResult(
                    SearchResultType.SEARCHLOCAL,
                    0, 0, false,
                    Text.translatable("omm.search.places").getString(),
                    "Photon"));

            addSearchResult(new SearchResult(
                    SearchResultType.SEARCH,
                    0, 0, false,
                    Text.translatable("omm.search.places").getString(),
                    "Photon"));
        } else displayingSearch = false;

        //set result widgets

        updateResultElements();

    }

    private static void updateResultElements() {
        for (int i = 0; i < MAX_RESULTS; i++) {
            MapScreen.searchResultLayers[i].setResult(i >= searchResults.length ? null : searchResults[i]);
        }
        updateResultElementPositions();
    }

    private static void updateResultElementPositions() {
        for (int i = 0; i < MAX_RESULTS; i++) {
            MapScreen.searchResultLayers[i].setY(23 + ((i - searchScroll) * 20));
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

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollMenu(verticalAmount);
        return false;
    }

    public static void resetScroll() {
        searchScroll = 0;
    }

    public static void scrollMenu(double verticalAmount) {
        if (numResults < maxDisplayedResults) {
            resetScroll();
        }
        else
        if (verticalAmount > 0) {
            searchScroll = Math.max(0, searchScroll - 1);
        } else {
            searchScroll = Math.min(numResults - numDisplayedResults, searchScroll + 1);
        }
        updateResultElementPositions();
    }

    public static void ensureFocusDisplay(int numFocused) {
        numFocused = (numFocused + (numResults + 1)) % (numResults + 1);
        if (numFocused == 0) { //search box now focused
            resetScroll();
            updateResultElementPositions();
            return;
        }
        numFocused--;

        if (searchScroll > numFocused) {
            searchScroll = numFocused;
        }
        if (searchScroll + numDisplayedResults <= numFocused) {
            searchScroll = numFocused - maxDisplayedResults + 1;
        }

        updateResultElementPositions();
    }

}
