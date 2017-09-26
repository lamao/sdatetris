/*
 * SDAField.java 08.02.2010 
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */

package com.sdatetris.gamelogic;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.sdatetris.SDAConstants;
import com.sdatetris.SDAOptions;
import com.sdatetris.animation.SDABoxAnimator;

/**
 * Game field contains set of boxes. This class contains both graphics and logic
 * 
 * @author lamao
 *
 */

public class SDAField 
{
	/** 
	 * Root node for field. It contains all boxes from <code>_data</code>.
	 */
	private Node _rootNode = new Node("root node");
	
	/**
	 * All cells of field. <code> _data[x][y] == null </code> the cell is empty.
	 */
	private Box[][] _data = new Box[SDAConstants.fieldSize.x][SDAConstants.fieldSize.y];
	
	/**
	 * Current tetramino
	 */
	private SDATetramino _tetramino = null;
	
	/**
	 * Position for newly generated netramino
	 */
	private Point _initTetPos = new Point(SDAConstants.fieldSize.x / 2 - 1,
										  SDAConstants.fieldSize.y - 4);
	
	/**
	 * Position of current tetramino. Is measured in cells
	 */
	private Point _tetPos = (Point)_initTetPos.clone();
	
	/**
	 * Next tetramino
	 */
	private SDATetramino _nextTetramino = null;
	
	/**
	 * Stub that is used in {@link #getFieldArea(Point, int)}.
	 * NOTE: You should not use this variable anywhere else. 
	 */
	private final static Box stub = new Box("stub box");
	
	/**
	 * Time since last update. Related to <code>tpf</code> in {@link #update(float)}
	 */
	private float _timeSinceLastUpdate = 0;
	
	/**
	 * Listeners of this field
	 */
	private List<ISDAFieldListener> _listeners = null;
	
	/**
	 * Pause value
	 */
	private boolean _pause = false;
	
	/** Speed modifier */
	private float _speed = 1;
	
	/** Processor for clear completed lines */
	private SDALineProcessor _lineProcessor = new SDALineProcessor();
	
	/** Places tetramino into the field */
	private SDATetraminoPlacer _placer = new SDATetraminoPlacer();
	
	public SDAField()
	{
		_nextTetramino = new SDATetramino();
		_nextTetramino.generateTetramino();
		reset();
	}
	
	public SDAField getInstance()
	{
		return this;
	}
	
	public Box[][] getData()
	{
		return _data;
	}
	
	public Node getRootNode()
	{
		return _rootNode;
	}
	
	public SDATetramino getTetramino()
	{
		return _tetramino;
	}
	
	public Point getTetPos()
	{
		return _tetPos;
	}
	
	public SDATetramino getNextTetramino()
	{
		return _nextTetramino;
	}
	
	public void setPause(boolean value)
	{
		_pause = value;
	}
	
	public boolean isPause()
	{
		return _pause;
	}
	
	public void setSpeed(float speed)
	{
		_speed = speed;
	}
	
	public float getSpeed()
	{
		return _speed;
	}
	
