/*
 * File updated ~ 5 - 3 - 2025 ~ Leaf
 */

package leaf.cosmere.common.registry;

import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.common.Cosmere;
import leaf.cosmere.common.registration.impl.CosmerePowerDeferredRegister;
import leaf.cosmere.common.registration.impl.CosmerePowerRegistryObject;
import net.minecraft.resources.ResourceLocation;

public class CosmerePowersRegistry
{
	public static final CosmerePowerDeferredRegister POWERS = new CosmerePowerDeferredRegister(Cosmere.MODID);
	//Cosmere library mod registers the cosmere effect registry that all sub mods can then add their effects to

	public static final CosmerePowerRegistryObject<CosmerePower> NONE = POWERS.register("none", CosmerePower::new);

	public static CosmerePower fromID(String location)
	{
		ResourceLocation resourceLocation = new ResourceLocation(location);
		return fromID(resourceLocation);
	}

	public static CosmerePower fromID(ResourceLocation location)
	{
		CosmerePower value = CosmereAPI.cosmerePowerRegistry().getValue(location);
		if (value != null)
		{
			return value;
		}
		return CosmerePowersRegistry.NONE.get();
	}
}
