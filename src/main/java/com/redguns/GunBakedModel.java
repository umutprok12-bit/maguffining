package com.redguns;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.lwjgl.util.vector.Vector3f;

/**
 * Wraps the baked OBJ model and supplies hand / GUI / ground transforms
 * (same semantics as the "display" block of a vanilla item model).
 *
 * The OBJ is baked with the barrel along +X, up along +Y and its centre at (0.5, 0.5, 0.5),
 * exactly like a flat tool sprite, so the numbers below are based on vanilla item/handheld.
 */
public class GunBakedModel implements IBakedModel {
    private final IBakedModel base;
    private final ItemCameraTransforms transforms = buildTransforms();

    public GunBakedModel(IBakedModel base) {
        this.base = base;
    }

    // vanilla display values: rotation in degrees, translation in 1/16 block, uniform scale
    private static ItemTransformVec3 t(float rx, float ry, float rz, float tx, float ty, float tz, float s) {
        return new ItemTransformVec3(new Vector3f(rx, ry, rz),
                new Vector3f(tx / 16.0F, ty / 16.0F, tz / 16.0F), new Vector3f(s, s, s));
    }

    private static ItemCameraTransforms buildTransforms() {
        ItemTransformVec3 tpRight = t(0, -90, 90, 0.00F, -1.35F, 0.32F, 0.90F);
        ItemTransformVec3 tpLeft  = t(0,  90, -90, 0.00F, -1.35F, 0.32F, 0.90F);
        ItemTransformVec3 fpRight = t(0, -90, 6, 1.13F, -1.04F, 1.56F, 0.85F);
        ItemTransformVec3 fpLeft  = t(0,  90, -6, 1.13F, -1.04F, 1.56F, 0.85F);
        ItemTransformVec3 head    = t(0, 180, 0, 0, 13, 7, 0.9F);
        ItemTransformVec3 gui     = t(18, 28, 0, 0, 0, 0, 1.15F);
        ItemTransformVec3 ground  = t(0, 0, 0, 0, 2, 0, 0.6F);
        ItemTransformVec3 fixed   = t(0, 180, 0, 0, 0, 0, 0.8F);
        return new ItemCameraTransforms(tpLeft, tpRight, fpLeft, fpRight, head, gui, ground, fixed);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return base.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return base.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return base.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return transforms;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }
}
