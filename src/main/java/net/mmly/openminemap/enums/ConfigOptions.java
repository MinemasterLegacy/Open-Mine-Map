package net.mmly.openminemap.enums;

import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.util.ColorUtil;
import net.mmly.openminemap.util.ConfigFile;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public enum ConfigOptions { //no underscore for standard config option, one for session variables (Ex: map positioning and zoom), two for development variables
    HUD_MAP_X(0, "HudMapX", "10"),
    HUD_MAP_Y(0, "HudMapY", "10"),
    HUD_MAP_WIDTH(0, "HudMapWidth", "144"),
    HUD_MAP_HEIGHT(0, "HudMapHeight", "81"),
    HUD_COMPASS_X(0, "HudCompassX", "10"),
    HUD_COMPASS_Y(0, "HudCompassY", "96"),
    HUD_COMPASS_WIDTH(0, "HudCompassWidth", "144"),
    TILE_MAP_URL(0, "TileMapUrl", "OpenStreetMap", "", "omm.config.tooltip.tile-source"),
    ARTIFICIAL_ZOOM(0, "ArtificialZoom", "off", "artificial-zoom"),
    SNAP_ANGLE(0, "SnapAngle", "", "snap-angle"),
    RIGHT_CLICK_MENU_USES(0, "RightClickMenuUses", "/tpll", "rcm-uses"),
    REVERSE_SCROLL(0, "ReverseScroll", "off", "reverse-scroll"),
    SHOW_PLAYERS(0, "ShowPlayers2", "all", "players"),
    SHOW_DIRECTION_INDICATORS(0, "ShowDirectionIndicators2", "all", "directions"),
    ALTITUDE_SHADING(0, "AltitudeShading", "on", "altitude-shading"),
    ZOOM_STRENGTH(0, "ZoomStrength", "0.40", "zoom-strength"),
    HOVER_NAMES(0, "HoverNames", "show", "hover-names"),
    INTERFACE_OPACITY(0, "InterfaceOpacity", "0.50", "interface-opacity"),
    PLAYER_SIZE(0, "PlayerSize", "normal", "player-size"),
    WAYPOINT_SIZE(0, "WaypointSize", "normal", "waypoint-size"),
    SHOW_CONNECTION_STATUS(0, "ConnectionStatus", "hide", "connection-status"),
    TILE_SCALE(0, "TileScale", "128", "tile-scale"),
    CLAIMS_RENDERING(0, "ClaimsRendering", "off", "claims"),
    HUDMAP_BORDER(0, "HudmapBorder", "show", "border"),
    COMPASS(0, "ShowCompass", "show", "compass"),
    TEXT_COLOR(0, "TextColor", "#FFFFFF", "text-color"),

    _CLAIMS_TOGGLE(1, "§claimstoggle", "true"),
    _HUD_TOGGLE(1, "§hudtoggle", "true"),
    _HUD_ENABLED(1, "§hudenabled", "true"),
    _HUD_LAST_ZOOM(1, "§hudlastzoom", "0"),
    _FS_LAST_ZOOM(1, "§fslastzoom", "0"),
    _FS_LAST_X(1, "§fslastx", "64"),
    _FS_LAST_Y(1, "§fslasty", "64"),
    _FS_LAST_TILE_SIZE(1, "§fslasttilesize", "128"),

    __SHOW_DEVELOPER_OPTIONS(2, "ShowDeveloperOptions", "false"),
    __DISABLE_WEB_REQUESTS(2, "DisableWebRequests", "false", "DisableWebRequests", ""),
    __SHOW_MEMORY_CACHE_SIZE(2, "ShowMemoryCacheSize", "false", "ShowMemoryCacheSize", "");

    private final String defaultValue;
    private final String rawText;
    public final String message;
    public final String tooltip;
    private final int type; //0 = normal, 1 = saved runtime variable, 2 = developer
    public static final String[] defaultValues = calcDefaults();

    private static String[] calcDefaults() {
        String[] defaults = new String[length()];
        for (int i = 0; i < length(); i++) {
            defaults[i] = values()[i].defaultValue;
        }
        return defaults;
    }

    private static final String messageKeyStart = "omm.config.option.";
    private static final String tooltipKeyStart = "omm.config.tooltip.";

    ConfigOptions(int type, String rawTextOf, String defaultValue, String subKey) {
        this(type, rawTextOf, defaultValue, messageKeyStart + subKey, tooltipKeyStart + subKey);
    }

    ConfigOptions(int type, String rawTextOf, String defaultValue) {
        this(type, rawTextOf, defaultValue, "", "");
    }

    ConfigOptions(int type, String rawTextOf, String defaultValue, String message, String tooltip) {
        this.type = type;
        this.defaultValue = defaultValue;
        this.rawText = rawTextOf;
        this.message = message;
        this.tooltip = tooltip;
    }

    public static String getRawTextOf(ConfigOptions configOption) {
        return configOption == null ? null : configOption.rawText;
    }

    public static ConfigOptions getOptionOf(String option) {
        for (ConfigOptions enu : ConfigOptions.values()) {
            if (enu.rawText.equals(option)) return enu;
        }
        return null;
    }

    public static int length() {
        return ConfigOptions.values().length;
    }

    public void write(String value) {
        ConfigFile.writeParameter(this, value);
    }

    public int getAsInt() {
        try {
            return Integer.parseInt(ConfigFile.readOption(this));
        } catch (NumberFormatException e) {
            OpenMineMap.LOGGER.warn("Failed to parse config option " + this + " with value " + ConfigFile.readOption(this) + "to integer.");
            return Integer.parseInt(ConfigFile.readDefaultParameter(this));
        }
    }

    public double getAsDouble() {
        try {
            return Double.parseDouble(ConfigFile.readOption(this));
        } catch (NumberFormatException | NullPointerException e) {
            OpenMineMap.LOGGER.warn("Failed to parse config option " + this + " with value " + ConfigFile.readOption(this) + "to double.");
            return Double.parseDouble(ConfigFile.readDefaultParameter(this));
        }
    }

    public boolean getAsBoolean() {
        return Boolean.parseBoolean(ConfigFile.readOption(this));
    }

    public String getAsString() {
        return ConfigFile.readOption(this);
    }

    public boolean getAsBooleanFromValues(List<String> values) {
        String value = ConfigFile.readOption(this);
        if (values.getFirst().equalsIgnoreCase(value)) return true;
        else if (values.get(1).equalsIgnoreCase(value)) return false;
        else {
            OpenMineMap.LOGGER.warn("Config option " + this + " with value " + ConfigFile.readOption(this) + " could not be parsed to boolean from values list.");
            return false;
        }
    }

    public String getAsStringFromValues(List<String> values) {
        String value = ConfigFile.readOption(this);
        for (String string : values) {
            if (string.equalsIgnoreCase(value)) return value;
        }

        OpenMineMap.LOGGER.warn("Config option " + this + " with value " + ConfigFile.readOption(this) + " was not contained in values list.");
        return ConfigFile.readDefaultParameter(this);
    }

    public static class Values {
        public static final List<String> ON_OFF = Arrays.stream(new String[] {"On", "Off"}).toList();
        public static final List<String> SHOW_HIDE = Arrays.stream(new String[] {"Show", "Hide"}).toList();
        public static final List<String> TRUE_FALSE = Arrays.stream(new String[] {"true", "false"}).toList();
        public static final List<String> VISIBILITY = Arrays.stream(new String[] {"None", "Self", "Local", "All"}).toList();
        public static final List<String> SIZES = Arrays.stream(new String[] {"Small", "Normal", "Large"}).toList();
        public static final List<String> TP_COMMANDS = Arrays.stream(new String[] {"/tpll", "/tp"}).toList();
        public static final List<String> ZOOM_STRENGTHS = Arrays.stream(range(0.05f, 2, 0.05f, 2)).toList();
        public static final List<String> DECIMAL_PERCENT = Arrays.stream(range(0, 1.01f, 0.05f, 2)).toList();
        public static final List<String> TILE_SCALES = Arrays.stream(range(64, 256, 8, 0)).toList();
        public static final List<String> COLORS = Arrays.stream(genColorRange()).toList();

        private static String[] range(float start, float end, float step, int roundToPlace) {
            String format = "%." + roundToPlace + "f";
            String[] values = new String[(int) ((end - start) / step) + 1];
            int index = 0;
            for (float i = start; i <= end; i += step) {
                values[index] = String.format(Locale.US, format, i);
                index++;
            }
            return values;
        }

        private static String[] genColorRange() {
            String[] choices = new String[22];
            choices[0] = "#FFFFFF";
            choices[21] = "Rainbow";
            int index = 0;
            for (int i = 0; i < 360; i += 18) {
                index += 1;
                choices[index] = "#" + Integer.toHexString(ColorUtil.hsl(255, i, 0.95f, 0.75f)).substring(2, 8);
            }
            return choices;
        }
    }

}
