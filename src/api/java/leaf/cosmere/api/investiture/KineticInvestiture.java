/*
* File updated ~ 15 - 9 - 25 ~ Soar
 */

package leaf.cosmere.api.investiture;

import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nonnull;

public class KineticInvestiture implements IInvestiture
{

	private ISpiritweb container;
	private InvHelpers.Shard shard;
	private Manifestation[] applicableManifestations;
	private int priority = 1;
	private double beu;
	private double decayRate;
	private double currentMaxDraw;

	private CompoundTag nbt;


	public KineticInvestiture(@Nonnull ISpiritweb container,
	                          InvHelpers.Shard shard,
	                          double beu,
	                          Manifestation[] applicableManifestations,
	                          double decayRate)
	{

		this.beu = beu;
		this.applicableManifestations = applicableManifestations;
		this.container = container;
		this.container.mergeOrAddInvestiture(this);
		this.decayRate = decayRate;
		this.shard = shard;
	}

	public KineticInvestiture(@Nonnull ISpiritweb container,
	                          InvHelpers.Shard shard,
	                          double beu,
	                          Manifestation[] applicableManifestations)
	{

		this.beu = beu;
		this.applicableManifestations = applicableManifestations;
		this.container = container;
		this.container.mergeOrAddInvestiture(this);
		this.shard = shard;
		this.decayRate = 0;
	}


	public double getBEU()
	{
		return beu;
	}

	public void setBEU(double beu)
	{
		this.beu = beu;
	}

	/*
	public int removeBEU(int remove)

	{
		if (remove > beu)
		{
			int ret = remove - beu;
			beu = 0;
			return ret;
		}
		else
		{
			beu -= remove;
			return 0;
		}
	}
	*/

	public void addBEU(double add)
	{
		beu += add;
	}

	public double removeBEU(double remove)
	{
		return removeBEU(remove, false);
	}

	public double removeBEU(double remove, boolean upToCurrent)
	{
			if (remove > beu)
			{
				double out = beu;
				beu = 0;
				return out;
			}
			else if (upToCurrent && remove > currentMaxDraw)
			{
				beu -= currentMaxDraw;
				return currentMaxDraw;
			}
			else
			{
				beu -= remove;
				return remove;
			}
	}

	public double drain()
	{
		double temp = beu;
		beu = 0;
		return temp;
	}

	public double getDecayRate()
	{
		return decayRate;
	}

	public void setDecayRate(double decayRate)
	{
		this.decayRate = decayRate;
	}

	public void decay()
	{
		beu -= decayRate;
	}

	public int getPriority(){return priority;}

	public void setPriority(int priority)
	{
		this.priority = priority;
	}

	public Manifestation[] getApplicableManifestations()
	{
		return applicableManifestations;
	}

	@Override
	public InvHelpers.Shard getShard()
	{
		return shard;
	}


	public ISpiritweb getContainer()
	{
		return container;
	}

	public double getCurrentMaxDraw()
	{
		return currentMaxDraw;
	}
	@Override
	public void calculateCurrentMaxDraw()
	{
		int x = 0;
		for(Manifestation manifest: applicableManifestations)
		{
			if(container != null && manifest.isActive(container))
			{
				x++;
			}
		}
		currentMaxDraw = beu/x;
	}



	public boolean isUsable(Manifestation manifest1)
	{
		for(Manifestation manifest: applicableManifestations)
		{
			if(manifest.equals(manifest1))
			{
				return true;
			}
		}
		return false;
	}

	public boolean merge(KineticInvestiture other)
	{
		if(this == other)
		{
			return false;
		}
		if(this.getPriority() == other.getPriority()
			&& this.shard == other.shard
			&& this.getContainer().equals(other.getContainer()))
		{
			this.beu += other.getBEU();
			other.setBEU(0);
			return true;
		}
		return false;
	}




	public void reattach()
	{
		if(!container.hasInvestiture(this))
		{
			container.mergeOrAddInvestiture(this);
		}
	}

	public CompoundTag serializeNBT()
	{
		if (this.nbt == null)
		{
			this.nbt = new CompoundTag();
		}
		nbt.putString("shard", shard.getName().toLowerCase());
		nbt.putInt("manifestations_length", applicableManifestations.length);
		final CompoundTag manifestationNBT = new CompoundTag();
		for (int i = 0; i < applicableManifestations.length; i++)
		{
			manifestationNBT.putInt(applicableManifestations[i].getRegistryName().toString(), i);
		}
		nbt.put("manifestations", manifestationNBT);
		nbt.putInt("priority", priority);
		nbt.putDouble("decay_rate", decayRate);
		nbt.putDouble("beu", beu);

		return nbt;
	}

	public void deserializeNBT(CompoundTag nbt)
	{
		this.nbt = nbt;
		decayRate = nbt.getDouble("decay_rate");
		beu = nbt.getDouble("beu");
		shard = InvHelpers.Shard.valueOf(nbt.getString("shard"));
		priority = nbt.getInt("priority");
		applicableManifestations = new Manifestation[nbt.getInt("manifestations_length")];
		CompoundTag manifestNBT = nbt.getCompound("manifestations");
		for (Manifestation manifestation : CosmereAPI.manifestationRegistry())
		{
			final String manifestationLoc = manifestation.getRegistryName().toString();

			if (manifestNBT.contains(manifestationLoc))
			{
				applicableManifestations[manifestNBT.getInt(manifestationLoc)] = manifestation;
			}
		}


	}

	public static KineticInvestiture buildFromNBT(CompoundTag nbt, ISpiritweb data)
	{
		Manifestation[] array = new Manifestation[nbt.getInt("manifestations_length")];
		CompoundTag manifestNBT = nbt.getCompound("manifestations");
		for (Manifestation manifestation : CosmereAPI.manifestationRegistry())
		{
			final String manifestationLoc = manifestation.getRegistryName().toString();

			if (manifestNBT.contains(manifestationLoc))
			{
				array[manifestNBT.getInt(manifestationLoc)] = manifestation;
			}
		}
		KineticInvestiture invest = new KineticInvestiture(data,
				InvHelpers.Shard.valueOf(nbt.getString("shard")),
				nbt.getDouble("beu"),array, nbt.getDouble("decay_rate"));
		invest.nbt = nbt;
		invest.setPriority(nbt.getInt("priority"));
		return invest;

	}
}
