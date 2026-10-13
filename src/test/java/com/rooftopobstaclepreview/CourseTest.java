package com.rooftopobstaclepreview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;
import org.junit.Test;

public class CourseTest
{
	@Test
	public void findsTheCourseThePlayerIsOnOrBeside()
	{
		assertEquals(Course.ARDOUGNE, Course.near(new WorldPoint(2671, 3299, 3)));
		assertEquals(Course.DRAYNOR, Course.near(new WorldPoint(3103, 3279, 0)));
		assertEquals(Course.SEERS, Course.near(new WorldPoint(2729, 3489, 0)));
		// The bank south of Ardougne market.
		assertNull(Course.near(new WorldPoint(2655, 3283, 0)));
	}

	@Test
	public void coursesDoNotOverlap()
	{
		for (Course course : Course.values())
		{
			for (RooftopObstacle obstacle : course.obstacles)
			{
				assertEquals(course, Course.near(obstacle.landing));
			}
		}
	}

	@Test
	public void eitherFaladorLedgeIsTheSameStep()
	{
		assertEquals(10, Course.FALADOR.indexOf(ObjectID.ROOFTOPS_FALADOR_LEDGE_3A));
		assertEquals(10, Course.FALADOR.indexOf(ObjectID.ROOFTOPS_FALADOR_LEDGE_3B));
		assertEquals(13, Course.FALADOR.obstacles.length);
	}

	@Test
	public void playerOnARoofLastCompletedTheObstacleThatLandsOnIt()
	{
		// Ardougne's first roof, from its far end.
		assertEquals(0, Course.ARDOUGNE.lastCompletedAt(new WorldPoint(2672, 3309, 3),
			(from, to) -> to.getX() >= 2671 && to.getX() <= 2672 && to.getY() >= 3299 && to.getY() <= 3309));
		// No obstacle lands on this plane.
		assertEquals(-1, Course.ARDOUGNE.lastCompletedAt(new WorldPoint(2660, 3305, 1), (from, to) -> true));
	}
}
