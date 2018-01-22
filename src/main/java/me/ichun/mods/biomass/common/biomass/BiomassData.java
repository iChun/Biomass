package me.ichun.mods.biomass.common.biomass;

import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.biomass.common.packet.PacketBiomassAmount;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class BiomassData
{
    public static final float MAX_BIOMASS = 999999999999F; //max = 1 quadrillion cm3

    public float currentBiomass;
    public float maxBiomass;
    public float criticalMassMultiplier;

    public BiomassData()
    {
        currentBiomass = 0;
        maxBiomass = 100;
        criticalMassMultiplier = 1.0F;
    }

    public void write(NBTTagCompound tag)
    {
        tag.setBoolean("hasAbility", true);
        tag.setFloat("maxBiomass", maxBiomass);
        tag.setFloat("currentBiomass", currentBiomass);
        tag.setFloat("criticalMassMultiplier", criticalMassMultiplier);
    }

    public void read(NBTTagCompound tag)
    {
        maxBiomass = tag.getFloat("maxBiomass");
        currentBiomass = tag.getFloat("currentBiomass");
        criticalMassMultiplier = Math.min(1.0F, tag.getFloat("criticalMassMultiplier"));
    }

    public void setBiomass(float current, float max, float criticalMult)
    {
        currentBiomass = current;
        maxBiomass = max;
        criticalMassMultiplier = Math.min(1.0F, criticalMult);
    }

    public void addBiomass(float amount)
    {
        currentBiomass += amount;
        if(currentBiomass > getMaxBiomass())
        {
            currentBiomass = getMaxBiomass();
        }
    }

    public float getMaxBiomass()
    {
        return Math.min(maxBiomass * criticalMassMultiplier, MAX_BIOMASS);
    }

    public void updateBiomass(EntityPlayer player)
    {
        Biomass.channel.sendTo(new PacketBiomassAmount(currentBiomass, maxBiomass, criticalMassMultiplier), player);
    }

    public static float calculateBiomass(EntityLivingBase living)
    {
        return living.width * living.width * living.height * 100F; //calculate in cm3
    }

    public static BiomassData createFromTag(NBTTagCompound tag)
    {
        BiomassData data = new BiomassData();
        data.read(tag);
        return data;
    }
}
