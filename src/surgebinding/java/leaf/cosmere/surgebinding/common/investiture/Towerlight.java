package leaf.cosmere.surgebinding.common.investiture;

import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.investiture.KineticInvestiture;
import leaf.cosmere.api.investiture.InvHelpers;
import leaf.cosmere.api.spiritweb.ISpiritweb;

public class Towerlight extends KineticInvestiture
{
	public Towerlight(ISpiritweb container,
	                  double beu)
	{
		super(container,
				InvHelpers.Shard.HONOR,
				beu,
				Manifestations.ManifestArrayBuilder.getAllType(Manifestations.ManifestationTypes.SURGEBINDING));

		//Decay rate is high, but towerlight constantly renews while in Urithiru.
		//Meaning infinite light in the tower, but none once you leave.
		super.setDecayRate(100000);

	}
}
