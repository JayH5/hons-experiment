package za.redbridge.experiment.MMNEAT.training.species;

import org.encog.ml.ea.genome.Genome;

import java.util.List;

import za.redbridge.experiment.MMNEAT.MMNEATPopulation;
import za.redbridge.experiment.MMNEAT.training.MMNEATGenome;
import za.redbridge.experiment.MMNEAT.training.MMNEATNeuronGene;
import za.redbridge.experiment.NEAT.training.species.NEATSpeciation;

/**
 * Adds an additional term to the NEAT speciation that accounts for sensor position.
 *
 * Created by jamie on 2014/10/21.
 */
public class MMNEATSpeciation extends NEATSpeciation {

    private static final long serialVersionUID = -505824828539787086L;

    private double constBearing = 0.4;

    private double constOrientation = 0.1;

    @Override
    public double getCompatibilityScore(Genome gen1, Genome gen2) {
        double score = super.getCompatibilityScore(gen1, gen2);

        int numMatched = 0;

        double bearingDifference = 0;
        double orientationDifference = 0;

        MMNEATGenome genome1 = (MMNEATGenome) gen1;
        MMNEATGenome genome2 = (MMNEATGenome) gen2;

        MMNEATPopulation population = (MMNEATPopulation) gen1.getPopulation();
        double bearingRange = population.getSensorBearingRange();
        double orientationRange = population.getSensorOrientationRange();

        List<MMNEATNeuronGene> genome1Inputs = genome1.getInputNeuronsChromosome();
        List<MMNEATNeuronGene> genome2Inputs = genome2.getInputNeuronsChromosome();

        int genome1InputCount = genome1Inputs.size();
        int genome2InputCount = genome2Inputs.size();

        int g1 = 0;
        int g2 = 0;

        while (g1 < genome1InputCount && g2 < genome2InputCount) {
            MMNEATNeuronGene genome1Input = genome1Inputs.get(g1);
            MMNEATNeuronGene genome2Input = genome2Inputs.get(g2);

            // get neuron id for each gene at this point
            long id1 = genome1Input.getId();
            long id2 = genome2Input.getId();

            if (id1 == id2) {
                double bearing1 = genome1Input.getInputSensorBearing() / bearingRange;
                double bearing2 = genome2Input.getInputSensorBearing() / bearingRange;
                bearingDifference += Math.abs(bearing2 - bearing1);

                double orientation1 = genome1Input.getInputSensorOrientation() / orientationRange;
                double orientation2 = genome2Input.getInputSensorOrientation() / orientationRange;
                orientationDifference += Math.abs(orientation2 - orientation1);

                g1++;
                g2++;
                numMatched++;
            } else if (id1 < id2) {
                g1++;
            } else { // if (id1 > id2)
                g2++;
            }
        }

        score += constBearing * (bearingDifference / numMatched)
                + constOrientation * (orientationDifference / numMatched);

        return score;
    }

    public double getConstBearing() {
        return constBearing;
    }

    public void setConstBearing(double constBearing) {
        this.constBearing = constBearing;
    }

    public double getConstOrientation() {
        return constOrientation;
    }

    public void setConstOrientation(double constOrientation) {
        this.constOrientation = constOrientation;
    }
}
