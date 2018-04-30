
import java.awt.Color;

public interface GeneticWalkersConstants
{
	public static final int CELL_SIZE = 20;
	public static final int NUM_ROWS = 30;
	public static final int NUM_COLS = 30;
	public static final int LEFT_MARGIN = 20;
	public static final int TOP_MARGIN = 20;
	public static final Color[] DOT_COLORS = {Color.RED, Color.BLUE, Color.YELLOW, Color.CYAN};
	
	public static final int NUM_STEPS = 40;
	public static final int NUM_WALKERS = 20; // make this no less than 4.
	
	public static final int[][] GENETIC_PAIRINGS = {{0,1},{0,2},{3,4},{1,5},{1,10},{8,9},{4,7},{6,8},{9,3},{2,NUM_WALKERS-1}}; // this should have exactly half as many pairs as there are walkers.

	public static final int TOGGLE_BLOCK_MODE = 0;
	public static final int SET_START_MODE = 1;
	public static final int SET_END_MODE = 2;
	
	public static final double MUTATION_RATE = 0.25;
	public static final double TRANSCRIPTION_RATE = 0.05; // see Extensions.
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
} 
