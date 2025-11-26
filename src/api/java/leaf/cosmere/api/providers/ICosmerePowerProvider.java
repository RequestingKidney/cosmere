package leaf.cosmere.api.providers;

import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.cosmerePower.CosmerePower;
import net.minecraft.resources.ResourceLocation;

public interface ICosmerePowerProvider extends IBaseProvider
{
	CosmerePower getPower();

	@Override
	default ResourceLocation getRegistryName()
	{
		return CosmereAPI.cosmerePowerRegistry().getKey(getPower());
	}

	@Override
	default String getTranslationKey()
	{
		return getPower().getTranslationKey();
	}
}
