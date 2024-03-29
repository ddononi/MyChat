package kr.co.myChat;

import java.io.IOException;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
 * 사용자 아이디 등록 액티비티
 */
public class RegisterActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		// 공유환경 설정에서 이름을 가져온다.

		// 엘리먼트 후킹
		final EditText et = (EditText) findViewById(R.id.id);
		Button btn = (Button) findViewById(R.id.submit);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 아이디를 입력햇는지 확인한다.
				CharSequence id = et.getText();
				if (TextUtils.isEmpty(id)) {
					// 아이디가 없으면 토스트를 굽는다.
					Toast.makeText(RegisterActivity.this, "아이디를 입력하세요",
							Toast.LENGTH_SHORT);
					return;
				}
				AsyncTaskeRegister asyncTask = new AsyncTaskeRegister();
				asyncTask.execute(id);

			}
		});

	}

	/**
	 *	서버에 쓰레드로 입력한 아이디를 전송한다.
	 */
	private class AsyncTaskeRegister extends AsyncTask<Object, String, Boolean> {
		private ProgressDialog dialog = null;
		private String id;	// 저장할 아이디

		@Override
		protected void onPostExecute(Boolean result) { // 전송 완료후
			dialog.dismiss(); // 프로그레스 다이얼로그 닫기
			// 화면 회전 락을 풀어준다.
			if(result){	//서버 전송 결과에 따라 메세지를 보여준다.
				Toast.makeText(RegisterActivity.this, "등록되었습니다.", Toast.LENGTH_SHORT).show();

				// 아이디를 공유환경 설정에 저장한후 서버에 등록한다.
				SharedPreferences sp = getSharedPreferences(SHARED,
						MODE_PRIVATE);
				SharedPreferences.Editor editor = sp.edit();
				editor.putString("id", this.id); // 아이디 저장
				editor.commit(); // 저장할려면 커밋은 필수				
			}else{
				Toast.makeText(RegisterActivity.this, "등록실패.", Toast.LENGTH_SHORT).show();
			}
			unLockScreenRotation();
		}

		/** 프로그래스 다이얼로그를 띄워 전송중임을 사용자에게 보여준다. */
		@Override
		protected void onPreExecute() { // 전송전 프로그래스 다이얼로그로 전송중임을 사용자에게 알린다.
			dialog = ProgressDialog.show(RegisterActivity.this, "전송중",
					"서버에 아이디를 등록중입니다.", true);
			dialog.show();
			mLockScreenRotation(); // 화면회전을 막는다.
		}

		@Override
		protected void onProgressUpdate(String... values) {
		}

		
		@Override
		protected Boolean doInBackground(Object... params) { // 전송중

			// TODO Auto-generated method stub
			boolean result = false;	// 전송 결과 처리

			// http 로 보낼 이름 값 쌍 컬랙션
			Vector<NameValuePair> vars = new Vector<NameValuePair>();
			this.id = (String)params[0];
			try {

				// HTTP POST 메서드를 이용하여 데이터 업로드 처리
				vars.add(new BasicNameValuePair("id", this.id)  ); // 경도

				String url = REGISTER_URL + URLEncodedUtils.format(vars, null);
				HttpPost request = new HttpPost(url);
				try {

					ResponseHandler<String> responseHandler = new BasicResponseHandler();
					HttpClient client = new DefaultHttpClient();
					// 서버전송후 음답내역
					String responseBody = client.execute(request, responseHandler); // 전송
					// ok면 정상처리
					if (responseBody.equals("ok")) {
						Log.i(DEBUG_TAG, responseBody);
						result = true;
					}
				} catch (ClientProtocolException e) {
					Log.e(DEBUG_TAG, "Failed to register id (protocol): ", e);
				} catch (IOException e) {
					Log.e(DEBUG_TAG, "Failed to register  i (io): ", e);
				}

			} catch (Exception e) {
				dialog.dismiss(); // 프로그레스 다이얼로그 닫기
				Log.e(DEBUG_TAG, "파일 업로드 에러", e);
			}

			return result;
		}

	}
}
