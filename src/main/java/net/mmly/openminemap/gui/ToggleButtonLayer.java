package net.mmly.openminemap.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.DrawableClaim;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ConfigFile;

import java.util.function.BooleanSupplier;

public class ToggleButtonLayer extends ClickableWidget {

    private int lastCheckedButton = 0;
    private final Type type;

    public ToggleButtonLayer(int x, int y, Type type) {
        super(x, y, 20,20, Text.of(""));
        this.type = type;
        setOwnTooltip();
    }

    private void setOwnTooltip() {
        this.setTooltip(Tooltip.of(Text.of(
                Text.translatable(type.topTooltipKey).getString() +
                        "\n" +
                        Text.translatable(type.bottomTooltipKey).getString() +
                        "\n" +
                        (type.isEnabled() ? Text.translatable("omm.fullscreen.hud-toggle.enabled").getString() : Text.translatable("omm.fullscreen.hud-toggle.disabled").getString())
        )));
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000); //0x00000000
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (type == Type.CLAIM_RENDERING) {
            if (lastCheckedButton == 0) {
                OmmMap.renderClaimsToggle = !OmmMap.renderClaimsToggle;
                setOwnTooltip();
                ConfigFile.writeParameter(ConfigOptions._CLAIMS_TOGGLE, Boolean.toString(OmmMap.renderClaimsToggle));
            }
            if (lastCheckedButton == 1) {
                DrawableClaim.reloadClaimData(true, false, true);
            }
        }

        if (type == Type.TOGGLE_HUDMAP) {
            HudMap.toggleEnabled();
            setOwnTooltip();
        }

    }

    @Override
    protected boolean isValidClickButton(int button) {
        lastCheckedButton = button;
        return button == 0 || (button == 1 && type.rightClickAllowed);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }

    public enum Type {
        CLAIM_RENDERING(
                "omm.claims.toggle",
                "omm.claims.reload",
                () -> OmmMap.renderClaimsToggle,
                true
        ),
        TOGGLE_HUDMAP(
                "omm.fullscreen.hud-toggle.name",
                "omm.fullscreen.hud-toggle.description",
                () -> HudMap.hudEnabled,
                false
        );

        public final String topTooltipKey;
        public final String bottomTooltipKey;
        private final BooleanSupplier stateDeterminer;
        public final boolean rightClickAllowed;

        Type(String topTooltipKey, String bottomTooltipKey, BooleanSupplier stateDeterminer, boolean rightClickAllowed) {
            this.topTooltipKey = topTooltipKey;
            this.bottomTooltipKey = bottomTooltipKey;
            this.stateDeterminer = stateDeterminer;
            this.rightClickAllowed = rightClickAllowed;
        }

        public boolean isEnabled() {
            return stateDeterminer.getAsBoolean();
        }
    }
}
