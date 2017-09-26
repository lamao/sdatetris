/*
 * SDAOptions.java 08.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */

package com.sdatetris;

import com.jme.input.KeyInput;

/**
 * 
 * Saves all application-specific options.
 * 
 * @author lamao
 *
 */

public class SDAOptions 
{
	// Game options
	
	/** Seconds on one logic circle (e.g. moving tetramino down) */
	public static float stepInterval = 0.5f;
	
	/** Time for effects between levels. In seconds */
	public static float interLevelEffectTime = 1.5f;

	// Game controls
	public static int leftKey = KeyInput.KEY_LEFT;
	public static int rightKey = KeyInput.KEY_RIGHT;
	public static int rotateKey = KeyInput.KEY_UP;
	public static int downKey = KeyInput.KEY_DOWN;
	
	public static int nextVisibleKey = KeyInput.KEY_F2;
	public static int scoreVisibleKey = KeyInput.KEY_F3;
	public static int fpsVisibleKey = KeyInput.KEY_F4;
	
	// Options
	public static boolean playSounds = true;
	public static boolean playMusic = true;
	
}
