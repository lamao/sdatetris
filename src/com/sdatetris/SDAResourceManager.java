/* 
 * SDAResoursceManager.java 12.02.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved
 */

package com.sdatetris;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.jme.image.Texture;
import com.jme.image.Texture.MagnificationFilter;
import com.jme.image.Texture.MinificationFilter;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.angelfont.BitmapFont;
import com.jmex.angelfont.BitmapFontLoader;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;

/**
 * Class for managing and loading all game resources (sounds, textures).
 * It is implemented as Singleton pattern.
 * 
 * @author lamao
 *
 */
public class SDAResourceManager
{
	/** Types of supported resources */
	private enum Types {TEXTURE, SOUND, BMFONT, TTFONT};
	
	/** Leading string of comment line */
	private static String COMMENT_PREFIX = "#";
	
	/** Separators for statements */
	private static String SEPARATORS = "[\t =]";
	
	/** Instance of this class */
	private static SDAResourceManager _instance = null;
	
	/** Logger fot this class */
	private static Logger _logger = Logger.getLogger(SDAResourceManager.class.getName());
	
	
	/** Loaded textures. Key is texture label */
	private Map<String,TextureState> _textures = new HashMap<String, TextureState>();
	
	/** Loaded sounds. Key is texture label */
	private Map<String, AudioTrack> _sounds = new HashMap<String, AudioTrack>();
	
	/** Loaded bitmap fonts */
	private Map<String, BitmapFont> _bmFonts = new HashMap<String, BitmapFont>();
	
	/** List which represents resource file to parse */
	private List<String> _config = new LinkedList<String>();
	
	private SDAResourceManager()
	{
	}
	
	/**
	 * Returns instance of class
	 */
	public static SDAResourceManager getInstance()
	{
		if (_instance == null)
		{
			_instance = new SDAResourceManager();
		}
		return _instance;
	}
	
	public void readConfig(File file)
	{
		try
		{
			readConfig(new FileInputStream(file));
		}
		catch (FileNotFoundException e)
		{
			_logger.warning("Can't find resource description file " + file);
			return;
		}
	}
	
	public void readConfig(InputStream is)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			_config.clear();
			do
			{
				line = reader.readLine();
				if (line != null)
				{
					_config.add(line);
				}
			}
			while (line != null); 
			reader.close();
			refine(_config);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public List<String> getConfig()
	{
		return _config;
	}
	
	/** 
	 * Loads all resources denoted in file
	 * 
	 * @param file - file where resources are described.
	 * @return number of resources has been loaded.
	 */
	public int loadAll(File file)
	{
		try
		{
			return loadAll(new FileInputStream(file));
		}
		catch (FileNotFoundException e)
		{
			_logger.warning("Can't find resource description file " + file);
			return 0;
		}
	}
	
	/** 
	 * Loads all resources denoted in stream
	 * @param is - stream where resources are desribed
	 * @return number of resources has been loaded
	 */
	public int loadAll(InputStream is)
	{
		readConfig(is);
		return parseConfig(_config);
	}
	
	public int parseConfig(List<String> config)
	{
		int loaded = 0;
		for (String line : config)
		{
			loaded += parseOneLine(line);
		}
		
		return loaded;
	}
	
	public int parseOneLine(String line)
	{
		int result = 0;
		if (line != null && line.length() > 0 
				&& !line.startsWith(COMMENT_PREFIX))
		{
			String[] items = split(line);
			addItem(items);
			result = 1;
		}
		return result;
	}
	
	public Map<String, TextureState> getTextures()
	{
		return _textures;
	}
	
	public TextureState getTexture(String label)
	{
		return _textures.get(label);
	}
	
	public Map<String, AudioTrack> getSounds()
	{
		return _sounds;
	}
	
	public AudioTrack getSound(String label)
	{
		return _sounds.get(label);
	}
	
	public Map<String, BitmapFont> getBmFonts()
	{
		return _bmFonts;
	}
	
