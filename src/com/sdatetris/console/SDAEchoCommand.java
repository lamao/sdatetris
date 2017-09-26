/* 
 * SDADebugCommand.java 10.03.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved. 
 */
package com.sdatetris.console;

/**
 * Command for console debugging. It works like 'echo' command by printing
 * command name.
 * @author lamao
 */
public class SDAEchoCommand implements ISDACommandHandler
{

	/* (non-Javadoc)
	 * @see com.sdatetris.gamestates.ISDACommandHandler#execute(java.lang.String)
	 */
	@Override
	public String execute(String[] args)
	{
		return "Command was '" + args[0] + "'";
	}

}
