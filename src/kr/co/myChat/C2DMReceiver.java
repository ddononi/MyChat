package kr.co.myChat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class C2DMReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    // C2DM 등록/해제에 대한 결과를 수신할 때는 REGISTRATION 인텐트인지 확인한다.
    if (intent.getAction()
        .equals("com.google.android.c2dm.intent.REGISTRATION")) {
      // 등록에 대한 결과를 수신한 경우..
      handleRegistration(context, intent);
    } else if (intent.getAction().equals(
        "com.google.android.c2dm.intent.RECEIVE")) {
      // 메시지 수신
      Log.i(C2DMMessengerActivity.TAG, "com.google.android.c2dm.intent.RECEIVE");
      handleMessage(context, intent);
    }
  }

  private void handleMessage(Context context, Intent intent) {
    // 서버(PHP코드)쪽에서 new_id라는 키를 사용해서 new_id=$new_id와 같이
    // 구글의 C2DM 서버에 데이터를 전달했으므로 인텐트 안의
    // Extra에서 메시지를 가져오기 위해 사용할 키는 new_id다.
    if (intent.getStringExtra("new_id") != null) {
      Log.i(C2DMMessengerActivity.TAG,
          "new_id: " + intent.getStringExtra("new_id"));
      Intent intentNewMessage = new Intent(C2DMMessengerActivity.NEW_MESSAGE);
      // intentNewMessage.setAction(C2DMMessengerActivity.NEW_MESSAGE);
      intentNewMessage.putExtra("new_id", intent.getStringExtra("new_id"));
      context.sendBroadcast(intentNewMessage);
    }
  }

  private void handleRegistration(Context context, Intent intent) {
    String registration = intent.getStringExtra("registration_id");
    if (intent.getStringExtra("error") != null) {
      // 등록에 실패했으므로 다시 한 번 시도해야 한다.
    } else if (intent.getStringExtra("unregistered") != null) {
      // 등록을 해제했으므로 지금부터 전송되는 새 메시지는 거부당할 것이다.
      Toast toast = Toast.makeText(context, "C2DM 등록을 해제했습니다.",
          Toast.LENGTH_SHORT);
      toast.show();
    } else if (registration != null) {
      // 등록 아이디를 메시지를 송신하는 서드파티 서버에 보낸다.
      // 분리된 스레드상에서 완료돼야 한다.
      // 등록 아이디를 보내고 나면 모든 등록 절차가 완료된다.
      Toast toast = Toast.makeText(context, "C2DM 서버에 등록됐습니다.",
          Toast.LENGTH_SHORT);
      toast.show();
      String registrationId = intent.getStringExtra("registration_id");
      // 등록 아이디를 수신했다면 이를 채팅 서버에 전송해야 한다.
      // 채팅 서버에 전송하는 코드는 이 예제에만 특화된 것으로 참고할 것.
      // 참고로 아래 코드에서 ip_address를 서버로 보내는 이유는 단말을 구별하기
      // 위해서다. 보통은 회원 가입이나 단말의 전화번호를 식별값으로 사용할 수 있으나
      // 여기서는 간단한 예제 처리를 위해 단말의 맥어드레스를 식별값으로 사용한다.
      String ip_address = getLocalIpAddress();
      Log.i(C2DMMessengerActivity.TAG, "ip_address = " + ip_address);
      try {
        URL idRegistrationUrl = new URL(
            "http://ddononi.cafe24.com/C2DM/c2dm_messenger.php?registration_id="
                + registrationId + "&ip_address=" + ip_address
                + "&mode=register");
        HttpURLConnection conn = (HttpURLConnection) idRegistrationUrl
            .openConnection();
        conn.setConnectTimeout(60000);
        conn.setReadTimeout(60000);
        conn.setUseCaches(false);
        Log.i(C2DMMessengerActivity.TAG, "idRegistrationUrl = "
            + idRegistrationUrl.toString());
        // URL에 접속
        InputStream is = conn.getInputStream();
        is.close();
        conn.disconnect();
        Toast toast_reg = Toast.makeText(context, "서드파티 애플리케이션 서버에 사용자 "
            + "정보를 업데이트했습니다.", Toast.LENGTH_SHORT);
        toast_reg.show();
        // C2DMMessengerActivity에 C2DM 정보 갱신
        Intent it = new Intent();
        it.setAction(C2DMMessengerActivity.REGISTER);
        it.putExtra("registrationId", registrationId);
        it.putExtra("ipAddress", ip_address);
        context.sendBroadcast(it);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private String getLocalIpAddress() {
    try {
      for (Enumeration<NetworkInterface> en = NetworkInterface
          .getNetworkInterfaces(); en.hasMoreElements();) {
        NetworkInterface intf = en.nextElement();
        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
            .hasMoreElements();) {
          InetAddress inetAddress = enumIpAddr.nextElement();
          if (!inetAddress.isLoopbackAddress()) {
            return inetAddress.getHostAddress().toString();
          }
        }
      }
    } catch (SocketException e) {
      e.printStackTrace();
    }
    return null;
  }
}
