package com.production_planning_game.eps_game;

/**
 * Created by euancampbell on 30/03/2017.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.OkHttpClient;
import okhttp3.Response;



public class RequestBuilder extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_builder);
    }
    //Login request body
    public static RequestBody LoginBody(String username, String password, String token) {
        return new FormBody.Builder()
                .add("action", "login")
                .add("format", "json")
                .add("username", username)
                .add("password", password)
                .add("logintoken", token)
                .build();
    }

    public static HttpUrl buildURL() {
        return new HttpUrl.Builder()
                .scheme("https") //http
                .host("http://35.165.103.236/appstatus")
                .addPathSegment("pathSegment")//adds "/pathSegment" at the end of hostname
                .addQueryParameter("param1", "value1") //add query parameters to the URL
                .addEncodedQueryParameter("encodedName", "encodedValue")//add encoded query parameters to the URL
                .build();

         //* The return URL:
         //*  https://http://35.165.103.236/appstatus
    }

    public static class ApiCall {

        //GET network request
        public static String GET(OkHttpClient client, HttpUrl url) throws IOException {
            Request request = new Request.Builder()
                    .url("http://35.165.103.236/appstatus")
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        //POST network request
        public static String POST(OkHttpClient client, HttpUrl url, RequestBody body) throws IOException {
            Request request = new Request.Builder()
                    .url("http://35.165.103.236/appstatus")
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
    }

}
