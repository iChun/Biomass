package me.ichun.mods.biomass.common;

import me.ichun.mods.biomass.client.core.EventHandlerClient;
import me.ichun.mods.biomass.common.command.CommandBiomass;
import me.ichun.mods.biomass.common.core.Config;
import me.ichun.mods.biomass.common.core.EventHandlerServer;
import me.ichun.mods.biomass.common.core.ProxyCommon;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.network.PacketChannel;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.update.UpdateChecker;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommandManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(name = Biomass.MOD_NAME, modid = Biomass.MOD_ID,
        version = Biomass.VERSION,
        guiFactory = iChunUtil.GUI_CONFIG_FACTORY,
        dependencies = "required-after:ichunutil@[" + iChunUtil.VERSION_MAJOR +".1.0," + (iChunUtil.VERSION_MAJOR + 1) + ".0.0)",
        acceptedMinecraftVersions = iChunUtil.MC_VERSION_RANGE
)
public class Biomass
{
    public static final String VERSION = iChunUtil.VERSION_MAJOR + ".0.0";

    public static final String MOD_NAME = "Biomass";
    public static final String MOD_ID = "biomass";

    @SidedProxy(clientSide = "me.ichun.mods.biomass.client.core.ProxyClient", serverSide = "me.ichun.mods.biomass.common.core.ProxyCommon")
    public static ProxyCommon proxy;

    public static Config config;

    public static EventHandlerServer eventHandlerServer;
    public static EventHandlerClient eventHandlerClient;

    public static PacketChannel channel;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        config = ConfigHandler.registerConfig(new Config(event.getSuggestedConfigurationFile()));

        proxy.preInit();

        UpdateChecker.registerMod(new UpdateChecker.ModVersionInfo(MOD_NAME, iChunUtil.VERSION_OF_MC, VERSION, false));
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerAboutToStartEvent event)
    {
        ICommandManager manager = event.getServer().getCommandManager();
        if(manager instanceof CommandHandler)
        {
            CommandHandler handler = (CommandHandler)manager;
            handler.registerCommand(new CommandBiomass());
        }
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event)
    {
        eventHandlerServer.playerBiomass.clear();
    }
}
