package agh;

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.linear.*;


public class EquationSolver {
    private final UnivariateIntegrator integrator = new IterativeLegendreGaussIntegrator(50, 1e-9, 1e-9);
    private static final double EPSILON = 1e-12;

    public double[] solve(int n) {
        if(n==1) {
            double mid = integration(1, 0, 0)-3;
            double val = (-30)/mid;
            return new double[]{val, 0.0};
        }
        double[][] data = bDiagonalsAndL(n);
        double[] midDiagonal = data[0];
        double[] upperDiagonal = data[1];
        double[] L =  data[2];

        double[][] B = new double[n][n];
        for (int i = 0; i < n-1; i++) {
            B[i][i] = midDiagonal[i];
            B[i][i + 1] = upperDiagonal[i];
            B[i + 1][i] = upperDiagonal[i];
        }
        B[n-1][n-1] = midDiagonal[n-1];

        RealMatrix matrix = new Array2DRowRealMatrix(B);
        DecompositionSolver solver = new LUDecomposition(matrix).getSolver();
        double[] result = solver.solve(new ArrayRealVector(L)).toArray();
        double[] fullResults = new double[n+1];
        System.arraycopy(result, 0, fullResults, 0, n);
        fullResults[n] = 0.0;
        return fullResults;
    }

    public double[] solveThomasAlgorithm(int n) {
        if(n==1) {
            double mid = integration(1, 0, 0)-3;
            double val = (-30)/mid;
            return new double[]{val, 0.0};
        }
        double[][] data = bDiagonalsAndL(n);
        double[] midDiagonal = data[0];
        for (int i = 0; i < n; i++) midDiagonal[i]+=EPSILON;
        double[] upperDiagonal = data[1];
        double[] L =  data[2];
        double[] result = thomasSolve(midDiagonal, upperDiagonal, L);
        double[] fullResults = new double[n+1];
        System.arraycopy(result, 0, fullResults, 0, n);
        fullResults[n] = 0.0;
        return fullResults;
    }

    private double[][] bDiagonalsAndL(int n) {
        double[][] results = new double[3][n];
        for  (int i = 0; i < n-1; i++) {
            results[0][i] = integration(n, i, i);
            results[1][i] = integration(n, i, i + 1);
            results[2][i] = 0.0;
        }
        results[0][n-1] = integration(n, n-1, n-1);
        results[1][n-1] = 0.0;
        results[2][n-1] = 0.0;
        results[0][0]-=3;
        results[2][0]-=30;
        return results;
    }

    private double integration(int n, int i, int j) {
        if(Math.abs(j-i) > 1) {
            return 0.0;
        }
        double lowerLimit = 2.0*Math.max(Math.max(i, j)-1, 0)/n;
        double upperLimit = 2.0*Math.min(Math.min(i, j)+1, n)/n;
        return integrator.integrate(Integer.MAX_VALUE, x -> E(x)*ePrime(n, i, x)*ePrime(n, j, x), lowerLimit, upperLimit);
    }

    private static double[] thomasSolve(double[] midDiagonal, double[] upperDiagonal, double[] L) {
        int n =  midDiagonal.length;
        double[] newUpperDiagonal = new double[n-1];
        double[] newL = new double[n];
        newUpperDiagonal[0] = upperDiagonal[0]/midDiagonal[0];
        newL[0] = L[0]/midDiagonal[0];
        for(int i = 1; i < n-1; i++) {
            double w = midDiagonal[i]-newUpperDiagonal[i-1]*upperDiagonal[i-1];
            newUpperDiagonal[i] = upperDiagonal[i]/w;
            newL[i] = (L[i]-upperDiagonal[i-1]*newL[i-1])/w;
        }
        double w = midDiagonal[n-1]-newUpperDiagonal[n-2]*upperDiagonal[n-2];
        newL[n-1] = (L[n-1]-upperDiagonal[n-2]*newL[n-2])/w;
        double[] result = new double[n];
        result[n-1] = newL[n-1];
        for(int i = n-2; i >= 0; i--) {
            result[i] = newL[i]-newUpperDiagonal[i]*result[i+1];
        }
        return result;
    }

    private static double E(double x) {
        return x <= 1.0 ? 3.0 : 5.0;
    }

    private static double ePrime(int n, int i, double x) {
        if (x>=(i-1)*2.0/n && x<=i*2.0/n) return n/2.0;
        if (x>i*2.0/n && x<=(i+1)*2.0/n) return -n/2.0;
        return 0.0;
    }
}