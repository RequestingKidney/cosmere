/*
 * File updated ~ 19 - 11 - 2023 ~ Leaf
 */

package leaf.cosmere.api.manifestation;

import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.Manifestations;
import leaf.cosmere.api.providers.IManifestationProvider;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class Manifestation implements IManifestationProvider
{
	final protected Manifestations.ManifestationTypes manifestationType;

	public Manifestation()
	{
		this.manifestationType = Manifestations.ManifestationTypes.NONE;
	}

	public Manifestation(Manifestations.ManifestationTypes manifestationType)
	{
		this.manifestationType = manifestationType;
	}

	public Manifestations.ManifestationTypes getManifestationType()
	{
		return manifestationType;
	}

	public int getPowerID()
	{
		return 0;
	}

	public int getActiveTick(ISpiritweb data)
	{
		return data.getLiving().tickCount + getPowerID();
	}

	public boolean isActiveTick(ISpiritweb data)
	{
		return getActiveTick(data) % 20 == 0;
	}

	public void onModeChange(ISpiritweb data, int lastMode)
	{

	}

	public int getMode(ISpiritweb data)
	{
		return data.getMode(this);
	}

	public int getModeModifier(ISpiritweb data, Manifestation manifestation, int requestedModifier)
	{
		return requestedModifier;
	}

	public int modeMax(ISpiritweb data)
	{
		return 0;
	}

	public int modeMin(ISpiritweb data)
	{
		return 0;
	}

	public boolean modeWraps(ISpiritweb data)
	{
		return false;
	}

	/**
	 * @param data : spiritweb of whatever entity that's using this manifestation
	 * @return true if this manifestation should trigger a {@link net.minecraft.world.level.gameevent.GameEvent} for sculk sensors and wardens to pick up
	 * @see leaf.cosmere.common.cap.entity.SpiritwebCapability#tick()
	 */
	@SuppressWarnings("JavadocReference")
	public boolean tick(ISpiritweb data)
	{
		return false;
	}

	protected void applyEffectTick(ISpiritweb data)
	{

	}

	public boolean isActive(ISpiritweb data)
	{
		return data.canTickManifestation(this);
	}

    public void grantManifestation(ISpiritweb spiritweb, int strength)
    {
        LivingEntity entity = spiritweb.getLiving();
        AttributeInstance attributeInstance = entity.getAttribute(getAttribute());
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(strength);
        }
    }

    public void removeManifestation(ISpiritweb spiritweb)
    {
        LivingEntity entity = spiritweb.getLiving();
        AttributeInstance attributeInstance = entity.getAttribute(getAttribute());

        if (attributeInstance != null) {
            attributeInstance.setBaseValue(0);
        }
    }

	public double getStrength(ISpiritweb spiritweb, boolean ignoreTemporaryPowers)
	{
        LivingEntity entity = spiritweb.getLiving();
        AttributeInstance attributeInstance = entity.getAttribute(getAttribute());
        if (attributeInstance != null) {
            return ignoreTemporaryPowers ? attributeInstance.getBaseValue() : attributeInstance.getValue();
        } else {
            return 0;
        }
	}

    public void raiseSkill(ISpiritweb spiritweb, int amount)
    {
        LivingEntity entity = spiritweb.getLiving();
        AttributeInstance attributeInstance = entity.getAttribute(getSkillAttribute());
        if (attributeInstance != null) {
            if ((attributeInstance.getAttribute() instanceof RangedAttribute rangedAttribute)) {
                int currentLevel = (int) attributeInstance.getBaseValue();
                int newLevel = currentLevel + amount;
                if (newLevel > rangedAttribute.getMaxValue()) {
                    newLevel = (int) rangedAttribute.getMaxValue();
                }
                attributeInstance.setBaseValue(newLevel);
            }
        }
    }

    public void lowerSkill(ISpiritweb spiritweb, int amount)
    {
        LivingEntity entity = spiritweb.getLiving();
        AttributeInstance attributeInstance = entity.getAttribute(getSkillAttribute());
        if (attributeInstance != null) {
            if ((attributeInstance.getAttribute() instanceof RangedAttribute rangedAttribute)) {
                int currentLevel = (int) attributeInstance.getBaseValue();
                int newLevel = currentLevel - amount;
                if (newLevel < rangedAttribute.getMinValue()) {
                    newLevel = (int) rangedAttribute.getMinValue();
                }
                attributeInstance.setBaseValue(newLevel);
            }
        }
    }

    public int getSkillLevel(ISpiritweb spiritweb, boolean ignoreTemporaryPowers)
    {
        LivingEntity entity = spiritweb.getLiving();
        AttributeInstance attributeInstance = entity.getAttribute(getSkillAttribute());
        if (attributeInstance != null) {
            return (int) (ignoreTemporaryPowers ? attributeInstance.getBaseValue() : attributeInstance.getValue());
        } else {
            return 0;
        }
    }

	@Override
	public ResourceLocation getRegistryName()
	{
		//May be null if called before the object is registered
		IForgeRegistry<Manifestation> registry = CosmereAPI.manifestationRegistry();
		return registry == null ? null : registry.getKey(this);
	}

	@Override
	public String getTranslationKey()
	{
		ResourceLocation regName = getRegistryName();
		return "manifestation." + regName.getNamespace() + "." + regName.getPath();
	}

	@Override
	public Manifestation getManifestation()
	{
		return this;
	}

	public boolean hasMenu()
	{
		return false;
	}

	public void openMenu(Minecraft minecraft)
	{

	}

	public Attribute getAttribute()
	{
		return ForgeRegistries.ATTRIBUTES.getValue(getRegistryName());
	}

    public Attribute getSkillAttribute()
    {
        ResourceLocation regName = getRegistryName();
        ResourceLocation skillAttributeRL = new ResourceLocation(regName + ".skill");
        IForgeRegistry<Attribute> registry = ForgeRegistries.ATTRIBUTES;
        return registry.getValue(skillAttributeRL);
    }

}