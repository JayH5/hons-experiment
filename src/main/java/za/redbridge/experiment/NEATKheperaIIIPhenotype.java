package za.redbridge.experiment;

import org.encog.ml.MLInput;
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

    private final MLData input = new BasicMLData(NUM_SENSORS);

    private final NEATNetwork network;

    public NEATKheperaIIIPhenotype(NEATNetwork network) {
        super();
        this.network = network;
    }

    @Override
    public Double2D step(List<List<Double>> sensorReadings) {
        for (int i = 0; i < input.size(); i++) {
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
}
