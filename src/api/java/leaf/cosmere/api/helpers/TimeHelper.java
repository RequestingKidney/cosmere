/*
 * File updated ~ 12 - 10 - 2022 ~ Leaf
 */

package leaf.cosmere.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import javax.swing.text.html.parser.Entity;

public class TimeHelper
{
	public static double MinutesToSeconds(double minutes)
	{
		return minutes * 60;
	}
	public static int SecondsToTicks(double seconds)
	{
		return (int)(seconds * 20);
	}

	public static double TicksToSeconds(int ticks)
	{
		return (double) ticks / 20.0;
	}

	public static double SecondsToMinutes(double seconds)
	{
		return seconds / 60;
	}

	public static long getTick(ResourceKey<Level> pDimension)
	{
		return Minecraft.getInstance().level.getServer().getLevel(pDimension).getGameTime();
	}

	public static long getTick(LivingEntity entity)
	{
		return entity.tickCount;
	}

	public static boolean isNthTick(long tick, int n, double ofNumSeconds)
	{
		return tick % SecondsToTicks(ofNumSeconds) == n;
	}

	public static boolean isNthTick(ResourceKey<Level> pDimension, int n, double ofNumSeconds)
	{
		return isNthTick(getTick(pDimension), n, ofNumSeconds);
	}

	public static boolean isNthTick(LivingEntity entity, int n, double ofNumSeconds)
	{
		return isNthTick(getTick(entity), n, ofNumSeconds);
	}

	public static boolean isNthTick(long tick, int n)
	{
		return isNthTick(tick, n, 1D);
	}

	public static boolean isNthTick(ResourceKey<Level> pDimension, int n)
	{
		return isNthTick(pDimension, n, 1D);
	}

	public static boolean isNthTick(LivingEntity entity, int n)
	{
		return isNthTick(entity, n, 1D);
	}
}
