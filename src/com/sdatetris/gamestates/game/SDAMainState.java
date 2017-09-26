/*
 * SDAMainState.java 08.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 * 
 */
package com.sdatetris.gamestates.game;

import java.awt.Point;

import com.jme.bounding.BoundingBox;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.InputActionInterface;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.CullState;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;
import com.jmex.game.state.BasicGameState;
import com.jmex.game.state.BasicGameStateNode;
import com.jmex.game.state.GameState;
import com.jmex.game.state.GameStateManager;
import com.jmex.game.state.TextGameState;
import com.sdatetris.SDAConstants;
import com.sdatetris.SDAOptions;
import com.sdatetris.SDAResourceManager;
import com.sdatetris.console.ISDACommandHandler;
import com.sdatetris.console.SDAConsoleState;
import com.sdatetris.console.SDAEchoCommand;
import com.sdatetris.console.SDAWireFrameCommand;
import com.sdatetris.effects.SDAParticleGlow;
import com.sdatetris.gamelogic.SDAField;
import com.sdatetris.gamelogic.SDALevelUtils;
import com.sdatetris.gamestates.mainmenu.SDAMenuState;

/**
 * Main game state. It actually represents game. All game actions
 * are performed here. 
 * 
 * @author lamao
 *
 */
public class SDAMainState extends BasicGameStateNode<GameState> 
{
	//------------------- Constants --------------------------------------------
	/** Name of this state in <code>GameStateManager</code> */
	public final static String STATE_NAME = "Game";
	
	private final String CMD_MENU = "menu";
	private final String CMD_PAUSE = "pause";
	private final String CMD_CONSOLE = "console";
	
	private final String CMD_MOVE_LEFT = "move left";
	private final String CMD_MOVE_RIGHT = "move right";
	private final String CMD_MOVE_DOWN = "move down";
	private final String CMD_ROTATE_RIGHT = "rotate right";
	
	private final String CMD_NEXT_VISIBLE = "next visible";
	private final String CMD_SCORE_VISIBLE = "score visible";
	private final String CMD_FPS_VISIBLE = "fps visible";
	
	private final String HUD_NEXT_TETRAMINO = "next tetramino HUD";
	private final String HUD_SCORE = "score HUD";
	
	//------------------- Variables --------------------------------------------
	/** Input handler for game */
	private InputHandler _input = new InputHandler();
	
	/** Field for game. Just field without any decoration. */
	private SDAField _field = null;

	/** Score */
	private long _score = 0;
	
	/** Current level */
	private int _level = 0;
	
	/** Number of lines */
	private int _lines = 0;
	
	/** FPS counter */
	private TextGameState _fps = null;
	
	/** Console */
	private SDAConsoleState _console = null;
	
	public SDAMainState(String name) 
	{
		super(name);
		
		CullState cs = DisplaySystem.getDisplaySystem().getRenderer()
				.createCullState();
		cs.setCullFace(CullState.Face.Back);
		rootNode.setRenderState(cs);
		
		createGameModels();
		initHUDs();
		bindKeys();
		setupConsoleCommands();
		
		rootNode.updateRenderState();
		
	}
	
	public SDAField getField()
	{
		return _field;
	}
	
	public long getScore()
	{
		return _score;
	}
	
	public void setScore(long score)
	{
		_score = score;
		SDAScoreState hud = (SDAScoreState) getChild(HUD_SCORE);
		hud.setScore(score);
	}
	
	public int getLevel()
	{
		return _level;
	}

	/**
	 * Sets new level and set new data to field
	 *  
	 * @param level - new level
	 */
	public void setLevel(int level)
	{
		_level = level;
		
		SDALevelUtils utils = SDALevelUtils.getInstance();
		SDAScoreState hud = (SDAScoreState) getChild(HUD_SCORE);
		hud.setLevel(level);
		hud.setNecessaryScore(utils.getNecessaryScore(level + 1));
		setSpeed(utils.getSpeedModifier(level));
		
		//generate new field
		_field.reset(utils.getInitialData(level));
		rootNode.updateRenderState();
		
	}
	
