package me.ichun.mods.biomass.client.core;

import me.ichun.mods.biomass.client.biomass.BiomassDataClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class EventHandlerClient
{
    public BiomassDataClient currentBiomassData;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            if(currentBiomassData != null && !Minecraft.getMinecraft().isGamePaused())
            {
                currentBiomassData.tick();
            }
        }
    }

//    @SubscribeEvent
//    public void onRenderTick(TickEvent.RenderTickEvent event)
//    {
//        if(event.phase == TickEvent.Phase.START)
//        {
//            if(currentBiomassData != null)
//            {
//                currentBiomassData.resetRenderData(event.renderTickTime);
//            }
//        }
//    }

    @SubscribeEvent
    public void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event)
    {
        if(currentBiomassData != null && !currentBiomassData.preRenderDone && BiomassDataClient.PRE_RENDER_EVENTS.contains(event.getType()))
        {
            currentBiomassData.doPreRender(event);
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event)
    {
        if(currentBiomassData != null && (event.getType() == RenderGameOverlayEvent.ElementType.JUMPBAR || event.getType() == RenderGameOverlayEvent.ElementType.EXPERIENCE))
        {
            currentBiomassData.render(event);
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        Minecraft.getMinecraft().addScheduledTask(this::disconnectFromServer);
    }

    public void disconnectFromServer()
    {
        currentBiomassData = null;
    }
}
