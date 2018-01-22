package me.ichun.mods.biomass.client.biomass;

import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.biomass.common.biomass.BiomassData;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.util.HashSet;

public class BiomassDataClient extends BiomassData
{
    public static final HashSet<RenderGameOverlayEvent.ElementType> PRE_RENDER_EVENTS = new HashSet<RenderGameOverlayEvent.ElementType>(){{
        add(RenderGameOverlayEvent.ElementType.HEALTH);
        add(RenderGameOverlayEvent.ElementType.ARMOR);
        add(RenderGameOverlayEvent.ElementType.FOOD);
        add(RenderGameOverlayEvent.ElementType.HEALTHMOUNT);
        add(RenderGameOverlayEvent.ElementType.AIR);
        add(RenderGameOverlayEvent.ElementType.JUMPBAR);
        add(RenderGameOverlayEvent.ElementType.EXPERIENCE);
    }};

    public static final float XP_BAR_OFFSET = 6F;
    public static int barTextureId = -1;

    public boolean preRenderDone;
    public int updateTime;
    public boolean shown;

    public float shownBiomass;
    public float shownMax;
    public float shownCrit;

    public BiomassDataClient()
    {
        super();
    }

    public void tick()
    {
        updateTime++;
        if(updateTime > 20)
        {
            shown = true;
        }

        if(Biomass.config.biomassBarShowOnUpdate == 1 && (Math.abs(currentBiomass - shownBiomass) > 1F || Math.abs(maxBiomass - shownMax) > 1F || Math.abs(criticalMassMultiplier - shownCrit) > 1F) && updateTime > 20)
        {
            updateTime = 20;
        }

        float amp = 0.3F;
        shownBiomass += (currentBiomass - shownBiomass) * amp;
        shownMax += (maxBiomass - shownMax) * amp;
        shownCrit += (criticalMassMultiplier - shownCrit) * amp;
    }

    @Override
    public void setBiomass(float current, float max, float criticalMult)
    {
        updateTime = 0;
        super.setBiomass(current, max, criticalMult);
    }

    public float getMaxBiomassRender()
    {
        return Math.min(shownMax * shownCrit, MAX_BIOMASS);
    }

    public boolean shouldNotShowBar() //TODO do not render if spectating?
    {
        return Biomass.config.renderBiomassInfo == 0 || Biomass.config.biomassBarShowOnUpdate == 1 && updateTime >= 70/* || !Minecraft.getMinecraft().playerController.gameIsSurvivalOrAdventure()*/;//TODO uncomment this
    }

    public void doPreRender(RenderGameOverlayEvent event)
    {
        if(!preRenderDone)
        {
            preRenderDone = true;
            if(shouldNotShowBar())
            {
                return;
            }

            float offset = XP_BAR_OFFSET;
            if((!shown || Biomass.config.biomassBarShowOnUpdate == 1) && updateTime < 3)
            {
                offset = MathHelper.clamp((updateTime + event.getPartialTicks()) / 3F, 0F, 1F) * XP_BAR_OFFSET;
            }
            if(Biomass.config.biomassBarShowOnUpdate == 1 && updateTime >= 60)
            {
                offset = (1.0F - MathHelper.clamp((updateTime - 60 + event.getPartialTicks()) / 3F, 0F, 1F)) * XP_BAR_OFFSET;
            }
            GlStateManager.translate(0F, -offset, 0F);
        }
    }

    public void render(RenderGameOverlayEvent event)
    {
        preRenderDone = false;

        Minecraft mc = Minecraft.getMinecraft();

        if(shouldNotShowBar())
        {
            return;
        }

        if(barTextureId == -1)
        {
            try(IResource iresource = mc.mcResourceManager.getResource(Gui.ICONS))
            {
                BufferedImage image = TextureUtil.readBufferedImage(iresource.getInputStream());
                ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
                colorConvert.filter(image, image);

                barTextureId = TextureUtil.glGenTextures();
                TextureUtil.uploadTextureImageAllocate(barTextureId, image, false, false);
                GlStateManager.bindTexture(barTextureId);
            }
            catch(IOException e)
            {
                e.printStackTrace();
                barTextureId = -2;
                mc.getTextureManager().bindTexture(Gui.ICONS);
            }
        }
        else if(barTextureId == -2)
        {
            mc.getTextureManager().bindTexture(Gui.ICONS);
        }
        else
        {
            GlStateManager.bindTexture(barTextureId);
        }

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        float alpha = 1.0F;
        float offset = XP_BAR_OFFSET;
        if((!shown || Biomass.config.biomassBarShowOnUpdate == 1))
        {
            offset = MathHelper.clamp((updateTime + event.getPartialTicks()) / 3F, 0F, 1F) * XP_BAR_OFFSET;
            alpha = MathHelper.clamp((updateTime - 2F + event.getPartialTicks()) / 3F, 0F, 1F);
        }
        if(Biomass.config.biomassBarShowOnUpdate == 1 && updateTime >= 58)
        {
            offset = (1.0F - MathHelper.clamp((updateTime - 60 + event.getPartialTicks()) / 3F, 0F, 1F)) * XP_BAR_OFFSET;
            alpha = 1.0F - MathHelper.clamp((updateTime - 58F + event.getPartialTicks()) / 3F, 0F, 1F);
        }

        int width = event.getResolution().getScaledWidth();
        int height = event.getResolution().getScaledHeight();
        int left = width / 2 - 91;

        short barWidth = 182;
        int top = height - 32 + 8 + 1;

        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        mc.ingameGUI.drawTexturedModalRect(left, top, 0, 64, barWidth, 5);
        if(shownBiomass > 0)
        {
            GlStateManager.color(0.8F, 0.8F, 0.8F, alpha);
            float totalRender = shownBiomass / getMaxBiomassRender();
            float criticalMassRender = shownMax / getMaxBiomassRender();
            if(totalRender > criticalMassRender) //needs to render critical mass;
            {
                int normDraw = (int)(criticalMassRender * barWidth);
                mc.ingameGUI.drawTexturedModalRect(left, top, 0, 69, normDraw, 5);

                float red = 0.6F;
                if(Biomass.config.guiCriticalMassPulsationTime > 0)
                {
                    red += 0.2F * (float)Math.sin(Math.toRadians(((iChunUtil.eventHandlerClient.ticks + event.getPartialTicks()) / (float)Biomass.config.guiCriticalMassPulsationTime) * 180F));
                }
                GlStateManager.color(red, 0.0F, 0.0F, alpha);
                mc.ingameGUI.drawTexturedModalRect(left + normDraw, top, normDraw, 69, (int)((totalRender - criticalMassRender) * barWidth), 5);
            }
            else
            {
                mc.ingameGUI.drawTexturedModalRect(left, top, 0, 69, (int)(totalRender * barWidth), 5);
            }
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.translate(0F, offset, 0F);

        mc.getTextureManager().bindTexture(Gui.ICONS);
    }

    public static BiomassDataClient createFromTag(NBTTagCompound tag)
    {
        BiomassDataClient data = new BiomassDataClient();
        data.read(tag);
        return data;
    }
}
