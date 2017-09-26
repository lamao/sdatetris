/*
 * SDALogoState.java 08.02.2010
 * 
 * Copyright 2010 sdaTetirs
 * All rights reserved
 */

package com.sdatetris.gamestates;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Text;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jmex.angelfont.BitmapFont;
import com.jmex.angelfont.BitmapFontLoader;
import com.jmex.angelfont.BitmapText;
import com.jmex.angelfont.BitmapFont.Align;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.GameStateManager;
import com.sdatetris.SDAConstants;
import com.sdatetris.SDAResourceManager;
import com.sdatetris.gamestates.mainmenu.SDAMenuState;

/**
 * Logo game state.
 * 
 * @author lamao
 *
 */
public class SDALogoState extends BasicGameState 
{
	//------------------- Constants --------------------------------------------
	public final static String STATE_NAME = "Logo";
	
	private final String CMD_NEXT = "next";
	
	/** Time when logo was activated */
	private float _onActiveTime = 0;
	
	public SDALogoState(String name) 
	{
		super(name);
		
		buildGraphics();
		
		KeyBindingManager manager = KeyBindingManager.getKeyBindingManager();
		manager.set(CMD_NEXT, KeyInput.KEY_SPACE);
		
	}
	
	/** Builds graphics for logo */
	private void buildGraphics()
	{
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		BitmapFont font = BitmapFontLoader.loadDefaultFont();
		
		Text info = Text.createDefaultTextLabel("info", "Press 'Space' to skip");
		info.setLocalTranslation(display.getWidth() / 2 - info.getWidth()  /2, 
								 0, 0);
		rootNode.attachChild(info);
		
		
		Quad tetrisLogo = new Quad("tet emblem", 5, 2);
		tetrisLogo.setLocalTranslation(-10, 7, 0);
		tetrisLogo.setRenderState(SDAResourceManager.getInstance()
				.getTexture(SDAConstants.logoTexture));
		tetrisLogo.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		rootNode.attachChild(tetrisLogo);
		
		Quad monkeyLogo = new Quad("monkey logo", 4, 4);
		monkeyLogo.setLocalTranslation(10, 7, 0);
		monkeyLogo.setRenderState(SDAResourceManager.getInstance()
				.getTexture(SDAConstants.monkeyTexture));
		monkeyLogo.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		rootNode.attachChild(monkeyLogo);
		
		monkeyLogo.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		BlendState bs = display.getRenderer().createBlendState();
		bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
		bs.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
		bs.setBlendEnabled(true);
		bs.setEnabled(true);
		monkeyLogo.setRenderState(bs);
		monkeyLogo.updateRenderState();
		
		BitmapFont mainFont = SDAResourceManager.getInstance()
				.getBmFont(SDAConstants.mainFont);
		BitmapText text = new BitmapText(mainFont, false);
		text.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		text.setSize(2);
		text.setText("S.D.A. T e t r i s");
		text.setAlignment(Align.Center);
		text.setLocalTranslation(0, text.getLineHeight() * 0.75f, 0);
		text.update();
		text.setDefaultColor(new ColorRGBA(190f/255f, 170f/255f, 35f/255f, 1));
		rootNode.attachChild(text);
		
		BitmapText powered = new BitmapText(mainFont, false);
		powered.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		powered.setSize(1);
		powered.setText("powered by jMonkeyEngine");
		powered.setAlignment(Align.Center);
		powered.setLocalTranslation(0, 
				text.getLocalTranslation().y - text.getLineHeight(), 0);
		powered.setDefaultColor(new ColorRGBA(20f/255f, 150f/255f, 30f/255f, 1));
		powered.update();
		rootNode.attachChild(powered);
		
		BitmapText developers = new BitmapText(font, false);
		developers.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
		developers.setSize(0.75f);
		developers.setAlignment(Align.Center);
		developers.setLocalTranslation(0, -4, 0);
		StringBuffer buffer = new StringBuffer("Developers\n");
		for (int i = 0; i < SDAConstants.developers.length; i++)
		{
			buffer.append(SDAConstants.developers[i] + "\n");
		}
		developers.setText(buffer.toString());
		developers.update();
		rootNode.attachChild(developers);
		
		
		rootNode.updateRenderState();
	}
	
	@Override
	public void setActive(boolean active)
	{
		if (active)
		{
			_onActiveTime = Timer.getTimer().getTimeInSeconds();
		}
		super.setActive(active);
	}

	@Override
	public void update(float tpf) 
	{
		KeyBindingManager keyManager = KeyBindingManager.getKeyBindingManager();
		if (Timer.getTimer().getTimeInSeconds() - _onActiveTime 
				> SDAConstants.logoDelay || 
			keyManager.isValidCommand(CMD_NEXT, false))
		{
			GameStateManager.getInstance().detachChild(this);
			SDAMenuState menu = new SDAMenuState(SDAMenuState.STATE_NAME);
			GameStateManager.getInstance().attachChild(menu);
			menu.setActive(true);
		}
		
		super.update(tpf);
	}
	
}
