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

package goap.scenario.actions;

import java.util.ArrayList;

import goap.goap.Action;
import goap.goap.PropertyType;
import goap.goap.TankWorldProperty;
import goap.goap.WorldStateSymbol;
import goap.scenario.GoapActionSystem;

/**
 * Action to collect a ToolBox. The tanks blackboard has a reference to the spotted toolbox, 
 * to check if the toolbox at the specific position has been removed by someone.
 * 
 * preCond
 * - toolBoxSpotted = true
 * Effect
 * - toolBoxSpotted = false ( man muss irgendwie die Location der ToolBox da noch mit reinbringen)
 * 
 * @author Pascal Jaeger
 *
 */
public class CollectToolBox extends Action{

	protected GoapActionSystem as;

	public CollectToolBox(GoapActionSystem as, String name, float cost) {
	    super(1, null, null,null );


		this.as = as;
		effect.add(new WorldStateSymbol<Boolean>(TankWorldProperty.ToolBoxSpotted,
				false, PropertyType.Boolean));
		preCond.add(new WorldStateSymbol<Boolean>(TankWorldProperty.HasDestination,
				true, PropertyType.Boolean));
		
	}

	public void performAction() {
	
	}

	public boolean isFinished() {
		return true;
	}

	@Override
	public boolean isValid() {
		if(as.getBlackboard().spottedToolBox != null){
			return true;
		}
		return false;
	}

	@Override
	public boolean contextPreconditionsMet() {
		return true;
	}
}
