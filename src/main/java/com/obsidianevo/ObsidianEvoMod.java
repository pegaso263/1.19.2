package com.obsidianevo;

import com.obsidianevo.item.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ObsidianEvoMod.MOD_ID)
public class ObsidianEvoMod {
    public static final String MOD_ID = "obsidianevo";

    public ObsidianEvoMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
