/*
 * File updated ~ 27 - 10 - 2023 ~ Leaf
 */

package leaf.cosmere.common.registration.impl;

import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.api.providers.ICosmerePowerProvider;
import leaf.cosmere.common.registration.WrappedDeferredRegister;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class CosmerePowerDeferredRegister extends WrappedDeferredRegister<CosmerePower>
{
	private final List<ICosmerePowerProvider> powersInRegistry = new ArrayList<>();


	public CosmerePowerDeferredRegister(String modid)
	{
		super(modid, CosmereAPI.cosmerePowerRegistryName());
	}

	public <COSMERE_POWER extends CosmerePower> CosmerePowerRegistryObject<COSMERE_POWER> register(String name, Supplier<? extends COSMERE_POWER> sup)
	{
		final CosmerePowerRegistryObject<COSMERE_POWER> registeredItem = register(name, sup, CosmerePowerRegistryObject::new);
		powersInRegistry.add(registeredItem);
		return registeredItem;
	}


	public List<ICosmerePowerProvider> getPowersInRegistry()
	{
		return Collections.unmodifiableList(powersInRegistry);
	}
}
