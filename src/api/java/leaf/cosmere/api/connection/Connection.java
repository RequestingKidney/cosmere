package leaf.cosmere.api.connection;

import leaf.cosmere.api.Connections;
import leaf.cosmere.api.providers.IConnectionProvider;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class Connection implements IConnectionProvider
{
    private final UUID connectionTarget;
    private final Connections.ConnectionType connectionType;
    private int strength;

    public Connection(UUID connectionTarget, Connections.ConnectionType connectionType, int strength)
    {
        this.connectionTarget = connectionTarget;
        this.connectionType = connectionType;
        this.strength = strength;
    }

    public Connections.ConnectionType getConnectionType()
    {
        return connectionType;
    }

    public UUID getConnectionTarget()
    {
        return connectionTarget;
    }

    public int getStrength()
    {
        return strength;
    }

    public void setStrength(int strength)
    {
        this.strength = strength;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Connection that = (Connection) obj;

        if(!connectionTarget.equals(that.connectionTarget)) return false;
        return connectionType == that.connectionType;
    }

    @Override
    public Connection getConnection()
    {
        return null;
    }

    @Override
    public String getTranslationKey()
    {
        ResourceLocation regName = getRegistryName();
        return "connection." + regName.getNamespace() + "." + regName.getPath();
    }
}