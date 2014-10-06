package za.redbridge.experiment;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

import sim.display.Console;
import za.redbridge.experiment.MMNEAT.MMNEATNetwork;
import za.redbridge.experiment.MMNEAT.SensorMorphology;
import za.redbridge.simulator.Simulation;
import za.redbridge.simulator.SimulationGUI;
import za.redbridge.simulator.config.SimConfig;
import za.redbridge.simulator.factories.HomogeneousRobotFactory;
import za.redbridge.simulator.factories.RobotFactory;
import za.redbridge.simulator.phenotype.Phenotype;

/**
 * Test runner for the simulation.
 * 
 * Created by jamie on 2014/09/09.
 */
public class ScoreCalculator implements CalculateScore {

    private static final Logger log = LoggerFactory.getLogger(ScoreCalculator.class);

    private final SimConfig simConfig;
    private final int simulationRuns;
    private final SensorMorphology sensorMorphology;

    private final DescriptiveStatistics performanceStats = new SynchronizedDescriptiveStatistics();
    private final DescriptiveStatistics scoreStats = new SynchronizedDescriptiveStatistics();
    private final DescriptiveStatistics sensorStats;

    public ScoreCalculator(SimConfig simConfig, int simulationRuns,
            SensorMorphology sensorMorphology) {
        this.simConfig = simConfig;
        this.simulationRuns = simulationRuns;
        this.sensorMorphology = sensorMorphology;

        // If fixed morphology then don't record sensor stats
        this.sensorStats = isEvolvingMorphology() ? new SynchronizedDescriptiveStatistics() : null;
    }

    @Override
    public double calculateScore(MLMethod method) {
        Instant start = Instant.now();

        NEATNetwork network = (NEATNetwork) method;
        RobotFactory robotFactory = new HomogeneousRobotFactory(getPhenotypeForNetwork(network),
                simConfig.getRobotMass(), simConfig.getRobotRadius(), simConfig.getRobotColour(),
                simConfig.getObjectsRobots());

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory);
        simulation.setStopOnceCollected(true);
        double fitness = 0;
        for (int i = 0; i < simulationRuns; i++) {
            simulation.run();
            fitness += simulation.getFitness();
        }

        // Get the fitness and update the total score
        double score = fitness / simulationRuns;
        scoreStats.addValue(score);

        if (isEvolvingMorphology()) {
            sensorStats.addValue(network.getInputCount());
        }

        log.debug("Score calculation completed: " + score);

        Duration duration = Duration.between(start, Instant.now());
        performanceStats.addValue(duration.toMillis());

        return score;
    }

    public void demo(MLMethod method) {
        // Create the robot and resource factories
        NEATNetwork network = (NEATNetwork) method;
        RobotFactory robotFactory = new HomogeneousRobotFactory(getPhenotypeForNetwork(network),
                simConfig.getRobotMass(), simConfig.getRobotRadius(), simConfig.getRobotColour(),
                simConfig.getObjectsRobots());

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory);

        SimulationGUI video = new SimulationGUI(simulation);

        //new console which displays this simulation
        Console console = new Console(video);
        console.setVisible(true);
    }

    private Phenotype getPhenotypeForNetwork(NEATNetwork network) {
        if (isEvolvingMorphology()) {
            return new MMNEATPhenotype((MMNEATNetwork) network);
        } else {
            return new NEATPhenotype(network, sensorMorphology);
        }
    }

    public boolean isEvolvingMorphology() {
        return sensorMorphology == null;
    }

    public DescriptiveStatistics getPerformanceStatistics() {
        return performanceStats;
    }

    public DescriptiveStatistics getScoreStatistics() {
        return scoreStats;
    }

    public DescriptiveStatistics getSensorStatistics() {
        return sensorStats;
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
