package za.redbridge.experiment.MMNEAT;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.neat.NEATLink;
import org.encog.neural.neat.NEATNetwork;

import java.util.List;

/**
 * Created by jamie on 2014/09/08.
 */
public class MMNEATNetwork extends NEATNetwork {

    private static final long serialVersionUID = -2277991937281560066L;

    /**
     * Construct a NEAT network. The links that are passed in also define the
     * neurons.
     *
     * @param inputNeuronCount       The input neuron count.
     * @param outputNeuronCount      The output neuron count.
     * @param connectionArray        The links.
     * @param theActivationFunctions
     */
    public MMNEATNetwork(int inputNeuronCount, int outputNeuronCount, List<NEATLink> connectionArray,
            ActivationFunction[] theActivationFunctions) {
        super(inputNeuronCount, outputNeuronCount, connectionArray, theActivationFunctions);
    }
}
