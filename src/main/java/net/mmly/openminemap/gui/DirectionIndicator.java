package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.PlayerAttributes;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class DirectionIndicator extends ClickableWidget {

    public Identifier textureId = Identifier.of("openminemap", "rotatabledirectionindicator.png");
    BufferedImage baseTexture;
    public boolean loadSuccess;

    public DirectionIndicator(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
        getTextureFromResources();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    protected void render(DrawContext context) {
        //if (updateDynamicTexture() || !loadSuccess) return;
        //context.drawTexture(textureId, FullscreenMapScreen.playerMapX - 8, FullscreenMapScreen.playerMapY - 8, 0, 0, 24, 24, 24, 24);
    }

    private void getTextureFromResources() {
        try {
            InputStream stream = MinecraftClient.getInstance().getResourceManager().getResource(textureId).get().getInputStream();
            baseTexture = ImageIO.read(stream);
            loadSuccess = true;
        } catch (IOException e) {
            loadSuccess = false;
        }
    }

    public boolean updateDynamicTexture() { //true for no error, false for error
        try {
            MinecraftClient.getInstance().getTextureManager().destroyTexture(textureId);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            if (Double.isNaN(PlayerAttributes.geoYaw)) return false;
            ImageIO.write(getRotatedTexture(PlayerAttributes.geoYaw), "png", os);
            InputStream is = new ByteArrayInputStream(os.toByteArray());
            NativeImage nImage = NativeImage.read(is);
            MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, new NativeImageBackedTexture(nImage));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private BufferedImage getRotatedTexture(double degrees) {
        //Rotation code source: https://www.geeksforgeeks.org/java/java-program-to-rotate-an-image/
        System.out.println(degrees);
        // Getting Dimensions of image
        int width = 142; //img.getWidth();
        int height = 142; //img.getHeight();

        // Creating a new buffered image
        BufferedImage newImage = new BufferedImage(width, height, baseTexture.getType());

        // creating Graphics in buffered image
        Graphics2D g2 = newImage.createGraphics();

        // Rotating image by degrees using toradians()
        // method
        // and setting new dimension t it
        g2.rotate(Math.toRadians(degrees), (double) width / 2, (double) height / 2);
        g2.drawImage(baseTexture, null, 0, 0);
        // Return rotated buffer image
        return newImage;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

}
