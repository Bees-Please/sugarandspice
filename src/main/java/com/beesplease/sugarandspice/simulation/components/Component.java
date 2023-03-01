package com.beesplease.sugarandspice.simulation.components;

import com.beesplease.sugarandspice.simulation.SimulationModel;

import java.util.function.BooleanSupplier;

/*
Class for all electronic components. Can check whether it still exists, and can populate the netlist
 */
public abstract class Component {

    public final int[] nodes;
    private double cachedExistenceAt = -1;
    private boolean cachedExistence = true;
    private BooleanSupplier doesStillExist = () -> true;

    public Component(int... nodes) {
        assert nodes.length == getNumberOfConnections(); // safety check
        this.nodes = nodes;
    }

    // Warning: Not lazy! Use cautiously! The Supplier might be expensive to run!
    protected boolean doesStillExist() {
        return doesStillExist.getAsBoolean(); // test to invalidate components of blocks that were removed
    }

    public int getNumberOfConnections() {  // some components might have multiple connections. If the connections is more than two, this needs to be overridden.
        return 2;
    }

    public void setDoesStillExistCheck(BooleanSupplier doesStillExist) {
        this.doesStillExist = doesStillExist;
    }

    // cached existence check
    public boolean doesStillExist(double simulationTimeStep) {
        if (simulationTimeStep > cachedExistenceAt) {
            cachedExistenceAt = simulationTimeStep;
            cachedExistence = doesStillExist();
        }
        return cachedExistence;
    }

    // add this component to a simulation to be simulated
    public void addToSimulation(SimulationModel simulationModel) {
    }
}
