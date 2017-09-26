/* 
 * SDAFieldListener.java 13.03.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved. 
 */
package com.sdatetris.gamestates.game;

import java.io.File;
import java.util.List;

import com.jme.animation.SpatialTransformer;
import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Controller;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.event.TrackStateAdapter;
import com.jmex.bui.BComponent;
import com.jmex.bui.BDialogMessage;
import com.jmex.bui.BInputBox;
import com.jmex.bui.BLabel;
import com.jmex.bui.BTitleBar;
import com.jmex.bui.BuiSystem;
import com.jmex.bui.UserResponse;
import com.jmex.bui.enumeratedConstants.DialogOptions;
import com.jmex.bui.enumeratedConstants.DisplayStyleOptions;
import com.jmex.bui.enumeratedConstants.IconOptions;
import com.jmex.bui.enumeratedConstants.TitleOptions;
import com.jmex.bui.event.DialogListener;
import com.jmex.game.state.GameStateManager;
import com.sdatetris.SDAConstants;
import com.sdatetris.SDAOptions;
import com.sdatetris.SDAResourceManager;
import com.sdatetris.effects.SDAParticleGlow;
import com.sdatetris.gamelogic.ISDAFieldListener;
import com.sdatetris.gamelogic.SDAField;
import com.sdatetris.gamelogic.SDALevelUtils;
import com.sdatetris.gamestates.mainmenu.SDAMenuState;
import com.sdatetris.gamestates.mainmenu.SDARecord;

/**
 * Class for listening <code>SDAField</code> actions.
 * @author lamao
 */
public class SDAFieldListener implements ISDAFieldListener
{
	/** Owner for listener. */
	private SDAMainState _owner = null;
	
	/** Effect for level up */
	private SDAParticleGlow _glow = new SDAParticleGlow();
	
	/** Field rotator */
	private SpatialTransformer _rotator = null;
	
	public SDAFieldListener(SDAMainState owner)
	{
		_owner = owner;
		createFieldRotator(SDAOptions.interLevelEffectTime);
	}
	
	@Override
	public void gameOver(SDAField sender)
	{
		if (SDAOptions.playSounds)
		{
			playGameOverSounds();
		}
		
		checkRecords(_owner.getScore(), _owner.getLines(), _owner.getLevel());
	}

	@Override
	public void lineCompleted(SDAField sender, int numberOfNewLines)
	{
		SDALevelUtils utils = SDALevelUtils.getInstance();
		
		_owner.setScore(_owner.getScore() + SDAConstants
				.linesMultiplier[numberOfNewLines - 1]);
		_owner.setLines(_owner.getLines() + numberOfNewLines);
		if (SDAOptions.playSounds)
		{
			SDAResourceManager.getInstance().getSound(SDAConstants.lineSound)
					.play();
		}
	
		//test for new level
		if (_owner.getScore() >= utils.getNecessaryScore(_owner.getLevel() + 1))
		{
			if (_owner.getLevel() + 1 >= SDAConstants.finalLevel)
			{
				// victory
				sender.setPause(true);
				if (SDAOptions.playSounds)
				{
					playVictorySounds();
				}				
				checkRecords(_owner.getScore(), _owner.getLines(), _owner.getLevel());
			}
			else
			{
				// next level
				float effectTime = SDAOptions.interLevelEffectTime;
				_glow.runEffect(_owner.getRootNode(), effectTime);
				runFieldRotation(effectTime);
				if (SDAOptions.playSounds)
				{
					playInterLevelSounds();
				}
			}
		}
	}

	@Override
	public void moved(SDAField sender)
	{}

	@Override
	public void placed(SDAField sender)
	{
		if (SDAOptions.playSounds)
		{
			SDAResourceManager.getInstance().getSound(
					SDAConstants.placeSound).play();
		}
	}

	@Override
	public void rotated(SDAField sender)
	{}
	
	@Override
	public void tetraminoChanged(SDAField sender)
	{
		SDANextTetraminoState hud = _owner.getNextHUD();
		hud.setTetramino(sender.getNextTetramino());
		hud.getRootNode().updateGeometricState(
				Timer.getTimer().getFrameRate(), true);
	}
	
	
	/** Checks records table and insert current values if need */
	private void checkRecords(long score, int lines, int level)
	{
		List<SDARecord> records = SDARecord.load(
				new File(SDAConstants.recordsFile));
		
		int i = 0;
		while (i < records.size() && records.get(i).score > score)
		{
			i++;				
		}
		if (i < records.size() || i < 10)
		{
			addNewRecord(i, records, new SDARecord(null, score, lines, level));
		}
	}
	
