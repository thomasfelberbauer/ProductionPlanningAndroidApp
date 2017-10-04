package com.production_planning_game.eps_game;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;


public class CheckInOutActivity extends AppCompatActivity {

    private TextView statusInfo;
    public TextView orderProduction;
    Chronometer chronoProduction;
    Chronometer gameTime;
    Button checkInOutBtn;
    int thc=0;
    public TextView checkOut;
    long localTime;
    long Time;
    public static Boolean startstop = false;
    public static String method = "";
    public static String orderList = "";
    public static String inventoryList = "";
    public String orderAmount;
    public String orderSaved;
    int counter1 =0;
    int counter2 =0;
    int error =0;
    int counterOrder =0;
    public String stockInComingStr;
    public int stockNeeded = 1;
    public int stockInComing = 0;
    //public int stockOutComing;
    public TextView tool;
    public ImageView foto;
    public ImageButton info;
    public TabHost host;

    public JSONObject  jObj = new JSONObject();

    //nfc
    NfcAdapter adapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Context context;

    List<List> machProdArray = new ArrayList<List>();
    List<List> prodArray = new ArrayList<List>();
    List<String> mach1Product = new ArrayList<String>();
    List<String> mach2Product = new ArrayList<String>();
    List<String> mach3Product = new ArrayList<String>();
    List<String> mach4Product = new ArrayList<String>();
    List<String> mach5Product = new ArrayList<String>();

    List<String> orderArray = new ArrayList<String>();

//set the color
    int red=Color.parseColor("#ff0000");
    int blue=Color.parseColor("#00bfff");
    int green=Color.parseColor("#32cd32");


    LoginActivity login = new LoginActivity();
    //HttpRequest request = new HttpRequest(getApplicationContext()); <------ this code crash the app

    android.os.Handler customHandler = new android.os.Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);

        //set the visual elements
        statusInfo = (TextView) findViewById(R.id.statusInfo);
        orderProduction = (TextView) findViewById(R.id.orderProduction);
        checkInOutBtn = (Button) findViewById(R.id.checkInOutBtn);
        checkOut = (TextView) findViewById(R.id.checkOut);
        chronoProduction = (Chronometer) findViewById(R.id.chronoProduction);
        gameTime = (Chronometer) findViewById(R.id.chronoGame);


//--------------------------------------------------------------Initialise the screen--------------------------------------------------------------
        Toast.makeText(CheckInOutActivity.this, login.machinenumber, Toast.LENGTH_SHORT).show(); //Show which machine you are
        orderProduction.setText("Relax & wait for more information"); //Initialise the message for the production
//-------------------------------------------------------------------------------------------------------------------------------------------------

        customHandler.postDelayed(updateTimerThread, 0); //This line call the function to do the loop


//-------------------------------------------------------------------------------------
        /*mach1Product.add(0, "A0");
        mach1Product.add("null");
        mach1Product.add("null");

        mach2Product.add(0, "B0");
        mach2Product.add("null");
        mach2Product.add("null");

        mach3Product.add(0, "C0");
        mach3Product.add("null");
        mach3Product.add("null");

        mach4Product.add(0, "D0");
        mach4Product.add("D1");
        mach4Product.add("null");

        mach5Product.add(0, "E0");
        mach5Product.add("E1");
        mach5Product.add("E2");

        machProdArray.add(0, mach1Product);
        machProdArray.add(mach2Product);
        machProdArray.add(mach3Product);
        machProdArray.add(mach4Product);
        machProdArray.add(mach5Product);

        prodArray.add(0, machProdArray.get(Integer.parseInt(login.nbrM)-1));
        //Log.d(" Array ", machProdArray.toString());
        //Log.d("first product is", prodArray.get(0).get(0).toString());
        */

//--------------------------------------------------------------NFC--------------------------------------------------------------
        context = this;
        adapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[]{tagDetected};
