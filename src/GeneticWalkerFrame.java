import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

public class GeneticWalkerFrame extends JFrame implements ActionListener, GeneticWalkersConstants
{
	private GeneticWalkerPanel mainPanel;
	private boolean inPlayMode;
	private JRadioButton blockRB, startRB, endRB;
	private JButton switchModeButton;
	private JButton runGenerationButton, resetButton;
	private JSpinner numGenerationsSpinner;
	private JLabel[] walkerLabels;
	private JLabel editModeLabel, playModeLabel;
	private JLabel generationLabel;
	
	public GeneticWalkerFrame()
	{
		super("Genetic Walkers");
		setSize(1000,800);
		setResizable(true);
		getContentPane().setLayout(new BorderLayout());
		mainPanel = new GeneticWalkerPanel(this);
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		getContentPane().add(buildSouthPanel(), BorderLayout.SOUTH);
		getContentPane().add(buildEastPanel(), BorderLayout.EAST);
		inPlayMode = false;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		mainPanel.randomizeWalkers();
	}
	
	public JPanel buildSouthPanel()
	{
		JPanel southPanel = new JPanel();
		
		southPanel.add(buildEditSubpanel());
		
		southPanel.add(buildSwitchSubpanel());
		
		southPanel.add(buildPlaySubpanel());
		
		return southPanel;
	}

	public JPanel buildEditSubpanel()
	{
		JPanel editPanel = new JPanel();
		editPanel.setBorder(new TitledBorder("Edit Challenge"));
		
		Box editLayout = Box.createVerticalBox();
		blockRB = new JRadioButton("Toggle Blocks");
		blockRB.setSelected(true);
		editLayout.add(blockRB);
		
		startRB = new JRadioButton("Move Start");
		editLayout.add(startRB);
		
		endRB = new JRadioButton("Move End");
		
		editLayout.add(endRB);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(blockRB);
		bg.add(startRB);
		bg.add(endRB);
		
		editPanel.add(editLayout);
		return editPanel;
	}
	
	public int whichEditSubmode()
	{
		if (blockRB.isSelected())
			return TOGGLE_BLOCK_MODE;
		if (startRB.isSelected())
			return SET_START_MODE;
		if (endRB.isSelected())
			return SET_END_MODE;
		return -1;
	}
	
	
	public Box buildSwitchSubpanel()
	{
		Box switchBox = Box.createVerticalBox();
		
		switchModeButton = new JButton("Switch Mode");
		switchModeButton.setAlignmentX(CENTER_ALIGNMENT);
		switchModeButton.addActionListener(this);
		switchBox.add(switchModeButton);
		
		editModeLabel = new JLabel("<-- Edit Mode");
		editModeLabel.setAlignmentX(CENTER_ALIGNMENT);
		switchBox.add(editModeLabel);
		
		playModeLabel = new JLabel("Play mode -->");
		playModeLabel.setAlignmentX(CENTER_ALIGNMENT);
		playModeLabel.setVisible(false);
		switchBox.add(playModeLabel);
		return switchBox;
	}
	
	public JPanel buildPlaySubpanel()
	{
		JPanel playPanel = new JPanel();
		playPanel.setBorder(new TitledBorder("Play"));
		
		
		resetButton = new JButton("Reset Walkers");
		resetButton.addActionListener(this);
		resetButton.setEnabled(false);
		playPanel.add(resetButton);
		
		runGenerationButton = new JButton("Run N Generations");
		runGenerationButton.addActionListener(this);
		runGenerationButton.setEnabled(false);
		playPanel.add(runGenerationButton);
		
		numGenerationsSpinner = new JSpinner(new SpinnerNumberModel(1,1,20,1));
		numGenerationsSpinner.setEnabled(false);
		playPanel.add(numGenerationsSpinner);
		
		generationLabel = new JLabel("Generation: -");
		playPanel.add(generationLabel);
		
		return playPanel;
	}
	
	public JPanel buildEastPanel()
	{
		
		JPanel walkerPanel = new JPanel();
		walkerPanel.setBackground(Color.lightGray);
		Box walkerLayout = Box.createVerticalBox();
		walkerLayout.add(Box.createVerticalStrut(10));
		walkerLabels = new JLabel[NUM_WALKERS];
		String defaultLabel ="";
		for (int i=0; i<NUM_STEPS; i++)
			defaultLabel+="-";
		
		for (int w=0; w<NUM_WALKERS; w++)
		{
			JLabel walkerLabel = new JLabel(defaultLabel);
			walkerLabels[w] =walkerLabel;
			walkerLayout.add(walkerLabel);
			if (w<4)
				walkerLabel.setForeground(DOT_COLORS[w]);
		}
		
		walkerLayout.add(Box.createVerticalGlue());
		walkerPanel.add(walkerLayout);
		return walkerPanel;
	}
	
	public void switchMode()
	{
		if (inPlayMode)
		{
			inPlayMode = false;
			blockRB.setEnabled(true);
			startRB.setEnabled(true);
			endRB.setEnabled(true);
			editModeLabel.setVisible(true);
			runGenerationButton.setEnabled(false);
			resetButton.setEnabled(false);
			numGenerationsSpinner.setEnabled(false);
			playModeLabel.setVisible(false);
			mainPanel.clearAllDots();
			generationLabel.setText("Generation: -");
		}
		else
		{
			inPlayMode = true;
			blockRB.setEnabled(false);
			startRB.setEnabled(false);
			endRB.setEnabled(false);
			editModeLabel.setVisible(false);
			runGenerationButton.setEnabled(true);
			resetButton.setEnabled(true);
			numGenerationsSpinner.setEnabled(true);
			playModeLabel.setVisible(true);
			mainPanel.doResetWalkers();
		}
		repaint();
	}
	
	public void updateGeneration(int g)
	{
		generationLabel.setText("Generation: "+g);
		
	}
	
	public boolean isInPlayMode()
	{
		return inPlayMode;
	}
	
	public void updateLabels(String[] walkers)
	{
		for (int w=0; w<NUM_WALKERS; w++)
			walkerLabels[w].setText(walkers[w]);
		repaint();
	}
	
	public void actionPerformed(ActionEvent actEvt)
	{
		if (actEvt.getSource() == switchModeButton)
			switchMode();
		
		if (actEvt.getSource() == runGenerationButton)
			mainPanel.doNGenerations(((SpinnerNumberModel)numGenerationsSpinner.getModel()).getNumber().intValue());
		if (actEvt.getSource() == resetButton)
			mainPanel.doResetWalkers();
	}
}
