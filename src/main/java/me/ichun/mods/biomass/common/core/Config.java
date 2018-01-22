package me.ichun.mods.biomass.common.core;

import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.annotations.ConfigProp;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntBool;
import me.ichun.mods.ichunutil.common.core.config.annotations.IntMinMax;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

public class Config extends ConfigBase
{
    @ConfigProp(category = "gameplay")
    @IntMinMax(min = -1, max = 100)
    public int biomassAbilityChance = 5;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0, max = 1)
    public int renderBiomassInfo = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntBool
    public int biomassBarShowOnUpdate = 1;

    @ConfigProp(category = "clientOnly", side = Side.CLIENT)
    @IntMinMax(min = 0)
    public int biomassBarCriticalMassPulsationTime = 40;

    public Config(File file)
    {
        super(file);
    }

    @Override
    public String getModId()
    {
        return Biomass.MOD_ID;
    }

    @Override
    public String getModName()
    {
        return Biomass.MOD_NAME;
    }
}
