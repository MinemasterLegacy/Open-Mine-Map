package net.mmly.openminemap.http;

import net.mmly.openminemap.OpenMineMap;
import net.mmly.openminemap.enums.ConfigOptions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public abstract class Requester<T> extends Thread {

    private final int TIMEOUT_MS = 5000;
    private final int INTERVAL_MS;
    private final boolean PERSISTENT;
    protected T pendingRequest = null;
    private boolean stop = false;
    public final String REQUEST_THREAD_TYPE;

    /// One iteration of the run loop
    public abstract void request();

    public Requester(boolean persistent, int intervalMs, String requestThreadType) {
        this.INTERVAL_MS = intervalMs;
        this.PERSISTENT = persistent;
        this.REQUEST_THREAD_TYPE = requestThreadType;
    }

    public final void setPendingRequest(T pendingRequest) {
        this.pendingRequest = pendingRequest;
    }

    public final void run() {
        while (!stop) {
            if (!ConfigOptions.__DISABLE_WEB_REQUESTS.getAsBoolean()) request();
            if (!PERSISTENT) {
                stopThread();
                continue;
            }
            try {
                Thread.sleep(INTERVAL_MS);
            } catch (InterruptedException e) { //should never happen, Thread.interrupt() is never called
                OpenMineMap.LOGGER.error("HTTP Requester thread was unexpectedly interrupted and will terminate: " + e.getMessage());
                stopThread();
            }
        }
    }

    public final void stopThread() {
        if (ConfigOptions.__LOG_HTTP_REQUESTS.getAsBoolean()) OpenMineMap.LOGGER.info("Shutting down request thread of type \"" + REQUEST_THREAD_TYPE + "\"");
        this.stop = true;
    }
    
    public final InputStream get(String url) {
        if (ConfigOptions.__LOG_HTTP_REQUESTS.getAsBoolean()) OpenMineMap.LOGGER.info("Requesting from " + url);
        try {
            HttpURLConnection connection = getConnection(url);
            if (connection.getResponseCode() != Math.clamp(connection.getResponseCode(), 200, 299)) {
                OpenMineMap.LOGGER.error("Error during url request: Response code " + connection.getResponseCode() + " received.");
                return null;
            }
            return connection.getInputStream();
        } catch (SocketException e) {
            OpenMineMap.LOGGER.warn("Connection to " + url + "timed out (after " + TIMEOUT_MS + "ms)");
            return null;
        } catch (Exception e) {
            OpenMineMap.LOGGER.error("Error during url request: " + e.getMessage());
            return null;
        }
    }

    private @NotNull HttpURLConnection getConnection(String url) throws URISyntaxException, IOException {
        URL url1 = new URI(url).toURL();
        HttpURLConnection connection = (HttpURLConnection) url1.openConnection();

        connection.setRequestProperty("User-Agent", "Java/21.0.8 OpenMineMap (contact: minemasterlegacy@gmail.com)");
        connection.setRequestProperty("cache-control", "max-age=7");
        connection.setUseCaches(true);
        connection.setRequestProperty("Retry-After", "3");
        connection.setConnectTimeout(TIMEOUT_MS);

        connection.connect();
        return connection;
    }
}
