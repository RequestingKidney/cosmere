package leaf.cosmere.common.items;

import leaf.cosmere.api.Constants;
import leaf.cosmere.api.IHasMetalType;
import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.helpers.StackNBTHelper;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.api.text.TextHelper;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import leaf.cosmere.common.config.CosmereConfigs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public abstract class InvestableItemBase extends BaseItem
{
	public InvestableItemBase(Properties prop)
	{
		super(prop);
	}

	public IInfuseContainer<ItemStack> getAsContainer(ItemStack stack)
	{
		return !InfusionContainer.get(stack).isPresent() ? null : InfusionContainer.get(stack).resolve().get();
	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, Level world)
	{
		return Integer.MAX_VALUE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
	{
		String attunedPlayerName = getAttunedPlayerName(stack);
		UUID attunedPlayer = getAttunedPlayer(stack);
		if (attunedPlayer != null)
		{
			tooltip.add(TextHelper.createText(attunedPlayerName));
		}
		tooltip.add(TextHelper.createText(String.format("%s/%s", getCharge(stack), getMaxCharge(stack))).withStyle(ChatFormatting.GRAY));
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack)
	{
		return getCharge(stack) > 1;
	}

	public int getCharge(ItemStack stack)
	{
		return getAsContainer(stack).currentBEU();
	}

	public int getMaxCharge(ItemStack stack)
	{
		final int maxCharge = CosmereConfigs.SERVER_CONFIG.CHARGEABLE_MAX_VALUE.get();
		return Mth.floor(maxCharge * getMaxChargeModifier()) * stack.getCount();
	}

	public float getMaxChargeModifier()
	{
		return 1;
	}

	public boolean trySetAttunedPlayer(ItemStack itemStack, Player entity)
	{
		if (itemStack.getItem() instanceof IHasMetalType metalType)
		{
			UUID attunedPlayerID = getAttunedPlayer(itemStack);
			UUID playerID = entity.getUUID();
			boolean noAttunedPlayer = attunedPlayerID == null;

			//only allow unkeyed metalminds if they aren't aluminum
			if (noAttunedPlayer && metalType.getMetalType() != Metals.MetalType.ALUMINUM)
			{
				//No attuned player! Check to see whether they are storing identity
				boolean isStoringIdentity = false;
				{
					Optional<ISpiritweb> data = SpiritwebCapability.get(entity).filter(obj -> true);
					if (data.isPresent()) {
						isStoringIdentity = Manifestations.ManifestationTypes.FERUCHEMY.getManifestation(Metals.MetalType.ALUMINUM.getID()).getMode(data.get()) > 0;
					}
				}

				//if they are
				if (isStoringIdentity)
				{
					//then set the metalmind to "unsealed". Any feruchemist with access to that power can use the metalmind
					StackNBTHelper.setUuid(itemStack, Constants.NBT.ATTUNED_PLAYER, Constants.NBT.UNKEYED_UUID);
					StackNBTHelper.setString(itemStack, Constants.NBT.ATTUNED_PLAYER_NAME, "Unkeyed"); // todo translation
					return true;

				}
			}

			//if theres no attuned player on the metalmind
			//or if the player is attuned to the metalmind
			//or if the metalmind is unsealed (anyone can access)
			if (noAttunedPlayer || attunedPlayerID.compareTo(playerID) == 0 || attunedPlayerID.compareTo(Constants.NBT.UNKEYED_UUID) == 0)
			{
				if (noAttunedPlayer && getCharge(itemStack) > 0)
				{
					setAttunedPlayer(itemStack, entity);
					setAttunedPlayerName(itemStack, entity);
				}
				//auto success if that player is already attuned
				return true;
			}

		}
		return false;
	}

	private void setAttunedPlayer(ItemStack itemStack, Player entity)
	{
		StackNBTHelper.setUuid(itemStack, Constants.NBT.ATTUNED_PLAYER, entity.getUUID());
	}

	protected UUID getAttunedPlayer(ItemStack itemStack)
	{
		return StackNBTHelper.getUuid(itemStack, Constants.NBT.ATTUNED_PLAYER);
	}

	private void setAttunedPlayerName(ItemStack itemStack, Player entity)
	{
		String playerName = entity.getDisplayName().getString();
		StackNBTHelper.setString(itemStack, Constants.NBT.ATTUNED_PLAYER_NAME, playerName);
	}

	protected String getAttunedPlayerName(ItemStack itemStack)
	{
		return StackNBTHelper.getString(itemStack, Constants.NBT.ATTUNED_PLAYER_NAME, "");
	}

	public boolean getPlayerIsAttuned(ItemStack itemStack, Player entity)
	{
		//todo clean up
		final MobEffect aluminumStoreEffect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation("feruchemy", "storing_" + Metals.MetalType.ALUMINUM.getName()));
		assert aluminumStoreEffect != null;
		MobEffectInstance storingIdentityEffect = entity.getEffect(aluminumStoreEffect);
		boolean noIdentityPlayer = storingIdentityEffect != null && storingIdentityEffect.getDuration() > 0;

		UUID itemAttunedPlayerUUID = getAttunedPlayer(itemStack);
		//null means not attuned at all, so can assume player is attuned with it
		return noIdentityPlayer || itemAttunedPlayerUUID == null || itemAttunedPlayerUUID == entity.getUUID();
	}
	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entityItem)
	{
		InvestitureContainer.get(stack).ifPresent(cap ->
		{
			cap.setEntityAssociate(entityItem);
		});
		return super.onEntityItemUpdate(stack, entityItem);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected)
	{
		InvestitureContainer.get(stack).ifPresent(cap ->
		{
			cap.setEntityAssociate(entity);
		});
		super.inventoryTick(stack, level, entity, slot, selected);
	}
}
