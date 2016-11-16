package speothos_venaticus;
//TODO: Option Explicit On ... Warning!!! not translated


import org.jdom2.Element;
import org.joml.Matrix4f;
import org.joml.MatrixStackf;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
//Imports Microsoft.DirectX
//Imports Microsoft.DirectX.Direct3D
//Imports Microsoft.DirectX.Direct3D.D3DX
//Event argument class for the AnimationChange event

public abstract class FlatBone {
	
 
 public Matrix4f LM;
 
 public Matrix4f CM;
 
 public Matrix4f BM;
 
 public Matrix4f RotationLocal;
 
 // new2016 /
 public Matrix4f PositionLocal;
 
 // /
 public ArrayList<Key> AnimationRawData1 = new ArrayList<Key>();
 
 // Key mod916
 public ArrayList<Key> AnimationRawData2 = new ArrayList<Key>();
 
 // Key mod916
 public ArrayList<AdvancedKey> AnimationData1  = new ArrayList<AdvancedKey>();
 
 // AdvancedKey mod916
 public ArrayList<AdvancedKey> AnimationData2 = new ArrayList<AdvancedKey>();
 
 // AdvancedKey
 // new916 
 public ArrayList<AdvancedKey> AnimationDataX = new ArrayList<AdvancedKey>();
 
 public Matrix4f identity = (new Matrix4f()).identity();
 
 public DQUtility dualQuat = new DQUtility();
 
 enum TransitionBAT {
     
     NotTransitioningAtAll,
     
     YetToStart,
     
     InProgress,
     
     Finished,
 }
 
 enum DirectionT {
     
     AtoB,
     
     BtoA,
 }
 
 class TransitionBetweenAnimationsStruct {
     
     public TransitionBAT TransitionBetweenAnimationsType;
     
     public DirectionT Direction;
     
     public int TransitionTime;
 }
 
 public TransitionBetweenAnimationsStruct TransitionBetweenAnimationsHolder;
 
 // **
 private float Measure;
 
 private Quaternionf Ident = (new Quaternionf()).identity();
 
 private Quaternionf Outputq;
 
 private Vector3f Outputp;
 
 private Matrix4f Outputm  = new Matrix4f();
 
 private float FrameTracker;
 
 private int SectionTracker;
 
 public boolean HasData = false;
 
 public boolean HasData2 = false;
 
 public boolean UsesPosition = false;
 
 private CycleData CycleType = CycleData.OneCycle;
 
 
 enum CycleData {
     
     OneCycle,
     
     Infinite,
     
     InfiniteRequireHandling,
     
     OneKey,
 }
 
 enum QuatRenderType {
     
     NoAction,
     
     Normal,
     
     StartAgain,
     
     SetQuat,
     
     MoveToDifferentAnimation,
 }
 
 public FlatBone(Matrix4f LM, Matrix4f CM, Matrix4f RL, Matrix4f LL) {
     // (/modified)
     SetLocalMatrix(LM);
     SetCombinedMatrix(CM);
     RotationLocal = RL;
     PositionLocal = LL;
     // new2016;;
     Outputq = (new Quaternionf()).identity();
     Outputp = new Vector3f();
     TransitionBetweenAnimationsHolder = new TransitionBetweenAnimationsStruct();
 }
 
 public FlatBone() {
     SetLocalMatrix( (new Matrix4f()).identity() );
     SetCombinedMatrix( (new Matrix4f()).identity() );
     Outputq = (new Quaternionf()).identity();
     RotationLocal = (new Matrix4f()).identity();
     PositionLocal = (new Matrix4f()).identity();
     TransitionBetweenAnimationsHolder = new TransitionBetweenAnimationsStruct();
     // new2016;;
 }
 
 public final Quaternionf GetOutputRotation() {
         return Outputq;
 }
 public final void SetOutputRotation(Quaternionf value) {
     Outputq = value;
}
 
 public final Vector3f GetOutputPosition() throws Exception { 
         if ((UsesPosition == true)) {
             return Outputp;
         }
         else {
             throw new Exception("This is not a bone that records position changes");
         }
         
     }
 public final void SetOutputPosition(Vector3f value) throws Exception { 
         if ((UsesPosition == true)) {
             Outputp = value;
         }
         else {
             throw new Exception("This is not a bone that records position changes");
         }
         
 }
 
 public final Matrix4f GetLocalMatrix() {
         return LM;
     }
 public final void SetLocalMatrix(Matrix4f value) {
         LM = value;
     }
 