	public void setNextLevel()
	{
		int nextLevel = getLevel();
		
		while (getScore() >= SDALevelUtils.getInstance()
				.getNecessaryScore(nextLevel + 1))
		{
			nextLevel++;
		}
		setLevel(nextLevel);
	}
	
	public int getLines()
	{
		return _lines;
	}
	
	public void setLines(int lines)
	{
		_lines = lines;
		SDAScoreState hud = (SDAScoreState) getChild(HUD_SCORE);
		hud.setNumberOfLines(lines);
	}

	private void setSpeed(float speed)
	{
		_field.setSpeed(speed);
		SDAScoreState hud = (SDAScoreState)getChild(HUD_SCORE);
		hud.setSpeed(speed);
	}
	
	public SDANextTetraminoState getNextHUD()
	{
		return (SDANextTetraminoState) getChild(HUD_NEXT_TETRAMINO);
	}
	
	/** 
	 * Initializes graphics. It includes graphics creating, field creating
	 */
	private void createGameModels()
	{
		// create field
		_field = new SDAField();
		_field.getRootNode().setLocalTranslation(
				-SDAConstants.cellSize / 2 * SDAConstants.fieldSize.x, 
				-SDAConstants.cellSize / 2 * SDAConstants.fieldSize.y, 
				0);
		rootNode.attachChild(_field.getRootNode());
		
		_field.addListener(new SDAFieldListener(this));
		
		//create main box
		rootNode.attachChild(buildBox());
	
		rootNode.updateModelBound();
		rootNode.updateRenderState();
	}
	
	/**
	 * Builds wood box which will 'contains' field
	 * 
	 * @return Created box
	 */
	private Node buildBox()
	{
		Node result = new Node("Main Box");
		SDAResourceManager resManager = SDAResourceManager.getInstance();
		float cellSize = SDAConstants.cellSize;
		Point fieldSize = SDAConstants.fieldSize;
		
		// create left side
		Box leftSide = new Box("left side", 
				new Vector3f(-cellSize / 2 * fieldSize.x - cellSize, 0, 0),
				cellSize / 2, 
				cellSize / 2 * fieldSize.y + cellSize * 1.5f,
				cellSize / 2);
		leftSide.setModelBound(new BoundingBox());
		leftSide.updateModelBound();
		leftSide.setRenderState(resManager.getTexture(SDAConstants.mainBoxTexture));
		result.attachChild(leftSide);
		
		// create right side
		Box rightSide = new Box("right side", 
				new Vector3f(cellSize / 2 * fieldSize.x,0, 0),
				cellSize / 2, 
				cellSize / 2 * fieldSize.y + cellSize * 1.5f,
				cellSize / 2);
		rightSide.setModelBound(new BoundingBox());
		rightSide.updateModelBound();
		rightSide.setRenderState(resManager.getTexture(SDAConstants.mainBoxTexture));
		result.attachChild(rightSide);
		
		// create bottom side
		Box bottomSide = new Box("bottom side", new Vector3f( 
				-cellSize / 2, -cellSize / 2 * fieldSize.y - cellSize, 0),
				cellSize / 2 * fieldSize.x, cellSize / 2, cellSize / 2);
		bottomSide.setModelBound(new BoundingBox());
		bottomSide.updateModelBound();
		bottomSide.setRenderState(resManager.getTexture(SDAConstants.mainBoxTexture));
		result.attachChild(bottomSide);
		
		// create back side
		Quad backSide = new Quad("back side", cellSize * fieldSize.x, 
				cellSize * fieldSize.y + 2 * cellSize);
		backSide.setModelBound(new BoundingBox());
		backSide.updateModelBound();
		backSide.setRenderState(resManager.getTexture(SDAConstants.mainBoxBgTexture));
		backSide.setLocalTranslation(-cellSize / 2, cellSize / 2, 
				-cellSize / 2 - 0.01f);
		
		CullState cs = DisplaySystem.getDisplaySystem().getRenderer()
			.createCullState();
		cs.setCullFace(CullState.Face.None);
		backSide.setRenderState(cs);
		backSide.updateRenderState();
		result.attachChild(backSide);
		
		return result;
	}
	
