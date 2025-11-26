package leaf.cosmere.api.cosmerePower;

import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.providers.ICosmerePowerProvider;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class CosmerePower implements ICosmerePowerProvider
{
	public CosmerePower getPower()
	{
		return this;
	}

	@Override
	public String getTranslationKey()
	{
		ResourceLocation regName = getRegistryName();
		return "power." + regName.getNamespace() + "." + regName.getPath();
	}

	public void grantPower(ISpiritweb spiritweb)
	{
	}

	public void removePower(ISpiritweb spiritweb)
	{
	}

	public void giveManifestation(Manifestation manifestation, ISpiritweb spiritweb, int baseValue)
	{
		LivingEntity livingEntity = spiritweb.getLiving();
		final Attribute attribute = manifestation.getAttribute();
		if (attribute == null)
		{
			return;
		}
		AttributeInstance manifestationAttribute = livingEntity.getAttribute(attribute);

		if (manifestationAttribute != null)
		{
			manifestationAttribute.setBaseValue(baseValue);
		}

		spiritweb.setHasBeenInitialized(true);
	}

	public void removeManifestation(Manifestation manifestation, ISpiritweb spiritweb)
	{
		LivingEntity livingEntity = spiritweb.getLiving();
		final Attribute attribute = manifestation.getAttribute();
		if (attribute == null)
		{
			return;
		}

		AttributeInstance manifestationAttribute = livingEntity.getAttribute(attribute);
		if (manifestationAttribute != null)
		{
			manifestationAttribute.setBaseValue(0);
		}
	}
}
