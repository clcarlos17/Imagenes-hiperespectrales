package Proyecto;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Color;

public class Pintar extends Frame {

    public Pintar() {
        this.setSize(200, 150);
        this.setVisible(true);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.red);
        g.drawLine(25, 125, 175, 125);
        g.drawLine(25, 50, 25, 125);
    }

    public static void main(String[] args) {
    	Pintar p = new Pintar();
    }

}