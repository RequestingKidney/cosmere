/*
 * File updated ~ 8 - 1 - 2025 ~ Leaf
 */

package leaf.cosmere.mixin;

import leaf.cosmere.api.math.MathHelper;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;
import leaf.cosmere.common.eventHandlers.EntityEventHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.npc.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public class VillagerMixin
{
	@Inject(method = "getBreedOffspring(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/AgeableMob;)Lnet/minecraft/world/entity/npc/Villager;", at = @At("RETURN"))
	private void getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent, CallbackInfoReturnable<Villager> cir)
	{
		Villager offspring = cir.getReturnValue();
		Villager thisParent = (Villager) (Object) this;

		SpiritwebCapability.get(thisParent).ifPresent(thisParentSW ->
		{
			final SpiritwebCapability cap = (SpiritwebCapability) thisParentSW;
			if (cap.hasAnyPowers())
			{
				SpiritwebCapability.get(pOtherParent).ifPresent(pOtherParentSW ->
				{
					final SpiritwebCapability otherCap = (SpiritwebCapability) pOtherParentSW;
					if (otherCap.hasAnyPowers())
					{
						SpiritwebCapability.get(offspring).ifPresent(offspringSW ->
						{
							final SpiritwebCapability offspringCap = (SpiritwebCapability) offspringSW;
							offspringCap.clearManifestations();
							offspringCap.setHasBeenInitialized(true);

							if (MathHelper.randomBool())//todo config for chance for offspring to also have powers
							{
								EntityEventHandler.giveEntityStartingManifestation(offspring, offspringCap);
							}
						});
					}
				});
			}
		});
	}
}