//----------------------------------------------------------------------------------------------------------------------------


        tool =(TextView) findViewById(R.id.toolBarMac);

        host =(TabHost)findViewById(R.id.tabby) ;
        host.setVisibility(View.GONE);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Cutting");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Cutting");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Surface ");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Paint");
        host.addTab(spec);

        spec = host.newTabSpec("Tab 4");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Assembly");
        host.addTab(spec);

        spec = host.newTabSpec("Tab 5");
        spec.setContent(R.id.tab5);
        spec.setIndicator("Final assembly");
        host.addTab(spec);

        foto =(ImageView) findViewById(R.id.fotaca);
        info =(ImageButton) findViewById(R.id.imageButton);
        tool.setText(login.machinenumber);

        setTabColor(host);
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String arg0) {

                setTabColor(host);
            }
        });


//--------------------------------------------------------------Set the info button (on the right)--------------------------------------------------------------

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (v==info&&thc%2==0)
                {
                    host.setVisibility(View.VISIBLE);
                    info.setImageResource(R.mipmap.ic_info2);
                    Vibrator vf = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vf.vibrate(200);
                    thc++;
                }
                else
                {
                    host.setVisibility(View.GONE);
                    info.setImageResource(R.mipmap.ic_launcher);
                    Vibrator vrt = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    vrt.vibrate(200);
                    //vrt.vibrate({125,75,125,275,200,275,125,75,125,275,200,600,200,600});
                    //long[] pattern = {80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,80,320,160,320,160,320,160,320};
                    //vrt.vibrate(pattern, -1);
                    thc++;
                }
            }
        });

        switch (login.machinenumber)
        {
            case "machine1":foto.setImageResource(R.drawable.ws1);
                tool.setText("Machine 1: Cutting");
                break;
            case "machine2":foto.setImageResource(R.drawable.ws2);
                tool.setText("Machine 2: Surface treatment");
                break;
            case "machine3":foto.setImageResource(R.drawable.ws3);
                tool.setText("Machine 3: Paint");
                break;
            case "machine4":foto.setImageResource(R.drawable.ws4);
                tool.setText("Machine 4: Assembly");
                break;
            case "machine5":foto.setImageResource(R.drawable.image_apaisadooooooooo_ws5);
                tool.setText("Machine 5: Final assembly");
                break;
           default:
               break;
        }

//------------------------------------------------------------------------Solution to use the button instead of the NFC tag to check in or check out-----------------------------------------------------
        /*checkInOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    //if (message.equals(orderArray.get(0).toString())) { //check if the tag is the good one, the good product
                    if (stockInComing >= stockNeeded) { //check if you have enough pieces to produce
                        if (counter1 % 2 == 0) {
                            HttpRequest req1 = new HttpRequest(getApplicationContext());
                            req1.requestName = "appstatus";
                            req1.var3 = "working";
                            req1.execute();
                            statusInfo.setText("Producing");

                            ChronoStart();

                            checkInOutBtn.setText("Producing");
                            checkInOutBtn.setBackgroundColor(green);

                        } else {
                            HttpRequest req = new HttpRequest(getApplicationContext());
                            req.requestName = "appstatus";
                            req.var3 = "idle";
                            req.execute();
                            statusInfo.setText("Idle");

                            //stockOutComing = Integer.parseInt(orderAmount);

                            ChronoStop();

                            checkInOutBtn.setText("Idle");
                            checkInOutBtn.setBackgroundColor(blue);

                            if (orderArray.size() > 0) {
                                orderArray.remove(0); // delete the product order when is done
                            }
                            if (orderArray.size() == 0) {
                                orderProduction.setText("Relax & wait for more information"); //initialise the message production
                                //stockInComing = 0;
                            }else {
                                orderProduction.setText("What to produce:\n"+orderArray.get(0)+":"+ orderAmount + " pieces" );
                                checkInOutBtn.setBackgroundColor(red);
                            }
                        }
                        counter1++;
                    } else if (stockInComing == 0) {
                        error ++;
                        orderSaved = orderProduction.getText().toString();
                        orderProduction.setText("You're not allowed to produce now");
                        //Log.d("on en est là", orderSaved);
                        new CountDownTimer(3000, 500) {
                            public void onTick(long millisUntilFinished) {

                            }
                            public void onFinish() {
                                orderProduction.setText(orderSaved);
                            }
                        }.start();
                    }
                /*}else{
                    error ++;
                    orderSaved = orderProduction.getText().toString();
                    orderProduction.setText("Wrong product");

                    new CountDownTimer(3000, 200) {

                        public void onTick(long millisUntilFinished) {
                        }
                        public void onFinish() {
                            orderProduction.setText(orderSaved);
                        }
                    }.start();
                }*//*
                }catch (Exception e)
                {
                    error ++;
                    orderSaved = orderProduction.getText().toString();
                    orderProduction.setText("You can't produce here");
                    new CountDownTimer(3000, 500) {

                        public void onTick(long millisUntilFinished) {
                        }
                        public void onFinish() {
                            orderProduction.setText(orderSaved);
                        }
                    }.start();
                }
            }
        });*/

