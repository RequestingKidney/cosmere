package leaf.cosmere.feruchemy.common.registries;

import leaf.cosmere.api.EnumUtils;
import leaf.cosmere.api.Metals;
import leaf.cosmere.common.registration.impl.CosmerePowerDeferredRegister;
import leaf.cosmere.common.registration.impl.CosmerePowerRegistryObject;
import leaf.cosmere.feruchemy.common.Feruchemy;
import leaf.cosmere.feruchemy.common.powers.FeruchemyPower;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FeruchemyPowers
{
	public static final CosmerePowerDeferredRegister POWERS = new CosmerePowerDeferredRegister(Feruchemy.MODID);

	public static final Map<Metals.MetalType, CosmerePowerRegistryObject<FeruchemyPower>> FERUCHEMY_POWERS =
			Arrays.stream(EnumUtils.METAL_TYPES)
					.filter(Metals.MetalType::hasAssociatedManifestation)
					.collect(Collectors.toMap(
							Function.identity(),
							metalType ->
									POWERS.register(
											metalType.getName(),
											() -> new FeruchemyPower(metalType))
					));
}
