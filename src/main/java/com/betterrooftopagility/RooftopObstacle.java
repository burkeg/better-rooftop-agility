package com.betterrooftopagility;

import com.google.common.primitives.Ints;
import net.runelite.api.coords.WorldPoint;

final class RooftopObstacle
{
	// Where the player stands when the obstacle's xp drops.
	final WorldPoint landing;
	private final int[] objectIds;

	RooftopObstacle(WorldPoint landing, int... objectIds)
	{
		this.landing = landing;
		this.objectIds = objectIds;
	}

	boolean is(int objectId)
	{
		return Ints.contains(objectIds, objectId);
	}

	/** The index of the obstacle that lands on the tile, or -1. */
	static int landingOn(RooftopObstacle[] obstacles, WorldPoint tile)
	{
		for (int index = 0; index < obstacles.length; index++)
		{
			if (obstacles[index].landing.equals(tile))
			{
				return index;
			}
		}
		return -1;
	}
}
