/*
 * SDATetraminoTest.java 10.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */
package com.sdatetris.gamelogic;

import java.util.Arrays;
import org.junit.Test;
import com.jme.scene.shape.Box;
import static junit.framework.Assert.*;

/**
 * Test case for testing SDATetramino logic
 * 
 * @author lamao
 */
public class SDATetraminoTest
{

	@Test
	public void testConstructors()
	{
		SDATetramino t = new SDATetramino();
		
		assertNotNull(t.getRootNode());
		assertEquals(t.getData().length, 4);
		for (int i = 0; i < 4; i++)
		{
			assertEquals(t.getData()[i].length, 4);
		}
		
		for (int i = 0; i < 4; i++)
		{
			for (int k = 0; k < 4; k++)
			{
				assertNull(t.getData()[i][k]);
			}
		}
	}
	
	@Test
	public void testGeneration()
	{
		SDATetramino t = new SDATetramino();
		for (int i = 0; i < 100; i++)
		{
			t.generateTetramino();
		}
	}
	
	@Test
	public void testRotation()
	{
		SDATetramino t = new SDATetramino();
		t.generateTetramino();
		
		Box[][] buffer = t.getData().clone();
		
		t.rotateLeft();
		t.rotateRight();

		for (int i = 0; i < 4; i++)
		{
			Arrays.equals(buffer[i], t.getData()[i]);
		}
		
		// 360 degrees right rotation
		for (int i = 0; i < 4; i++)
		{
			t.rotateRight();
		}
		for (int i = 0; i < 4; i++)
		{
			Arrays.equals(buffer[i], t.getData()[i]);
		}
		
		// 360 degrees left rotation
		for (int i = 0; i < 4; i++)
		{
			t.rotateLeft();
		}
		for (int i = 0; i < 4; i++)
		{
			Arrays.equals(buffer[i], t.getData()[i]);
		}
		
	}
	
	@Test
	public void testIntersection()
	{
		SDATetramino t = new SDATetramino();
		t.generateTetramino();
		
		// intersection with itself
		Box[][] buffer = new Box[4][];
		for (int i = 0; i < 4; i++)
		{
			buffer[i] = t.getData()[i].clone();
		}
		assertTrue(t.doesIntersect(buffer));
		
		//intersection with rotated tetramino
		t.rotateRight();
		assertTrue(t.doesIntersect(buffer));
		
		// intersection with empty area 
		for (int i = 0; i < 4; i++)
		{
			Arrays.fill(buffer[i], null);
		}
		assertFalse(t.doesIntersect(buffer));

		//intersection with inversed area
		Box stub = new Box();
		for (int i = 0; i < 4; i++)
		{
			for (int k = 0; k < 4; k++)
			{
				if (t.getData()[i][k] == null)
				{
					buffer[i][k] = stub;
				}
				else
				{
					buffer[i][k] = null;
				}
			}
		}
		assertFalse(t.doesIntersect(buffer));
	}
	
}
