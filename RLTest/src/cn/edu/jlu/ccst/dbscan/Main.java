package cn.edu.jlu.ccst.dbscan;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double e = 0.11;
		int k = 5;
		String fileName = "4.txt";
		
		DBSCAN d = new DBSCAN();
		Util u = new Util();
		d.run(e, k, u.readFile(fileName));
		JfreeChartScatter scatter = new JfreeChartScatter();
		scatter.Scatter(d.getGroups());
	}

}
