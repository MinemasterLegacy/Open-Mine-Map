package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import javax.swing.text.TabExpander;

public class AddRasterScreen extends Screen {
    public AddRasterScreen() {
        super(Text.of(""));
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(new RasterScreen(RasterScreen.returnToHud));
    }
}