	public BitmapFont getBmFont(String label)
	{
		return _bmFonts.get(label);
	}
	
	/**
	 * Loads one resource item.
	 * @param items - one line already split into tokens.
	 * @see #split(String)
	 */
	private void addItem(String[] items)
	{
		if (items[0].equals("type"))
		{
			String type = items[1].toUpperCase();
			if (type.equals(Types.TEXTURE.toString()))
			{
				parseTexture(items);
			}
			else if (type.equals(Types.SOUND.toString()))
			{
				parseSound(items);
			}
			else if (type.equals(Types.BMFONT.toString()))
			{
				parseBmFont(items);
			}
			else if (type.equals(Types.TTFONT.toString()))
			{
				parseTtFont(items);
			}
		}
	}

	/** 
	 * Parse texture resource. It's assumed that type (e.i. <code>items[1]</code>)
	 * is "texture".
	 * @param items - tokens for this resource
	 * @see #split(String)
	 */
	private void parseTexture(String[] items)
	{
		Texture tx = null;
		String path = null;
		String label;
		int i = 2;
		
		if (!items[i].equals("label"))
		{
			_logger.warning("Label is not specified");
			return;
		}
		else
		{
			label = items[i + 1];
			i += 2;
		}
		
		if (!items[i].equals("path"))
		{
			_logger.warning("Path is not specified");
			return;
		}
		else
		{
			try
			{
				path = items[i + 1];
				URL url = new File(path).toURI().toURL();
				tx = TextureManager.loadTexture(url);
				tx.setWrap(Texture.WrapMode.Clamp);
			}
			catch (MalformedURLException e)
			{
				_logger.warning("Can't load texture: " + path);
				return;
			}
			
			i += 2;
		}
		
		for (; i < items.length; i+= 2)
		{
			if (items[i].equals("filtering"))
			{
				if (items[i + 1].equals("bilinear"))
				{
					tx.setMinificationFilter(MinificationFilter.BilinearNearestMipMap);
					tx.setMagnificationFilter(MagnificationFilter.Bilinear);
				}
				else if (items[i + 1].equals("trilinear"))
				{
					tx.setMinificationFilter(MinificationFilter.Trilinear);
					tx.setMagnificationFilter(MagnificationFilter.Bilinear);
				}
			}
		}

		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer()
				.createTextureState();
		ts.setTexture(tx);
		_textures.put(label, ts);
		
	}
	
	/** 
	 * Parse sound resource. It's assumed that type (e.i. <code>items[1]</code>)
	 * is "sound". 
	 * @param items - tokens for this resource
	 * @see #split(String)
	 */
	private void parseSound(String[] items)
	{
		AudioTrack track = null;
		String path = null;
		String label = null;
		int i = 2;
		
		if (!items[i].equals("label"))
		{
			_logger.warning("Label is not specified");
			return;
		}
		else
		{
			label = items[i + 1];
			i += 2;
		}
		
		if (!items[i].equals("path"))
		{
			_logger.warning("Path is not specified");
			return;
		}
		else
		{
			try
			{
				path = items[i + 1];
				URL url = new File(path).toURI().toURL();
				
				boolean stream = false;
				if (items.length > i + 3 && items[i + 2].equals("stream"))
				{
					i += 2;
					stream = Boolean.parseBoolean(items[i + 1]);
				}
				
				track = AudioSystem.getSystem().createAudioTrack(
						url, stream);
			}
			catch (MalformedURLException e)
			{
				_logger.warning("Can't load texture: " + path);
				return;
			}
			
			i += 2;
		}
		
		for (; i < items.length; i+= 2)
		{
			if (items[i].equals("loop"))
			{
				if (items[i + 1].equals("yes") || items[i + 1].equals("true"))
				{
					track.setLooping(true);
				}
			}
		}
		
		_sounds.put(label, track);
	}
	