	/**
	 * Copies next tetramino into current one and performs all necessary 
	 * routines (e.i. moves current tetramino to the top center, generates new
	 * next tetramino etc).
	 */
	private void useNextTetramino()
	{
		if (_tetramino != null)
		{
			getRootNode().detachChild(_tetramino.getRootNode());
		}
		_tetramino = _nextTetramino;
		_nextTetramino = new SDATetramino();
		_nextTetramino.generateTetramino();
		
		_tetramino.getRootNode().setLocalTranslation(
				SDAConstants.cellSize * (_initTetPos.x), 
				SDAConstants.cellSize * (_initTetPos.y),
				SDAConstants.cellSize / 2);
		_tetPos.setLocation(_initTetPos);
		
		getRootNode().attachChild(_tetramino.getRootNode());		
		getRootNode().updateModelBound();
		getRootNode().updateRenderState();
		
		for (ISDAFieldListener listener : getListeners())
		{
			listener.tetraminoChanged(this);
		}
	}
	
	
	/**
	 * Create a copy of data from field in window <code>(bottomLeft.x, 
	 * bottomLeft.y)-(bottomLeft.x + size, bottomLeft.y + size)</code>.
	 * All cells that exceeds field bounds (x or y < 0, x or y > field size) 
	 * are treated as occupied cells and are filled by stub Box. 
	 *  
	 * @see #canMoveDown()
	 * @see #canMoveLeft()
	 * @see #canMoveRight()
	 *  
	 * @param bottomLeft - bottom left point of window
	 * @param size - window size
	 * @return Matrix size x size of data in specified area
	 */
	private Box[][] getFieldArea(Point bottomLeft, int size)
	{
		Box[][] result = new Box[size][size];
		
		for (int x = 0; x < size; x++)
		{
			for (int y = 0; y < size; y++)
			{
				if (x + bottomLeft.x >= SDAConstants.fieldSize.x || 
					y + bottomLeft.y >= SDAConstants.fieldSize.y ||
					x + bottomLeft.x < 0 || y + bottomLeft.y < 0)
				{
					result[x][y] = stub;
				}
				else
				{
					result[x][y] = _data[x + bottomLeft.x][y + bottomLeft.y];
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Checks whether tetramino can be move one cell left
	 * 
	 * @return <b>true</b> if it can
	 */
	public boolean canMoveLeft()
	{
		// get area to check intersection
		Box[][] checkArea = getFieldArea(new Point(_tetPos.x - 1, _tetPos.y), 4);
		return !_tetramino.doesIntersect(checkArea);
	}
	
	/**
	 * Checks whether tetramino can be move one cell right
	 * 
	 * @return <b>true</b> if it can
	 */
	public boolean canMoveRight()
	{
		// get area to check intersection
		Box[][] checkArea = getFieldArea(new Point(_tetPos.x + 1, _tetPos.y), 4);
		return !_tetramino.doesIntersect(checkArea);
	}
	
	/**
	 * Checks whether tetramino can be move one cell down
	 * 
	 * @return <b>true</b> if it can
	 */
	public boolean canMoveDown()
	{
		// get area to check intersection
		Box[][] checkArea = getFieldArea(new Point(_tetPos.x, _tetPos.y - 1), 4);
		return !_tetramino.doesIntersect(checkArea);
	}
	
	/**
	 * Checks whether tetramino can be rotated right
	 * @return
	 */
	public boolean canRotateRight()
	{
		boolean result = false;
		
		if (canMoveDown())
		{
			_tetramino.rotateRight();
			Box[][] checkArea = getFieldArea(_tetPos, 4);
			result = !_tetramino.doesIntersect(checkArea);
			_tetramino.rotateLeft();
		}
		return result;
	}
	
	/**
	 * Moves tetramino one cell left<br>
	 * <b>NOTE:</b> It doesn't care about field boundaries and intersection
	 */
	public void moveLeft()
	{
		Vector3f location = _tetramino.getRootNode().getLocalTranslation();
		location.addLocal(-SDAConstants.cellSize, 0, 0);
		_tetramino.getRootNode().setLocalTranslation(location);
		_tetPos.x--;
		
		for (ISDAFieldListener listener : getListeners())
		{
			listener.moved(this);
		}
	}
	
	/**
	 * Moves tetramino one cell right<br>
	 * <b>NOTE:</b> It doesn't care about field boundaries and intersection
	 */
	public void moveRight()
	{
		Vector3f location = _tetramino.getRootNode().getLocalTranslation();
		location.addLocal(SDAConstants.cellSize, 0, 0);
		_tetramino.getRootNode().setLocalTranslation(location);
		_tetPos.x++;
		
		for (ISDAFieldListener listener : getListeners())
		{
			listener.moved(this);
		}
	}
	
	/**
	 * Moves tetramino one cell down<br>
	 * <b>NOTE:</b> It doesn't care about field boundaries and intersection
	 */
	public void moveDown()
	{
		Vector3f location = _tetramino.getRootNode().getLocalTranslation();
		location.addLocal(0, -SDAConstants.cellSize, 0);
		_tetramino.getRootNode().setLocalTranslation(location);
		_tetPos.y--;
		
		for (ISDAFieldListener listener : getListeners())
		{
			listener.moved(this);
		}
	}
	
	/**
	 * Rotates current tetramino right<br>
	 * <b>NOTE:</b> It doesn't care about field boundaries and intersection
	 */
	public void rotateRight()
	{
		_tetramino.rotateRight();
		for (ISDAFieldListener listener : getListeners())
		{
			listener.rotated(this);
		}
	}
	
	/**
	 * Copy tetramino's data into the field's data. It is used when tetramino
	 * can't move down
	 */
	private void placeTetramino()
	{
		
	}
	
	/**
	 * Here all time-depends logic is placed (e.g. moving tetramino down each
	 * n secs) 
	 * 
	 * @param tpf - the elapsed time since the last frame
	 */
	public void update(float tpf)
	{
		_timeSinceLastUpdate += tpf;
		if (_timeSinceLastUpdate >= SDAOptions.stepInterval / getSpeed() 
			&& !isPause())
		{
			if (canMoveDown())
			{
				moveDown();
			}
			else
			{
				// game over
				if (_tetPos.y == _initTetPos.y)
				{
					setPause(true);
					for (ISDAFieldListener listener : getListeners())
					{
						listener.gameOver(this);
					}
				}
				// place tetramino
				else
				{
					// place tetramino
					_placer.place(this);
					setPause(true);
					final SDABoxAnimator tetAnimator = new SDABoxAnimator(
							_placer.getAnimationData());
					tetAnimator.setMoveDirection(new Vector3f(0, 0, -1));
					getRootNode().addController(tetAnimator);
					tetAnimator.invokeAfterFinishing(new Callable<Void>()
					{
						public Void call() throws Exception
						{
							getRootNode().removeController(tetAnimator);
							setPause(false);
							for (ISDAFieldListener listener : getListeners())
							{
								listener.placed(getInstance());
							}
					
							useNextTetramino();

							if (_lineProcessor.isLine(getInstance()))
							{
								final int numberOfNewLines = _lineProcessor.processLines(
										_data, getRootNode());
								setPause(true);
								final SDABoxAnimator animator = new SDABoxAnimator( 
										_lineProcessor.getAnimationData());
								getRootNode().addController(animator);
								animator.invokeAfterFinishing(new Callable<Void>()
								{
									public Void call() throws Exception	
									{
										getRootNode().removeController(animator);
										setPause(false);
										for (ISDAFieldListener listener : getListeners())
										{

											listener.lineCompleted(getInstance(), 
													numberOfNewLines);
										}
										return null;
									}

								});
							}
							
							return null;
						}
					});
					
				}
				
			}
			_timeSinceLastUpdate = 0;
			
		}
	}
	
	/**
	 * Resets field for new game. It use given <code>data</code> to fill its
	 * <code>_data</code>, sets {@link #_numberOfLines} etc. 
	 * 
	 * @param data - start field contents
	 */
	public void reset(Box[][] data)
	{
		assert data.length == SDAConstants.fieldSize.x;
		
		// reset field
		getRootNode().detachAllChildren();
		for (int x = 0; x < data.length; x++)
		{
			assert data[x].length == SDAConstants.fieldSize.y;
			for (int y = 0; y < data[x].length; y++)
			{
				_data[x][y] = data[x][y];
				if (_data[x][y] != null)
				{
					getRootNode().attachChild(data[x][y]);
					_data[x][y].getCenter().set(x * SDAConstants.cellSize,
							y * SDAConstants.cellSize, 0);
					_data[x][y].updateGeometry();
				}
			}
		}
		
		// reset tetramino
		useNextTetramino();
		
		getRootNode().updateModelBound();
		getRootNode().updateRenderState();
	}
	
	/**
	 * Resets field to empty new field
	 * 
	 * @see #reset(Box[][])
	 */
	public void reset()
	{
		Box[][] data = new Box[SDAConstants.fieldSize.x][SDAConstants.fieldSize.y];
		reset(data);
	}
	
	/**
	 * @return listeners of this field
	 */
	public List<ISDAFieldListener> getListeners()
	{
		if (_listeners == null)
		{
			_listeners = new LinkedList<ISDAFieldListener>();
		}
		return _listeners; 
	}
	
	/**
	 * Adds listener to field
	 * 
	 * @param listener - listener 
	 */
	public void addListener(ISDAFieldListener listener)
	{
		getListeners().add(listener);
	}
	
	/**
	 * Removes listener from field listeners if present
	 * 
	 * @param listener - listener
	 */
	public void removeListener(ISDAFieldListener listener)
	{
		getListeners().remove(listener);
	}
	
	/** Recalculates boxes positions. Used after animation to avoid defects. */
//	private void updateBoxesPosition()
//	{
//		for (int x = 0; x < SDAConstants.fieldSize.x; x++)
//		{
//			for (int y = 0; y < SDAConstants.fieldSize.y; y++)
//			{
//				if (_data[x][y] != null)
//				{
//					_data[x][y].setLocalTranslation(x * SDAConstants.cellSize,
//							y * SDAConstants.cellSize, 0);
//				}
//			}
//		}
//	}
	
}
