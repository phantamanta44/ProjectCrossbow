package io.github.phantamanta44.pcrossbow.client.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ClientTickHandler {

    private static long tick;

    public static long getTick() {
        return tick;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.ClientTickEvent.Phase.END)
            tick++;
    }

}
