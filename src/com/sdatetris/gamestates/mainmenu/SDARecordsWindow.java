/*
 * SDARecordsWindow 21.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */
package com.sdatetris.gamestates.mainmenu;

import java.io.File;
import java.util.List;

import com.jme.system.DisplaySystem;
import com.jmex.bui.BButton;
import com.jmex.bui.BLabel;
import com.jmex.bui.BWindow;
import com.jmex.bui.BuiSystem;
import com.jmex.bui.layout.VGroupLayout;
import com.jmex.bui.text.HTMLView;
import com.jmex.bui.util.Dimension;
import com.sdatetris.SDAConstants;

/** Displays top 10 records */
public class SDARecordsWindow extends BWindow
{

	
	public SDARecordsWindow(String name)
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

        BLabel label = new BLabel("Records", "label_decorated");
        label.setPreferredSize(prefSize);
        add(label);
        
        HTMLView table = new HTMLView("", getTableContents());
        add(table);
        
        BButton backBtn = new BButton("Back");
        backBtn.setPreferredSize(prefSize);
        backBtn.setAction(SDABackListener.CMD_BACK);
        backBtn.addListener(new SDABackListener(this));
        add(backBtn);
        
        center();
        
	}
	
	/** 
	 * Builds HTML text for records table
	 * 
	 *  @return contents for records table
	 */
	private String getTableContents()
	{
		List<SDARecord> records = SDARecord.load(new File(
				SDAConstants.recordsFile));
        StringBuffer contents = new StringBuffer(
        		"<table border=1 cellpadding=2 cellspacing=2 " +
        		"width=100% bgcolor=#AAAAAA>" +
        		"<tr><td><b>NAME</b></td><td> SCORE</td> <td>LINES</td> " +
        		"<td>LEVEL</td></tr>");
        for (int i = 0; i < records.size(); i++)
        {
        	contents.append("<tr><td><b>" + records.get(i).name + "</b></td>" 
        			+ "<td>" + records.get(i).score + "</td>" 
        			+ "<td>" +records.get(i).lines + "</td>" 
        			+ "<td>" +records.get(i).level + "</td></tr>");
        }
        for (int i = records.size(); i < 10; i++)
        {
        	contents.append("<tr><td><b>EMPTY</b></td><td>-</td><td>-</td>" +
        			"<td>-</td></tr><br>");
        }
        contents.append("</table>");
        
        return contents.toString();
	}
	
}