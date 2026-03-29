package leaf.cosmere.feruchemy.common.investiture;

import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.manifestation.Manifestation;

public class FNicrosilCharge extends FeruCharge
{
	SpiritwebInvestiture stored;
	public FNicrosilCharge(IInfuseContainer<?> container, double beu, IInvContainer<?> infuser, Manifestation storedType, int mode )
	{
		super(container, Manifestations.ManifestationTypes.FERUCHEMY.getManifestation(11), beu, infuser);
		stored = new SpiritwebInvestiture(container, storedType, infuser.getSpiritweb().orElse(null), mode);
	}

	@Override
	public double getBEU()
	{
		return stored.getBEU() * super.getBEU();
	}


}
