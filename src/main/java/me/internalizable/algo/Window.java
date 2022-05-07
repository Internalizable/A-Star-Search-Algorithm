package me.internalizable.algo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class Window implements ActionListener{

    private final Map maze;
    private JComboBox mapSelector;
    private JButton executeButton;
    private JPanel wrapper;
    private JTextArea obstaclesAreInNavyTextArea;
    private JTextArea execResults;
    private JButton resetButton;

    public Window(Map maze){
        this.maze = maze;
        wrapper.add(maze, BorderLayout.CENTER);

        mapSelector.addItem("Default");
        mapSelector.addItem("Second Map");

        mapSelector.addActionListener(this);
        executeButton.addActionListener(this);
        resetButton.addActionListener(this);
    }

    public JPanel getWrapper(){
        return wrapper;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == executeButton){
            //Execute the program
            FiboAlgorithm solver = new FiboAlgorithm();
            Point home = maze.getEndPosition();

            StringBuilder sb = new StringBuilder();
            Point winner = null;
            int numMoves = Integer.MAX_VALUE;

            List<Point> winnerPoints = null;

            for(Point start : maze.getStarts()) {
                try {
                    //Execute A* for the smiley
                    List<Point> points = solver.execute(maze, (int) start.getX(), (int) start.getY(),
                            (int) home.getX(), (int) home.getY());
                    //Output the path for the smiley
                    sb.append(String.format("Start (%d, %d) found end via", (int) start.getX(), (int) start.getY()));

                    for(Point p : points) {
                        sb.append(String.format("\n   (%d, %d) ", (int) p.getX(), (int) p.getY()));
                    }

                    sb.append("\nand expanding " + solver.getNumberOfNodesVisited() + " nodes\nwith a path of " + points.size() + " cells.\n\n");

                    if(winner == null || numMoves > points.size()){
                        winner = start;
                        numMoves = points.size();
                        winnerPoints = points;
                    }

                } catch(RuntimeException ex){
                    sb.append(String.format("The end can't be reached by start (%d, %d).\n",
                            (int) start.getX(), (int) start.getY()));
                }
            }

            if(winner != null) {
                sb.append(String.format("\nStart (%d, %d) is the winner,\nwith %d moves", (int) winner.getX(), (int) winner.getY(), numMoves));

                for(Point p : winnerPoints) {

                    if((int) home.getX() != (int) p.getX() || (int) home.getY() != (int) p.getY())
                        this.maze.getMap()[(int) p.getX()][(int) p.getY()] = 'P';
                }

                execResults.setText(sb.toString());
                wrapper.validate();

                this.maze.repaint();
            } else {
                final Runnable runnable =
                        (Runnable) Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
                if (runnable != null) runnable.run();

                JOptionPane.showMessageDialog(getWrapper(),
                        "No path found from any of the start points!", "A* Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if(e.getSource() == mapSelector) {

            String map = (String) mapSelector.getSelectedItem();
            if(map.equals("Default"))
                maze.setMap(MapGenerator.getDefaultWorld());
            else
                maze.setMap(MapGenerator.getSecondWorld());
        } else if(e.getSource() == resetButton) {
            execResults.setText("");

            System.out.println("Reset button");

            Arrays.stream(this.maze.getMap()).forEach(row -> {
                for(int i = 0; i < row.length; i++)
                    if(row[i] == 'P')
                        row[i] = ' ';
            });

            this.maze.repaint();
        }
    }
}
