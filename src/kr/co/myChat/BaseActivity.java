package kr.co.myChat;

import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


/**
 *	기본 설정 베이스 클래스
 */
public class BaseActivity extends Activity {
	public static final String SERVER = "";		// 서버url
	public static final String DEBUG_TAG = "mychat";	// 디버그 태그
	public static final String APP_NAME = "";	// 앱 이름
	public final static int DB_VER = 1;			// 디비 버젼
	public static final String SHARED = "mychat";
	private boolean isTwoClickBack = false;		// 두번 클릭여부
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		/*
		 * back 버튼이면 타이머(2초)를 이용하여 다시한번 뒤로 가기를 
		 * 누르면 어플리케이션이 종료 되도록한다.
		 */
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				if (!isTwoClickBack) {	// 연속 두번 클릭이 아니면
					Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르면 종료됩니다.",
							Toast.LENGTH_SHORT).show();
					CntTimer timer = new CntTimer(2000, 1); // 두번 클릭 타이머
					timer.start();
				} else {
					moveTaskToBack(true);
	                finish();
					return true;
				}

			}
		}
		return false;
	}

	// 뒤로가기 종료를 위한 타이머
	class CntTimer extends CountDownTimer {
		public CntTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			isTwoClickBack = true;
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			isTwoClickBack = false;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// TODO Auto-generated method stub
			Log.i("Test", " isTwoClickBack " + isTwoClickBack);
		}

	} 
	
}
