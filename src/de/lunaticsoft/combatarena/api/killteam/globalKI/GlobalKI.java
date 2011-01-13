package de.lunaticsoft.combatarena.api.killteam.globalKI;

import java.util.HashMap;
import java.util.Map;

import com.jme.math.Vector3f;

import memory.map.MemorizedMap;
import memory.objectStorage.MemorizedWorldObject;
import memory.objectStorage.ObjectStorage;
import de.lunaticsoft.combatarena.api.interfaces.IPlayer;
import de.lunaticsoft.combatarena.api.interfaces.IWorldInstance;
import de.lunaticsoft.combatarena.api.killteam.KillKI;
import debug.MapServer;

/**
 * Stellt die gobale KI dar. Hier soll
 * <li> die Karte aufgebaut werden
 * <li> die globale Strategie entschieden werden
 * <li> etc
 * <br>
 * damit die GlobalKI mit der Welt interagieren kann
 * 
 * <br><br>
 * @author pascal
 *
 */
public class GlobalKI {
	
//	private 
	private String name = "GlobaleKI";
	private IWorldInstance world;	
	private MemorizedMap map;
	private ObjectStorage objectStorage;
	private Map<KillKI, PlayerData> players;
	
	public GlobalKI() {
		map = new MemorizedMap();
		objectStorage = new ObjectStorage(map, this);
		players = new HashMap<KillKI, PlayerData>();
		
		MapServer srv = new MapServer(map, this.objectStorage);

		Thread t = new Thread(srv);
		t.start();
	}
//
//	public GlobalKIBlackboard getBlackBoard(){
//		return blackboard;
//	}

	/**
	 * Is called by one of the tanks, since only IPlayer classes get an world reference 
	 * from the TankArenaServer
	 * 
	 * @param world
	 */
	public void setWorldInstance(IWorldInstance world) {
		this.world = world;	
	}
	
	public MemorizedMap getWorldMap(){
		return map;
	}
	
	public ObjectStorage getObjectStorage() {
		return this.objectStorage;
	}
	
	/**
	 * Registers a tank in global KI after it has spawned and creates the players status object
	 * @param tank
	 */
	public void registerTank(KillKI tank){
		PlayerData data = new PlayerData();
		players.put(tank, data);
	}
	
	/**
	 * Removes a registered tank when it dies
	 * @param tank
	 */
	public void removeTank(KillKI tank){
		players.remove(tank);
	}
	
	/**
	 * Updates a specific status item of a registered tank
	 * 
	 * @param tank
	 * @param statusItem
	 */
	public void tankStatusChanged(KillKI tank, Object statusItem, StatusType type){
		PlayerData data = players.get(tank); 
		switch (type) {
		case Position:
			if(statusItem instanceof Vector3f){
				Vector3f pos = (Vector3f)statusItem;
				data.lastPosition = data.Position;
				data.Position = pos;
			}	
			break;
		case GoalPosition:
			if(statusItem instanceof Vector3f){
				data.GoalPosition = (Vector3f)statusItem;
			}	
			break;
		case Attacked:
			if(statusItem instanceof Integer){
				data.attacked = (Integer)statusItem;
			}
			break;	
		case HangarFound:
			if(statusItem instanceof Vector3f){
				data.hangarFound = (Vector3f)statusItem;
			}
			break;		
		case HangarRemoved:
			break;			
		case FlagSpotted:
			if(statusItem instanceof Vector3f){
				data.flagSpotted = (Vector3f)statusItem;
			}
			break;
		default:
			break;
		}
	}
	
	public void notifyTanks(StatusType type, Object changedObject){
		for(KillKI tank : players.keySet()){
			tank.notify(type, changedObject);
		}
	}
}
