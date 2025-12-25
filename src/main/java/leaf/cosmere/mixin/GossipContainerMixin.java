package leaf.cosmere.mixin;

import leaf.cosmere.api.Connections;
import leaf.cosmere.api.spiritweb.ISpiritweb;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import leaf.cosmere.common.cap.entity.SpiritwebCapability;

import java.util.UUID;
import java.util.function.Predicate;

@Mixin(GossipContainer.class)
public class GossipContainerMixin {

    @Inject(
            method = "getReputation(Ljava/util/UUID;Ljava/util/function/Predicate;)I",
            at = @At("RETURN"),
            cancellable = true
    )
    private void addConnectionBaseReputation(UUID pIdentifier, Predicate<GossipType> pGossip,
                                             CallbackInfoReturnable<Integer> cir)
    {
        int adjustReputation = cir.getReturnValue();

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        ServerPlayer player = server.getPlayerList().getPlayer(pIdentifier);
        if (player == null) return;

        if(!SpiritwebCapability.get(player).isPresent()) return;
        ISpiritweb spiritweb = SpiritwebCapability.get(player).resolve().get();

        if(spiritweb.hasConnectionType(Connections.ConnectionType.VILLAGE)) {
            int connectionLevel = spiritweb.getConnections().get(Connections.ConnectionType.VILLAGE.getUUID()).getStrength();
            adjustReputation += connectionLevel;
        }

        cir.setReturnValue(adjustReputation);
    }
}