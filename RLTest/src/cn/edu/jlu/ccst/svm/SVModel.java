package cn.edu.jlu.ccst.svm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

public class SVModel {

	private svm_model model;

	public svm_model getModel() {
		return model;
	}

	public void setModel(svm_model model) {
		this.model = model;
	}

	public SVModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public double trian(String addr, double gamma, double c) {
		svm_problem prob_train = this.readdata(addr);
		svm_parameter param = initsvm(gamma, c);
		model = svm.svm_train(prob_train, param);
		return predict(prob_train);
	}
	
	public double predict(svm_problem prob) {
		int count = 0;
		double[] ys = new double[prob.l];
		for (int i = 0; i <= prob.l - 1; i++) {
			ys[i] = svm.svm_predict(model, prob.x[i]);
			count = ys[i] == prob.y[i] ? count + 1 : count;
		}
		// System.out.println(count+" / "+h+" = "+(double)count/h);
		return (double) count / prob.l;

	}

	public List<double[]> scores = null;
	public List<Double> pre_ys = null;


	public svm_parameter initsvm(double gamma, double c) {

		svm_parameter param = new svm_parameter();
		// default values
		param.svm_type = svm_parameter.NU_SVC;
		param.kernel_type = svm_parameter.RBF;
		param.degree = 3;
		param.gamma = gamma; // 1/num_features
		param.coef0 = 0;
		param.nu = 0.5;
		param.cache_size = 100;
		param.C = c;
		param.eps = 0.1;
		param.p = 0.1;
		param.shrinking = 1;
		param.probability = 0;
		param.nr_weight = 0;
		param.weight_label = new int[0];
		param.weight = new double[0];

		return param;
	}

	public double[][] scale(double lower, double upper, double[][] input) {
		int m = input.length;
		int n = input[0].length;
		double[] min = new double[n - 1];
		double[] max = new double[n - 1];
		double[][] output = new double[m][n];
		for (int j = 0; j <= n - 2; j++) {
			min[j] = Double.MAX_VALUE;
			max[j] = -Double.MAX_VALUE;
			for (int i = 0; i <= m - 1; i++) {
				min[j] = Math.min(min[j], input[i][j]);
				max[j] = Math.max(max[j], input[i][j]);
				// output[i][n-1] = input[i][n-1]==0?lower:upper;
				output[i][n - 1] = input[i][n - 1];
			}
		}
		for (int j = 0; j <= n - 2; j++) {
			for (int i = 0; i <= m - 1; i++) {
				output[i][j] = ((upper - lower) * (input[i][j] - min[j]) / (max[j] - min[j])) + lower;
			}
		}
		return output;
	}

	public double[][] generatetestdata() {

		double[][] temp_arr = { { 0.05, 7.0, 0.1, 0.08574 }, { 0.05, 7.0, 0.2, 0.05789 }, { 0.05, 7.0, 0.3, 0.05002 },
				{ 0.05, 7.0, 0.4, 0.06347 }, { 0.05, 7.0, 0.5, 0.06603 }, { 0.05, 7.0, 0.6, 0.07771 },
				// {0.05, 7.0, 0.7, 0.082},
				{ 0.05, 7.0, 0.8, 0.13 }, { 0.05, 7.0, 0.9, 0.17 }, { 0.05, 5.0, 0.7, 0.079 },
				{ 0.05, 5.5, 0.7, 0.069 }, { 0.05, 6.0, 0.7, 0.064 }, { 0.05, 6.5, 0.7, 0.0608 },
				{ 0.05, 7.0, 0.7, 0.06649 }, { 0.05, 7.5, 0.7, 0.1514 }, { 0.05, 8.0, 0.7, 0.1682 },
				// {0.05, 6.5, 0.7, 0.06675},
				{ 0.10, 6.5, 0.7, 0.0517 }, { 0.20, 6.5, 0.7, 0.082 }, { 0.30, 6.5, 0.7, 0.2445 },
				{ 0.40, 6.5, 0.7, 0.3275 }, { 0.50, 6.5, 0.7, 0.468 } };

		return temp_arr;
	}

	public svm_problem readdata(String addr) {
		List<String[]> datalist = new ArrayList<String[]>();

		try {
			InputStreamReader ir = new InputStreamReader(new FileInputStream(addr));
			BufferedReader reader = new BufferedReader(ir);
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				String[] lines = line.trim().split(" ");
				datalist.add(lines);
			}
			ir.close();
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int m = datalist.size();
		int n = datalist.get(0).length;

		svm_node[][] x = new svm_node[m][n-1];
		double[] y = new double[m];
		for (int i = 0; i <= m - 1; i++) {
			y[i] = Double.parseDouble(datalist.get(i)[0]);
			int nn = datalist.get(i).length;
			for (int j = 1; j <= n - 1; j++){
				x[i][j - 1] = new svm_node();
				x[i][j - 1].index = j;
				x[i][j - 1].value = 0;
			}
			for (int j = 1; j <= nn - 1; j++) {
				String[] xp = datalist.get(i)[j].split(":");
				x[i][Integer.parseInt(xp[0])-1].value = Double.parseDouble(xp[1]);
			}
		}
		svm_problem prob = new svm_problem();
		prob.l = m;
		prob.x = x;
		prob.y = y;
		return prob;
	}

	public double run_cross_validation(String addr, double gamma, double c, int nr_fold) {
		svm_problem prob = this.readdata(addr);
		svm_parameter param = initsvm(gamma, c);
		int i;
		int total_correct = 0;
		double total_error = 0;
		double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
		double[] target = new double[prob.l];

		// svm.svm_cross_validation_onebyone(prob,param,nr_fold,target);
		svm.svm_cross_validation(prob, param, nr_fold, target);
		if (param.svm_type == svm_parameter.EPSILON_SVR || param.svm_type == svm_parameter.NU_SVR) {
			for (i = 0; i < prob.l; i++) {
				double y = prob.y[i];
				double v = target[i];
				total_error += (v - y) * (v - y);
				sumv += v;
				sumy += y;
				sumvv += v * v;
				sumyy += y * y;
				sumvy += v * y;
			}
			System.out.print("Cross Validation Mean squared error = " + total_error / prob.l + "\n");
			System.out.print("Cross Validation Squared correlation coefficient = "
					+ ((prob.l * sumvy - sumv * sumy) * (prob.l * sumvy - sumv * sumy))
							/ ((prob.l * sumvv - sumv * sumv) * (prob.l * sumyy - sumy * sumy))
					+ "\n");
			return total_error / prob.l;
		} else {
			for (i = 0; i < prob.l; i++)
				if (target[i] == prob.y[i])
					++total_correct;
			 System.out.print("Cross Validation Accuracy ="+100.0*total_correct/prob.l+"%\n");
			return 1.0 * total_correct / prob.l;
		}
	}

	public static void main(String[] args) {
		SVModel svmodle = new SVModel();

		String addr = "iris.txt";
		int nr_fold = 10;
		double gamma = 0.03;
		double c = 1;

		svmodle.run_cross_validation(addr, gamma, c, nr_fold);
		System.out.println(svmodle.trian(addr, gamma, c));

	}

}
