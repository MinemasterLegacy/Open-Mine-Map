package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.cursor.StandardCursors;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.network.NetworkState;
import net.mmly.openminemap.util.ConfigFile;

public class NetworkStatusLayer extends ClickableWidget {
    public NetworkStatusLayer(int x, int y) {
        super(x, y, 26, 26, Text.of(""));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.isHovered()) context.setCursor(StandardCursors.POINTING_HAND);
    }

    public boolean shouldBeVisible() {
        return !MinecraftClient.getInstance().isInSingleplayer() && ConfigOptions.SHOW_CONNECTION_STATUS.getAsBooleanFromValues(ConfigOptions.Values.SHOW_HIDE);
    }

    protected void drawWidget(DrawContext context) {
        if (!shouldBeVisible()) return;
        int winWidth = MinecraftClient.getInstance().getWindow().getScaledWidth();
        UContext.fillZone(winWidth - 26, 0, 26, 26, MapScreen.backingColor);
        if (MapScreen.semiTransparentUi) UContext.setTextureAlpha(UContext.SEMI_TRANSPARENT_UI_ALPHA);
        if (isHovered()) {
            UContext.drawTexture(NetworkState.getNetworkState().selectionIdentifier, winWidth - 24, 2, 22, 22, 22, 22);
            setTooltip(Tooltip.of(Text.translatable(NetworkState.getNetworkState().translationKey)));
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
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
