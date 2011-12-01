package kr.co.myChat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// SQLiteOpenHelper 재정의 클래스
public class DBHelper extends SQLiteOpenHelper {
	public static final String TABLE_NAME = "chat";
	public DBHelper(Context context){
		// 디비명 및 버젼 설정
		super(context, "chat.db", null, BaseActivity.DB_VER);
	}

	/** 디비가 생성시 테이블을 만들어준다. */
	@Override
	public void onCreate(SQLiteDatabase db) {	// db가 생성될때 테이블도 생성
		// TODO Auto-generated method stub
		String sql = "CREATE TABLE "+ TABLE_NAME + " (no INTEGER PRIMARY KEY," +
				     " id TEXT NOT NULL, chat TEXT , time TEXT, file TEXT );";
		db.execSQL(sql);
	}
	

	@Override
	public void onOpen(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		super.onOpen(db);
	}	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}