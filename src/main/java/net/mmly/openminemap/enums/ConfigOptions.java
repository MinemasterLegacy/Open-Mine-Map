package net.mmly.openminemap.enums;

import net.mmly.openminemap.util.ConfigFile;

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
    SHOW_PLAYERS(0, "ShowPlayers", "all", "players"),
    SHOW_DIRECTION_INDICATORS(0, "ShowDirectionIndicators", "all", "directions"),
    ALTITUDE_SHADING(0, "AltitudeShading", "on", "altitude-shading"),
    ZOOM_STRENGTH(0, "ZoomStrength", "0.4", "zoom-strength"),
    HOVER_NAMES(0, "HoverNames", "show", "hover-names"),
    INTERFACE_OPACITY(0, "InterfaceOpacity", "0.5", "Opacity", "Set opacity of the interface backing"),
    PLAYER_SIZE(0, "PlayerSize", "normal", "Player Size", "Set the render size for players"),
    WAYPOINT_SIZE(0, "WaypointSize", "normal", "Waypoint Size", "Set the render size for waypoints"),
    SHOW_CONNECTION_STATUS(0, "ConnectionStatus", "hide", "Connection Status", "Show the map's current connection status with the server"),
    TILE_SCALE(0, "TileScale", "128", "Tile Scale", "Change the render size of map tiles"),
    CLAIMS_RENDERING(0, "ClaimsRendering", "off", "claims"),
    HUDMAP_BORDER(0, "HudmapBorder", "show", "border"),

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

    public String read() {
        return ConfigFile.readParameter(this);
    }

    public void write(String value) {
        ConfigFile.writeParameter(this, value);
    }
}
