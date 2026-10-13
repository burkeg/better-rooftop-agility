package com.betterrooftopagility;

import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "Better Rooftop Agility",
	description = "Shows where each rooftop obstacle's clickbox will be when it is your next click, coloured by click order",
	tags = {"agility", "rooftop", "clickbox", "overlay", "course"}
)
public class BetterRooftopAgilityPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private EventBus eventBus;

	@Inject
	private OverlayManager overlayManager;

	private ObstacleTracker tracker;
	private GhostClickboxOverlay overlay;

	@Override
	protected void startUp()
	{
		tracker = new ObstacleTracker(client);
		overlay = new GhostClickboxOverlay(client, tracker);
		eventBus.register(tracker);
		clientThread.invoke(tracker::scan);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		eventBus.unregister(tracker);
	}
}
