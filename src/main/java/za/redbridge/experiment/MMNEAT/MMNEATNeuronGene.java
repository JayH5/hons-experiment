package za.redbridge.experiment.MMNEAT;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.neat.NEATNeuronType;
import org.encog.neural.neat.training.NEATNeuronGene;

/**
 * Created by jamie on 2014/09/08.
 */
public class MMNEATNeuronGene extends NEATNeuronGene {

    private static final long serialVersionUID = 1583781843029771944L;

    private double inputSensorBearing;
    private double inputSensorOrientation;

    public MMNEATNeuronGene(NEATNeuronType type, ActivationFunction theActivationFunction, long id,
            long innovationID) {
        setNeuronType(type);
        setInnovationId(innovationID);
        setId(id);
        setActivationFunction(theActivationFunction);
    }

    /**
     * Construct this gene by comping another.
     * @param other The other gene to copy.
     */
    public MMNEATNeuronGene(MMNEATNeuronGene other) {
        copy(other);
    }

    /**
     * Copy another gene to this one.
     *
     * @param other
     *            The other gene.
     */
    public void copy(MMNEATNeuronGene other) {
        setId(other.getId());
        setNeuronType(other.getNeuronType());
        setActivationFunction(other.getActivationFunction());
        setInnovationId(other.getInnovationId());

        if (getNeuronType() == NEATNeuronType.Input) {
            setInputSensorBearing(other.getInputSensorBearing());
            setInputSensorOrientation(other.getInputSensorOrientation());
        }
    }

    public double getInputSensorBearing() {
        checkInputNeuron();
        return inputSensorBearing;
    }

    public void setInputSensorBearing(double inputSensorBearing) {
        checkInputNeuron();
        this.inputSensorBearing = inputSensorBearing;
    }

    public double getInputSensorOrientation() {
        checkInputNeuron();
        return inputSensorOrientation;
    }

    public void setInputSensorOrientation(double inputSensorOrientation) {
        checkInputNeuron();
        this.inputSensorOrientation = inputSensorOrientation;
    }

    private void checkInputNeuron() {
        if (getNeuronType() != NEATNeuronType.Input) {
            throw new UnsupportedOperationException("Not an input neuron");
        }
    }
}
