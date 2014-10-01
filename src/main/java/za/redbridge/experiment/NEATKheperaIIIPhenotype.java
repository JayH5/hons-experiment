package za.redbridge.experiment;

import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.neat.NEATNetwork;

import java.util.List;
import java.util.Map;

import sim.util.Double2D;
import za.redbridge.simulator.khepera.KheperaIIIPhenotype;

/**
 * Khepera III phenotype with a NEAT controller.
 * Created by jamie on 2014/09/25.
 */
public class NEATKheperaIIIPhenotype extends KheperaIIIPhenotype {

    private final MLData input;

    private final NEATNetwork network;

    private static final Configuration DEFAULT_CONFIGURATION = new Configuration();
    static {
        DEFAULT_CONFIGURATION.enableProximitySensors40Degrees = true;
        DEFAULT_CONFIGURATION.enableProximitySensorBottom = true;
        DEFAULT_CONFIGURATION.enableUltrasonicSensor0Degrees = true;
        DEFAULT_CONFIGURATION.enableUltrasonicSensors90Degrees = true;
    }

    public NEATKheperaIIIPhenotype(NEATNetwork network) {
        super(DEFAULT_CONFIGURATION);
        this.network = network;
        input = new BasicMLData(DEFAULT_CONFIGURATION.getNumberOfSensors());
    }

    @Override
    public Double2D step(List<List<Double>> sensorReadings) {
        for (int i = 0, n = input.size(); i < n; i++) {
            input.setData(i, sensorReadings.get(i).get(0));
        }

        MLData output = network.compute(input);

        return new Double2D(output.getData(0) * 2.0 - 1.0, output.getData(1) * 2.0 - 1.0);
    }

    @Override
    public void configure(Map<String, Object> stringObjectMap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public NEATKheperaIIIPhenotype clone() {
        return new NEATKheperaIIIPhenotype(network);
    }

    public static int getNumberOfSensors() {
        return DEFAULT_CONFIGURATION.getNumberOfSensors();
    }

}
