package me.ichun.mods.biomass.client.entity;

import me.ichun.mods.biomass.client.model.ModelAbsorb;
import me.ichun.mods.biomass.client.render.RenderAbsorbBiomass;
import me.ichun.mods.ichunutil.client.model.util.ModelHelper;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import me.ichun.mods.ichunutil.common.core.util.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityAbsorbBiomass extends Entity
{
    public EntityLivingBase killed;
    public EntityLivingBase absorber;

    public ModelAbsorb model;
    public ResourceLocation killedTexture;
    public float prevScaleX = -1F;
    public float prevScaleY = -1F;
    public float prevScaleZ = -1F;

    public int age;

    public EntityAbsorbBiomass(World world)
    {
        super(world);
        model = new ModelAbsorb();
        killedTexture = ResourceHelper.texPig;
        setSize(0.1F, 0.1F);
        noClip = true;
        ignoreFrustumCheck = true;
    }

    public EntityAbsorbBiomass(World world, EntityLivingBase killed, EntityLivingBase absorber)
    {
        super(world);
        setSize(0.1F, 0.1F);
        noClip = true;
        ignoreFrustumCheck = true;

        this.killed = killed;
        this.absorber = absorber;
        Render rend = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(killed);
        model = new ModelAbsorb(rend, killed);
        killedTexture = ObfHelper.getEntityTexture(rend, rend.getClass(), killed);

        setLocationAndAngles(absorber.posX, absorber.posY, absorber.posZ, absorber.rotationYaw, absorber.rotationPitch);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance)
    {
        double d0 = this.getEntityBoundingBox().getAverageEdgeLength() * 20.0D; // * 20D is the new renderDistanceWeight

        if(Double.isNaN(d0))
        {
            d0 = 1.0D;
        }

        d0 = d0 * 64.0D * getRenderDistanceWeight();
        return distance < d0 * d0;
    }

    @Override
    public void onUpdate()
    {
        lastTickPosX = absorber.lastTickPosX;
        lastTickPosY = absorber.lastTickPosY + absorber.height / 2F;
        lastTickPosZ = absorber.lastTickPosZ;
        prevPosX = absorber.prevPosX;
        prevPosY = absorber.prevPosY + absorber.height / 2F;
        prevPosZ = absorber.prevPosZ;
        posX = absorber.posX;
        posY = absorber.posY + absorber.height / 2F;
        posZ = absorber.posZ;

        age++;
        if(age > 400 || killed.getDistance(absorber) > 128)//20 seconds
        {
            setDead();
            return;
        }

        model.tick(this);
    }x

    @Override
    public void setDead()
    {
        super.setDead();
        model.clean();
    }

    @Override
    public boolean shouldRenderInPass(int pass)
    {
        return RenderAbsorbBiomass.isFirstPerson(absorber) ? pass == 1 : pass == 0;
    }

    @Override
    public boolean isEntityAlive()
    {
        return !this.isDead;
    }

    @Override
    public boolean writeToNBTOptional(NBTTagCompound tag)
    {
        return false;
    }

    @Override
    protected void entityInit(){}

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {}

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {}
}
