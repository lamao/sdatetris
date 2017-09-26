/*
 * SDANextTetraminoState.java 14.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */
package com.sdatetris.gamestates.game;

import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jmex.angelfont.BitmapFont;
import com.jmex.angelfont.BitmapText;
import com.jmex.game.state.BasicGameState;
import com.sdatetris.SDAConstants;
import com.sdatetris.SDAResourceManager;
import com.sdatetris.gamelogic.SDATetramino;

/**
 * HUD which display the next tetramino
 * 
 * @author lamao
 *
 */
public class SDANextTetraminoState extends BasicGameState
{
	
	/** Current displayed tetramino */
	private SDATetramino _tetramino = null;
	
	public SDANextTetraminoState(String name)
	{
		super(name);
		initGraphics();
	}
	
	/**
	 * Initializes all graphics
	 */
	private void initGraphics()
	{
		BitmapFont font = SDAResourceManager.getInstance()
				.getBmFont(SDAConstants.mainFont);
		BitmapText title = new BitmapText(font, false);
		title.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		title.setText("NEXT");
		title.setLocalTranslation(0, SDAConstants.cellSize * 5, 0);
		title.setSize(0.75f);
		title.setUseKerning(true);
		title.update();
		rootNode.attachChild(title);
		
		rootNode.updateRenderState();
		
	}
	
	/**
	 * Sets new tetramino for displaying in HUD. Old tetramino is automatically
	 * detached
	 * 
	 * @param tetramino - new tetramino
	 */
	public void setTetramino(SDATetramino tetramino)
	{
		if (_tetramino != null)
		{
			rootNode.detachChild(_tetramino.getRootNode());
		}
		_tetramino = tetramino;
		rootNode.attachChild(tetramino.getRootNode());
		tetramino.getRootNode().setLocalTranslation(new Vector3f());
		rootNode.updateRenderState();
	}
	
}
