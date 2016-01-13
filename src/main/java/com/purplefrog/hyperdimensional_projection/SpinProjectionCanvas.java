package com.purplefrog.hyperdimensional_projection;

import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.apache.commons.math3.linear.*;

import javax.imageio.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * Created by thoth on 1/13/16.
 */
public class SpinProjectionCanvas
    extends Canvas
{
    RealMatrix baseOrientation;
    private Vector3D axis;
    private Vector3D origin;

    public SpinProjectionCanvas(Rotation baseOrientation, Vector3D axis)
    {
        this.baseOrientation = new Array2DRowRealMatrix(baseOrientation.getMatrix());
        this.axis = axis;
    }

    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(800,600);
    }

    @Override
    public void paint(Graphics g)
    {
        final long start = System.currentTimeMillis();
        //super.paint(g);
        int w_ = getWidth();
        int h_ = getHeight();


        if (true) {
            BufferedImage img = new BufferedImage(w_, h_, BufferedImage.TYPE_INT_RGB);

            Graphics2D g2 = img.createGraphics();
//        nap(100);
            paintCells(w_, h_, g2);
//        nap(100);

            g.drawImage(img, 0,0, null);
//        nap(100);
        } else {
            paintCells(w_, h_, (Graphics2D) g);
        }

        Runnable runnable = new Runnable()
        {
            public void run()
            {
                int rate = 50;
                long now = System.currentTimeMillis();
                long toWait = start+(1000/rate)-now;
                if (toWait>0)
                    nap((int)toWait);
                repaint();
            }
        };
        new Thread(runnable, "refresh").start();
    }

    @Override
    public void update(Graphics graphics)
    {
        paint(graphics);
    }

    public static void nap(int millis)
    {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void paintCells(int width, int height, Graphics2D g2)
    {
        g2.setColor(Color.black);
        g2.fillRect(0,0,width,height);

        long periodMillis = 12000;
        double phase = (System.currentTimeMillis() % periodMillis) / (double)periodMillis;
        RealMatrix m = matrixForTime(phase, axis, baseOrientation);

        double scale=40;
        paintCells(width, height, g2, m, scale);
    }

    public static void paintCells(int width, int height, Graphics2D g2, RealMatrix m, double scale)
    {
        g2.translate(width/2, height/2);
        g2.scale(scale, scale);
        g2.setStroke(new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));

        java.util.List<Face> polys = Project3D.facesForBoundary(m, width /scale/2, height /scale/2);

        for (Face poly : polys) {
            draw(g2, poly);
        }
    }

    public static RealMatrix matrixForTime(double phase, Vector3D axis, RealMatrix baseOrientation)
    {
        double theta = 2*Math.PI*phase;

        Rotation spin_ = new Rotation(axis, theta, RotationConvention.VECTOR_OPERATOR);
        RealMatrix spin = new Array2DRowRealMatrix(spin_.getMatrix());

        double[] origin = {-0.5, -0.5, -0.5};

        RealMatrix result;
        if (true) {
            result = spin.multiply(baseOrientation);
        } else {
            result = baseOrientation.multiply(spin);
        }

        RealMatrix translation = Util.translationMatrix(origin);

        return Project3D.to_4x4(result).multiply(translation);
    }

    public static void draw(Graphics2D g2, Face poly)
    {
        GeneralPath gp = poly.toGeneralPath();
        g2.setColor(poly.fill);
        g2.fill(gp);
        g2.setColor(Color.white);
        g2.draw(gp);
    }

    /**
     * After you generate your frames of animation
     * <pre>convert -delay 3 -loop 0 outdir/*.png anim.gif</pre>
     */
    public static class RenderAnimation
    {
        public static void main(String[] argv)
            throws IOException
        {
            double nSeconds = 12;
            double fps = 30;
            int frames = (int) (fps * nSeconds);

            int w = Integer.parseInt(argv[0]);
            int h = Integer.parseInt(argv[1]);
            double scale = Double.parseDouble(argv[2]);
            File outdir;
            if (argv.length>3) {
                outdir = new File(argv[3]);
            } else {
                outdir = new File("/var/tmp/blender/hyperdimensional-projection1");
            }

            if (!outdir.exists())
                outdir.mkdirs();

            Random rand;
            if (argv.length>4) {
                rand = new Random(Integer.parseInt(argv[4]));
            } else {
                rand = new Random();
            }

            Vector3D axis = Util.randomAxis(rand);
            Rotation q = Util.randomOrientation(rand);
            Array2DRowRealMatrix baseOrientation = new Array2DRowRealMatrix(q.getMatrix());

            for (int i=0; i<frames; i++) {
                double phase = i/(double) frames;

                RealMatrix m = matrixForTime(phase, axis, baseOrientation);

                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                paintCells(w, h, img.createGraphics(), m, scale);

                ImageIO.write(img, "png", new File(outdir, pad4(i)+".png"));
                if (i>0 && i%75==0)
                    System.out.println();
                System.out.print('.');
            }
            System.out.println();
        }

        private static String pad4(int val)
        {
            StringBuilder rval = new StringBuilder();
            rval.append(val);
            while (4>rval.length())
                rval.insert(0, '0');

            return rval.toString();
        }
    }
}
