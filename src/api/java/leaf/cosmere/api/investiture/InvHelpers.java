/*
* File updated ~ 15 - 9 - 25 ~ Soar
 */

package leaf.cosmere.api.investiture;

import leaf.cosmere.api.EnumUtils;
import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.manifestation.Manifestation;
import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

public class InvHelpers
{
	public enum Shard
	{
		NONE(0),
		AUTONOMY(1),
		RUIN(2),
		PRESERVATION(16),
		HARMONY(17),
		HONOR(10),
		CULTIVATION(3),
		ODIUM(9),
		RETRIBUTION(18),
		ENDOWMENT(4),
		VIRTUOSITY(5),
		INVENTION(6),
		VALOR(7),
		WHIMSY(8),
		REASON(11),
		DEVOTION(12),
		DOMINION(13),
		AMBITION(14),
		MERCY(15),
		DOR(19),
		AETHER(20),
		PURE(21);


		final int id;

		Shard(int number)
		{
			this.id = number;
		}

		public static Optional<Shard> valueOf(int value)
		{
			return Arrays.stream(values())
					.filter(shard -> shard.id == value)
					.findFirst();
		}

		public int getID()
		{
			return id;
		}

		public String getName()
		{
			return name().toLowerCase(Locale.ROOT);
		}

		public Shard[] getComponentShards()
		{
			return switch (this)
			{
				case DOR -> new Shard[]{DEVOTION, DOMINION};
				case HARMONY -> new Shard[]{RUIN, PRESERVATION};
				case RETRIBUTION -> new Shard[]{HONOR, ODIUM};
				case PURE -> EnumUtils.SHARDS;
				default -> new Shard[]{this};

			};
		}

		public Manifestations.ManifestationTypes getManifestationType()
		{
			return switch (this)
			{
				case HARMONY -> Manifestations.ManifestationTypes.FERUCHEMY;
				case PRESERVATION -> Manifestations.ManifestationTypes.ALLOMANCY;
				case RUIN -> Manifestations.ManifestationTypes.HEMALURGY;
				case AUTONOMY -> Manifestations.ManifestationTypes.SANDMASTERY;
				case DOR -> Manifestations.ManifestationTypes.AON_DOR;
				case ENDOWMENT -> Manifestations.ManifestationTypes.AWAKENING;
				case HONOR, ODIUM, RETRIBUTION, CULTIVATION -> Manifestations.ManifestationTypes.SURGEBINDING;
				default -> Manifestations.ManifestationTypes.NONE;
			};
		}

		public static Shard getShardOfManifest(Manifestation manifestation)
		{
			return getShardOfManifest(manifestation.getManifestationType());
		}

		public static Shard getShardOfManifest(Manifestations.ManifestationTypes manifestationT)
		{
			return switch (manifestationT)
			{
				case ALLOMANCY -> PRESERVATION;
				case HEMALURGY -> RUIN;
				case SANDMASTERY -> AUTONOMY;
				case AON_DOR -> DOR;
				case AWAKENING -> ENDOWMENT;
				case SURGEBINDING -> HONOR;
				default -> NONE;
			};
		}
	}
	// All possible sources for gaining kinetic investiture
	public enum InvestitureSource
	{
		DIRECT(0),
		MISTS(1),
		GEMSTONE(2),
		HIGHSTORM(3),
		SELF(4),
		LUHEL_BOND(5),
		NAHEL_BOND(6),
		//Hemalurgy
		LIFEFORCE(7),
		PERPENDICULARITY(8);

		final int id;

		InvestitureSource(int id)
		{
			this.id = id;
		}

		public static Optional<InvestitureSource> valueOf(int value)
		{
			return Arrays.stream(values())
					.filter(shard -> shard.id == value)
					.findFirst();
		}

		public int getID()
		{
			return id;
		}

		public String getName()
		{
			return name().toLowerCase(Locale.ROOT);
		}

	}

	public static class InvMath
	{
		public static double beuToStrength(double beu)
		{
			return beu /Constants.beuStrengthRatio;
		}

		public static int strengthToBEU(double strength)
		{
			return (int) (strength*Constants.beuStrengthRatio);
		}

		public static int beuToPower(double beu)
		{
			return (int) (beu/Constants.beuPowerRatio);
		}

		public static double powerToBEU(int power)
		{
			return power * Constants.beuPowerRatio;
		}

	}

	public static class Constants
	{
		//Tick Counters
		//containers clean up.
		public static final int containerTick = 39;
		//investiture is transfered.
		public static final int transferTick = 19;

		//KineticInvestiture objects decay
		//KineticInvestiture calculates next second's max investiture drain.
		public static final int investitureTick = 2;



		public static final int beuStrengthRatio = 15;
		public static final int beuPowerRatio = 20;
	}

	public static class TransferHelper
	{
		public static HashSet<Transferer> transferers = new HashSet<>();

		public static void addTransferer(Transferer transferer)
		{
			transferers.add(transferer);
		}

		protected static void removeTransferer(Transferer transferer)
		{
			transferers.remove(transferer);
		}

		public static void transferAll()
		{
			for(Transferer transferer: transferers)
			{
				transferer.transfer();
			}
		}

		public static void clean()
		{
			transferers.removeIf(transferer -> transferer.getInvestIn() == null || transferer.getInvestOut() == null);
		}

		@SubscribeEvent
		public static void tick()
		{
			if(Minecraft.getInstance().level.getServer().getTickCount() % 40 == 19)
			{
				transferAll();
				clean();
			}
		}
	}
}
