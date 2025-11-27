package leaf.cosmere.api;

import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.api.cosmerePower.CosmerePowerInstance;
import leaf.cosmere.api.manifestation.Manifestation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public interface IGrantsPowers
{
	public ArrayList<CosmerePower> determinePowers(ItemStack itemStack);

	public void grantPowers(LivingEntity livingEntity, ArrayList<CosmerePowerInstance> cosmerePowerInstances);
}
