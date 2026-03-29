package leaf.cosmere.feruchemy.common.investiture;

import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.investiture.*;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.common.investiture.Infusion;

public class FeruCharge extends Infusion
{

	public FeruCharge(IInfuseContainer<?> container, Manifestation manifest, double beu, IInvContainer infuser)
	{
		super(container, Manifestations.ManifestArrayBuilder.getArray(manifest), beu, infuser,
			InvHelpers.Shard.HARMONY, InvHelpers.InvestitureSource.SELF);
	}

}
