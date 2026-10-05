package com.dwm.autoarmor;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "dwm_autoarmor", name = "DWM AutoArmor", version = "1.0.0", clientSideOnly = true)
public class AutoArmorMod {
    public static final String MODID = "dwm_autoarmor";
    public static final AutoArmorHandler HANDLER = new AutoArmorHandler();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(HANDLER);
    }
}
