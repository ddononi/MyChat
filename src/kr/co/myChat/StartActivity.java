package kr.co.myChat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;

/**
 *	첫 시작 엑티비티로 아이디가 등록 되었으면 친구선택 화면으로 넘기고 
 *	그렇지 않으면 이름을 등록하는 페이지로 넘긴다.
 */
public class StartActivity extends BaseActivity {
	private SharedPreferences sp;	// 공유설정 환경
	private String id;				// 아이디

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		// 공유환경 설정에서 이름을 가져온다.
		sp = getSharedPreferences(SHARED, MODE_PRIVATE);
		this.id = sp.getString("id", "");	
		if(TextUtils.isEmpty(id)){	// 이름이 등록이 안됐으면 이름 등록페이지로 이동한다.
			Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
			startActivity(intent);
			finish();
		}else{
			
		}
		
		
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {	
		// TODO Auto-generated method stub
		/*
		 * 화면 터치시 다음 화면으로 이동한다.
		 */
		  if ( event.getAction() == MotionEvent.ACTION_DOWN ){
			  
			 Intent intent = null;  
			 intent =  new Intent(StartActivity.this, MainActivity.class);
				 
			 startActivity(intent);	// 다음 인텐트로 이동
			 finish();
			 return true;
		  }
		  
		  return super.onTouchEvent(event);
		  
	}	
}
