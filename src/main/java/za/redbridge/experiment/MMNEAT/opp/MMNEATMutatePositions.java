package za.redbridge.experiment.MMNEAT.opp;

import org.encog.ml.ea.genome.Genome;
import org.encog.neural.neat.training.opp.NEATMutation;

import java.util.List;
import java.util.Random;

import za.redbridge.experiment.MMNEAT.MMNEATGenome;
import za.redbridge.experiment.MMNEAT.MMNEATNeuronGene;
import za.redbridge.experiment.MMNEAT.MMNEATPopulation;
import za.redbridge.experiment.MMNEAT.opp.sensors.MutateSensorPosition;
import za.redbridge.experiment.MMNEAT.opp.sensors.SelectSensors;

/**
 * Created by jamie on 2014/09/08.
 */
public class MMNEATMutatePositions extends NEATMutation {

    private final SelectSensors sensorSelection;

    private final MutateSensorPosition positionMutation;

    /**
     * Construct a position mutation operator
     * @param sensorSelection The method used to choose the sensors for mutation.
     * @param positionMutation The method used to actually mutate the positions.
     */
    public MMNEATMutatePositions(SelectSensors sensorSelection,
            MutateSensorPosition positionMutation) {
        this.sensorSelection = sensorSelection;
        this.positionMutation = positionMutation;
    }

    public SelectSensors getSensorSelection() {
        return sensorSelection;
    }

    public MutateSensorPosition getPositionMutation() {
        return positionMutation;
    }

    @Override
    public void performOperation(Random rnd, Genome[] parents, int parentIndex, Genome[] offspring,
            int offspringIndex) {
        MMNEATGenome target =
                (MMNEATGenome) obtainGenome(parents, parentIndex, offspring, offspringIndex);

        double sensorBearingRange =
                ((MMNEATPopulation)getOwner().getPopulation()).getSensorBearingRange();

        double sensorOrientationRange =
                ((MMNEATPopulation)getOwner().getPopulation()).getSensorOrientationRange();

        List<MMNEATNeuronGene> sensors = sensorSelection.selectLinks(rnd, target);
        for (MMNEATNeuronGene sensor : sensors) {
            positionMutation
                    .mutatePosition(rnd, sensor, sensorBearingRange, sensorOrientationRange);
        }
    }
}