 public final Matrix4f GetBindMatrix() {
         return BM;
     }
 public final void SetBindMatrix(Matrix4f value) {
         BM = value;
     }
 
 public final Matrix4f GetCombinedMatrix() {
         return CM;
     }
 public final void SetCombinedMatrix(Matrix4f value) {
         CM = value;
         if (BM == null)
        	 BM = new Matrix4f();
         BM = CM.invert(BM);
     }
 

 public final Matrix4f GetOutputMatrix() {
         // Outputm = CombinedMatrix * Matrix4f.RotationQuaternion(Outputq) * Matrix4f.Invert(CombinedMatrix)
         return Outputm;
     }
 //deprecated
 public final void SetOutputMatrix(Matrix4f value) {
         Outputm = value;
     }
 
 public final void SetOutputMatrixFS(MatrixStackf stack) {
	 stack.get(Outputm);
 }

 
 public final Quaternionf GetQuaternion(Vector3f V0, Vector3f V1, boolean Relative) {
     if (Relative == true) {
           //
    	 return new Quaternionf();
     }
     else {
            // Matrix4f WER = (Matrix4f.RotationYawPitchRoll((V1.X * -1), (V1.Y * -1), (V1.Z * -1)) * RotationLocal); //java - order changed
    	 Matrix4f WER = new Matrix4f();
    	 RotationLocal.mul( (new Matrix4f()).rotateYXZ((V1.x * -1), (V1.y * -1), (V1.z * -1)), WER );
             //java - YPR is ZXY? Then for OGL is it YXZ?
             // CINEMA 4D's rotation (hpb) is unfortunately given in parent coords (since the position and rotation of every object, including that of bones, is defined relative to parent space) but set in-app in local coords; 
             // We compensate for this via multiplication with the local (rotational) space Matrix4f to get the rotation in local space.
             //Quaternion Q = Quaternion.RotationMatrix(WER); // java - 
    	 Quaternionf Q = (new Quaternionf()).setFromUnnormalized(WER); 
             return Q;
     }
 }
 
 // new2016 /
 private final Vector3f GetPositionDifference(Vector3f V0, Vector3f V1) {
     Matrix4f R = (new Matrix4f()).translate(V1.x, V1.y, V1.z);
     // * PositionLocal
     // (2016)CINEMA 4D's translation (xyz) is unfortunately given in parent coords (since the position and rotation of every object, including that of bones, is defined relative to parent space) but set in-app in local coords; 
     // We compensate for this via multiplication with the local (rotational) space Matrix4f to get the rotation in local space.
     Vector3f trans = new Vector3f();
    // R.Decompose(null, null, trans); java -
     R.getTranslation(trans);
     return trans;
 }
 
 public class ExposeAnimReturn {
	 ArrayList<Key> ARD;
	 ArrayList<AdvancedKey> AD;
	  
 }
 // /
 public ExposeAnimReturn ExposeAnim(CycleData c, ArrayList<Key> ARD, ArrayList<AdvancedKey> AD) { //account for no ref!!!!
     CycleType = c;
     ArrayList<Key> Key_ = ARD;
     ArrayList<AdvancedKey> V3c = new ArrayList<AdvancedKey> ();
     // Loop through computing interval vector movement
     if ((Key_.size() == 1)) {
         CycleType = CycleData.OneKey;
     }
     
     if (!    ( (Key_.get(0)).Tag == 0) ) {
         Key Inserted = new Key( (Key_.get(0)).V3.x, (Key_.get(0)).V3.y, (Key_.get(0)).V3.z, 0);
         // Here we handle the unusual case where there is no key at the start of the animation (we must create one, a duplicate of the first key available).
         Key_.add(0, Inserted);
     }
     
     for (int i = 0; (i 
                 <= (Key_.size() - 1)); i++) {
         System.out.println(Key_.get(i).toString());
         Vector3f v;
         Vector3f va;
         v = ( Key_.get(i) ).V3;
         va = ( Key_.get(0) ).V3;
         Quaternionf q = this.GetQuaternion(va, v, false);
         float interval = ( Key_.get(i) ).Tag;
         // new2016 / 
         if (!( (Key_.get(0)).P3 == null)) {
             AdvancedKey a = new AdvancedKey(interval, q, this.GetPositionDifference( ( Key_.get(0) ).P3, ( Key_.get(i) ).P3));
             V3c.add(a);
         }
         else {
             AdvancedKey a = new AdvancedKey(interval, q);
             // (/retained)
             a.V3 = v;
             // (/retained)
             V3c.add(a);
             // (/retained)
         }
         
         //  /
     }
     
     V3c.trimToSize();
     // Intervals are based on 60 fps. Divide intervals by required frames, in this case 60*5 = 300. Given an AdvancedKey x, we store the interval between x and (x+1) in the same x.
     for (int i = 0; (i 
                 <= (V3c.size() - 1)); i++) {
         float interval;
         if (!((i + 1) 
                     == Key_.size())) {
             float FrameMultiplier = (CustomMeshA.FrameRate / 60f) * 2f;
             interval = (int)(      (  Key_.get(i + 1).Tag - Key_.get(i).Tag  ) 
                         * FrameMultiplier);
             V3c.get(i).Tag = interval;
         }
         else {
             interval = 0;
             V3c.get(i).Tag = interval;
         }
         
     }
     
     AD = V3c;
     if ((CycleType == CycleData.InfiniteRequireHandling)) {
         CreateRepeat((float)CustomMeshA.FrameRate, 0.5f, AD);
     }
     
     if (TransitionBetweenAnimationsHolder == null)
     {
    	 System.out.println("null");
     }
     
     TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType = TransitionBAT.NotTransitioningAtAll;
     ExposeAnimReturn returning = new ExposeAnimReturn();
     returning.AD = AD;
     returning.ARD = Key_;
     return returning; //no ref!
     // Console.WriteLine("sd " + V3c(0).Q4.ToString + V3c(1).Q4.ToString + V3c(2).Q4.ToString)
 }
 
