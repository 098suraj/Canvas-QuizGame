package com.heyletscode.ihavetofly;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {
    ArrayList<Question> questions = new ArrayList<>();
    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0, index = 0;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Birdb[] birds;
    private Birda[] birdsa;
    private Birdc[] birdsc;
    private SharedPreferences prefs;
    private Random random;
    private SoundPool soundPool;
    private List<Bullet> bullets;
    private int sound;
    private Flight flight;
    private GameActivity activity;
    private Bitmap rectangle;
    String a = "a";
    String b = "b";
    String c = "c";

    private Background background1, background2;

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        sound = soundPool.load(activity, R.raw.shoot, 1);


        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;
        questions.add(new Question("What is part of a database that holds only one type of information?","Report",
                "Field", "Record","Field"));
        questions.add(new Question("'OS' computer abbreviation usually means","Order of Significance","Open Software","Operating System","Operating System"));
        questions.add(new Question("Which of the following operating systems is produced by IBM?","OS-2","Windows","DOS","OS-2"));
        questions.add(new Question("What is a GPU?","Grouped Processing Unit","Graphics Processing Unit","Graphical Performance Utility","Graphics Processing Unit"));
        questions.add(new Question("Which one of the following is a search engine?","Macromedia Flash","Google","Netscape","Google"));
        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        flight = new Flight(this, screenY, getResources());

        bullets = new ArrayList<>();

        background2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.BLACK);

        birds = new Birdb[1];
        Birdb bird = new Birdb(getResources());
        birds[0] = bird;
        bird.x = screenX;
        bird.y = (screenY - bird.height);
        //bird a-------------------------------------//
        birdsa = new Birda[1];
        Birda birda = new Birda(getResources());
        birdsa[0] = birda;
        birda.x = screenX;
        birda.y = (screenY - birda.height) - 280;
        //bird c-------------------------------------//
        birdsc = new Birdc[1];
        Birdc birdc = new Birdc(getResources());
        birdsc[0] = birdc;
        birdc.x = screenX;
        birdc.y = (screenY - birdc.height) - 540;

    }

    @Override
    public void run() {

        while (isPlaying) {

            update();
            draw();
            sleep();

        }

    }

    private void update() {

        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }

        if (flight.isGoingUp)
            flight.y -= 15 * screenRatioY;
        else
            flight.y += 15 * screenRatioY;

        if (flight.y < 0)
            flight.y = 0;

        if (flight.y >= screenY - flight.height)
            flight.y = screenY - flight.height;

        List<Bullet> trash = new ArrayList<>();

        for (Bullet bullet : bullets) {

            if (bullet.x > screenX)
                trash.add(bullet);

            bullet.x += 50 * screenRatioX;

            for (Birdb bird : birds) {

                if (Rect.intersects(bird.getCollisionShape(),
                        bullet.getCollisionShape())) {

                    if (TextUtils.equals(questions.get(index).getB(), questions.get(index).getCorrect_answer())) {
                        score++;
                        bird.wasShot = true;
                        bullet.x = screenX + 1000;
                       getAlerbox();
                    } else {
                        getAlertwrong();
                    }

                }

            }
            // bird a-----------------------------------//


            for (Birda birda : birdsa) {

                if (Rect.intersects(birda.getCollisionShape(),
                        bullet.getCollisionShape())) {
                    if (TextUtils.equals(questions.get(index).getA(), questions.get(index).getCorrect_answer())) {
                        score++;
                        birda.wasShot = true;
                        bullet.x = screenX + 1000;
                        getAlerbox();
                    } else {
                        getAlertwrong();

                    }

                }

            }
            //bird c------------------------------------//

            for (Birdc birdc : birdsc) {

                if (Rect.intersects(birdc.getCollisionShape(),
                        bullet.getCollisionShape())) {
                    if (TextUtils.equals(questions.get(index).getC(), questions.get(index).getCorrect_answer())) {
                        score++;
                        birdc.wasShot = true;
                        bullet.x = screenX + 1000;
                        getAlerbox();
                    } else {
                        getAlertwrong();
                    }

                }

            }
        }

        for (Bullet bullet : trash)
            bullets.remove(bullet);

        for (Birdb bird : birds) {

            bird.x -= bird.speed;

            if (bird.x + bird.width < 0) {

                if (!bird.wasShot) {
                    isGameOver = true;
                    return;
                }

                int bound = (int) (30 * screenRatioX);
                bird.speed = (bound);

//                if (bird.speed < 10 * screenRatioX)
//                    bird.speed = (int) (10 * screenRatioX);

                bird.x = screenX;
                bird.y = (screenY - bird.height);

                bird.wasShot = false;
            }

            if (Rect.intersects(bird.getCollisionShape(), flight.getCollisionShape())) {
                timeOut();
                isGameOver = true;
                return;
            }

        }
        // bird a-----------------------------------//


        for (Birda birda : birdsa) {

            birda.x -= birda.speed;

            if (birda.x + birda.width < 0) {

                if (!birda.wasShot) {
                    timeOut();
                    isGameOver = true;

                    return;
                }

                int bound = (int) (30 * screenRatioX);
                birda.speed = (bound);

//                if (bird.speed < 10 * screenRatioX)
//                    bird.speed = (int) (10 * screenRatioX);

                birda.x = screenX;
                birda.y = (screenY - birda.height) - 320;

                birda.wasShot = false;
            }

            if (Rect.intersects(birda.getCollisionShape(), flight.getCollisionShape())) {
                timeOut();
                isGameOver = true;
                return;
            }

        }

        //--------------------------------bird c---------//
        for (Birdc birdc : birdsc) {

            birdc.x -= birdc.speed;

            if (birdc.x + birdc.width < 0) {


                if (!birdc.wasShot) {
                    timeOut();
                    isGameOver = true;

                    return;
                }

                int bound = (int) (30 * screenRatioX);
                birdc.speed = (bound);

//                if (bird.speed < 10 * screenRatioX)
//                    bird.speed = (int) (10 * screenRatioX);

                birdc.x = screenX;
                birdc.y = (screenY - birdc.height) - 540;

                birdc.wasShot = false;
            }

            if (Rect.intersects(birdc.getCollisionShape(), flight.getCollisionShape())) {
                timeOut();
                isGameOver = true;

                return;
            }

        }
    }

    private void timeOut() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pause();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Time Out")
                        .setMessage("You Have Crossed The Time Limit")
                        .setCancelable(false)
                        .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                isPlaying = true;
                                background1.x -= 0;
                                background2.x -= 0;
                                birds[0].x = screenX;
                                birdsa[0].x = screenX;
                                birdsc[0].x = screenX;
                                for (Bullet bullet:bullets){
                                    bullet.x = screenX + 1000;
                                }


                                isGameOver = false;
                                getAlerbox();


                            }
                        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                scoreDialogue();
                            }
                        });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });
    }

    private void draw() {

        if (getHolder().getSurface().isValid()) {

            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            for (Birdb bird : birds)
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);

            // bird a-----------------------------------//
            for (Birda birda : birdsa)
                canvas.drawBitmap(birda.getBird(), birda.x, birda.y, paint);

            for (Birdc birdc : birdsc) {
                canvas.drawBitmap(birdc.getBird(), birdc.x, birdc.y, paint);
            }

            canvas.drawText(score + "", screenX / 2f, 164, paint);

            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                return;
            }

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);

            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);

        }


    }

    private void getAlerbox() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pause();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(questions.get(index).getQuestion())
                        .setMessage("a." + questions.get(index).getA()
                                + "\n" + "b." + questions.get(index).getB() +
                                "\n" + "c." + questions.get(index).getC())
                        .setCancelable(false)
                        .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                isPlaying = true;
                                background1.x -= 0;
                                background2.x -= 0;
                                birds[0].x = screenX;
                                birdsa[0].x = screenX;
                                birdsc[0].x = screenX;
                                if (index < questions.size()) {
                                    index++;
                                } else {
                                    index = 0;
                                }
                                isGameOver = false;
                                resume();
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               scoreDialogue();
                            }
                        });
                AlertDialog dialog = alertDialog.create();
                dialog.show();


            }

        });

    }

    private void getAlertwrong() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pause();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Wrong Answer")
                        .setMessage("Exit")
                        .setCancelable(false)
                        .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                isPlaying = true;
                                background1.x -= 0;
                                background2.x -= 0;
                                birds[0].x = screenX;
                                birdsa[0].x = screenX;
                                birdsc[0].x = screenX;
                                for (Bullet bullet:bullets){
                                    bullet.x = screenX + 1000;
                                }


                                isGameOver = false;
                                getAlerbox();

                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               scoreDialogue();
                            }
                        });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
            }
        });
    }

    private void waitBeforeExiting() {

        try {
            scoreDialogue();
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void scoreDialogue() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alert=new AlertDialog.Builder(getContext())
                        .setTitle("Score ")
                        .setMessage("Your Score Is :"+score)
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                             waitBeforeExiting();
                            }
                        });
                AlertDialog di=alert.create();
                di.show();

            }
        });
    }

    private void saveIfHighScore() {

        if (prefs.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }

    }

    private void sleep() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {

        isPlaying = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause() {

        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int pointerIndex = event.getActionIndex();


        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN: {
                // Flight
                if (event.getX(pointerIndex) < screenX / 2) {
                    flight.isGoingUp = true;
                }
                // Shoot
                if (event.getX(pointerIndex) > screenX / 2)
                    flight.toShoot++;

                break;
            }

            // Last finger UP

            case MotionEvent.ACTION_UP: {
                flight.isGoingUp = false;

                break;
            }
            // LEFT finger UP
            case MotionEvent.ACTION_POINTER_UP: {
                if ((event.getX(pointerIndex) < screenX / 2))
                    flight.isGoingUp = false;

                break;
            }
        }

        return true;
    }


    public void newBullet() {


        Bullet bullet = new Bullet(getResources());
        bullet.x = flight.x + flight.width;
        bullet.y = flight.y + (flight.height / 2);
        if (bullets.isEmpty()){
            if (!prefs.getBoolean("isMute", false))
                soundPool.play(sound, 1, 1, 0, 0, 1);

            bullets.add(bullet);
        }else
            bullets.remove(bullet);


    }
}
