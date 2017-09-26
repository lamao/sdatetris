/*
 * SDAScoreState.java 14.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */
package com.sdatetris.gamestates.game;

import com.jme.renderer.Renderer;
import com.jmex.angelfont.BitmapFont;
import com.jmex.angelfont.BitmapText;
import com.jmex.game.state.BasicGameState;
import com.sdatetris.SDAConstants;
import com.sdatetris.SDAResourceManager;

/**
 * HUD which displays score, number of new lines, level etc
 * 
 * @author lamao
 */
public class SDAScoreState extends BasicGameState
{
	/** Displays necessary scores for archiving next level */
	private BitmapText _necessaryScore = null;
	
	/** Displays score */
	private BitmapText _score = null;
	
	/** Displays number of completed lines */
	private BitmapText _numberOflines = null;
	
	/** Displays current level */
	private BitmapText _level = null;
	
	/** Displays current speed */
	private BitmapText _speed = null;
	
	
	public SDAScoreState(String name)
	{
		super(name);
		initGraphics();
	}
	
	/** Initializes all graphics */
	private void initGraphics()
	{
		BitmapFont font = SDAResourceManager.getInstance()
				.getBmFont(SDAConstants.mainFont);
		float pos = 0;
		
		_speed = new BitmapText(font, false);;
		_speed.setSize(1);
		_speed.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		_speed.setText("Speed: 0");
		_speed.update();
		_speed.setLocalTranslation(0, pos, 0);
		rootNode.attachChild(_speed);
		pos += _speed.getLineHeight();
		
		_level = new BitmapText(font, false);;
		_level.setSize(1);
		_level.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		_level.setText("L: 0");
		_level.update();
		_level.setLocalTranslation(0, pos, 0);
		rootNode.attachChild(_level);
		pos += _level.getLineHeight();
		
		_numberOflines = new BitmapText(font, false);;
		_numberOflines.setSize(1);
		_numberOflines.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		_numberOflines.setText("Lines: 0");
		_numberOflines.update();
		_numberOflines.setLocalTranslation(0, pos, 0);
		rootNode.attachChild(_numberOflines);
		pos += _numberOflines.getLineHeight();
		
		_score = new BitmapText(font, false);;
		_score.setSize(1);
		_score.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		_score.setText("Score: 0");
		_score.update();
		_score.setLocalTranslation(0, pos, 0);		
		rootNode.attachChild(_score);
		pos += _score.getLineHeight();
		
		_necessaryScore = new BitmapText(font, false);;
		_necessaryScore.setSize(1);
		_necessaryScore.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		_necessaryScore.setText("Need:  0");
		_necessaryScore.update();
		_necessaryScore.setLocalTranslation(0, pos, 0);
		rootNode.attachChild(_necessaryScore);
		
		rootNode.updateRenderState();
	}
	
	public void setScore(long score)
	{
		_score.setText("Score: " + score);
		_score.update();
	}
	
	public void setNumberOfLines(int numberOfLines)
	{
		_numberOflines.setText("Lines: " + numberOfLines);
		_numberOflines.update();
	}
	
	public void setLevel(int level)
	{
		_level.setText("Level: " + level);
		_level.update();
	}
	
	public void setNecessaryScore(long score)
	{
		_necessaryScore.setText("Need:  "  + score);
		_necessaryScore.update();
	}
	
	public void setSpeed(float speed)
	{
		_speed.setText("Speed: " + speed);
		_speed.update();
	}
}
