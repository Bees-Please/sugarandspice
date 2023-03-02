import com.beesplease.sugarandspice.math.MatrixHelper;
import com.beesplease.sugarandspice.math.solvers.ODESolver;
import com.beesplease.sugarandspice.math.solvers.SimpleEulerSolver;
import com.beesplease.sugarandspice.simulation.Netlist;
import com.beesplease.sugarandspice.simulation.SimulationModel;
import com.beesplease.sugarandspice.simulation.components.basic.Capacitor;
import com.beesplease.sugarandspice.simulation.components.basic.Inductor;
import com.beesplease.sugarandspice.simulation.components.basic.Resistor;
import com.beesplease.sugarandspice.simulation.components.basic.VoltageSource;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.logging.log4j.core.config.Configurator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
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
        assertEquals(5, solution.getEntry(0)); // potential of node 1 (should be the same as voltage source)
        assertEquals(0.05, solution.getEntry(1)); // current supplied by voltage source (should be U/R = 0.05 A)

        LOGGER.info("Simulation tested");
    }

    @Test
    public void testLC() {
        Netlist netlist = new Netlist();
        netlist.addComponent(new Capacitor(1d, 0, 1)); // 1F
        netlist.addComponent(new Inductor(1d, 0, 1)); // 1H

        SimulationModel model = netlist.buildSimulationModel(1);
        assertNotNull(model);
        Triple<RealMatrix, RealMatrix, RealVector> systemMatrices = model.buildSystemMatrices();

        MatrixHelper.printMatrix(systemMatrices.getLeft());
        MatrixHelper.printMatrix(systemMatrices.getMiddle());
        MatrixHelper.printVector(systemMatrices.getRight());

        ODESolver solver = new SimpleEulerSolver(systemMatrices)
                .withStartingTimestep(0)
                .withStartingSolution(new ArrayRealVector(new double[]{1, 0})); // Start: 1 V, no current flow

        XYSeries voltage = new XYSeries("voltage over capacitor");
        XYSeries current = new XYSeries("current through inductor");

        // 100 steps of hopefully LC oscillator function
        for (int i = 1; i < 1000; i++) {
            double timestep = i / 50d;
            RealVector solution = solver.calculateNextIteration(timestep);
            assertEquals(2, solution.getDimension());

            voltage.add(timestep, solution.getEntry(0));
            current.add(timestep, solution.getEntry(1));

            // MatrixHelper.printVector(solution);
        }

        if (System.getProperty("enableGraphing") == null || !System.getProperty("enableGraphing").equals("true")) {
            return;
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(voltage);
        dataset.addSeries(current);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "LC oscillator", // Chart title
                "time [seconds]", // X-axis label
                "Amplitude", // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot orientation
                true, // Show legend
                true, // Use tooltips
                false // Generate URLs
        );

        chart.getXYPlot().getRenderer().setSeriesPaint(0, java.awt.Color.RED); // Set color for series1
        chart.getXYPlot().getRenderer().setSeriesPaint(1, java.awt.Color.BLUE); // Set color for series2

        // Display the plot.
        ChartFrame frame = new ChartFrame("Plot", chart);
        frame.pack();
        frame.setVisible(true);
        while (frame.isVisible()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
