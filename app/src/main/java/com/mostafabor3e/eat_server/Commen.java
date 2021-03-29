package com.mostafabor3e.eat_server;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.mostafabor3e.eat_server.Model.Request;
import com.mostafabor3e.eat_server.Model.User;
import com.mostafabor3e.eat_server.Remot.ApiServec;
import com.mostafabor3e.eat_server.Remot.GeoCode;
import com.mostafabor3e.eat_server.Remot.RetrioftClient;

public class Commen {
    public static User currentUser;
    public static Request currentRequest;
   // RetrioftClient retrioftClient=new RetrioftClient();
   public static final String baseUrl="https://maps.googleapis.com";
    public static final String baseUr="https://fcm.googleapis.com";
    public static ApiServec getFcm(){
        return    RetrioftClient.getClient(baseUr).create(ApiServec.class);
    }

    public static  String ConvertCodeToStatu(String status){
        if (status.equals("0"))
            return "placed";

        else if (status.equals("1"))
            return "On My Way";

        else return "Shipped";

    }


    public static GeoCode getGeoCodeService(){
        //RetrioftClient retrioftClient=new RetrioftClient();

        return RetrioftClient.getClient(baseUrl).create(GeoCode.class);

    }
    public static Bitmap scaleBitmap(Bitmap bitmap,int newWidth,int newHeight){
        Bitmap scalebitmap=Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleX=newWidth/(float)bitmap.getWidth();
        float scaleY=newHeight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix matrix=new Matrix();
        matrix.setScale(scaleX,scaleY,pivotX,pivotY);
        Canvas canvas=new Canvas(scalebitmap);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(bitmap,0,0,new Paint((Paint.FILTER_BITMAP_FLAG)));
        return scalebitmap;

    }
}
