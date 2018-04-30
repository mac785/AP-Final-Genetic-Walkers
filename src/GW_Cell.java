import java.awt.Color;
import java.awt.Graphics;

public class GW_Cell implements GeneticWalkersConstants
{
	private int r,c;
	private boolean isBlocked;
	private boolean isFinishLoc;
	private boolean isStartLoc;
	private int whichDotsToDisplay; // four bits...

	
	public GW_Cell(int inR, int inC)
	{
		r = inR;
		c = inC;
		isBlocked = false;
		isFinishLoc = false;
		isStartLoc = false;
		whichDotsToDisplay = 0;
	}


	public boolean isBlocked()
	{
		return isBlocked;
	}


	public void setBlocked(boolean isBlocked)
	{
		this.isBlocked = isBlocked;
	}


	public boolean isFinishLoc()
	{
		return isFinishLoc;
	}


	public void setFinishLoc(boolean isFinishLoc)
	{
		this.isFinishLoc = isFinishLoc;
	}
	
	
	public boolean isStartLoc()
	{
		return isStartLoc;
	}


	public void setStartLoc(boolean isStartLoc)
	{
		this.isStartLoc = isStartLoc;
	}


	public void clearDots()
	{
		whichDotsToDisplay = 0;
	}
	
	public void displayDotN(int N)
	{
		whichDotsToDisplay = whichDotsToDisplay | (1<<N);
	}
	
	public void clearDotN(int N)
	{
		whichDotsToDisplay = whichDotsToDisplay & (15-(1<<N));
	}
	
	public boolean dotNIsDisplayed(int N)
	{
		return (whichDotsToDisplay & (1<<N))>0;
	}
	
	public void drawSelf(Graphics g)
	{
		if (isBlocked)
			g.setColor(Color.darkGray);
		else
			g.setColor(Color.lightGray);
		g.fillRect(LEFT_MARGIN+CELL_SIZE*c, TOP_MARGIN+CELL_SIZE*r, CELL_SIZE, CELL_SIZE);
		g.setColor(Color.BLACK);
		g.drawRect(LEFT_MARGIN+CELL_SIZE*c, TOP_MARGIN+CELL_SIZE*r, CELL_SIZE, CELL_SIZE);
		
		// drawing dots. I am using some slick binary tricks.
		for (int i=0; i<4; i++)
		{
			if (dotNIsDisplayed(i))
			{
				g.setColor(DOT_COLORS[i]);
				g.fillOval(LEFT_MARGIN+CELL_SIZE*c+CELL_SIZE/2*(i&1),
						   TOP_MARGIN+CELL_SIZE*r+CELL_SIZE/2*(i&2)/2, 
						   CELL_SIZE/2, CELL_SIZE/2);
			}
		}
		
		if (isStartLoc)
		{
			g.setColor(new Color(255,128,0));
			g.fillRect(LEFT_MARGIN+CELL_SIZE*c+CELL_SIZE/4, 
					   TOP_MARGIN+CELL_SIZE*r+CELL_SIZE/4,
					   CELL_SIZE/2, CELL_SIZE/2);
		}
		if (isFinishLoc)
		{
			g.setColor(Color.GREEN);
			g.fillRect(LEFT_MARGIN+CELL_SIZE*c+CELL_SIZE/4, 
					   TOP_MARGIN+CELL_SIZE*r+CELL_SIZE/4,
					   CELL_SIZE/2, CELL_SIZE/2);
		}
	}
	
	
}
