package me.ichun.mods.biomass.common.core;

import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.biomass.common.packet.PacketBiomassAmount;
import me.ichun.mods.biomass.common.packet.PacketBiomassData;
import me.ichun.mods.biomass.common.packet.PacketUnlockBiomassAbility;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import net.minecraftforge.common.MinecraftForge;

public class ProxyCommon
{
    public void preInit()
    {
        Biomass.eventHandlerServer = new EventHandlerServer();
        MinecraftForge.EVENT_BUS.register(Biomass.eventHandlerServer);

        Biomass.channel = new PacketChannel("Biomass", PacketBiomassData.class, PacketUnlockBiomassAbility.class, PacketBiomassAmount.class);
    }
}
