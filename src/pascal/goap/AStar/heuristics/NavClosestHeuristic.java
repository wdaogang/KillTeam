package pascal.goap.AStar.heuristics;

import pascal.goap.pathfinding.NavNode;
import pascal.goap.AStar.AStarHeuristic;
import pascal.goap.AStar.IMap;
import pascal.goap.AStar.Node;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 * 
 * @author Kevin Glass
 * http://www.cokeandcode.com/pathfinding
 */
public class NavClosestHeuristic implements AStarHeuristic {
	/**
	 * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int)
	 */
	public float getCost(IMap map, Node start, Node end) {
		
        float dx = ((NavNode)end).x - ((NavNode)start).x;
		float dy = ((NavNode)end).y - ((NavNode)start).y;
		
		return (float) (Math.sqrt((dx*dx)+(dy*dy)));
	}

}
