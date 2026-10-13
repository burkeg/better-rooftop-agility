package com.rooftopobstaclepreview;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.util.Arrays;
import net.runelite.api.Client;
import net.runelite.api.DecorativeObject;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Model;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Renderable;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * Draws each obstacle's clickbox where it will be on screen when it is the next to click: as seen from the tile the
 * obstacle before it lands the player on. The camera orbits a focal point above the player, so that view is today's
 * camera looking at the obstacle's model shifted by (focal point now - focal point then).
 */
class GhostClickboxOverlay extends Overlay
{
	private static final int STEADY_FRAMES = 15;
	private static final int MAX_OFF_AXIS = 16;
	private static final float BLUE_HUE = 240f / 360f;
	private static final int FILL_ALPHA = 90;
	private static final int BORDER_ALPHA = 150;
	private static final Color SHARED_TRIM = new Color(255, 200, 0);

	private final Client client;
	private final ObstacleTracker tracker;

	// The focal point is distanceRatio * (600 + 3 * pitch) ahead of the camera and followPerScale * zoom above the
	// player's ground. Client.getCameraFocalPoint only works in free-camera mode, so both are measured whenever the
	// player and camera are at rest.
	private double distanceRatio = 1.25;
	private double followPerScale = 0.082;
	private double[] lastCamera = new double[3];
	private int steadyFrames;
	private boolean measured;

