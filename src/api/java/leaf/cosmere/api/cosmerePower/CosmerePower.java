package leaf.cosmere.api.cosmerePower;

import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.providers.ICosmerePowerProvider;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

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

	public void grantPower(ISpiritweb spiritweb, UUID identity, int strength, boolean isModifier)
	{
	}

	public void removePower(ISpiritweb spiritweb, UUID identity, boolean isModifier)
	{
	}

	public void giveManifestation(Manifestation manifestation, ISpiritweb spiritweb, UUID identity, int strength, boolean isModifier)
	{
		LivingEntity livingEntity = spiritweb.getLiving();
		final Attribute attribute = manifestation.getAttribute();
		if (attribute == null) return;

		AttributeInstance manifestationAttribute = livingEntity.getAttribute(attribute);
		if(manifestationAttribute == null) return;

		if(isModifier)
		{
			AttributeModifier modifier = new AttributeModifier(
					identity.toString(),
					strength,
					AttributeModifier.Operation.ADDITION
			);
			manifestationAttribute.addTransientModifier(modifier);
		}
		else
		{
			manifestationAttribute.setBaseValue(strength);
		}

		spiritweb.setHasBeenInitialized(true);
	}

	public void removeManifestation(Manifestation manifestation, ISpiritweb spiritweb, UUID identity, boolean isModifier)
	{
		LivingEntity livingEntity = spiritweb.getLiving();
		final Attribute attribute = manifestation.getAttribute();
		if (attribute == null) return;

		AttributeInstance manifestationAttribute = livingEntity.getAttribute(attribute);
		if(manifestationAttribute == null) return;


		if(isModifier)
		{
			manifestationAttribute.removeModifier(identity);
		}
		else
		{
			manifestationAttribute.setBaseValue(0);
		}
	}
}
