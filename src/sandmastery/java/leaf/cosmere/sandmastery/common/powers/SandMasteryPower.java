package leaf.cosmere.sandmastery.common.powers;

import leaf.cosmere.api.Taldain;
import leaf.cosmere.api.cosmerePower.CosmerePower;
import leaf.cosmere.api.manifestation.Manifestation;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import leaf.cosmere.sandmastery.common.registries.SandmasteryAttributes;
import leaf.cosmere.sandmastery.common.registries.SandmasteryManifestations;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public class SandMasteryPower extends CosmerePower
{
	public SandMasteryPower()
	{
		super();
	}

	public void grantPower(ISpiritweb spiritweb, UUID identity, int strength, boolean isModifier)
	{
		this.giveManifestation(spiritweb, identity, strength, isModifier);
	}

	public void removePower(ISpiritweb spiritweb, UUID identity, boolean isModifier)
	{
		this.removeManifestation(spiritweb, identity, isModifier);
	}

	public void giveManifestation(ISpiritweb spiritweb, UUID identity, int strength, boolean isModifier)
	{
		LivingEntity livingEntity = spiritweb.getLiving();
		final Attribute attribute = SandmasteryAttributes.RIBBONS.getAttribute();

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
		final Attribute attribute = SandmasteryAttributes.RIBBONS.getAttribute();

		AttributeInstance manifestationAttribute = livingEntity.getAttribute(attribute);
		if (manifestationAttribute == null) return;

		if (isModifier)
		{
			manifestationAttribute.removeModifier(identity);
		}
		else
		{
			manifestationAttribute.setBaseValue(0);
		}
	}
}
