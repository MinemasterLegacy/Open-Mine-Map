package net.mmly.openminemap.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.mmly.openminemap.draw.UContext;
import net.mmly.openminemap.enums.ConfigOptions;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.map.DrawableClaim;
import net.mmly.openminemap.maps.OmmMap;
import net.mmly.openminemap.util.ColorUtil;
import net.mmly.openminemap.util.ConfigFile;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import java.util.function.BooleanSupplier;

public class ToggleButtonLayer extends AbstractWidget {

    private int lastCheckedButton = 0;
    private final Type type;

    public ToggleButtonLayer(int x, int y, Type type) {
        super(x, y, 20,20, Component.nullToEmpty(""));
        this.type = type;
        setOwnTooltip();
    }

    public void draw(GuiGraphics context) {
        if (MapScreen.semiTransparentUi) UContext.setTextureAlpha(UContext.SEMI_TRANSPARENT_UI_ALPHA);
        if (!ButtonLayer.texturedButtons) {
            UContext.drawButtonOnWidget(this, false, isHovered());
            UContext.drawTexture(type.getShadowIdentifier(isOn()), getX() + 1, getY() + 1, getWidth(), getHeight());
        }
        UContext.drawTexture(type.getIdentifier(isOn(), isHovered()), getX(), getY(), getWidth(), getHeight());
        UContext.resetTextureAlpha();
    }

    private boolean isOn() {
        if (type == Type.CLAIM_RENDERING) return OmmMap.renderClaimsToggle;
        if (type == Type.TOGGLE_HUDMAP) return HudMap.hudEnabled;
        return false;
    }

    @Override
    protected boolean isValidClickButton(MouseButtonInfo input) {
        return input.button() == 0 || input.button() == 1;
    }

    private void setOwnTooltip() {
        this.setTooltip(Tooltip.create(Component.nullToEmpty(
                Component.translatable(type.topTooltipKey).getString() +
                        "\n" +
                        Component.translatable(type.bottomTooltipKey).getString() +
                        "\n" +
                        (type.isEnabled() ? Component.translatable("omm.fullscreen.hud-toggle.enabled").getString() : Component.translatable("omm.fullscreen.hud-toggle.disabled").getString())
        )));
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        //context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000); //0x00000000
        if (this.isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        if (type == Type.CLAIM_RENDERING) {
            if (click.button() == 0) {
                OmmMap.renderClaimsToggle = !OmmMap.renderClaimsToggle;
                setOwnTooltip();
                ConfigFile.writeParameter(ConfigOptions._CLAIMS_TOGGLE, Boolean.toString(OmmMap.renderClaimsToggle));
            }
            if (click.button() == 1) {
                DrawableClaim.reloadClaimData(true, false, true);
            }
        }

        if (type == Type.TOGGLE_HUDMAP) {
            HudMap.toggleEnabled();
            setOwnTooltip();
        }

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }

    public enum Type {
        CLAIM_RENDERING(
                "omm.claims.toggle",
                "omm.claims.reload",
                () -> OmmMap.renderClaimsToggle,
                true,
                "claims"
        ),
        TOGGLE_HUDMAP(
                "omm.fullscreen.hud-toggle.name",
                "omm.fullscreen.hud-toggle.description",
                () -> HudMap.hudEnabled,
                false,
                "map"
        );

        public final String topTooltipKey;
        public final String bottomTooltipKey;
        private final BooleanSupplier stateDeterminer;
        public final boolean rightClickAllowed;
        private final String fileName;

        private final Identifier onGeneratedIdentifier;
        private final Identifier offGeneratedIdentifier;
        private final Identifier onGeneratedShadowIdentifier;
        private final Identifier offGeneratedShadowIdentifier;

        Type(String topTooltipKey, String bottomTooltipKey, BooleanSupplier stateDeterminer, boolean rightClickAllowed, String fileName) {
            this.topTooltipKey = topTooltipKey;
            this.bottomTooltipKey = bottomTooltipKey;
            this.stateDeterminer = stateDeterminer;
            this.rightClickAllowed = rightClickAllowed;
            this.fileName = fileName;

            onGeneratedIdentifier = Identifier.fromNamespaceAndPath("openminemap", "buttons/vanilla/generated/" + fileName + "on.png");
            offGeneratedIdentifier = Identifier.fromNamespaceAndPath("openminemap", "buttons/vanilla/generated/" + fileName + "off.png");
            onGeneratedShadowIdentifier = ColorUtil.getColoredIdentifier(onGeneratedIdentifier,0x00003e);
            offGeneratedShadowIdentifier = ColorUtil.getColoredIdentifier(offGeneratedIdentifier,0x00003e);

        }

        public Identifier getShadowIdentifier(boolean on) {
            return on ? onGeneratedShadowIdentifier : offGeneratedShadowIdentifier;
        }

        public Identifier getIdentifier(boolean on, boolean highlighted) {
            if (!ButtonLayer.texturedButtons) {
                if (on) return onGeneratedIdentifier;
                return offGeneratedIdentifier;
            }
            return Identifier.fromNamespaceAndPath("openminemap", "buttons/vanilla/" + (highlighted ? "hover" : "default") + "/" + fileName + (on ? "on" : "off") + ".png");
        }

        public boolean isEnabled() {
            return stateDeterminer.getAsBoolean();
        }
    }
}
