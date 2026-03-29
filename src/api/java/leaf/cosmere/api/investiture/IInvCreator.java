package leaf.cosmere.api.investiture;

import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;

public interface IInvCreator
{

	KineticInvestiture newInvest(ISpiritweb data);

	KineticInvestiture newInvest(ISpiritweb data, double beu, double decay);

	KineticInvestiture newInvest(ISpiritweb data, double beu);

}
