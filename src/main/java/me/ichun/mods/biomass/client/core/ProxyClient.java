package me.ichun.mods.biomass.client.core;

import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.biomass.common.core.ProxyCommon;
import net.minecraftforge.common.MinecraftForge;

public class ProxyClient extends ProxyCommon
{
    @Override
    public void preInit()
    {
        super.preInit();

        Biomass.eventHandlerClient = new EventHandlerClient();
        MinecraftForge.EVENT_BUS.register(Biomass.eventHandlerClient);
    }
}
