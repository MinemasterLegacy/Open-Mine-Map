package net.mmly.openminemap.map;

import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Requester extends Thread {

    boolean disableWebRequests = Boolean.parseBoolean(ConfigFile.readParameter(ConfigOptions.__DISABLE_WEB_REQUESTS)); //development variable for disabling web requests. If disabled, tiles will ony be loaded from the cache or as error tiles
    int requestAttempts = 2; //how many times a tile will be requested before it is determined to not request it anymore

    ArrayList<int[]> failedRequests = new ArrayList<>();

    int requestCounter = 0;

    public void run() {
        while (true) {
            if (RequestManager.pendingRequest != null) {
                this.tileGetRequest(RequestManager.pendingRequest[0], RequestManager.pendingRequest[1], RequestManager.pendingRequest[2], ConfigFile.readParameter(ConfigOptions.TILE_MAP_URL));
                requestCounter++;
                if (requestCounter >= requestAttempts) {
                    requestCounter = 0;
                    failedRequests.add(new int[]{RequestManager.pendingRequest[0], RequestManager.pendingRequest[1], RequestManager.pendingRequest[2]});
                }
                //System.out.println("Tile request");
            }
            //System.out.println("Request loop");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    BufferedImage tileGetRequest(int x, int y, int zoom, String urlPattern) {
        BufferedImage image = null;
        if (disableWebRequests || TileManager.isTileOutOfBounds(x, y, zoom) || failedRequests.contains(new int[] {x, y, zoom})) return image;

        urlPattern = ((urlPattern.replace("{z}", Integer.toString(zoom)).replace("{x}", Integer.toString(x))).replace("{y}", Integer.toString(y)));
        try {
            URL url = new URI(urlPattern).toURL();
            URLConnection connection = url.openConnection();

            connection.setRequestProperty("User-Agent", "Java/21.0.8 OpenMineMap (contact: minemasterlegacy@gmail.com)");
            connection.setRequestProperty("cache-control", "max-age=7");
            connection.setUseCaches(true);
            connection.setRequestProperty("Retry-After", "3");

            connection.connect();
            //System.out.println(connection.getContent());
            image = ImageIO.read(connection.getInputStream());
            File out = new File(TileManager.getRootFile() + "openminemap/"+zoom+"/"+x+"-"+y+".png");
            ImageIO.write(image, "png", out);
            RequestManager.pendingRequest = null;
            requestCounter = 0;

        } catch (Exception e) {
            System.out.println("Error during url request: " + e);
            e.printStackTrace();
        }

        return image;
    }
}
