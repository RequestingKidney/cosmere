package leaf.cosmere.allomancy.common.powers;

import leaf.cosmere.allomancy.common.Allomancy;
import leaf.cosmere.allomancy.common.registries.AllomancyManifestations;
import leaf.cosmere.api.Metals.MetalType;
import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.common.registry.ManifestationRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class AllomancyPower extends CosmerePower
{
	private final MetalType metalType;

	public AllomancyPower(MetalType metalType)
	{
		super();
		this.metalType = metalType;
	}

	public void grantPower(ISpiritweb spiritweb, UUID identity, int strength, boolean isModifier)
	{
		this.giveManifestation(spiritweb, identity, strength, isModifier);
	}

	public void removePower(ISpiritweb spiritweb, UUID identity, boolean isModifier)
	{
		this.removeManifestation(spiritweb, identity, isModifier);
	}

	public MetalType getMetalType()
	{
		return metalType;
	}

	public void giveManifestation(ISpiritweb spiritweb, UUID identity, int strength, boolean isModifier)
	{
		LivingEntity livingEntity = spiritweb.getLiving();
		Manifestation manifestation = AllomancyManifestations.ALLOMANCY_MANIFESTATIONS.get(metalType).get();
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

	public void removeManifestation(ISpiritweb spiritweb, UUID identity, boolean isModifier)
	{
		LivingEntity livingEntity = spiritweb.getLiving();
		Manifestation manifestation = AllomancyManifestations.ALLOMANCY_MANIFESTATIONS.get(metalType).get();
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