//-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    }

//--------------------------------------------------------------This function run the checkstartstop() every second--------------------------------------------------------------
    private Runnable updateTimerThread = new Runnable()
    {
        public void run() {

            checkstartstop();

            if (method.equals("MRP")){
                try {
                    getOrderProd();
                    //getInventory();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            customHandler.postDelayed(this, 500);
        }
    };


//--------------------------------------------------------------This function check if the game has started and if yes, get the order list and start the local time-------------------------------
    public void checkstartstop()
    {
        HttpRequest req = new HttpRequest(getApplicationContext());
        req.requestName = "checkstartstop";
        req.execute();
        Log.d("response", startstop.toString());

        if(startstop == false){
            counter2 = 0;
            counter1 =0;
            counterOrder =0;
            stockNeeded = 1;
            stockInComing = 0;
            orderArray.clear();
            orderProduction.setText("Relax & wait for more information");
            checkInOutBtn.setText("Idle");
            checkInOutBtn.setBackgroundColor(blue);
            ChronoStop();
            chronoProduction.setBase(SystemClock.elapsedRealtime());
            gameTime.stop();
            gameTime.setBase(SystemClock.elapsedRealtime());

        }else if(startstop == true && counter2 == 0){
            if (method.equals("MRP")){
                gameTime.setBase(SystemClock.elapsedRealtime());
                gameTime.start();

                HttpRequest req2 = new HttpRequest(getApplicationContext());
                req2.requestName = "getorderlist";
                req2.execute();

                counter2++;
            }else if (method.equals("CONWIP") || method.equals("KANBAN")){
                gameTime.setBase(SystemClock.elapsedRealtime());
                gameTime.start();

                HttpRequest req2 = new HttpRequest(getApplicationContext());
                req2.requestName = "getcostreq";
                req2.execute();

                counter2++;
            }

        }else if(startstop == true){
            localTime = (SystemClock.elapsedRealtime() - gameTime.getBase())/1000;
            String t = "" + localTime;
            Log.d("The time is", t);
        }
    }


//--------------------------------------------------------------get the order list and say when you have to produce------------------------------------------------------------------
    private void getOrderProd() throws JSONException {

        //Log.d("oderlist", orderList );

        JSONArray arr     = new JSONArray(orderList);
        //JSONObject  jObj = new JSONObject();
        jObj = arr.getJSONObject(counterOrder);

        String  orderTimeStr = jObj.getString("time");
        int     orderTimeInt = Integer.parseInt(orderTimeStr);
        String  orderProduct = jObj.getString("product");
        orderAmount  = jObj.getString("amount");
        String  orderMachine = jObj.getString("machine");
        String  orderId = jObj.getString("orderID");

        //Log.d("user", login.machinenumber);
        //Log.d("We have", orderProduct);
        //Log.d("We have", orderMachine);

        if(localTime == orderTimeInt){
            counterOrder++;
            if(login.machinenumber.equals(orderMachine)){
                orderArray.add(orderProduct); //store the product order in an array
                stockNeeded = Integer.parseInt(orderAmount);
                //Log.d("Needed stock is", orderAmount);
                stockInComing = 8;
                //Log.d("after added", orderArray.toString());
                orderProduction.setText("What to produce:\n"+orderArray.get(0)+":"+ orderAmount + " pieces");
                if (orderArray.size() == 1) {
                    checkInOutBtn.setBackgroundColor(red);
                }
                //Log.d("We have", orderProduct);
                //Log.d("We have", orderAmount);
            }
        }
    }

//--------------------------------------------------------------get the inventory--------------------------------------------------------------
/*
    private void getInventory() throws JSONException {

        HttpRequest req = new HttpRequest(getApplicationContext());
        req.requestName = "getinventory";
        req.execute();

        JSONArray   arr     = new JSONArray(inventoryList);
        jObj = arr.getJSONObject(0);

        if(orderArray.size() >0){
            if(orderArray.get(0).equals("A0")){

                stockInComing = Integer.parseInt(orderArray.get(0));

            }else if(orderArray.get(0).equals("B0")){

                stockInComingStr = jObj.getString("A0");
                stockInComing = Integer.parseInt(stockInComingStr);

            }else if(orderArray.get(0).equals("C0")){

                stockInComingStr = jObj.getString("B0");
                stockInComing = Integer.parseInt(stockInComingStr);

            }else if(orderArray.get(0).equals("D0")){

                stockInComingStr = jObj.getString("C0");
                stockInComing = Integer.parseInt(stockInComingStr);

            }else if(orderArray.get(0).equals("D1")){

                stockInComingStr = jObj.getString("C0");
                stockInComing = Integer.parseInt(stockInComingStr);

            }else if(orderArray.get(0).equals("E0")){

                stockInComingStr = jObj.getString("D0");
                stockInComing = Integer.parseInt(stockInComingStr);

            }else if(orderArray.get(0).equals("E1")){

                stockInComingStr = jObj.getString("D0");
                stockInComing = Integer.parseInt(stockInComingStr);

            }else if(orderArray.get(0).equals("E2")){

                stockInComingStr = jObj.getString("D1");
                stockInComing = Integer.parseInt(stockInComingStr);
            }
            //Log.d("inventory is", stockInComingStr);
        }
    }
    */
//----------------------------------------------------------------info button tabhost-----------------------------------------------------------------------------------------------------------------

    public static void setTabColor(TabHost tabhost) {
        for(int i=0;i<tabhost.getTabWidget().getChildCount();i++) {
            tabhost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#e0e0e0")); //unselected
        }
        tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).setBackgroundColor(Color.parseColor("#00bfff")); // selected
    }

//---------------------------------------------------------------------------------------------NFC detection and code--------------------------------------------------------------
    protected void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] messages;
            if (rawMsgs != null) {
                messages = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    messages[i] = (NdefMessage) rawMsgs[i];
                    NdefRecord record = messages[i].getRecords()[i];
                    byte[] idd = record.getId();
                    short tnf = record.getTnf();
                    byte[] type = record.getType();
                    String message = getTextData(record.getPayload());
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();


                    try {
                        //if (message.equals(prodArray.get(0).get(0).toString()) || message.equals(prodArray.get(0).get(1).toString()) ||message.equals(prodArray.get(0).get(2).toString())) {
                            //check if the tag is for this machine
                        if (message.equals(orderArray.get(0).toString())) { //check if the tag is the good one, the good product
                            if (stockInComing >= stockNeeded) { //check if you have enough pieces to produce
                                if (counter1 % 2 == 0) {
                                    HttpRequest req1 = new HttpRequest(getApplicationContext());
                                    req1.requestName = "appstatus";
                                    req1.var3 = "working";
                                    req1.execute();
                                    statusInfo.setText("Producing");

                                    ChronoStart();

                                    checkInOutBtn.setText("Producing");
                                    checkInOutBtn.setBackgroundColor(green);

                                }else {
                                    HttpRequest req = new HttpRequest(getApplicationContext());
                                    req.requestName = "appstatus";
                                    req.var3 = "idle";
                                    req.execute();
                                    statusInfo.setText("Idle");

                                    //stockOutComing = Integer.parseInt(orderAmount);

                                    ChronoStop();

                                    checkInOutBtn.setText("Idle");
                                    checkInOutBtn.setBackgroundColor(blue);

                                    if (orderArray.size() > 0) {
                                        orderArray.remove(0); // delete the product order when is done
                                    }
                                    if (orderArray.size() == 0) {
                                        orderProduction.setText("Relax & wait for more information"); //initialise the message production
                                        stockInComing = 0;
                                    }else {
                                        orderProduction.setText("What to produce:\n"+orderArray.get(0)+":"+ orderAmount + " pieces" );
                                        checkInOutBtn.setBackgroundColor(red);
                                    }
                                }
                                counter1++;
                            } else if (stockInComing == 0) {
                                error ++;
                                orderSaved = orderProduction.getText().toString();
                                orderProduction.setText("You're not allowed to produce now");
                                //Log.d("on en est là", orderSaved);
                                new CountDownTimer(3000, 500) {
                                     public void onTick(long millisUntilFinished) {

                                     }
                                     public void onFinish() {
                                         orderProduction.setText(orderSaved);
                                     }
                                }.start();
                            }
                        }else{
                            error ++;
                            orderSaved = orderProduction.getText().toString();
                            orderProduction.setText("Wrong product");

                            new CountDownTimer(3000, 200) {

                                public void onTick(long millisUntilFinished) {
                                }
                                public void onFinish() {
                                        orderProduction.setText(orderSaved);
                                    }
                            }.start();
                        }
                        /*}else{
                            orderSaved = orderProduction.getText().toString();
                            orderProduction.setText("Wrong tag");

                            new CountDownTimer(3000, 500) {

                                public void onTick(long millisUntilFinished) {
                                }
                                public void onFinish() {
                                    orderProduction.setText(orderSaved);
                                }
                            }.start();
                        }*/
                    }
                    catch (Exception e)
                    {
                        error ++;
                        orderSaved = orderProduction.getText().toString();
                        orderProduction.setText("You can't produce here");
                        new CountDownTimer(3000, 500) {

                            public void onTick(long millisUntilFinished) {
                            }
                            public void onFinish() {
                                orderProduction.setText(orderSaved);
                            }
                        }.start();
                    }
                }
            }
        }
    }


//--------------------------------------------------------------Necessary for the detection and read of the NFC--------------------------------------------------------------
    private String getTextData(byte[] payload) {
        if(payload == null)
            return null;
        try {
            String encoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
            int langageCodeLength = payload[0] & 0077;
            return new String(payload, langageCodeLength + 1, payload.length - langageCodeLength - 1, encoding);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void onPause(){
        super.onPause();
        WriteModeOff();
    }
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        writeMode = true;
        adapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }

    private void WriteModeOff(){
        writeMode = false;
        adapter.disableForegroundDispatch(this);
    }
//-----------------------------------------------------------------------------------------------------------------------------------------------------------



//--------------------------------------------------------------This function start the local chrono of production--------------------------------------------------------------
    public void ChronoStart(){
                chronoProduction.setBase(SystemClock.elapsedRealtime() + Time);
                chronoProduction.start();
    }

//--------------------------------------------------------------This function stop the local chrono of production--------------------------------------------------------------
    public  void ChronoStop(){
                //chronoProduction.setBase(SystemClock.elapsedRealtime()+time);
                chronoProduction.stop();
                chronoProduction.setBase(SystemClock.elapsedRealtime());
    }


}
