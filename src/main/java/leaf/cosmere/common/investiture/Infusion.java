package leaf.cosmere.common.investiture;

import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.investiture.IInvestiture;
import leaf.cosmere.api.investiture.InvHelpers;
import leaf.cosmere.api.investiture.KineticInvestiture;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class Infusion implements IInvestiture
{
	private ISpiritweb container;
	private ISpiritweb infuser;
	private InvHelpers.Shard shard;
	private Manifestation[] applicableManifestations;
	private double beu;
	private double currentMaxDraw;

	private CompoundTag nbt;

	public Infusion(ISpiritweb container, Manifestation manifest, List<KineticInvestiture> investitures)
	{

		beu = 0;
		this.container = container;
		for(KineticInvestiture investiture: investitures)
		{
			if(investiture.isUsable(manifest))
			{
				beu += investiture.getBEU();
				investiture.setBEU(0);
				if(this.shard == null)
				{
					this.shard = investiture.getShard();
				}
				else if(investiture.getShard() != this.shard)
				{
					InvHelpers.Shard shard1 = this.shard;
					{
						this.shard = switcher(shard1, investiture.getShard());
					}
				}
			}
		}
		applicableManifestations = new Manifestation[]{manifest};
		infuser =  investitures.get(0).getContainer();
	}

	public Infusion(ISpiritweb container, Manifestation[] manifest, double beu, ISpiritweb infuser,
	                InvHelpers.Shard shard, InvHelpers.InvestitureSource source
	                )
	{
		this.container = container;
		this.infuser = infuser;
		this.applicableManifestations = manifest;
		this.beu = beu;
		this.shard = shard;}

	@Override
	public double getBEU()
	{
		return beu;
	}

	@Override
	public void setBEU(double beu)
	{
		this.beu = beu;
	}

	@Override
	public Manifestation[] getApplicableManifestations()
	{
		return applicableManifestations;
	}

	@Override
	public InvHelpers.Shard getShard()
	{
		return shard;
	}


	@Override
	public ISpiritweb getContainer()
	{
		return container;
	}

	@Override
	public double getCurrentMaxDraw()
	{
		return currentMaxDraw;
	}

	@Override
	public void calculateCurrentMaxDraw()
	{
		currentMaxDraw = Math.min(beu, applicableManifestations[0].maxInvestitureDraw(infuser));
	}

	public boolean merge(Infusion other)
	{
		if(this == other)
		{
			return false;
		}
		if(this.shard == other.shard
				&& this.getContainer().equals(other.getContainer()))
		{
			this.beu += other.getBEU();
			other.setBEU(0);
			return true;
		}
		return false;
	}

	@Override
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
		nbt.putDouble("beu", beu);

		nbt.putInt("infuser", infuser.getLiving().getId());

		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		this.nbt = nbt;
		beu = nbt.getDouble("beu");
		shard = InvHelpers.Shard.valueOf(nbt.getString("shard"));
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
		if (Minecraft.getInstance().level.getEntity(nbt.getInt("infuser")) instanceof LivingEntity entity)
		{
			infuser = SpiritwebCapability.get(entity).resolve().get();
		}
	}

	private static InvHelpers.Shard switcher(InvHelpers.Shard shard1, InvHelpers.Shard shard2)
	{
		int int1 = 0;
		switch (shard1)
		{
			case DOMINION, DEVOTION, DOR:
				int1 += 1;
				break;
			case PRESERVATION, RUIN, HARMONY:
				int1 += 3;
				break;
			case HONOR, ODIUM, RETRIBUTION:
				int1 += 5;
				break;
			case PURE:
				int1 += 20;
				break;
			default:
				int1 += 0;

		}

		switch (shard2)
		{
			case DOMINION, DEVOTION, DOR:
				int1 += 1;
				break;
			case PRESERVATION, RUIN, HARMONY:
				int1 += 3;
				break;
			case HONOR, ODIUM, RETRIBUTION:
				int1 += 5;
				break;
			case PURE:
				int1 += 20;
				break;
			default:
				int1 += 0;

		}

		if (int1>20)
		{
			int1 = 20;
		}
		return switch (int1)
		{
			case 2 -> InvHelpers.Shard.DOR;
			case 6 -> InvHelpers.Shard.HARMONY;
			case 10 -> InvHelpers.Shard.RETRIBUTION;
			case 20 -> InvHelpers.Shard.PURE;
			default -> InvHelpers.Shard.NONE;
		};
	}
//
//	public static Infusion buildFromNBT(CompoundTag nbt, IInfuseContainer<?> container)
//	{
//		Manifestation[] array = new Manifestation[nbt.getInt("manifestations_length")];
//		CompoundTag manifestNBT = nbt.getCompound("manifestations");
//		for (Manifestation manifestation : CosmereAPI.manifestationRegistry())
//		{
//			final String manifestationLoc = manifestation.getRegistryName().toString();
//
//			if (manifestNBT.contains(manifestationLoc))
//			{
//				array[manifestNBT.getInt(manifestationLoc)] = manifestation;
//			}
//		}
//
//		UUID uuid = nbt.getUUID("infuser");
//		Entity infuse = Minecraft.getInstance().level.getServer().getAllLevels().iterator().next().getEntity(uuid);
//
//		Infusion invest = new Infusion(container, array,
//				nbt.getDouble("beu"), Infusor, InvHelpers.Shard.valueOf(nbt.getString("shard")),
//				InvHelpers.InvestitureSource.valueOf(nbt.getString("source")));
//		invest.nbt = nbt;
//		return invest;
//	}
}
