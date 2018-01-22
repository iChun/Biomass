package me.ichun.mods.biomass.common.command;

import me.ichun.mods.biomass.common.Biomass;
import me.ichun.mods.biomass.common.biomass.BiomassData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

public class CommandBiomass extends CommandBase
{
    @Override
    public String getName()
    {
        return "biomass";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/biomass get/add/set <player> <amount>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if(args.length < 3)
        {
            throw new WrongUsageException(getUsage(sender));
        }
        EntityPlayerMP player = getPlayer(server, sender, args[1]);
        BiomassData data = Biomass.eventHandlerServer.playerBiomass.computeIfAbsent(player.getName(), v -> new BiomassData());
        if("get".startsWith(args[0]))
        {
            sender.sendMessage(new TextComponentString("Biomass: " + data.currentBiomass + " Max: " + data.getMaxBiomass()));
            return;
        }
        float amount = 0F;
        try
        {
            amount = Float.parseFloat(args[2]);
        }
        catch(NumberFormatException e)
        {
            throw new WrongUsageException(getUsage(sender));
        }
        if("add".startsWith(args[0]))
        {
            data.addBiomass(amount);
            data.updateBiomass(player);
        }
        else if("set".startsWith(args[0]))
        {
            data.setBiomass(amount, data.maxBiomass, data.criticalMassMultiplier);
            data.updateBiomass(player);
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "get", "add", "set") : args.length == 2 ? getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : getListOfStringsMatchingLastWord(args, "");
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        return args.length > 0 && index == 1;
    }
}
