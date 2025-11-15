package net.mmly.openminemap.gui;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.mmly.openminemap.enums.WebIcon;
import net.mmly.openminemap.map.TileManager;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

public class WebAppSelectLayer extends ClickableWidget {

    private static HashMap<WebIcon, Identifier> webIcons = new HashMap<>();
    private static HashMap<WebIcon, Identifier> iconSelections = new HashMap<>();
    private int selection = 0;

    public WebAppSelectLayer() {
        super(0, 0, 14, 98, Text.of(""));

        webIcons.put(WebIcon.GOOGLE_MAPS, Identifier.of("openminemap", "webicons/icons/gm.png"));
        webIcons.put(WebIcon.GOOGLE_EARTH, Identifier.of("openminemap", "webicons/icons/ge.png"));
        webIcons.put(WebIcon.GOOGLE_EARTH_PRO, Identifier.of("openminemap", "webicons/icons/gep.png"));
        webIcons.put(WebIcon.OPEN_STREET_MAP, Identifier.of("openminemap", "webicons/icons/osm.png"));
        webIcons.put(WebIcon.BING_MAPS, Identifier.of("openminemap", "webicons/icons/bm.png"));
        webIcons.put(WebIcon.APPLE_MAPS, Identifier.of("openminemap", "webicons/icons/am.png"));

        iconSelections.put(WebIcon.GOOGLE_MAPS, Identifier.of("openminemap", "webicons/selections/gm.png"));
        iconSelections.put(WebIcon.GOOGLE_EARTH, Identifier.of("openminemap", "webicons/selections/ge.png"));
        iconSelections.put(WebIcon.GOOGLE_EARTH_PRO, Identifier.of("openminemap", "webicons/selections/gep.png"));
        iconSelections.put(WebIcon.OPEN_STREET_MAP, Identifier.of("openminemap", "webicons/selections/osm.png"));
        iconSelections.put(WebIcon.BING_MAPS, Identifier.of("openminemap", "webicons/selections/bm.png"));
        iconSelections.put(WebIcon.APPLE_MAPS, Identifier.of("openminemap", "webicons/selections/am.png"));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        selection = (int) Math.floor((double) (mouseY - getY() - 1) / 16);
        if (selection > 5 || selection < 0 || mouseX < getX() || mouseX > getX() + getWidth() - 1) {
            selection = -1;
            setTooltip(null);
        } else {
            setTooltip(WebIcon.getTooltipUsingId(selection));
        }
    }

    protected void drawWidget(DrawContext context) {
        if (!RightClickMenu.selectingSite) return;

        context.fill(getX(), getY(), getX() + getWidth(), getY()+getHeight(), 0x88000000);

        if (selection != -1) context.drawTexture(RenderPipelines.GUI_TEXTURED, iconSelections.get(WebIcon.getUsingId(selection)), getX() + 2 - 1, getY() + 2 + (selection * 16) - 1, 0, 0, 12, 16, 12, 16);

        for (int i = 0; i < webIcons.size(); i++) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, webIcons.get(WebIcon.getUsingId(i)), getX() + 2, getY() + 2 + (i * 16), 0, 0, 10, 14, 10, 14);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        float lat = RightClickMenu.savedMouseLat;
        float lon = RightClickMenu.savedMouseLong;
        int zoom = FullscreenMapScreen.zoomLevel;
        switch (selection) {
            case 0: {
                openUrl("https://google.com/maps/@"+lat+","+lon+","+Math.max(2, zoom)+"z", false);
                break;
            } case 1: {
                openUrl("https://earth.google.com/web/search/"+lat+"+"+lon, false);
                break;
            } case 2: {
                openUrl(lat+", "+lon+" (.kml file)", true);
                break;
            } case 3: {
                openUrl("https://openstreetmap.org/#map="+zoom+"/"+lat+"/"+lon, false);
                break;
            } case 4: {
                openUrl("https://bing.com/maps?cp="+lat+"~"+lon+"&lvl="+zoom, false);
                break;
            } case 5: {
                openUrl("https://maps.apple.com/frame?center="+lat+"%2C"+lon, false);
                break;
            }
        }
        RightClickMenu.selectingSite = false;
    }

    private static void openUrl(String url, boolean isGep) {
        MinecraftClient.getInstance().setScreen(
            new ConfirmLinkScreen(new BooleanConsumer() {
                @Override
                public void accept(boolean b) {
                    if(b) {
                        if (isGep) openInGep();
                        else Util.getOperatingSystem().open(url);
                    }
                    MinecraftClient.getInstance().setScreen(new FullscreenMapScreen());
                }
            }, url, true)

        );
    }

    private static void openInGep() {
        //Util.getOperatingSystem().
        File file = new File(TileManager.getRootFile() + "openminemap/location.kml");
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "utf-8"))) {
                writer.write(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n" +
                                "<Document>\n" +
                                "\t<name>OpenMineMap Location</name>\n" +
                                "\t<Placemark>\n" +
                                "\t\t<name>"+RightClickMenu.savedMouseLat+", "+RightClickMenu.savedMouseLong+"</name>\n" +
                                "\t\t<LookAt>\n" +
                                "\t\t\t<longitude>"+RightClickMenu.savedMouseLong+"</longitude>\n" +
                                "\t\t\t<latitude>"+RightClickMenu.savedMouseLat+"</latitude>\n" +
                                "\t\t\t<altitude>0</altitude>\n" +
                                "\t\t\t<heading>-11.42103893546798</heading>\n" +
                                "\t\t\t<tilt>0</tilt>\n" +
                                "\t\t\t<range>"+zoomToMetersAbove(FullscreenMapScreen.zoomLevel)+"</range>\n" +
                                "\t\t\t<gx:altitudeMode>relativeToSeaFloor</gx:altitudeMode>\n" +
                                "\t\t</LookAt>\n" +
                                "\t\t<Point>\n" +
                                "\t\t\t<gx:drawOrder>1</gx:drawOrder>\n" +
                                "\t\t\t<coordinates>"+RightClickMenu.savedMouseLong+","+RightClickMenu.savedMouseLat+",0</coordinates>\n" +
                                "\t\t</Point>\n" +
                                "\t</Placemark>\n" +
                                "</Document>\n" +
                                "</kml>"
                );
        } catch (IOException e) {
            return;
        }
        System.out.println(file.exists());
        Util.getOperatingSystem().open(file);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    private static String zoomToMetersAbove(int z) {
        return String.format("%.7f",
                84412457.8 * Math.pow(0.5, z)
        );
    }
}
