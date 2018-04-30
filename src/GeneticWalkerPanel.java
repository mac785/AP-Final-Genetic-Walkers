import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GeneticWalkerPanel extends JPanel implements GeneticWalkersConstants, MouseListener
{
	private GW_Cell[][] theGrid;
	private String[] walkerCommands;
	private int startR, startC, endR, endC;
	private double [] walkerScores;
	private int generationCount;
	private GeneticWalkerFrame myWindow;
	
	public GeneticWalkerPanel(GeneticWalkerFrame window)
	{
		super();
		myWindow = window;
		theGrid = new GW_Cell[NUM_ROWS][NUM_COLS];
		for (int r =0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
				theGrid[r][c] = new GW_Cell(r,c);
		walkerCommands = new String[NUM_WALKERS];
		walkerScores = new double[NUM_WALKERS];
		
		updateStart(NUM_ROWS/2,NUM_COLS/2);
		updateEnd(NUM_ROWS/4,NUM_COLS/4);
		
		addMouseListener(this);
		generationCount = 0;
	}
	/**
	 * change the value of the startR and startC variables, and update the grid to reflect this.
	 * @param r
	 * @param c
	 */
	public void updateStart(int r, int c)
	{
		theGrid[startR][startC].setStartLoc(false);
		startR =r;
		startC =c;
		theGrid[startR][startC].setStartLoc(true);
	}
	/**
	 * change the value of the endR and endC variables, and update the grid to reflect this.
	 * @param r
	 * @param c
	 */
	public void updateEnd(int r, int c)
	{
		theGrid[endR][endC].setFinishLoc(false);
		endR =r;
		endC =c;
		theGrid[endR][endC].setFinishLoc(true);
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		for (int r =0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
				theGrid[r][c].drawSelf(g);
	}
	/**
	 * select NUM_STEPS random numbers from 0-3, inclusive, for each of the NUM_WALKERS walkers.
	 */
	public void randomizeWalkers()
	{
		for (int w=0; w<NUM_WALKERS; w++)
		{
			walkerCommands[w] = "";
			for (int i=0; i<NUM_STEPS; i++)
				walkerCommands[w]+=(int)(Math.random()*4);
		}
		myWindow.updateLabels(walkerCommands);
	}
	/**
	 * resets the appearance of the grid.
	 */
	public void clearAllDots()
	{
		for (int r =0; r<NUM_ROWS; r++)
			for (int c=0; c<NUM_COLS; c++)
				theGrid[r][c].clearDots();
		repaint();
	}
	//====================================== Button Command Handlers ===============================
	public void doResetWalkers()
	{
		clearAllDots();
		randomizeWalkers();
		generationCount = 0;
	}
	// user just pressed the "Run N Generations" button.
	public void doNGenerations(int N)
	{
		for (int i=0; i<N; i++)
			doGeneration();
	}
	/**
	 * finds the trail for each of the Walkers, scores them, and performs genetic algorithm to
	 * prepare for next cycle.
	 */
	public void doGeneration()
	{
		clearAllDots();
		parseWalkerStrings();
		doDarwin();
		generationCount++;
		repaint();
		
	}
	/**
	 * for each walker, starts at the start location and follows the commands in theWalker to follow
	 * it's path through the grid, then scores the result. If a walker encounters the end point, it
	 * finishes early.
	 */
	public void parseWalkerStrings()
	{
		for (int w=0; w<NUM_WALKERS; w++)
		{
			int [] walkerBumps = new int[NUM_WALKERS];
			int r=startR;
			int c=startC;
			theGrid[r][c].displayDotN(w);
			int steps = 0; // Note: "steps" will still be in scope after the loop is over.
			for (steps=0; steps<NUM_STEPS; steps++)
			{
				int bearing = Integer.parseInt(walkerCommands[w].substring(steps, steps+1));
				switch (bearing)
				{
				case NORTH:
					if (r-1>-1 && !theGrid[r-1][c].isBlocked())
						r -=1;
					else{
						walkerBumps[w]++;
					}
					break;// breaks out of "switch," not "for."
				case EAST:
					if (c+1<NUM_COLS && !theGrid[r][c+1].isBlocked())
						c+=1;
					else{
						walkerBumps[w]++;
					}
					break;// breaks out of "switch," not "for."
				case SOUTH: 
					if (r+1<NUM_ROWS && !theGrid[r+1][c].isBlocked())
						r+=1;
					else{
						walkerBumps[w]++;
					}
					break;// breaks out of "switch," not "for."
				case WEST:
					if (c-1>-1 && !theGrid[r][c-1].isBlocked())
						c-=1;
					else{
						walkerBumps[w]++;
					}
					break;// breaks out of "switch," not "for."
				}
				// if this is one of the first four walkers (from the most successful parents),
				// display it on the grid as dots.
				if (w<4)
					theGrid[r][c].displayDotN(w);
				// if we are at the endpoint, exit the loop early.
				if (r==endR && c == endC)
					break; // breaks out of "for."
			}
			
			
			
			// Score this run. We want the LOWEST score possible.
			// if we didn't reach the goal, then our score is a positive distance from where
			//     we finished to where the goal is.
			// However, if we did reach the goal (and left the loop early), the result is the 
			//     negative of the number of remaining steps we had when we exited the loop.
			//TODO: insert your code here.
			if(r==endR && c==endC){
				walkerScores[w] = Math.abs(steps)*-1+walkerBumps[w];
			}
			else{
				walkerScores[w] = Math.sqrt(Math.pow(r-endR, 2)+Math.pow(c-endC, 2))+walkerBumps[w];
			}
			
			//----------------------------------
		}
		myWindow.updateLabels(walkerCommands);
	}
	/**
	 * examines the scores for all the walkers and returns a list of the walker numbers, ranked by best -> worst.
	 * For example, suppose scores is [1.0, 2.0, 1.414, 4.0, 2.828, -2]... this would suggest that the rank array
	 * will return [5, 0, 2, 1, 4, 3]. That would mean that walker #5 was best, #0 was second best,
	 * etc... and #3 was the worst.
	 * @return - a list of walker numbers, ranked by scoring.
	 */
	public int[] rankWalkers()
	{
		// making a copy of the scores that we can mess with.
		ArrayList<Double> scoresCopy = new ArrayList<Double>();
		for (int i=0; i<NUM_WALKERS;i++)
			scoresCopy.add(walkerScores[i]);
		
		int[] ranks = new int[NUM_WALKERS];
		// my suggestion. find the lowest value in scorescopy, and put the number of which walker
		// had that number into the ranks list. Then, in scorescopy replace the best score with a 
		// ridiculously high number, so now the (previously) second-best number is the best.
		// TODO: insert your code here.
		int counter = 0;
		while(counter<NUM_WALKERS){
			int best = 0;
			for(int i=0;i<scoresCopy.size();i++){
				if(scoresCopy.get(i)<scoresCopy.get(best)){
					best = i;
				}
			}
			ranks[counter] = best;
			scoresCopy.set(best, 100000000.);
			counter++;
		}
		//---------------------------------
		return ranks;
	}
	
	/**
	 * Based on the rankings of the walkers and the list of pairings in GENETIC_PAIRINGS, "mate" 
	 * various walkers with each other - they will each produce "fraternal twins," which you should
	 * add to the newWalkerList and then update the list of walkers on the screen.
	 */
	public void doDarwin()
	{
		int[] ranks = rankWalkers();
		String[] newWalkerList = new String[NUM_WALKERS];
		// TODO: insert your code here.
		for(int i=0;i<NUM_WALKERS/2;i++){
			String temp[] = haveSex(ranks[GENETIC_PAIRINGS[i][0]],ranks[GENETIC_PAIRINGS[i][1]]);
			newWalkerList[i*2] = temp[0];
			newWalkerList[i*2+1] = temp[1];
		}
		if ((int)(Math.random()*50)==7){
			String temp1 = "";
			for (int i=0; i<NUM_STEPS; i++)
				temp1+=(int)(Math.random()*4);
			newWalkerList[(int)(Math.random()*NUM_WALKERS)] = temp1;
		}
		//-------------------------------------
		myWindow.updateGeneration(generationCount);
//		System.out.println("---------------------------\nGeneration: "+generationCount+"\n------------------------------");
//		for (int w=0; w<NUM_WALKERS; w++)
//			System.out.println(newWalkerList[ranks[w]]+"\t"+walkerScores[ranks[w]]);
		// -------------------------------------
		walkerCommands = newWalkerList; // now make this new list the starting point for the next generation.
	}
	
	/**
	 * take the commands for two walkers, randomly select a point in the middle and swap the second parts
	 * of the command lists to create two new command lists. 
	 * Here's an example, but I'm going to put it in terms of letters, not numbers: 
	 *   If you have commands    [ABCDEFG] and [abcdefg], 
	 *   you might wind up with  [ABCdefg] and [abcDEFG]. 
	 * 
	 * Additionally, if a 
	 * random 0-1 number is less than MUTATION_RATE, randomly change one command number in one child 
	 * with a random value 0-3, inclusive. (So we might, in fact get [abcDEhG].)
	 * 
	 * Here's an example with numbers:  [0101010101] and [2323232323] might make:
	 *                                  [0101012323] and [2023230101]
	 * (Note: the second child has a mutation in the second position.)
	 * 
	 * (Note 2: The extension to this activity has more you might do here.
	 * 
	 * @param consentingAdult1 - index of the first parent in the walkerCommands list
	 * @param consentingAdult2 - index of the second parent in the walkerCommands list
	 * @return an array with two commands.
	 */
	public String[] haveSex(int consentingAdult1, int consentingAdult2)
	{
		String[] result = new String[2];
		//TODO: insert your code here.
		String temp1 = walkerCommands[consentingAdult1];
		String temp2 = walkerCommands[consentingAdult2];
		int splitPoint = (int)(Math.random()*(NUM_STEPS-2)+1);
		
		result[0] = temp1.substring(0,splitPoint)+temp2.substring(splitPoint);
		result[1] = temp2.substring(0,splitPoint)+temp1.substring(splitPoint);
		
//		Normal Mutation Thingamajig V1
		
		if(Math.random()<MUTATION_RATE){
			String tempMutate = result[1];
			int var = (int)(Math.random()*NUM_STEPS);
			int vartwo = (int)(Math.random()*3);
			String tempOne = tempMutate.substring(0, var);
			String tempTwo = Integer.toString(vartwo);
			String tempThree = tempMutate.substring(var+1);
			String tempLast = "";
			tempLast += tempOne;
			tempLast += tempTwo;
			tempLast += tempThree;
			result[1] = tempLast;
		}

//		My Weird Mutation Thingamajig v2
		
		if(Math.random()<MUTATION_RATE){
			String tempString = result[0];
			int var1 = (int)(Math.random()*(NUM_STEPS-5))+1;
			int var2 = var1+5;
			
			String tempFirst = tempString.substring(0, var1);
			String tempSecond = tempString.substring(var1+1,var2);
			String tempThird = tempString.substring(var1,var1+1);
			String tempFourth = tempString.substring(var2);
			
			String finalString = "";
			finalString += tempFirst;
			finalString += tempSecond;
			finalString += tempThird;
			finalString += tempFourth;
			
//			System.out.println("Original");
//			System.out.println(result[0]);
//			System.out.println("Changed");
//			System.out.println(finalString);
			result[0] = finalString;
		}
		
		
		
//		My Weird Mutation Thingamajig v1
		
//		if((int)Math.random()*20==19){
//			String temp = result[0];
//			int var1 = (int)Math.random()*4;
//			int var2 = (int)Math.random()*4;
//			while(var1 == var2){
//				var2 = (int)Math.random()*4;
//			}
//			temp.replaceAll(Integer.toString(var1), Integer.toString(var2));
//			result[0] = temp;
//		}
		//-------------------------
		return result;
	
	}
	// ==================================  MOUSE RESPONSE METHODS ===========================
	/**
	 * turns on or off the block at the location that the user clicked.
	 * @param mouseX
	 * @param mouseY
	 */
	public void toggleBlockAt(int mouseX, int mouseY)
	{
		int col = (mouseX-LEFT_MARGIN)/CELL_SIZE;
		int row = (mouseY-TOP_MARGIN)/CELL_SIZE;
		
		if (row>-1 && row<NUM_ROWS && col>-1 && col < NUM_COLS)
			if (theGrid[row][col].isBlocked())
				theGrid[row][col].setBlocked(false);
			else
				theGrid[row][col].setBlocked(true);
		repaint();
		
	
	}
	/**
	 * changes the location of the starting square to where the user clicked.
	 * @param mouseX
	 * @param mouseY
	 */
	public void setStartLoc(int mouseX, int mouseY)
	{
		int col = (mouseX-LEFT_MARGIN)/CELL_SIZE;
		int row = (mouseY-TOP_MARGIN)/CELL_SIZE;
		if (row>-1 && row<NUM_ROWS && col>-1 && col < NUM_COLS)
		{
			updateStart(row,col);
		}
		repaint();
	}

	/**
	 * changes the location of the ending square to where the user clicked.
	 * @param mouseX
	 * @param mouseY
	 */
	public void setEndLoc(int mouseX, int mouseY)
	{
		int col = (mouseX-LEFT_MARGIN)/CELL_SIZE;
		int row = (mouseY-TOP_MARGIN)/CELL_SIZE;
		if (row>-1 && row<NUM_ROWS && col>-1 && col < NUM_COLS)
		{
			updateEnd(row,col);
		}
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (myWindow.isInPlayMode())
			return;
		int currentMode = myWindow.whichEditSubmode();
		if (currentMode == TOGGLE_BLOCK_MODE)
			toggleBlockAt(e.getX(),e.getY());
		if (currentMode == SET_START_MODE)
			setStartLoc(e.getX(),e.getY());
		if (currentMode == SET_END_MODE)
			setEndLoc(e.getX(),e.getY());
	}

	
	// ----------- required mouselistener events we aren't using.....
	@Override
	public void mouseClicked(MouseEvent e)
	{	
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
	}
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}
	
}
