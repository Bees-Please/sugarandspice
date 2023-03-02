package com.beesplease.sugarandspice.simulation.components.basic;

import com.beesplease.sugarandspice.simulation.SimulationModel;
import com.beesplease.sugarandspice.simulation.components.ValuedComponent;

import java.util.function.Function;

public class Resistor extends ValuedComponent<Double> {

    // constant resistance
    public Resistor(Double value, int... nodes) {
        super(value, nodes);
    }

    // variable resistance
    public Resistor(Function<Double, Double> value, int... nodes) {
        super(value, nodes);
    }

    @Override
    public void addToSimulation(SimulationModel simulationModel) {
        simulationModel.getDescriptors().addResistor(getValue(simulationModel.getSimulationTimeStep()), nodes[0], nodes[1]);
    }
}
