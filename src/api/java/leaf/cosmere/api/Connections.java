package leaf.cosmere.api;

import leaf.cosmere.api.spiritweb.Connection;
import leaf.cosmere.api.text.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Connections
{
    public final static int BLANK_ID = 0;
    public final static int WORLD_ID = 1;
    public final static int PLAYER_ID = 2;
    public final static int ITEM_ID = 3;
    public final static int SHARD_ID = 4;
    public final static int ARTHROPOD_ID = 5;
    public final static int UNDEAD_ID = 6;
    public final static int WATER_ID = 7;
    public final static int VILLAGE_ID = 8;
    public final static int ILLAGE_ID = 9;

    public enum ConnectionType
    {
        BLANK(BLANK_ID),
        WORLD(WORLD_ID),
        PLAYER(PLAYER_ID),
        ITEM(ITEM_ID),
        SHARD(SHARD_ID),
        UNDEAD(UNDEAD_ID),
        ARTHROPOD(ARTHROPOD_ID),
        WATER(WATER_ID),
        VILLAGE(VILLAGE_ID),
        ILLAGE(ILLAGE_ID);

        ConnectionType(int id)
        {
            this.id = id;
        }

        final int id;

        public int getID()
        {
            return id;
        }

        public static Optional<Connections.ConnectionType> valueOf(int value)
        {
            return Arrays.stream(values())
                    .filter(connectionType -> connectionType.id == value)
                    .findFirst();
        }

        public String getName()
        {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public UUID getUUID()
        {
            return UUID.nameUUIDFromBytes(name().getBytes());
        }

        public String getNameFromId(UUID uuid)
        {
            switch(this)
            {
                case BLANK:
                    return "Nothing";
                case WORLD:
                    return "the " + StringHelper.fixCapitalisation(getWorldFromUUID(uuid).toLowerCase());
                case PLAYER:
                    Level level = Minecraft.getInstance().level;
                    if (level == null)
                    {
                        return "???";
                    }
                    Player player = level.getPlayerByUUID(uuid);
                    return player != null ? player.getName().getString() : "???";
                case ITEM:
                    return "Item";
                case SHARD:
                    return StringHelper.fixCapitalisation(Shards.Shard.getFromUUID(uuid).getName());
                case UNDEAD:
                    return "the Undead";
                case ARTHROPOD:
                    return "the Arthropods";
                case WATER:
                    return "the Aquatic";
                case VILLAGE:
                    return "the Villagers";
                case ILLAGE:
                    return "the Illagers";
                default:
                    return "???";
            }
        }

        public static ConnectionType getFromUUID(UUID uuid)
        {
            return Arrays.stream(values())
                    .filter(connectionType -> connectionType.getUUID().equals(uuid))
                    .findFirst()
                    .orElse(BLANK);
        }

        public static Map<UUID, Connection> getDefaultConnections(EntityType entityType)
        {
            if (entityType.equals(EntityType.VILLAGER))
            {
                return Map.of(
                        UUID.nameUUIDFromBytes("OVERWORLD".getBytes()), new Connection(WORLD, 32),
                        VILLAGE.getUUID(), new Connection(VILLAGE, 32)
                );
            } else if (entityType.equals(EntityType.PILLAGER) || entityType.equals(EntityType.ILLUSIONER) ||
                    entityType.equals(EntityType.EVOKER))
            {
                return Map.of(
                        UUID.nameUUIDFromBytes("OVERWORLD".getBytes()), new Connection(WORLD, 32),
                        ILLAGE.getUUID(), new Connection(ILLAGE, 32)
                );
            } else
            {
                return Map.of(
                        BLANK.getUUID(), new Connection(BLANK, 16)
                );
            }
        }

        public static String getWorldFromUUID(UUID uuid)
        {
            List<String> availableWorlds = List.of(new String[] {"OVERWORLD", "NETHER", "END"});

            for (String world : availableWorlds)
            {
                if (UUID.nameUUIDFromBytes(world.getBytes()).equals(uuid))
                {
                    return world;
                }
            }
            return "UNKNOWN";
        }

    }

}