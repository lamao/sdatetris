/*
 * SDAMenuState.java 08.02.2010
 * 
 *  Copyright 2010 sdaTetris
 *  All rights reserved
 *  
 */

package com.sdatetris.gamestates.mainmenu;

import com.jme.input.MouseInput;
import com.jme.scene.Node;
import com.jme.scene.Text;
import com.jme.system.DisplaySystem;
import com.jmex.bui.BWindow;
import com.jmex.bui.BuiSystem;
import com.jmex.game.state.BasicGameState;
import com.sdatetris.SDAConstants;

/**
 * Menu game state
 * 
 * @author lamao
 *
 */
public class SDAMenuState extends BasicGameState 
{
	/** Name of this state in <code>GameStateManager</code> */
	public final static String STATE_NAME = "Menu";
	
	/** Window with main menu */
	private BWindow _menuWindow = null;
	
	/** Node used for handling static text */
	private Node _statNode = new Node("stat node");
	
	/**
	 * Constructor
	 * 
	 * @param name - name of game state
	 */
	public SDAMenuState(String name) 
	{
		super(name);
		
		initGraphics();
		buildGUI();
		
		_statNode.updateRenderState();
		rootNode.updateRenderState();
	}
	
	/**
	 * Initializes graphics for this state
	 */
	private void initGraphics()
	{
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		// create label with build number
		Text buildNumber = Text.createDefaultTextLabel("build number", 
				"Build #" + SDAConstants.buildNumber);
		buildNumber.setLocalTranslation(
				0, display.getHeight() - buildNumber.getHeight(), 0);
		_statNode.attachChild(buildNumber);
	}
	
	/**
	 * Builds menu GUI
	 */
	private void buildGUI()
	{
		_menuWindow = new SDAMenuWindow("Main menu", new SDAMainMenuListener(this));
	}
	
	@Override
	public void setActive(boolean active)
	{
		if (active) 
		{
			BuiSystem.addWindow(_menuWindow);
			rootNode.attachChild(BuiSystem.getRootNode());
		}
		else
		{
			_menuWindow.dismiss();
			rootNode.detachChild(BuiSystem.getRootNode());
		}
		
		MouseInput.get().setCursorVisible(active);
		super.setActive(active);
	}
	
	@Override
	public void render(float tpf)
	{
		super.render(tpf);
		DisplaySystem.getDisplaySystem().getRenderer().draw(_statNode);
	}
	
}
