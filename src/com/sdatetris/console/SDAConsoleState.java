/* 
 * SDAConsoleState.java 10.03.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved. 
 */
package com.sdatetris.console;

import java.util.HashMap;
import java.util.Map;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.scene.Text;
import com.jme.system.DisplaySystem;
import com.jmex.game.state.BasicGameState;

/**
 * Game console. Can be used for debugging or any other purposes.
 * Command name should be separated by space from its arguments. The syntax of
 * rest command depends on concrete command.<br>
 * Now it supports just one line.<br>
 * Default controls are:<br>
 * <li><b>Backspace</b> removes one preceding character </li>
 * <li><b>CTRL+Backspace</b> removes whole line</li>
 * <li><b>ENTER</b> executes command</li>
 * <li><b>'exit'</b> command is for console disabling</li><br>
 * <br>
 * <b>NOTE: </b> Console is not modal. That is keys intersection is not 
 * maintained. You should do it youself.
 * @author lamao
 */
public class SDAConsoleState extends BasicGameState implements KeyInputListener
{
	/** Name of this game state */
	public final static String STATE_NAME = "console";
	
	/** Characters used for splitting command into arguments */
	public final static String SPLITTERS = "[ =]";
	
	/** Promt for console */
	private final static String PROMT = "> ";
	
	/** Registered commands */
	Map<String, ISDACommandHandler> _commands = 
		new HashMap<String, ISDACommandHandler>();

	/** Visual text components for displaying commands */
	Text _console = null;
	
	public SDAConsoleState(String name)
	{
		super(name);
		
		initGUI();
		bindKeys();
		
		add("exit", new SDAExitHandler());
		
		rootNode.updateRenderState();
	}
	
	/** Initializes all gui */
	private void initGUI()
	{
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		_console = Text.createDefaultTextLabel("console", PROMT);
		_console.setLocalTranslation(0, display.getHeight() 
				- _console.getHeight(), 0);
		rootNode.attachChild(_console);
	}
	
	private void bindKeys()
	{
		KeyInput.get().addListener(this);
	}
	
	public void add(String command, ISDACommandHandler handler)
	{
		_commands.put(command, handler);
	}
	
	/**
	 * Find handler for given command and call its <code>execute</code>
	 * method. 
	 * @param cmd - full command
	 * @return message from handler or error message if command is not supported
	 */
	public String execute(String cmd)
	{
		int spaceIndex = cmd.indexOf(' ', PROMT.length());
		String name = cmd.substring(PROMT.length(), 
				spaceIndex != -1 ? spaceIndex : cmd.length()); 
			
		ISDACommandHandler handler = _commands.get(name);
		String result = null;
		if (handler == null)
		{
			result = "Command <" + name +"> is not supported";
		}
		else
		{
			String[] arguments = cmd.substring(PROMT.length(), cmd.length())
					.split(SPLITTERS);
			result = handler.execute(arguments);
		}
		return result;
	}
	
	/* (non-Javadoc)
	 * @see com.jme.input.KeyInputListener#onKey(char, int, boolean)
	 */
	@Override
	public void onKey(char character, int keyCode, boolean pressed)
	{
		if (!pressed || !isActive())
		{
			return;
		}
		
		if (keyCode == KeyInput.KEY_RETURN)
		{
			String result = execute(_console.getText().toString());
			_console.print(PROMT + (result == null ? "" : result));
		}
		else if (keyCode == KeyInput.KEY_BACK)
		{
			if (KeyInput.get().isControlDown())
			{
				_console.print(PROMT);
			}
			else
			{
				StringBuffer s = _console.getText();
				if (s.length() > PROMT.length())
				{
					s.deleteCharAt(s.length() - 1);
				}
			}
		}
		else if (isValidCharacter(character))
		{
			_console.getText().append(character);
		}
		
	}
	
	private boolean isValidCharacter(char ch)
	{
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || 
				(ch >= '0' && ch <= '9') || (ch == '.') || (ch == '=') || 
				(ch == '-') || (ch == ' ');
	}

	/* (non-Javadoc)
	 * @see com.jmex.game.state.GameState#setActive(boolean)
	 */
	@Override
	public void setActive(boolean active)
	{
		if (active)
		{
			_console.print(PROMT);
		}
		super.setActive(active);
	}
	
	/** Default handler of 'exit' command */ 
	private class SDAExitHandler implements ISDACommandHandler
	{
		@Override
		public String execute(String[] args)
		{
			setActive(false);
			return null;
		}
	}

}
