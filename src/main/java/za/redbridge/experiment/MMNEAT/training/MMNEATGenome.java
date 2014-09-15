package za.redbridge.experiment.MMNEAT.training;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.mathutil.randomize.RangeRandomizer;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.NEATPopulation;
import org.encog.neural.neat.training.NEATGenome;
import org.encog.neural.neat.training.NEATLinkGene;
import org.encog.neural.neat.training.NEATNeuronGene;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import za.redbridge.experiment.MMNEAT.MMNEATPopulation;

/**
 * Created by jamie on 2014/09/08.
 */
public class MMNEATGenome extends NEATGenome {

    private static final long serialVersionUID = 7694155481242674578L;

    private final List<MMNEATNeuronGene> inputsList = new ArrayList<>();

    /**
     * Construct a genome by copying another.
     *
     * @param other
     *            The other genome.
     */
    public MMNEATGenome(MMNEATGenome other) {
        setNetworkDepth(other.getNetworkDepth());
        setPopulation(other.getPopulation());
        setScore(other.getScore());
        setAdjustedScore(other.getAdjustedScore());
        setInputCount(other.getInputCount());
        setOutputCount(other.getOutputCount());
        setSpecies(other.getSpecies());

        // copy neurons
        final List<NEATNeuronGene> neurons = getNeuronsChromosome();
        for (NEATNeuronGene oldNeuron : other.getNeuronsChromosome()) {
            final MMNEATNeuronGene newGene = new MMNEATNeuronGene((MMNEATNeuronGene) oldNeuron);
            neurons.add(newGene);

            if (newGene.getNeuronType() == NEATNeuronType.Input) {
                inputsList.add(newGene);
            }
        }

        // copy links
        final List<NEATLinkGene> links = getLinksChromosome();
        for (NEATLinkGene oldGene : other.getLinksChromosome()) {
            final NEATLinkGene newGene = new NEATLinkGene(
                    oldGene.getFromNeuronID(), oldGene.getToNeuronID(),
                    oldGene.isEnabled(), oldGene.getInnovationId(),
                    oldGene.getWeight());
            links.add(newGene);
        }
    }

    /**
     * Create a NEAT gnome. Neuron genes will be added by reference, links will
     * be copied.
     *
     * @param neurons
     *            The neurons to create.
     * @param links
     *            The links to create.
     * @param inputCount
     *            The input count.
     * @param outputCount
     *            The output count.
     */
    public MMNEATGenome(List<NEATNeuronGene> neurons, List<NEATLinkGene> links, int inputCount,
            int outputCount) {
        setAdjustedScore(0);
        setInputCount(inputCount);
        setOutputCount(outputCount);

        List<NEATLinkGene> ourLinks = getLinksChromosome();
        for (NEATLinkGene gene : links) {
            ourLinks.add(new NEATLinkGene(gene));
        }

        if (!neurons.stream().allMatch(o -> o instanceof MMNEATNeuronGene)) {
            throw new ClassCastException("Neuron gene is not a MMNEAT neuron gene");
        }
        List<NEATNeuronGene> ourNeurons = getNeuronsChromosome();
        ourNeurons.addAll(neurons);

        neurons.stream().filter(o -> o.getNeuronType() == NEATNeuronType.Input)
                .map(o -> (MMNEATNeuronGene) o)
                .forEach(inputsList::add);
    }

    /**
     * Create a new genome with the specified connection density. This
     * constructor is typically used to create the initial population.
     * @param rnd Random number generator.
     * @param pop The population.
     * @param inputCount The input count.
     * @param outputCount The output count.
     * @param connectionDensity The connection density.
     */
    public MMNEATGenome(Random rnd, NEATPopulation pop, int inputCount, final int outputCount,
            double connectionDensity) {
        MMNEATPopulation population = (MMNEATPopulation) pop;

        setAdjustedScore(0);
        setInputCount(inputCount);
        setOutputCount(outputCount);

        // get the activation function
        ActivationFunction af = pop.getActivationFunctions().pickFirst();

        final List<NEATNeuronGene> neurons = getNeuronsChromosome();

        // first bias
        int innovationID = 0;
        NEATNeuronGene biasGene =
                new MMNEATNeuronGene(NEATNeuronType.Bias, af, inputCount, innovationID++);
        neurons.add(biasGene);

        // then inputs
        for (int i = 0; i < inputCount; i++) {
            MMNEATNeuronGene gene =
                    new MMNEATNeuronGene(NEATNeuronType.Input, af, i, innovationID++);
            neurons.add(gene);

            double bearing = RangeRandomizer.randomize(rnd, -population.getSensorBearingRange(),
                    population.getSensorBearingRange());
            gene.setInputSensorBearing(bearing);

            double orientation = RangeRandomizer.randomize(rnd,
                    -population.getSensorOrientationRange(),
                    population.getSensorOrientationRange());
            gene.setInputSensorOrientation(orientation);

            inputsList.add(gene);
        }

        // then outputs
        for (int i = 0; i < outputCount; i++) {
            NEATNeuronGene gene = new MMNEATNeuronGene(NEATNeuronType.Output, af,
                    i + inputCount + 1, innovationID++);
            neurons.add(gene);
        }

        // and now links
        List<NEATLinkGene> links = getLinksChromosome();
        for (int i = 0; i < inputCount + 1; i++) {
            for (int j = 0; j < outputCount; j++) {
                // make sure we have at least one connection
                if (links.size() < 1 || rnd.nextDouble() < connectionDensity) {
                    long fromID = neurons.get(i).getId();
                    long toID = neurons.get(inputCount + j + 1).getId();
                    double w = RangeRandomizer.randomize(rnd, -pop.getWeightRange(),
                            pop.getWeightRange());
                    NEATLinkGene gene = new NEATLinkGene(fromID, toID, true, innovationID++, w);
                    links.add(gene);
                }
            }
        }
    }

    public MMNEATGenome() {

    }

    public List<MMNEATNeuronGene> getInputNeuronsChromosome() {
        return inputsList;
    }
}
