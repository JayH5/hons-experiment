package za.redbridge.experiment;

import org.encog.ml.CalculateScore;
import org.encog.ml.MLMethod;

import java.awt.Color;
import java.awt.Paint;

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

    private static final double DEFAULT_ROBOT_MASS = 0.7;
    private static final double DEFAULT_ROBOT_RADIUS = 0.15;
    private static final Paint DEFAULT_ROBOT_PAINT = Color.BLACK;

    private final SimConfig simConfig;

    private double lastScore;

    public ScoreCalculator(SimConfig simConfig) {
        this.simConfig = simConfig;
    }

    @Override
    public double calculateScore(MLMethod method) {
        MMNEATNetwork network = (MMNEATNetwork) method;

        // Create the robot and resource factories
        RobotFactory robotFactory = new HomogeneousRobotFactory(new MMNEATPhenotype(network),
                DEFAULT_ROBOT_MASS, DEFAULT_ROBOT_RADIUS, DEFAULT_ROBOT_PAINT);

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory, 15);
        simulation.run();

        // Get the fitness
        lastScore = simulation.getFitness();
        return lastScore;
    }

    public void demo(MMNEATNetwork network) {
        // Create the robot and resource factories
        RobotFactory robotFactory = new HomogeneousRobotFactory(new MMNEATPhenotype(network),
                DEFAULT_ROBOT_MASS, DEFAULT_ROBOT_RADIUS, DEFAULT_ROBOT_PAINT);

        // Create the simulation and run it
        Simulation simulation = new Simulation(simConfig, robotFactory, 15);

        SimulationGUI video = new SimulationGUI(simulation);

        //new console which displays this simulation
        Console console = new Console(video);
        console.setVisible(true);
    }

    @Override
    public boolean shouldMinimize() {
        return false;
    }

    @Override
    public boolean requireSingleThreaded() {
        return false;
    }

    public double getLastScore() {
        return lastScore;
    }
}
