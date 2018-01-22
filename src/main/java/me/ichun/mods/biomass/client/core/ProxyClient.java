package me.ichun.mods.biomass.client.core;

import me.ichun.mods.biomass.client.entity.EntityAbsorbBiomass;
import me.ichun.mods.biomass.client.render.RenderAbsorbBiomass;
import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.biomass.common.core.ProxyCommon;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        Biomass.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(Biomass.eventHandlerClient);

        RenderingRegistry.registerEntityRenderingHandler(EntityAbsorbBiomass.class, new RenderAbsorbBiomass.RenderFactory());
    }
}
