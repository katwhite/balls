package com.samsung.itschool.surfaceviewagain;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class TestSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    int color = Color.RED;
    DrawThread thread;
    public void changeColor() {
        Random r = new Random();
        color = Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255));
    }

    class DrawThread extends Thread {
        float x, y, x2, y2;
        Random r = new Random();
        Paint p = new Paint();
        Paint p2 = new Paint();

        public boolean collision (float x1, float y1, float x2, float y2) {
            boolean flag = false;
            double distance = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            if (distance <= 200) flag = true;
            return flag;
        }

        boolean runFlag = true;
        public DrawThread(SurfaceHolder holder) {
            this.holder = holder;
        }
        SurfaceHolder holder;
        @Override
        public void run() {
            super.run();

            x = r.nextFloat() * 500;
            y = r.nextFloat() * 500;
            x2 = r.nextFloat() * 500;
            y2 = r.nextFloat() * 500;
            while ((x == x2) || (y == y2)) {
                x2 = r.nextFloat() * 300;
                y2 = r.nextFloat() * 300;
            }
            float vx = 50;
            float vy = 40;

            float vx2 = -60;
            float vy2 = -70;
            ArrayList<Integer> colors = new ArrayList<Integer>(Arrays.asList(
                    Color.LTGRAY, Color.YELLOW,
                    Color.GREEN, Color.BLUE,
                    Color.CYAN, Color.MAGENTA,
                    Color.BLACK, Color.GRAY
            ));
            p.setColor(colors.get(1));
            p2.setColor(colors.get(2));
            // задание: реализовать плавную смену цвета
            // палитру выбирайте сами
            while (runFlag) {
                Canvas c = holder.lockCanvas();
                if (c != null) {
                    c.drawColor(color);
                    x += vx;
                    y += vy;
                    x2 += vx2;
                    y2 += vy2;

                    int clr = (int) (Math.random() * 8);
                    if ((x >= c.getWidth()) || (x <= 0)){
                        vx *= -1;
                        p.setColor(colors.get(clr));
                    }
                    if ((y >= c.getHeight()) || (y <= 0) ){
                        vy *= -1;
                        p.setColor(colors.get(clr));
                    }
                    if ((x2 >= c.getWidth()) || (x2 <= 0)){
                        vx2 *= -1;
                        p2.setColor(colors.get(clr));
                    }
                    if ((y2 >= c.getHeight()) || (y2 <= 0) ){
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
                    c.drawCircle(x, y, 100, p);
                    c.drawCircle(x2, y2, 100, p2);
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
}
