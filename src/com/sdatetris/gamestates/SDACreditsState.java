/*
 * SDACreditsState.java 08.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 * 
 */

package com.sdatetris.gamestates;

import java.util.LinkedList;
import java.util.List;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.math.Vector3f;
import com.jme.scene.Text;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameStateManager;
import com.sdatetris.gamestates.mainmenu.SDAMenuState;

/**
 * Credits game state
 * 
 * @author lamao
 *
 */
public class SDACreditsState extends BasicGameState 
{
	//------------------- Constants --------------------------------------------
	/** Name of this state in <code>GameStateManager</code> */
	public final static String STATE_NAME = "Credits";
	
	private final String CMD_BACK = "back";
	
	/** Sets the speed of moving credits up */ 
	private final float UPDATE_INTERVAL = 0.05f;
	
	/** Text for credits. Last few empty lines act as placeholders. They
	 * keep free space when first lines are jumping to bottom.*/
	private final String[] _developers = new String[] {
		"Team Lead: ",
		"   lamao",
		"Programming:",
		"   lamao",
		"GUI:",
		"   lamao",
		"Sounds & Music:",
		"   http://www.lunerouge.org",
		"   Stardust project",
		"",
		"",
		"",
		"",
		"",
		"",
		""
		
	};
	
	/** Time since last update. In seconds */
	private float _lastUpdate = 0;
	
	/** List of labels contains credits */
	private List<Text> _labels = new LinkedList<Text>();
	
	/** Y coordinate of the most bottom label */
	private float _minPos = 0;

	public SDACreditsState(String name) 
	{
		super(name);
		
		buildGUI();
		
		KeyBindingManager keyManager = KeyBindingManager.getKeyBindingManager();
		keyManager.set(CMD_BACK, KeyInput.KEY_ESCAPE);
	}
	
	/** Builds GUI */
	private void buildGUI()
	{
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		Text logoText = Text.createDefaultTextLabel("title", "CREDITS");
		logoText.setLocalTranslation(
				display.getWidth() / 2 - logoText.getWidth() / 2, 
				display.getHeight() - logoText.getHeight() * 3,
				0);
		rootNode.attachChild(logoText);
		
		Text credits = null;		
		for (int i = 0; i < _developers.length; i++)
		{
			credits = Text.createDefaultTextLabel("credits" + i, 
					_developers[i]);
			rootNode.attachChild(credits);
			
			_labels.add(credits);
		}
		
		
		rootNode.updateRenderState();
	}
	
	/** Resets positions of all labels */
	private void resetPositions()
	{
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		_minPos = 0;
		
		for (Text label : _labels)
		{
			label.setLocalTranslation(display.getWidth() / 3, 
					_minPos - label.getHeight(), 0);
			_minPos -= label.getHeight();
		}
	}

	@Override
	public void setActive(boolean active)
	{
		if (active)
		{
			resetPositions();
		}
		super.setActive(active);
	}
	
	@Override
	public void update(float tpf) 
	{
		updateText(tpf);
		
		KeyBindingManager keyManager = KeyBindingManager.getKeyBindingManager();
		if (keyManager.isValidCommand(CMD_BACK, false))
		{
			setActive(false);
			GameStateManager.getInstance().getChild(SDAMenuState.STATE_NAME)
					.setActive(true);
		}
		super.update(tpf);
	}

	/** 
	 * Scrolls credits text
	 * 
	 *  @param tpf seconds per frame
	 */
	private void updateText(float tpf)
	{
		if (Timer.getTimer().getTimeInSeconds() - _lastUpdate >= UPDATE_INTERVAL)
		{
			_lastUpdate = Timer.getTimer().getTimeInSeconds();
			
			for (Text label : _labels)
			{
				Vector3f pos = label.getLocalTranslation().addLocal(0, 1, 0);
				if (pos.y > DisplaySystem.getDisplaySystem().getHeight() - 100)
				{
					pos.y = Math.min(0, _minPos - label.getHeight());
					_minPos = pos.y;
				}
				else if (pos.y - _minPos < 1.01f)
				{
					_minPos = pos.y;
				}
				
				label.updateGeometricState(tpf, true);
			}
		}
	}
}
