/* 
 * SDAMain.java 08.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */

package com.sdatetris;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.Callable;

import com.jme.input.InputHandler;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.Timer;
import com.jmex.bui.BuiSystem;
import com.jmex.bui.PolledRootNode;
import com.jmex.game.StandardGame;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.load.LoadingGameState;
import com.sdatetris.console.SDAConsoleState;
import com.sdatetris.gamestates.SDALogoState;
import com.sdatetris.gamestates.game.SDAMainState;

/**
 * Main class of the application
 * 
 * @author lamao
 *
 */

public class SDAMain 
{
	private static StandardGame _game = null;
	
	public static void main(String[] args) 
	{
		_game = new StandardGame("sdaTetris");
		_game.start();
		
		LoadingGameState loading = new LoadingGameState();
		GameStateManager.getInstance().attachChild(loading);
		
		BuiSystem.init(new PolledRootNode(Timer.getTimer(), new InputHandler()), 
				"/data/style2.bss");
		
		GameTaskQueueManager.getManager().update(new LoadingTask(loading));
				
		loading.setActive(true);
		
		
	}
	
	public static void exit()
	{
		_game.shutdown();
	}
	
	/** Task for loading resources */
	public static class LoadingTask implements Callable<Void>
	{
		/** Game state for displaying progress */
		private LoadingGameState _loadingState = null;
		
		/** Iterator for current line to parse */
		private Iterator<String> _it = null;
		
		/** Number of loaded resources */
		private int _progress = 0;
		
		public LoadingTask(LoadingGameState loadingState)
		{
			_loadingState = loadingState;
			SDAResourceManager.getInstance().readConfig(new File("data/resources.txt"));
			_it = SDAResourceManager.getInstance().getConfig().iterator();
		}

		@Override
		public Void call() throws Exception
		{
			if (_it.hasNext())
			{
				SDAResourceManager manager = SDAResourceManager.getInstance();
				String line = _it.next();
				_progress += manager.parseOneLine(line);
				_loadingState.setProgress((float)_progress 
						/ manager.getConfig().size());
				GameTaskQueueManager.getManager().update(this);
			}
			else
			{
				SDAResourceManager.getInstance().loadAll(new File("data/resources.txt"));

				SDAConsoleState console = new SDAConsoleState("console");
				GameStateManager.getInstance().attachChild(console);
				
				SDALogoState logo = new SDALogoState(SDALogoState.STATE_NAME);
				GameStateManager.getInstance().attachChild(logo);

				SDAMainState game = new SDAMainState(SDAMainState.STATE_NAME);
				GameStateManager.getInstance().attachChild(game);
				
				_loadingState.setProgress(100);
				GameStateManager.getInstance().detachChild(_loadingState);
				
				logo.setActive(true);
			}
			return null;
		}
		
	}
}
