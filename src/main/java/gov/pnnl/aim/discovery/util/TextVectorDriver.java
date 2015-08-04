package gov.pnnl.aim.discovery.util;

import gov.pnnl.jac.geom.RealMatrixCoordinateList;
import gov.pnnl.jac.math.linalg.PCA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;


public class TextVectorDriver {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// Read in the document vectors
		File vectFile = new File("00000033.vect.txt");
		BufferedReader reader = new BufferedReader(new FileReader(vectFile));

		reader.readLine();
		reader.readLine();
		reader.readLine();

		List<double []> vectors = new ArrayList<double []>();
		String line = null;
		while ((line = reader.readLine()) != null) {
			String [] values = line.split(" ");
			double [] vect = new double[values.length - 1]; 
			for (int i = 1; i < values.length; i++) {
				vect[i-1] = Double.parseDouble(values[i]); 
			}
			vectors.add(vect);
		}
		reader.close();

		
		// Read in the document vectors
		File centFile = new File("00000033.cluster");
		reader = new BufferedReader(new FileReader(centFile));

		reader.readLine();
		reader.readLine();
		reader.readLine();
		reader.readLine();

		List<double []> centVectors = new ArrayList<double []>();
		line = null;
		while ((line = reader.readLine()) != null) {
			String [] values = line.split(" ");
			double [] vect = new double[values.length]; 
			for (int i = 0; i < values.length; i++) {
				vect[i] = Double.parseDouble(values[i]); 
			}
			centVectors.add(vect);
			reader.readLine();
		}
		reader.close();
		
		
		// For each centroid, find center-most document		
		int sizeC = centVectors.size();
		int sizeV = vectors.size();
		int [] clusterRep = new int[sizeC];
		                         
		for (int i = 0; i < sizeC; i++) {
			
			double [] centroid = centVectors.get(i);
			
			int nearIndex = -1;
			double nearDist = Double.MAX_VALUE;			
			for (int j = 0; j < sizeV; j++) {
				
				double [] docvect = vectors.get(j);
				double dist = dist(docvect, centroid);
				
				if (dist < nearDist) {
					nearDist = dist;
					nearIndex = j;
				}
			}
			clusterRep[i] = nearIndex;
		}
						
		long startMS = System.currentTimeMillis();
		
		// Write out the similarity matrix
		BufferedWriter writer = new BufferedWriter(new FileWriter("simMatrix.txt"));
		writer.write("DocID1\tDocID2\tSimilarity\n");
		
		int size = vectors.size();
		double[][] simMatrix = new double[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				simMatrix[i][j] = ((i == j) ? 1.0 : (dist(vectors.get(i), vectors.get(j)) / Math.sqrt(2.0)));
				writer.write(i + "\t" + j + "\t" + simMatrix[i][j] + "\n");
			}
		}
		writer.close();
		System.out.println("Total Millis: " + (System.currentTimeMillis() - startMS) + "  Records: " + size);
		

		// Write out the representative doc per cluster
		writer = new BufferedWriter(new FileWriter("ClusterCentroids.txt"));
		writer.write("ClusterID\tDocID\n");
		for (int i = 0; i < sizeC; i++) {
			int docid = clusterRep[i];
			writer.write(i + "\t" + docid + "\n");
		}
		writer.close();
	}

	static private double dist(double[] a, double[] b) {
//        return new Cosine().distanceBetween(a, b);
//		return new gov.pnnl.jac.geom.distance.Manhattan().distanceBetween(a,  b) / 50.0;
		return new gov.pnnl.jac.geom.distance.Euclidean().distanceBetween(a, b);
    }

	static private double[] add(double[] source, double[] dest) {
        for (int i = 0; i < source.length; i++) {
            dest[i] += source[i];
        }
        return dest;
    }

	static private double[] divide(double[] dest, double divisor) {
        for (int i = 0; i < dest.length; i++) {
            dest[i] /= divisor;
        }
        return dest;
    }

	static private double[][] reduce(double[][] matrix) {

        Array2DRowRealMatrix realMatrix = new Array2DRowRealMatrix(matrix);
        PCA pca = new PCA(realMatrix, PCA.CovarianceType.COVARIANCE, 2);
        RealMatrix projMatrix = pca.getPrincipalComponents();

        MatrixUtil.normalizeDimensions(new RealMatrixCoordinateList(projMatrix), MatrixUtil.computeMinMax(projMatrix));

        return projMatrix.getData();

// The orientation of this needs to be inverted to work with the calling code
//		return MDSJ.classicalScaling(matrix);
    }
}
