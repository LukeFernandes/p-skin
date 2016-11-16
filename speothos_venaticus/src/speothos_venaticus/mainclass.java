/*package speothos_venaticus;

import org.joml.Matrix4f;

	// TODO: Option Explicit On ... Warning!!! not translated
	
	// Imports Microsoft.DirectX
	// Imports Microsoft.DirectX.Direct3D
	// Imports Microsoft.DirectX.Direct3D.D3DX
	//  Event argument class for the AnimationChange event

 abstract class FlatBone {
	    
	    private Matrix4f LM;
	    
	    private Matrix4f CM;
	    
	    private Matrix4f BM;
	    
	    public Matrix4f RotationLocal;
	    
	    // new2016 /
	    public Matrix4f PositionLocal;
	    
	    // /
	    public java.Util.ArrayList<Key> AnimationRawData;
	    
	    // Key
	    public System.Collections.ArrayList AnimationData;
	    
	    // AdvancedKey
	    private float Measure;
	    
	    private Quaternion Ident = Quaternion.Identity;
	    
	    private Quaternion Outputq;
	    
	    private Vector3 Outputp;
	    
	    private Matrix4f Outputm;
	    
	    private float FrameTracker;
	    
	    private int SectionTracker;
	    
	    public boolean HasData = false;
	    
	    public boolean UsesPosition = false;
	    
	    private CycleData CycleType = CycleData.OneCycle;
	    
	    public delegate void CustomACDelegate(object sender, CustomACEventArgs e);
	    
	    public event CustomACDelegate AnimationChange;
	    
	    //  Raise the OAC event
	    private final void OnAnimationChange(int Position) {
	        AnimationChange(this, new CustomACEventArgs());
	    }
	    
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
	    }
	    
	    public FlatBone(Matrix4f LM, Matrix4f CM, Matrix4f RL, Matrix4f LL) {
	        // (/modified)
	        LocalMatrix4f = LM;
	        CombinedMatrix4f = CM;
	        RotationLocal = RL;
	        PositionLocal = LL;
	        // new2016;;
	        Outputq = Quaternion.Identity;
	    }
	    
	    public FlatBone() {
	        LocalMatrix4f = (new Matrix4f).identity();
	        CombinedMatrix4f = Matrix4f.Identity;
	        Outputq = Quaternion.Identity;
	        RotationLocal = Matrix4f.Identity;
	        PositionLocal = Matrix4f.Identity;
	        // new2016;;
	    }
	    
	    public final Quaternion OutputRotation {
	        get {
	            return Outputq;
	        }
	        set {
	            Outputq = value;
	        }
	    }
	    
	    public final Vector3 OutputPosition {
	        get {
	            if ((UsesPosition == true)) {
	                return Outputp;
	            }
	            else {
	                throw new Exception("This is not a bone that records position changes");
	            }
	            
	        }
	        set {
	            if ((UsesPosition == true)) {
	                Outputp = value;
	            }
	            else {
	                throw new Exception("This is not a bone that records position changes");
	            }
	            
	        }
	    }
	    
	    public final Matrix4f LocalMatrix4f {
	        get {
	            return LM;
	        }
	        set {
	            LM = value;
	        }
	    }
	    
	    public final Matrix4f BindMatrix4f {
	        get {
	            return BM;
	        }
	        set {
	            BM = value;
	        }
	    }
	    
	    public final Matrix4f CombinedMatrix4f {
	        get {
	            return CM;
	        }
	        set {
	            CM = value;
	            BM = Matrix4f.Invert(CM);
	        }
	    }
	    
	    public final Matrix4f OutputMatrix4f {
	        get {
	            // Outputm = CombinedMatrix4f * Matrix4f.RotationQuaternion(Outputq) * Matrix4f.Invert(CombinedMatrix4f)
	            return Outputm;
	        }
	        set {
	            Outputm = value;
	        }
	    }
	    
	    private final Quaternion GetQuaternion(Vector3 V0, Vector3 V1, boolean Relative) {
	        switch (Relative) {
	            case true:
	                Func<Quaternion, Quaternion, Quaternion> Relativise;
	                ((Quaternion)(x0));
	                ((Quaternion)(x1));
	                ((x0 * Quaternion.Conjugate(x1)) 
	                            * -1);
	                Matrix4f WER = (Matrix4f.RotationYawPitchRoll((V1.X * -1), (V1.Y * -1), (V1.Z * -1)) * RotationLocal);
	                Matrix4f WERa = (Matrix4f.RotationYawPitchRoll((V0.X * -1), (V0.Y * -1), (V0.Z * -1)) * RotationLocal);
	                // CINEMA 4D's rotation (hpb) is unfortunately given in parent coords (since the position and rotation of every object, including that of bones, is defined relative to parent space) but set in-app in local coords; 
	                // We compensate for this via multiplication with the local (rotational) space Matrix4f to get the rotation in local space.
	                Quaternion Q1 = Quaternion.RotationMatrix4f(WER);
	                Quaternion Q0 = Quaternion.RotationMatrix4f(WERa);
	                return Relativise[Q0, Q1];
	                break;
	            case false:
	                Matrix4f WER = (Matrix4f.RotationYawPitchRoll((V1.X * -1), (V1.Y * -1), (V1.Z * -1)) * RotationLocal);
	                // CINEMA 4D's rotation (hpb) is unfortunately given in parent coords (since the position and rotation of every object, including that of bones, is defined relative to parent space) but set in-app in local coords; 
	                // We compensate for this via multiplication with the local (rotational) space Matrix4f to get the rotation in local space.
	                Quaternion Q = Quaternion.RotationMatrix4f(WER);
	                return Q;
	                break;
	        }
	    }
	    
	    // new2016 /
	    private final Vector3 GetPositionDifference(Vector3 V0, Vector3 V1) {
	        Matrix4f R = Matrix4f.Translation(V1.X, V1.Y, V1.Z);
	        // * PositionLocal
	        // (2016)CINEMA 4D's translation (xyz) is unfortunately given in parent coords (since the position and rotation of every object, including that of bones, is defined relative to parent space) but set in-app in local coords; 
	        // We compensate for this via multiplication with the local (rotational) space Matrix4f to get the rotation in local space.
	        Vector3 trans;
	        R.Decompose(null, null, trans);
	        return trans;
	    }
	    
	    // /
	    public final void ExposeAnim(CycleData c) {
	        CycleType = c;
	        System.Collections.ArrayList Key_ = AnimationRawData;
	        System.Collections.ArrayList V3c = new System.Collections.ArrayList();
	        // Loop through computing interval vector movement
	        if ((Key_.Count == 1)) {
	            CycleType = CycleData.OneKey;
	        }
	        
	        if (!(Key_[0].Tag == 0)) {
	            Key Inserted = new Key(Key_[0].V3.X, Key_[0].V3.Y, Key_[0].V3.Z, 0);
	            // Here we handle the unusual case where there is no key at the start of the animation (we must create one, a duplicate of the first key available).
	            Key_.Insert(0, Inserted);
	        }
	        
	        for (int i = 0; (i 
	                    <= (Key_.Count - 1)); i++) {
	            Console.WriteLine(Key_[i].ToString);
	            Vector3 v;
	            Vector3 va;
	            v = Key_[i].V3;
	            va = Key_[0].V3;
	            Quaternion q = this.GetQuaternion(va, v, false);
	            float interval = Key_[i].Tag;
	            // new2016 / 
	            if (!(Key_[0].P3 == null)) {
	                AdvancedKey a = new AdvancedKey(interval, q, this.GetPositionDifference(Key_[0].P3, Key_[i].P3));
	                V3c.Add(a);
	            }
	            else {
	                AdvancedKey a = new AdvancedKey(interval, q);
	                // (/retained)
	                a.V3 = v;
	                // (/retained)
	                V3c.Add(a);
	                // (/retained)
	            }
	            
	            //  /
	        }
	        
	        V3c.TrimToSize();
	        // Intervals are based on 60 fps. Divide intervals by required frames, in this case 60*5 = 300. Given an AdvancedKey x, we store the interval between x and (x+1) in the same x.
	        for (int i = 0; (i 
	                    <= (V3c.Count - 1)); i++) {
	            float interval;
	            if (!((i + 1) 
	                        == Key_.Count)) {
	                float FrameMultiplier = float.Parse(((CustomMesh.FrameRate / 60) 
	                                * 2));
	                interval = ((Key_[(i + 1)].Tag - Key_[i].Tag) 
	                            * FrameMultiplier);
	                V3c[i].Tag = interval;
	            }
	            else {
	                interval = 0;
	                V3c[i].Tag = interval;
	            }
	            
	        }
	        
	        AnimationData = V3c;
	        if ((CycleType == CycleData.InfiniteRequireHandling)) {
	            CreateRepeat(CustomMesh.FrameRate, 0.5);
	        }
	        
	        // Console.WriteLine("sd " + V3c(0).Q4.ToString + V3c(1).Q4.ToString + V3c(2).Q4.ToString)
	    }
	    
	    private final int CycleThroughAnim() {
	        QuatRenderType T;
	        if (((SectionTracker 
	                    < (AnimationData.Count - 1)) 
	                    && !(CycleType == CycleData.OneKey))) {
	            T = QuatRenderType.Normal;
	            if ((FrameTracker < AnimationData[SectionTracker].Tag)) {
	                // This is the general case. Every frame, the Measure float is incremented towards the quaternion value of the next key until the interval and FrameTracker values match.
	                FrameTracker++;
	                float.Parse(AnimationData[SectionTracker].Tag);
	            }
	            else {
	                // Handles when the next key has been reached.
	                SectionTracker++;
	                switch (SectionTracker) {
	                }
	                (AnimationData.Count - 1);
	                // Do Nothing and wait for next if.
	            }
	            
	        }
	        else {
	            // Start on next key transition immediately.
	            FrameTracker = 1;
	            float.Parse(AnimationData[SectionTracker].Tag);
	        }
	        
	    }
	}
	if ((SectionTracker 
	            == (AnimationData.Count - 1))) {
	    // Handles repeat of animation.
	    switch (CycleType) {
	        case CycleData.InfiniteRequireHandling:
	            T = QuatRenderType.StartAgain;
	            if ((FrameTracker < AnimationData[SectionTracker].Tag)) {
	                // Auto interpolate between end and start.
	                FrameTracker++;
	                float.Parse(AnimationData[SectionTracker].Tag);
	            }
	            else {
	                // Start on key0 -> key1 transition immediately.
	                SectionTracker = 0;
	                FrameTracker = 1;
	                float.Parse(AnimationData[SectionTracker].Tag);
	            }
	            
	            break;
	        case CycleData.Infinite:
	            T = QuatRenderType.StartAgain;
	            // Start on key0 -> key1 transition immediately.
	            SectionTracker = 0;
	            FrameTracker = 1;
	            float.Parse(AnimationData[SectionTracker].Tag);
	            break;
	        case CycleData.OneCycle:
	            // Do nothing except:
	            T = QuatRenderType.NoAction;
	            // Renders out of scope for DeployAnim()
	            break;
	        case CycleData.OneKey:
	            // Do this
	            T = QuatRenderType.SetQuat;
	            break;
	    }
	}

	return T;
	EndFunctionEndclass Public {
	    
	    class IndexReadyBone extends FlatBone {
	        
	        private int ParentIndexf;
	        
	        private int FirstChildIndexf;
	        
	        private int NextSiblingIndexf;
	        
	        public IndexReadyBone(Matrix4f LM, Matrix4f CM, Matrix4f RL) {
	            LocalMatrix4f = LM;
	            CombinedMatrix4f = CM;
	            RotationLocal = RL;
	            OutputRotation = Quaternion.Identity;
	        }
	        
	        public IndexReadyBone(Matrix4f LM, Matrix4f CM, Matrix4f RL, Matrix4f PL) {
	            LocalMatrix4f = LM;
	            CombinedMatrix4f = CM;
	            RotationLocal = RL;
	            PositionLocal = PL;
	            OutputRotation = Quaternion.Identity;
	        }
	        
	        public final int ParentIndex {
	            get {
	                return ParentIndexf;
	            }
	            set {
	                ParentIndexf = value;
	            }
	        }
	        
	        public final int FirstChildIndex {
	            get {
	                return FirstChildIndexf;
	            }
	            set {
	                FirstChildIndexf = value;
	            }
	        }
	        
	        public final int NextSiblingIndex {
	            get {
	                return NextSiblingIndexf;
	            }
	            set {
	                NextSiblingIndexf = value;
	            }
	        }
	    }
	    
	    public struct ByteFour {
	        
	        private byte d;
	        
	        public ByteFour(SByte ai, SByte bi, SByte ci, SByte di) {
	            a = byte.Parse(ai);
	            b = byte.Parse(bi);
	            c = byte.Parse(ci);
	            d = byte.Parse(di);
	        }
	    }
	    
	    public class Key {
	        
	        public Vector3 V3;
	        
	        //  new2016/
	        public Vector3 P3;
	        
	        //  /
	        public float Tag;
	        
	        public Key(float Vh, float Vp, float Vb, float Tag_) {
	            V3 = new Vector3(Vh, Vp, Vb);
	            P3 = null;
	            Tag = Tag_;
	        }
	        
	        public Key(float Vh, float Vp, float Vb, float Px, float Py, float Pz, float Tag_) {
	            V3 = new Vector3(Vh, Vp, Vb);
	            P3 = new Vector3(Px, Py, Pz);
	            Tag = Tag_;
	        }
	        
	        public override string ToString() {
	            string s;
	            s = ("Vector3 (" 
	                        + (V3.ToString + (") " + ("at " + Tag.ToString))));
	            return s;
	        }
	    }
	    
	    public class AdvancedKey extends Key {
	        
	        public Quaternion Q4;
	        
	        public AdvancedKey(float Vh, float Vp, float Vb, float Tag_) {
	            super(Vh, Vp, Vb, Tag_);
	            
	            Q4 = Quaternion.Identity;
	        }
	        
	        public AdvancedKey(float Tag_, Quaternion q) {
	            super(null, null, null, Tag_);
	            
	            Q4 = q;
	        }
	        
	        // new2016 /
	        public AdvancedKey(float Tag_, Quaternion q, Vector3 Translation) {
	            super(null, null, null, Tag_);
	            
	            Q4 = q;
	            super.P3 = Translation;
	        }
	    }
	    
	    public class CustomMesh {
	        
	        private Device device;
	        
	        public string Name;
	        
	        public VertexBuffer v;
	        
	        public IndexBuffer ind;
	        
	        private Vector4[] VertexArray;
	        
	        private Int16[] IndexArray;
	        
	        public Vector2[] UV;
	        
	        public Vector3[] Normals;
	        
	        public Vector3[] Tangents;
	        
	        public Vector4[] BlendWeights;
	        
	        public ByteFour[] BlendIndices;
	        
	        public int numPoints;
	        
	        public int numPolygons;
	        
	        public UsageType Usage = UsageType.Skinned;
	        
	        public TransMate TransformationMatrices;
	        
	        private Matrix4f M;
	        
	        public Bone XBone = null;
	        
	        private int tt = 1;
	        
	        public Matrix4f[] BoneTransformMatrices;
	        
	        private Matrix4fStack CLMatrix4f = new Matrix4fStack();
	        
	        public boolean Act = false;
	        
	        public Matrix4f HookMatrix4f = Matrix4f.Identity;
	        
	        public static int FrameRate;
	        
	        public float[] MFloats;
	        
	        public final float[] GetMFloats {
	            get {
	                return MFloats;
	            }
	            set {
	                MFloats = value;
	            }
	        }
	        
	        public final int BoneNo {
	            get {
	                return YBone.Count;
	            }
	        }
	        
	        enum UsageType {
	            
	            DefaultUsage = 0,
	            
	            Standard = 1,
	            
	            Skinned = 2,
	            
	            WaterMove = 3,
	        }
	        
	        // *****
	        public struct TransMate {
	            
	            private Matrix4f Translation;
	            
	            private Matrix4f Rotation;
	            
	            private Matrix4f Scaling;
	            
	            private Matrix4f Total;
	            
	            public TransMate(Matrix4f t, Matrix4f r, Matrix4f s) {
	                Translation = t;
	                Scaling = s;
	                Rotation = r;
	                Total = (Scaling 
	                            * (Rotation * Translation));
	            }
	        }
	        
	        private struct VBSkinStruct {
	            
	            private Vector4 v4;
	            
	            private Vector3 norm;
	            
	            private Vector2 uv;
	            
	            private Vector3 tang;
	            
	            private Vector4 blendweight;
	            
	            private ByteFour blendind;
	            
	            public VBSkinStruct(Vector4 vec, Vector3 normi, Vector2 uvi, Vector3 tangi, Vector4 blendweighti, ByteFour blendindi) {
	                v4 = vec;
	                norm = normi;
	                uv = uvi;
	                tang = tangi;
	                blendweight = blendweighti;
	                blendind = blendindi;
	            }
	        }
	        
	        private struct VBStruct {
	            
	            private Vector4 v4;
	            
	            private Vector3 norm;
	            
	            private Vector2 uv;
	            
	            private Vector3 tang;
	            
	            public VBStruct(Vector4 vec, Vector3 normi, Vector2 uvi, Vector3 tangi) {
	                v4 = vec;
	                norm = normi;
	                uv = uvi;
	                tang = tangi;
	            }
	        }
	        
	        public final void TraverseHierarchyAndComposeMatrices() {
	            // Here we use an ID3DXMatrix4fStack to multiply the transform matrices down the hierarchy. We do a depth-first search, as below, though here we use the IndexReadyBone's 'quasi hierarchy' array model. We use the pop, push and multiplyMatrix4flocal functions
	            // to easily left multiply the local transform matrices with the parents (L*P). Now we can just load an animation for one bone and have its transform automatically applied to all its descendants.
	            int i = 1;
	            // new2016 /
	            Matrix4f Transport;
	            // (/retained but separated)
	            //  If YBone(i).UsesPosition = True Then
	            // Dim TransRot As Matrix4f = Matrix4f.Multiply(Matrix4f.Translation(YBone(i).OutputPosition), Matrix4f.RotationQuaternion(YBone(i).OutputRotation))
	            // Transport = Matrix4f.Multiply(YBone(i).CombinedMatrix4f, Matrix4f.Multiply(TransRot, YBone(i).BindMatrix4f))
	            // Else
	            Transport = Matrix4f.Multiply(YBone(i).CombinedMatrix4f, Matrix4f.Multiply(Matrix4f.RotationQuaternion(YBone(i).OutputRotation), YBone(i).BindMatrix4f));
	            // (/retained)
	            // End If
	            // /
	            CLMatrix4f.Push();
	            CLMatrix4f.LoadMatrix4f(Transport);
	            if ((YBone(i).UsesPosition == true)) {
	                CLMatrix4f.TranslateLocal(YBone(i).OutputPosition.X, YBone(i).OutputPosition.Y, YBone(i).OutputPosition.Z);
	            }
	            
	            YBone(i).OutputMatrix4f = CLMatrix4f.Top;
	            while (!(YBone(i).FirstChildIndex == 0)) {
	                i = YBone(i).FirstChildIndex;
	                Transport = Matrix4f.Multiply(YBone(i).CombinedMatrix4f, Matrix4f.Multiply(Matrix4f.RotationQuaternion(YBone(i).OutputRotation), YBone(i).BindMatrix4f));
	                CLMatrix4f.Push();
	                CLMatrix4f.MultiplyMatrix4fLocal(Transport);
	                YBone(i).OutputMatrix4f = CLMatrix4f.Top;
	                // Console.WriteLine("THACM result is " + i.ToString)
	                while () {
	                    while ((YBone(i).NextSiblingIndex == 0)) {
	                        i = YBone(i).ParentIndex;
	                        CLMatrix4f.Pop();
	                        if ((i == 1)) {
	                            CLMatrix4f.LoadIdentity();
	                            break; //Warning!!! Review that break works as 'Exit Do' as it could be in a nested instruction like switch
	                        }
	                        
	                        // Console.WriteLine("THACM result is " + i.ToString)
	                    }
	                    
	                    if (!(YBone(i).NextSiblingIndex == 0)) {
	                        i = YBone(i).NextSiblingIndex;
	                        CLMatrix4f.Pop();
	                        Transport = Matrix4f.Multiply(YBone(i).CombinedMatrix4f, Matrix4f.Multiply(Matrix4f.RotationQuaternion(YBone(i).OutputRotation), YBone(i).BindMatrix4f));
	                        CLMatrix4f.Push();
	                        CLMatrix4f.MultiplyMatrix4fLocal(Transport);
	                        YBone(i).OutputMatrix4f = CLMatrix4f.Top;
	                        // Console.WriteLine("THACM result is " + i.ToString)
	                    }
	                    
	                }
	                
	                ((Bone)(Matrix4fHandle(((XmlNode)(x)), ((Bone)(Vx)))));
	                Bone V = new Bone();
	                M = null;
	                Vector3 fpv = new Vector3(float.Parse(x.FirstChild.Attributes[1].Value), float.Parse(x.FirstChild.Attributes[2].Value), float.Parse(x.FirstChild.Attributes[3].Value));
	                // /
	                // Dim v0 As Vector3 = New Vector3(CSng(x.FirstChild.Attributes(1).Value), CSng(x.FirstChild.Attributes(2).Value), CSng(x.FirstChild.Attributes(3).Value))
	                // **Legacy Code
	                // Dim v1 As Vector3 = New Vector3(CSng(x.FirstChild.Attributes(4).Value), CSng(x.FirstChild.Attributes(5).Value), CSng(x.FirstChild.Attributes(6).Value))
	                Vector3 v2 = new Vector3(float.Parse(x.FirstChild.Attributes[7].Value), float.Parse(x.FirstChild.Attributes[8].Value), float.Parse(x.FirstChild.Attributes[9].Value));
	                Vector3 v3 = new Vector3(float.Parse(x.FirstChild.Attributes[10].Value), float.Parse(x.FirstChild.Attributes[11].Value), float.Parse(x.FirstChild.Attributes[12].Value));
	                // ***
	                Vector3 hpb = new Vector3(float.Parse(x.FirstChild.Attributes[13].Value), float.Parse(x.FirstChild.Attributes[14].Value), float.Parse(x.FirstChild.Attributes[15].Value));
	                // **Legacy Code
	                // M.Rows(0) = New Vector4(v1.X, v2.X, v3.X, 0.0F)
	                // M.Rows(1) = New Vector4(v1.Y, v2.Y, v3.Y, 0.0F)
	                // M.Rows(2) = New Vector4(v1.Z, v2.Z, v3.Z, 0.0F)
	                // M.Rows(3) = New Vector4(0.0F, 0.0F, 0.0F, 1.0F)
	                // **Legac Code
	                Matrix4f matrox = Matrix4f.Identity;
	                Matrix4f maty = Matrix4f.RotationY((hpb.X * -1));
	                Matrix4f matp = Matrix4f.RotationX((hpb.Y * -1));
	                Matrix4f matr = Matrix4f.RotationZ((hpb.Z * -1));
	                matrox = matr;
	                matrox = (matrox * matp);
	                matrox = (matrox * maty);
	                matrox = Matrix4f.Transpose(matrox);
	                // matrox.Rows(3) = New Vector4(v0.X, v0.Y, v0.Z, 1.0F)
	                // Console.WriteLine("bx1 " + M.ToString)
	                Console.WriteLine(("er " + matrox.ToString));
	                V.RotationLocal = matrox;
	                V.PositionLocal = Matrix4f.Translation((fpv.X * -1), (fpv.Y * -1), (fpv.Z * -1));
	                // new2016;;
	                V.LocalMatrix4f = Matrix4f.Multiply(Matrix4f.Translation((fpv.X * -1), (fpv.Y * -1), (fpv.Z * -1)), matrox);
	                if (Vx) {
	                    IsNot;
	                    null;
	                    V.CombinedMatrix4f = (Vx.CombinedMatrix4f * V.LocalMatrix4f);
	                    Console.WriteLine(("Origin: " + Vector3.Transform(new Vector3(0, 0, 0), Matrix4f.Invert(V.CombinedMatrix4f)).ToString));
	                }
	                else {
	                    
	                }
	                
	                V.CombinedMatrix4f = V.LocalMatrix4f;
	                if (Console.WriteLine(("a " + V.CombinedMatrix4f.ToString))) {
	                    return V;
	                }
	                
	                GetRawAnimationInformation(ref ((XmlNode)(x)), ref ((IndexReadyBone)(y)));
	                if ((x.ChildNodes(1).HasChildNodes == true)) {
	                    y.HasData = true;
	                    XmlNode ph = x.ChildNodes(1);
	                    Console.WriteLine(("Indices: " + y.ParentIndex.ToString));
	                    y.AnimationRawData = new ArrayList();
	                    int i = 0;
	                    int Aset = 0;
	                    for (
	                    ; (i < ph.ChildNodes.Count); 
	                    ) {
	                        if ((Convert.ToInt32(ph.ChildNodes(i).Name.Chars(2)) 
	                                    == (Aset + 1))) {
	                            Aset = (Aset + 1);
	                        }
	                        
	                        // new2016 in order to add position data to Bone1 /
	                        if (((x.Name == "Bone1") 
	                                    && (ph.ChildNodes(i).Attributes.Count > 4))) {
	                            y.AnimationRawData.Add(new Key(float.Parse(ph.ChildNodes(i).Attributes[1].Value), float.Parse(ph.ChildNodes(i).Attributes[2].Value), float.Parse(ph.ChildNodes(i).Attributes[3].Value), float.Parse(ph.ChildNodes(i).Attributes[4].Value), float.Parse(ph.ChildNodes(i).Attributes[5].Value), float.Parse(ph.ChildNodes(i).Attributes[6].Value), int.Parse(ph.ChildNodes(i).Attributes[0].Value)));
	                            y.UsesPosition = true;
	                        }
	                        else {
	                            // End If (/removed)
	                            y.AnimationRawData.Add(new Key(float.Parse(ph.ChildNodes(i).Attributes[1].Value), float.Parse(ph.ChildNodes(i).Attributes[2].Value), float.Parse(ph.ChildNodes(i).Attributes[3].Value), int.Parse(ph.ChildNodes(i).Attributes[0].Value)));
	                            // (/retained)
	                        }
	                        
	                        //  /
	                        i++;
	                    }
	                    
	                    y.AnimationRawData.TrimToSize();
	                    y.ExposeAnim(FlatBone.CycleData.Infinite);
	                }
	                
	                ConstructHierarchy(((XmlNode)(x)));
	                // This subroutine performs a depth-first search of the XML bone tree inside the PSC file, building the FlatBone derived Bone to build up a mutually linked object hierarchy.
	                Bone B = null;
	                int Yi = 1;
	                // Bone list starts at one, not zero.
	                Console.WriteLine(x.Name);
	                B = Matrix4fHandle(x, null);
	                XBone = B;
	                YBone(Yi) = new IndexReadyBone(XBone.LocalMatrix4f, XBone.CombinedMatrix4f, XBone.RotationLocal, XBone.PositionLocal);
	                YBone(Yi).ParentIndex = -1;
	                Console.WriteLine(("TTX" 
	                                + (Yi.ToString + ("          " + x.ChildNodes(0).Attributes[0].Value.ToString))));
	                // new2016 /
	                if ((x.Name == "Bone1")) {
	                    GetRawAnimationInformation(x, YBone(Yi));
	                }
	                
	                //  /
	                for (
	                ; ((x.Name == "Bone1") 
	                            == false); 
	                ) {
	                    if (x.ChildNodes(2)) {
	                        IsNot;
	                        null;
	                        // If this bone has a third XML/PSC child, i.e. if it has any child bones at all.
	                        for (
	                        ; ((x.ChildNodes(2) == null) 
	                                    == false); 
	                        ) {
	                            x = x.ChildNodes(2);
	                            Yi++;
	                            Console.WriteLine(x.Name);
	                            B = Matrix4fHandle(x, XBone);
	                            XBone.CreateChild(B);
	                            XBone = XBone.FirstChild;
	                            YBone(Yi) = new IndexReadyBone(XBone.LocalMatrix4f, XBone.CombinedMatrix4f, XBone.RotationLocal);
	                            YBone((Yi - 1)).FirstChildIndex = Yi;
	                            YBone(Yi).ParentIndex = (Yi - 1);
	                            Console.WriteLine(("TTX" 
	                                            + (Yi.ToString + ("          " + x.ChildNodes(0).Attributes[0].Value.ToString))));
	                            GetRawAnimationInformation(x, YBone(Yi));
	                        }
	                        
	                    }
	                    else if ((x.NextSibling == null)) {
	                        int upcounter = 0;
	                        while (((x.NextSibling == null) 
	                                    && !(x.Name == "Bone1"))) {
	                            upcounter++;
	                            x = x.ParentNode;
	                            XBone = XBone.Parent;
	                            Console.Write(("Up " 
	                                            + (x.Name + "  ")));
	                        }
	                        
	                        if (!(x.Name == "Bone1")) {
	                            x = x.NextSibling;
	                            Yi++;
	                            Console.WriteLine(x.Name);
	                            B = Matrix4fHandle(x, XBone.Parent);
	                            XBone.CreateSibling(B);
	                            XBone = XBone.NextSibling;
	                            YBone(Yi) = new IndexReadyBone(XBone.LocalMatrix4f, XBone.CombinedMatrix4f, XBone.RotationLocal);
	                            YBone((Yi - (1 - upcounter))).NextSiblingIndex = Yi;
	                            YBone(Yi).ParentIndex = YBone((Yi - (1 - upcounter))).ParentIndex;
	                            Console.WriteLine(("TTX" 
	                                            + (Yi.ToString + ("          " + x.ChildNodes(0).Attributes[0].Value.ToString))));
	                            GetRawAnimationInformation(x, YBone(Yi));
	                        }
	                        
	                    }
	                    else if (x.NextSibling) {
	                        IsNot;
	                        (null 
	                                    && !x.Name) = "Bone1";
	                        x = x.NextSibling;
	                        Yi++;
	                        Console.WriteLine(x.Name);
	                        B = Matrix4fHandle(x, XBone.Parent);
	                        XBone.CreateSibling(B);
	                        XBone = XBone.NextSibling;
	                        YBone(Yi) = new IndexReadyBone(XBone.LocalMatrix4f, XBone.CombinedMatrix4f, XBone.RotationLocal);
	                        YBone((Yi - 1)).NextSiblingIndex = Yi;
	                        YBone(Yi).ParentIndex = YBone((Yi - 1)).ParentIndex;
	                        Console.WriteLine(("TTX" 
	                                        + (Yi.ToString + ("          " + x.ChildNodes(0).Attributes[0].Value.ToString))));
	                        GetRawAnimationInformation(x, YBone(Yi));
	                    }
	                    
	                }
	                
	                Load(((string)(file)));
	                // Dim sys As New IO.StreamReader(file)
	                // Dim b As Byte()
	                // sys.BaseStream.Read(b, 0, CInt(sys.BaseStream.Length))
	                // Do
	                //     s.
	                //  Loop Until sys.ReadLine() = "</Object>"
	                // Dim m As Long = sys.BaseStream.Position
	                // Dim mp As New IO.StreamWriter(sys.BaseStream)
	                // Dim b As IO.Stream = sys.BaseStream.
	                XmlDocument xr = new XmlDocument();
	                XmlElement elem;
	                xr.Load(file);
	                elem = xr.DocumentElement;
	                Console.WriteLine(elem.Name);
	                XmlAttributeCollection attr;
	                // Dim xn As XmlNode = elem.FirstChild.NextSibling
	                XmlNode xn = elem.FirstChild;
	                attr = xn.ChildNodes.ItemOf(0).Attributes;
	                Matrix4fSet(0) = Matrix4f.Translation(Convert.ToSingle(attr[1].Value), Convert.ToSingle(attr[2].Value), Convert.ToSingle(attr[3].Value));
	                attr = xn.ChildNodes.ItemOf(1).Attributes;
	                Matrix4fSet(1) = Matrix4f.RotationYawPitchRoll((Convert.ToSingle(attr[1].Value) * -1), (Convert.ToSingle(attr[2].Value) * -1), (Convert.ToSingle(attr[3].Value) * -1));
	                attr = xn.ChildNodes.ItemOf(2).Attributes;
	                Matrix4fSet(2) = Matrix4f.Scaling(Convert.ToSingle(attr[1].Value), Convert.ToSingle(attr[2].Value), Convert.ToSingle(attr[3].Value));
	                TransformationMatrices = new TransMate(Matrix4fSet(0), Matrix4fSet(1), Matrix4fSet(2));
	                xn = elem.FirstChild.NextSibling;
	                Array.Resize(IndexArray, xn.ChildNodes.Count);
	                numPolygons = ((int)((xn.ChildNodes.Count / 3)));
	                for (int i = 0; (i 
	                            <= (xn.ChildNodes.Count - 1)); i++) {
	                    attr = xn.ChildNodes.ItemOf(i).Attributes;
	                    IndexArray(i) = Convert.ToInt16(attr[1].Value);
	                    // Console.WriteLine(Convert.ToString(IndexArray(i)))
	                }
	                
	                xn = elem.FirstChild.NextSibling.NextSibling;
	                numPoints = xn.ChildNodes.Count;
	                Array.Resize(VertexArray, xn.ChildNodes.Count);
	                for (int i = 0; (i 
	                            <= (xn.ChildNodes.Count - 1)); i++) {
	                    attr = xn.ChildNodes.ItemOf(i).Attributes;
	                    VertexArray(i) = new Vector4(Convert.ToSingle(attr[1].Value), Convert.ToSingle(attr[2].Value), Convert.ToSingle(attr[3].Value), 1.0F);
	                }
	                
	                xn = elem.FirstChild.NextSibling.NextSibling.NextSibling;
	                Array.Resize(UV, xn.ChildNodes.Count);
	                for (int i = 0; (i 
	                            <= (xn.ChildNodes.Count - 1)); i++) {
	                    attr = xn.ChildNodes.ItemOf(i).Attributes;
	                    UV[i].X = Convert.ToSingle(attr[1].Value);
	                    UV[i].Y = Convert.ToSingle(attr[2].Value);
	                    // Console.WriteLine(Convert.ToString(UV(i).X) + " " + Convert.ToString(UV(i).Y))
	                }
	                
	                xn = elem.FirstChild.NextSibling.NextSibling.NextSibling.NextSibling;
	                Array.Resize(Normals, xn.ChildNodes.Count);
	                for (int i = 0; (i 
	                            <= (xn.ChildNodes.Count - 1)); i++) {
	                    attr = xn.ChildNodes.ItemOf(i).Attributes;
	                    Normals(i) = new Vector3(Convert.ToSingle(attr[1].Value), Convert.ToSingle(attr[2].Value), Convert.ToSingle(attr[3].Value));
	                }
	                
	                xn = elem.FirstChild.NextSibling.NextSibling.NextSibling.NextSibling.NextSibling;
	                Array.Resize(Tangents, xn.ChildNodes.Count);
	                for (int i = 0; (i 
	                            <= (xn.ChildNodes.Count - 1)); i++) {
	                    attr = xn.ChildNodes.ItemOf(i).Attributes;
	                    Tangents(i) = new Vector3(Convert.ToSingle(attr[1].Value), Convert.ToSingle(attr[2].Value), Convert.ToSingle(attr[3].Value));
	                }
	                
	                if ((elem.ChildNodes.Count == 9)) {
	                    // does this psc file have bones?
	                    Usage = UsageType.Skinned;
	                    Array.Resize(Of, Matrix4f)[BoneTransformMatrices, 41];
	                    xn = xn.NextSibling.FirstChild;
	                    // Get Bone 1
	                    Console.WriteLine(("derf" + xn.ChildNodes.Count.ToString));
	                    ConstructHierarchy(xn);
	                    xn = xn.ParentNode.NextSibling;
	                    Array.Resize(BlendIndices, xn.ChildNodes.Count);
	                    for (int i = 0; (i 
	                                <= (xn.ChildNodes.Count - 1)); i++) {
	                        attr = xn.ChildNodes.ItemOf(i).Attributes;
	                        BlendIndices(i) = new ByteFour(Convert.ToSByte(attr[1].Value), Convert.ToSByte(attr[2].Value), Convert.ToSByte(attr[3].Value), Convert.ToSByte(attr[4].Value));
	                        Console.WriteLine(("BI " 
	                                        + (attr[1].Value + (" " 
	                                        + (attr[2].Value + (" " 
	                                        + (attr[3].Value + (" " + attr[4].Value))))))));
	                    }
	                    
	                    xn = xn.NextSibling;
	                    Array.Resize(BlendWeights, xn.ChildNodes.Count);
	                    for (int i = 0; (i 
	                                <= (xn.ChildNodes.Count - 1)); i++) {
	                        attr = xn.ChildNodes.ItemOf(i).Attributes;
	                        BlendWeights(i) = new Vector4(Convert.ToSingle(attr[1].Value), Convert.ToSingle(attr[2].Value), Convert.ToSingle(attr[3].Value), Convert.ToSingle(attr[4].Value));
	                    }
	                    
	                }
	                
	                RecalculateAnimations();
	                this.TraverseHierarchyAndComposeMatrices();
	                for (int BoneNo = 1; (BoneNo 
	                            <= (YBone.Count - 1)); BoneNo++) {
	                    if (YBone(BoneNo)) {
	                        IsNot;
	                        null;
	                        YBone(BoneNo).DeployAnim(null);
	                        BoneTransformMatrices(BoneNo) = YBone(BoneNo).OutputMatrix4f;
	                    }
	                    
	                }
	                
	                //  To play nice with Jni4net we need to create a giant array of singles
	                ArrayList ExportSingleCompound = new ArrayList();
	                float[] Matrix4fIdentity = Matrix4f.Identity.ToArray();
	                Console.WriteLine(Matrix4fIdentity.Count);
	                for (int r = 0; (r 
	                            <= (Matrix4fIdentity.Length - 1)); r++) {
	                    ExportSingleCompound.Add(Matrix4fIdentity[r]);
	                }
	                
	                for (int i = 1; (i 
	                            <= (BoneTransformMatrices.Length - 1)); i++) {
	                    object TempSingles = BoneTransformMatrices(i).ToArray();
	                    for (int e = 0; (e 
	                                <= (TempSingles.Length - 1)); e++) {
	                        ExportSingleCompound.Add(TempSingles[e]);
	                    }
	                    
	                }
	                
	                ExportSingleCompound.TrimToSize();
	                float[] ReturnArray = new float[] {
	                        ExportSingleCompound.Count};
	                for (int b = 0; (b 
	                            <= (ExportSingleCompound.Count - 1)); b++) {
	                    ReturnArray[b] = Convert.ToSingle(ExportSingleCompound[b]);
	                }
	                
	                MFloats = ReturnArray;
	                ((string)(Print()));
	                return "Welcome to the Bridge";
	                ((string)(InstPrint()));
	                return "Welcome to the Bridge";
	                ((string)(file));
	                ((int)(_FrameRate));
	                FrameRate = _FrameRate;
	                Load(file);
	                Name = IO.Path.GetFileNameWithoutExtension(file);
	                RemoveRubbish();
	                v.Dispose();
	                ind.Dispose();
	                CLMatrix4f.Dispose();
	                Bone;
	                FlatBone;
	                ((Bone)(Parent));
	                ((Bone)(FirstChild));
	                ((Bone)(NextSibling));
	                CreateChild(((Bone)(B)));
	                FirstChild = B;
	                FirstChild.Parent = this;
	                CreateSibling(((Bone)(B)));
	                NextSibling = B;
	                NextSibling.Parent = Parent;
	            }
	            
	        }
	    }
	}

	    
	    public final void DeployAnim(int SecondQuat) {
	        if (HasData) {
	            try {
	                int xc = this.CycleThroughAnim();
	                switch (xc) {
	                    case QuatRenderType.Normal:
	                        // new2016 /
	                        if ((UsesPosition == true)) {
	                            Vector3.Lerp(AnimationData[SectionTracker].P3, AnimationData[(SectionTracker + 1)].P3, Measure, Outputp);
	                        }
	                        
	                        // /
	                        Quaternion.Slerp(AnimationData[SectionTracker].Q4, AnimationData[(SectionTracker + 1)].Q4, Measure, Outputq);
	                        break;
	                    case QuatRenderType.StartAgain:
	                        // new2016 /
	                        if ((UsesPosition == true)) {
	                            Vector3.Lerp(AnimationData[SectionTracker].P3, AnimationData[0].P3, Measure, Outputp);
	                        }
	                        
	                        // /
	                        Quaternion.Slerp(AnimationData[SectionTracker].Q4, AnimationData[0].Q4, Measure, Outputq);
	                        break;
	                    case QuatRenderType.SetQuat:
	                        Outputq = AnimationData[0].Q4;
	                        break;
	                }
	            }
	            catch (NullReferenceException e) {
	                Console.WriteLine("No Bone Animation Data");
	            }
	            
	        }
	        
	    }
	    
	    private final void CreateRepeat(float fps, float noseconds) {
	        AnimationData[(AnimationData.Count - 1)].Tag = int.Parse((fps * noseconds));
	    }
} */
