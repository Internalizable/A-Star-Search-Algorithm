package me.internalizable.algo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Map extends JPanel implements MouseListener, MouseMotionListener {

    public static final char START = 'S',
            OBSTACLE = 'X',
            END = 'E',
            EMPTY = ' ',
            PATH = 'P';

    private static final Color OBSTACLE_COLOR = new Color(44, 62, 80),
            START_COLOR = new Color(241, 196, 15),
            START_HOVER_COLOR = new Color(255, 230, 31),
            EMPTY_COLOR = new Color(236, 240, 241),
            EMPTY_HOVER_COLOR = new Color(214, 218, 219),
            END_COLOR = new Color(52, 152, 219),
            PATH_COLOR = new Color(211, 12, 12);

    private char[][] map;
    private int mapWidth, mapHeight;
    private final double padding = 0.1; // In percent
    private Point origin;
    private Point mousePosition;
    private Point endPosition;
    private double zoomFactor;

    private ArrayList<Point> latestPoints;

    public Map(char[][] map){
        setMap(map);
        addMouseListener(this);
        addMouseMotionListener(this);

        this.latestPoints = new ArrayList<>();
    }

    public void setMap(char[][] map){
        this.map = map;
        mapHeight = map.length;
        int width = 0;
        for(int i = 0; i < map.length; i++){
            if(map[i].length > width)
                width = map[i].length;
            for(int j = 0; j < map[i].length; j++)
                if(map[i][j] == END)
                    endPosition = new Point(i, j);
        }

        mapWidth = width;

        repaint();
    }

    public char[][] getMap(){
        return map;
    }

    public Point getEndPosition(){
        return endPosition;
    }

    public Set<Point> getStarts(){
        Set<Point> starts = new HashSet<Point>();
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[i].length; j++)
                if(map[i][j] == START)
                    starts.add(new Point(i, j));
        }

        return starts;
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;

        Dimension panelDimensions = getSize();
        double panelWidth = panelDimensions.getWidth();
        double panelHeight = panelDimensions.getHeight();

        if(mapWidth / panelWidth > mapHeight / panelHeight)
            zoomFactor = panelWidth * (1 - 2 * padding) / mapWidth;
        else
            zoomFactor = panelHeight * (1 - 2 * padding) / mapHeight;

        origin = new Point((int) ((panelWidth - mapWidth * zoomFactor) / 2),
                (int) ((panelHeight - mapHeight * zoomFactor) / 2));

        g2.setColor(Color.WHITE);
        g2.clearRect(0, 0, (int) panelWidth, (int) panelHeight);

        g2.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Font bigFont = new Font(g2.getFont().getFontName(), Font.PLAIN, 25);
        Font smallFont = new Font(g2.getFont().getFontName(), Font.PLAIN, 15);

        double cellDimen = zoomFactor * mapWidth / map.length;


        g2.setFont(smallFont);
        g2.setColor(Color.BLACK);
        FontMetrics fm = g2.getFontMetrics();

        for(int i = 0; i < map.length; i++){
            String text = i + "";
            int x = ((int) cellDimen - fm.stringWidth(text)) / 2;
            int y = (fm.getAscent() + ((int) cellDimen - (fm.getAscent() + fm.getDescent())) / 2);
            g2.drawString(text, (int) (origin.getX() + x - cellDimen), (int) (origin.getY() + i * cellDimen + y));
        }

        for(int i = 0; i < map[0].length; i++){
            String text = i + "";
            int x = ((int) cellDimen - fm.stringWidth(text)) / 2;
            int y = (fm.getAscent() + ((int) cellDimen - (fm.getAscent() + fm.getDescent())) / 2);
            g2.drawString(text, (int) (origin.getX() + i * cellDimen + x), (int) (origin.getY() + y - cellDimen));
        }

        boolean hovering = false;

        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[i].length; j++){
                char type = map[i][j];

                switch(type) {
                    case START: g2.setColor(START_COLOR);
                        break;
                    case END: g2.setColor(END_COLOR);
                        break;
                    case OBSTACLE: g2.setColor(OBSTACLE_COLOR);
                        break;
                    case PATH: g2.setColor(PATH_COLOR);
                        break;
                    case EMPTY:
                    default:g2.setColor(EMPTY_COLOR);
                        break;
                }

                int anchorX = (int) (origin.getX() + cellDimen * j);
                int anchorY = (int) (origin.getY() + cellDimen * i);


                if((type == EMPTY || type == START) && mousePosition != null &&
                        mousePosition.getX() == i && mousePosition.getY() == j){

                    g2.setColor(type == START ? START_HOVER_COLOR: EMPTY_HOVER_COLOR);
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    hovering = true;
                }

                g2.fillRect(1 + anchorX, 1 + anchorY, (int) cellDimen - 2, (int) cellDimen - 2);

                String text = null;

                if(type == START)
                    text = "☨";
                else if(type == END)
                    text = "⚑";

                g2.setColor(Color.white);

                if(mousePosition != null && mousePosition.getX() == i && mousePosition.getY() == j && type != START) {
                    text = String.format("(%d, %d)", i, j);
                    if(type == EMPTY)
                        g2.setColor(Color.BLACK);
                    g2.setFont(smallFont);
                    fm = g2.getFontMetrics();
                    int x = ((int) cellDimen - fm.stringWidth(text)) / 2;
                    int y = (fm.getAscent() + ((int) cellDimen - (fm.getAscent() + fm.getDescent())) / 2);
                    g2.drawString(text, anchorX + x, anchorY + y);
                } else if(text != null) {
                    g2.setFont(bigFont);
                    fm = g2.getFontMetrics();
                    int x = ((int) cellDimen - fm.stringWidth(text)) / 2;
                    int y = (fm.getAscent() + ((int) cellDimen - (fm.getAscent() + fm.getDescent())) / 2);
                    g2.drawString(text, anchorX + x, anchorY + y);
                }
            }
        }

        if(!hovering)
            setCursor(Cursor.getDefaultCursor());

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mousePosition = getMapCell(e.getX(), e.getY());
        if(mousePosition != null) {
            int i = (int) mousePosition.getX();
            int j = (int) mousePosition.getY();

            if(SwingUtilities.isRightMouseButton(e)) {
                if(map[i][j] == OBSTACLE)
                    map[i][j] = EMPTY;
                else if(map[i][j] == EMPTY)
                    map[i][j] = OBSTACLE;
            } else if(SwingUtilities.isLeftMouseButton(e)) {
                if(map[i][j] == START)
                    map[i][j] = EMPTY;
                else if(map[i][j] == EMPTY)
                    map[i][j] = START;
            } else if(SwingUtilities.isMiddleMouseButton(e)) {
                if(map[i][j] == END) {
                    map[i][j] = EMPTY;
                    endPosition = null;
                }
                else if(map[i][j] == EMPTY) {
                    for(int y = 0; y < map.length; y++)
                        for(int z = 0; z < map[y].length; z++)
                            if(map[y][z] == END)
                                map[y][z] = EMPTY;

                    map[i][j] = END;
                    endPosition = new Point(i, j);
                }
            }

        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition = getMapCell(e.getX(), e.getY());
        repaint();
    }

    private Point getMapCell(double x, double y){
        int i = (int) ((y - origin.getY()) / zoomFactor);
        int j = (int) ((x - origin.getX()) / zoomFactor);

        if(i < 0 || i >= mapWidth)
            return null;
        else if(j < 0 || j >= map[i].length)
            return null;
        return new Point(i, j);
    }

    public void setLatestPoints(ArrayList<Point> latestPoints) {
        this.latestPoints = latestPoints;
    }
}
