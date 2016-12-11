package com.badinini.walid.animalssounds;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class GameActivity extends AppCompatActivity {

    MediaPlayer mp ;
    String choosedRes;
    int score;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // choisi un string resource au hasard
        choosedRes = randomRes();

        // modifier background du buttons
        modifyImageButtons(choosedRes);

        // Recommencer l'audio de l'animal choisi
        mp = MediaPlayer.create(this, getSound(choosedRes));
        mp.start();


    }

    public void modifyImageButtons(String nomRes)
    {

        // avoir une sequence des nombres 1 2 3 , en désordre
        LinkedHashSet<Integer> generated = new LinkedHashSet<Integer>();
        while (generated.size() < 3)
        {
           int i = rand(0,3);
           generated.add(i);
        }


        int[] idImageButtons = new int[3];
        idImageButtons[0] = R.id.imageButton1;
        idImageButtons[1] = R.id.imageButton2;
        idImageButtons[2] = R.id.imageButton3;


        ImageButton button ;
        Drawable d ;
        Vector<String> previousString = new Vector<String>();
        int cpt = 0;

        Iterator<Integer> it = generated.iterator();
        while(it.hasNext())
        {

            String randNom = randomRes();

            if(!choosedRes.equals(randNom) && !previousString.contains(randNom))
            {
                int tmp = it.next();
                button = (ImageButton) findViewById(idImageButtons[tmp]);
                button.setTag(randNom);

                try
                {
                    d = getAssetImage(GameActivity.this,randNom);
                    previousString.add(randNom);
                    button.setBackgroundDrawable(d);
                }
                catch (IOException e)
                {

                }
                cpt++;
            }

            if(cpt==2)
            {
                break;
            }

        }

        int tmp = it.next();
        button = (ImageButton) findViewById(idImageButtons[tmp]);
        try
        {
            d = getAssetImage(GameActivity.this, choosedRes);
            button.setTag(choosedRes);
            button.setBackgroundDrawable(d);
        }
        catch (IOException e)
        {

        }




    }
    public static Drawable getAssetImage(Context context, String filename) throws IOException {
        AssetManager assets = context.getResources().getAssets();
        InputStream buffer = new BufferedInputStream((assets.open("images/" + filename + ".jpg")));
        Bitmap bitmap = BitmapFactory.decodeStream(buffer);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public int rand(int min,int max)
    {
        return (min + (int)(Math.random() * (max - min) ) );
    }

    public String randomRes()
    {
        Field[] tab = R.raw.class.getFields();
        String[] nomRes = new String[tab.length];
        for(int i=0;i<tab.length;i++)
        {
            nomRes[i] = tab[i].toString().split("raw.")[1];
        }

        int i = rand(0,tab.length);
        return  nomRes[i];
    }


    public int getSound(String nomRes)
    {
        return getResources().getIdentifier(nomRes, "raw", "com.badinini.walid.animalssounds");
    }

    public int getImage(String nomRes)
    {
        return getResources().getIdentifier(nomRes, "drawable", "com.badinini.walid.animalssounds");
    }

    public void playSound(View v)
    {
        if(mp.isPlaying())
        {
            mp.stop();

        }
        mp = MediaPlayer.create(this,getSound(choosedRes));
        mp.start();



    }


    public void verify(View v)
    {
        String clickedButton = (String)v.getTag();


        if(clickedButton.equals(choosedRes))
        {
            mp.stop();
            score += 10;
            TextView scoreView = (TextView)findViewById(R.id.score);
            scoreView.setText(score + "");

            final Dialog alertDialog = new Dialog(GameActivity.this);
            alertDialog.setContentView(R.layout.win);
            alertDialog.setTitle("Tu as gagné!");

            Button dialogButton = (Button) alertDialog.findViewById(R.id.suivantButton);

            // if button is clicked
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    choosedRes = randomRes();
                    mp = MediaPlayer.create(GameActivity.this, getSound(choosedRes));
                    mp.start();
                    modifyImageButtons(choosedRes);
                    alertDialog.dismiss();

                }
            });


            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            alertDialog.show();
            alertDialog.getWindow().setAttributes(lp);

        }
        else {
            mp.stop();
            ImageButton button = (ImageButton)v;

            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            button.startAnimation(shake);

            Vibrator vib = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            vib.vibrate(500);

            if(score != 0)
                 score -=10;
            TextView scoreView = (TextView)findViewById(R.id.score);
            scoreView.setText(score + "");

        }
    }

    @Override
    public void onBackPressed() {

        mp.stop();
        super.onBackPressed();

    }
}

