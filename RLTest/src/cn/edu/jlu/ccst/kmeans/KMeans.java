package cn.edu.jlu.ccst.kmeans;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class KMeans {
	private static int ClusterNum;
	private static String addr;
	private static ArrayList<ArrayList<double[]>> cluster;
	private static double[][] center = new double[ClusterNum][2];
	private static double[][] lastCenter = new double[ClusterNum][2];
	private ArrayList<double[]> dataSet = new ArrayList<double[]>();
	
	/*
	 * ���캯��
	 */
	public KMeans(int clusterNum, String addr)
	{
		ClusterNum = clusterNum;
		this.addr = addr;
		cluster = new ArrayList<ArrayList<double[]>>();
		center = new double[ClusterNum][2];
		lastCenter = new double[ClusterNum][2];
		dataSet = new ArrayList<double[]>();
	}
	
	/*
	 * ��ִ�з���
	 */
	public void ExecuteMethod() throws IOException
	{
		LoadDataSet();
		initCenters();
		do
		{
			initCluster();
			AllocateCluster();
			lastCenter = center;
			setNewCenter();
		}
		while(this.IsCenterChanged(center));
	}
	
	/*
	 * ��ȡ��
	 */
	public ArrayList<ArrayList<double[]>> getCluster()
	{
		return cluster;
	}
	
	/*
	 * װ������
	 */
	private void LoadDataSet() throws IOException
	{
		String fileName =  addr;
		FileReader fileReader = new FileReader(fileName);
		BufferedReader bufferReader = new BufferedReader(fileReader);
		String line = bufferReader.readLine();
		while(!(line == null || line.isEmpty()))
		{
			double[] data  = new double[2];
			String[] input = line.split("\t");
			data[0] = Double.parseDouble(input[0]);
			data[1] = Double.parseDouble(input[1]);
			line = bufferReader.readLine();
			dataSet.add(data);
		}
		
		fileReader.close();
		bufferReader.close();
	}
	
	/*
	 * �жϴ����ĵ��Ƿ�ı�  ��Ϊ�㷨��������
	 */
	private boolean IsCenterChanged(double[][] center)
	{
		for(int i = 0; i < center.length; i++)
		{
			if(center[i][0] != lastCenter[i][0] || center[i][1] != lastCenter[i][1])
			{
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * ��ʼ��������
	 */
	private void initCenters()
	{
		//// ���ĵ�����ûᵼ�·���ʧ��
		//Ϊ�˱�̷��㣬����ȷ���˶�ά���ݣ��Ժ���ظĳɶ�̬ƥ��ά��
		double l_min = 0;
		double l_max = 0;
		double r_min = 0;
		double r_max = 0;
		for(double[] data : this.dataSet){
			l_min = (data[0]<l_min)?data[0]:l_min;
			l_max = (data[0]>l_max)?data[0]:l_max;
			r_min = (data[1]<r_min)?data[1]:r_min;
			r_max = (data[1]>r_max)?data[1]:r_max;
		}
		center = new double[ClusterNum][2];
		for(int i = 0; i<= this.ClusterNum-1;i++){
			center[i] = new double[]{Math.random()*(l_max-l_min)+l_min,Math.random()*(r_max-r_min)+r_min};
		}
//		center = new double[][]{{-1,-2},{-3,2},{2,4}};
//		center = new double[][]{{0.403,0.237},{0.343,0.099},{0.532,0.472}};
	}
	
	/*
	 * ��ʼ��������
	 */
	private void initCluster()
	{
		ArrayList<ArrayList<double[]>> initCluster = new ArrayList<ArrayList<double[]>>();
		for(int i = 0; i < ClusterNum; i++)
		{
			initCluster.add(new ArrayList<double[]>());
		}
		
		if(cluster != null)
		{
			cluster.clear();
		}
		
		cluster = initCluster;
	}
	
	/*
	 * ����ŷʽ����  ���Ը��ݾ�����ɷ���
	 */
	private double CalcDistance(double[] element, double[] center)
	{
		double x = element[0] - center[0];
		double y = element[1] - center[1];
		double z = x*x + y*y;
		return (double)Math.sqrt(z);
	}
	
	/*
	 * ��ȡ����ڵ������ĸ���
	 */
	private int getClusterIndex(double[] distance)
	{
		double minDistance = distance[0];
		int clusterIndex = 0;
		for(int i = 0; i < distance.length; i++)
		{
			if(distance[i] < minDistance)
			{
				minDistance = distance[i];
				clusterIndex = i;
			}
		}
		
		return clusterIndex;
	}
	
	/*
	 * �����
	 */
	private void AllocateCluster()
	{
		double[] distance = new double[ClusterNum];
		for(double[] data : dataSet)
		{
			for(int j = 0; j < ClusterNum; j++)
			{
				distance[j] = this.CalcDistance(data, center[j]);
			}
			
			int clusterIndex = this.getClusterIndex(distance);
			
			/*
			 * �����ArrayList<double[][]>��������Ҳ�ǿ��е� �����������ܲ��ô���   ������Ϊÿһ���ض�����һ������ֵ
			 */
			cluster.get(clusterIndex).add(data);
		}
	}
	
	/*
	 * �����µĴ�����
	 */
	private void setNewCenter()
	{
		center = new double[ClusterNum][2];
		for(int i = 0; i < center.length; i++)
		{
			if(cluster.get(i).size() != 0)
			{
				double[] newCenter = new double[2];
				for(int j = 0; j < cluster.get(i).size(); j++)
				{
					newCenter[0] += cluster.get(i).get(j)[0];
					newCenter[1] += cluster.get(i).get(j)[1];
				}
				
				center[i][0] = newCenter[0]/cluster.get(i).size();
				center[i][1] = newCenter[1]/cluster.get(i).size();
			}
		}
	}
}
