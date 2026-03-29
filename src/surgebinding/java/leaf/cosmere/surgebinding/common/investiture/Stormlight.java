package leaf.cosmere.surgebinding.common.investiture;

import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.investiture.KineticInvestiture;
import leaf.cosmere.api.investiture.InvHelpers;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.surgebinding.common.capabilities.SurgebindingSpiritwebSubmodule;
import leaf.cosmere.surgebinding.common.config.SurgebindingConfigs;
import net.minecraft.world.entity.LivingEntity;

public class Stormlight extends KineticInvestiture
{
	public Stormlight(ISpiritweb container,
	                  double beu)
	{
		super(container,
				InvHelpers.Shard.HONOR,
				beu,
				Manifestations.ManifestArrayBuilder.getAllType(Manifestations.ManifestationTypes.SURGEBINDING));
		int decayRate;

		SurgebindingSpiritwebSubmodule ssm = (SurgebindingSpiritwebSubmodule) container.getSubmodule(Manifestations.ManifestationTypes.SURGEBINDING);
		decayRate = (6 - ssm.getIdeal()) * SurgebindingConfigs.SERVER.STORMLIGHT_DRAIN_RATE.get() / 2;
		super.setDecayRate(decayRate);
	}
}
