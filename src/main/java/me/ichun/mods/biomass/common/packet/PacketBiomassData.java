package me.ichun.mods.biomass.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.biomass.client.biomass.BiomassDataClient;
import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

public class PacketBiomassData extends AbstractPacket
{
    public NBTTagCompound tag;

    public PacketBiomassData(){}

    public PacketBiomassData(NBTTagCompound tag)
    {
        this.tag = tag;
    }

    @Override
    public void writeTo(ByteBuf buf)
    {
        ByteBufUtils.writeTag(buf, tag);
    }

    @Override
    public void readFrom(ByteBuf buf)
    {
        tag = ByteBufUtils.readTag(buf);
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        Biomass.eventHandlerClient.currentBiomassData = BiomassDataClient.createFromTag(tag);
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }
}
