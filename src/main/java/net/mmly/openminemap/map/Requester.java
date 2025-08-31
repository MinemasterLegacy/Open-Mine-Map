package net.mmly.openminemap.map;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Requester extends Thread {

    boolean disableWebRequests = false; //development variable for disabling web requests. If disabled, tiles will ony be loaded from the cache or as error tiles
    int requestAttempts = 2; //how many times a tile will be requested before it is determined to not request it anymore

    ArrayList<int[]> failedRequests = new ArrayList<int[]>();

    int requestCounter = 0;

    public void run() {
        while (true) {
            if (RequestManager.pendingRequest != null) {
                this.tileGetRequest(RequestManager.pendingRequest[0], RequestManager.pendingRequest[1], RequestManager.pendingRequest[2], "");
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

    BufferedImage tileGetRequest(int x, int y, int zoom, String type) {
        BufferedImage image = null;
        if (disableWebRequests || TileManager.isTileOutOfBounds(x, y, zoom) || failedRequests.contains(new int[] {x, y, zoom})) return image;

        try {
            URL url = new URI("http://tile.openstreetmap.org/"+zoom+"/"+x+"/"+y+".png").toURL();
            URLConnection connection = url.openConnection();

            connection.setRequestProperty("User-Agent", "Java/21.0.8 McOpenMineMap/0.0");
            connection.setRequestProperty("cache-control", "max-age=7");
            connection.setUseCaches(true);
            connection.setRequestProperty("Retry-After", "3");

            connection.connect();
            System.out.println(connection.getContent());
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
