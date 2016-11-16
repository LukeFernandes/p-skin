package speothos_venaticus;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import speothos_venaticus.FlatBone.TransitionBetweenAnimationsStruct;

public class IndexReadyBone extends FlatBone {
    
    private int ParentIndexf;
    
    private int FirstChildIndexf;
    
    private int NextSiblingIndexf;
    
    public IndexReadyBone(Matrix4f LM, Matrix4f CM, Matrix4f RL) {
        SetLocalMatrix(LM);
        SetCombinedMatrix(CM);
        RotationLocal = RL;
        SetOutputRotation(new Quaternionf().identity());
        TransitionBetweenAnimationsHolder = new TransitionBetweenAnimationsStruct();
    }
    
    public IndexReadyBone(Matrix4f LM, Matrix4f CM, Matrix4f RL, Matrix4f PL) {
    	 SetLocalMatrix(LM);
    	 SetCombinedMatrix(CM);
        RotationLocal = RL;
        PositionLocal = PL;
        SetOutputRotation(new Quaternionf().identity());
        TransitionBetweenAnimationsHolder = new TransitionBetweenAnimationsStruct();
    }
    
    public final int GetParentIndex(){
            return ParentIndexf;
        }
    public final void SetParentIndex(int value) {
            ParentIndexf = value;
        }
    
    public final int GetFirstChildIndex() {
            return FirstChildIndexf;
        }
    public final void SetFirstChildIndex(int value)  {
            FirstChildIndexf = value;
        }
    
    public final int GetNextSiblingIndex() {
            return NextSiblingIndexf;
        }
    public final void SetNextSiblingIndex(int value) {
            NextSiblingIndexf = value;
    }
}
