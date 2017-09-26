/* 
 * SDAConsoleCommand.java 10.03.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved. 
 */
package com.sdatetris.console;

/**
 * Command handler for console.
 * @author lamao
 */
public interface ISDACommandHandler
{
	/** Executes given command
	 * @param command - full string of command including its name.
	 */
	public String execute(String[] args);
}
