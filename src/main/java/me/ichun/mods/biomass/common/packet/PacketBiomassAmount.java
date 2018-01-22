package me.ichun.mods.biomass.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.biomass.client.biomass.BiomassDataClient;
import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

public class PacketBiomassAmount extends AbstractPacket
{
    public float current;
    public float max;
    public float criticalMult;

    public PacketBiomassAmount(){}

    public PacketBiomassAmount(float cur, float max, float criticalMult)
    {
        this.current = cur;
        this.max = max;
        this.criticalMult = criticalMult;
    }

    @Override
    public void writeTo(ByteBuf buf)
    {
        buf.writeFloat(current);
        buf.writeFloat(max);
        buf.writeFloat(criticalMult);
    }

    @Override
    public void readFrom(ByteBuf buf)
    {
        current = buf.readFloat();
        max = buf.readFloat();
        criticalMult = buf.readFloat();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        if(Biomass.eventHandlerClient.currentBiomassData == null)
        {
            Biomass.eventHandlerClient.currentBiomassData = new BiomassDataClient();
        }
        Biomass.eventHandlerClient.currentBiomassData.setBiomass(current, max, criticalMult);
    }

    @Override
    public Side receivingSide()
    {
        return null;
    }
}
