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
import za.redbridge.experiment.sensor.SensorType;

/**
 * Created by jamie on 2014/09/08.
 */
public class MMNEATGenome extends NEATGenome {

    private static final long serialVersionUID = 7694155481242674578L;

    private final List<MMNEATNeuronGene> inputsList = new ArrayList<>();
    private final List<MMNEATNeuronGene> outputsList = new ArrayList<>();
    private final List<MMNEATNeuronGene> hiddenList = new ArrayList<>();

    private MMNEATNeuronGene biasGene;

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
        }

        initNeuronLists();

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

        initNeuronLists();
    }

    /**
     * Create a new genome with the specified connection density. This
     * constructor is typically used to create the initial population.
     * @param rnd Random number generator.
     * @param pop The population.
     * @param outputCount The output count.
     * @param connectionDensity The connection density.
     */
    public MMNEATGenome(Random rnd, NEATPopulation pop, int outputCount, double connectionDensity) {
        MMNEATPopulation population = (MMNEATPopulation) pop;

        final SensorType[] sensorTypes = SensorType.values();
        final int inputCount = sensorTypes.length;

        setAdjustedScore(0);
        setOutputCount(outputCount);
        setInputCount(inputCount);

        // get the activation function
        ActivationFunction af = pop.getActivationFunctions().pickFirst();

        final List<NEATNeuronGene> neurons = getNeuronsChromosome();

        // first bias
        int innovationID = 0;
        NEATNeuronGene biasGene =
                new MMNEATNeuronGene(NEATNeuronType.Bias, af, inputCount, innovationID++);
        neurons.add(biasGene);

        // then inputs - minimal set is one of each sensor type
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

            SensorType sensorType = sensorTypes[i];
            gene.setInputSensorType(sensorType);
        }

        // then outputs
        for (int i = 0; i < outputCount; i++) {
            NEATNeuronGene gene = new MMNEATNeuronGene(NEATNeuronType.Output, af,
                    i + inputCount + 1, innovationID++);
            neurons.add(gene);
        }

        initNeuronLists();

        // and now links
        List<NEATLinkGene> links = getLinksChromosome();
        boolean haveInputOutputLink = false;
        for (int i = 0; i < inputCount + 1; i++) {
            for (int j = 0; j < outputCount; j++) {
                if (rnd.nextDouble() < connectionDensity) {
                    long fromID = neurons.get(i).getId();
                    long toID = neurons.get(inputCount + j + 1).getId();
                    double w = RangeRandomizer.randomize(rnd, -pop.getWeightRange(),
                            pop.getWeightRange());
                    NEATLinkGene gene = new NEATLinkGene(fromID, toID, true, innovationID++, w);
                    links.add(gene);

                    if (i != 0) { // if not bias node
                        haveInputOutputLink = true;
                    }
                }
            }
        }

        // make sure we have at least one connection between inputs and outputs
        if (!haveInputOutputLink) {
            // choose a random input/output pair
            int inputIndex = (int) (rnd.nextDouble() * inputCount) + 1;
            int outputIndex = (int) (rnd.nextDouble() * outputCount) + inputCount + 1;
            long fromID = neurons.get(inputIndex).getId();
            long toID = neurons.get(outputIndex).getId();
            double w = RangeRandomizer.randomize(rnd, -pop.getWeightRange(), pop.getWeightRange());
            NEATLinkGene gene = new NEATLinkGene(fromID, toID, true, innovationID, w);
            links.add(gene);
        }
    }

    public MMNEATGenome() {

    }

    private void initNeuronLists() {
        final List<NEATNeuronGene> neurons = getNeuronsChromosome();
        for (NEATNeuronGene neuron : neurons) {
            MMNEATNeuronGene neuronGene = (MMNEATNeuronGene) neuron;
            switch (neuron.getNeuronType()) {
                case Input:
                    inputsList.add(neuronGene);
                    break;
                case Output:
                    outputsList.add(neuronGene);
                    break;
                case Hidden:
                    hiddenList.add(neuronGene);
                    break;
                case Bias:
                    biasGene = neuronGene;
                    break;
                default:
                    break;
            }
        }
    }

    public List<MMNEATNeuronGene> getInputNeuronsChromosome() {
        return inputsList;
    }

    public void addInputNeuron(MMNEATNeuronGene inputNeuron) {
        if (inputNeuron.getNeuronType() != NEATNeuronType.Input) {
            throw new IllegalArgumentException("Not an input neuron");
        }

        getNeuronsChromosome().add(getInputCount() + 1, inputNeuron);

        getInputNeuronsChromosome().add(inputNeuron);
    }

    @Override
    public int getInputCount() {
        return inputsList.size();
    }

    public MMNEATNeuronGene getBiasGene() {
        return biasGene;
    }

    public List<MMNEATNeuronGene> getOutputNeuronsChromosome() {
        return outputsList;
    }

    public List<MMNEATNeuronGene> getHiddenNeuronsChromosome() {
        return hiddenList;
    }
}
