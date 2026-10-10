package com.redguns;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** The plain brown gun: fires a red energy bolt. */
public class ItemRedGun extends Item {
    public static final int COOLDOWN_TICKS = 12;

    public ItemRedGun() {
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
            player.rotationPitch -= 1.2F; // small recoil kick on the shooter's screen
        } else {
            EntityRedProjectile bolt = new EntityRedProjectile(world, player);
            bolt.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, 2.6F, 0.4F);
            world.spawnEntity(bolt);
            world.playSound(null, player.posX, player.posY, player.posZ,
                    SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS, 0.6F, 1.6F);
        }

        player.getCooldownTracker().setCooldown(this, COOLDOWN_TICKS);
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        tooltip.add("Fires a red energy bolt");
        tooltip.add("Damage: 5");
    }
}
