package com.rooftopobstaclepreview;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RooftopObstaclePreviewPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RooftopObstaclePreviewPlugin.class);
		RuneLite.main(args);
	}
}
