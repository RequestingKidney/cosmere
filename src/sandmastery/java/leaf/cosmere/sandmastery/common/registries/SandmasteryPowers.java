package leaf.cosmere.sandmastery.common.registries;

import leaf.cosmere.common.registration.impl.CosmerePowerDeferredRegister;
import leaf.cosmere.common.registration.impl.CosmerePowerRegistryObject;
import leaf.cosmere.sandmastery.common.Sandmastery;
import leaf.cosmere.sandmastery.common.powers.SandMasteryPower;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SandmasteryPowers
{
	public static final CosmerePowerDeferredRegister POWERS = new CosmerePowerDeferredRegister(Sandmastery.MODID);

	public static final CosmerePowerRegistryObject<SandMasteryPower> SANDMASTERY_POWER = POWERS.register(Sandmastery.MODID, SandMasteryPower::new);

}
