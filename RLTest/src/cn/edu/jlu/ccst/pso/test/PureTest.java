package cn.edu.jlu.ccst.pso.test;

import java.util.ArrayList;
import java.util.List;

import cn.edu.jlu.ccst.pso.FitnessFunction;
import cn.edu.jlu.ccst.pso.Swarm;

public class PureTest {

	public void Calculate(int max_gen, int numofparticals, int dimention, double intertia, double velocity,
			List<List<Double>> scopes, FitnessFunction fitness) {
		// System.out.println("Begin: MaxTest 1\n");
		long startTime = System.currentTimeMillis();
		// Create a swarm (using 'MyParticle' as sample particle and
		// 'MyFitnessFunction' as finess function)
		Swarm swarm = new Swarm(numofparticals, new MaxParticle(dimention), fitness);

		// Set position (and velocity) constraints. I.e.: where to look for
		// solutions
		double[] lowers = new double[dimention];
		double[] uppers = new double[dimention];
		for (int i = 0; i <= dimention - 1; i++) {
			lowers[i] = scopes.get(i).get(0);
			uppers[i] = scopes.get(i).get(1);
		}
		swarm.setInertia(intertia);
		swarm.setMaxPosition(uppers);
		swarm.setMinPosition(lowers);
		swarm.setMaxMinVelocity(velocity);

		int numberOfIterations = max_gen;

		// Optimize (and time it)
		for (int i = 0; i < numberOfIterations; i++) {
			swarm.evolve();
		}

		// Print en results
		System.out.println(swarm.toStringStats());
		long endTime = System.currentTimeMillis();
		System.out.println("Running Time: " + (endTime - startTime) + "ms");

	}

	// -------------------------------------------------------------------------
	// Main
	// -------------------------------------------------------------------------
	public static void main(String[] args) {

		int max_gen = 50;
		int numofparticals = 4;
		int dimention = 1;
		double intertia = 0.9;
		double velocity = 0.9;
		List<List<Double>> scopes = new ArrayList();
		for (int i = 0; i <= dimention - 1; i++) {
			List<Double> sp = new ArrayList();
			sp.add(0.0);
			sp.add(31.0);
			scopes.add(sp);
		}
		PureTest purepso = new PureTest();

		purepso.Calculate(max_gen, numofparticals, dimention, intertia, velocity, scopes, new MaxFunction());


	}
}
