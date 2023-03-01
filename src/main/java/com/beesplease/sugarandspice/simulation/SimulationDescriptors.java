package com.beesplease.sugarandspice.simulation;

import com.beesplease.sugarandspice.math.MatrixHelper;
import com.google.common.collect.BiMap;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SimulationDescriptors {
    public final BiMap<Integer, Integer> nodes;
    int[] vectorMaskColumns = new int[]{0};
    int[] vectorMaskRows;
    // incidence matrices
    private RealMatrix incidenceG;
    private RealMatrix incidenceL;
    private RealMatrix incidenceC;
    private RealMatrix incidenceV;
    private RealMatrix incidenceI;
    // values
    private List<Double> vG = new ArrayList<>();
    private List<Double> vL = new ArrayList<>();
    private List<Double> vC = new ArrayList<>();
    private List<Double> vI = new ArrayList<>();
    private List<Double> vV = new ArrayList<>();
    private int groundNode = 0;  // node to remove from the equation for being ground

    public SimulationDescriptors(BiMap<Integer, Integer> nodes) {
        this.nodes = nodes;

        incidenceG = new OpenMapRealMatrix(nodes.size() - 1, 0);
        incidenceL = new OpenMapRealMatrix(nodes.size() - 1, 0);
        incidenceC = new OpenMapRealMatrix(nodes.size() - 1, 0);
        incidenceV = new OpenMapRealMatrix(nodes.size() - 1, 0);
        incidenceI = new OpenMapRealMatrix(nodes.size() - 1, 0);

        updateVectorMask();
    }

    public void setGroundNode(int groundNodeId) {
        this.groundNode = nodes.getOrDefault(groundNodeId, 0);
        updateVectorMask();
    }

    private void updateVectorMask() {
        vectorMaskRows = new int[nodes.size()];
        int entry = 0;
        for (int i = 0; entry < nodes.size() - 1; i++) {
            if (i != groundNode) {
                vectorMaskRows[entry] = i;
            }
            entry++;
        }
    }

    public List<Double> getValuesG() {
        return vG;
    }

    public List<Double> getValuesL() {
        return vL;
    }

    public List<Double> getValuesC() {
        return vC;
    }

    public List<Double> getValuesI() {
        return vI;
    }

    public List<Double> getValuesV() {
        return vV;
    }

    public RealMatrix getIncidenceG() {
        return incidenceG;
    }

    public RealMatrix getIncidenceL() {
        return incidenceL;
    }

    public RealMatrix getIncidenceC() {
        return incidenceC;
    }

    public RealMatrix getIncidenceV() {
        return incidenceV;
    }

    public RealMatrix getIncidenceI() {
        return incidenceI;
    }

    private RealMatrix updateIncidenceMatrix(Supplier<RealMatrix> base, int node1, int node2) {
        RealMatrix branchVector = new OpenMapRealMatrix(nodes.size(), 1);
        branchVector.setEntry(nodes.get(node1), 0, 1);
        branchVector.setEntry(nodes.get(node2), 0, -1);
        return MatrixHelper.hstack(base.get(), branchVector.getSubMatrix(vectorMaskRows, vectorMaskColumns));
    }

    public void addResistor(double value, int node1, int node2) {
        vG.add(1 / value);
        incidenceG = updateIncidenceMatrix(this::getIncidenceG, node1, node2);
    }

    public void addCapacitor(double value, int node1, int node2) {
        vC.add(value);
        incidenceC = updateIncidenceMatrix(this::getIncidenceC, node1, node2);
    }

    public void addInductor(double value, int node1, int node2) {
        vL.add(value);
        incidenceL = updateIncidenceMatrix(this::getIncidenceL, node1, node2);
    }

    public void addCurrentSource(double value, int node1, int node2) {
        vI.add(-value); // negative because equation system
        incidenceI = updateIncidenceMatrix(this::getIncidenceI, node1, node2);
    }

    public void addVoltageSource(double value, int node1, int node2) {
        vV.add(-value); // negative because equation system
        incidenceV = updateIncidenceMatrix(this::getIncidenceV, node1, node2);
    }
}
