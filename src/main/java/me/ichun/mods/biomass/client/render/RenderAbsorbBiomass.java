package me.ichun.mods.biomass.client.render;

import me.ichun.mods.biomass.client.entity.EntityAbsorbBiomass;
import me.ichun.mods.biomass.common.biomass.BiomassData;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public class RenderAbsorbBiomass extends Render<EntityAbsorbBiomass>
{
    public RenderAbsorbBiomass(RenderManager manager)
    {
        super(manager);
    }

    @Override
    public void doRender(EntityAbsorbBiomass ent, double x, double y, double z, float entityYaw, float partialTicks)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        if(!ent.model.killedCubes.isEmpty())
        {
            GlStateManager.pushMatrix();
            double offX = (ent.killed.posX) - (ent.prevPosX + (ent.posX - ent.prevPosX) * partialTicks);
            double offY = (ent.killed.posY) - (ent.prevPosY + (ent.posY - ent.prevPosY) * partialTicks);
            double offZ = (ent.killed.posZ) - (ent.prevPosZ + (ent.posZ - ent.prevPosZ) * partialTicks);
            GlStateManager.translate(offX, offY, offZ);
            GlStateManager.rotate(180F - ent.killed.renderYawOffset, 0F, 1F, 0F);
            GlStateManager.scale(-1.0F, -1.0F, 1.0F);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            float skinProg = MathHelper.clamp((ent.age + partialTicks) / 5F, 0F, 1F);

            if(ent.prevScaleX == -1) //setting up
            {
                Render rend = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(ent.killed);

                if(rend instanceof RenderLivingBase)
                {
                    FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
                    FloatBuffer buffer1 = GLAllocation.createDirectFloatBuffer(16);

                    GlStateManager.pushMatrix();
                    GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, buffer);
                    ObfHelper.invokePreRenderCallback((RenderLivingBase)rend, rend.getClass(), ent.killed, iChunUtil.eventHandlerClient.renderTick);
                    GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, buffer1);
                    GlStateManager.popMatrix();

                    ent.prevScaleX = buffer1.get(0) / buffer.get(0);
                    ent.prevScaleY = buffer1.get(5) / buffer.get(5);
                    ent.prevScaleZ = buffer1.get(8) / buffer.get(8);
                }
                else
                {
                    ent.prevScaleX = ent.prevScaleY = ent.prevScaleZ = 1F;
                }
            }

            //Render killed
            GlStateManager.scale(ent.prevScaleX, ent.prevScaleY, ent.prevScaleZ);
            GlStateManager.translate(0F, -1.5F, 0F);
            GlStateManager.color(1F, 1F, 1F, 1F);

            if(skinProg < 1F && ent.killedTexture != null)
            {
                bindTexture(ent.killedTexture);
                ent.model.renderKilled(0.0625F);
                GlStateManager.color(1F, 1F, 1F, skinProg);
            }

            bindTexture(BiomassData.BIOMASS_SKIN);
            ent.model.renderKilled(0.0625F);
            //end render killed

            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        bindTexture(BiomassData.BIOMASS_SKIN);
        ent.model.renderArms(0.0625F, isFirstPerson(ent.absorber));

        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }

    public static boolean isFirstPerson(EntityLivingBase ent)
    {
        return (ent == Minecraft.getMinecraft().getRenderViewEntity() &&
                Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 &&
                !(
                        (Minecraft.getMinecraft().currentScreen instanceof GuiInventory || Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) &&
                                Minecraft.getMinecraft().getRenderManager().playerViewY == 180.0F
                )
        );
    }

    @Override
    public ResourceLocation getEntityTexture(EntityAbsorbBiomass entity)
    {
        return BiomassData.BIOMASS_SKIN;
    }

    public static class RenderFactory implements IRenderFactory<EntityAbsorbBiomass>
    {
        @Override
        public Render<EntityAbsorbBiomass> createRenderFor(RenderManager manager)
        {
            return new RenderAbsorbBiomass(manager);
        }
    }
}
