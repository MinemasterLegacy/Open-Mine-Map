package net.mmly.openminemap.raster;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WarningScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.LayoutWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

public class RasterWarningScreen extends WarningScreen {

    protected final Screen parent;

    //TODO translate (may be able to use mc translations)
    protected RasterWarningScreen() {
        super(
                Text.literal("Notice: Using Custom Raster Providers").formatted(Formatting.BOLD),
                Text.literal("OpenStreetMap (and Raster Providers that use their data) have Attribution Guidelines that require applications that use their data to display proper attribution information. Additionally, Raster Providers may have exclusionary terms on what their service can be used for. ")
                        .append(Text.literal("By creating and using a custom Raster Provider, you accept responsibility for ensuring compliance with these guidelines.").formatted(Formatting.BOLD)),
                Text.literal("Don't Show Again"), Text.literal(""));
        parent = MinecraftClient.getInstance().currentScreen;
    }

    @Override
    protected LayoutWidget getLayout() {
        DirectionalLayoutWidget directionalLayoutWidget = DirectionalLayoutWidget.horizontal().spacing(8);
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.PROCEED, (button) -> {
            if (this.checkbox.isChecked()) {
                ConfigOptions._RASTER_WARNING_ACCEPTED.write("true");
                ConfigFile.writeToFile();
            }

            this.client.setScreen(new CreateRasterScreen(null));
        }).build());
        directionalLayoutWidget.add(ButtonWidget.builder(ScreenTexts.BACK, (button) -> this.close()).build());
        return directionalLayoutWidget;
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
    }
}
