package leaf.cosmere.common.items;

import leaf.cosmere.api.*;
import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.api.cosmerePower.CosmerePowerInstance;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import leaf.cosmere.common.registry.CosmerePowersRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class GodMetalNuggetItem extends MetalNuggetItem implements IHasSize, IGrantsPowers
{
	public static int MIN_SIZE = 1;
	public static int MAX_SIZE = 16;

	public GodMetalNuggetItem(Metals.MetalType metalType)
	{
		super(metalType);
	}

	@Override
	public int getMaxSize()
	{
		return MAX_SIZE;
	}

	@Override
	public int getMinSize()
	{
		return MIN_SIZE;
	}

	@Override
	public void onCraftedBy(ItemStack itemStack, Level level, Player player) {
		CompoundTag nbt = itemStack.getOrCreateTag();
		if(!nbt.contains("nuggetSize")) writeMetalAlloySizeNbtData(itemStack, getMaxSize());
	}

	// God Metals shouldn't hurt
	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level pLevel, LivingEntity pLivingEntity)
	{
		if (pLevel.isClientSide)
		{
			return itemstack;
		}

		if (pLivingEntity instanceof Player player && !player.isCreative())
		{
			itemstack.shrink(1);
		}

		return itemstack;
	}

	@Override
	public boolean hasCraftingRemainingItem(ItemStack stack) {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack stack) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean isFoil(ItemStack itemStack)
	{
		// God Metals should have foil
		return super.isFoil(itemStack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		Integer size = readMetalAlloySizeNbtData(stack);

		tooltip.add(Component.literal("Size: ").withStyle(ChatFormatting.WHITE).append(
				Component.literal(size + "/" + MAX_SIZE).withStyle(ChatFormatting.GRAY)));

		ArrayList<CosmerePower> cosmerePowers = determinePowers(stack);

		if (!cosmerePowers.isEmpty())
		{
			tooltip.add(Component.empty());
			tooltip.add(Component.literal("When consumed:").withStyle(ChatFormatting.GOLD));
			for (CosmerePower cosmerePower : cosmerePowers)
			{
				tooltip.add(Component.literal("+" + size + " ").append(
								Component.translatable(cosmerePower.getTranslationKey()))
						.withStyle(ChatFormatting.BLUE));
			}
		}
	}

	public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
		return false;
	}

	@Override
	public ArrayList<CosmerePower> determinePowers(ItemStack itemStack)
	{
		ArrayList<CosmerePower> cosmerePowers = new ArrayList<>();

		if (this.getMetalType() == Metals.MetalType.LERASIUM)
		{
			for (Metals.MetalType metal : EnumUtils.METAL_TYPES)
			{
				CosmerePower cosmerePower = Manifestations.ManifestationTypes.ALLOMANCY.getCosmerePower(metal.getID());
				if (cosmerePower.getPower() != CosmerePowersRegistry.NONE.get())
				{
					cosmerePowers.add(cosmerePower);
				}
			}
		}
		else if (this.getMetalType() == Metals.MetalType.LERASATIUM)
		{
			for (Metals.MetalType metal : EnumUtils.METAL_TYPES)
			{
				CosmerePower cosmerePower = Manifestations.ManifestationTypes.FERUCHEMY.getCosmerePower(metal.getID());
				if (cosmerePower.getPower() != CosmerePowersRegistry.NONE.get())
				{
					cosmerePowers.add(cosmerePower);
				}
			}
		}
		return cosmerePowers;
	}

	@Override
	public void grantPowers(LivingEntity livingEntity, ArrayList<CosmerePowerInstance> cosmerePowers)
	{
		SpiritwebCapability.get(livingEntity).ifPresent(iSpiritweb ->
		{
			SpiritwebCapability spiritweb = (SpiritwebCapability) iSpiritweb;

			for(CosmerePowerInstance cosmerePower: cosmerePowers)
			{
				spiritweb.giveCosmerePower(cosmerePower);
			}

			if (livingEntity instanceof ServerPlayer serverPlayer)
			{
				spiritweb.syncToClients(serverPlayer);
			}
		});
	}

}
