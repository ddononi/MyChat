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
  // 개발자 계정(sender account). C2DM 서비스 사용을 요청할 때 입력한 이메일 주소.
  private static final String sender = "everontech24@gmail.com";
  // 등록 아이디. C2DM 등록 절차를 거쳐 C2DMReceiver로부터 전달받습니다.
  private String registrationId = null;
  private String ipAddress = null;
  public static final String REGISTER = "kr.co.myChat.REGISTER";
  public static final String NEW_MESSAGE = "kr.co.myChat.NEW_MESSAGE";
  // 브로드캐스트 리시버.
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
          // 서버에 접속해 메시지를 받아온다.
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
  // MessageDeliverer 스레드에서 직접 채팅 목록을 업데이트 할 수 없으므로
  // 핸들러를 통해 업데이트
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
    // 채팅 목록을 출력할 리스트뷰
    messageTimeline = (ListView) findViewById(R.id.message_timeline);
    ArrayList<String> arrayList = new ArrayList<String>();
    mAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_list_item_1, arrayList);
    messageTimeline.setAdapter(mAdapter);
    IntentFilter filter = new IntentFilter(REGISTER);
    registerReceiver(br, filter);
    IntentFilter filter2 = new IntentFilter(NEW_MESSAGE);
    registerReceiver(br, filter2);
    // 메시지 전송 버튼을 눌렀을 때 처리
    Button sendButton = (Button) findViewById(R.id.send_button);
    sendButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        EditText messageBox = (EditText) findViewById(R.id.message_box);
        String msg = messageBox.getText().toString();
        messageBox.setText("");
        // 메시지 전송이란 특별한 것이 없습니다.
        // 서드파티 애플리케이션 서버에 HTTP상으로 메시지를 보내기만 하면 됩니다.
        // 그러면 서드파티 애플리케이션 서버는 등록된 단말에 새 메시지가
        // 도착했다고 알려줄 수 있게 C2DM 서버에 요청을 보낼 것입니다.
        // 서드파티에 메시지를 보낼 때 사용하는 URL 형식은 다음과 같습니다.
        // "http://<서버주소>/c2dm_messenger.php?registration_id=<등록 아이디>
        // &ip_address=<아이피주소>&mode=send&message=<메시지내용>"
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

  /* 서버 접속을 위한 옵션 메뉴를 만든다. */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "C2DM 사용 시작");
    menu.add(Menu.NONE, Menu.FIRST + 2, Menu.NONE, "C2DM 사용 중지");
    return (super.onCreateOptionsMenu(menu));
  }

  /* 옵션 메뉴를 통해 C2DM에 애플리케이션을 등록하고 해제할 수 있게 한다. */
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

  /* C2DM 서버에 애플리케이션 등록 */
  private void registerForC2DM() {
    Intent registrationIntent = new Intent(
        "com.google.android.c2dm.intent.REGISTER");
    registrationIntent.putExtra("app",
        PendingIntent.getBroadcast(this, 0, new Intent(), 0));
    registrationIntent.putExtra("sender", sender);
    startService(registrationIntent);
    Toast toast = Toast.makeText(this, "C2DM 서버에 등록을 요청합니다.",
        Toast.LENGTH_SHORT);
    toast.show();
  }

  /* C2DM 서버에 애플리케이션 등록해제 */
  private void unregisterForC2DM() {
    Intent unregIntent = new Intent(
        "com.google.and roid.c2dm.intent.UNREGISTER");
    unregIntent.putExtra("app",
        PendingIntent.getBroadcast(this, 0, new Intent(), 0));
    startService(unregIntent);
    Toast toast = Toast.makeText(this, "C2DM 서버에 등록해제를 요청합니다.",
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
    msg.obj = message; // 방금 보낸 채팅메시지를 obj 객체에 저장
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
        // 메시지 URL 인코딩. 서버에 메시지를 보낼 때 메시지에 띄어쓰기가
        // 포함돼 있을 경우에 메시지가 잘리는 것을 방지해준다.
        String encodedMessage = URLEncoder.encode(message, "utf-8");
        // 서드파티 애플리케이션 서버에 메시지 전송 요청을 하기 위한 URL
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
        // URL에 접속
        InputStream is = conn.getInputStream();
        is.close();
        conn.disconnect();
        // 채팅목록에 메시지를 출력하기 위해 핸들러에게 메시지 송신
        addMessage(message);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}