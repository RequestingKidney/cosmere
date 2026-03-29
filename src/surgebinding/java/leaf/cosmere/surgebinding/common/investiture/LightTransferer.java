package leaf.cosmere.surgebinding.common.investiture;

import leaf.cosmere.api.investiture.KineticInvestiture;
import leaf.cosmere.api.investiture.Transferer;

public class LightTransferer extends Transferer
{
	public LightTransferer(KineticInvestiture investIn, IInvContainer containerOut, int transferRate, double decayRate, int cycles)
	{
		super(investIn, getInvestiture(investIn, containerOut, transferRate, decayRate), transferRate, cycles, containerOut.getMaxBEU());
	}

	private static KineticInvestiture getInvestiture(KineticInvestiture investIn, IInvContainer containerOut, int transferRate, double decayRate)
	{
		if(investIn instanceof Stormlight light)
		{
			return new Stormlight(containerOut, transferRate);
		}
		else if(investIn instanceof Towerlight light)
		{
			return new Towerlight(containerOut, transferRate);
		}
		else if(investIn instanceof Voidlight light)
		{
			return new Voidlight(containerOut, transferRate);
		}
		else
		{
			return new KineticInvestiture(containerOut,
								   investIn.getShard(),
								   investIn.getContainer().containerSource(),
								   transferRate,
								   investIn.getApplicableManifestations(),
								   decayRate);
		}
	}
}
