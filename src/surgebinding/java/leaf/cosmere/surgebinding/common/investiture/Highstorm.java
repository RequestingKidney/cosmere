package leaf.cosmere.surgebinding.common.investiture;

import leaf.cosmere.api.helpers.TimeHelper;
import leaf.cosmere.api.investiture.KineticInvestiture;
import leaf.cosmere.surgebinding.common.registries.SurgebindingDimensions;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Highstorm implements IInvCreator
{

	public static Highstorm highstorm = new Highstorm();
	private double highstormX;
	private final double HOME = 0;

	public Highstorm()
	{
		highstormX = 17750D;
	}

	@SubscribeEvent
	public void tick()
	{
		if(TimeHelper.isNthTick(SurgebindingDimensions.ROSHAR_DIM_KEY, 1,5D))
		{
			if(highstormX + 3 > HOME + 35500D)
			{
				highstormX = HOME;
			}
			else
			{
				highstormX += 15;
			}
		}

	}

	public HighstormLocation getNearestHighstorm(Entity entity)
	{
		Vec3 lastChecked = new Vec3(highstormX, entity.position().y(), entity.position().z());
		double home = 0;
		int num = 0;
		while(!entity.position().closerThan(lastChecked,17750D))
		{
			if((entity.position().x() - lastChecked.x()) > 0)
			{
				lastChecked = new Vec3(
						highstormX + 35500,
						entity.position().y(),
						entity.position().z());
				num++;
				home += 35500;
			}
			else
			{
				lastChecked = new Vec3(
						highstormX - 35500,
						entity.position().y(),
						entity.position().z());
				num--;
				home -= 35500;
			}
		}
		return new HighstormLocation(lastChecked, home, num);

	}

	public static boolean isHighstorm(Entity entity)
	{
		if(entity.level().dimension().equals(SurgebindingDimensions.ROSHAR_DIM_KEY))
		{
			if(Math.abs(entity.position().x - highstorm.highstormX) < 50.0)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public KineticInvestiture newInvest(IInvContainer data)
	{
		KineticInvestiture investiture = new Stormlight(data, 500);
		return investiture;
	}

	@Override
	public KineticInvestiture newInvest(IInfuseContainer data, double beu, double decay)
	{
		KineticInvestiture out = new Stormlight(data, beu);
		out.setDecayRate(decay);
		return out;
	}

	@Override
	public KineticInvestiture newInvest(IInfuseContainer data, double beu)
	{
		return new Stormlight(data, beu);
	}

	public class HighstormLocation
	{
		public double home;
		public int number;
		public Vec3 pos;

		public HighstormLocation(double x, double home, int number)
		{
			pos = new Vec3(x, 0, 0);
			this.home = home;
			this.number = number;
		}

		public HighstormLocation(Vec3 pos, double home, int number)
		{

		}

		public Position getPosition()
		{
			return pos;
		}

		public void setPos(Vec3 pos)
		{
			this.pos = pos;
		}

		public void setYZ(Entity entity)
		{
			pos = new Vec3(pos.x(), entity.position().z(), entity.position().z());
		}
	}
}
