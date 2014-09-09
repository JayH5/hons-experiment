package za.redbridge.experiment;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

import org.encog.Encog;
import org.encog.ml.ea.train.EvolutionaryAlgorithm;

import za.redbridge.experiment.MMNEAT.MMNEATCODEC;
import za.redbridge.experiment.MMNEAT.MMNEATPopulation;
import za.redbridge.experiment.MMNEAT.MMNEATUtil;
import za.redbridge.simulator.config.SimConfig;

/**
 * Created by jamie on 2014/09/09.
 */
public class Main {

    public static void main(String[] args) {
        Args options = new Args();
        new JCommander(options, args);

        SimConfig simConfig;
        if (options.configFile != null && !options.configFile.isEmpty()) {
            simConfig = SimConfig.loadFromFile(options.configFile);
        } else {
            simConfig = new SimConfig();
        }

        MMNEATPopulation population = new MMNEATPopulation(2, options.numSensors, 1000);
        population.reset();

        ScoreCalculator calculateScore = new ScoreCalculator(simConfig);
        calculateScore.calculateScore(new MMNEATCODEC().decode(population.getBestGenome()));

        /*EvolutionaryAlgorithm train = MMNEATUtil.constructNEATTrainer(population, calculateScore);

        for (int i = 0; i < options.numIterations; i++) {
            train.iteration();
            System.out.println("Epoch #" + train.getIteration() + " Score: "
                    + calculateScore.getLastScore());
        }

        System.out.println("Training complete");
        Encog.getInstance().shutdown();*/
    }

    private static class Args {
        @Parameter(names = "-c", description = "Simulation config file to load")
        private String configFile = null;

        @Parameter(names = "-i", description = "Number of simulation iterations to train for")
        private int numIterations = 500;

        @Parameter(names = "-s", description = "Number of sensors")
        private int numSensors = 4;

        @Parameter(names = "--ui", description = "Display GUI")
        private boolean ui = false;
    }
}
