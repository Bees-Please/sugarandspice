import com.beesplease.sugarandspice.BuildConfig;
import com.beesplease.sugarandspice.math.solvers.ODESolver;
import com.beesplease.sugarandspice.math.solvers.SimpleEulerSolver;
import com.beesplease.sugarandspice.simulation.Netlist;
import com.beesplease.sugarandspice.simulation.SimulationModel;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestMotor {
    @BeforeClass
    public static void setupLogging() {
        try (var ignored = Configurator.initialize(null, "log4j2-test.xml")) {
        }
    }

    @Test
    public void testMotor() {
        Netlist netlist = new Netlist();
        netlist.addComponent(new VoltageSource(t -> 90 * Math.sin(2 * Math.PI * 50 * t) * Math.sqrt(3), 0, 1)); // 50 Hz source based on the timeStep atomic, 90V effective voltage
        netlist.addComponent(new Resistor(800d, 1, 2)); // 800 Ohms
        netlist.addComponent(new Inductor(0.001d, 2, 0)); // 1mH

        XYSeries voltage = new XYSeries("voltage at source [V]");
        XYSeries current = new XYSeries("current through inductor [mA]");
        XYSeries power = new XYSeries("power supplied [totally not in deci Watts ;)]");


        RealVector lastSolution = new ArrayRealVector(new double[]{0, 0, 0, 0}); // potential at 1, potential at 2, current through inductor, current through voltage source
        double h = 1e-6;

        long startTime = System.nanoTime();
        int timesteps = 0;

        for (double t = h; t < 1d / 20; t += h) {
            timesteps++;
            SimulationModel model = netlist.buildSimulationModel(t);
            assertNotNull(model);
            Triple<RealMatrix, RealMatrix, RealVector> systemMatrices = model.buildSystemMatrices();
            ODESolver solver = new SimpleEulerSolver(systemMatrices)
                    .withStartingTimestep(t - h)
                    .withStartingSolution(lastSolution); // Start: 1 V, no current flow

            lastSolution = solver.calculateNextIteration(t);
            assertEquals(4, lastSolution.getDimension());
            voltage.add(t, lastSolution.getEntry(0));
            current.add(t, lastSolution.getEntry(2) * 1000);
            power.add(t, lastSolution.getEntry(0) * lastSolution.getEntry(2) * 10);
        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;

        BuildConfig.LOGGER.info("Calculating {} timesteps took {} ms", timesteps, duration);

        if (System.getProperty("enableGraphing") == null || !System.getProperty("enableGraphing").equals("true")) {
            return;
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(voltage);
        dataset.addSeries(current);
        dataset.addSeries(power);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Motor behaviour", // Chart title
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
