package za.redbridge.experiment.MMNEAT;

import org.encog.ml.ea.species.BasicSpecies;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATInnovationList;

import java.util.Random;

import za.redbridge.experiment.sensor.SensorType;

/**
 * Created by jamie on 2014/09/08.
 */
public class MMNEATPopulation extends NEATPopulation {

    private static final long serialVersionUID = -6647644833955733411L;

    private final double sensorBearingRange = Math.PI;

    private final double sensorOrientationRange = Math.PI / 2;

    private final double weightRange = 1.0;

    /**
     * An empty constructor for serialization.
     */
    public MMNEATPopulation() {

    }

    /**
     * Construct a starting NEAT population. This does not generate the initial
     * random population of genomes.
     *
     * @param outputCount
     *            The output neuron count.
     * @param populationSize
     *            The population size.
     */
    public MMNEATPopulation(int outputCount, int populationSize) {
        super(SensorType.values().length, outputCount, populationSize);
    }

    @Override
    public void reset() {
        setCODEC(new MMNEATCODEC());
        setGenomeFactory(new FactorMMNEATGenome());

        // create the new genomes
        getSpecies().clear();

        // reset counters
        getGeneIDGenerate().setCurrentID(1);
        getInnovationIDGenerate().setCurrentID(1);

        final Random rnd = getRandomNumberFactory().factor();

        // create one default species
        final BasicSpecies defaultSpecies = new BasicSpecies();
        defaultSpecies.setPopulation(this);

        // create the initial population
        for (int i = 0; i < getPopulationSize(); i++) {
            final NEATGenome genome = getGenomeFactory().factor(rnd, this,
                    getInputCount(), getOutputCount(), getInitialConnectionDensity());
            defaultSpecies.add(genome);
        }
        defaultSpecies.setLeader(defaultSpecies.getMembers().get(0));
        getSpecies().add(defaultSpecies);

        // create initial innovations
        setInnovations(new NEATInnovationList(this));
    }

    @Override
    public double getWeightRange() {
        return weightRange;
    }

    public double getSensorOrientationRange() {
        return sensorOrientationRange;
    }

    public double getSensorBearingRange() {
        return sensorBearingRange;
    }
}
