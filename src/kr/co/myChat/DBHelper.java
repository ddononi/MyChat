package kr.co.myChat;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// SQLiteOpenHelper ������ Ŭ����
public class DBHelper extends SQLiteOpenHelper {
	public static final String TABLE_NAME = "chat";
	public DBHelper(Context context){
		// ���� �� ���� ����
		super(context, "chat.db", null, BaseActivity.DB_VER);
	}

	/** ��� ������ ���̺��� ������ش�. */
	@Override
	public void onCreate(SQLiteDatabase db) {	// db�� �����ɶ� ���̺� ����
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