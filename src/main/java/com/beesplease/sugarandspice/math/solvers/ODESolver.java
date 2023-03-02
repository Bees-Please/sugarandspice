package com.beesplease.sugarandspice.math.solvers;

import org.apache.commons.math3.linear.RealVector;

public abstract class ODESolver {
    public abstract RealVector getLatestSolution();

    public abstract RealVector calculateNextIteration(double timestep);
}
