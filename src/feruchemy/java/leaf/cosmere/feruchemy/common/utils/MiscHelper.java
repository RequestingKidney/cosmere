/*
 * File updated ~ 9 - 8 - 2024 ~ Leaf
 */

package leaf.cosmere.feruchemy.common.utils;

import leaf.cosmere.api.IGrantsPowers;
import leaf.cosmere.api.IHasSize;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.api.cosmerePower.CosmerePowerInstance;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.text.TextHelper;
import leaf.cosmere.common.items.GodMetalNuggetItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class MiscHelper
{

	public static void consumeNugget(LivingEntity livingEntity, ItemStack itemStack)
	{
		if (livingEntity.level().isClientSide) return;


		if(itemStack.getItem() instanceof IGrantsPowers manifestingItem && itemStack.getItem() instanceof IHasSize sizeItem)
		{
			Integer size = sizeItem.readMetalAlloySizeNbtData(itemStack);
			if(size != null)
			{
				ArrayList<CosmerePower> powers = manifestingItem.determinePowers(itemStack);
				ArrayList<CosmerePowerInstance> powerInstances = new ArrayList<>();
				for(CosmerePower power : powers)
				{
					powerInstances.add(new CosmerePowerInstance(
							power,
							livingEntity.getUUID(),
							size,
							false
					));
				}
				manifestingItem.grantPowers(livingEntity, powerInstances);
			}

			if (itemStack.getItem() instanceof GodMetalNuggetItem godItem && godItem.getMetalType() == Metals.MetalType.LERASATIUM)
			{
				if (livingEntity instanceof Llama && !livingEntity.hasCustomName())
				{
					//todo translations
					livingEntity.setCustomName(TextHelper.createTranslatedText("Full Feruchemist Llama"));
				}
			}
		}
	}


}
