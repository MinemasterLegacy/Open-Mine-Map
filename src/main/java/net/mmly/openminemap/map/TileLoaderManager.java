package net.mmly.openminemap.map;

import java.util.LinkedList;

public class TileLoaderManager {

    static LinkedList<LoadableTile> tileLoadQueue = new LinkedList<>();
    static LinkedList<RegisterableTile> tileRegisteringQueue = new LinkedList<>();

}
