/*
 * File updated ~ 8 - 10 - 2023 ~ Leaf
 */

package leaf.cosmere.common.registration.impl;

import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.api.providers.ICosmerePowerProvider;
import leaf.cosmere.common.registration.WrappedRegistryObject;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class CosmerePowerRegistryObject<COSMERE_POWER extends CosmerePower> extends WrappedRegistryObject<COSMERE_POWER> implements ICosmerePowerProvider
{
	public CosmerePowerRegistryObject(RegistryObject<COSMERE_POWER> registryObject)
	{
		super(registryObject);
	}

	@NotNull
	@Override
	public COSMERE_POWER getPower()
	{
		return get();
	}

}