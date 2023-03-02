import com.beesplease.sugarandspice.math.MatrixHelper;
import com.beesplease.sugarandspice.math.solvers.ODESolver;
import com.beesplease.sugarandspice.math.solvers.SimpleEulerSolver;
import com.beesplease.sugarandspice.simulation.Netlist;
import com.beesplease.sugarandspice.simulation.SimulationModel;
import com.beesplease.sugarandspice.simulation.components.basic.Resistor;
import com.beesplease.sugarandspice.simulation.components.basic.VoltageSource;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import static com.beesplease.sugarandspice.BuildConfig.LOGGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestSimulation {
    @BeforeClass
    public static void setupLogging() {
        try (var ignored = Configurator.initialize(null, "log4j2-test.xml")) {
        }
    }

    @Test
    public void testNetlist() {
        Netlist netlist = new Netlist();
        netlist.addComponent(new Resistor(100d, 0, 1)); // 100 ohms
        netlist.addComponent(new VoltageSource(5d, 0, 1)); // 5V

        SimulationModel model = netlist.buildSimulationModel(1);
        assertNotNull(model);
        Triple<RealMatrix, RealMatrix, RealVector> systemMatrices = model.buildSystemMatrices();

        MatrixHelper.printMatrix(systemMatrices.getLeft());
        MatrixHelper.printMatrix(systemMatrices.getMiddle());
        MatrixHelper.printVector(systemMatrices.getRight());

        ODESolver solver = new SimpleEulerSolver(systemMatrices).withStartingTimestep(0);
        RealVector solution = solver.calculateNextIteration(1);

        MatrixHelper.printVector(solution);

        assertEquals(2, solution.getDimension());
        assertEquals(-5, solution.getEntry(0)); // potential of node 1 (should be the same as voltage source)
        assertEquals(-0.05, solution.getEntry(1)); // current supplied by voltage source (should be U/R = 0.05 A)

        LOGGER.info("Simulation tested");
    }
}
