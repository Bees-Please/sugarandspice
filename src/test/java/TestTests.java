import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import static com.beesplease.sugarandspice.BuildConfig.MODID;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTests {
    public static final Logger LOGGER = LogManager.getLogger(MODID);

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
}
