package com.beesplease.sugarandspice.math.solvers;

import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.OpenMapRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class SimpleEulerSolver extends ODESolver {
    private RealMatrix M;
    private RealMatrix K;
    private RealVector r;
    private RealVector lastSolution;
    private double lastSolutionTimestamp = -1;

    public SimpleEulerSolver(RealMatrix M, RealMatrix K, RealVector r) {
        this.M = M;
        this.K = K;
        this.r = r;
        lastSolution = new OpenMapRealVector(M.getRowDimension());

        testSystemValidity();
    }

    public SimpleEulerSolver(Triple<RealMatrix, RealMatrix, RealVector> system) {
        this(system.getLeft(), system.getMiddle(), system.getRight());
    }

    private void testSystemValidity() {
        assert M.getColumnDimension() == K.getColumnDimension();
        assert M.getRowDimension() == K.getRowDimension();

        assert M.getRowDimension() == lastSolution.getDimension();
        assert M.getColumnDimension() == r.getDimension();
    }

    public void updateSystem(Triple<RealMatrix, RealMatrix, RealVector> system) {
        M = system.getLeft() != null ? system.getLeft() : M;
        K = system.getMiddle() != null ? system.getMiddle() : K;
        r = system.getRight() != null ? system.getRight() : r;

        lastSolution = lastSolution.getDimension() == M.getRowDimension() ? lastSolution : new OpenMapRealVector(M.getRowDimension());

        testSystemValidity();
    }

    @Override
    public RealVector getLatestSolution() {
        return lastSolution;
    }

    public SimpleEulerSolver withStartingSolution(RealVector startingSolution) {
        this.lastSolution = startingSolution;
        return this;
    }

    public SimpleEulerSolver withStartingTimestep(double startingTimestep) {
        this.lastSolutionTimestamp = startingTimestep;
        return this;
    }

    @Override
    public RealVector calculateNextIteration(double nextTimestep) {
        double h = nextTimestep - this.lastSolutionTimestamp;
        RealMatrix MOverH = M.scalarMultiply(1 / h);

        RealMatrix A = MOverH.add(K);
        RealVector b = MOverH.operate(lastSolution).add(r);

        LUDecomposition luDecomposition = new LUDecomposition(A);
        lastSolution = luDecomposition.getSolver().solve(b);
        lastSolutionTimestamp = nextTimestep;

        return lastSolution;
    }
}
