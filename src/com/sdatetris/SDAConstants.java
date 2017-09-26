/*
 * SDAConstants.java 08.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */

package com.sdatetris;

/**
 * 
 * Holds all global constants.
 * 
 * @author lamao
 *
 */

public class SDAConstants 
{
	/** Time to display logo. In seconds */
	public final static float logoDelay = 5;
	
	// Informational constants
	public final static String projectName = "sdaTetris";
	public final static int buildNumber = 6;
	public final static int buildYear = 2010;

	// Game constants
	/** Size of one cell in game. It actually defines the size of on box */
	public final static float cellSize = 0.5f;	
	
	/** Size of field in cells */
	public final static java.awt.Point fieldSize = new java.awt.Point(20, 30);
	
	/** Final level for game. If you reach it you will be a WINNER :))) */
	public final static int finalLevel = 500;
	
	/** 
	 * Multiplier for score if <code>k</code> lines were completed 
	 * simultaneously 
	 */
	public final static int[] linesMultiplier = new int[] {1, 4, 9, 16};
	
	/** File with records */
	public final static String recordsFile ="data/records.txt";
	
	/** Resource description file */
	public final static String resourceFile = "data/resources.txt";
	
	/** List of developers */
	public final static String[] developers = {
			"Mishcheryakov Vyacheslav aka lamao"};
	
	// Textures
	public static String monkeyTexture = "monkeyTX";
	public static String logoTexture = "logoTX";
	public static String tetraminoTexture = "tetraminoTX";
	public static String mainBoxTexture = "mainBoxTX";
	public static String mainBoxBgTexture = "mainBoxBgTX";
	public static String particlesTexture = "particlesTX";
	
	// Filenames for sounds 
	public static String placeSound = "placedSnd";
	public static String lineSound = "lineSnd";
	public static String musicSound = "musicSnd";
	public static String levelSound = "levelSnd";
	public static String gameoverSound = "gameoverSnd";
	public static String victorySound = "victorySnd";
	
	// Fonts
	public static String mainFont = "snap";
}
