package za.redbridge.experiment;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;
import org.encog.neural.neat.NEATNetwork;

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

    private final SimConfig simConfig;
    private final int simulationRuns;

    private final Object statsLock = new Object();
    private double epochTotalScore;
    private double epochBestScore;
    private int numSimulationsRun;

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
        for (int i = 0; i < simulationRuns; i++) {
            simulation.run();
            incrementSimulationsRun();
        }

        // Get the fitness and update the total score
        double score = simulation.getFitness() / simulationRuns;
        recordEpochScore(score);

        return score;
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

    private void recordEpochScore(double score) {
        synchronized (statsLock) {
            epochTotalScore += score;
            if (score > epochBestScore) {
                epochBestScore = score;
            }
        }
    }

    private void incrementSimulationsRun() {
        synchronized (statsLock) {
            numSimulationsRun++;
        }
    }

    public double getEpochAverageScore() {
        synchronized (statsLock) {
            return epochTotalScore / numSimulationsRun;
        }
    }

    public double getEpochBestScore() {
        synchronized (statsLock) {
            return epochBestScore;
        }
    }

    public void resetScoreCounters() {
        synchronized (statsLock) {
            epochTotalScore = 0;
            epochBestScore = 0;
            numSimulationsRun = 0;
        }
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
