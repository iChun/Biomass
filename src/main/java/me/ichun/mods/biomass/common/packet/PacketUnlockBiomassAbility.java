package me.ichun.mods.biomass.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.biomass.client.biomass.BiomassDataClient;
import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class PacketUnlockBiomassAbility extends AbstractPacket
{
    public PacketUnlockBiomassAbility(){}

    @Override
    public void writeTo(ByteBuf buf)
    {
    }

    @Override
    public void readFrom(ByteBuf buf)
    {
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        Biomass.eventHandlerClient.currentBiomassData = new BiomassDataClient();
        //TODO play effect
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }
}
