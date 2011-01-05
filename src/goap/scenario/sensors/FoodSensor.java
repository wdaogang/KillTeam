/*
 * Copyright (C) 2009 Arne Klingenberg
 * E-Mail: klingenberg.a@googlemail.com
 * 
 * This software is free; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as published
 * by the Free Software Foundation.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, see <http://www.gnu.org/licenses/>.
 */

package goap.scenario.sensors;

import goap.agent.ISensor;
import goap.agent.WorkingMemory;
import goap.scenario.BaseGame;
import goap.scenario.MarketStand;
import goap.scenario.Pedestrian;

/**
 * Sensor which searches the Agents visualRange for stands which could provide food
 * @author Klinge
 *
 */
public class FoodSensor implements ISensor {

	Pedestrian owner;
	
	public FoodSensor(Pedestrian owner)
	{
		this.owner =owner;
	}
	
	@Override
	public void update(WorkingMemory memory) {
		
		for(MarketStand foodStand :BaseGame.foodStands){
//			if(owner.inVisualRange(foodStand.getShape())){
//				for(NavNode n: foodStand.getFreeSpots()){
//					MemoryObject m = new MemoryObject(1.0f, "Food", Helper.navNodeToLocalCords(n));
//					
//					if(!n.blocked)		
//						memory.addMemory(m);
//					else
//						memory.removeMemory(m);
//				}
//			}
		}
	}

}