	/** Adds new record to records list and saves changes */
	private void addNewRecord(final int i, final List<SDARecord> list, 
			final SDARecord record)
	{
		final BInputBox input = new BInputBox("Input your name",
				new BTitleBar("title bar", new BLabel("Input"), 
							  TitleOptions.NONE),
				new BDialogMessage("dlg mess", new BLabel("Input your name"), 
								   IconOptions.QUESTION,
								   DisplayStyleOptions.WINDOWS),
				DialogOptions.OK, BuiSystem.getStyle());
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		input.setSize(display.getWidth() / 2, display.getHeight() / 3);
		
		input.setModal(true);
		input.center();			

		MouseInput.get().setCursorVisible(true);
		BuiSystem.addWindow(input);
		_owner.getRootNode().attachChild(BuiSystem.getRootNode());

		// TODO: Replace following code by modal dialog behavior
		input.setDialogListener(new DialogListener()
		{
			@Override
			public void responseAvailable(UserResponse response,
					BComponent source)
			{
				record.name = input.getInputText();
				list.add(i, record);
				if (list.size() > 10)
				{
					list.remove(list.size() - 1);
				}

				SDARecord.save(new File(SDAConstants.recordsFile), list);
				
				MouseInput.get().setCursorVisible(false);
				_owner.getRootNode().detachChild(BuiSystem.getRootNode());
				
				_owner.setActive(false);
				GameStateManager.getInstance().getChild(
						SDAMenuState.STATE_NAME).setActive(true);
			}
		});
		
	}
	
	/** Creates rotator for field */
	private void createFieldRotator(float effectTime)
	{
		_rotator = new SpatialTransformer(1);
		_rotator.setObject(_owner.getRootNode(), 0, -1);
		
		Quaternion x0=new Quaternion();
        x0.fromAngleAxis(0,Vector3f.UNIT_Y);
        _rotator.setRotation(0,0,x0);
        
        /* Assign a rotation for object 0 at time 2 to rotate 180 degrees 
        * around the z axis
        */
        Quaternion x180=new Quaternion();
        x180.fromAngleAxis(FastMath.DEG_TO_RAD*180,Vector3f.UNIT_Y);
        _rotator.setRotation(0,effectTime / 2,x180);
        
        /* Assign a rotation for object 0 at time 4 to rotate 360 degrees 
         * around the z axis
         */
        Quaternion x360=new Quaternion();
        x360.fromAngleAxis(FastMath.DEG_TO_RAD*360,Vector3f.UNIT_Y);
        _rotator.setRotation(0,effectTime,x360);
        
        /* Prepare my controller to start moving around */
        _rotator.interpolateMissing();
		
	}
	
	/** Run rotation of field. Field is rotated asynchronously */
	@SuppressWarnings("serial")
	private void runFieldRotation(final float effectTime)
	{
		_rotator.setCurTime(0);
		_rotator.setMaxTime(effectTime / 2);
        _owner.getRootNode().addController(_rotator);
        _owner.getField().setPause(true);
        
        //pauses field
        _owner.getRootNode().addController(new Controller()
        {
        	public void update(float time)
        	{
        		if (_rotator.getCurTime() >= effectTime / 2)
        		{
        			//_owner.getRootNode().removeController(_rotator);
        			_owner.getRootNode().removeController(this);
        			_owner.setNextLevel();
        			
        			_rotator.setMaxTime(effectTime);
        			_rotator.setActive(true);
        			_owner.getRootNode().addController(new Controller()
        			{
        				public void update(float time)
        				{
        					if (_rotator.getCurTime() >= effectTime)
        					{
        						_owner.getRootNode().removeController(_rotator);
        						_owner.getRootNode().removeController(this);
        						_owner.getField().setPause(false);
        					}
        				}
        			});
        		}
        	}
        });
        
	}
	
	private void playInterLevelSounds()
	{
		// play inter-level sound
        SDAResourceManager manager = SDAResourceManager.getInstance();
        final AudioTrack music = manager.getSound(SDAConstants.musicSound);
        final AudioTrack levelSound = manager.getSound(SDAConstants.levelSound);
        if (music.isPlaying())
        {
        	music.pause();
        }
        levelSound.play();
        levelSound.addTrackStateListener(new TrackStateAdapter()
        {
        	public void trackStopped(AudioTrack track)
        	{
        		track.removeTrackStateListener(this);
        		if (SDAOptions.playMusic && _owner.isActive())
        		{
        			music.setVolume(0.1f);
        			music.setTargetVolume(1.0f);
        			music.setVolumeChangeRate(1f / 3);
        			music.play();
        		}
        	}
        });
	}
	
	private void playGameOverSounds()
	{
		SDAResourceManager manager = SDAResourceManager.getInstance();
		AudioTrack music = manager.getSound(SDAConstants.musicSound);
		music.stop();
		
		AudioTrack gameover = manager.getSound(SDAConstants.gameoverSound);
		gameover.play();
	}
	
	private void playVictorySounds()
	{
		SDAResourceManager manager = SDAResourceManager.getInstance();
		AudioTrack music = manager.getSound(SDAConstants.musicSound);
		music.stop();
		
		AudioTrack victory = manager.getSound(SDAConstants.victorySound);
		victory.play();
	}
	
}
