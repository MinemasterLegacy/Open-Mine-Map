package net.mmly.openminemap.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.network.NetworkState;
import net.mmly.openminemap.util.ConfigFile;

public class NetworkStatusLayer extends AbstractWidget {
    public NetworkStatusLayer(int x, int y) {
        super(x, y, 26, 26, Component.nullToEmpty(""));
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (this.isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);
    }

    public boolean shouldBeVisible() {
        return !Minecraft.getInstance().isLocalServer() && ConfigOptions.SHOW_CONNECTION_STATUS.getAsBooleanFromValues(ConfigOptions.Values.SHOW_HIDE);
    }

    protected void drawWidget(GuiGraphics context) {
        if (!shouldBeVisible()) return;
        int winWidth = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        UContext.fillZone(winWidth - 26, 0, 26, 26, MapScreen.backingColor);
        if (MapScreen.semiTransparentUi) UContext.setTextureAlpha(UContext.SEMI_TRANSPARENT_UI_ALPHA);
        if (isHovered()) {
            UContext.drawTexture(NetworkState.getNetworkState().selectionIdentifier, winWidth - 24, 2, 22, 22, 22, 22);
            setTooltip(Tooltip.create(Component.translatable(NetworkState.getNetworkState().translationKey)));
        } else {
            setTooltip(null);
        }
        UContext.drawTexture(NetworkState.getNetworkState().identifier, winWidth - 23, 3, 20, 20, 20, 20);
        UContext.resetTextureAlpha();
    }

    /*
    @Override
    public void onClick(double mouseX, double mouseY) {
        switch (PlayerInfoPacketCodec.currentNetworkState) {
            case CONNECTED -> PlayerInfoPacketCodec.currentNetworkState = NetworkState.BAD_CONNECTION;
            case BAD_CONNECTION -> PlayerInfoPacketCodec.currentNetworkState = NetworkState.NOT_CONNECTED;
            case NOT_CONNECTED -> PlayerInfoPacketCodec.currentNetworkState = NetworkState.CONNECTED;
        }
        super.onClick(mouseX, mouseY);
    }
     */

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }
}
