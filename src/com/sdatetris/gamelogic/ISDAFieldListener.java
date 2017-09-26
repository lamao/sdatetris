/*
 * ISDAFieldListener.java 08.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */

package com.sdatetris.gamelogic;

/**
 * Listener for field in-game events
 * 
 * @author lamao
 *
 */
public interface ISDAFieldListener
{
	/**
	 * Tetramino was moved in any direction
	 */
	public void moved(SDAField sender);
	
	/**
	 * Tetramino was rotated
	 */
	public void rotated(SDAField sender);
	
	/**
	 * Tetramino was placed into the field
	 */
	public void placed(SDAField sender);
	
	/**
	 * At least one line was completed
	 * 
	 * @param numberNewOfLines - number of completed lines
	 */
	public void lineCompleted(SDAField sender, int numberNewOfLines);
	
	/**
	 * The field is filled and game is overed
	 */
	public void gameOver(SDAField sender);
	
	/**
	 * Current tetramion was replaced by next tetramino and next tetramino
	 * was replaced by new tetramino
	 */
	public void tetraminoChanged(SDAField sender);
}
