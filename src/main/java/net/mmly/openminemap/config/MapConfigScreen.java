package net.mmly.openminemap.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.mmly.openminemap.hud.HudMap;

public class MapConfigScreen extends Screen {
    protected MapConfigScreen() {
        super(Text.empty());
    }


    @Override
    protected void renderDarkening(DrawContext context) {}
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {}


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        context.fill(HudMap.hudMapX, HudMap.hudMapY, HudMap.hudMapX2, HudMap.hudMapY2, 0xFFCEE1E4);
    }
}
