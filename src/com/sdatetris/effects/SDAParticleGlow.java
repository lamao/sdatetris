/* 
 * SDAGlobalParticleEffect.java 12.03.2010
 * 
 * Copyright 2010 sdaTetris
 * All rights reserved. 
 */
package com.sdatetris.effects;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleSystem;
import com.jmex.effects.particles.RampEntry;
import com.sdatetris.SDAConstants;
import com.sdatetris.SDAResourceManager;

/**
 * Creates particle effect of entire screen glow. Taken from jmetest package.
 * @author lamao
 */
public class SDAParticleGlow
{
	/** Created particle node */
	private ParticleSystem _particles = null;
	
	/** 
	 * Creates glow effect.
	 */
	public SDAParticleGlow()
	{
		DisplaySystem display = DisplaySystem.getDisplaySystem();
		
		// ParticleSystem using RampEntries to change the color and size over time
        _particles = ParticleFactory.buildParticles("particles", 200);
        _particles.setEmissionDirection(new Vector3f(0, 1, 0));
        _particles.setInitialVelocity(0.1f);
        _particles.setMinimumLifeTime(1000);
        _particles.setMaximumLifeTime(1000);
        _particles.setMaximumAngle(180 * FastMath.DEG_TO_RAD);
        _particles.getParticleController().setControlFlow(true);
        _particles.getParticleController().setSpeed(0.5f);
        _particles.setParticlesInWorldCoords(true);
        _particles.setLocalTranslation(0, 0, 2);
        _particles.setLocalScale(0.2f);
        
        // Start color is RED, opaque
        _particles.setStartColor(new ColorRGBA(1, 0, 0, 1));
        _particles.setStartSize(2.5f);

        // At 25% life, let's have the color be WHITE, opaque
        final RampEntry entry25 = new RampEntry(0.25f);
        entry25.setColor(new ColorRGBA(1, 1, 1, 1));
        _particles.getRamp().addEntry(entry25);

        // At 50% life, (25% higher than previous) let's have the color be 
        // GREEN, opaque and much bigger.
        // Note that at 25% life the size will be about 3.75 since we did not 
        // set a size on that.
        final RampEntry entry50 = new RampEntry(0.25f);
        entry50.setColor(new ColorRGBA(0, 1, 0, 1));
        entry50.setSize(6.5f);
        _particles.getRamp().addEntry(entry50);

        // At 75% life, (25% higher than previous) let's have the color be 
        // WHITE, opaque
        final RampEntry entry75 = new RampEntry(0.25f);
        entry75.setColor(new ColorRGBA(1, 1, 1, 1));
        _particles.getRamp().addEntry(entry75);

        // End color is BLUE, opaque (size is back to 2.5 now.
        _particles.setEndColor(new ColorRGBA(0, 0, 1, 1));
        _particles.setEndSize(2.5f);

        _particles.warmUp(60);

        // set up a BlendState to enable transparency
        final BlendState blend = display.getRenderer().createBlendState();
        blend.setEnabled(true);
        blend.setBlendEnabled(true);
        blend.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        blend.setDestinationFunction(BlendState.DestinationFunction.One);
        blend.setTestEnabled(true);
        blend.setTestFunction(BlendState.TestFunction.GreaterThan);
        _particles.setRenderState(blend);

        TextureState ts = SDAResourceManager.getInstance().getTexture(
        		SDAConstants.particlesTexture);
        _particles.setRenderState(ts);

        // set up a non-writable ZBuffer
        ZBufferState zstate = display.getRenderer().createZBufferState();
        zstate.setWritable(false);
        zstate.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        _particles.setRenderState(zstate);

        _particles.getParticleGeometry().setModelBound(new BoundingBox());
        _particles.getParticleGeometry().updateModelBound(); 
	}
	
	public ParticleSystem getParticles()
	{
		return _particles;
	}
	
	/**
	 * Runs effect for <code>effectTime</code> seconds 
	 * @param parent - parent node for effect.
	 * @param effectTime - time for effect.
	 */
	@SuppressWarnings("serial")
	public void runEffect(final Node parent, final float effectTime)
	{
		parent.attachChild(getParticles());
		parent.addController(new Controller()
		{
			float _time = 0;
			public void update(float time)
			{
				_time += time;
				if (_time > effectTime)
				{
					parent.removeController(this);
					parent.detachChild(getParticles());
				}
			}
			
		});
		parent.updateRenderState();
	}
}
