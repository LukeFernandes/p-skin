package speothos_venaticus;

//import org.w3c.dom.Document;

import org.w3c.dom.Node;
//import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;

import nu.xom.*;
import speothos_venaticus.FlatBone.ExposeAnimReturn;

import java.io.File;
import java.io.IOException;

import org.joml.Matrix4f;
import org.joml.MatrixStackf;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;

public class CustomMeshA {
   
    
    public String Name;
    
    public UsageType Usage = UsageType.Skinned;
    
    private Matrix4f M;
    
    public Bone XBone = null;
    
    public IndexReadyBone[] YBone = new IndexReadyBone[14];
    
    private int tt = 1;
    
    public Matrix4f[] BoneTransformMatrices;
    
    public float[][][] Matrix2by4;
    
    public float[] Matrix2by4contig;
    
    private MatrixStackf CLMatrix = new MatrixStackf(15);
    
    public boolean Act = false;
    
    public Matrix4f HookMatrix = (new Matrix4f()).identity();
    
    public static int FrameRate;
    
    public float[] MFloats;
    
    public final float[] GetMFloats() {
            return MFloats;
    }
    
    public final int BoneNo() {
            return YBone.length;
    }
    
    enum UsageType {
        
        DefaultUsage,
        
        Standard,
        
        Skinned,
        
        WaterMove,
    }
    
    Matrix4f transport  = new Matrix4f();
    Matrix4f inner_mul  = new Matrix4f();
    Matrix4f rotquat = new Matrix4f();
    Matrix4f empty2 = new Matrix4f();
    Matrix4f mtemp = new Matrix4f();
    
