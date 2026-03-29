package leaf.cosmere.surgebinding.common.investiture;

import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.investiture.KineticInvestiture;
import leaf.cosmere.api.investiture.InvHelpers;
import leaf.cosmere.api.spiritweb.ISpiritweb;

public class Voidlight extends KineticInvestiture
{
	public Voidlight(ISpiritweb container,
	                 int beu)
	{
		//As we start implementing voidbinding, add that to the list of manifestations.
		//Not much is known about voidlight, but it's here for the future.
		super(container,
				InvHelpers.Shard.ODIUM,
				beu,
				Manifestations.ManifestArrayBuilder.getAllType(Manifestations.ManifestationTypes.SURGEBINDING));
	}
}
