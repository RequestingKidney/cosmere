package leaf.cosmere.api;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class Connections
{
    public final static int BLANK_ID = 0;
    public final static int WORLD_ID = 1;
    public final static int ENTITY_ID = 2;
    public final static int ITEM_ID = 3;
    public final static int SHARD_ID = 4;

    public enum ConnectionType
    {
        BLANK(BLANK_ID),
        WORLD(WORLD_ID),
        ENTITY(ENTITY_ID),
        ITEM(ITEM_ID),
        SHARD(SHARD_ID);

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

    }

}