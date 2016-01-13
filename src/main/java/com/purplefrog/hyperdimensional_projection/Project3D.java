package com.purplefrog.hyperdimensional_projection;

import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.apache.commons.math3.linear.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by thoth on 1/12/16.
 */
public class Project3D
{
    public static void main(String[] argv)
    {
        Random r = new Random(420);

        Rotation rot = new Rotation(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble(), true);

        RealMatrix m = new Array2DRowRealMatrix(rot.getMatrix());
        m = to_4x4(m);

        double w =16;
        double h = 9;

        List<Face> polys = facesForBoundary(m, w, h);

        JFrame fr = new JFrame();
        Canvas canvas = new FaceCanvas(polys);
        fr.getContentPane().add(canvas);
        fr.pack();
        fr.setVisible(true);


    }

    public static List<Face> facesForBoundary(RealMatrix m, double w, double h)
    {
        double[] c_ul = m.operate(new double[]{-w, h, 0, 1});
        double[] c_ur = m.operate(new double[]{w, h, 0, 1});
        double[] c_ll = m.operate(new double[]{-w, -h, 0, 1});
        double[] c_lr = m.operate(new double[]{w, -h, 0, 1});

        double[] c0 = min(c_ul, c_ur, c_ll, c_lr);
        double[] c1 = max(c_ul, c_ur, c_ll, c_lr);

        return facesForBoundary(m, c0, c1);
    }

    public static List<Face> facesForBoundary(RealMatrix cutPlane, double[] cornerMin, double[] cornerMax)
    {
        List<Face> polys = new ArrayList<Face>();
        if (true) {
            for (double x = Math.floor(cornerMin[0]); x<= Math.ceil(cornerMax[0]); x++) {
                for (double y = Math.floor(cornerMin[1]); y<= Math.ceil(cornerMax[1]); y++) {
                    for (double z = Math.floor(cornerMin[2]); z<= Math.ceil(cornerMax[2]); z++) {
                        renderCell(polys, x,y,z, cutPlane);
                    }
                }
            }
        } else {
            for (double x = -2; x<= 2; x++) {
                for (double y = -2; y<= 2; y++) {
                    for (double z = -2; z<= 2; z++) {
                        renderCell(polys, x,y,z, cutPlane);
                    }
                }
            }
        }
        return polys;
    }

    public static RealMatrix to_4x4(RealMatrix in_)
    {
        double[][] in = in_.getData();
        return new Array2DRowRealMatrix(new double[][]{
            {in[0][0], in[0][1], in[0][2], 0},
            {in[1][0], in[1][1], in[1][2], 0},
            {in[2][0], in[2][1], in[2][2], 0},
            {0,0,0,1},
        });
    }

    static Color xColor1 = new Color(0.5f, 0.5f, 0.7f);
    static Color xColor2 = new Color(0f, 0f, 1f);
    static Color zColor1 = new Color(0f, 0.6f, 0f);
    static Color zColor2 = new Color(0f, 1f, 0f);
    static Color yColor1 = new Color(0.6f, 0f, 0f);
    static Color yColor2 = new Color(1f, 0f, 0f);

