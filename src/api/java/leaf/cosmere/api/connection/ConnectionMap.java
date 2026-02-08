package leaf.cosmere.api.connection;

import leaf.cosmere.api.Connections;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

public class ConnectionMap
{
    private final Map<UUID, Connection> connections = new HashMap<>();
    private boolean isDirty = false;

    public boolean isDirty()
    {
        return isDirty;
    }

    public void clean()
    {
        isDirty = false;
    }

    public boolean hasConnectionToTarget(UUID connectionTarget)
    {
        return connections.values().stream().anyMatch(connection -> connection.getConnectionTarget().equals(connectionTarget));
    }

    public int getConnectionStrength(Connection connection)
    {
        return connections.values().stream()
                .filter(conn -> conn.equals(connection))
                .flatMapToInt((conn -> IntStream.of(conn.getStrength())))
                .sum();
    }

    public boolean hasConnection(Connection connection)
    {
        return connections.values().stream().anyMatch(conn -> conn.equals(connection));
    }

    public Map<UUID, Connection> getMap()
    {
        return connections;
    }

    public void grantConnection(UUID id, Connection connection)
    {
        connections.put(id, connection);
        isDirty = true;
    }

    public void removeConnection(UUID id)
    {
        connections.remove(id);
        isDirty = true;
    }

    public void modifyConnection(UUID id, int amount)
    {
        if(connections.containsKey(id))
        {
            int newStrength = connections.get(id).getStrength() + amount;
            if(newStrength <= 0)
            {
                removeConnection(id);
            }
            else
            {
                connections.get(id).setStrength(newStrength);
            }
        }
        isDirty = true;
    }

    public CompoundTag save() {
        CompoundTag connectionsNbt = new CompoundTag();
        connections.forEach((mapId, connection) -> {
            CompoundTag connectionNbt = new CompoundTag();
            connectionNbt.putString("connectionTarget", connection.getConnectionTarget().toString());
            connectionNbt.putInt("connectionType", connection.getConnectionType().getID());
            connectionNbt.putInt("connectionStrength", connection.getStrength());
            connectionsNbt.put(mapId.toString(), connectionNbt);
        });
        return connectionsNbt;
    }

    public void load(CompoundTag connectionsNbt) {
        connections.clear();
        connectionsNbt.getAllKeys().forEach(id -> {
            CompoundTag connectionNbt = connectionsNbt.getCompound(id);
            UUID mapId = UUID.fromString(id);

            Connection connection = new Connection(UUID.fromString(connectionNbt.getString("connectionTarget")),
                    Connections.ConnectionType.valueOf(connectionNbt.getInt("connectionType")).get(),
                    connectionNbt.getInt("connectionStrength"));

            connections.put(mapId, connection);
        });
        isDirty = true;
    }
}
