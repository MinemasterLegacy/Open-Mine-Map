package net.mmly.openminemap.waypoint;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WaypointIconSelectButton extends ClickableWidget {

    String type;
    int typeId;

    public WaypointIconSelectButton(int type) {
        super(0, 0, 7, 11, Text.of(""));
        if (type == -1) this.type = "left";
        if (type == 1) this.type = "right";
        typeId = type;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (isHovered()) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("openminemap", "arrowselect/"+type+"selected.png"), getX(), getY(), 0, 0, width, height, 7, 11);
        } else {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of("openminemap", "arrowselect/"+type+".png"), getX(), getY(), 0, 0, width, height, 7, 11);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        WaypointScreen.instance.styleSelection = WaypointStyle.getByOrdinal(WaypointScreen.instance.styleSelection.ordinal() + typeId);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}