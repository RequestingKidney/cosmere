/*
 * File updated ~ 15 - 9 - 2025 ~ Soar
 */

package leaf.cosmere.api;

import leaf.cosmere.api.investiture.InvHelpers;
import net.minecraft.core.Direction;

//constant static enum arrays for enums in api that get used often. Save memory allocation where possible
public class EnumUtils
{
	//cosmere
	public static final Metals.MetalType[] METAL_TYPES = Metals.MetalType.values();

	//roshar
	public static final Roshar.RadiantOrder[] RADIANT_ORDERS = Roshar.RadiantOrder.values();
	public static final Roshar.Gemstone[] GEMSTONE_TYPES = Roshar.Gemstone.values();
	public static final Roshar.GemSize[] GEM_SIZES = Roshar.GemSize.values();
	public static final Roshar.Surges[] SURGES = Roshar.Surges.values();

	//investiture
	public static final InvHelpers.Shard[] SHARDS = InvHelpers.Shard.values();

	//yes I know it's a vanilla enum
	public static final Direction[] DIRECTIONS = Direction.values();
}
