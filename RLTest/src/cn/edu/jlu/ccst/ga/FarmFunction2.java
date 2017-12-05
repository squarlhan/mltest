/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package cn.edu.jlu.ccst.ga;

import java.util.List;

import org.jgap.*;
import org.jgap.impl.*;

/**
 * Fitness function for our example. See evaluate(...) method for details.
 *
 * @author Neil Rotstan
 * @author Klaus Meffert
 * @since 2.0
 */
public class FarmFunction2 extends FitnessFunction {
	/** String containing the CVS revision. Read out via reflection! */
	private final static String CVS_REVISION = "$Revision: 1.6 $";

	public static int counts = 0;

	/**
	 * This example implementation calculates the fitness value of Chromosomes using
	 * BooleanAllele implementations. It simply returns a fitness value equal to the
	 * numeric binary value of the bits. In other words, it optimizes the numeric
	 * value of the genes interpreted as bits. It should be noted that, for clarity,
	 * this function literally returns the binary value of the Chromosome's genes
	 * interpreted as bits. However, it would be better to return the value raised
	 * to a fixed power to exaggerate the difference between the higher values. For
	 * example, the difference between 254 and 255 is only about .04%, which isn't
	 * much incentive for the selector to choose 255 over 254. However, if you
	 * square the values, you then get 64516 and 65025, which is a difference of
	 * 0.8% -- twice as much and, therefore, twice the incentive to select the
	 * higher value.
	 *
	 * @param a_subject
	 *            the Chromosome to be evaluated
	 * @return defect rate of our problem
	 *
	 * @author Neil Rotstan
	 * @author Klaus Meffert
	 * @since 2.0
	 */
	public double evaluate(IChromosome a_subject) {
		double total = 0;
		int time_delay = 0;
		try {
			Thread.sleep(time_delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int n = a_subject.size();
		if (n == 6) {
			
			int c = 0;
			int w = 0;
			int o = 0;
			
			for(int i =0;i<=4;i++) {
				c=((IntegerGene) a_subject.getGene(i)).intValue()==0?c+120:c;
				w=((IntegerGene) a_subject.getGene(i)).intValue()==0?w+120:w;
				o=((IntegerGene) a_subject.getGene(i)).intValue()==0?o+120:o;
			}
			c=((IntegerGene) a_subject.getGene(5)).intValue()==0?c+120:c;
			w=((IntegerGene) a_subject.getGene(5)).intValue()==0?w+120:w;
			o=((IntegerGene) a_subject.getGene(5)).intValue()==0?o+120:o;
			
			double y = 400*c+200*w+250*o;
			double ai = 3*c+w+1.5*o;
			double aj = 0.8*c+0.2*w+0.3*o;
		
			y = ai>1000?Math.sqrt(y):y;
			y = aj>300?Math.sqrt(y):y;
			
			total = y;
			
		}
		
		counts++;

		return total;
	}
}
