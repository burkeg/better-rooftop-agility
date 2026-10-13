package com.betterrooftopagility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import net.runelite.api.coords.WorldPoint;
import org.junit.Test;

public class CourseProgressTest
{
	// Three obstacles: 0 lands on A, 1 on B and the last on C, where a lap starts.
	private static final WorldPoint A = new WorldPoint(10, 10, 3);
	private static final WorldPoint B = new WorldPoint(30, 20, 3);
	private static final WorldPoint C = new WorldPoint(40, 40, 0);

	private final CourseProgress progress = new CourseProgress(new RooftopObstacle[]{
		new RooftopObstacle(A, 1), new RooftopObstacle(B, 2), new RooftopObstacle(C, 3)});

	@Test
	public void lapStartsAtTheFirstObstacle()
	{
		progress.tick(C);
		assertEquals(0, next());
	}

	@Test
	public void setsOffOnLeavingAfterClickingNext()
	{
		progress.tick(C);
		progress.clicked(0);
		assertEquals(0, next());

		progress.tick(C.dx(1));
		assertEquals(1, next());
		assertEquals(0, progress.indexClicksAway(2));
	}

	@Test
	public void walkingOffWithoutClickingNextIsNotSettingOff()
	{
		progress.tick(C);
		assertFalse(progress.clickingFromElsewhere(C));

		progress.tick(C.dx(1));
		assertEquals(0, next());
		assertTrue(progress.clickingFromElsewhere(C.dx(1)));

		progress.clicked(0);
		progress.tick(C.dx(2));
		assertEquals(1, next());
		assertFalse(progress.clickingFromElsewhere(C.dx(2)));
	}

	@Test
	public void holdsThroughTheObstacleAndOnItsLandingTile()
	{
		progress.tick(C);
		progress.clicked(0);
		progress.tick(C.dx(1));
		progress.tick(new WorldPoint(12, 12, 1));
		assertEquals(1, next());

		land(A);
		progress.tick(A);
		assertEquals(1, next());

		progress.clicked(1);
		progress.tick(A.dy(1));
		assertEquals(2, next());
	}

	@Test
	public void landingTileIdentifiesTheObstacle()
	{
		progress.tick(C);
		land(B);
		assertEquals(2, next());
	}

	@Test
	public void unknownLandingTileFollowsLapOrder()
	{
		progress.tick(C);
		land(A.dx(1));
		assertEquals(1, next());
		assertTrue(progress.clickingFromElsewhere(A.dx(1)));
		assertFalse(progress.clickingFromElsewhere(A));
	}

	@Test
	public void clickOnAnotherObstacleOverridesLapOrder()
	{
		progress.tick(B);
		progress.clicked(2);
		land(C.dx(1));
		assertEquals(0, next());
	}

	@Test
	public void clickingAheadKeepsLapOrder()
	{
		progress.tick(C);
		progress.clicked(1);
		land(A.dx(1));
		assertEquals(1, next());
	}

	@Test
	public void reachingTheLandingTileATickLateIsNotLeaving()
	{
		progress.tick(C);
		land(A.dy(-1));
		progress.clicked(1);
		progress.tick(A);
		assertEquals(1, next());

		progress.tick(A.dy(1));
		assertEquals(2, next());
	}

	@Test
	public void lastObstacleWrapsToTheFirst()
	{
		progress.tick(B);
		land(C);
		assertEquals(0, next());

		progress.clicked(0);
		progress.tick(C.dx(1));
		assertEquals(1, next());
	}

	@Test
	public void fallRestartsTheLap()
	{
		progress.tick(C);
		land(A);
		progress.clicked(1);

		WorldPoint fallen = new WorldPoint(5, 5, 0);
		progress.fell();
		progress.tick(fallen);
		assertEquals(0, next());
		assertTrue(progress.clickingFromElsewhere(fallen));

		progress.tick(fallen.dx(1));
		assertEquals(0, next());

		progress.clicked(0);
		progress.tick(fallen.dx(2));
		assertEquals(1, next());
	}

	@Test
	public void resumesPartWayRound()
	{
		progress.resumeAfter(0);
		progress.tick(A.dy(4));
		assertEquals(1, next());
		assertTrue(progress.clickingFromElsewhere(A.dy(4)));

		progress.clicked(1);
		progress.tick(A.dy(5));
		assertEquals(2, next());
	}

	private int next()
	{
		return progress.indexClicksAway(0);
	}

	private void land(WorldPoint tile)
	{
		progress.xpDrop();
		progress.tick(tile);
	}
}
