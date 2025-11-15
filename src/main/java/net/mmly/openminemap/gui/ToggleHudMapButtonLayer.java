package net.mmly.openminemap.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.mmly.openminemap.hud.HudMap;
import net.mmly.openminemap.util.UnitConvert;

public class ToggleHudMapButtonLayer extends ClickableWidget {

    public ToggleHudMapButtonLayer(int x, int y) {
        super(x, y, 20,20, Text.of(""));
        setOwnTooltip();
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(getX(), getY(), getX() + this.width, getY() + this.height, 0x00000000); //0x00000000
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    @Override
    public void onClick(double mouseX, double mouseY) {
        HudMap.toggleEnabled();
        setOwnTooltip();
    }

    private void setOwnTooltip() {
        this.setTooltip(Tooltip.of(Text.of("Toggle Hud Elements\nDominant over the toggle keybind\nCurrently "+(Boolean.toString(HudMap.hudEnabled).equals("true") ? "Enabled" : "Disabled"))));
    }

    public boolean isHovered() {
        return this.isMouseOver(UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getX()), UnitConvert.pixelToScaledCoords((float) MinecraftClient.getInstance().mouse.getY()));
    }
}