    public final void TraverseHierarchyAndComposeMatrices() {
        // Here we use an ID3DXMatrixStack to multiply the transform matrices down the hierarchy. We do a depth-first search, as below, though here we use the IndexReadyBone's 'quasi hierarchy' array model. We use the pop, push and multiplymatrixlocal functions
        // to easily left multiply the local transform matrices with the parents (L*P). Now we can just load an animation for one bone and have its transform automatically applied to all its descendants.
        int i = 1;
        // new2016 /
       // Matrix4f Transport;

        //Transport = Matrix4f.Multiply(YBone[i].CombinedMatrix, Matrix4f.Multiply(Matrix4f.RotationQuaternion(YBone[i].OutputRotation), YBone[i].BindMatrix));
        rotquat.rotation(YBone[i].GetOutputRotation());
        YBone[i].GetBindMatrix().mul(rotquat, inner_mul);
        inner_mul.mul(YBone[i].GetCombinedMatrix(), CLMatrix);

        
        //CLMatrix.pushMatrix();
        //CLMatrix.LoadMatrix(Transport);
        if (YBone[i].UsesPosition == true) {
            try {
				CLMatrix.translate(YBone[i].GetOutputPosition().x, YBone[i].GetOutputPosition().y, YBone[i].GetOutputPosition().z);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        // using dual quaternions now ***
       Matrix2by4[i] = YBone[i].dualQuat.QuatTrans2UDQ(CLMatrix) ;
        // ****
        YBone[i].SetOutputMatrixFS(CLMatrix);
        
        outerloop: while (true) {
          while (!(YBone[i].GetFirstChildIndex() == 0)) {
               i = YBone[i].GetFirstChildIndex();
               //Transport = Matrix4f.Multiply(YBone[i].CombinedMatrix, Matrix4f.Multiply(Matrix4f.RotationQuaternion(YBone[i].OutputRotation), YBone[i].BindMatrix));
           
               rotquat.rotation(YBone[i].GetOutputRotation());
               YBone[i].GetBindMatrix().mul(rotquat, inner_mul);
               inner_mul.mul(YBone[i].GetCombinedMatrix(), transport);
              // CLMatrix.MultiplyMatrixLocal(Transport);
               
               CLMatrix.mul(transport, mtemp);//transport.mul(CLMatrix); //hold it in transport
               CLMatrix.pushMatrix();
               CLMatrix.set(mtemp);
            
               // using dual quaternions now ***
               Matrix2by4[i] = YBone[i].dualQuat.QuatTrans2UDQ(CLMatrix) ;
                // ****
               YBone[i].SetOutputMatrixFS(CLMatrix);
               // Console.WriteLine("THACM result is " + i.ToString)
          }

          while (YBone[i].GetNextSiblingIndex() == 0) {
                    i = YBone[i].GetParentIndex();
                    CLMatrix.popMatrix();
                    if (i == 1) {
                        //CLMatrix.LoadIdentity();
                        break outerloop; //Warning!!! Review that break works as 'Exit Do' as it could be in a nested instruction like switch
                    }
                    
                    // Console.WriteLine("THACM result is " + i.ToString)
                }
                
                if (!(YBone[i].GetNextSiblingIndex() == 0)) {
                    i = YBone[i].GetNextSiblingIndex();
                    CLMatrix.popMatrix();
                    
                    rotquat.rotation(YBone[i].GetOutputRotation());
                    YBone[i].GetBindMatrix().mul(rotquat, inner_mul);
                    inner_mul.mul(YBone[i].GetCombinedMatrix(), transport);
                    
                    //transport.mul(CLMatrix); //hold it in transport
                    CLMatrix.mul(transport, mtemp);
                    CLMatrix.pushMatrix();
                    CLMatrix.set(mtemp);
                    
                    //Transport = Matrix4f.Multiply(YBone[i].CombinedMatrix, Matrix4f.Multiply(Matrix4f.RotationQuaternion(YBone[i].OutputRotation), YBone[i].BindMatrix));
                    //CLMatrix.Push();
                    //CLMatrix.MultiplyMatrixLocal(Transport);
                    // using dual quaternions now ***
                    Matrix2by4[i] = YBone[i].dualQuat.QuatTrans2UDQ(CLMatrix) ;
                     // ****
                    YBone[i].SetOutputMatrixFS(CLMatrix);
                    // Console.WriteLine("THACM result is " + i.ToString)
                }
                
        }
    }
   
  public static Matrix4f CoreMatrixHandle(Vector3f fpv, Vector3f hpb) {
      Matrix4f matrox = (new Matrix4f()).identity();
      Matrix4f maty = (new Matrix4f()).rotationY((hpb.x * -1));
      Matrix4f matp = (new Matrix4f()).rotationX((hpb.y * -1));
      Matrix4f matr = (new Matrix4f()).rotationZ((hpb.z * -1));
      //matr*matp*maty
      matrox = maty;
      matrox.mul(matp);
      matrox.mul(matr);

      matrox.transpose(matrox);
      return matrox;
  }
  
  private class ReturnCollect {
	  Element x;
	  Bone Vx;
	  Bone V;
	  
  }
            
  private ReturnCollect MatrixHandle(Element x, Bone Vx) {
            Bone V = new Bone();
            M = null;
            
            Vector3f fpv = new Vector3f(Float.parseFloat(x.getChildren().get(0).getAttributes().get(1).getValue()),
            							Float.parseFloat(x.getChildren().get(0).getAttributes().get(2).getValue()),
            							Float.parseFloat(x.getChildren().get(0).getAttributes().get(3).getValue()) );
            Vector3f hpb = new Vector3f(Float.parseFloat(x.getChildren().get(0).getAttributes().get(13).getValue()), 
            							Float.parseFloat(x.getChildren().get(0).getAttributes().get(14).getValue()),
            							Float.parseFloat(x.getChildren().get(0).getAttributes().get(15).getValue()) );
 
            Matrix4f matrox = CoreMatrixHandle(fpv, hpb);
            System.out.println("er " + matrox.toString());
            V.RotationLocal = matrox;
            V.PositionLocal = (new Matrix4f()).translation((fpv.x * -1), (fpv.y * -1), (fpv.z * -1));
            // new2016;;
           // V.LocalMatrix = Matrix4f.Multiply(Matrix4f.Translation((fpv.X * -1), (fpv.Y * -1), (fpv.Z * -1)), matrox);
            Matrix4f tempmatrix = new Matrix4f();
            Matrix4f tmatrix = new Matrix4f().translation((fpv.x * -1), (fpv.y * -1), (fpv.z * -1));
            matrox.mul(tmatrix, tempmatrix);
            V.SetLocalMatrix(tempmatrix);
            if (Vx != null) {
                //V.CombinedMatrix = (Vx.CombinedMatrix * V.LocalMatrix);
            	V.GetLocalMatrix().mul(Vx.GetCombinedMatrix(), V.CM); // tempmatrix );
            	V.CM.invert(V.BM);
            	//V.SetCombinedMatrix(tempmatrix);

            }
            else {  
            V.SetCombinedMatrix(V.GetLocalMatrix());
            } 
            System.out.println("a " + V.GetCombinedMatrix().toString() ); 
            ReturnCollect returning = new ReturnCollect();
            returning.x = x; returning.Vx = Vx; returning.V = V;
            return returning;
            
  }
            
 private void GetRawAnimationInformation(Element x, IndexReadyBone y) throws Exception { //both ref!!1
            if (x.getChildren().get(1).getChildren().isEmpty() == false) {
                y.HasData = true;
                Element ph = x.getChildren().get(1);
                System.out.println("Indices: " + y.GetParentIndex());
                y.AnimationRawData1 = new ArrayList<Key>();
                int i = 0;
                int Aset = 0;
                do
                {
                    if ( Character.getNumericValue(ph.getChildren().get(i).getName().toCharArray()[2]) 
                                == (Aset + 1) ) {
                        Aset = (Aset + 1);
                    }
                    
                    // new2016 in order to add position data to Bone1 /
                    if ( (x.getName() == "Bone1") && (ph.getChildren().get(i).getAttributes().size() > 4) ) {
                        y.AnimationRawData1.add(new Key( Float.parseFloat(ph.getChildren().get(i).getAttributes().get(1).getValue()), Float.parseFloat(ph.getChildren().get(i).getAttributes().get(2).getValue()), 
                        								Float.parseFloat(ph.getChildren().get(i).getAttributes().get(3).getValue()), Float.parseFloat(ph.getChildren().get(i).getAttributes().get(4).getValue()),
                        								Float.parseFloat(ph.getChildren().get(i).getAttributes().get(5).getValue()), Float.parseFloat(ph.getChildren().get(i).getAttributes().get(6).getValue()), 
														Float.parseFloat(ph.getChildren().get(i).getAttributes().get(0).getValue()) ));
                        y.UsesPosition = true;
                        y.SetOutputPosition(new Vector3f());
                    }
                    else {
                        // End If (/removed)
                        y.AnimationRawData1.add(new Key(Float.parseFloat(ph.getChildren().get(i).getAttributes().get(1).getValue()),
                        								Float.parseFloat(ph.getChildren().get(i).getAttributes().get(2).getValue()),
                        								Float.parseFloat(ph.getChildren().get(i).getAttributes().get(3).getValue()),
                        								Float.parseFloat(ph.getChildren().get(i).getAttributes().get(0).getValue()) ));
                        // (/retained)
                    }
                    
                    //  /
                    i++;
                } while ( (i < ph.getChildren().size()) && (ph.getChildren().get(i).getName() != "Keys2") );
                
                y.AnimationRawData1.trimToSize();
                ExposeAnimReturn temp = y.ExposeAnim(FlatBone.CycleData.Infinite, y.AnimationRawData1, y.AnimationData1);
                y.AnimationRawData1 = temp.ARD;
                y.AnimationData1 = temp.AD;
                y.AnimationDataX = y.AnimationData1;
            }
            
            // new916 for keys 2 ***********
            if ( (x.getChildren().get(1).getChildren().isEmpty() == false) && (getLastSibling(x.getChildren().get(1).getChildren().get(0)).getName() == "Keys2") ) {
                y.HasData2 = true;
                Element ph = getLastSibling(x.getChildren().get(1).getChildren().get(0));
                y.AnimationRawData2 = new ArrayList<Key>();
                int i = 0;
                int Aset = 0;
                
                do {
                	if ( Character.getNumericValue(ph.getChildren().get(i).getName().toCharArray()[2]) 
                            == (Aset + 1) ) {
                    Aset = (Aset + 1);
                           }
                    
                    // new2016 in order to add position data to Bone1 /
                    if ( (x.getName() == "Bone1")  && (ph.getChildren().get(i).getAttributes().size() > 4)) {
                    	  y.AnimationRawData2.add(new Key( Float.parseFloat(ph.getChildren().get(i).getAttributes().get(1).getValue()), Float.parseFloat(ph.getChildren().get(i).getAttributes().get(2).getValue()), 
  														   Float.parseFloat(ph.getChildren().get(i).getAttributes().get(3).getValue()), Float.parseFloat(ph.getChildren().get(i).getAttributes().get(4).getValue()),
  														   Float.parseFloat(ph.getChildren().get(i).getAttributes().get(5).getValue()), Float.parseFloat(ph.getChildren().get(i).getAttributes().get(6).getValue()), 
  														   Float.parseFloat(ph.getChildren().get(i).getAttributes().get(0).getValue()) ));
                    	  y.UsesPosition = true;
                    }
                    else {
                        // End If (/removed)
                        y.AnimationRawData2.add(new Key(Float.parseFloat(ph.getChildren().get(i).getAttributes().get(1).getValue()),
								Float.parseFloat(ph.getChildren().get(i).getAttributes().get(2).getValue()),
								Float.parseFloat(ph.getChildren().get(i).getAttributes().get(3).getValue()),
								Float.parseFloat(ph.getChildren().get(i).getAttributes().get(0).getValue()) ));
                        // (/retained)
                    }
                    
                    //  /
                    i++;
                } while (i < ph.getChildren().size());
                
                y.AnimationRawData2.trimToSize();
                ExposeAnimReturn temp = y.ExposeAnim(FlatBone.CycleData.Infinite, y.AnimationRawData2, y.AnimationData2);
                y.AnimationRawData2 = temp.ARD;
                y.AnimationData2 = temp.AD;
                y.AnimationDataX = y.AnimationData2;
            }
  }
            
            // ***************
 private void ConstructHierarchy(Element x) throws Exception {
            // This subroutine performs a depth-first search of the XML bone tree inside the PSC file, building the FlatBone derived Bone to build up a mutually linked object hierarchy.
            Bone B = null;
            int Yi = 1;
            // Bone list starts at one, not zero.
            System.out.println(x.getName());
            ReturnCollect RC = MatrixHandle(x, null); B = RC.V; x = RC.x; 
            XBone = B;
            YBone[Yi] = new IndexReadyBone(XBone.GetLocalMatrix(), XBone.GetCombinedMatrix(), XBone.RotationLocal, XBone.PositionLocal);
            YBone[Yi].SetParentIndex(-1);
            System.out.println("TTX" 
                            + Yi + ("          " + x.getChildren().get(0).getAttributes().get(0).getValue() ));
            // new2016 /
            if (x.getName() == "Bone1") {
                GetRawAnimationInformation(x, YBone[Yi]);
            }
            
            //  /
            do 
             {
                if (x.getChildren().size() > 2 && x.getChildren().get(2) != null) {

                    // If this bone has a third XML/PSC child, i.e. if it has any child bones at all.
                  do
                  {
                        x = x.getChildren().get(2);
                        Yi++;
                        System.out.println(x.getName());
                        RC = MatrixHandle(x, XBone); B = RC.V; x = RC.x; XBone = RC.Vx;
                        XBone.CreateChild(B);
                        XBone = XBone.FirstChild;
                        YBone[Yi] = new IndexReadyBone(XBone.GetLocalMatrix(), XBone.GetCombinedMatrix(), XBone.RotationLocal);
                        YBone[Yi - 1].SetFirstChildIndex(Yi);
                        YBone[Yi].SetParentIndex(Yi - 1);
                        System.out.println("TTX" 
                                        + Yi + "          " + x.getChildren().get(0).getAttributes().get(0).getValue() );
                        GetRawAnimationInformation(x, YBone[Yi]);
                    } while (x.getChildren().size() > 2 && x.getChildren().get(2)  != null) ;
                    
                }
                else if (getNextSibling(x) == null) {
                    int upcounter = 0;
                    while ( (getNextSibling(x) == null) && !(x.getName() == "Bone1") ) {
                        upcounter++;
                        x = x.getParentElement();
                        XBone = XBone.Parent;
                        System.out.println("Up " 
                                        + x.getName() + "  ");
                    }
                    
                    if (!(x.getName() == "Bone1")) {
                        x = getNextSibling(x);
                        Yi++;
                        System.out.println(x.getName());
                        RC = MatrixHandle(x, XBone.Parent); B = RC.V; x = RC.x; XBone.Parent = RC.Vx;
                        XBone.CreateSibling(B);
                        XBone = XBone.NextSibling;
                        YBone[Yi] = new IndexReadyBone(XBone.GetLocalMatrix(), XBone.GetCombinedMatrix(), XBone.RotationLocal);
                        YBone[(Yi - 1 - upcounter)].SetNextSiblingIndex(Yi);
                        YBone[Yi].SetParentIndex(YBone[(Yi - 1 - upcounter)].GetParentIndex());
                        System.out.println("TTX" 
                                        + Yi + "          " + x.getChildren().get(0).getAttributes().get(0).getValue());
                        GetRawAnimationInformation(x, YBone[Yi]);
                    }
                    
                }
                else if ( (getNextSibling(x) != null)  && !(x.getName() == "Bone1") ) {
                    x = getNextSibling(x);
                    Yi++;
                    System.out.println(x.getName());
                    RC = MatrixHandle(x, XBone.Parent); B = RC.V; x = RC.x; XBone.Parent = RC.Vx;
                    XBone.CreateSibling(B);
                    XBone = XBone.NextSibling;
                    YBone[Yi] = new IndexReadyBone(XBone.GetLocalMatrix(), XBone.GetCombinedMatrix(), XBone.RotationLocal);
                    YBone[Yi - 1].SetNextSiblingIndex(Yi);
                    YBone[Yi].SetParentIndex(YBone[Yi - 1].GetParentIndex());
                    System.out.println("TTX" 
                                    + Yi + "          " + x.getChildren().get(0).getAttributes().get(0).getValue());
                    GetRawAnimationInformation(x, YBone[Yi]);
                }
                
            } while ( !(x.getName() == "Bone1") );
 }
 
 public static Element getNextSibling(org.jdom2.Element current) {
	  Element parent = current.getParentElement();
	  if (parent == null) return null;
	  int index = parent.getChildren().indexOf(current);
	  if (index+1 == parent.getChildren().size()) return null;
	  return parent.getChildren().get(index+1);
	}
 
 public static Element getPreviousSibling(org.jdom2.Element current) {
	  Element parent = current.getParentElement();
	  if (parent == null) return null;
	  int index = parent.getChildren().indexOf(current);
	  if (index-1 == -1) return null;
	  return parent.getChildren().get(index-1);
	}
 public static Element getLastSibling(org.jdom2.Element current) {
	  Element parent = current.getParentElement();
	  if (parent == null) return null;
	  int ccount = parent.getChildren().size();
	  if (ccount == 0) return null;
	  return parent.getChildren().get(ccount - 1);
	}
            
 public void Load(String file) throws Exception {
	 
		 File mfile = new File(file);
			Node xn = null;
	        
		 
		 SAXBuilder builder = new SAXBuilder();
		 Document document = null;
		 Element elem = null;
		 try {

				 document = (Document) builder.build(mfile);
			     elem = document.getRootElement();
				
		 }
		 catch (Exception e) {
			 e.printStackTrace();
		 }
		 Element xn1 = getLastSibling(elem.getChildren().get(0));
            
            if (elem.getChildren().size() == 9) {
                // does this psc file have bones?
                Usage = UsageType.Skinned;
                BoneTransformMatrices = new Matrix4f[41];
                //dual quats
                Matrix2by4 = new float[41][2][4];
                Matrix2by4contig = new float[200];
                xn1 = getPreviousSibling(getPreviousSibling(xn1)).getChildren().get(0);
                // Get Bone 1
                System.out.println("derf" + xn1.getChildren().size());
                ConstructHierarchy(xn1);
                
            }
 }
            
  public void StartTransitionAtoB(int _TransitionTime) {
            for (int BoneNo = 1; (BoneNo <= (YBone.length - 1)); BoneNo++) {
                if (YBone[BoneNo] != null) {
               
                    YBone[BoneNo].TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType = FlatBone.TransitionBAT.YetToStart;
                    YBone[BoneNo].TransitionBetweenAnimationsHolder.Direction = FlatBone.DirectionT.AtoB;
                    YBone[BoneNo].TransitionBetweenAnimationsHolder.TransitionTime = _TransitionTime;
                }
            }
                
            }
            
  public void StartTransitionBtoA(int _TransitionTime) {
            for (int BoneNo = 1; (BoneNo <= (YBone.length - 1)); BoneNo++) {
                if (YBone[BoneNo] != null) {
                    YBone[BoneNo].TransitionBetweenAnimationsHolder.TransitionBetweenAnimationsType = FlatBone.TransitionBAT.YetToStart;
                    YBone[BoneNo].TransitionBetweenAnimationsHolder.Direction = FlatBone.DirectionT.BtoA;
                    YBone[BoneNo].TransitionBetweenAnimationsHolder.TransitionTime = _TransitionTime;
                }
                
            }
  }
            
    public void AddAnimationTrack(String Filename) throws Exception {
            CustomMeshA c = new CustomMeshA(Filename, FrameRate);
            if (!(YBone.length == c.YBone.length)) {
                throw new Exception("The supplied filename represents a model with a different no. of bones");
            }
            
            for (int BoneNo = 1; (BoneNo 
                        <= (YBone.length - 1)); BoneNo++) {
                if (YBone[BoneNo] != null) {
                    YBone[BoneNo].AnimationData2 = c.YBone[BoneNo].AnimationData1;
                    YBone[BoneNo].HasData2 = true;
                }
                
            }
    }
            
     public CustomMeshA RecalculateAnimations() {
    	 for (int BoneNo = 0; (BoneNo <= (YBone.length - 1)); BoneNo++) {

         	if  (YBone[BoneNo] != null) {//&& YBone[BoneNo].HasData == true) {
                 YBone[BoneNo].DeployAnim(0);
             }

             
         }
            this.TraverseHierarchyAndComposeMatrices();
            for (int BoneNo = 0; (BoneNo <= (YBone.length - 1)); BoneNo++) {
            	if (BoneNo == 0)
            		BoneTransformMatrices[BoneNo] = YBone[1].identity;
            	else if  (YBone[BoneNo] != null) { //&& YBone[BoneNo].HasData == true) {
                    BoneTransformMatrices[BoneNo] = YBone[BoneNo].GetOutputMatrix();
                }
                //else if (YBone[BoneNo] != null && YBone[BoneNo].HasData == false) {
                //	BoneTransformMatrices[BoneNo] = YBone[1].identity;
                //}
                
            }
            
            ArrayList<Float> ExportSingleCompound = new ArrayList<Float>();
            float[] MatrixIdentity = new float[16];
            YBone[1].identity.get(MatrixIdentity);
            
            for (int r = 0; (r 
                        <= (MatrixIdentity.length - 1)); r++) {
                ExportSingleCompound.add(MatrixIdentity[r]);
            }

            for (int i = 1; (i 
                        <= (YBone.length - 1)); i++) {
                float[] TempSingles = new float[16];
                BoneTransformMatrices[i].get(TempSingles);
                for (int e = 0; (e 
                            <= (TempSingles.length - 1)); e++) {
                    ExportSingleCompound.add(TempSingles[e]);
                }
                
            }

            ExportSingleCompound.trimToSize();
            float[] ReturnArray = new float[
                    ExportSingleCompound.size()];
            for (int b = 0; (b 
                        <= (ExportSingleCompound.size() - 1)); b++) {
                ReturnArray[b] = (float)(ExportSingleCompound.get(b));
            }

            MFloats = ReturnArray;
            
            //deal with Matrix2b4contig and dual quaternions
            for (int i = 0; i < 8; i++) Matrix2by4contig[i] = 0f;
            for (int i = 1; i < YBone.length; i++) {
            	for (int y = 0; y < 4; y++) {
            	   int count = (i*8) + y;
            	   Matrix2by4contig[count] = Matrix2by4[i][0][y];
            	}
            	for (int y = 4; y < 8; y++) {
             	   int count = (i*8) + y;
             	   Matrix2by4contig[count] = Matrix2by4[i][1][y-4];
             	}
            }
            
            
            return this;
           
     }
     
     public CustomMeshA RR(int rep) {
    	 CustomMeshA c = null;
    	 for (int i = 0; i < rep; i++) {
    	 c = RecalculateAnimations();
     }
    	 return c;
     }
            
      public CustomMeshA (String file, int _FrameRate) throws Exception {
            FrameRate = _FrameRate;
            Load(file);
            //Name = IO.Path.GetFileNameWithoutExtension(file);
            }

        
        
    
}