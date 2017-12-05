/*
 * This file is part of JGAP.
 *
 * JGAP offers a dual license model containing the LGPL as well as the MPL.
 *
 * For licensing information please see the file license.txt included with JGAP
 * or have a look at the top of class org.jgap.Chromosome which representatively
 * includes the JGAP license policy applicable for any file delivered with JGAP.
 */
package cn.edu.jlu.ccst.pso.test;

import java.util.List;

import cn.edu.jlu.ccst.pso.FitnessFunction;

public class MaxFunction 
    extends FitnessFunction{
  private final static String CVS_REVISION = "$Revision: 1.6 $";
  
  public static int counts = 0;
  public double evaluate(double position[]) {
    double total = 0;
    int time_delay = 0;
    try {
		Thread.sleep(time_delay);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    int n = position.length;
    for (int i = 0; i < n; i++) {
      
    	total += Math.pow(position[i], 2.0);
      
    }
    counts ++;
    return total;
  }
}
