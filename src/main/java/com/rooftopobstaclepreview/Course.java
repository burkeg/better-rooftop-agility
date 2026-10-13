package com.rooftopobstaclepreview;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiPredicate;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ObjectID;

/** A rooftop course: its bounds, and its obstacles in lap order with the tile each lands the player on. */
enum Course
{
	DRAYNOR(3088, 3255, 3103, 3279,
		step(3102, 3279, 3, ObjectID.ROOFTOPS_DRAYNOR_WALLCLIMB),
		step(3090, 3276, 3, ObjectID.ROOFTOPS_DRAYNOR_TIGHTROPE_1),
		step(3092, 3266, 3, ObjectID.ROOFTOPS_DRAYNOR_TIGHTROPE_2),
		step(3088, 3261, 3, ObjectID.ROOFTOPS_DRAYNOR_WALLCROSSING),
		step(3088, 3255, 3, ObjectID.ROOFTOPS_DRAYNOR_WALLSCRAMBLE),
		step(3096, 3256, 3, ObjectID.ROOFTOPS_DRAYNOR_LEAPDOWN),
		step(3103, 3261, 0, ObjectID.ROOFTOPS_DRAYNOR_CRATE)),
	AL_KHARID(3265, 3163, 3318, 3195,
		step(3273, 3192, 3, ObjectID.ROOFTOPS_KHARID_WALLCLIMB),
		step(3272, 3172, 3, ObjectID.ROOFTOPS_KHARID_TIGHTROPE_1),
		step(3284, 3166, 3, ObjectID.ROOFTOPS_KHARID_ROPE_SWING),
		step(3315, 3163, 1, ObjectID.ROOFTOPS_KHARID_SLIDE_SIDE),
		step(3317, 3174, 2, ObjectID.ROOFTOPS_KHARID_BAMBOO_TREE_TOP),
		step(3316, 3180, 3, ObjectID.ROOFTOPS_KHARID_WALLCLIMB_2),
		step(3302, 3186, 3, ObjectID.ROOFTOPS_KHARID_TIGHTROPE_4),
		step(3299, 3194, 0, ObjectID.ROOFTOPS_KHARID_LEAPDOWN)),
	VARROCK(3192, 3397, 3240, 3417,
		step(3219, 3414, 3, ObjectID.ROOFTOPS_VARROCK_WALLCLIMB),
		step(3208, 3414, 3, ObjectID.ROOFTOPS_VARROCK_CLOTHESLINE),
		step(3197, 3416, 1, ObjectID.ROOFTOPS_VARROCK_LEAPTORUINS),
		step(3192, 3406, 3, ObjectID.ROOFTOPS_VARROCK_WALLSWING),
		step(3193, 3398, 3, ObjectID.ROOFTOPS_VARROCK_WALLSCRAMBLE),
		step(3218, 3399, 3, ObjectID.ROOFTOPS_VARROCK_LEAPTOBALCONY),
		step(3236, 3403, 3, ObjectID.ROOFTOPS_VARROCK_LEAPDOWN),
		step(3236, 3410, 3, ObjectID.ROOFTOPS_VARROCK_STEPUPROOF),
		step(3236, 3417, 0, ObjectID.ROOFTOPS_VARROCK_FINISH)),
	CANIFIS(3478, 3475, 3510, 3505,
		step(3506, 3492, 2, ObjectID.ROOFTOPS_CANIFIS_START_TREE),
		step(3502, 3504, 2, ObjectID.ROOFTOPS_CANIFIS_JUMP),
		step(3492, 3504, 2, ObjectID.ROOFTOPS_CANIFIS_JUMP_2),
		step(3479, 3499, 3, ObjectID.ROOFTOPS_CANIFIS_JUMP_5),
		step(3478, 3486, 2, ObjectID.ROOFTOPS_CANIFIS_JUMP_3),
		step(3489, 3476, 3, ObjectID.ROOFTOPS_CANIFIS_POLEVAULT),
		step(3510, 3476, 2, ObjectID.ROOFTOPS_CANIFIS_JUMP_4),
		step(3510, 3485, 0, ObjectID.ROOFTOPS_CANIFIS_LEAPDOWN)),
	// Only ledge 3B's route was recorded, and the last jump's landing tile varies with where it starts.
	FALADOR(3011, 3332, 3050, 3364,
		step(3036, 3342, 3, ObjectID.ROOFTOPS_FALADOR_WALLCLIMB),
		step(3047, 3344, 3, ObjectID.ROOFTOPS_FALADOR_TIGHTROPE_1),
		step(3050, 3357, 3, ObjectID.ROOFTOPS_FALADOR_HANDHOLDS_START),
		step(3048, 3361, 3, ObjectID.ROOFTOPS_FALADOR_GAP_1),
		step(3041, 3361, 3, ObjectID.ROOFTOPS_FALADOR_GAP_2),
		step(3028, 3354, 3, ObjectID.ROOFTOPS_FALADOR_TIGHTROPE_2),
		step(3020, 3353, 3, ObjectID.ROOFTOPS_FALADOR_TIGHTROPE_3),
		step(3018, 3349, 3, ObjectID.ROOFTOPS_FALADOR_GAP_3),
		step(3014, 3346, 3, ObjectID.ROOFTOPS_FALADOR_LEDGE_1),
		step(3013, 3342, 3, ObjectID.ROOFTOPS_FALADOR_LEDGE_2),
		step(3015, 3334, 3, ObjectID.ROOFTOPS_FALADOR_LEDGE_3A, ObjectID.ROOFTOPS_FALADOR_LEDGE_3B),
		step(3019, 3334, 3, ObjectID.ROOFTOPS_FALADOR_LEDGE_4),
		step(3029, 3334, 0, ObjectID.ROOFTOPS_FALADOR_EDGE)),
	SEERS(2700, 3461, 2729, 3496,
		step(2729, 3491, 3, ObjectID.ROOFTOPS_SEERS_WALLCLIMB),
		step(2713, 3494, 2, ObjectID.ROOFTOPS_SEERS_JUMP),
		step(2710, 3480, 2, ObjectID.ROOFTOPS_SEERS_TIGHTROPE),
		step(2710, 3472, 3, ObjectID.ROOFTOPS_SEERS_JUMP_1),
		step(2702, 3465, 2, ObjectID.ROOFTOPS_SEERS_JUMP_2),
		step(2704, 3464, 0, ObjectID.ROOFTOPS_SEERS_LEAPDOWN)),
	POLLNIVNEACH(3349, 2962, 3369, 3001,
		step(3351, 2964, 1, ObjectID.ROOFTOPS_POLLNIVNEACH_BASKET),
		step(3352, 2973, 1, ObjectID.ROOFTOPS_POLLNIVNEACH_MARKETSTALL),
		step(3360, 2977, 1, ObjectID.ROOFTOPS_POLLNIVNEACH_HANGINGBANNER),
		step(3366, 2976, 1, ObjectID.ROOFTOPS_POLLNIVNEACH_GAP),
		step(3368, 2982, 1, ObjectID.ROOFTOPS_POLLNIVNEACH_TREE),
		step(3365, 2983, 2, ObjectID.ROOFTOPS_POLLNIVNEACH_WALLCLIMB),
		step(3358, 2991, 2, ObjectID.ROOFTOPS_POLLNIVNEACH_MONKEYBARS_START),
		step(3359, 3000, 2, ObjectID.ROOFTOPS_POLLNIVNEACH_TREETOP),
		step(3363, 2998, 0, ObjectID.ROOFTOPS_POLLNIVNEACH_LINE)),
	RELLEKKA(2621, 3654, 2654, 3677,
		step(2625, 3676, 3, ObjectID.ROOFTOPS_RELLEKKA_WALLCLIMB),
		step(2622, 3668, 3, ObjectID.ROOFTOPS_RELLEKKA_GAP_1),
		step(2627, 3654, 3, ObjectID.ROOFTOPS_RELLEKKA_TIGHTROPE_1),
		step(2639, 3653, 3, ObjectID.ROOFTOPS_RELLEKKA_GAP_2),
		step(2643, 3657, 3, ObjectID.ROOFTOPS_RELLEKKA_GAP_3),
		step(2655, 3670, 3, ObjectID.ROOFTOPS_RELLEKKA_TIGHTROPE_3),
		step(2653, 3676, 0, ObjectID.ROOFTOPS_RELLEKKA_DROPOFF)),
	ARDOUGNE(2651, 3296, 2673, 3318,
		step(2671, 3299, 3, ObjectID.ROOFTOPS_ARDY_WALLCLIMB),
		step(2665, 3318, 3, ObjectID.ROOFTOPS_ARDY_JUMP),
		step(2656, 3318, 3, ObjectID.ROOFTOPS_ARDY_PLANK),
		step(2653, 3314, 3, ObjectID.ROOFTOPS_ARDY_JUMP_2),
		step(2651, 3309, 3, ObjectID.ROOFTOPS_ARDY_JUMP_3),
		step(2656, 3297, 3, ObjectID.ROOFTOPS_ARDY_WALLCROSSING),
		step(2668, 3297, 0, ObjectID.ROOFTOPS_ARDY_JUMP_4));

