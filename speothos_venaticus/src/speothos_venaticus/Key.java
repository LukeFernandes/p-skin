package speothos_venaticus;

import org.joml.Vector3f;

public class Key {
    
    public Vector3f V3;
    
    //  new2016/
    public Vector3f P3;
    
    //  /
    public float Tag;
    
    public Key(float Vh, float Vp, float Vb, float Tag_) {
        V3 = new Vector3f(Vh, Vp, Vb);
        P3 = null;
        Tag = Tag_;
    }
    
    public Key(float Vh, float Vp, float Vb, float Px, float Py, float Pz, float Tag_) {
        V3 = new Vector3f(Vh, Vp, Vb);
        P3 = new Vector3f(Px, Py, Pz);
        Tag = Tag_;
    }
    
    public String ToString() {
        String s;
        s = ("Vector3f (" 
                    + (V3.toString() + (") " + ("at " + Tag ))));
        return s;
    }
}
