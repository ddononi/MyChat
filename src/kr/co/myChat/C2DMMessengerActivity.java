package kr.co.myChat;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import kr.co.myChat.R;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class C2DMMessengerActivity extends Activity {
  public static final String TAG = "C2DMMessengerActivity";
  // ������ ����(sender account). C2DM ���� ����� ��û�� �� �Է��� �̸��� �ּ�.
  private static final String sender = "everontech24@gmail.com";
  // ��� ���̵�. C2DM ��� ������ ���� C2DMReceiver�κ��� ���޹޽��ϴ�.
  private String registrationId = null;
  private String ipAddress = null;
  public static final String REGISTER = "kr.co.myChat.REGISTER";
  public static final String NEW_MESSAGE = "kr.co.myChat.NEW_MESSAGE";
  // ��ε�ĳ��Ʈ ���ù�.
  BroadcastReceiver br = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.e(TAG, "onReceive is here");
      if (intent.getAction().equals(REGISTER)) {
        registrationId = intent.getExtras().getString("registrationId");
        ipAddress = intent.getExtras().getString("ipAddress");
      } else if (intent.getAction().equals(NEW_MESSAGE)) {
        Log.e(TAG, "NEW_MESSAGE is here");
        String new_id = intent.getExtras().getString("new_id");
        if (new_id != null) {
          // ������ ������ �޽����� �޾ƿ´�.
          // addMessage(new_id);
          try {
            URL getMessageURL = new URL(
                "http://ddononi.cafe24.com/C2DM/c2dm_messenger.php?mode=read&id="
                    + new_id);
            HttpURLConnection conn = (HttpURLConnection) getMessageURL
                .openConnection();
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            conn.setUseCaches(false);
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            byte data[] = new byte[1024];
            StringBuffer sb = new StringBuffer();
            while (bis.read(data, 0, 1024) != -1) {
              sb.append(new String(data, "utf-8"));
            }
            bis.close();
            is.close();
            conn.disconnect();
            String sdata = sb.toString().trim();
            Log.i(TAG, "sdata:" + sdata);
            String[] arr_sdata = sdata.split("\n");
            String ip_address_from = arr_sdata[0];
            String message = arr_sdata[1];
            addMessage(message, ip_address_from);
          } catch (MalformedURLException e) {
            e.printStackTrace();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }
  };
  private ListView messageTimeline;
  private ArrayAdapter<String> mAdapter;
  private static final int ADD_MESSAGE = 0;
  // MessageDeliverer �����忡�� ���� ä�� ����� ������Ʈ �� �� �����Ƿ�
  // �ڵ鷯�� ���� ������Ʈ
  Handler chatHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
      case ADD_MESSAGE:
        mAdapter.add((String) msg.obj);
        messageTimeline.setSelection(messageTimeline.getCount() - 1);
        break;
      }
    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    // ä�� ����� ����� ����Ʈ��
    messageTimeline = (ListView) findViewById(R.id.message_timeline);
    ArrayList<String> arrayList = new ArrayList<String>();
    mAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, arrayList);
    messageTimeline.setAdapter(mAdapter);
    IntentFilter filter = new IntentFilter(REGISTER);
    registerReceiver(br, filter);
    IntentFilter filter2 = new IntentFilter(NEW_MESSAGE);
    registerReceiver(br, filter2);
    // �޽��� ���� ��ư�� ������ �� ó��
    Button sendButton = (Button) findViewById(R.id.send_button);
    sendButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        EditText messageBox = (EditText) findViewById(R.id.message_box);
        String msg = messageBox.getText().toString();
        messageBox.setText("");
        // �޽��� �����̶� Ư���� ���� �����ϴ�.
        // ������Ƽ ���ø����̼� ������ HTTP������ �޽����� �����⸸ �ϸ� �˴ϴ�.
        // �׷��� ������Ƽ ���ø����̼� ������ ��ϵ� �ܸ��� �� �޽�����
        // �����ߴٰ� �˷��� �� �ְ� C2DM ������ ��û�� ���� ���Դϴ�.
        // ������Ƽ�� �޽����� ���� �� ����ϴ� URL ������ ������ �����ϴ�.
        // "http://<�����ּ�>/c2dm_messenger.php?registration_id=<��� ���̵�>
        // &ip_address=<�������ּ�>&mode=send&message=<�޽�������>"
        if (msg != null && msg.length() > 0) {
          MessageDeliverer md = new MessageDeliverer(
              C2DMMessengerActivity.this, msg, ipAddress, registrationId);
          md.start();
        }
      }
    });
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (registrationId != null) {
      unregisterForC2DM();
    }
  }

  /* ���� ������ ���� �ɼ� �޴��� �����. */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "C2DM ��� ����");
    menu.add(Menu.NONE, Menu.FIRST + 2, Menu.NONE, "C2DM ��� ����");
    return (super.onCreateOptionsMenu(menu));
  }

  /* �ɼ� �޴��� ���� C2DM�� ���ø����̼��� ����ϰ� ������ �� �ְ� �Ѵ�. */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return (itemCallback(item) || super.onOptionsItemSelected(item));
  }

  private boolean itemCallback(MenuItem item) {
    switch (item.getItemId()) {
    case Menu.FIRST + 1:
      registerForC2DM();
      break;
    case Menu.FIRST + 2:
      unregisterForC2DM();
      break;
    }
    return false;
  }

  /* C2DM ������ ���ø����̼� ��� */
  private void registerForC2DM() {
    Intent registrationIntent = new Intent(
        "com.google.android.c2dm.intent.REGISTER");
    registrationIntent.putExtra("app",
        PendingIntent.getBroadcast(this, 0, new Intent(), 0));
    registrationIntent.putExtra("sender", sender);
    startService(registrationIntent);
    Toast toast = Toast.makeText(this, "C2DM ������ ����� ��û�մϴ�.",
        Toast.LENGTH_SHORT);
    toast.show();
  }

  /* C2DM ������ ���ø����̼� ������� */
  private void unregisterForC2DM() {
    Intent unregIntent = new Intent(
        "com.google.and roid.c2dm.intent.UNREGISTER");
    unregIntent.putExtra("app",
        PendingIntent.getBroadcast(this, 0, new Intent(), 0));
    startService(unregIntent);
    Toast toast = Toast.makeText(this, "C2DM ������ ��������� ��û�մϴ�.",
        Toast.LENGTH_SHORT);
    toast.show();
  }

  private void addMessage(String message) {
    addMessage(message, this.ipAddress);
  }

  private void addMessage(String message, String ipAddress) {
    message = ipAddress + ": " + message;
    Message msg = chatHandler.obtainMessage();
    msg.what = ADD_MESSAGE;
    msg.arg1 = 0;
    msg.arg2 = 0;
    msg.obj = message; // ��� ���� ä�ø޽����� obj ��ü�� ����
    chatHandler.sendMessage(msg);
  }

  class MessageDeliverer extends Thread {
    Context context;
    String ipAddress;
    String message;
    String registrationId;

    public MessageDeliverer(Context context, String message, String ipAddress,
        String registrationId) {
      this.context = context;
      this.message = message;
      this.ipAddress = ipAddress;
      this.registrationId = registrationId;
    }

    public void run() {
      if (ipAddress == null || ipAddress.equals("")) {
        Log.e(TAG, "Macaddress is invalid");
        return;
      }
      if (message == null || message.equals("")) {
        Log.e(TAG, "Message is empty");
        return;
      }
      try {
        // �޽��� URL ���ڵ�. ������ �޽����� ���� �� �޽����� ���Ⱑ
        // ���Ե� ���� ��쿡 �޽����� �߸��� ���� �������ش�.
        String encodedMessage = URLEncoder.encode(message, "utf-8");
        // ������Ƽ ���ø����̼� ������ �޽��� ���� ��û�� �ϱ� ���� URL
        URL idRegistrationUrl = new URL(
            "http://ddononi.cafe24.com/C2DM/c2dm_messenger.php?"
                + "registration_id=" + registrationId + "&ip_address="
                + ipAddress + "&mode=send" + "&message=" + encodedMessage);
        HttpURLConnection conn = (HttpURLConnection) idRegistrationUrl
            .openConnection();
        conn.setConnectTimeout(60000);
        conn.setReadTimeout(60000);
        conn.setUseCaches(false);
        Log.i(TAG, "Message send URL: " + idRegistrationUrl.toString());
        // URL�� ����
        InputStream is = conn.getInputStream();
        is.close();
        conn.disconnect();
        // ä�ø�Ͽ� �޽����� ����ϱ� ���� �ڵ鷯���� �޽��� �۽�
        addMessage(message);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}