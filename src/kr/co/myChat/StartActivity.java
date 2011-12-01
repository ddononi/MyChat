package kr.co.myChat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;

/**
 *	ù ���� ��Ƽ��Ƽ�� ���̵� ��� �Ǿ����� ģ������ ȭ������ �ѱ�� 
 *	�׷��� ������ �̸��� ����ϴ� �������� �ѱ��.
 */
public class StartActivity extends BaseActivity {
	private SharedPreferences sp;	// �������� ȯ��
	private String id;				// ���̵�

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		// ����ȯ�� �������� �̸��� �����´�.
		sp = getSharedPreferences(SHARED, MODE_PRIVATE);
		this.id = sp.getString("id", "");	
		if(TextUtils.isEmpty(id)){	// �̸��� ����� �ȵ����� �̸� ����������� �̵��Ѵ�.
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
		 * ȭ�� ��ġ�� ���� ȭ������ �̵��Ѵ�.
		 */
		  if ( event.getAction() == MotionEvent.ACTION_DOWN ){
			  
			 Intent intent = null;  
			 intent =  new Intent(StartActivity.this, MainActivity.class);
				 
			 startActivity(intent);	// ���� ����Ʈ�� �̵�
			 finish();
			 return true;
		  }
		  
		  return super.onTouchEvent(event);
		  
	}	
}