	// How far outside its bounds, in tiles, still counts as on the course.
	private static final int MARGIN = 8;
	private static final int[][] STEPS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

	final RooftopObstacle[] obstacles;
	private final int minX, minY, maxX, maxY;

	Course(int minX, int minY, int maxX, int maxY, RooftopObstacle... obstacles)
	{
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.obstacles = obstacles;
	}

	private static RooftopObstacle step(int x, int y, int plane, int... objectIds)
	{
		return new RooftopObstacle(new WorldPoint(x, y, plane), objectIds);
	}

	/** The course the tile is on or beside, or null. */
	static Course near(WorldPoint tile)
	{
		return Arrays.stream(values()).filter(course -> course.isNear(tile)).findFirst().orElse(null);
	}

	static boolean isObstacle(int objectId)
	{
		return Arrays.stream(values()).anyMatch(course -> course.indexOf(objectId) >= 0);
	}

	boolean isNear(WorldPoint tile)
	{
		return tile.getX() >= minX - MARGIN && tile.getX() <= maxX + MARGIN
			&& tile.getY() >= minY - MARGIN && tile.getY() <= maxY + MARGIN;
	}

	/** The object's place in the lap, or -1. */
	int indexOf(int objectId)
	{
		for (int index = 0; index < obstacles.length; index++)
		{
			if (obstacles[index].is(objectId))
			{
				return index;
			}
		}
		return -1;
	}

	/**
	 * The obstacle whose landing tile can be walked to from the given tile, or -1. Roofs are only joined by
	 * obstacles, so that is the one the player last completed.
	 */
	int lastCompletedAt(WorldPoint from, BiPredicate<WorldPoint, WorldPoint> canStep)
	{
		Set<WorldPoint> seen = new HashSet<>();
		Queue<WorldPoint> reached = new ArrayDeque<>();
		seen.add(from);
		reached.add(from);
		while (!reached.isEmpty())
		{
			WorldPoint tile = reached.remove();
			int index = RooftopObstacle.landingOn(obstacles, tile);
			if (index >= 0)
			{
				return index;
			}
			for (int[] step : STEPS)
			{
				WorldPoint next = tile.dx(step[0]).dy(step[1]);
				if (isNear(next) && canStep.test(tile, next) && seen.add(next))
				{
					reached.add(next);
				}
			}
		}
		return -1;
	}
}
