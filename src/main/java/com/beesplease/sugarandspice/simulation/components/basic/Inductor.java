package com.beesplease.sugarandspice.simulation.components.basic;

import com.beesplease.sugarandspice.simulation.SimulationModel;
import com.beesplease.sugarandspice.simulation.components.ValuedComponent;

import java.util.function.Supplier;

public class Inductor extends ValuedComponent<Double> {

    // constant resistance
    public Inductor(Double value, int... nodes) {
        super(value, nodes);
    }

    // variable resistance
    public Inductor(Supplier<Double> value, int... nodes) {
        super(value, nodes);
    }

    @Override
    public void addToSimulation(SimulationModel simulationModel) {
        simulationModel.getDescriptors().addInductor(getValue(simulationModel.getSimulationTimeStep()), nodes[0], nodes[1]);
    }
}
