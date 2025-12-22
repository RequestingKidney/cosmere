package leaf.cosmere.api.spiritweb;

import leaf.cosmere.api.Connections;

public class Connection
{
    private final Connections.ConnectionType connectionType;
    private int strength;

    public Connection(Connections.ConnectionType connectionType, int strength)
    {
        this.connectionType = connectionType;
        this.strength = strength;
    }

    public Connections.ConnectionType getConnectionType()
    {
        return connectionType;
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

        if (strength != that.strength) return false;
        return connectionType == that.connectionType;
    }
}