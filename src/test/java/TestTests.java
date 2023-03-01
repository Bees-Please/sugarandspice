import com.beesplease.sugarandspice.math.MatrixHelper;
import org.apache.commons.math3.linear.OpenMapRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import static com.beesplease.sugarandspice.BuildConfig.LOGGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestTests {

    @BeforeClass
    public static void setupLogging() {
        try (var ignored = Configurator.initialize(null, "log4j2-test.xml")) {
            LOGGER.debug("testing logger configured");
        }
    }

    @Test
    void firstLittleTest() {
        LOGGER.info("Running unit tests...");
        assertEquals(1, 1);
    }

    @Test
    void testMatrixHelper() {
        RealMatrix a = new OpenMapRealMatrix(3, 3);
        a.setEntry(0, 0, 1.0);
        a.setEntry(0, 2, 2.0);
        a.setEntry(2, 0, 3.0);
        a.setEntry(2, 2, 4.0);

        RealMatrix b = new OpenMapRealMatrix(3, 3);
        b.setEntry(0, 1, 5.0);
        b.setEntry(1, 2, 6.0);
        b.setEntry(2, 0, 7.0);
        b.setEntry(2, 2, 8.0);

        // hopefully no dimension errors
        RealMatrix x = MatrixHelper.hstack(a, b);
        RealMatrix y = MatrixHelper.vstack(a, b);
        RealMatrix z = MatrixHelper.hstack(MatrixHelper.vstack(a, b), b);

        assertNotNull(x);
        assertNotNull(y);
        assertNotNull(z);

        RealMatrix large1 = new OpenMapRealMatrix(5, 5);
        RealMatrix large2 = new OpenMapRealMatrix(5, 5);

        // test sparse sub-matrix definition
        large1.setSubMatrix(a.getData(), 1, 2);
        MatrixHelper.setSparseSubMatrix(large2, a, 1, 2);
        assertEquals(large1, large2);
    }
}
