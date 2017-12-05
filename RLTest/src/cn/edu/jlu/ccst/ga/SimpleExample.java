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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import org.jgap.*;
import org.jgap.impl.*;

/**
 * Simple class that demonstrates the basic usage of JGAP.
 * 
 * @author Neil Rotstan
 * @author Klaus Meffert
 * @since 2.0
 */
public class SimpleExample {
	/** String containing the CVS revision. Read out via reflection! */
	private static final String CVS_REVISION = "$Revision: 1.9 $";

	public void runga(int ng, int chromeSize, int popsize, double left, double right, FitnessFunction fitnessfun) {
		long startTime = System.currentTimeMillis();
		int numEvolutions = ng;
		Configuration gaConf = new DefaultConfiguration();
		gaConf.reset();
		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(false);
		Genotype genotype = null;

		try {
			// 构建基因(Gene)
			Gene[] sampleGenes = new Gene[chromeSize];// 基因长度2
			for (int i = 0; i < sampleGenes.length; i++) {
				sampleGenes[i] = new DoubleGene(gaConf, left, right);
			}
			// 构建染色体(Chromosome)
			IChromosome sampleChromosome = new Chromosome(gaConf, sampleGenes);
			gaConf.setSampleChromosome(sampleChromosome);
			gaConf.setPopulationSize(popsize);
			gaConf.setFitnessFunction(fitnessfun);
			genotype = Genotype.randomInitialGenotype(gaConf);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		int progress = 0;
		int percentEvolution = numEvolutions / 10;
		for (int i = 0; i < numEvolutions; i++) {
			genotype.evolve();
			// Print progress.
			// ---------------
			progress++;
			IChromosome fittest = genotype.getFittestChromosome();

			if (percentEvolution > 0 && i % percentEvolution == 0) {
				double fitness = fittest.getFitnessValue();
				System.out.println("Currently fittest Chromosome has fitness " + fitness);
			}
		}
		// Print summary.
		// --------------
		IChromosome fittest = genotype.getFittestChromosome();
		System.out.println("Fittest Chromosome has fitness " + (fittest.getFitnessValue()));

		DecimalFormat myformat = new DecimalFormat("#0.00");
		for (int i = 0; i < chromeSize; i++) {

			// System.out.println(myformat.format(((DoubleGene)fittest.getGene(i)).doubleValue()));
			System.out.print(myformat.format(fittest.getGene(i).getAllele()) + "	");
		}
		System.out.println();
		long endTime = System.currentTimeMillis();
		System.out.println("运行时间 " + (endTime - startTime) + "ms");
		System.out.println("sum counts:  " + MaxFunction.counts);
	}
	
	public void rungaint(int ng, int chromeSize, int popsize, int left, int right, FitnessFunction fitnessfun) {
		long startTime = System.currentTimeMillis();
		int numEvolutions = ng;
		Configuration gaConf = new DefaultConfiguration();
		gaConf.reset();
		gaConf.setPreservFittestIndividual(true);
		gaConf.setKeepPopulationSizeConstant(false);
		Genotype genotype = null;

		try {
			// 构建基因(Gene)
			Gene[] sampleGenes = new Gene[chromeSize];// 基因长度2
			for (int i = 0; i < sampleGenes.length; i++) {
				sampleGenes[i] = new IntegerGene(gaConf, left, right);
			}
			// 构建染色体(Chromosome)
			IChromosome sampleChromosome = new Chromosome(gaConf, sampleGenes);
			gaConf.setSampleChromosome(sampleChromosome);
			gaConf.setPopulationSize(popsize);
			gaConf.setFitnessFunction(fitnessfun);
			genotype = Genotype.randomInitialGenotype(gaConf);
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
			System.exit(-2);
		}
		int progress = 0;
		int percentEvolution = numEvolutions / 10;
		for (int i = 0; i < numEvolutions; i++) {
			genotype.evolve();
			// Print progress.
			// ---------------
			progress++;
			IChromosome fittest = genotype.getFittestChromosome();

			if (percentEvolution > 0 && i % percentEvolution == 0) {
				double fitness = fittest.getFitnessValue();
				System.out.println("Currently fittest Chromosome has fitness " + fitness);
			}
		}
		// Print summary.
		// --------------
		IChromosome fittest = genotype.getFittestChromosome();
		System.out.println("Fittest Chromosome has fitness " + (fittest.getFitnessValue()));

		DecimalFormat myformat = new DecimalFormat("#0.00");
		for (int i = 0; i < chromeSize; i++) {

			// System.out.println(myformat.format(((DoubleGene)fittest.getGene(i)).doubleValue()));
			System.out.print(myformat.format(fittest.getGene(i).getAllele()) + "	");
		}
		System.out.println();
		long endTime = System.currentTimeMillis();
		System.out.println("运行时间 " + (endTime - startTime) + "ms");
		System.out.println("sum counts:  " + MaxFunction.counts);
	}

	/**
	 * Starts the example.
	 * 
	 * @param args
	 *            if optional first argument provided, it represents the number
	 *            of bits to use, but no more than 32
	 * 
	 * @author Neil Rotstan
	 * @author Klaus Meffert
	 * @throws IOException
	 * @since 2.0
	 */
	public static void main(String[] args) throws IOException {

		SimpleExample se = new SimpleExample();

		for (int a = 0; a <= 0; a++) {
			// se.runga(100, 30, 40, -100, 100, new MaxFunction());
//			se.runga(500, 3, 100, 0, 625, new FarmFunction());
			se.rungaint(500, 6, 100, 0, 2, new FarmFunction2());

		}

	}
}
