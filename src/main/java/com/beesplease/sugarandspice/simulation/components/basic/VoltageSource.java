package com.beesplease.sugarandspice.simulation.components.basic;

import com.beesplease.sugarandspice.simulation.SimulationModel;
import com.beesplease.sugarandspice.simulation.components.ValuedComponent;

import java.util.function.Supplier;

public class VoltageSource extends ValuedComponent<Double> {

    // constant resistance
    public VoltageSource(Double value, int... nodes) {
        super(value, nodes);
    }

    // variable resistance
    public VoltageSource(Supplier<Double> value, int... nodes) {
        super(value, nodes);
    }

    @Override
    public void addToSimulation(SimulationModel simulationModel) {
        simulationModel.getDescriptors().addVoltageSource(getValue(simulationModel.getSimulationTimeStep()), nodes[0], nodes[1]);
    }
}
