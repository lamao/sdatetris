/* 
 * SDATetraminoPlacer.java 27.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved. 
 */
package com.sdatetris.gamelogic;

import java.util.HashMap;
import java.util.Map;

import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.sdatetris.SDAConstants;

/**
 * Places tetramino to field (moving boxes from tetramino into field data)
 * @author lamao
 */
public class SDATetraminoPlacer
{
	/** Map with animation data for each box. Specifies distance of animation */
	private Map<Box, Vector3f> _animateble = new HashMap<Box, Vector3f>();
	
	/** Places tetramino */
	public void place(SDAField field)
	{
		Box[][] tetData = field.getTetramino().getData();
		Box[][] fieldData = field.getData();
		java.awt.Point tetPos = field.getTetPos();
		_animateble.clear();
		
		for (int x = 0; x < 4; x++)
		{
			for (int y = 0; y < 4; y++)
			{
				if (tetData[x][y] != null)
				{
					fieldData[x + tetPos.x][y + tetPos.y] = tetData[x][y];
					field.getTetramino().getRootNode().detachChild(tetData[x][y]);
					field.getRootNode().attachChild(tetData[x][y]);
					
					tetData[x][y].setLocalTranslation(
							(x + tetPos.x) * SDAConstants.cellSize,
							(y + tetPos.y) * SDAConstants.cellSize, 
							SDAConstants.cellSize / 2);
					_animateble.put(tetData[x][y], new Vector3f( 
							0, 0, -SDAConstants.cellSize / 2));
				}
			}
		}
	}
	
	public Map<Box, Vector3f> getAnimationData()
	{
		return _animateble;
	}
}
