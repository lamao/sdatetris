/*
 * SDAControlsWindow.java 20.02.2010
 * 
 *  Copyright 2010 sdaTetris
 *  All rights reserved
 *  
 */
package com.sdatetris.gamestates.mainmenu;

import com.jme.input.KeyInput;
import com.jme.system.DisplaySystem;
import com.jmex.bui.BButton;
import com.jmex.bui.BLabel;
import com.jmex.bui.BList;
import com.jmex.bui.BWindow;
import com.jmex.bui.BuiSystem;
import com.jmex.bui.Spacer;
import com.jmex.bui.layout.VGroupLayout;
import com.jmex.bui.util.Dimension;
import com.sdatetris.SDAOptions;

/** Window for setting controls */
public class SDAControlsWindow extends BWindow
{
	public SDAControlsWindow(String name)
	{
		super(name, BuiSystem.getStyle(), new VGroupLayout());
		
		buildGUI();
	}
	
	/** Builds GUI */
	private void buildGUI()
	{
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
        setSize((int) (display.getWidth() / 2),
                        (int) ((display.getHeight() * 0.75f)));
        Dimension prefSize = new Dimension(200, 30);
        
        BLabel title = new BLabel("Controls", "label_decorated");
        add(title);
        add(new Spacer(prefSize));
        
        KeyInput ki = KeyInput.get();
        BList controls = new BList(new Object[] {
        		"Move Left: " + ki.getKeyName(SDAOptions.leftKey),
        		"Move Right: " + ki.getKeyName(SDAOptions.rightKey),
        		"Move Down: " + ki.getKeyName(SDAOptions.downKey),
        		"Move Rotate: " + ki.getKeyName(SDAOptions.rotateKey),
        		"Next on/off: " + ki.getKeyName(SDAOptions.nextVisibleKey),
        		"Score on/off: " + ki.getKeyName(SDAOptions.scoreVisibleKey),
        		"FPS on/off: " + ki.getKeyName(SDAOptions.fpsVisibleKey),
        		"Pause: " + ki.getKeyName(KeyInput.KEY_P),
        		"Main menu: " + ki.getKeyName(KeyInput.KEY_ESCAPE)
        });
        controls.invalidate();
        add(controls);
        
        add(new Spacer(prefSize));
        
        BButton backBtn = new BButton("Back");
        backBtn.setPreferredSize(prefSize);
        backBtn.setAction(SDABackListener.CMD_BACK);
        backBtn.addListener(new SDABackListener(this));
        add(backBtn);
        
        center();
	}

}
