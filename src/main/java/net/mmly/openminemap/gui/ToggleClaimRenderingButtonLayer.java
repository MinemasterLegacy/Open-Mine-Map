package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.mmly.openminemap.map.DrawableClaim;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.Notification;

public class ToggleClaimRenderingButtonLayer extends ClickableWidget {

    private int lastCheckedButton = 0;
    private long lastReloaded = -60000;

    public ToggleClaimRenderingButtonLayer(int x, int y) {
        super(x, y, 20,20, Text.of(""));
        setOwnTooltip();
    }

    private void setOwnTooltip() {
        this.setTooltip(Tooltip.of(Text.of(
                Text.translatable("omm.claims.toggle").getString() +
                        "\n" +
                        Text.translatable("omm.claims.reload").getString() +
                        "\n" +
                        (OmmMap.renderClaimsToggle ? Text.translatable("omm.fullscreen.hud-toggle.enabled").getString() : Text.translatable("omm.fullscreen.hud-toggle.disabled").getString())
        )));
    }

    //TODO implement rendering toggle config option
    //TODO add notifications for loading

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (lastCheckedButton == 0) {
            OmmMap.renderClaimsToggle = !OmmMap.renderClaimsToggle;
            setOwnTooltip();
        }
        if (lastCheckedButton == 1) {
            long neededTime = (lastReloaded + 60000);
            if (Util.getMeasuringTimeMs() < neededTime) {
                MapScreen.addNotification(new Notification(Text.literal(
                        Text.translatable("omm.claims.wait-start").getString() +
                            ((int) (neededTime - Util.getMeasuringTimeMs()) / 1000) +
                        Text.translatable("omm.claims.wait-end").getString()
                )));
                return;
            }
            DrawableClaim.Loader.reloadClaimData(true);
            MapScreen.addNotification(new Notification(Text.translatable("omm.claims.reloading")));
            lastReloaded = Util.getMeasuringTimeMs();
        }
    }

    @Override
    protected boolean isValidClickButton(int button) {
        lastCheckedButton = button;
        return button == 0 || button == 1;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