 private final QuatRenderType CycleThroughAnim(ArrayList<AdvancedKey> AD) {
     QuatRenderType T = QuatRenderType.Normal; //java - requires default value
     if ( (SectionTracker < (AD.size() - 1)) 
                 && !(CycleType == CycleData.OneKey) ) 
     {
    	 //for sound
    	// if (FrameTracker == 0 && SectionTracker == 0) AnimationDataX.get(SectionTracker).
         T = QuatRenderType.Normal;
         if ((FrameTracker < ( AD.get(SectionTracker) ).Tag)) {
             // This is the general case. Every frame, the Measure float is incremented towards the quaternion value of the next key until the interval and FrameTracker values match.
             FrameTracker++;
             Measure += 1.0f / (float)( ( AD.get(SectionTracker) ).Tag);
         }
         else {
             // Handles when the next key has been reached.
             SectionTracker++;
             
             if (SectionTracker == (AD.size() - 1)  ) {
             // Do Nothing and wait for next if.
             }
             else {
            	 // Start on next key transition immediately.
            	 FrameTracker = 1;
            	 Measure = 1.0f / (float)( ( AD.get(SectionTracker) ).Tag);
             						}
     
         		}
     }
     if ((SectionTracker  == (AD.size() - 1))) {
    	 	// Handles repeat of animation.
     switch (CycleType) {
      case InfiniteRequireHandling:
    	 			T = QuatRenderType.StartAgain;
    	 			if ((FrameTracker < ( AD.get(SectionTracker) ).Tag)) {
    	 				// Auto interpolate between end and start.
    	 				FrameTracker++;
    	 				Measure += 1.0 / (float)( ( AD.get(SectionTracker) ).Tag);
    	 			}
    	 			else {
    	 				// Start on key0 -> key1 transition immediately.
    	 				SectionTracker = 0;
    	 				FrameTracker = 1;
    	 				Measure = 1.0f / (float)( ( AD.get(SectionTracker) ).Tag);
    	 				}
         break;
      case Infinite:
         T = QuatRenderType.StartAgain;
         // Start on key0 -> key1 transition immediately.
         SectionTracker = 0;
         FrameTracker = 1;
         Measure = 1.0f / (float)( ( AD.get(SectionTracker) ).Tag);
         break;
      case OneCycle:
         // Do nothing except:
         T = QuatRenderType.NoAction;
         // Renders out of scope for DeployAnim()
         break;
      case OneKey:
         // Do this
         T = QuatRenderType.SetQuat;
         break;
 }
}

return T;
}
 
