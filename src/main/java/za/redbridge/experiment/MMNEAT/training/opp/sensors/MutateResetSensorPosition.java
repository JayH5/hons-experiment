package za.redbridge.experiment.MMNEAT.training.opp.sensors;

import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

import java.util.Random;

import za.redbridge.experiment.MMNEAT.training.MMNEATNeuronGene;

/**
 * Mutation that resets a sensor's position to a new random position.
 *
 * Created by jamie on 2014/10/14.
 */
public class MutateResetSensorPosition implements MutateSensorPosition {

    private EvolutionaryAlgorithm trainer;

    @Override
    public EvolutionaryAlgorithm getTrainer() {
        return trainer;
    }

    @Override
    public void init(EvolutionaryAlgorithm theTrainer) {
        trainer = theTrainer;
    }

    @Override
    public void mutatePosition(Random rnd, MMNEATNeuronGene neuronGene, double bearingRange,
            double orientationRange) {
        neuronGene.setInputSensorBearing(RangeRandomizer.randomize(rnd, -bearingRange,
                bearingRange));
        neuronGene.setInputSensorOrientation(RangeRandomizer.randomize(rnd, -orientationRange,
                orientationRange));
    }
}
