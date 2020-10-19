 package com.example.user.myapplication;

 import android.Manifest;
 import android.app.Activity;
 import android.os.Build;
 import android.os.Bundle;
 import android.provider.ContactsContract;
 import android.view.View;
 import android.widget.Button;
 import android.widget.TextView;

 import org.opencv.android.CameraBridgeViewBase;
 import org.opencv.android.JavaCameraView;
 import org.opencv.android.OpenCVLoader;
 import org.opencv.core.Core;
 import org.opencv.core.CvType;
 import org.opencv.core.Mat;
 import org.opencv.core.MatOfPoint;
 import org.opencv.core.Point;
 import org.opencv.core.Rect;
 import org.opencv.core.Scalar;
 import org.opencv.imgproc.Imgproc;
 import java.util.ArrayList;
 import java.util.List;
 import static org.opencv.imgproc.Imgproc.contourArea;



 public class  MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {

     private JavaCameraView camera_view;
     private TextView textView1;
     private TextView textView2;
     private Button button;
     private int M_REQUEST_CODE = 203;
     private String[] permissions = {Manifest.permission.CAMERA};
     double[] pointx = new double[10];
     double [] pointy=new double[10];
     int j=0;


     int cnt_point=0;


     int Flag_line=0;
     int Flag_start=0;


         @Override
         protected void onCreate (Bundle savedInstanceState){
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         textView1=(TextView)super.findViewById(R.id.info);
         textView2=(TextView)super.findViewById(R.id.info2);
         button = (Button)findViewById(R.id.button);
         button.setOnClickListener(new Button.OnClickListener(){
             @Override
             public void onClick(View v) {
                 String sInfo2 = "手勢";
                 textView2.setText(sInfo2);
                 String sInfo1 = "RES";
                 textView1.setText(sInfo1);
                 j = 0;
                 pointx = new double[10];
                 pointy = new double[10];
                 cnt_point = 0;
                 Flag_start=0;
                 Flag_line=0;

             }
         });
         camera_view = (JavaCameraView) findViewById(R.id.camera_view);
         camera_view.setCvCameraViewListener(this);

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
             requestPermissions(permissions, M_REQUEST_CODE);
         }
     }

         @Override
         protected void onPause () {
         super.onPause();
         if (camera_view != null) {
             camera_view.disableView();
         }
     }

         @Override
         protected void onResume () {
         super.onResume();
         if (OpenCVLoader.initDebug()) {
             camera_view.enableView();
         }
     }

         @Override
         protected void onDestroy () {
         super.onDestroy();
         if (camera_view != null) {
             camera_view.disableView();
         }
     }


         private Mat mCannyResult;
         private Mat src;

         @Override
         public void onCameraViewStarted ( int width, int height){
         mCannyResult = new Mat(width, height, CvType.CV_8UC3);
         src = new Mat(width, height, CvType.CV_8UC3);
     }

         @Override
         public void onCameraViewStopped () {
         mCannyResult.release();
         src.release();

     }

         @Override

         public Mat onCameraFrame (CameraBridgeViewBase.CvCameraViewFrame inputFrame){
             int r_i=0;

         int largest_area = 0;
         int x_biggestob;
         int y_biggestob;
         double area = 0;
         float R_a=0;
         float R_b=0;
         float min_R;
         float fr_pt_r = 0;
         float angle=0;
         int cnt_ptinline=0;
         float pt_r;
         float angle_R=0;
         int cnt_right=0;
         int cnt_left=0;
         int cnt_L_Line=0;
         float Line=0;
         int boundary_top=0;
         int boundary_down=0;
         int boundary_left=0;
         int boundary_right=0;
         int boundary_inleft=0;
         int boundary_inright=0;
         int boundary_intop=0;
         int boundary_indown=0;
         int boundary_size=15;
         Rect bounding_biggest_rect = null;
         Mat src = inputFrame.rgba();
         Mat dst = new Mat(src.rows(), src.cols(), CvType.CV_8UC3, Scalar.all(0));
         Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2HSV);
         Core.inRange(dst, new Scalar(100, 43, 46), new Scalar(124, 255, 255), dst);
         final List<MatOfPoint> contours = new ArrayList<>();
         final Mat hierarchy = new Mat();
         Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE); // Find the contours in the image

         for (int i = 0; i < contours.size(); i++) // Iterate through each contour
         {
             area = contourArea(contours.get(i), false); // Find the area of contour
             if (area > 0) {
                 if (area > largest_area) {
                     largest_area = (int) area;
                     bounding_biggest_rect = Imgproc.boundingRect(contours.get(i)); // Find the bounding rectangle for biggest contour


                 }
             }
         }

         if (area > 0) {
             x_biggestob = (bounding_biggest_rect.x + bounding_biggest_rect.x + bounding_biggest_rect.width) / 2;
             y_biggestob = (bounding_biggest_rect.y + bounding_biggest_rect.y + bounding_biggest_rect.height) / 2;
             pointx[cnt_point] = x_biggestob;
             pointy[cnt_point] = y_biggestob;
         }
         else{}



         if(Flag_start==0) {
             Imgproc.rectangle(src,new Point(100,100),new Point(400,400),new Scalar(255,0,0),5,0,0);
             Imgproc.rectangle(src, new Point(pointx[cnt_point] - 5, pointy[cnt_point] - 5), new Point(pointx[cnt_point] + 5, pointy[cnt_point] + 5), new Scalar(255, 0, 0), Core.FILLED, 1, 0);
             if((pointx[cnt_point]>=100) && (pointy[cnt_point]>=100) && (pointx[cnt_point]<=400) && (pointy[cnt_point]<=400)){
                 String sInfo1 = "偵測中";
                 textView1.setText(sInfo1);
                 Flag_start=1;
                 j = 0;
                 pointx = new double[10];
                 pointy = new double[10];
                 cnt_point = 0;
                 Flag_line=0;
             }
             else{
                 Flag_start=0;
             }

        }
         else {
             min_R=(float)3000;
             if(j>29) {
                 for (int i = 0; i <= cnt_point; i++) {
                     Imgproc.rectangle(src, new Point(pointx[i] - 5, pointy[i] - 5), new Point(pointx[i] + 5, pointy[i] + 5), new Scalar(0, 255, 0), Core.FILLED, 1, 0);
                 }
                 if((cnt_point<9) && ((j%3)==2))    {
                     cnt_point++;
                 }
                 else { }
             }
             else{
                 cnt_point=0;
                 Imgproc.rectangle(src, new Point(pointx[0] - 5, pointy[0] - 5), new Point(pointx[0] + 5, pointy[0] + 5), new Scalar(255, 0, 0), Core.FILLED, 1, 0);

             }
             if (j == 59) {
                 for (int i = 0; i < 360; i++) {
                     angle_R= (float) Math.toRadians(i);
                     R_a = (float) (pointx[0] * Math.cos(angle_R) + pointy[0] * Math.sin(angle_R));
                     R_b = (float) (pointx[cnt_point] * Math.cos(angle_R) + pointy[cnt_point] * Math.sin(angle_R));

                     if (Math.abs(R_a-R_b)<min_R) {
                         min_R=Math.abs(R_a-R_b);
                         angle = (float) Math.toRadians(i);
                         fr_pt_r=R_a;

                     }
                 }





                 for (int i = 0; i <= cnt_point; i++) {
                     pt_r = (float) (pointx[i] * Math.cos(angle) + pointy[i] * Math.sin(angle));

                     if ((( fr_pt_r -50) < pt_r) && (pt_r < ( fr_pt_r + 50))) {
                         cnt_ptinline = cnt_ptinline + 1;
                     }
                 }

                 if (cnt_ptinline > 7) {
                     Flag_line = 1;
                     String sInfo3 = "Line";
                     textView1.setText(sInfo3);
                     if((angle>Math.toRadians(80)) && (angle<Math.toRadians(100)) ){
                         if(pointx[cnt_point]>pointx[0]){
                             String sInfo2 ="→";
                             textView2.setText(sInfo2);
                         }
                         else{
                             String sInfo2 ="← ";
                             textView2.setText(sInfo2);
                         }

                     }
                     else if((angle<Math.toRadians(20)) || ((angle>Math.toRadians(160)) && (angle<Math.toRadians(190))) || (angle>Math.toRadians(340))){
                         if(pointy[cnt_point]>pointy[0]){
                             String sInfo2 ="↓ ";
                             textView2.setText(sInfo2);
                         }
                         else{
                             String sInfo2 ="↑";
                             textView2.setText(sInfo2);
                         }
                     }
                     else{
                         if(pointx[cnt_point]>pointx[0]){
                             if(pointy[cnt_point]>pointy[0]){
                                 String sInfo2 ="↘";
                                 textView2.setText(sInfo2);
                             }
                             else{
                                 String sInfo2 ="↗";
                                 textView2.setText(sInfo2);

                             }
                         }
                         else{
                             if(pointy[cnt_point]>pointy[0]){
                                 String sInfo2 ="↙";
                                 textView2.setText(sInfo2);
                             }
                             else{
                                 String sInfo2 ="↖";
                                 textView2.setText(sInfo2);
                             }
                         }
                     }
                 } else {
                     Flag_line = 0;
                     if(((angle>Math.toRadians(20)) && (angle<Math.toRadians(160))) ||((angle>Math.toRadians(190)) && (angle<Math.toRadians(340)))){
                         float m= (float) ((pointy[0]-pointy[cnt_point])/(pointx[0]-pointx[cnt_point]));

                         for(int i =0;i<=cnt_point;i++) {
                             Line= (float) ((m*(pointx[i]-pointx[0]))-(pointy[i]-pointy[0]));
                             if(i==5){

                             }
                             if (Line<0) {
                                 cnt_left++;
                             }
                             if (Line>0) {
                                 cnt_right++;
                             }
                         }
                         if(m>0){
                             if(cnt_left>cnt_right){                                                                               //└

                                 boundary_top= (int) (pointy[0]<pointy[cnt_point] ? pointy[0] : pointy[cnt_point]);
                                 boundary_down= (int) ((pointy[0]>pointy[cnt_point] ? pointy[0] : pointy[cnt_point])+boundary_size);
                                 boundary_left= (int) (pointx[0]<pointx[cnt_point] ? pointx[0] : pointx[cnt_point])-boundary_size;
                                 boundary_right= (int) (pointx[0]>pointx[cnt_point] ? pointx[0] : pointx[cnt_point]);

                                 boundary_intop=boundary_top;
                                 boundary_indown=(int) ((pointy[0]>pointy[cnt_point] ? pointy[0] : pointy[cnt_point])-boundary_size);
                                 boundary_inleft=(int) (pointx[0]<pointx[cnt_point] ? pointx[0] : pointx[cnt_point])+boundary_size;
                                 boundary_inright=boundary_right;
                             }
                             if(cnt_left<cnt_right) {                                                                          //┐


                                 boundary_top= (int) (pointy[0]<pointy[cnt_point] ? pointy[0] : pointy[cnt_point])-boundary_size;
                                 boundary_down= (int) (pointy[0]>pointy[cnt_point] ? pointy[0] : pointy[cnt_point]);
                                 boundary_left= (int) (pointx[0]<pointx[cnt_point] ? pointx[0] : pointx[cnt_point]);
                                 boundary_right= (int) (pointx[0]>pointx[cnt_point] ? pointx[0] : pointx[cnt_point])+boundary_size;

                                 boundary_intop=(int) (pointy[0]<pointy[cnt_point] ? pointy[0] : pointy[cnt_point])+boundary_size;
                                 boundary_indown=boundary_down;
                                 boundary_inleft=boundary_left;
                                 boundary_inright=(int) (pointx[0]>pointx[cnt_point] ? pointx[0] : pointx[cnt_point])-boundary_size;

                             }
                         }
                         if(m<0){
                             if(cnt_left>cnt_right){                                                                           //┘

                              boundary_top= (int) (pointy[0]<pointy[cnt_point] ? pointy[0] : pointy[cnt_point]);
                              boundary_down= (int) ((pointy[0]>pointy[cnt_point] ? pointy[0] : pointy[cnt_point])+boundary_size);
                              boundary_left= (int) (pointx[0]<pointx[cnt_point] ? pointx[0] : pointx[cnt_point]);
                              boundary_right= (int) (pointx[0]>pointx[cnt_point] ? pointx[0] : pointx[cnt_point])+boundary_size;

                              boundary_intop= boundary_top;
                              boundary_indown= (int) (pointy[0]>pointy[cnt_point] ? pointy[0] : pointy[cnt_point])-boundary_size;
                              boundary_inleft=boundary_left;
                              boundary_inright=(int) (pointx[0]>pointx[cnt_point] ? pointx[0] : pointx[cnt_point])-boundary_size;

                             }
                             if(cnt_left<cnt_right){                                                                          //┌

                                boundary_top= (int) (pointy[0]<pointy[cnt_point] ? pointy[0] : pointy[cnt_point])-boundary_size;
                                boundary_down= (int) ((pointy[0]>pointy[cnt_point] ? pointy[0] : pointy[cnt_point]));
                                boundary_left= (int) (pointx[0]<pointx[cnt_point] ? pointx[0] : pointx[cnt_point])-boundary_size;
                                boundary_right= (int) (pointx[0]>pointx[cnt_point] ? pointx[0] : pointx[cnt_point]);

                                boundary_intop=(int) (pointy[0]<pointy[cnt_point] ? pointy[0] : pointy[cnt_point])+boundary_size;
                                boundary_indown=   boundary_down;
                                boundary_inleft= (int) (pointx[0]<pointx[cnt_point] ? pointx[0] : pointx[cnt_point])+boundary_size;
                                boundary_inright=boundary_right;


                             }
                         }
                       // String sInfo4 = String.valueOf(a)+"  "+String.valueOf(pointx[0])+"   "+String.valueOf(pointy[0])+"\n"+String.valueOf(pointx[cnt_point])+"   "+String.valueOf(pointy[cnt_point]);
                       //String sInfo4 = String.valueOf(cnt_left)+"   "+String.valueOf(cnt_right)+" \n "+String.valueOf(Line);
                         //extView1.setText(sInfo4);
                        for (int i=0 ; i<=cnt_point;i++){
                                if((pointx[i]>boundary_left) && (pointx[i]<boundary_right) && (pointy[i]>boundary_top) && (pointy[i]<boundary_down)){
                                    if(!((pointx[i]>boundary_inleft) && (pointx[i]<boundary_inright) && (pointy[i]>boundary_intop) && (pointy[i]<boundary_indown))){
                                        cnt_L_Line++;
                                    }
                                }
                         }
                        if(cnt_L_Line>7){
                            String sInfo3 = "L";
                            textView1.setText(sInfo3);
                        }

                     }
                 }
                 j = 0;
                 pointx = new double[10];
                 pointy = new double[10];
                 cnt_point = 0;

                 Flag_start=0;
             } else {
                 j++;
             }
         }





         hierarchy.release();
         dst.release();
         mCannyResult = src;
         return mCannyResult;
     }
 }




