package speothos_venaticus;

public class Bone extends FlatBone {
    
    public Bone Parent;
    
    public Bone FirstChild;
    
    public Bone NextSibling;
    
    public final void CreateChild(Bone B) {
        FirstChild = B;
        FirstChild.Parent = this;
    }
    
    public final void CreateSibling(Bone B) {
        NextSibling = B;
        NextSibling.Parent = Parent;
    }
}