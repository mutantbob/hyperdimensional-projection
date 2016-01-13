package com.purplefrog.hyperdimensional_projection;

import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.apache.commons.math3.linear.*;

/**
 * Created by thoth on 1/13/16.
 */
public class Exp1
{
    public static void main(String[] argv)
    {
        Rotation r = new Rotation(new Vector3D(1,1,1), Math.PI, RotationConvention.VECTOR_OPERATOR);

        RealMatrix m = new Array2DRowRealMatrix(r.getMatrix());

        System.out.println(m);
        System.out.println(m.multiply(m));
    }
}
