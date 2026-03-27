package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.map.DrawableClaim;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ConfigFile;
import net.mmly.openminemap.util.Notification;

public class ToggleClaimRenderingButtonLayer extends ClickableWidget {

    private int lastCheckedButton = 0;

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

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (lastCheckedButton == 0) {
            OmmMap.renderClaimsToggle = !OmmMap.renderClaimsToggle;
            setOwnTooltip();
            ConfigFile.writeParameter(ConfigOptions._CLAIMS_TOGGLE, Boolean.toString(OmmMap.renderClaimsToggle));
        }
        if (lastCheckedButton == 1) {
            DrawableClaim.reloadClaimData(true, false, true);
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
