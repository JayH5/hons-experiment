package za.redbridge.experiment;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;

import sim.display.Console;
import za.redbridge.experiment.MMNEAT.MMNEATNetwork;
import za.redbridge.simulator.Simulation;
import za.redbridge.simulator.SimulationGUI;
import za.redbridge.simulator.config.SimConfig;
import za.redbridge.simulator.factories.HomogeneousRobotFactory;
import za.redbridge.simulator.factories.RobotFactory;

/**
 * Created by jamie on 2014/09/09.
 */
public class ScoreCalculator implements CalculateScore {

    private final SimConfig simConfig;

    private final Object statsLock = new Object();
    private double epochTotalScore;

    public ScoreCalculator(SimConfig simConfig) {
        this.simConfig = simConfig;
    }

    @Override
    public double calculateScore(MLMethod method) {
        MMNEATNetwork network = (MMNEATNetwork) method;

        // Create the robot and resource factories
        RobotFactory robotFactory = new HomogeneousRobotFactory(new MMNEATPhenotype(network),
                simConfig.getRobotMass(), simConfig.getRobotRadius(), simConfig.getRobotColour(),
                simConfig.getObjectsRobots());

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory);
        simulation.run();

        // Get the fitness and update the total score
        double score = simulation.getFitness();
        incrementEpochScore(score);

        return score;
    }

    public void demo(MMNEATNetwork network) {
        // Create the robot and resource factories
        RobotFactory robotFactory = new HomogeneousRobotFactory(new MMNEATPhenotype(network),
                simConfig.getRobotMass(), simConfig.getRobotRadius(), simConfig.getRobotColour(),
                simConfig.getObjectsRobots());

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory);

        SimulationGUI video = new SimulationGUI(simulation);

        //new console which displays this simulation
        Console console = new Console(video);
        console.setVisible(true);
    }

    public void resetEpochScore() {
        synchronized (statsLock) {
            epochTotalScore = 0.0;
        }
    }

    private void incrementEpochScore(double amount) {
        synchronized (statsLock) {
            epochTotalScore += amount;
        }
    }

    public double getEpochTotalScore() {
        return epochTotalScore;
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
