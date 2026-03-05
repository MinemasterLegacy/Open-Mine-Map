package net.mmly.openminemap.search;

public enum SearchResultType {
    COORDINATES,
    WAYPOINT,
    PLAYER,
    LOCATION,
    SEARCH,
    SEARCHLOCAL;

    public boolean isSearchType() {
        return this == SEARCH || this == SEARCHLOCAL;
    }
}