	/**
	 * Initializes in-game HUDs
	 */
	private void initHUDs()
	{
		float cellSize = SDAConstants.cellSize;
		Point fieldSize = SDAConstants.fieldSize;
		
		// init next tetramino HUD
		SDANextTetraminoState nextTet = new SDANextTetraminoState(HUD_NEXT_TETRAMINO);
		nextTet.getRootNode().setLocalTranslation(
				cellSize / 2 * fieldSize.x + cellSize * 2, 
				cellSize / 2 * fieldSize.y - cellSize  * 2, 0);
		nextTet.setActive(true);
		attachChild(nextTet);
		
		// init score HUD
		SDAScoreState scoreHUD = new SDAScoreState(HUD_SCORE);
		scoreHUD.getRootNode().setLocalTranslation(
				cellSize / 2 * fieldSize.x + cellSize * 2, -cellSize, 0);
		scoreHUD.setActive(true);
		scoreHUD.setNecessaryScore(SDALevelUtils.getInstance()
				.getNecessaryScore(getLevel() + 1));
		scoreHUD.setSpeed(1);
		attachChild(scoreHUD);
		
		// init fps counter
		_fps = new TextGameState("FPS: ???");
		_fps.setActive(true);
		attachChild(_fps);
	}
	
	/**
	 * Binds keys with actions
	 */
	private void bindKeys()
	{
		MoveAction keyListener = new MoveAction();
		_input.addAction(keyListener, CMD_MOVE_LEFT, SDAOptions.leftKey, true);
		_input.addAction(keyListener, CMD_MOVE_RIGHT, SDAOptions.rightKey, true);
		_input.addAction(new MoveDownAction(), CMD_MOVE_DOWN, SDAOptions.downKey, 
				false);
		_input.addAction(new RotateAction(), CMD_ROTATE_RIGHT, 
				SDAOptions.rotateKey, false);
		
		IUAction uiaction = new IUAction();
		_input.addAction(uiaction, CMD_MENU, KeyInput.KEY_ESCAPE, false);
		_input.addAction(uiaction, CMD_PAUSE, KeyInput.KEY_P, false);
		_input.addAction(uiaction, CMD_CONSOLE, KeyInput.KEY_GRAVE, false);
		
		HudVisibleAction hudAction = new HudVisibleAction();
		_input.addAction(hudAction, CMD_NEXT_VISIBLE, 
				SDAOptions.nextVisibleKey, false);
		_input.addAction(hudAction, CMD_SCORE_VISIBLE, 
				SDAOptions.scoreVisibleKey, false);
		_input.addAction(hudAction, CMD_FPS_VISIBLE,
				SDAOptions.fpsVisibleKey, false);
	}
	
	private void setupConsoleCommands()
	{
		_console = (SDAConsoleState)GameStateManager
				.getInstance().getChild("console");
		_console.add("echo", new SDAEchoCommand());
		
		_console.add("wired", new SDAWireFrameCommand(rootNode));
		
		_console.add("level", new ISDACommandHandler()
		{
			public String execute(String[] args)
			{
				int level = Integer.parseInt(args[1]);
				setLevel(level);
				return null;
			}
		});
		
		_console.add("score", new ISDACommandHandler()
		{
			public String execute(String[] args)
			{
				long score = Long.parseLong(args[1]);
				setScore(score);
				return null;
			}
		});
		
		_console.add("particles", new ISDACommandHandler()
		{
			public String execute(String[] args)
			{
				SDAParticleGlow effect = new SDAParticleGlow();
				effect.runEffect(rootNode, Float.parseFloat(args[1]));
				return null;
			}
		});
	}
	
