/*
 * SDAOptionsState.java 18.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 * 
 */
package com.sdatetris.gamestates.mainmenu;

import com.jme.system.DisplaySystem;
import com.jmex.bui.BButton;
import com.jmex.bui.BCheckBox;
import com.jmex.bui.BContainer;
import com.jmex.bui.BLabel;
import com.jmex.bui.BWindow;
import com.jmex.bui.BuiSystem;
import com.jmex.bui.Spacer;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;
import com.jmex.bui.layout.VGroupLayout;
import com.jmex.bui.util.Dimension;
import com.sdatetris.SDAOptions;

/** Game screen for setting options */
public class SDAOptionsWindow extends BWindow
{

	// UI elements
	private BCheckBox _soundsCB = null;
	private BCheckBox _musicCB = null;
	private BButton _exitBtn = null;
	
	
	
	public SDAOptionsWindow(String name)
	{
		super(name, BuiSystem.getStyle(), new VGroupLayout());
		buildGUI();
	}
	
	/** Builds GUI for options screen */
	private void buildGUI()
	{
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
        setSize((int) (display.getWidth() / 2),
                        (int) ((display.getHeight() * 0.75f)));
        
        
        Dimension prefSize = new Dimension(200, 30);
        SDAOptionsListener listener = new SDAOptionsListener(this);
        
        BLabel title = new BLabel("Options", "label_decorated");
        title.setPreferredSize(prefSize);
        add(title);
        add(new Spacer(prefSize));
        
        _soundsCB = new BCheckBox("Sound");
        _soundsCB.setSelected(SDAOptions.playSounds);
        _soundsCB.setPreferredSize(prefSize);
        _soundsCB.setAction(listener.CMD_SOUND);
        _soundsCB.addListener(listener);
        
        _musicCB = new BCheckBox("Music");
        _musicCB.setSelected(SDAOptions.playMusic);
        _musicCB.setPreferredSize(prefSize);
        _musicCB.setAction(listener.CMD_MUSIC);
        _musicCB.addListener(listener);
        
        BContainer container = new BContainer("ab", new VGroupLayout());
        container.setStyleClass("list");
        container.add(_soundsCB);
        container.add(_musicCB);	
        add(container);
        
        add(new Spacer(prefSize));
        
        _exitBtn = new BButton("Back");
        _exitBtn.setPreferredSize(prefSize);
        _exitBtn.setAction(listener.CMD_BACK);
        _exitBtn.addListener(listener);
        add(_exitBtn);

        center();
	}

	
	/** Listener for options events */
	private class SDAOptionsListener implements ActionListener
	{
		// Commands
		public String CMD_BACK = "back";
		public String CMD_SOUND = "sound";
		public String CMD_MUSIC = "music";
		
		/** Window-owner of listener */
		private BWindow _owner = null;
		
		public SDAOptionsListener(BWindow owner)
		{
			_owner = owner;
		}
		
		@Override
		public void actionPerformed(ActionEvent event)
		{
			if (event.getAction().equals(CMD_BACK))
			{
				BuiSystem.back(_owner);
			}
			else if (event.getAction().equals(CMD_SOUND))
			{
				SDAOptions.playSounds = !SDAOptions.playSounds;
			}
			else if (event.getAction().equals(CMD_MUSIC))
			{
				SDAOptions.playMusic = !SDAOptions.playMusic;
			}
		}
		
	}
}
