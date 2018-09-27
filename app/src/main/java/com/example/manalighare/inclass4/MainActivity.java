/*
 ************************ Assignment #InClass 04 *******************************************
 *********************** File Name- MainActivity.java *************************************
 ************************ Full Name- 1. Manali Ghare 2. Anup Deshpande (Group 19) ***********

 */

package com.example.manalighare.inclass4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button thread_btn;
    private Button async_btn;
    private ProgressBar progressBar;
    private ImageView imageView;


    Handler handler;
    ExecutorService executorService= Executors.newFixedThreadPool(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Display Image");


        thread_btn=(Button)findViewById(R.id.thread_btn);
        async_btn=(Button)findViewById(R.id.async_btn);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        imageView=(ImageView)findViewById(R.id.imageView);


        thread_btn.setOnClickListener(this);
        async_btn.setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);

        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                if(msg.obj!=null){
                    imageView.setImageBitmap((Bitmap) msg.obj);

                }

                progressBar.setVisibility(View.INVISIBLE);

                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.thread_btn:
                progressBar.setVisibility(View.VISIBLE);
                executorService.execute(new GetImageThread());

                break;

            case R.id.async_btn:
                progressBar.setVisibility(View.VISIBLE);
                new GetImageAsyncThread().execute("https://cdn.pixabay.com/photo/2014/12/16/22/25/youth-570881_960_720.jpg");
                break;

        }
    }


    class GetImageThread implements Runnable{

        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        URL url=null;

        @Override
        public void run() {
            try {
                url= new URL("https://cdn.pixabay.com/photo/2017/12/31/06/16/boats-3051610_960_720.jpg");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                    Message message=new Message();
                    message.obj=bitmap;
                    handler.sendMessage(message);

                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }



        }
    }


    class GetImageAsyncThread extends AsyncTask<String,Integer,Bitmap>{

        HttpURLConnection connection = null;
        Bitmap bitmap = null;
        URL url=null;


        @Override
        protected void onPostExecute(Bitmap bitmap) {

            super.onPostExecute(bitmap);
            if(bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }
            progressBar.setVisibility(View.INVISIBLE);

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                 url= new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            return bitmap;
        }
    }



}
