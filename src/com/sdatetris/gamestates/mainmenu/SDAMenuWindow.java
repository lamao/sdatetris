/* 
 * SDAMenuWindow.java 20.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */
package com.sdatetris.gamestates.mainmenu;

import com.jme.system.DisplaySystem;
import com.jmex.bui.BButton;
import com.jmex.bui.BWindow;
import com.jmex.bui.BuiSystem;
import com.jmex.bui.layout.VGroupLayout;
import com.jmex.bui.util.Dimension;

/** 
 * Main menu window. 
 * 
 * @author lamao
 */
public class SDAMenuWindow extends BWindow
{
	// Buttons 
	private BButton _newGameBtn = null;
	private BButton _optionsBtn = null;
	private BButton _keysBtn = null;
	private BButton _recordsBtn = null;
	private BButton _creditsBtn = null;
	private BButton _exitBtn = null;
	
	/** Listener for buttons. Actually listener for menu events*/
	private SDAMainMenuListener _listener = null;

	public SDAMenuWindow(SDAMainMenuListener listener)
	{
		this("Main menu", listener);
	}

	public SDAMenuWindow(String name, SDAMainMenuListener listener)
	{
		super(name, BuiSystem.getStyle(), new VGroupLayout());
		_listener = listener;
		buildGUI();
	}
	
	/** Builds gui for main menu */
	public void buildGUI()
	{
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
        setSize((int) (display.getWidth() / 2),
                        (int) ((display.getHeight() * 0.75f)));
        
        Dimension btnSize = new Dimension(200, 30);	//Prefered size for buttons

        _newGameBtn = new BButton("New Game");
        _newGameBtn.setPreferredSize(btnSize);
        _newGameBtn.setAction(SDAMainMenuListener.CMD_GAME);
        _newGameBtn.addListener(_listener);
        add(_newGameBtn);
        
        _optionsBtn = new BButton("Options");
        _optionsBtn.setPreferredSize(btnSize);
        _optionsBtn.setAction(SDAMainMenuListener.CMD_OPTIONS);
        _optionsBtn.addListener(_listener);
        add(_optionsBtn);
        
        _keysBtn = new BButton("Controls");
        _keysBtn.setPreferredSize(btnSize);
        _keysBtn.setAction(SDAMainMenuListener.CMD_CONTROLS);
        _keysBtn.addListener(_listener);
        add(_keysBtn);
        
        _creditsBtn = new BButton("Credits");
        _creditsBtn.setPreferredSize(btnSize);
        _creditsBtn.setAction(SDAMainMenuListener.CMD_CREDITS);
        _creditsBtn.addListener(_listener);
        add(_creditsBtn);
        
        _recordsBtn = new BButton("Records");
        _recordsBtn.setPreferredSize(btnSize);
        _recordsBtn.setAction(SDAMainMenuListener.CMD_RECORDS);
        _recordsBtn.addListener(_listener);
        add(_recordsBtn);

        _exitBtn = new BButton("Exit");
        _exitBtn.setPreferredSize(btnSize);
        _exitBtn.setAction(SDAMainMenuListener.CMD_EXIT);
        _exitBtn.addListener(_listener);
        add(_exitBtn);

        center();
	}
	
}
