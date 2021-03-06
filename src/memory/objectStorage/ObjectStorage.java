package memory.objectStorage;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import map.fastmap.FastRoutableWorldMap;
import map.fastmap.LinkedTile;
import memory.map.MemorizedMap;

import com.jme.math.Vector3f;

import de.lunaticsoft.combatarena.api.enumn.EColors;
import de.lunaticsoft.combatarena.api.killteam.globalKI.GlobalKI;
import de.lunaticsoft.combatarena.api.killteam.globalKI.StatusType;

public class ObjectStorage {

	private MemorizedMap map;
	private GlobalKI globalKI;
	List<MemorizedWorldObject> knownObjects;
	Map<LinkedTile, List<MemorizedWorldObject>> objectsMap; 
	Map<Point, MemorizedWorldObject> items;
	Map<EColors, Map<Point, MemorizedWorldObject>> hangars;
	Map<EColors, Map<Point, MemorizedWorldObject>> tanks;

	public ObjectStorage(MemorizedMap map, GlobalKI globalKI) {
		this.map = map;
		this.globalKI = globalKI;
		knownObjects = Collections.synchronizedList(new ArrayList<MemorizedWorldObject>());
		objectsMap = Collections.synchronizedMap(new HashMap<LinkedTile, List<MemorizedWorldObject>>());
		items = Collections.synchronizedMap(new HashMap<Point, MemorizedWorldObject>());
		hangars = Collections.synchronizedMap(new HashMap<EColors, Map<Point,MemorizedWorldObject>>());
		tanks = Collections.synchronizedMap(new HashMap<EColors, Map<Point,MemorizedWorldObject>>());
		for(EColors color : EColors.values()) {
			tanks.put(color, new HashMap<Point, MemorizedWorldObject>());
			hangars.put(color, new HashMap<Point, MemorizedWorldObject>());
		}
		
		//start degeneration Thread
		Thread t = new Thread(new DegenerationThread(this));
		t.start();
	}
	
	synchronized public void storeObject(Vector3f position, MemorizedWorldObject object) {
		Point objectPosition = FastRoutableWorldMap.coordinates2MapIndex(position);
		switch(object.getType()) {
			case Competitor:
				tanks.get(object.getColor()).put(objectPosition, object);
				break;
			case Hangar:
				hangars.get(object.getColor()).put(objectPosition, object);
				break;
			case Item:
				items.put(objectPosition, object);
				break;
		}
		knownObjects.add(object);
		addToObjectsMap(object);
	}
	
	synchronized public void removeObject(MemorizedWorldObject object) {
		removeFromObjectsMap(object);
		Point objectPosition = FastRoutableWorldMap.coordinates2MapIndex(object.getPosition());
		switch(object.getType()) {
			case Competitor:
				tanks.get(object.getColor()).remove(objectPosition);
				break;
			case Hangar:
				Map<Point, MemorizedWorldObject> hangarMap = hangars.get(object.getColor());
				if(hangarMap.values().contains(object)){
					hangars.get(object.getColor()).remove(objectPosition);
					globalKI.notifyTanks(StatusType.HangarRemoved, object);
				}
				break;
			case Item:
				
				Map<Point, MemorizedWorldObject> itemMap = hangars.get(object.getColor());
				if(itemMap.values().contains(object)){
					items.remove(objectPosition);
					globalKI.notifyTanks(StatusType.ItemRemoved, object);
				}
				break;
		}
		knownObjects.remove(object);
	}
	
	synchronized public Map<Point, MemorizedWorldObject> getEnemyTanks() {
		Map<Point,MemorizedWorldObject> listOfAllTanks = new HashMap<Point, MemorizedWorldObject>();
		for(Map<Point,MemorizedWorldObject> enemyList : tanks.values()) {
			listOfAllTanks.putAll(enemyList);
		}
		return listOfAllTanks;
	}
	
	synchronized public Map<Point, MemorizedWorldObject> getEnemyTanksOfPlayer(EColors enemy) {
		return tanks.get(enemy);
	}
	
	synchronized public Map<Point, MemorizedWorldObject> getEnemyHangars() {
		Map<Point,MemorizedWorldObject> listOfAllHangars = new HashMap<Point, MemorizedWorldObject>();
		for(Map<Point,MemorizedWorldObject> enemyList : hangars.values()) {
			listOfAllHangars.putAll(enemyList);
		}
		return listOfAllHangars;
	}
	
	synchronized public Map<Point, MemorizedWorldObject> getEnemyHangarsOfPlayer(EColors enemy) {
		return hangars.get(enemy);
	}
	
	synchronized public Map<Point, MemorizedWorldObject> getEnemyObjects() {
		Map<Point,MemorizedWorldObject> objects = getEnemyHangars();
		objects.putAll(getEnemyTanks());
		return objects;
	}
	
	synchronized public Map<Point, MemorizedWorldObject> getEnemyObjectsOfPlayer(EColors enemy) {
		Map<Point,MemorizedWorldObject> objects = getEnemyHangarsOfPlayer(enemy);
		objects.putAll(getEnemyTanksOfPlayer(enemy));
		return objects;
	}
	
	synchronized public Map<Point, MemorizedWorldObject> getRepairkits() {
		return items;
	}
	
	
	private class DegenerationThread implements Runnable{
		ObjectStorage storage;
		
		public DegenerationThread(ObjectStorage storage){
			this.storage = storage;
		}
		
		@Override
		public void run() {
			while(true){
				//iterate over all enemy tanks
				ArrayList<MemorizedWorldObject> objects = new ArrayList<MemorizedWorldObject>(storage.getEnemyTanks().values());
				for(MemorizedWorldObject obj: objects){
					//as long as object is durable decrease durability
					if(obj.getDurability()>0){
						obj.decreaseDurability(1000);
					}
					else{
						//remove the object as it's not durable anymore
						this.storage.removeObject(obj);
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}	
	}
	
	private void addToObjectsMap(MemorizedWorldObject object){
		LinkedTile tile = map.getTileAtCoordinate(object.getPosition());
		if(!objectsMap.containsKey(tile)){
			objectsMap.put(tile, new ArrayList<MemorizedWorldObject>());
		}
		objectsMap.get(tile).add(object);
	}
	
	private void removeFromObjectsMap(MemorizedWorldObject obj){
		LinkedTile tile = map.getTileAtCoordinate(obj.getPosition());
		int index = objectsMap.get(tile).indexOf(obj);
		objectsMap.get(tile).remove(index);
		if(objectsMap.get(tile).isEmpty()){
			objectsMap.remove(tile);
		}
	}
	
	/**
	 * Returns all  objects placed on the given tiles
	 * 
	 * @param tiles
	 * @return
	 */
	public Set<MemorizedWorldObject> getObjectsAtTiles(List<LinkedTile> tiles){
		Set<MemorizedWorldObject> set = new HashSet<MemorizedWorldObject>();
		
		for(LinkedTile tile : tiles){
			if(objectsMap.containsKey(tile)){
				List<MemorizedWorldObject> list2 = objectsMap.get(tile);
					for(MemorizedWorldObject memo : list2){
						set.add(memo);
				}			
			}
		}
		return set;
	}
}
