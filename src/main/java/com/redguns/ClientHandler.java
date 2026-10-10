package com.redguns;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = RedGuns.MODID, value = Side.CLIENT)
public class ClientHandler {

    /** Called from RedGuns.preInit on the client only. */
    public static void preInit() {
        OBJLoader.INSTANCE.addDomain(RedGuns.MODID);

        // the projectile is drawn purely with particles, so its renderer draws nothing
        RenderingRegistry.registerEntityRenderingHandler(EntityRedProjectile.class,
                new IRenderFactory<EntityRedProjectile>() {
                    @Override
                    public Render<? super EntityRedProjectile> createRenderFor(RenderManager manager) {
                        return new Render<EntityRedProjectile>(manager) {
                            @Override
                            protected ResourceLocation getEntityTexture(EntityRedProjectile entity) {
                                return null;
                            }
                        };
                    }
                });
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent e) {
        ModelLoader.setCustomModelResourceLocation(ModItems.RED_GUN, 0,
                new ModelResourceLocation(RedGuns.MODID + ":red_gun", "inventory"));
        ModelLoader.setCustomModelResourceLocation(ModItems.LASER_GUN, 0,
                new ModelResourceLocation(RedGuns.MODID + ":laser_gun", "inventory"));
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e) {
        wrap(e, "red_gun");
        wrap(e, "laser_gun");
    }

    private static void wrap(ModelBakeEvent e, String name) {
        ModelResourceLocation loc = new ModelResourceLocation(RedGuns.MODID + ":" + name, "inventory");
        IBakedModel model = e.getModelRegistry().getObject(loc);
        if (model != null) {
            e.getModelRegistry().putObject(loc, new GunBakedModel(model));
        }
    }
}
