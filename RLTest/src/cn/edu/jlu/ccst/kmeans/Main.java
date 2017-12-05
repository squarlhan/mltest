package cn.edu.jlu.ccst.kmeans;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
//		KMeans kmeans = new KMeans(3,"testSet.txt");
		KMeans kmeans = new KMeans(4,"4.txt");
		JfreeChartScatter scatter = new JfreeChartScatter();
		kmeans.ExecuteMethod();
		scatter.Scatter(kmeans.getCluster());
	}
}
