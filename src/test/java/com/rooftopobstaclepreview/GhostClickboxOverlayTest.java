package com.rooftopobstaclepreview;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import org.junit.Test;

public class GhostClickboxOverlayTest
{
	@Test
	public void coloursRunFromRedForNextToBlueForJustDone()
	{
		assertEquals(new Color(255, 0, 0, 90), GhostClickboxOverlay.orderColour(0, 7));
		assertEquals(new Color(0, 255, 0, 90), GhostClickboxOverlay.orderColour(3, 7));
		assertEquals(new Color(0, 0, 255, 90), GhostClickboxOverlay.orderColour(6, 7));
	}
}
