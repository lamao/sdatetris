/*
 * SDAMainMenuListener.java 11.02.2010
 * 
 *  Copyright 2010 sdaTetris
 *  All rights reserved
 *  
 */

package com.sdatetris.gamestates.mainmenu;

import com.jmex.bui.BComponent;
import com.jmex.bui.BWindow;
import com.jmex.bui.BuiSystem;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import com.sdatetris.SDAMain;
import com.sdatetris.gamestates.SDACreditsState;
import com.sdatetris.gamestates.game.SDAMainState;

/**
 * Listener for {@link SDAMainState}
 * 
 * @author lamao
 *
 */
public class SDAMainMenuListener implements ActionListener
{
	//------------------- Constants --------------------------------------------
	public final static String CMD_GAME = "game";
	public final static String CMD_OPTIONS = "options";
	public final static String CMD_CONTROLS = "controls";
	public final static String CMD_RECORDS = "records";
	public final static String CMD_CREDITS = "credits";
	public final static String CMD_EXIT = "exit";
	
	
	
	//------------------- Variables --------------------------------------------
	
	/**
	 * Menu state which actions this class listens
	 */
	private GameState _menuState = null;
	
	
	/**
	 * Constructor
	 * @param menuWindow - menu state which actions this class listens
	 */
	public SDAMainMenuListener(GameState menuState)
	{
		super();
		
		assert menuState != null;
		_menuState = menuState;
	}



	@Override
	public void actionPerformed(ActionEvent event)
	{
		String nextState = null;
		
		if (event.getAction().equals(CMD_EXIT))
		{
			SDAMain.exit();
		}
		else if (event.getAction().equals(CMD_GAME))
		{
			nextState = SDAMainState.STATE_NAME;
		}
		else if (event.getAction().equals(CMD_RECORDS))
		{
			BWindow window = ((BComponent)event.getSource()).getWindow();
			BuiSystem.push(window);
			window.dismiss();
			SDARecordsWindow options = new SDARecordsWindow("Options");
			BuiSystem.addWindow(options);
		}
		else if (event.getAction().equals(CMD_CREDITS))
		{
			nextState = SDACreditsState.STATE_NAME;
			SDACreditsState credits = new SDACreditsState(
					SDACreditsState.STATE_NAME);
			GameStateManager.getInstance().attachChild(credits);
			
		}
		else if (event.getAction().equals(CMD_OPTIONS))
		{
			BWindow window = ((BComponent)event.getSource()).getWindow();
			BuiSystem.push(window);
			window.dismiss();
			SDAOptionsWindow options = new SDAOptionsWindow("Options");
			BuiSystem.addWindow(options);
		}
		else if (event.getAction().equals(CMD_CONTROLS))
		{
			BWindow window = ((BComponent)event.getSource()).getWindow();
			BuiSystem.push(window);
			window.dismiss();
			SDAControlsWindow controls = new SDAControlsWindow("Controls");
			BuiSystem.addWindow(controls);
		}

		if (nextState != null)
		{
			_menuState.setActive(false);
			GameStateManager.getInstance().getChild(nextState).setActive(true);
		}
	}

}
