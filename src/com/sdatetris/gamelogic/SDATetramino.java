/*
 * SDATetramino.java 08.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */

package com.sdatetris.gamelogic;

import java.awt.Point;
import java.util.Arrays;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.sdatetris.SDAConstants;
import com.sdatetris.SDAResourceManager;

/**
 * Tetramino :)). Holds all related graphics and implements all logic related 
 * to one tetramino (e.g. rotate, generate etc)
 * 
 * @author lamao
 *
 */

public class SDATetramino 
{
	/** Root node for tetramino */
	private Node _rootNode = new Node("tetraRoot" + _numberOfTetraminos++);
	
	/** Matrix of boxes, which is used for rotation and other logic operations.
	 * First number is X, second - Y
	 */
	private Box[][] _data = new Box[4][4];
	
	/** Patterns for tetraminos. Defines (x,y) coordinates of boxes inside 
	 * <code>_data</code> 
	 */
	private Point[][] _patterns = // I, L, J, O, T, Z, S
			{{new Point(0, 3),new Point(0, 2),new Point(0, 1),new Point(0, 0)},
			{new Point(0, 2),new Point(0, 1),new Point(0, 0),new Point(1, 0)},
			{new Point(1, 2),new Point(1, 1),new Point(1, 0),new Point(0, 0)},
			{new Point(0, 1),new Point(0, 0),new Point(1, 0),new Point(1, 1)},
			{new Point(0, 0),new Point(1, 0),new Point(2, 0),new Point(1, 1)},
			{new Point(0, 1),new Point(1, 1),new Point(1, 0),new Point(2, 0)},
			{new Point(0, 0),new Point(1, 0),new Point(1, 1),new Point(2, 1)},};
	
	/**
	 * Used for naming box nodes
	 * 
	 * @see #generateTetramino()
	 */
	private static int _numberOfBoxes = 0;
	
	/**
	 * Used for naming <code>_rootNode</code>
	 * 
	 * @see #_rootNode
	 */
	private static int _numberOfTetraminos = 0;
	

	public Node getRootNode() 
	{ 
		return _rootNode;
	}
	
	public Box[][] getData()
	{
		return _data;
	}
	
	/**
	 * Generates new tetramino and replace current one
	 */
	public void generateTetramino()
	{
		
		// clear figure
		for (int i = 0; i < 4; i++)
		{
			Arrays.fill(_data[i], null);
		}
		
		//choose pattern 
		int index = Math.round((float)Math.random() * (_patterns.length - 1));
		
		// generate
		float cellSize = SDAConstants.cellSize;
		Point pos = null;		
		for (int i = 0; i < 4; i++)
		{
			pos = _patterns[index][i];
			_data[pos.x][pos.y] = new Box(
					"box" + _numberOfBoxes++, Vector3f.ZERO.clone(), 
					cellSize / 2, cellSize / 2, cellSize / 2);
			_data[pos.x][pos.y].setLocalTranslation(
					pos.x * cellSize, pos.y * cellSize, 0f);
			_data[pos.x][pos.y].setModelBound(new BoundingBox());
			_data[pos.x][pos.y].updateModelBound();
			_data[pos.x][pos.y].setRenderState(SDAResourceManager.getInstance()
					.getTexture(SDAConstants.tetraminoTexture));
			_data[pos.x][pos.y].updateRenderState();
			
			_rootNode.attachChild(_data[pos.x][pos.y]);
			
			
		}
		_rootNode.updateModelBound();
	}
	
	/**
	 * Rotates tetramino to right.
	 */
	public void rotateRight()
	{
		int y = 0;
		int x = 0;
		Box buf;
		
		// rotate figure
		for (y = 0; y <= 1; y++)
		{
			for (x = y; x <= 2 - y; x++)
			{
				buf = _data[x][y];
				_data[x][y] = _data[3 - y][x];
				_data[3 - y][x] = _data[3 - x][3 - y];
				_data[3 - x][3 - y] = _data[y][3 - x];
				_data[y][3 - x] = buf;
			}
		}
		
		//move figure down into 4x4 box
		while (_data[0][0] == null && _data[1][0] == null &&
				 _data[2][0] == null && _data[3][0] == null)
		{
			for (y = 0; y <3; y++)
			{
				for (x = 0; x <= 3; x++)
				{
					_data[x][y] = _data[x][y + 1];
				}
			}
			for (x = 0; x <= 3; x++)
			{
				_data[x][3] = null;
			}
		}
		
		
		// correct box locations 
		float cellSize = SDAConstants.cellSize;
		for (x = 0; x < 4; x++)
		{
			for (y = 0; y < 4; y++)
			{
				if (_data[x][y] != null)
				{
					_data[x][y].setLocalTranslation(x * cellSize, y * cellSize, 0);
				}
				
			}
		}
		
		_rootNode.updateModelBound();
	}
	
	/**
	 * Rotates tetramino to left.
	 */
	public void rotateLeft()
	{
		int y = 0;
		int x = 0;
		Box buf;
		
		// rotate figure
		for (y = 0; y <= 1; y++)
		{
			for (x = y; x <= 2 - y; x++)
			{
				buf = _data[y][3 - x];
				_data[y][3 - x] = _data[3 - x][3 - y];
				_data[3 - x][3 - y] = _data[3 - y][x];
				_data[3 - y][x] = _data[x][y];
				_data[x][y] = buf;
			}
		}
		
		//move figure left into 4x4 box
		while (_data[0][0] == null && _data[0][1] == null &&
				 _data[0][2] == null && _data[0][3] == null)
		{
			for (y = 0; y <= 3; y++)
			{
				for (x = 0; x <3; x++)
				{
					_data[x][y] = _data[x + 1][y];
				}
			}
			for (y = 0; y <= 3; y++)
			{
				_data[3][y] = null;
			}
		}
		
		
		// correct box locations 
		float cellSize = SDAConstants.cellSize;
		for (x = 0; x < 4; x++)
		{
			for (y = 0; y < 4; y++)
			{
				if (_data[x][y] != null)
				{
					_data[x][y].setLocalTranslation(x * cellSize, y * cellSize, 0);
				}
				
			}
		}
		
		_rootNode.updateModelBound();
	}
	
	/**
	 * Checks whether tetramino intersects with <code>area</code>
	 * 
	 * @param area - matrix 4x4. Should be area from field.
	 * @return <b>true</b> if tetramino intersects with <code>area</code>
	 */
	public boolean doesIntersect(Box[][] area)
	{
		assert area.length == 4;
		
		for (int i = 0; i < 4; i++)
		{
			for (int k = 0; k < 4; k++)
			{
				if (_data[i][k] != null && area[i][k] != null)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	
}
