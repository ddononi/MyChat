package kr.co.myChat;

import java.io.IOException;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * ����� ���̵� ��� ��Ƽ��Ƽ
 */
public class RegisterActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		// ����ȯ�� �������� �̸��� �����´�.

		// ������Ʈ ��ŷ
		final EditText et = (EditText) findViewById(R.id.id);
		Button btn = (Button) findViewById(R.id.submit);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// ���̵� �Է��޴��� Ȯ���Ѵ�.
				CharSequence id = et.getText();
				if (TextUtils.isEmpty(id)) {
					// ���̵� ������ �佺Ʈ�� ���´�.
					Toast.makeText(RegisterActivity.this, "���̵� �Է��ϼ���",
							Toast.LENGTH_SHORT);
					return;
				}

				// ���̵� ����ȯ�� ������ �������� ������ ����Ѵ�.
				SharedPreferences sp = getSharedPreferences(SHARED,
						MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("id", id.toString()); // ���̵� ����
				editor.commit(); // �����ҷ��� Ŀ���� �ʼ�

			}
		});

	}

	private class AsyncTaskeRegister extends AsyncTask<Object, String, Boolean> {
		ProgressDialog dialog = null;

		@Override
		protected void onPostExecute(Boolean result) { // ���� �Ϸ���
			dialog.dismiss(); // ���α׷��� ���̾�α� �ݱ�
			RegisterActivity.this
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); // ȭ��
																							// ����
																							// ����
		}

		/** ���α׷��� ���̾�α׸� ��� ���������� ����ڿ��� �����ش�. */
		@Override
		protected void onPreExecute() { // ������ ���α׷��� ���̾�α׷� ���������� ����ڿ��� �˸���.
			dialog = ProgressDialog.show(RegisterActivity.this, "������",
					"������ ���̵� ������Դϴ�.", true);
			dialog.show();
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}

		
		@Override
		protected Boolean doInBackground(Object... params) { // ������

			// TODO Auto-generated method stub
			boolean result = true;

			// http �� ���� �̸� �� �� �÷���
			Vector<NameValuePair> vars = new Vector<NameValuePair>();
																								// ��
			try {

				// HTTP GET �޼��带 �̿��Ͽ� ������ ���ε� ó��
				vars.add(new BasicNameValuePair("id", di.getDeviceNumber())); // �浵

				String url = SERVER + URLEncodedUtils.format(vars, null);
				HttpGet request = new HttpGet(url);
				try {

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					HttpClient client = new DefaultHttpClient();
					String responseBody = client.execute(request,
							responseHandler); // ����

					if (responseBody.equals("ok")) {
						// Toast.makeText(getBaseContext(), responseBody,
						// Toast.LENGTH_LONG).show();
						Log.i(DEBUG_TAG, responseBody);
						result = true;
					}
				} catch (ClientProtocolException e) {
					Log.e(DEBUG_TAG, "Failed to get playerId (protocol): ", e);
					result = false;
				} catch (IOException e) {
					Log.e(DEBUG_TAG, "Failed to get playerId (io): ", e);
					result = false;
				}

			} catch (Exception e) {
				result = false;
				dialog.dismiss(); // ���α׷��� ���̾�α� �ݱ�
				Log.e(DEBUG_TAG, "���� ���ε� ����", e);
			}

			return result;
		}

	}
}