	GhostClickboxOverlay(Client client, ObstacleTracker tracker)
	{
		this.client = client;
		this.tracker = tracker;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Player player = client.getLocalPlayer();
		Course course = tracker.getCourse();
		if (player == null || course == null || tracker.getObjects().isEmpty())
		{
			return null;
		}

		double pitch = client.getCameraFpPitch();
		double yaw = client.getCameraFpYaw();
		double[] forward = {-Math.sin(yaw) * Math.cos(pitch), Math.cos(yaw) * Math.cos(pitch), Math.sin(pitch)};
		double[] camera = {client.getCameraFpX(), client.getCameraFpY(), client.getCameraFpZ()};
		double formulaDistance = 600 + 3 * pitch / Perspective.UNIT;

		boolean atRest = cameraAtRest(camera) && tracker.getTicksStill() >= 2;
		if (atRest && !measured)
		{
			measureCamera(camera, forward, formulaDistance, player.getLocalLocation());
		}
		measured = atRest;

		double distance = distanceRatio * formulaDistance;
		double[] focus = new double[3];
		Arrays.setAll(focus, axis -> camera[axis] + distance * forward[axis]);

		int count = course.obstacles.length;
		int next = tracker.indexClicksAway(0);
		boolean fromElsewhere = tracker.clickingFromElsewhere();
		Area[] boxes = new Area[count];
		Arrays.setAll(boxes, index -> new Area());
		for (TileObject object : tracker.getObjects())
		{
			int index = course.indexOf(object.getId());
			if (index < 0)
			{
				continue;
			}
			// Off the last landing tile and not yet set off, the next obstacle is clicked from where the player
			// stands, so its box goes on the real clickbox.
			int[] shift = fromElsewhere && index == next ? new int[3]
				: shift(course.obstacles[(index + count - 1) % count].landing, focus);
			if (shift != null)
			{
				addClickbox(boxes[index], object, shift[0], shift[1], shift[2]);
			}
		}

		// Coolest first, so the next box ends up on top.
		graphics.setStroke(new BasicStroke(1));
		for (int away = count - 1; away >= 0; away--)
		{
			Area box = boxes[tracker.indexClicksAway(away)];
			Color fill = orderColour(away, count);
			graphics.setColor(fill);
			graphics.fill(box);
			graphics.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), BORDER_ALPHA));
			graphics.draw(box);
		}

		// Gold outline where the next two or more boxes overlap: one mouse position serves all of those clicks.
		graphics.setColor(SHARED_TRIM);
		graphics.setStroke(new BasicStroke(2));
		Area shared = boxes[next];
		for (int away = 1; away < count && !shared.isEmpty(); away++)
		{
			shared.intersect(boxes[tracker.indexClicksAway(away)]);
			graphics.draw(shared);
		}
		return null;
	}

	/** Red for the obstacle to click next, through orange, green and cyan to blue for the one just done. */
	static Color orderColour(int clicksAway, int count)
	{
		Color colour = Color.getHSBColor(count <= 1 ? 0 : BLUE_HUE * clicksAway / (count - 1), 1, 1);
		return new Color(colour.getRed(), colour.getGreen(), colour.getBlue(), FILL_ALPHA);
	}

	/** How far the focal point is from where it will be with the player on the tile, or null if that is off the map. */
	private int[] shift(WorldPoint tile, double[] focus)
	{
		// Not the WorldPoint overload, which returns null for a tile on another plane than the player's.
		LocalPoint local = LocalPoint.fromWorld(client.getTopLevelWorldView(), tile.getX(), tile.getY());
		if (local == null)
		{
			return null;
		}
		double height = Perspective.getTileHeight(client, local, tile.getPlane()) - followPerScale * client.getScale();
		return new int[]{(int) Math.round(focus[0] - local.getX()), (int) Math.round(focus[1] - local.getY()),
			(int) Math.round(focus[2] - height)};
	}

	/** Adds the object's clickbox as if it stood (dx, dy, dz) local units from where it is. */
	private void addClickbox(Area box, TileObject object, int dx, int dy, int dz)
	{
		int x = object.getX() + dx;
		int y = object.getY() + dy;
		int z = object.getZ() + dz;
		if (object instanceof GameObject)
		{
			GameObject gameObject = (GameObject) object;
			add(box, object, gameObject.getRenderable(), gameObject.getModelOrientation(), x, y, z);
		}
		else if (object instanceof WallObject)
		{
			add(box, object, ((WallObject) object).getRenderable1(), 0, x, y, z);
			add(box, object, ((WallObject) object).getRenderable2(), 0, x, y, z);
		}
		else if (object instanceof DecorativeObject)
		{
			DecorativeObject decoration = (DecorativeObject) object;
			add(box, object, decoration.getRenderable(), 0, x + decoration.getXOffset(), y + decoration.getYOffset(), z);
			add(box, object, decoration.getRenderable2(), 0, x, y, z);
		}
		else if (object instanceof GroundObject)
		{
			add(box, object, ((GroundObject) object).getRenderable(), 0, x, y, z);
		}
	}

	private void add(Area box, TileObject object, Renderable renderable, int orientation, int x, int y, int z)
	{
		if (renderable == null)
		{
			return;
		}
		Model model = renderable instanceof Model ? (Model) renderable : renderable.getModel();
		Shape clickbox = Perspective.getClickbox(client, object.getWorldView(), model, orientation, x, y, z);
		if (clickbox != null)
		{
			box.add(new Area(clickbox));
		}
	}

	private boolean cameraAtRest(double[] camera)
	{
		boolean moved = false;
		for (int axis = 0; axis < 3; axis++)
		{
			moved |= Math.abs(camera[axis] - lastCamera[axis]) > 0.01;
		}
		lastCamera = camera;
		steadyFrames = moved ? 0 : steadyFrames + 1;
		return steadyFrames >= STEADY_FRAMES;
	}

	// At rest the camera's axis passes over the player: how far along gives the camera distance, and the height
	// there gives the follow height.
	private void measureCamera(double[] camera, double[] forward, double formulaDistance, LocalPoint at)
	{
		int ground = Perspective.getTileHeight(client, at, client.getTopLevelWorldView().getPlane());
		double along = ((at.getX() - camera[0]) * forward[0] + (at.getY() - camera[1]) * forward[1])
			/ (forward[0] * forward[0] + forward[1] * forward[1]);
		double offAxis = Math.hypot(camera[0] + along * forward[0] - at.getX(), camera[1] + along * forward[1] - at.getY());
		double ratio = along / formulaDistance;
		double perScale = (ground - (camera[2] + along * forward[2])) / client.getScale();
		if (offAxis <= MAX_OFF_AXIS && ratio > 0.5 && ratio < 3 && perScale > 0.02 && perScale < 0.3)
		{
			distanceRatio = ratio;
			followPerScale += 0.3 * (perScale - followPerScale);
		}
	}
}
