package com.redguns;

import net.minecraft.item.Item;

public class ModItems {
    public static final Item RED_GUN = new ItemRedGun()
            .setRegistryName(RedGuns.MODID, "red_gun").setUnlocalizedName("red_gun");
    public static final Item LASER_GUN = new ItemLaserGun()
            .setRegistryName(RedGuns.MODID, "laser_gun").setUnlocalizedName("laser_gun");
}
