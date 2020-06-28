package com.example.koend.pinitadmin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.eclipse.paho.android.service.*;
import org.eclipse.paho.client.mqttv3.*;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

//Main Activity of the application
public class MainActivity extends AppCompatActivity{

    private ArrayList<String> announcements;
    private ArrayAdapter<String> announcementsAdapter;

    private ArrayList<String> agenda;
    private ArrayAdapter<String> agendaAdapter;

    private Button button;
    private String scanContent;
    private Menu menu;
    private MqttAndroidClient client;
    private EditText messageSend;
    private EditText header;
    private EditText editText1;
    private EditText editText2;
    private MqttConnectOptions options;
    private String firstName;
    private String lastName;
    private String userId;

    //Creates the menu when application starts up
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);

        this.menu = menu;
        return true;

    }

    //Creates a layout and hides the menu when application starts up
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);


        announcements = new ArrayList<>();
        agenda = new ArrayList<>();

        connectMqtt();
        setContentView(R.layout.activity_main);
    }

    public void connectMqtt()
    {
        options = new MqttConnectOptions();
        options.setUserName("AndroidApp");
        options.setPassword("innovate".toCharArray());
        String clientId = MqttClient.generateClientId();

        client = new MqttAndroidClient(this.getApplicationContext(), "tcp://194.171.181.139:1883",
                clientId);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("tag","Succes");
                    try{
                        final String topic = "/PinIt/Inf/#";
                        IMqttToken subToken = client.subscribe(topic, 1);
                        subToken.setActionCallback(new IMqttActionListener(){
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken)
                            {
                                Log.d("succes","succes");
                            }
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                            {
                                Log.d("fail","fail");
                            }
                        });
                    }catch (MqttException e)
                    {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("test","Failure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void goToMain_Page(){
        setContentView(R.layout.news_board);

        getSupportActionBar().show();
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        announcementsAdapter = new ArrayAdapter<String>(this, R.layout.list_layout, announcements);
        ListView announcementListView = (ListView) findViewById(R.id.announcement_list);
        announcementListView.setAdapter(announcementsAdapter);
        if(announcements.size() == 0) {
            String topic = "/PinIt/Inf/ReminderRequest/" + androidId;

            String payload = "test";
            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }

        agendaAdapter = new ArrayAdapter(this, R.layout.list_layout, agenda);
        ListView agendaListView = (ListView) findViewById(R.id.agenda_list);
        agendaListView.setAdapter(agendaAdapter);
        if(agenda.size() == 0){
            String topic = "/PinIt/Inf/AgendaRequest/" + androidId;

            String payload = "test";
            byte[] encodedPayload = new byte[0];
            try {
                encodedPayload = payload.getBytes("UTF-8");
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            } catch (UnsupportedEncodingException | MqttException e) {
                e.printStackTrace();
            }
        }

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                connectMqtt();
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String AndroidID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if (topic.contains("ReminderMessages/" + AndroidID)) {

                    String[] messageArray = message.toString().split("@");
                    announcements.add(messageArray[0] + "\n     Posted by: " + messageArray[1] + "\n\n");
                    announcementsAdapter.notifyDataSetChanged();
                    Collections.sort(announcements, Collections.<String>reverseOrder());
                }
                if (topic.contains("ReminderMessagesTMP/")) {

                    String[] messageArray = message.toString().split("@");
                    announcements.add(messageArray[0] + "\n     Posted by: " + messageArray[1] + "\n\n");
                    announcementsAdapter.notifyDataSetChanged();
                    Collections.sort(announcements, Collections.<String>reverseOrder());
                }
                if (topic.contains("AgendaMessages/" + AndroidID)) {
                    String[] messageArray = message.toString().split("@");
                    agenda.add(messageArray[0] + "\n\n" + messageArray[1] + "\n");
                    agendaAdapter.notifyDataSetChanged();
                    Collections.sort(agenda, Collections.<String>reverseOrder());
                }
                if (topic.contains("AgendaMessagesTMP/")) {
                    String[] messageArray = message.toString().split("@");
                    agenda.add(messageArray[0] + "\n\n" + messageArray[1] + "\n");
                    agendaAdapter.notifyDataSetChanged();
                    Collections.sort(agenda, Collections.<String>reverseOrder());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }


    //Gives functionality to the different menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.exit_app) {

            finish();
            return true;

            //Goes to news board when pressed and creates two listview items
        } else if (id == R.id.main_page){
            goToMain_Page();

            //Goes to Qr generator when pressed
        }else if (id == R.id.qr_generator){
            buildQrGenerator();        }

        return super.onOptionsItemSelected(item);
    }
    public void QrGeneratorView(View view){
        getSupportActionBar().show();
        buildQrGenerator();
    }
    public void buildQrGenerator(){
        setContentView(R.layout.qrgeneratorlayout);
        final Context context = this;
        editText1 = (EditText) this.findViewById(R.id.editText2);
        editText2 = (EditText) this.findViewById(R.id.editText3);
        button = (Button) this.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){

            //Generates a click event where a Qr code is being generated from the input given
            @Override
            public void onClick(View view){

                String topic = "/PinIt/Inf/NewAccount/";
                String payload = "1" + "@" + editText1.getText().toString() + "@" + editText2.getText().toString();
                firstName = editText1.getText().toString();
                lastName = editText2.getText().toString();

                if(firstName.equals("") || lastName.equals(""))
                {
                    Toast.makeText(context, "Please enter a firstname or a lastname!",Toast.LENGTH_LONG).show();
                }
                else {
                    byte[] encodedPayload;
                    try {
                        Log.d("", "yes");
                        encodedPayload = payload.getBytes("UTF-8");
                        MqttMessage message = new MqttMessage(encodedPayload);
                        client.publish(topic, message);
                        QrGenerator();

                    } catch (UnsupportedEncodingException | MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void QrGenerator()
    {
        final Context context = this;

        client.setCallback(new MqttCallback()
        {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topicResponse, MqttMessage messageReceived) throws Exception {

                if(topicResponse.contains("NewAccountResponse"))
                {
                    userId = messageReceived.toString();
                    String text2Qr = messageReceived.toString()+ "@" + firstName + "@" + lastName;
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try{
                        BitMatrix bitMatrix = multiFormatWriter.encode(text2Qr, BarcodeFormat.QR_CODE, 200, 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        Intent intent = new Intent(context, QrActivity.class);
                        intent.putExtra("pic",bitmap);
                        context.startActivity(intent);
                    }catch (WriterException e){
                        e.printStackTrace();
                    }
                    Log.d(topicResponse ,messageReceived.toString());
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }


}