 public final QuatRenderType CycleBetweenAnims(ArrayList<AdvancedKey> AD1, ArrayList<AdvancedKey> AD2) {
     // new916 *****
	 QuatRenderType T = QuatRenderType.Normal; //java - requires default value
     if (TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType != TransitionBAT.NotTransitioningAtAll) {
         switch (TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType) {
             case YetToStart:
            	 Measure = 1.0F / TransitionBetweenAnimationsHolder.TransitionTime;
                 // we want a good transition time from one to the other
                 FrameTracker = 1;
                 T = QuatRenderType.MoveToDifferentAnimation;
                 TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType = TransitionBAT.InProgress;
                 break;
             case InProgress:
                 if (FrameTracker < TransitionBetweenAnimationsHolder.TransitionTime) {
                	 Measure += 1.0F / TransitionBetweenAnimationsHolder.TransitionTime;
                     // advance measure
                     FrameTracker++;
                     T = QuatRenderType.MoveToDifferentAnimation;
                 }
                 else {
                     T = QuatRenderType.StartAgain;
                     // Start on key0 -> key1 transition immediately.
                     SectionTracker = 0;
                     FrameTracker = 1;
                     Measure = 1.0f / (float)( AD2.get(SectionTracker).Tag);
                     TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType = TransitionBAT.NotTransitioningAtAll;
                     AnimationDataX = AD2;
                 }
                 
                 break;
         }
     }
     
     return T;
     // ****
 }
 
 public final void DeployAnim(int SecondQuat) {
     if (HasData) {
         System.out.println(TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType.toString());
         try {
             QuatRenderType xc;
             // mod916
             // new916 *****
             if (TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType == TransitionBAT.NotTransitioningAtAll) {
                 xc = this.CycleThroughAnim(AnimationDataX);
                 QuatVectorInterpolate(xc, AnimationDataX, AnimationData2);
             }
             else if ( (TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType != TransitionBAT.NotTransitioningAtAll) 
                         && HasData2 ) {
                 if (TransitionBetweenAnimationsHolder.Direction == DirectionT.AtoB) {
                     xc = CycleBetweenAnims(AnimationDataX, AnimationData2);
                     QuatVectorInterpolate(xc, AnimationDataX, AnimationData2);
                 }
                 else if (TransitionBetweenAnimationsHolder.Direction == DirectionT.BtoA) {
                     xc = CycleBetweenAnims(AnimationDataX, AnimationData1);
                     QuatVectorInterpolate(xc, AnimationDataX, AnimationData1);
                 }
                 
             }
             
             // ****
         }
         catch (NullPointerException e) {
             System.out.println("No Bone Animation Data");
         }
     }
     
 }
 
 private final void QuatVectorInterpolate(QuatRenderType xc, ArrayList<AdvancedKey> AD1, ArrayList<AdvancedKey> AD2) {
     switch (xc) {
         case Normal:
             // new2016 /
             if (UsesPosition == true) {
                 AD1.get(SectionTracker).P3.lerp( ( AD1.get(SectionTracker + 1) ).P3, Measure, Outputp);
                 System.out.println(Outputp.toString());
             }
             
             // /
             ( AD1.get(SectionTracker) ).Q4.slerp( ( AD1.get(SectionTracker + 1) ).Q4, Measure, Outputq);
             System.out.println(Outputq.toString());
             break;
         case StartAgain:
             // new2016 /
             if (UsesPosition == true) {
            	 ( AD1.get(SectionTracker) ).P3.lerp( ( AD1.get(0) ).P3, Measure, Outputp);
             }
             
             // /
             ( AD1.get(SectionTracker) ).Q4.slerp( ( AD1.get(0) ).Q4, Measure, Outputq);
             break;
         case SetQuat:
             Outputq = ( AD1.get(0) ).Q4;
             // new916 **************
             break;
         case MoveToDifferentAnimation:
             // this has some of the semantics of both of the above. We stop the current anim mid stream
             // and (s)lerp to the beginning of a different one
             if ((UsesPosition == true)) {
                 Vector3f LastLerp = new Vector3f();
                 ( AD1.get(SectionTracker) ).P3.lerp( ( AD1.get(SectionTracker + 1) ).P3, Measure, LastLerp);
                 LastLerp.lerp( ( AD2.get(0) ).P3, Measure, Outputp);
             }
             
             Quaternionf LastSlerp = new Quaternionf();
             ( AD1.get(SectionTracker) ).Q4.slerp( ( AD1.get(SectionTracker + 1) ).Q4, Measure, LastSlerp);
             LastSlerp.slerp( ( AD2.get(0) ).Q4, Measure, Outputq);
             // ****************************
             break;
     }
 }
 
 private final void CreateRepeat(float fps, float noseconds, ArrayList<AdvancedKey> AD) {
     ( AD.get(AD.size() - 1) ).Tag = (int)(fps * noseconds);
 }
}