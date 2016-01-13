package com.purplefrog.hyperdimensional_projection;

import java.awt.*;
import java.awt.geom.*;

/**
 * Created by thoth on 1/13/16.
 */
public class FaceCanvas
    extends Canvas
{
    private final java.util.List<Face> polys;

    public FaceCanvas(java.util.List<Face> polys)
    {
        this.polys = polys;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(800, 450);
    }

    @Override
    public void paint(Graphics graphics)
    {
        super.paint(graphics);

        Graphics2D g2 = (Graphics2D) graphics;

        g2.setColor(Color.black);
        int w = getWidth();
        int h = getHeight();
        g2.fillRect(0, 0, w, h);

//            g2.setColor(Color.green);
//            for (Face poly : polys) {
//                draw(g2, poly);
//            }

        g2.translate(w / 2, h / 2);
        g2.scale(40, 40);
        if (false) {
            g2.setColor(Color.red);

            if (false) {
                g2.fillRect(-4, -4, 8, 8);
            } else {
                GeneralPath gp = new GeneralPath();
                gp.moveTo(-4, -4);
                gp.lineTo(4, -4);
                gp.lineTo(4, 4);
                gp.lineTo(-4, 5);
                gp.closePath();
                g2.fill(gp);
            }
        }

        g2.setColor(Color.green);
        g2.setStroke(new BasicStroke(0.1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        for (Face poly : polys) {
            g2.setColor(poly.fill);
            GeneralPath gp = poly.toGeneralPath();
            g2.fill(gp);
            g2.setColor(poly.stroke);
            g2.draw(gp);
        }


    }

}
