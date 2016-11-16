package speothos_venaticus;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import clojure.java.api.Clojure;
import clojure.lang.IFn;

public class AdvancedKey extends Key {
    
    public Quaternionf Q4;
    
    class AudioMarker {
    	int audioindex;
    	int repeat;
    }
    
    AudioMarker mAudioMarker = null;
    int[] OutArray = new int[2];
    
    IFn sendm = Clojure.var("audio.clj", "send-to-agent");
    
    private void playAudio()
    {
    	OutArray[0] = mAudioMarker.audioindex;
    	OutArray[1] = mAudioMarker.repeat;
    	sendm.invoke(OutArray);
    }
    
    
    public AdvancedKey(float Vh, float Vp, float Vb, float Tag_) {
        super(Vh, Vp, Vb, Tag_);
        
        Q4 = (new Quaternionf()).identity();
    }
    
    public AdvancedKey(float Tag_, Quaternionf q) {
        super(0, 0, 0, Tag_); //java - cannot use null value types
        
        Q4 = q;
    }
    
    // new2016 /
    public AdvancedKey(float Tag_, Quaternionf q, Vector3f Translation) {
        super(0, 0, 0, Tag_); //java - cannot use null value types
        
        Q4 = q;
        super.P3 = Translation;
    }
}