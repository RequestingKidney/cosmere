package leaf.cosmere.common.util;

import leaf.cosmere.api.Manifestations.ManifestationTypes;
import leaf.cosmere.api.Metals;
import leaf.cosmere.api.Roshar;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.ForgeRegistries;

public class CosmereAttributeUtils
{
	public static Attribute getAttribute(ManifestationTypes manifestationType, int powerId)
	{
		switch (manifestationType)
		{
			case ALLOMANCY:
			case FERUCHEMY:
				return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(
						manifestationType.getName(),
						Metals.MetalType.valueOf(powerId).get().getName()));
			case SURGEBINDING:
				return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(
						manifestationType.getName(),
						Roshar.Surges.valueOf(powerId).get().getName()
				));
			case SANDMASTERY:
				return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(
						manifestationType.getName(),
						"ribbons"
				));
			case AVIAR:
				return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(
						manifestationType.getName(),
						"hostile_life_sense"
				));
			default:
				return null;
		}
	}

    public static int getAttributeId(Attribute attribute)
    {
        ManifestationTypes manifestationType = getManifestationType(attribute);
        switch (manifestationType)
        {
            case ALLOMANCY:
            case FERUCHEMY:
                return Metals.MetalType.valueOf(attribute.getDescriptionId().split("\\.")[2].toUpperCase()).getID();
            case SURGEBINDING:
                return Roshar.Surges.valueOf(attribute.getDescriptionId().split("\\.")[2].toUpperCase()).getID();
            default:
                return 0;
        }
    }

	public static Attribute getAttributeById(String id)
	{
		String[] attributeSections = id.split("\\.");
		return ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(
				attributeSections[1],
				attributeSections[2]
		));
	}

	public static ManifestationTypes getManifestationType(Attribute attribute)
	{
		if(attribute == null) return ManifestationTypes.NONE;
		String modId = attribute.getDescriptionId().split("\\.")[1];

		for (ManifestationTypes manifestationType : ManifestationTypes.values())
		{
			if (manifestationType.getName().equals(modId))
			{
				return manifestationType;
			}
		}
		return ManifestationTypes.NONE;
	}

    public static void removeBaseAttribute(LivingEntity livingEntity, Attribute attribute)
    {
        AttributeInstance entityAttributeInstance = livingEntity.getAttribute(attribute);
        if (entityAttributeInstance == null)
        {
            return;
        }
        entityAttributeInstance.setBaseValue(0);
    }

	public static void addToBaseAttribute(LivingEntity livingEntity, RangedAttribute attribute, int strength)
	{
		AttributeInstance entityAttributeInstance = livingEntity.getAttribute(attribute);
		if (entityAttributeInstance == null)
		{
			return;
		}

        int currentStrength = (int) entityAttributeInstance.getValue();

		// Let's ensure not to exceed the base value if it's out of range,
		// even if it will get sanitized
		int newStrength = strength + currentStrength;
		if (newStrength < attribute.getMinValue())
		{
			newStrength = (int) attribute.getMinValue();
		}
		else if (newStrength > attribute.getMaxValue())
		{
			newStrength = (int) attribute.getMaxValue();
		}

		entityAttributeInstance.setBaseValue(newStrength);
	}

    public static void subtractFromBaseAttribute(LivingEntity livingEntity, RangedAttribute attribute, int strength)
    {
        AttributeInstance entityAttributeInstance = livingEntity.getAttribute(attribute);
        if (entityAttributeInstance == null)
        {
            return;
        }

        int currentStrength = (int) entityAttributeInstance.getValue();

        // Let's ensure not to exceed the base value if it's out of range,
        // even if it will get sanitized
        int newStrength = currentStrength - strength;
        if (newStrength < attribute.getMinValue())
        {
            newStrength = (int) attribute.getMinValue();
        }
        else if (newStrength > attribute.getMaxValue())
        {
            newStrength = (int) attribute.getMaxValue();
        }

        entityAttributeInstance.setBaseValue(newStrength);
    }
}
