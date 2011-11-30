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
    // C2DM ���/������ ���� ����� ������ ���� REGISTRATION ����Ʈ���� Ȯ���Ѵ�.
    if (intent.getAction()
        .equals("com.google.android.c2dm.intent.REGISTRATION")) {
      // ��Ͽ� ���� ����� ������ ���..
      handleRegistration(context, intent);
    } else if (intent.getAction().equals(
        "com.google.android.c2dm.intent.RECEIVE")) {
      // �޽��� ����
      Log.i(C2DMMessengerActivity.TAG, "com.google.android.c2dm.intent.RECEIVE");
      handleMessage(context, intent);
    }
  }

  private void handleMessage(Context context, Intent intent) {
    // ����(PHP�ڵ�)�ʿ��� new_id��� Ű�� ����ؼ� new_id=$new_id�� ����
    // ������ C2DM ������ �����͸� ���������Ƿ� ����Ʈ ����
    // Extra���� �޽����� �������� ���� ����� Ű�� new_id��.
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
      // ��Ͽ� ���������Ƿ� �ٽ� �� �� �õ��ؾ� �Ѵ�.
    } else if (intent.getStringExtra("unregistered") != null) {
      // ����� ���������Ƿ� ���ݺ��� ���۵Ǵ� �� �޽����� �źδ��� ���̴�.
      Toast toast = Toast.makeText(context, "C2DM ����� �����߽��ϴ�.",
          Toast.LENGTH_SHORT);
      toast.show();
    } else if (registration != null) {
      // ��� ���̵� �޽����� �۽��ϴ� ������Ƽ ������ ������.
      // �и��� ������󿡼� �Ϸ�ž� �Ѵ�.
      // ��� ���̵� ������ ���� ��� ��� ������ �Ϸ�ȴ�.
      Toast toast = Toast.makeText(context, "C2DM ������ ��ϵƽ��ϴ�.",
          Toast.LENGTH_SHORT);
      toast.show();
      String registrationId = intent.getStringExtra("registration_id");
      // ��� ���̵� �����ߴٸ� �̸� ä�� ������ �����ؾ� �Ѵ�.
      // ä�� ������ �����ϴ� �ڵ�� �� �������� Ưȭ�� ������ ������ ��.
      // ����� �Ʒ� �ڵ忡�� ip_address�� ������ ������ ������ �ܸ��� �����ϱ�
      // ���ؼ���. ������ ȸ�� �����̳� �ܸ��� ��ȭ��ȣ�� �ĺ������� ����� �� ������
      // ���⼭�� ������ ���� ó���� ���� �ܸ��� �ƾ�巹���� �ĺ������� ����Ѵ�.
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
        // URL�� ����
        InputStream is = conn.getInputStream();
        is.close();
        conn.disconnect();
        Toast toast_reg = Toast.makeText(context, "������Ƽ ���ø����̼� ������ ����� "
            + "������ ������Ʈ�߽��ϴ�.", Toast.LENGTH_SHORT);
        toast_reg.show();
        // C2DMMessengerActivity�� C2DM ���� ����
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
