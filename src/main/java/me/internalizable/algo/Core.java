package me.internalizable.algo;

import javax.swing.*;
import java.awt.*;

public class Core {

    public static void main(String[] args){
        JFrame frame = new JFrame("AI Project - A* Search Algorithm");
        frame.setContentPane(new Window(new Map(MapGenerator.getDefaultWorld())).getWrapper());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(900, 700));
        frame.pack();
        frame.setVisible(true);
    }

}
