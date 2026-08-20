package net.mmly.openminemap.raster;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.WarningScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.util.ConfigFile;

public class RasterWarningScreen extends WarningScreen {

    protected final Screen parent;

    protected RasterWarningScreen() {
        super(
                Component.translatable("omm.raster.warning.title").withStyle(ChatFormatting.BOLD),
                Component.translatable("omm.raster.warning.body")
                        .append(Component.translatable("omm.raster.warning.disclaimer").withStyle(ChatFormatting.BOLD)),
                Component.translatable("multiplayerWarning.check"), Component.literal(""));
        parent = Minecraft.getInstance().gui.screen();
    }

    @Override
    protected Layout addFooterButtons() {
        LinearLayout directionalLayoutWidget = LinearLayout.horizontal().spacing(8);
        directionalLayoutWidget.addChild(Button.builder(CommonComponents.GUI_PROCEED, (button) -> {
            if (this.stopShowing.selected()) {
                ConfigOptions._RASTER_WARNING_ACCEPTED.write("true");
                ConfigFile.writeToFile();
            }

            this.minecraft.gui.setScreen(new CreateRasterScreen(null));
        }).build());
        directionalLayoutWidget.addChild(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose()).build());
        return directionalLayoutWidget;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().gui.setScreen(parent);
    }
}
