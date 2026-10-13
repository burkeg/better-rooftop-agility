package com.rooftopobstaclepreview;

import net.runelite.api.coords.WorldPoint;

/** Which obstacle the player last completed, and whether they have set off for the next one. */
final class CourseProgress
{
	private final RooftopObstacle[] obstacles;
	private int landedOn;
	private int lastClicked = -1;
	private WorldPoint anchor;
	private boolean pendingLanding;
	private boolean left;

	// A lap starts where the last obstacle lands the player.
	CourseProgress(RooftopObstacle[] obstacles)
	{
		this.obstacles = obstacles;
		landedOn = obstacles.length - 1;
	}

	/** For a player first seen part-way round. */
	void resumeAfter(int index)
	{
		landedOn = index;
	}

	void clicked(int index)
	{
		if (index >= 0)
		{
			lastClicked = index;
		}
	}

	void xpDrop()
	{
		pendingLanding = true;
	}

	/** A failed obstacle drops the player off the course, so the lap starts over. */
	void fell()
	{
		landedOn = obstacles.length - 1;
		anchor = null;
		pendingLanding = false;
		left = false;
	}

	/** Call once per game tick with the player's tile. */
	void tick(WorldPoint player)
	{
		if (pendingLanding)
		{
			landedOn = landingIndex(player);
			anchor = player;
			pendingLanding = false;
			left = false;
		}
		else if (anchor == null)
		{
			anchor = player;
		}
		// Setting off is leaving after clicking the next obstacle, not walking off for a mark of grace. The xp can
		// drop a tick before the player reaches the landing tile, so arriving there is not leaving either.
		else if (!left && lastClicked == (landedOn + 1) % obstacles.length
			&& !player.equals(anchor) && !player.equals(obstacles[landedOn].landing))
		{
			left = true;
		}
	}

	/** Whether the next obstacle will be clicked from somewhere other than the last landing tile. */
	boolean clickingFromElsewhere(WorldPoint player)
	{
		return !left && !player.equals(obstacles[landedOn].landing);
	}

	/** The index of the obstacle that many clicks away: 0 is the next to click, the last is the one just done. */
	int indexClicksAway(int clicks)
	{
		return (landedOn + (left ? 1 : 0) + 1 + clicks) % obstacles.length;
	}

	private int landingIndex(WorldPoint player)
	{
		int index = RooftopObstacle.landingOn(obstacles, player);
		if (index >= 0)
		{
			return index;
		}
		// Off every landing tile, go by lap order unless the last click says otherwise. A click on the obstacle
		// after the expected one is just the player clicking ahead.
		int expected = (landedOn + 1) % obstacles.length;
		boolean clickFits = lastClicked < 0 || lastClicked == expected || lastClicked == (expected + 1) % obstacles.length;
		return clickFits ? expected : lastClicked;
	}
}
