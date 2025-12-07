package net.mmly.openminemap.waypoint;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WaypointScreen extends Screen {

    private ColorSliderWidget hueSlider;
    private ColorSliderWidget saturationSlider;
    private ColorSliderWidget valueSlider;

    public WaypointScreen() {
        super(Text.of("OpenMineMap Waypoints"));
    }

    @Override
    protected void init() {
        super.init();

        hueSlider = new ColorSliderWidget(20, 20, 120, 20, ColorSliderType.HUE);
        saturationSlider = new ColorSliderWidget(20, 60, 120, 20, ColorSliderType.SATURATION);
        valueSlider = new ColorSliderWidget(20, 100, 120, 20, ColorSliderType.VALUE);

        this.addDrawableChild(hueSlider);
        this.addDrawableChild(saturationSlider);
        this.addDrawableChild(valueSlider);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

}
