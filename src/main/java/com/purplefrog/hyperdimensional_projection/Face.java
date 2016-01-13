package com.purplefrog.hyperdimensional_projection;

import java.awt.*;
import java.awt.geom.*;

/**
 * Created by thoth on 1/13/16.
 */
public class Face
{
    public final double[] xys;
    public final Color fill;
    public static Color stroke = Color.white;

    public Face(double[] xys, Color c)
    {
        this.xys = xys;
        fill = c;
    }

    public GeneralPath toGeneralPath()
    {
        int n = xys.length / 2;
        GeneralPath gp = new GeneralPath();
        for (int i = 0; i < n; i++) {
            double x = xys[i * 2];
            double y = xys[i * 2 + 1];
            int i2 = ((i + 1) % n) * 2;
            double x2 = xys[i2];
            double y2 = xys[i2 + 1];

            if (i == 0)
                gp.moveTo(x, y);
            else
                gp.lineTo(x, y);
        }
        gp.closePath();
        return gp;
    }
}