	/**
	 * Resets fields while being activated
	 */
	@Override
	public void setActive(boolean active)
	{
		if (active == isActive())
		{
			return;
		}
		
		if (active)
		{
			setScore(0);
			setLevel(0);
			setLines(0);
			_field.setPause(false);
		}
		if (SDAOptions.playMusic)
		{
			SDAResourceManager manager = SDAResourceManager.getInstance();
			if (!active)
			{
				manager.getSound(SDAConstants.musicSound).stop();
			}
			else
			{
				manager.getSound(SDAConstants.musicSound).play();
			}
		}
			
		super.setActive(active);
	}

	/**
	 * Updates field, input handler and performs other per-circle logic
	 * 
	 * @see BasicGameState#update(float)
	 */
	@Override
	public void update(float tpf) 
	{
		_fps.setText("FPS: " + String.valueOf(Timer.getTimer().getFrameRate()));
		_field.update(tpf);
		if (!_console.isActive())
		{
			_input.update(tpf);
		}
		super.update(tpf);
	}
	
	/**
	 * Listener for key action of the state
	 * 
	 * @author lamao
	 *
	 */
	private class MoveAction extends KeyInputAction
	{
		/** Time since last action */
		private float _timeOfLastAction = 0;
		
		/**
		 * Reacts on subscribed action
		 * @see InputActionInterface#performAction(InputActionEvent)
		 */
		@Override
		public void performAction(InputActionEvent event)
		{
			float currTime = Timer.getTimer().getTimeInSeconds();
			
			// TODO: Implement this stuff in other way (maybe using 'speed'
			if (currTime - _timeOfLastAction >= 0.1f && !_field.isPause())
			{
				if (event.getTriggerName().equals(CMD_MOVE_LEFT) &&
						_field.canMoveLeft())
				{
					_field.moveLeft();
				}
				else if (event.getTriggerName().equals(CMD_MOVE_RIGHT) && 
						_field.canMoveRight())
				{
					_field.moveRight();
				}

				_timeOfLastAction = currTime;
			}
		}
	}
	
	/** Action for 'rotate' key event */
	private class RotateAction extends KeyInputAction
	{
		@Override
		public void performAction(InputActionEvent event)
		{
			if (event.getTriggerName().equals(CMD_ROTATE_RIGHT) && 
					_field.canRotateRight() && !_field.isPause())
			{
				_field.rotateRight();
			}
		}
	}
	
	/** Action for 'move down' key event */
	private class MoveDownAction extends KeyInputAction
	{
		@Override
		public void performAction(InputActionEvent event)
		{
			if (event.getTriggerName().equals(CMD_MOVE_DOWN) && !_field.isPause())
			{
				while (_field.canMoveDown())
				{
					_field.moveDown();
				}
			}
		}
	}
	
	/** Action for 'menu' key event */
	private class IUAction extends KeyInputAction
	{
		@Override
		public void performAction(InputActionEvent event)
		{
			if (event.getTriggerName().equals(CMD_MENU))
			{
				setActive(false);
				GameStateManager.getInstance().getChild(SDAMenuState.STATE_NAME)
						.setActive(true);
			}
			else if (event.getTriggerName().equals(CMD_PAUSE))
			{
				_field.setPause(!_field.isPause());
			}
			else if (event.getTriggerName().equals(CMD_CONSOLE))
			{
				_console.setActive(true);
			}
		}
	}
	
	/** Action that handles visibility of HUDs */
	public class HudVisibleAction extends KeyInputAction
	{
		@Override
		public void performAction(InputActionEvent event)
		{
			GameState gs = null;
			if (event.getTriggerName().equals(CMD_NEXT_VISIBLE))
			{
				gs = getChild(HUD_NEXT_TETRAMINO);
			}
			else if (event.getTriggerName().equals(CMD_SCORE_VISIBLE))
			{
				gs = getChild(HUD_SCORE);
			}
			else if (event.getTriggerName().equals(CMD_FPS_VISIBLE))
			{
				gs = _fps;
			}
			
			if (gs != null)
			{
				gs.setActive(!gs.isActive());
			}
		}
	}
	
	/**
	 * Listener for field
	 */
	

}
