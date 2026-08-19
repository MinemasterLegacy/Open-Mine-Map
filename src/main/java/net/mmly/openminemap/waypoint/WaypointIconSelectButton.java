package net.mmly.openminemap.waypoint;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class WaypointIconSelectButton extends AbstractWidget {

    String type;
    int typeId;

    public WaypointIconSelectButton(int type) {
        super(0, 0, 7, 11, Component.nullToEmpty(""));
        if (type == -1) this.type = "left";
        if (type == 1) this.type = "right";
        typeId = type;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        if (isHovered()) {
            context.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("openminemap", "arrowselect/"+type+"selected.png"), getX(), getY(), 0, 0, width, height, 7, 11);
        } else {
            context.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath("openminemap", "arrowselect/"+type+".png"), getX(), getY(), 0, 0, width, height, 7, 11);
        }
        if (this.isHovered()) context.requestCursor(CursorTypes.POINTING_HAND);
    }

    public void onClick(double mouseX, double mouseY) {
        WaypointScreen.instance.styleSelection = WaypointStyle.getByOrdinal(WaypointScreen.instance.styleSelection.ordinal() + typeId);
    }

    @Override
    public void onClick(MouseButtonEvent click, boolean doubled) {
        onClick(click.x(), click.y());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput builder) {

    }
}