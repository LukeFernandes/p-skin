package speothos_venaticus;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DQUtility { //one of these per bone

//avoid GC by pre-allocating everything
Quaternionf destq = new Quaternionf();
Vector3f destt = new Vector3f();
float[] q0 = new float[4];
float t[] = new float[3];
float dq[][] = new float[2][4];

	// input: unit quaternion 'q0', translation vector 't' 
	// output: unit dual quaternion 'dq'
float[][] QuatTrans2UDQ(Matrix4f matrix)
	{
	   matrix.getNormalizedRotation(destq);
	   matrix.getTranslation(destt);
	   q0[0] = destq.w; q0[1] = destq.x;  q0[2] = destq.y;  q0[3] = destq.z; 
	   t[0] = destt.x; t[1] = destt.y; t[2] = destt.z;
	   // non-dual part (just copy q0):
	   for (int i=0; i<4; i++) dq[0][i] = q0[i];
	   // dual part:
	   dq[1][0] = -0.5f*(t[0]*q0[1] + t[1]*q0[2] + t[2]*q0[3]);
	   dq[1][1] = 0.5f*( t[0]*q0[0] + t[1]*q0[3] - t[2]*q0[2]);
	   dq[1][2] = 0.5f*(-t[0]*q0[3] + t[1]*q0[0] + t[2]*q0[1]);
	   dq[1][3] = 0.5f*( t[0]*q0[2] - t[1]*q0[1] + t[2]*q0[0]);
	   
	   return dq;
	}

}
