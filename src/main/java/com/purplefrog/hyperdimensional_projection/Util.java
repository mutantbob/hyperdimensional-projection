package com.purplefrog.hyperdimensional_projection;

import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.apache.commons.math3.linear.*;

import java.util.*;

/**
 * Created by thoth on 1/13/16.
 */
public class Util
{
    /**
     * returns a random unit vector where the randomness is evenly distributed on the surface of the 3-sphere.
     */
    public static Vector3D randomAxis(Random rand)
    {
        double z = rand.nextDouble()*2-1;

        double theta = rand.nextDouble()*2*Math.PI;
        double c = Math.cos(theta);
        double s = Math.sin(theta);

        double r = Math.sqrt(1-z*z);
        return new Vector3D(r*c, r*s, z);
    }

    public static Rotation randomOrientation(Random r)
    {
        return new Rotation(r.nextDouble(), r.nextDouble(), r.nextDouble(), r.nextDouble(), true);
    }

    public static RealMatrix translationMatrix(double ... origin)
    {
        double[][] mt = {
            {1,0,0,-origin[0]},
            {0,1,0,-origin[1]},
            {0,0,1,-origin[2]},
            {0,0,0,1}
        };
        return new Array2DRowRealMatrix(mt);
    }
}
