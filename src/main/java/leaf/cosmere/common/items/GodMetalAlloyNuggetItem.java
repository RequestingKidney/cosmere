package leaf.cosmere.common.items;

import leaf.cosmere.api.IGrantsPowers;
import leaf.cosmere.api.IHasSize;
import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.api.cosmerePower.CosmerePowerInstance;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import leaf.cosmere.common.registry.CosmerePowersRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class GodMetalAlloyNuggetItem extends AlloyNuggetItem implements IHasSize, IGrantsPowers
{
	public static int MIN_SIZE = 1;
	public static int MAX_SIZE = 16;

	public int getMinSize()
	{
		return MIN_SIZE;
	}

	public int getMaxSize()
	{
		return MAX_SIZE;
	}

	public GodMetalAlloyNuggetItem(Metals.MetalType metalType, Metals.MetalType alloyedMetalType)
	{

		super(metalType, alloyedMetalType);
	}

	public void addFilled(CreativeModeTab.Output output, int size)
	{
		ItemStack itemStack = new ItemStack(this);
		writeMetalAlloySizeNbtData(itemStack, size);
		output.accept(itemStack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		Integer size = readMetalAlloySizeNbtData(stack);

		MutableComponent metalListComponent = Component.empty();
		metalListComponent.append(Component.translatable(this.getMetalType().getTranslationKey())
				.withStyle(Style.EMPTY.withColor(this.getMetalType().getColorValue())));

		metalListComponent.append(Component.literal(" / ").withStyle(ChatFormatting.WHITE));
		metalListComponent.append(Component.translatable(alloyedMetalType.getTranslationKey())
				.withStyle(Style.EMPTY.withColor(alloyedMetalType.getColorValue())));

		tooltip.add(metalListComponent);
		tooltip.add(Component.literal("Size: ").withStyle(ChatFormatting.WHITE).append(
				Component.literal(size + "/" + MAX_SIZE).withStyle(ChatFormatting.GRAY)));

		ArrayList<CosmerePower> cosmerePowers = determinePowers(stack);

		if (!cosmerePowers.isEmpty() & size != null)
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

	@Override
	public Rarity getRarity(ItemStack itemStack)
	{
		Integer size = readMetalAlloySizeNbtData(itemStack);
		if (size == null) return Rarity.COMMON;

		if (size <= 8) return Rarity.UNCOMMON;
		else if (size == MAX_SIZE) return Rarity.EPIC;
		return Rarity.RARE;
	}

	// This needs to be replaced once a connection system is in place
	// https://wob.coppermind.net/events/361-skyward-pre-release-ama/#e11225
	// Mixing Lerasium with other god metals or magic systems metals could create connections
	// for new Investiture sources and new Manifestations
	public ArrayList<CosmerePower> determinePowers(ItemStack itemStack)
	{
		ArrayList<CosmerePower> cosmerePowers = new ArrayList<>();

		CosmerePower cosmerePower;
		if (this.getMetalType() == Metals.MetalType.LERASIUM)
		{
			cosmerePower = Manifestations.ManifestationTypes.ALLOMANCY.getCosmerePower(alloyedMetalType.getID());
			if (cosmerePower != CosmerePowersRegistry.NONE.get())
			{
				cosmerePowers.add(cosmerePower);
			}

		}
		else if (this.getMetalType() == Metals.MetalType.LERASATIUM)
		{
			cosmerePower = Manifestations.ManifestationTypes.FERUCHEMY.getCosmerePower(alloyedMetalType.getID());
			if (cosmerePower != CosmerePowersRegistry.NONE.get())
			{
				cosmerePowers.add(cosmerePower);
			}
		}
		return cosmerePowers;
	}

	@Override
	public void grantPowers(LivingEntity livingEntity, ArrayList<CosmerePowerInstance> cosmerePowerInstances)
	{
		SpiritwebCapability.get(livingEntity).ifPresent(iSpiritweb ->
		{
			SpiritwebCapability spiritweb = (SpiritwebCapability) iSpiritweb;

			for(CosmerePowerInstance cosmerePowerInstance: cosmerePowerInstances)
			{
				spiritweb.giveCosmerePower(cosmerePowerInstance);
			}

			if (livingEntity instanceof ServerPlayer serverPlayer)
			{
				spiritweb.syncToClients(serverPlayer);
			}
		});
	}
}