package com.tumod.networthmod;

import com.tumod.networthmod.commands.CommandNetworth;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "networthmod", name = "NetworthMod", version = "1.0")
public class NetworthMod {

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandNetworth());
    }
}
