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

    protected RasterWarningScreen() {
        super(
                Text.translatable("omm.raster.warning.title").formatted(Formatting.BOLD),
                Text.translatable("omm.raster.warning.body")
                        .append(Text.translatable("omm.raster.warning.disclaimer").formatted(Formatting.BOLD)),
                Text.translatable("multiplayerWarning.check"), Text.literal(""));
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
