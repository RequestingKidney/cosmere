package leaf.cosmere.sandmastery.common.investiture;

import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.investiture.KineticInvestiture;
import leaf.cosmere.api.investiture.InvHelpers;

public class SandmasteryInvestiture extends KineticInvestiture
{
	public SandmasteryInvestiture(IInvContainer<?> container, int BEU)
	{
		super(container,
				InvHelpers.Shard.AUTONOMY,
				InvHelpers.InvestitureSource.LUHEL_BOND,
				BEU,
				Manifestations.ManifestArrayBuilder.getAllType(Manifestations.ManifestationTypes.SANDMASTERY));
	}

}
