package me.ichun.mods.biomass.common.core;

import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.biomass.common.biomass.BiomassData;
import me.ichun.mods.biomass.common.packet.PacketBiomassData;
import me.ichun.mods.biomass.common.packet.PacketUnlockBiomassAbility;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.HashMap;

public class EventHandlerServer
{
    public static final String BIOMASS_DATA_NAME = "BiomassSave";

    public HashMap<String, BiomassData> playerBiomass = new HashMap<>();

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event)
    {
        if(!event.getEntityLiving().getEntityWorld().isRemote)
        {
            if(event.getEntityLiving() instanceof EntityPlayerMP)
            {
                //biomass accumulated is lost on death
                EntityPlayerMP player = (EntityPlayerMP)event.getEntityLiving();
                BiomassData data = playerBiomass.get(player.getName());
                if(data != null)
                {
                    data.setBiomass(0F, data.maxBiomass, data.criticalMassMultiplier);
                }
            }
            if(event.getSource().getTrueSource() instanceof EntityPlayerMP && event.getEntityLiving() != event.getSource().getTrueSource())
            {
                //Absorb biomass
                EntityPlayerMP player = (EntityPlayerMP)event.getSource().getTrueSource();
                BiomassData data = playerBiomass.get(player.getName());
                if(data != null)
                {
                    //TODO send packet to play biomass absorption effect.
                    data.addBiomass(BiomassData.calculateBiomass(event.getEntityLiving()));
                    data.updateBiomass(player);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event)
    {
        if(!event.getEntityLiving().world.isRemote && event.getSource() == DamageSource.WITHER && Biomass.config.biomassAbilityChance > 0 &&  event.getEntityLiving() instanceof EntityPlayerMP && !playerBiomass.containsKey(event.getEntityLiving().getName()) && !(FakePlayer.class.isAssignableFrom(event.getEntityLiving().getClass()) || ((EntityPlayerMP)event.getEntityLiving()).connection == null) && event.getEntityLiving().getRNG().nextFloat() < (float)Biomass.config.biomassAbilityChance / 100F)
        {
            //unlock biomass ability
            playerBiomass.put(event.getEntityLiving().getName(), new BiomassData());
            Biomass.channel.sendTo(new PacketUnlockBiomassAbility(), (EntityPlayerMP)event.getEntityLiving());
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        if(loadPlayerData(event.player))
        {
            NBTTagCompound tag = new NBTTagCompound();
            playerBiomass.get(event.player.getName()).write(tag);
            Biomass.channel.sendTo(new PacketBiomassData(tag), event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if(playerBiomass.containsKey(event.player.getName()))
        {
            savePlayerData(event.player);
            playerBiomass.remove(event.player.getName());
        }
    }

    public void savePlayerData(EntityPlayer player)
    {
        BiomassData data = playerBiomass.get(player.getName());
        if(data != null)
        {
            NBTTagCompound tag = EntityHelper.getPlayerPersistentData(player, BIOMASS_DATA_NAME);
            data.write(tag);
        }
    }

    public boolean loadPlayerData(EntityPlayer player) //Returns true if the player has a morph and requires synching to the clients.
    {
        NBTTagCompound tag = EntityHelper.getPlayerPersistentData(player, BIOMASS_DATA_NAME);
        if(tag.hasKey("hasAbility"))
        {
            playerBiomass.put(player.getName(), BiomassData.createFromTag(tag));
            return true;
        }
        else if(Biomass.config.biomassAbilityChance == -1) //Give the ability anyways
        {
            playerBiomass.put(player.getName(), new BiomassData());
            return true;
        }
        return false;
    }
}
