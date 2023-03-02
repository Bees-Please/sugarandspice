package com.beesplease.sugarandspice.simulation;

import com.beesplease.sugarandspice.math.MatrixHelper;
import com.beesplease.sugarandspice.simulation.components.Component;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SimulationModel {
    private final double simulationTimeStep;
    private int componentCount = 0;

    private SimulationDescriptors descriptors = null;

    public SimulationModel(double simulationTimeStep) {

        this.simulationTimeStep = simulationTimeStep;
    }

    public double getSimulationTimeStep() {
        return simulationTimeStep;
    }

    public void addToSimulation(Component component) {
        if (this.descriptors == null)
            throw new IllegalStateException("Can not set add component before initializing available nodes!");

        componentCount++;
        component.addToSimulation(this);
    }

    public int getComponentCount() {
        return componentCount;
    }

    // to set the ground node to something that is not the lowest ID
    public void setGroundNode(int groundNodeId) {
        if (this.descriptors == null)
            throw new IllegalStateException("Can not set ground node before initializing available nodes!");

        this.descriptors.setGroundNode(groundNodeId);
    }

    public void setNodeSet(Collection<Integer> nodes) {
        List<Integer> nodesSorted = new ArrayList<>(nodes);
        Collections.sort(nodesSorted); // assumption: ground is always the node with the lowest ID

        BiMap<Integer, Integer> nodeMap = HashBiMap.create(nodesSorted.size());  // nodeId of netlist -> id in the equation
        for (int i = 0; i < nodes.size(); i++) {
            nodeMap.put(nodesSorted.get(i), i);
        }
        this.descriptors = new SimulationDescriptors(nodeMap);
    }

    public SimulationDescriptors getDescriptors() {
        return descriptors;
    }

    public Triple<RealMatrix, RealMatrix, RealVector> buildSystemMatrices() {
        RealMatrix G = new DiagonalMatrix(descriptors.getValuesG().stream().mapToDouble(Double::doubleValue).toArray());
        RealMatrix L = new DiagonalMatrix(descriptors.getValuesL().stream().mapToDouble(Double::doubleValue).toArray());
        RealMatrix C = new DiagonalMatrix(descriptors.getValuesC().stream().mapToDouble(Double::doubleValue).toArray());
        RealVector I = new OpenMapRealVector(descriptors.getValuesI().stream().mapToDouble(Double::doubleValue).toArray());
        RealVector V = new OpenMapRealVector(descriptors.getValuesV().stream().mapToDouble(Double::doubleValue).toArray());

        RealMatrix K_TL = MatrixHelper.matrixEmpty(descriptors.getIncidenceG()) ? null : descriptors.getIncidenceG().multiply(G).multiply(descriptors.getIncidenceG().transpose());


        // K matrix (linear terms)
        RealMatrix K = MatrixHelper.vstack(MatrixHelper.hstack(K_TL, descriptors.getIncidenceL(), descriptors.getIncidenceV()),
                MatrixHelper.matrixEmpty(descriptors.getIncidenceL()) ? null : descriptors.getIncidenceL().transpose().scalarMultiply(-1),
                MatrixHelper.matrixEmpty(descriptors.getIncidenceV()) ? null : descriptors.getIncidenceV().transpose().scalarMultiply(-1));

        assert K.getRowDimension() == K.getColumnDimension(); // K needs to be square, I think?? If not, this will at least scream.


        // M matrix (derivatives)
        RealMatrix M = new OpenMapRealMatrix(K.getRowDimension(), K.getColumnDimension());
        int capRows = 0;
        int capCols = 0;

        if (!MatrixHelper.matrixEmpty(descriptors.getIncidenceC())) {
            RealMatrix capacityMatrix = descriptors.getIncidenceC().multiply(C).multiply(descriptors.getIncidenceC().transpose());
            assert capacityMatrix.getRowDimension() == capacityMatrix.getColumnDimension(); // same as for K
            MatrixHelper.setSparseSubMatrix(M, capacityMatrix, 0, 0);
            capRows = capacityMatrix.getRowDimension();
            capCols = capacityMatrix.getColumnDimension();
        }

        MatrixHelper.setSparseSubMatrix(M, L, capRows, capCols);


        // right side
        RealVector r = new OpenMapRealVector(K.getColumnDimension());
        int currentSourceDim = 0;
        if (!MatrixHelper.matrixEmpty(descriptors.getIncidenceI())) {
            RealVector currentSourceInfo = descriptors.getIncidenceI().operate(I);
            r.setSubVector(0, currentSourceInfo);
            currentSourceDim = currentSourceInfo.getDimension();
        }
        r.setSubVector(r.getDimension() - (currentSourceDim + V.getDimension()), V);

        return Triple.of(M, K, r);
    }
}
