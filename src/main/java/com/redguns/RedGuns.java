package com.redguns;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = RedGuns.MODID, name = "Red Guns", version = "1.0", acceptedMinecraftVersions = "[1.12.2]")
public class RedGuns {
    public static final String MODID = "redguns";

    @Mod.Instance(MODID)
    public static RedGuns instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        EntityRegistry.registerModEntity(new ResourceLocation(MODID, "red_projectile"),
                EntityRedProjectile.class, "red_projectile", 0, instance, 64, 1, true);

        if (e.getSide() == Side.CLIENT) {
            ClientHandler.preInit();
        }
    }
}
