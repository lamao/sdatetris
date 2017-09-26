/* 
 * SDALineAnimator.java 26.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved. 
 */
package com.sdatetris.animation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.scene.shape.Box;

/**
 * Performs animated moving of boxes. To animate you need set animation data of
 * form <code>Map<Box, Vector3f></code>, where key - box to animate, value - 
 * moving distance.<br>
 * <b>NOTE: </b> distance and direction coords should have the same sign and now
 * can be only with minus sign. 
 * That is, <code>distance.addLocal_direction.mult(-time * speed)</code> should 
 * decrease distance, because animation stops when distance coordinates are 
 * greater or equal to 0.
 * @author lamao
 */
@SuppressWarnings("serial")
public class SDABoxAnimator extends Controller
{
	/** Animation data. The key is box, the value is moving distance */
	private List<Entry<Box, Vector3f>> _boxes = null;
	
	/** Direction of moving */
	private Vector3f _direction = new Vector3f(0, -1, 0);
	
	/** Function that should be called when animation will be finished */
	private Callable<Void> _callable = null;

	public SDABoxAnimator(Map<Box, Vector3f> animateble)
	{
		setRepeatType(RT_CLAMP);
		setSpeed(2);
		_boxes = new ArrayList<Entry<Box, Vector3f>>(animateble.entrySet());
	}
	
	/* (non-Javadoc)
	 * @see com.jme.scene.Controller#update(float)
	 */
	@Override
	public void update(float time)
	{
		Entry<Box, Vector3f> entry = null;
		for (int i = 0; i < _boxes.size(); i++)
		{
			entry = _boxes.get(i);
			if (entry.getValue().x >= 0 && entry.getValue().y >= 0 &&
				entry.getValue().z >= 0)
			{
				// final correction
				entry.getKey().getLocalTranslation().addLocal(entry.getValue());
				
				_boxes.remove(i);
				i--;
			}
			else
			{
				entry.getKey().getLocalTranslation().addLocal(_direction.mult(time * getSpeed()));
				entry.getValue().addLocal(_direction.mult(-time * getSpeed()));
			}
		}
		if (_boxes.size() == 0 && _callable != null)
		{
			try
			{
				_callable.call();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	public void setMoveDirection(Vector3f direction)
	{
		_direction = direction;
	}
	
	public void invokeAfterFinishing(Callable<Void> callable)
	{
		_callable = callable;
	}
	
}
