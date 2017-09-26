/*
 * SDAbstractState.java 09.02.2010
 * 
 * Copyright 2010 sdaTetirs
 * All rights reserved
 */

package com.sdatetris.gamestates;

import com.jmex.game.state.BasicGameState;

/**
 * Basic game state for all other game states. Defines common variables (e.g. 
 * input handler).
 * 
 * @author lamao
 *
 */

public abstract class SDAAbstractState extends BasicGameState 
{
	public SDAAbstractState(String name) 
	{
		super(name);
		
	}
	
	
//	/**
//	 * This method overrides standard update methods in such way that if
//	 * game state is inactive it handle nothing. 
//	 * 
//	 * @see #updateImpl(float)
//	 * @see BasicGameState#update(float)
//	 * 
//	 */
//	@Override
//	public final void update(float tpf)
//	{
//		if (!isActive())
//		{
//			return;
//		}
//		updateImpl(tpf);
//		super.update(tpf);
//	}
//	
//	/**
//	 * You should place all 'update' code here rather than in render(float)
//	 * 
//	 * @see #update(float)
//	 * 
//	 */
//	public abstract void updateImpl(float tpf);
	
}
