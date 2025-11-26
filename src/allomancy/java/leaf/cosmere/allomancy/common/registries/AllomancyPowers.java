package leaf.cosmere.allomancy.common.registries;

import leaf.cosmere.allomancy.common.Allomancy;
import leaf.cosmere.allomancy.common.powers.AllomancyPower;
import leaf.cosmere.api.EnumUtils;
import leaf.cosmere.api.Metals;
import leaf.cosmere.common.registration.impl.CosmerePowerDeferredRegister;
import leaf.cosmere.common.registration.impl.CosmerePowerRegistryObject;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AllomancyPowers
{
	public static final CosmerePowerDeferredRegister POWERS = new CosmerePowerDeferredRegister(Allomancy.MODID);

	public static final Map<Metals.MetalType, CosmerePowerRegistryObject<AllomancyPower>> ALLOMANCY_POWERS =
			Arrays.stream(EnumUtils.METAL_TYPES)
					.filter(Metals.MetalType::hasAssociatedManifestation)
					.collect(Collectors.toMap(
							Function.identity(),
							metalType ->
									POWERS.register(
											metalType.getName(),
											() -> new AllomancyPower(metalType))
					));
}
