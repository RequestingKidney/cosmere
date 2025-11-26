package leaf.cosmere.api.cosmerePower;

import leaf.cosmere.api.Constants;
import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.EnumUtils;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.helpers.CompoundNBTHelper;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class CosmerePowerInstance
{
	private CosmerePower power = null;
	private UUID identity = Constants.NBT.UNKEYED_UUID;
	private double strength = 0;
	private boolean isModifier = false;

	public CosmerePowerInstance() {}

	public CosmerePowerInstance(CosmerePower power, UUID identity, double strength, boolean isModifier)
	{
		this.power = power;
		this.identity = identity;
		this.strength = strength;
		this.isModifier = isModifier;
	}

	public static CosmerePowerInstance deserialize(CompoundTag powerTag)
	{
		CosmerePowerInstance cosmerePowerInstance = new CosmerePowerInstance();
		if(powerTag == null) return cosmerePowerInstance;

		String powerId = powerTag.getString("powerId");
		cosmerePowerInstance.power = CosmereAPI.cosmerePowerRegistry().getValue(new ResourceLocation(powerId));

		cosmerePowerInstance.identity = powerTag.getUUID("powerIdentity");
		cosmerePowerInstance.strength = powerTag.getDouble("powerStrength");
		cosmerePowerInstance.isModifier = powerTag.getBoolean("powerModifier");

		return cosmerePowerInstance;
	}

	public CompoundTag serialize()
	{
		if(power == null) return null;
		CompoundTag powerTag = new CompoundTag();

		powerTag.putString("powerId", power.getRegistryName().toString());
		powerTag.putUUID("powerIdentity", identity);
		powerTag.putDouble("powerStrength", strength);
		powerTag.putBoolean("powerModifier", isModifier);

		return powerTag;
	}

	public CosmerePower getPower()
	{
		return power;
	}

	public void setPower(CosmerePower power)
	{
		this.power = power;
	}

	public UUID getIdentity()
	{
		return identity;
	}

	public void setIdentity(UUID identity)
	{
		this.identity = identity;
	}

	public double getStrength()
	{
		return strength;
	}

	public void setStrength(double strength)
	{
		this.strength = strength;
	}

	public boolean isModifier()
	{
		return isModifier;
	}

	public void setModifier(boolean modifier)
	{
		isModifier = modifier;
	}
}
