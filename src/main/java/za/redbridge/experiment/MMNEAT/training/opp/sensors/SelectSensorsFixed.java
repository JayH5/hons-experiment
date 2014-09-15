package za.redbridge.experiment.MMNEAT.training.opp.sensors;

import org.encog.ml.ea.train.EvolutionaryAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import za.redbridge.experiment.MMNEAT.training.MMNEATGenome;
import za.redbridge.experiment.MMNEAT.training.MMNEATNeuronGene;

/**
 * Created by jamie on 2014/09/08.
 */
public class SelectSensorsFixed implements SelectSensors {

    private EvolutionaryAlgorithm evolutionaryAlgorithm;

    private final int sensorCount;

    public SelectSensorsFixed(int sensorCount) {
        this.sensorCount = sensorCount;
    }

    @Override
    public EvolutionaryAlgorithm getTrainer() {
        return evolutionaryAlgorithm;
    }

    @Override
    public void init(EvolutionaryAlgorithm theTrainer) {
        this.evolutionaryAlgorithm = theTrainer;
    }

    @Override
    public List<MMNEATNeuronGene> selectLinks(Random rnd, MMNEATGenome genome) {
        final List<MMNEATNeuronGene> result = new ArrayList<>();
        final int count = Math.min(sensorCount, genome.getInputNeuronsChromosome().size());

        while (result.size() < count) {
            final int idx = rnd.nextInt(genome.getInputNeuronsChromosome().size());
            final MMNEATNeuronGene link = genome.getInputNeuronsChromosome().get(idx);
            if (!result.contains(link)) {
                result.add(link);
            }
        }
        return result;
    }
}
