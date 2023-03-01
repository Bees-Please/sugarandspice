package com.beesplease.sugarandspice.simulation.components.basic;

import com.beesplease.sugarandspice.simulation.SimulationModel;
import com.beesplease.sugarandspice.simulation.components.ValuedComponent;

import java.util.function.Supplier;

public class Capacitor extends ValuedComponent<Double> {

    // constant resistance
    public Capacitor(Double value, int... nodes) {
        super(value, nodes);
    }

    // variable resistance
    public Capacitor(Supplier<Double> value, int... nodes) {
        super(value, nodes);
    }

    @Override
    public void addToSimulation(SimulationModel simulationModel) {
        simulationModel.getDescriptors().addCapacitor(getValue(simulationModel.getSimulationTimeStep()), nodes[0], nodes[1]);
    }
}
