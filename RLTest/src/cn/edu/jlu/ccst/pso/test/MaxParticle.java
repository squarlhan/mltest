package cn.edu.jlu.ccst.pso.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cn.edu.jlu.ccst.pso.Particle;

/**
 * Simple particle example
 * @author Pablo Cingolani <pcingola@users.sourceforge.net>
 */
public class MaxParticle extends Particle {

	/** Default constructor */
	public MaxParticle() {
		super(); // Create a 2-dimentional particle
	}
	
	public MaxParticle(int d) {
		super(d); // Create a 2-dimentional particle
	}
	
	public Object selfFactory() {
		
		if(this.getDimention()>0){
			return new MaxParticle(this.getDimention());
		}else{
			return new MaxParticle();
		}
	}
}