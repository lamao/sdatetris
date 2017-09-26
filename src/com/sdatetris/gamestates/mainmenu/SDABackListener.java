/*
 * SDABackListener.java 21.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */
package com.sdatetris.gamestates.mainmenu;

import com.jmex.bui.BWindow;
import com.jmex.bui.BuiSystem;
import com.jmex.bui.event.ActionEvent;
import com.jmex.bui.event.ActionListener;

/** Listener for <code>BWindow</code> which handles 'Back' event. It returns 
 * to previous window (Assuming there is at least one window in <code>BuiSystem
 * </code> history. 
 */
public class SDABackListener implements ActionListener
{
	// Commands
	public static String CMD_BACK = "back";
	
	/** Owner of listener */
	private BWindow _owner = null;
	
	public SDABackListener(BWindow owner)
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
	}

}
