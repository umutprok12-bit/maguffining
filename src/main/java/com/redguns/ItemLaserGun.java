package com.redguns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** The upgraded gun: instant red laser that pierces up to {@link #MAX_PIERCE} entities and stops at blocks. */
public class ItemLaserGun extends Item {
    public static final double RANGE = 48.0;
    public static final float DAMAGE = 8.0F;
    public static final int MAX_PIERCE = 3;
    public static final int COOLDOWN_TICKS = 8;

    public ItemLaserGun() {
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.getCooldownTracker().hasCooldown(this)) {
            return new ActionResult<>(EnumActionResult.FAIL, stack);
        }

        if (world.isRemote) {
            player.rotationPitch -= 0.8F;
        } else {
            fire(world, player);
        }

        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private void fire(World world, EntityPlayer player) {
        Vec3d start = player.getPositionEyes(1.0F);
        Vec3d look = player.getLookVec();
        Vec3d end = start.add(look.scale(RANGE));

        // the beam is stopped by solid blocks only
        RayTraceResult blockHit = world.rayTraceBlocks(start, end, false, true, false);
        if (blockHit != null) {
            end = blockHit.hitVec;
        }

        // collect every entity the beam crosses, nearest first
        final Vec3d s = start;
        List<Entity> hit = new ArrayList<>();
        List<Double> dist = new ArrayList<>();
        AxisAlignedBB area = new AxisAlignedBB(start, end).grow(1.0);
        for (Entity e : world.getEntitiesWithinAABBExcludingEntity(player, area)) {
            if (!e.canBeCollidedWith()) continue;
            RayTraceResult r = e.getEntityBoundingBox().grow(0.3).calculateIntercept(start, end);
            if (r != null) {
                hit.add(e);
                dist.add(s.squareDistanceTo(r.hitVec));
            }
        }
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < hit.size(); i++) order.add(i);
        Collections.sort(order, (a, b) -> Double.compare(dist.get(a), dist.get(b)));

        int pierced = 0;
        Vec3d beamEnd = end;
        for (int idx : order) {
            Entity e = hit.get(idx);
            e.attackEntityFrom(DamageSource.causePlayerDamage(player), DAMAGE);
            e.hurtResistantTime = 0; // let repeated shots land
            if (++pierced >= MAX_PIERCE) {
                RayTraceResult r = e.getEntityBoundingBox().grow(0.3).calculateIntercept(start, end);
                if (r != null) beamEnd = r.hitVec;
                break;
            }
        }

        // draw the beam; muzzle starts slightly in front and below the eyes
        WorldServer ws = (WorldServer) world;
        Vec3d from = start.add(look.scale(0.9)).add(0.0, -0.15, 0.0);
        Vec3d dir = beamEnd.subtract(from);
        double len = dir.length();
        if (len > 0.1) {
            Vec3d step = dir.scale(1.0 / len);
            for (double d = 0.0; d < len; d += 0.3) {
                Vec3d p = from.add(step.scale(d));
                // REDSTONE with count 0: offsets are the RGB colour
                ws.spawnParticle(EnumParticleTypes.REDSTONE, true, p.x, p.y, p.z, 0, 1.0, 0.05, 0.05, 1.0);
            }
        }
        ws.spawnParticle(EnumParticleTypes.FLAME, true, beamEnd.x, beamEnd.y, beamEnd.z, 6, 0.08, 0.08, 0.08, 0.02);

        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_FIREWORK_LAUNCH, SoundCategory.PLAYERS, 0.8F, 2.0F);
        world.playSound(null, player.posX, player.posY, player.posZ,
                SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.4F, 2.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add("Fires an instant laser");
        tooltip.add("Damage: 8, pierces up to 3 targets");
    }
}
