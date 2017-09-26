/*
 * SDARecord.java 21.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */
package com.sdatetris.gamestates.mainmenu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


/** Record for one games.  */ 
public class SDARecord
{
	public String name = null;
	public long score = 0;
	public int lines = 0;
	public int level = 0;
	
	public SDARecord()
	{
		this("", 0, 0, 0);
	}
	
	public SDARecord(String aName, long aScore, int aLines, int aLevel)
	{
		name = aName;
		score = aScore;
		lines = aLines;
		level = aLevel;
	}
	
	@Override
	public String toString()
	{
		return name + " " + score + " " + lines + " " + level;
	}
	
	/** Loads a list of records from file
	 * @param file
	 */
	public static List<SDARecord> load(File file)
	{
		List<SDARecord> result = new LinkedList<SDARecord>();
		try
		{
			
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;

			do
			{
				line = reader.readLine();
				if (line == null)
				{
					break;
				}
				SDARecord record = new SDARecord();
				String[] items = line.split(" +");
				record.name = items[0];
				record.score = Long.parseLong(items[1]);
				record.lines = Integer.parseInt(items[2]);
				record.level = Integer.parseInt(items[3]);
				result.add(record);
			}
			while (line != null && result.size() < 10);
		
			reader.close();
			
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Saves a list of records to file
	 * @param file 
	 */
	public static void save(File file, List<SDARecord> list)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			int i = 0;
			for (SDARecord record : list)
			{
				writer.write(record.toString() + "\n");
				if (++i >= 10)
				{
					break;
				}				
			}
			writer.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
}
