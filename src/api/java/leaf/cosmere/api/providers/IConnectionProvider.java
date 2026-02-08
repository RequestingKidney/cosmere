package leaf.cosmere.api.providers;

import leaf.cosmere.api.Connections;
import leaf.cosmere.api.CosmereAPI;
import leaf.cosmere.api.connection.Connection;
import leaf.cosmere.api.cosmereEffect.CosmereEffect;
import net.minecraft.resources.ResourceLocation;

public interface IConnectionProvider extends IBaseProvider
{
    Connection getConnection();

    @Override
    default ResourceLocation getRegistryName()
    {
        return CosmereAPI.connectionRegistry().getKey(getConnection());
    }

    String getTranslationKey();
}
