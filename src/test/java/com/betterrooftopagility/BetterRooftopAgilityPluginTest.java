package com.betterrooftopagility;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class BetterRooftopAgilityPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(BetterRooftopAgilityPlugin.class);
		RuneLite.main(args);
	}
}
