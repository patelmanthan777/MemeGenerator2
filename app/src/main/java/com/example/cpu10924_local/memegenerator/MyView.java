package com.example.cpu10924_local.memegenerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CPU10924-local on 3/29/2016.
 */
public class MyView extends View {
    private Bitmap bmpImage;
    private Matrix matrix;
    private List<CaptionText> captionTextList = new ArrayList<CaptionText>();
    public List<Sticker> stickerList = new ArrayList<Sticker>();
    private int indexClickText = -1;
    private int indexClickSticker = -1;
    private float initX;
    private float initY;
    private int imageViewWidth;
    private int imageViewHeight;
    public ScaleGestureDetector mScaleDetector;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bmpImage, 0, 0, null);
        //Draw text
        for (int i=0; i<captionTextList.size();i++)
        {

            canvas.drawText(captionTextList.get(i).content.toUpperCase(),captionTextList.get(i).x , captionTextList.get(i).y, captionTextList.get(i).strokepaint);
            canvas.drawText(captionTextList.get(i).content.toUpperCase(), captionTextList.get(i).x , captionTextList.get(i).y, captionTextList.get(i).paint);

        }
        //Draw sticker
        for(int i=0; i<stickerList.size();i++)
        {
            canvas.drawBitmap(stickerList.get(i).bitmap,stickerList.get(i).x,stickerList.get(i).y,null);
        }


    }

    public Bitmap getResizedBitmap(Bitmap bmp, int newWidth, int newHeight)
    {
        int width = bmpImage.getWidth();
        int height = bmpImage.getHeight();
        float scaleWidth = ((float)newWidth)/width;
        float scaleHeight = ((float)newHeight)/height;
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createScaledBitmap(bmp,newWidth,newWidth,false);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        imageViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        imageViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        bmpImage = getResizedBitmap(bmpImage,imageViewWidth, imageViewHeight);

    }

    public MyView(Context context,AttributeSet attrs)
    {
        super(context, attrs);

        init();
    }

    private void init()
    {
        matrix = new Matrix();
    }
    public void setCanvasBitmap(Bitmap bitmap)
    {
        bmpImage = bitmap;

    }
    public void setSticker(Sticker sticker)
    {
        sticker.bitmap = getResizedBitmap(sticker.bitmap,300,300);
        stickerList.add(sticker);
    }

    public void addTextCaption(CaptionText textCaption)
    {
        Rect bound = new Rect();
       textCaption.paint.getTextBounds(textCaption.content,0,textCaption.content.length(),bound);
        //Move text to center
        textCaption.x = imageViewWidth /2 -bound.width()/2;
        switch (captionTextList.size())
        {
            //Move text to bottom
            case 1:
                textCaption.y = imageViewHeight;
                break;
            //Move text to middle
            case 2:
                textCaption.y = imageViewHeight/2 - bound.height()/2;
                break;
            //Otherwise, move up
            default:
                break;

        }
        captionTextList.add(textCaption);
        invalidate();
    }



    public CaptionText getInitTextLocation(float locX,float locY)
    {
        for(int i=0;i<captionTextList.size();i++)
        {

            Rect bound = new Rect();
            captionTextList.get(i).paint.getTextBounds(captionTextList.get(i).content,0,captionTextList.get(i).content.length(),bound);
            float padding = 10;
            float left = captionTextList.get(i).x - padding;
            float top = captionTextList.get(i).y -padding;
            float right = captionTextList.get(i).x + bound.width() +padding;
            float bottom = captionTextList.get(i).y + bound.height() +padding;

            if (top <= locY && locY <=bottom)
            {
                if(left <=locX && locX <=right)
                {
                    initX = locX;
                    initY = locY;
                    indexClickText = i;
                    return captionTextList.get(i);
                }
            }

        }
        indexClickText = -1;
        return null;

    }

    public Sticker getInitStickerLocation(float locX, float locY)
    {
        for(int i=0; i<stickerList.size();++i)
        {
            Bitmap img = stickerList.get(i).bitmap;
            float padding = 10;
            float left = stickerList.get(i).x - padding;
            float right = stickerList.get(i).x + img.getWidth() + padding;
            float top = stickerList.get(i).y - padding;
            float bottom = stickerList.get(i).y + img.getHeight() + padding;
            if (top <= locY && locY <= bottom)
            {
                if (left  <= locX && locX <= right)
                {
                    initX = locX;
                    initY = locY;
                    indexClickSticker =  i;
                    return stickerList.get(i);
                }
            }
        }
        indexClickSticker = -1;
        return  null;
    }
    public void moveObject(float newX, float newY)
    {
        if (indexClickText !=-1)
        {

            CaptionText updateCaptionText = captionTextList.get(indexClickText);
            float deltaX = newX - initX;
            float deltaY = newY - initY;
            updateCaptionText.x +=deltaX;
            updateCaptionText.y +=deltaY;
            initX = newX;
            initY = newY;
            captionTextList.set(indexClickText, updateCaptionText);

        }
        if (indexClickSticker !=-1)
        {
            Sticker updateSticker = stickerList.get(indexClickSticker);
            float deltaX = newX - initX;
            float deltaY = newY - initY;
            updateSticker.x +=deltaX;
            updateSticker.y +=deltaY;
            initX = newX;
            initY = newY;
            stickerList.set(indexClickSticker,updateSticker);
        }

    }

    public void scaleSticker(float mScaleFactor)
    {
        if (indexClickSticker != -1)
        {
            Sticker updateSticker= stickerList.get(indexClickSticker);
            Bitmap bmp = updateSticker.bitmap;

            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int newWidth;
            int newHeight;
            if (mScaleFactor > 1)
            {
               newWidth = (int)(width*mScaleFactor);
               newHeight = (int)(height*mScaleFactor);
            }
            else{
                 newWidth = (int)(width/mScaleFactor);
                newHeight = (int)(height/mScaleFactor);
            }


            bmp = getResizedBitmap(bmp,newWidth,newHeight);
            updateSticker.bitmap = bmp;
            stickerList.set(indexClickSticker,updateSticker);
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options,int Width, int Height)
    {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > Height || width > Width) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > Height
                    && (halfWidth / inSampleSize) > Width) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
