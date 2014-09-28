package za.redbridge.experiment;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sim.display.Console;
import za.redbridge.experiment.MMNEAT.MMNEATNetwork;
import za.redbridge.simulator.Simulation;
import za.redbridge.simulator.SimulationGUI;
import za.redbridge.simulator.config.SimConfig;
import za.redbridge.simulator.factories.HomogeneousRobotFactory;
import za.redbridge.simulator.factories.RobotFactory;
import za.redbridge.simulator.phenotype.Phenotype;

/**
 * Created by jamie on 2014/09/09.
 */
public class ScoreCalculator implements CalculateScore {

    private static final double TIME_BONUS_MULTIPLIER = 2.0;

    private static final Logger log = LoggerFactory.getLogger(ScoreCalculator.class);

    private final SimConfig simConfig;
    private final int simulationRuns;

    private DescriptiveStatistics statistics = new SynchronizedDescriptiveStatistics();

    public ScoreCalculator(SimConfig simConfig, int simulationRuns) {
        this.simConfig = simConfig;
        this.simulationRuns = simulationRuns;
    }

    @Override
    public double calculateScore(MLMethod method) {
        RobotFactory robotFactory = new HomogeneousRobotFactory(getPhenotypeFromMethod(method),
                simConfig.getRobotMass(), simConfig.getRobotRadius(), simConfig.getRobotColour(),
                simConfig.getObjectsRobots());

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory);
        simulation.setStopOnceCollected(true);
        double fitness = 0;
        for (int i = 0; i < simulationRuns; i++) {
            simulation.run();
            fitness += simulation.getFitness();
            fitness += timeBonus(simulation.getStepNumber());
        }

        // Get the fitness and update the total score
        double score = fitness / simulationRuns;
        statistics.addValue(score);

        log.debug("Score calculation completed: " + score);

        return score;
    }

    private double timeBonus(long steps) {
        double reward = 0.0;
        if (steps < simConfig.getSimulationIterations()) {
            reward = 1.0 - (double) steps / simConfig.getSimulationIterations();
            reward *= TIME_BONUS_MULTIPLIER;

            log.debug("Time bonus awarded: " + reward + " (" + steps + " steps)");
        }
        return reward;
    }

    private static Phenotype getPhenotypeFromMethod(MLMethod method) {
        if (method instanceof MMNEATNetwork) {
            MMNEATNetwork network = (MMNEATNetwork) method;
            return new MMNEATPhenotype(network);
        } else if (method instanceof NEATNetwork) {
            NEATNetwork network = (NEATNetwork) method;
            return new NEATKheperaIIIPhenotype(network);
        } else {
            throw new IllegalArgumentException("Unknown MLMethod type");
        }
    }

    public void demo(MLMethod method) {
        // Create the robot and resource factories
        RobotFactory robotFactory = new HomogeneousRobotFactory(getPhenotypeFromMethod(method),
                simConfig.getRobotMass(), simConfig.getRobotRadius(), simConfig.getRobotColour(),
                simConfig.getObjectsRobots());

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory);

        SimulationGUI video = new SimulationGUI(simulation);

        //new console which displays this simulation
        Console console = new Console(video);
        console.setVisible(true);
    }

    public DescriptiveStatistics getStatistics() {
        return statistics;
    }

    @Override
    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return false;
    }

}
