package net.mmly.openminemap.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.mmly.openminemap.draw.UContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DrawContext.class)
public class ExampleMixin {
    @Shadow
    @Final
    private GuiRenderState state;

    @Inject(at = @At("HEAD"), method = "getMatrices")
    public void init(CallbackInfoReturnable<MatrixStack> cir) {
        // This code is injected into the start of MinecraftServer.loadWorld()V
        //UContext.capturedVertexProvider = this.vertexConsumers;
    }
}