/*
 * SDALevelUtils.java 15.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 * 
 */

package com.sdatetris.gamelogic;

import java.util.Random;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.util.Timer;
import com.sdatetris.SDAConstants;
import com.sdatetris.SDAResourceManager;

/**
 * Used for obtaining level-specific information (e.i speed, initial data, 
 * scores to next line).
 *  
 *  Levels are changed from 0 to 500. Every level up inside decade (e.g. 
 * from 8 to 9, but not from 9 to 10) increases speed. Every level up over 
 * decade (e.g from 9 to 10) resets speed, but adds new initial partly 
 * completed line. The number of free cells in every line is 
 * (level div 100) + 1. It means every handred adds new free cell. After 
 * level up over hundred (e.i. from 99 to 100) the number of initial 
 * lines resets to 0 (zero).<br><br> 
 * 
 * The common rule can be defined as: <br> 
 * <li>New level. Speed++</li> 
 * <li>New level mod 10 == 0. Speed = default, filled_lines++</li> 
 * <li>New level mod 100 == 0. Filled_lines = 0, unfilled_items++</li> 
 * <li>New level == 500. YOU ARE WINNER</li> 
 * <br><br>
 * <b>Example 1</b> <br> 
 * Level 11. Speed = 1<br> 
 * Field at the begin: <br>
 * <code>
 * 0 0 0 0 0 0 0 <br>
 * ............. <br>
 * 0 0 0 0 0 0 0 <br>
 * 1 0 1 1 1 1 1 <br>
 * </code> 
 * <br>
 * <b>Example 2</b> <br> 
 * Level 123. Speed = 3 <br> 
 * Field at the start: <br>
 * <code> 
 * 0 0 0 0 0 0 0 <br> 
 * ............. <br>
 * 0 0 0 0 0 0 0 <br>
 * 1 1 1 0 1 0 1 <br> 
 * 0 1 0 1 1 1 1 <br> <br>
 * </code>
 * 
 * @author lamao
 */
public class SDALevelUtils
{

	/** Instance of this class (singleton) */
	private static SDALevelUtils _intstance = null;
	
	/** 
	 * Number of generater boxes. Used for naming in {@link #getInitialData(int)}
	 */
	private static int numberOfBoxes = 0;
	
	/** Level after which all game settings are dropped to default. It means
	 * the next circle of game begins.
	 */
	private final int resetLevel = 500;
	
	private SDALevelUtils()
	{		
	}
	
	/** Returns an instance of class */
	public static SDALevelUtils getInstance()
	{
		if (_intstance == null)
		{
			_intstance = new SDALevelUtils();
		}
		return _intstance;
	}
	
	/**
	 * Generates field data for the given level
	 * 
	 * @param level - level for which data must be generated
	 * @return generated field data
	 */
	public Box[][] getInitialData(int level)
	{
		java.awt.Point fieldSize = SDAConstants.fieldSize;
		float cellSize = SDAConstants.cellSize;
		Box[][] result = new Box[fieldSize.x][fieldSize.y]; 
		
		Box stub = new Box("stub box");
		Random rnd = new Random(Timer.getTimer().getTime());
		
		int coord = 0;
		for (int y = 0; y < getFilledLines(level); y++)
		{
			// get coords of free cells
			for (int x = 0; x < getUnfilledCells(level); x++)
			{
				do
				{
					coord = rnd.nextInt(fieldSize.x);
				}
				while (result[coord][y] != null);
				result[coord][y] = stub;
			}
			
			//fill line
			for (int x = 0; x < fieldSize.x; x++)
			{
				if (result[x][y] != null)
				{
					result[x][y] = null;
				}
				else
				{
					result[x][y] = new Box("generator box #" + numberOfBoxes++,
							new Vector3f(), cellSize / 2, cellSize / 2, 
							cellSize / 2);
					result[x][y].setRenderState(SDAResourceManager.getInstance()
							.getTexture(SDAConstants.tetraminoTexture));
					result[x][y].setModelBound(new BoundingBox());
					result[x][y].updateModelBound();
				}
			}
		}
		
		return result;
	}
	
	/** 
	 * @param level - level 
	 * @return number of lines should be filled for given level
	 */
	private int getFilledLines(int level)
	{
		if (level >= resetLevel)
		{
			level = level % resetLevel;
		}
		return ((level % 100) / 10);
	}
	
	/**
	 * @param level - level
	 * @return number of free cells in line for given level
	 */
	private int getUnfilledCells(int level)
	{
		if (level >= resetLevel)
		{
			level = level % resetLevel;
		}
		return (level / 100) + 1;
	}
	
	/**
	 * 
	 * @param level - level
	 * @return speed modifier (actually speed if default speed is 1) for given
	 * level
	 */
	public float getSpeedModifier(int level)
	{
		if (level >= resetLevel)
		{
			level = level % resetLevel;
		}
		return (float)(level % 10) / 2 + 1;
	}
	
	/**
	 * 
	 * @param level - level
	 * @return return necessary score for jumping to the specified level. 
	 * 
	 */
	public long getNecessaryScore(int level)
	{
		return Math.round(Math.pow(level, 2));
	}
	
}
