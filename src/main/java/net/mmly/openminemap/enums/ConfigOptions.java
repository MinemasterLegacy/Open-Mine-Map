package net.mmly.openminemap.enums;

public enum ConfigOptions { //no underscore for standard config option, one for session variables (Ex: map positioning and zoom), two for development variables
    HUD_MAP_X,
    HUD_MAP_Y,
    HUD_MAP_WIDTH,
    HUD_MAP_HEIGHT,
    HUD_COMPASS_X,
    HUD_COMPASS_Y,
    HUD_COMPASS_WIDTH,
    TILE_MAP_URL,
    ARTIFICIAL_ZOOM,
    SNAP_ANGLE,
    RIGHT_CLICK_MENU_USES,
    REVERSE_SCROLL,

    _HUD_TOGGLE,
    _HUD_LAST_ZOOM,
    _FS_LAST_ZOOM,
    _FS_LAST_X,
    _FS_LAST_Y,

    __DISABLE_WEB_REQUESTS;

    public static String getRawTextOf(ConfigOptions configOption) {
        switch (configOption) {
            case HUD_MAP_X: return "HudMapX";
            case HUD_MAP_Y: return "HudMapY";
            case HUD_MAP_WIDTH: return "HudMapWidth";
            case HUD_MAP_HEIGHT: return "HudMapHeight";
            case HUD_COMPASS_X: return "HudCompassX";
            case HUD_COMPASS_Y: return "HudCompassY";
            case HUD_COMPASS_WIDTH: return "HudCompassWidth";
            case TILE_MAP_URL: return "TileMapUrl";
            case ARTIFICIAL_ZOOM: return "ArtificialZoom";
            case SNAP_ANGLE: return "SnapAngle";
            case RIGHT_CLICK_MENU_USES: return "RightClickMenuUses";
            case REVERSE_SCROLL: return "ReverseScroll";

            case _HUD_TOGGLE: return "§hudtoggle";
            case _HUD_LAST_ZOOM: return "§hudlastzoom";
            case _FS_LAST_ZOOM: return "§fslastzoom";
            case _FS_LAST_X: return "§fslastx";
            case _FS_LAST_Y: return "§fslasty";

            case __DISABLE_WEB_REQUESTS: return "DisableWebRequests";
        }
        return null;
    }

    public static ConfigOptions getOptionOf(String option) {
        switch (option) {
            case "HudMapX": return HUD_MAP_X;
            case "HudMapY": return HUD_MAP_Y;
            case "HudMapWidth": return HUD_MAP_WIDTH;
            case "HudMapHeight": return HUD_MAP_HEIGHT;
            case "HudCompassX": return HUD_COMPASS_X;
            case "HudCompassY": return HUD_COMPASS_Y;
            case "HudCompassWidth": return HUD_COMPASS_WIDTH;
            case "TileMapUrl": return TILE_MAP_URL;
            case "ArtificialZoom": return ARTIFICIAL_ZOOM;
            case "SnapAngle": return SNAP_ANGLE;
            case "RightClickMenuUses": return RIGHT_CLICK_MENU_USES;
            case "ReverseScroll": return REVERSE_SCROLL;

            case "§hudtoggle": return _HUD_TOGGLE;
            case "§hudlastzoom": return _HUD_LAST_ZOOM;
            case "§fslastzoom": return _FS_LAST_ZOOM;
            case "§fslastx": return _FS_LAST_X;
            case "§fslasty": return _FS_LAST_Y;

            case "DisableWebRequests": return __DISABLE_WEB_REQUESTS;
        }
        return null;
    }

    public static int length() {
        return ConfigOptions.values().length;
    }
}
