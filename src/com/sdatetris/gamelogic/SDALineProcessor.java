/* 
 * SDALineProcessor.java 25.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved. 
 */
package com.sdatetris.gamelogic;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.sdatetris.SDAConstants;

/**
 * Clears field data from completed lines.
 * 
 * @author lamao
 */
public class SDALineProcessor
{
	private Map<Box, Vector3f> _animateble = new HashMap<Box, Vector3f>();

	public Map<Box, Vector3f> getAnimationData()
	{
		return _animateble;
	}
	
	/**
	 * Scan data for completed lines and clears them
	 * @param - field for processing
	 * @return Number of completed lines being cleared
	 */
	public int processLines(Box[][] data, Node rootNode) 
	{
		int result = 0;
		int maxY = SDAConstants.fieldSize.y;
		_animateble.clear();
		
		for (int y = 0; y < maxY; y++)
		{
			if (isCompleted(y, data))
			{
				detachLine(y, data, rootNode);
				addForAnimation(y, data);
				shiftDown(y, data);				
				y--;	
				maxY--;
				result++;
			}
		}
		
//		for (Box box : _animateble.keySet())
//		{
//			System.out.println("pos = " + box.getCenter() + "; new pos = " + 
//					_animateble.get(box));
//		}
		
		return result;
	}
	
	/**
	 * Checks if there is at least one completed line of boxes
	 * @return <b>true</b> if ther is
	 */
	public boolean isLine(SDAField field)
	{
		int maxY = SDAConstants.fieldSize.y;
		int y = 0;
		while (y < maxY && !isCompleted(y, field.getData()))
		{
			y++;
		}
		
		return y < maxY;
	}
	/** 
	 * Checks if y-line is completed 
	 * @param y - line
	 * @param data - field data
	 */
	private boolean isCompleted(int y, Box[][] data)
	{
		int x = 0;
		while (x < SDAConstants.fieldSize.x && data[x][y] != null)
		{
			x++;
		}
		
		return x == SDAConstants.fieldSize.x;			
	}
	
	/** 
	 * Detaches boxes in line
	 * @param y - line
	 * @param data - field data 
	 * @param rootNode - node which contains boxes
	 * @return list of detached boxes
	 */
	private List<Box> detachLine(int y, Box[][] data, Node rootNode)
	{
		List<Box> result = new LinkedList<Box>();
		for (int x = 0; x < SDAConstants.fieldSize.x; x++)
		{
			rootNode.detachChild(data[x][y]);
			result.add(data[x][y]);
		}
		return result;
	}
	
	/** 
	 * Shifts field data one line down for filling cleared line
	 * @param y - cleared (completed) line
	 * @param data - field data
	 */
	private void shiftDown(int y, Box[][] data)
	{
		for (int y1 = y; y1 < SDAConstants.fieldSize.y - 1; y1++)
		{
			for (int x1 = 0; x1 < SDAConstants.fieldSize.x; x1++)
			{
				data[x1][y1] = data[x1][y1 + 1];
//				if (data[x1][y1 + 1] != null)
//				{
//					data[x1][y1 + 1].getCenter().y -= SDAConstants.cellSize;
//					data[x1][y1 + 1].updateGeometry();
//				}
			}
		}
		for (int x1 = 0; x1 < SDAConstants.fieldSize.x; x1++)
		{
			data[x1][SDAConstants.fieldSize.y - 1] = null;
		}
	}
	
	private void addForAnimation(int line, Box[][] data)
	{
		Vector3f distance = null;
		for (int x = 0; x < SDAConstants.fieldSize.x; x++)
		{
			_animateble.remove(data[x][line]);
			for (int y = line + 1; y < SDAConstants.fieldSize.y; y++)
			{
				if (data[x][y] != null)
				{
					distance = _animateble.get(data[x][y]);
					if (distance == null)
					{
						distance = Vector3f.ZERO.clone();
						_animateble.put(data[x][y], distance);
					}
//					System.out.print(finalPos + "-->");
//					finalPos.addLocal(0, -SDAConstants.cellSize, 0);
					distance.y -= SDAConstants.cellSize;
//					System.out.println(finalPos);
				}
			}
		}
	}
}
