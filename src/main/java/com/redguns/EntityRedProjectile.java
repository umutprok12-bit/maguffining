package com.redguns;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityRedProjectile extends EntityThrowable {
    public static final float DAMAGE = 5.0F;

    public EntityRedProjectile(World world) {
        super(world);
    }

    public EntityRedProjectile(World world, EntityLivingBase thrower) {
        super(world, thrower);
    }

    @Override
    protected float getGravityVelocity() {
        return 0.0F; // flies straight
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote) {
            // glowing red trail
            for (int i = 0; i < 2; i++) {
                world.spawnParticle(EnumParticleTypes.REDSTONE,
                        posX - motionX * 0.5 * i, posY - motionY * 0.5 * i, posZ - motionZ * 0.5 * i,
                        1.0, 0.0, 0.0);
            }
        } else if (ticksExisted > 80) {
            setDead();
        }
    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if (world.isRemote) return;
        if (result.entityHit != null) {
            result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), DAMAGE);
        }
        ((WorldServer) world).spawnParticle(EnumParticleTypes.FLAME, true, posX, posY, posZ,
                8, 0.1, 0.1, 0.1, 0.03);
        setDead();
    }
}
