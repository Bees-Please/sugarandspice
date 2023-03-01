package com.beesplease.sugarandspice.simulation;

import com.beesplease.sugarandspice.simulation.components.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Netlist {
    private List<Component> netList = new LinkedList<>();

    @Nullable
    public SimulationModel buildSystemMatrices(double simulationTimeStep) {
        Map<Integer, Integer> nodeConnections = new HashMap<>();
        SimulationModel model = new SimulationModel(simulationTimeStep);
        netList.forEach(c -> Arrays.stream(c.nodes).forEach(n -> nodeConnections.put(n, nodeConnections.getOrDefault(n, 0) + 1)));
        Collection<Integer> nodes = nodeConnections.keySet();
        if (nodes.isEmpty())
            throw new RuntimeException("non-empty simulation model does not contain any known nodes");

        model.setNodeSet(nodes);

        netList.stream()
                .filter(c -> Arrays.stream(c.nodes).allMatch(n -> nodeConnections.get(n) >= 2)) // only non-dangling components are simulated.
                .forEach(model::addToSimulation);

        if (model.getComponentCount() == 0)
            return null; // nothing to simulate

        return model;
    }

    public void cleanNetlist(double simulationTimeStep) {
        netList.removeIf(c -> !c.doesStillExist(simulationTimeStep));
    }

    public void addComponent(Component c) {
        netList.add(c);
    }
}
