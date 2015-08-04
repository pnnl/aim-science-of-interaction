package gov.pnnl.aim.discovery.data;

import gov.pnnl.jac.cluster.ClusterList;

import java.io.File;

import org.apache.commons.math3.linear.RealMatrix;

public class InspireTextMatrixInitializerTest {
	
	public static void main(String [] args) {	
		
		int sum = 0;
		for (int i = 1; i < 1000; i++) {
			
			if (i % 3 == 0 || i % 5 == 0) {
				sum += i;
			}
		}
		System.out.println("SUM: " + sum);

		final String path = "/data/GasTech/TextFeatures";		
		final File file = new File(InspireTextMatrixInitializerTest.class.getResource(path).getFile());			
		InspireTextMatrixInitializer in = new InspireTextMatrixInitializer(file, AIMDataType.TEXT);
		
		MatrixProperties props = in.initialize();
		int cols = props.getMatrix().getColumnDimension();
		int rows = props.getMatrix().getRowDimension();
		
		SDIClusteringStrategy cluster = new SDIClusteringStrategy(props);
		ClusterList clusterList = cluster.cluster();
		RealMatrix matrix = cluster.getTwoSpaceMatrix();
		
		int clusterCount = clusterList.getClusterCount();
		System.out.println("Row Count = " + rows);
		System.out.println("Column Count = " + cols);
		System.out.println("Cluster Count = " + clusterCount);
		for (int i = 0; i < clusterCount; i++) {
			System.out.println(matrix.getColumn(i)[0] + ", " + matrix.getColumn(i)[1]);
		}
	}
}
