package me.ichun.mods.biomass.common.packet;

import io.netty.buffer.ByteBuf;
import me.ichun.mods.biomass.client.entity.EntityAbsorbBiomass;
import me.ichun.mods.ichunutil.common.core.network.AbstractPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketBiomassAbsorptionEffect extends AbstractPacket
{
    public int killedId;
    public int absorberId;

    public PacketBiomassAbsorptionEffect(){}

    public PacketBiomassAbsorptionEffect(int killedId, int absorberId)
    {
        this.killedId = killedId;
        this.absorberId = absorberId;
    }

    @Override
    public void writeTo(ByteBuf buf)
    {
        buf.writeInt(killedId);
        buf.writeInt(absorberId);
    }

    @Override
    public void readFrom(ByteBuf buf)
    {
        killedId = buf.readInt();
        absorberId = buf.readInt();
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        handleClient();
    }

    @Override
    public Side receivingSide()
    {
        return Side.CLIENT;
    }

    @SideOnly(Side.CLIENT)
    public void handleClient()
    {
        Minecraft mc = Minecraft.getMinecraft();
        Entity killed = mc.world.getEntityByID(killedId);
        Entity absorber = mc.world.getEntityByID(absorberId);
        if(killed instanceof EntityLivingBase && absorber instanceof EntityLivingBase)
        {
            mc.world.spawnEntity(new EntityAbsorbBiomass(mc.world, (EntityLivingBase)killed, (EntityLivingBase)absorber));
            killed.setDead();
        }
    }
}
