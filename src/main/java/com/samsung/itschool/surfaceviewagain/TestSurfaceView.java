package com.samsung.itschool.surfaceviewagain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Debug;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    int color = Color.GREEN;
    float leftr, topr, rightr, bottomr;
    boolean moving = false;


    DrawThread thread;

    class DrawThread extends Thread {
        float x, y, x2, y2;

        Random r = new Random();
        Paint p = new Paint();
        Paint p2 = new Paint();
        Paint prect = new Paint();
        public boolean collision (float x1, float y1, float x2, float y2) {
            boolean flag = false;
            double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            if (distance <= 215) flag = true;
            return flag;
        }

        public DrawThread(SurfaceHolder holder) {
            this.holder = holder;
        }
        SurfaceHolder holder;
        boolean runFlag = true;

        @Override
        public void run() {
            super.run();
            leftr = 0; topr = 0; rightr = 500; bottomr = 200;

            x = r.nextFloat() * 500 + 300;
            y = r.nextFloat() * 500 + 300;
            x2 = r.nextFloat() * 500 + 300;
            y2 = r.nextFloat() * 500 + 300;
            while (collision(x,y,x2,y2)) {
                x2 = r.nextFloat() * 300;
                y2 = r.nextFloat() * 300;
            }

            float vx = 50;
            float vy = 40;

            float vx2 = -60;
            float vy2 = -70;
            ArrayList<Integer> colors = new ArrayList<Integer>(Arrays.asList(
                    Color.LTGRAY, Color.YELLOW,
                    Color.RED, Color.BLUE,
                    Color.CYAN, Color.MAGENTA,
                    Color.BLACK, Color.GRAY
            ));
            p.setColor(colors.get(1));
            p2.setColor(colors.get(2));
            prect.setColor(Color.BLACK);

            // задание: реализовать плавную смену цвета
            // палитру выбирайте сами
            while (runFlag ) {



                Canvas c = holder.lockCanvas();
                if (c != null) {
                    c.drawColor(color);

                    RectF rect = new RectF(leftr,topr, rightr, bottomr);
                    c.drawRect(rect, prect);
                    x += vx;
                    y += vy;
                    x2 += vx2;
                    y2 += vy2;

                    int clr = (int) (Math.random() * 8);
                    if ((x + 100 >= c.getWidth()) || (x - 100 <= 0)){
                        vx *= -1;
                        p.setColor(colors.get(clr));
                    }
                    if ((y + 100 >= c.getHeight()) || (y - 100 <= 0) ){
                        vy *= -1;
                        p.setColor(colors.get(clr));
                    }
                    if ((x2 + 100 >= c.getWidth()) || (x2 - 100 <= 0)){
                        vx2 *= -1;
                        p2.setColor(colors.get(clr));
                    }
                    if ((y2 + 100 >= c.getHeight()) || (y2 - 100 <= 0) ){
                        vy2 *= -1;
                        p2.setColor(colors.get(clr));
                    }
                    if (collision(x, y, x2, y2)){
                        vx *= -1;
                        vy *= -1;
                        vx2 *= -1;
                        vy2 *= -1;
                        p.setColor(colors.get(clr));
                        p2.setColor(colors.get(clr));
                    }
                    if (rect.intersect(x - 100, y - 100, x + 100, y + 100)){
                        vx *= -1;
                        vy *= -1;
                        p.setColor(colors.get(clr));
                    }
                    if (rect.intersect(x2 - 100, y2 - 100, x2 + 100, y2 + 100)){
                        vx2 *= -1;
                        vy2 *= -1;
                        p2.setColor(colors.get(clr));
                    }

                    c.drawCircle(x, y, 100, p);
                    c.drawCircle(x2, y2, 100, p2);
                    if (p.getColor() == p2.getColor()){
                        p.setColor(Color.BLACK);
                        RectF rectCongrats = new RectF(c.getWidth()/2 - 250, c.getHeight()/2 - 100,
                                c.getWidth()/2 + 250, c.getHeight()/2 + 100);
                        c.drawRect(rectCongrats, p);
                        Paint p3 = new Paint();
                        p3.setColor(Color.WHITE);
                        p3.setTextSize(40);
                        c.drawText("congratulationfrtsctecrdcdt", rectCongrats.left, rectCongrats.centerY(), p3);
                        runFlag=false;
                    }

                    holder.unlockCanvasAndPost(c);


                        try {
                        Thread.sleep(100);
                    } catch(InterruptedException e) {}
                }

            }


        }
    }


    public TestSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // этот класс является обработчиком событий с поверхностью
        getHolder().addCallback(this);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // запустить поток отрисовки

        thread = new DrawThread(holder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int i, int i1, int i2) {
        // перезапустить поток
        thread.runFlag = false;
        thread = new DrawThread(holder);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // остановить поток
        thread.runFlag = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch( event.getAction() ) {
            case MotionEvent.ACTION_DOWN:
                moving = true;

                final int x1 = (int) event.getX();
                final int y1 = (int) event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (moving) {

                    final int x_new = (int) event.getX();
                    final int y_new = (int) event.getY();
                    leftr = x_new - 250;
                    rightr = 250+x_new;

                }
                return true;
            case MotionEvent.ACTION_UP:
                moving = false;
                return true;
        }

        return true;

    }
}