    public static void renderCell(List<Face> polys, double x, double y, double z, RealMatrix m)
    {
        double x2 = x+1;
        double y2 = y+1;
        double z2 = z+1;

        double[] p000 = m.operate(new double[]{x, y, z, 1});
        double[] p001 = m.operate(new double[]{x, y, z2, 1});
        double[] p010 = m.operate(new double[]{x, y2, z, 1});
        double[] p011 = m.operate(new double[]{x, y2, z2, 1});
        double[] p100 = m.operate(new double[]{x2, y, z, 1});
        double[] p101 = m.operate(new double[]{x2, y, z2, 1});
        double[] p110 = m.operate(new double[]{x2, y2, z, 1});
        double[] p111 = m.operate(new double[]{x2, y2, z2, 1});

        int cutCount = countPositiveZ(p000, p001, p010, p011, p100, p101, p110, p111);

        if (cutCount==0 || cutCount>4) {
            // this cell can not be part of the boundary
            return;
        }

        //blender_dump(p000, p001, p010, p011, p100, p101, p110, p111);

        boolean xSide = (Math.floor(x) %2) ==0;
        boolean ySide = (Math.floor(y) %2) ==0;
        boolean zSide = (Math.floor(z) %2) ==0;
        maybeAddFace(polys, Arrays.asList(p000, p001, p011, p010), xSide?xColor1:xColor2);
        maybeAddFace(polys, Arrays.asList(p100, p101, p111, p110), xColor2);

        maybeAddFace(polys, Arrays.asList(p000, p010, p110, p100), zSide?zColor1:zColor2);
        maybeAddFace(polys, Arrays.asList(p001, p011, p111, p101), zColor2);

        maybeAddFace(polys, Arrays.asList(p000, p100, p101, p001), ySide?yColor1:yColor2);
        maybeAddFace(polys, Arrays.asList(p010, p110, p111, p011), yColor2);
    }

    public static void blender_dump(double[] ...points)
    {
        System.out.println("[");
        for (double[] point : points) {
            System.out.print("[");
            for (double x : point) {
                System.out.print(x+",");
            }
            System.out.println("]");
        }
        System.out.println("]");
    }

    public static void maybeAddFace(List<Face> polys, List<double[]> corners, Color color)
    {
        int count = countPositiveZ(corners);
        if (0==count) {
            double[] xys = new double[corners.size()*2];

            for (int i=0; i<corners.size(); i++) {
                double[] xy = corners.get(i);
                xys[i*2] = xy[0];
                xys[i*2+1] = xy[1];
            }

            polys.add(new Face(xys, color));
        }
    }

    public static boolean sameSide(List<double[]> corners)
    {
        boolean side = corners.get(0)[2]>0;
        for (int i=1; i<corners.size(); i++) {
            boolean side2 = corners.get(i)[2]>0;
            if (side!=side2)
                return false;
        }
        return true;
    }

    public static int countPositiveZ(List<double[]> corners)
    {
        int count=0;
        for (double[] corner : corners) {
            if (corner[2] > 0)
                count++;
        }
        return count;
    }

        public static int countPositiveZ(double[] ...corners)
        {
        int count=0;
        for (double[] corner : corners) {
            if (corner[2] > 0)
                count++;
        }
        return count;
    }

    public static void test1()
    {
        Vector3D a = new Vector3D(1,0,0);
        Vector3D b = new Vector3D(0,1,0);

        Vector3D c = a.crossProduct(b);

        System.out.println(c);

        double [][] thing = {
            {
            2,0,0.1,},
            {0,1,0.2},
            {0,0,1.5}
        };
        RealMatrix rm = new Array2DRowRealMatrix(thing);

        RealVector rv = new ArrayRealVector(c.toArray());
        RealVector xx = rm.preMultiply(rv);
        System.out.println(xx);
        ArrayRealVector a_ = new ArrayRealVector(a.toArray());
        System.out.println(rm.preMultiply(a_));

        System.out.println(rm.operate(rv));
    }

    public static double[] min(double[] a, double[] b, double[] c, double[] d)
    {
        double[] rval = new double[a.length];
        for (int i = 0; i < rval.length; i++) {
            double v = Math.min(Math.min(a[i], b[i]), Math.min(c[i], d[i]));
            rval[i] = v;
        }
        return rval;
    }

    public static double[] max(double[] a, double[] b, double[] c, double[] d)
    {
        double[] rval = new double[a.length];
        for (int i = 0; i < rval.length; i++) {
            double v = Math.max(Math.max(a[i], b[i]), Math.max(c[i], d[i]));
            rval[i] = v;
        }
        return rval;
    }

}