	/**
	 * Parses bitmap font. It's assumed that type (e.i. <code>items[1]</code>)
	 * is "bmfont"
	 * @param items - tokens for this resource
	 * @see #split(String)
	 */
	private void parseBmFont(String[] items)
	{
		URL path = null;
		URL texture = null;
		String label = null;
		int i = 2;
		
		if (!items[i].equals("label"))
		{
			_logger.warning("Label is not specified");
			return;
		}
		else
		{
			label = items[i + 1];
			i += 2;
		}
		
		if (!items[i].equals("path"))
		{
			_logger.warning("Path is not specified");
			return;
		}
		else
		{
			try
			{
				path = new File(items[i + 1]).toURI().toURL();
			}
			catch (MalformedURLException e)
			{
				_logger.warning("Can't load texture: " + items[i + 1]);
				return;
			}
			i += 2;
		}
		
		if (!items[i].equals("texture"))
		{
			_logger.warning("Texture for font is not specified");
			return;
		}
		else
		{
			try
			{
				texture = new File(items[i + 1]).toURI().toURL();
			}
			catch (MalformedURLException e)
			{
				_logger.warning("Can't load texture: " + items[i + 1]);
			}
		}
		
		BitmapFont font;
		try
		{
			font = BitmapFontLoader.load(path, texture);
			_bmFonts.put(label, font);
		}
		catch (IOException e)
		{
			_logger.warning("Can't load bitmap font: " + path);
			return;
		}
		
	}
	
	/**
	 * Parses TrueType font. It's assumed that type (e.i. <code>items[1]</code>)
	 * is "ttfont"
	 * @param items - tokens for this resource
	 * @see #split(String)
	 */
	private void parseTtFont(String[] items)
	{
		int i = 2;
		if (!items[i].equals("path"))
		{
			_logger.warning("Path is not specified");
			return;
		}
		else
		{
			try
			{
				Font font = Font.createFont(Font.TRUETYPE_FONT, 
						new File(items[i + 1]));
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.registerFont(font);
			}
			catch (FontFormatException e)
			{
				_logger.warning("Font is bad: " + items[i + 1]);
				return;
			}
			catch (IOException e)
			{
				_logger.warning("Can't load font: " + items[i + 1]);
				return;
			}
			i += 2;
		}
	}
	
	
	/**
	 * Splits line that describes resource into tokens. Line must consist of
	 * pairs <code>key=value</code>. Value depends on key, but if it is two 
	 * (or more) word string it must be in quotes (like <code>"some value"</code>)
     *
	 * @param string - line that describes resource
	 * @return array of tokens
	 */
	private String[] split(String string)
	{
		int i = 0;
		List<String> result = new LinkedList<String>();
		boolean readString = false;
		int end = 0;
		String item = null;
		
		while (i < string.length())
		{
			// skip separators
			while (i < string.length() && SEPARATORS.indexOf(string.charAt(i)) != -1)
			{
				i++;
			}
			if (i >= string.length())
			{
				break;
			}
			// check for string
			if (string.charAt(i) == '"')
			{
				readString = true;
				i++;
			}
			end = i + 1;
			while ((SEPARATORS.indexOf(string.charAt(end)) == -1 && !readString) || 
					(string.charAt(end) != '"' && readString))
			{
				end++;
			}
			item = string.substring(i, end);
			if (!readString)
			{
				item.toLowerCase();
			}
			result.add(string.substring(i, end));
			i = end + 1;
			readString = false;
		}
		
		String[] array = new String[result.size()];
		result.toArray(array);
		return array;
	}
	
	/**
	 * Removes all comment and blank lines
	 * @param config
	 */
	private void refine(List<String> config)
	{
		String line = null;

		for (int i = 0; i < config.size(); i++)
		{
			line = config.get(i);
			if (line.length() == 0 || line.startsWith(COMMENT_PREFIX))
			{
				config.remove(i--);
			}
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		for (TextureState ts : _textures.values())
		{
			ts.deleteAll();
		}
		
		for (AudioTrack track : _sounds.values())
		{
			track.release();
		}
		super.finalize();
	}
	
}
