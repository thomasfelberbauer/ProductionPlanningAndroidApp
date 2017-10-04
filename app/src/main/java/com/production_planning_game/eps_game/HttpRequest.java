package com.production_planning_game.eps_game;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static android.support.v4.content.ContextCompat.startActivity;
import static com.production_planning_game.eps_game.R.id.statusInfo;


public class HttpRequest extends AsyncTask<Void, Void, Void> {

Context context;
public HttpRequest(Context context){
        this.context = context.getApplicationContext();
    }


public String requestName; //the name of the request you want to call
public String var1; //variable given in the request for the username
public String var2; //variable given in the request for the password
public String var3; //variable given in the request for the status
public String res; //variable to save the response in a string object
//public String IPAdress = "35.165.103.236";
public String IPAdress = "91.219.68.208";
public String methodStr = "";
LoginActivity login = new LoginActivity();
CheckInOutActivity checkInOut = new CheckInOutActivity();

//Intent intent = new Intent(HttpRequest.this.getActivity(), CheckInOutActivity.class);

    @Override
    protected Void doInBackground(Void... arg0) {

        //Send http request
        if(requestName == "appsignin"){
            appSignInRequest();//call the function to sign in

        }else if(requestName == "checkstartstop"){
            try {
                appCheckStartStop(); //call the function to check if the game started or not
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if(requestName == "appstatus"){
            appStatusRequest(); //call the function to do the check in/ check out

        }else if(requestName == "getorderlist"){
            try {
                appProductionMRP(); //call the function to get the order list for the MRP method
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(requestName == "getinventory"){
            try {
                appInventory(); //call the function to get the inventory
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else if (requestName == "getcostreq"){
            try {
                appProduction(); //call the function to get the costumer order list
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    // Do the check in/check out ; send to the server if the player is producing or not
    public void appStatusRequest(){

        OkHttpClient client = new OkHttpClient();
        JSONObject data = new JSONObject();
        String url = "http://"+IPAdress +"/appstatus"; //save the url of the server
        try {
            data.put("machine", login.machinenumber); //send which machine you are
            data.put("status",var3); //send what is the status of the machine (producing or idle)
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,data.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            //Log.d("Response executed",response.body().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //Do the log in
    public String appSignInRequest(){

        OkHttpClient client = new OkHttpClient();
        JSONObject data = new JSONObject();
        String url = "http://"+IPAdress +"/appsignin"; //save the url of the server

        try {
            data.put("username", var1); //send the username from the login screen
            data.put("password",var2); //send the password from the login screen
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON,data.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            res = response.body().string();
            //Log.d("You received ",res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(res.equals("Approved")){ //if your username and password are correct you get "approved" and you do this code
            //Change activity to the checkInOutActivity
            Intent i=new Intent(context, CheckInOutActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity( i);
            res = "access ok";

        }else if(res.equals("Denied")){ //if they are incorrect you do "nothing"
            //Doesn't change activity
            res = "access refused";
        }
        return res;
    }


    //Get the information if the game has started or not
    public void appCheckStartStop() throws JSONException {

        OkHttpClient client = new OkHttpClient();
        String url = "http://"+IPAdress +"/checkstartstop"; //save the url of the server

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            res = response.body().string();
            //Log.d("You get ",res);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(res != null) { // to avoid the app crashes
            JSONArray arr = new JSONArray(res); //put the response string in an Json array
            JSONObject jObj = new JSONObject();
            jObj = arr.getJSONObject(0); //put the first object of the array in an Json object
            String statusGame = jObj.getString("startstop"); //take the string of the object related to "startstop", here the status of the game
            methodStr = jObj.getString("parameter"); //take the string of the object related to "parameter", here the methods of the game
            //Log.d("parameter is", methodStr);

            //Log.d("STATUS", statusGame);

            if (statusGame.equals("true")) { //if the game has started you do this code
                checkInOut.startstop = true; //save the status of the game in the variable from the CheckInOut activity
                checkInOut.method = methodStr; //save the methods of the game in the variable from the CheckInOut activity
                //Log.d("Now you have", statusGame);
                //appProduction();
            } else if (statusGame.equals("false")) { //if not you do this one
                checkInOut.startstop = false; //save the status of the game in the variable from the CheckInOut activity
                //Log.d("Now you have", statusGame);
            }
        }
        else{}
    }


    //Get the order list (for MRP)
    public void appProductionMRP() throws JSONException {

        OkHttpClient client = new OkHttpClient();
        String url = "http://"+IPAdress +"/getorderlist"; //save the url of the server

        Request request = new Request.Builder()
            .url(url)
            .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            checkInOut.orderList = response.body().string(); //save the order list in the variable from the CheckInOut activity
            //Log.d("Response executed", checkInOut.orderList );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get the inventory
    public void appInventory() throws JSONException {

        OkHttpClient client = new OkHttpClient();
        String url = "http://"+IPAdress +"/getinventory"; //save the url of the server

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            checkInOut.inventoryList = response.body().string(); //save the inventory in the variable from the CheckInOut activity
            //Log.d("Response executed", checkInOut.inventoryList );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Get the costumers order list
    public void appProduction() throws  JSONException {

        OkHttpClient client = new OkHttpClient();
        String url = "http://"+IPAdress +"/getcostreq"; //save the url of the server

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = null;

        try {
            response = client.newCall(request).execute();
            checkInOut.orderList = response.body().string(); //save the costumers oder list in the variable from the CheckInOut activity
            //Log.d("Response executed", checkInOut.orderList );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //this method gets executed if the request has finished
    @Override
    protected void onPostExecute(Void res) {

        //super.onPostExecute(result);
        //please put your code here.
        //Log.d("Response executed","Request finished");
    }

}