package leaf.cosmere.api.connection;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class ConnectionInstance
{
    private final Connection connection;
    private final Map<UUID, AttributeModifier> modifiers = new HashMap<>();
    private double baseValue = 0;

    public ConnectionInstance(Connection connection)
    {
        this.connection = connection;
    }

    public ConnectionInstance(Connection connection, int baseValue)
    {
        this.connection = connection;
        this.baseValue = baseValue;
    }

    public double getBaseValue()
    {
        return baseValue;
    }

    public void setBaseValue(double baseValue)
    {
        this.baseValue = baseValue;
    }

    public Connection getConnection()
    {
        return connection;
    }

    public Map<UUID, AttributeModifier> getModifiers()
    {
        return modifiers;
    }
}
