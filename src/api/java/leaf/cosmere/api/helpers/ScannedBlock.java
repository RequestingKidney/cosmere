package leaf.cosmere.api.helpers;

import dev.ryanhcode.sable.companion.ClientSubLevelAccess;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

public record ScannedBlock(BlockPos pos, @Nullable ClientSubLevelAccess subLevel)
{
	public ScannedBlock immutable()
	{
		return new ScannedBlock(pos.immutable(), subLevel);
	}
}
