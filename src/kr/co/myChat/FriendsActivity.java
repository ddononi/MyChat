package kr.co.myChat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.net.ssl.HttpsURLConnection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.ProgressDialog;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

/**
 *	친구 리스트를 보여준다.
 */
public class FriendsActivity extends BaseActivity  {
	private ProgressDialog progressDialog; 		// 진행상태 다이얼로그
	private ArrayList<Friend> mList = new ArrayList<Friend>();
	
	// 엘리먼트
	private ListView mFriendLV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend);

		// 데이터를 가져올동안 진행상태 다이얼로그를 표시
		progressDialog = new ProgressDialog(this);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog.setMessage("로딩중...");		
		progressDialog.show();
		// 엘리먼트 후킹
		mFriendLV = (ListView)findViewById(R.id.list);
		mFriendLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(FriendsActivity.this, "dd", Toast.LENGTH_SHORT).show();
			}
		});
		// 쓰레드로 친구 리스트를 가져온다.
		Thread XmlThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// 친구 xml String
				String friendXml = getFriendXmlList();
				try {
					mList = parseXML(friendXml);
					// 어댑터 설정
					FriendsArrayAdapater adapter = new FriendsArrayAdapater(mList);
					mFriendLV.setAdapter(adapter);
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					// 다이얼로그가 띄어져 있으면 다이얼 로그를 닫는다.
					if( progressDialog.isShowing() ){
						progressDialog.dismiss();	
					}
				}
			}
		});	
		XmlThread.start();
	}
	
	private String getFriendXmlList(){
		// 서버에서 친구 내역을 가져온다.
		URL url = null;
		HttpsURLConnection conn = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL(FRIEND_URL);
			conn = (HttpsURLConnection)url.openConnection();
			conn.setConnectTimeout(TIMEOUT);	// 연결 타임아웃 시간
			conn.setReadTimeout(TIMEOUT);		// 읽기 타임아웃 시간
			// 버퍼리더로 가져온다.
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while( true ){
				// 한줄씩 읽는다.
				line = br.readLine();
				if(line == null){
					break;
				}
				sb.append(line+"\n");
			}
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			// 연결을 닫는다.
			try {
				br.close();
				conn.disconnect();
			} catch (IOException e) {}
		}
		return sb.toString();
	}

	/**
	 * xml형식을 담고있는 문자열을 파싱하여 list에 넣어준다.
	 * @param xmlStr
	 * 	xml형태의 문자열
	 * @return
	 * 	Friend를 담은 리스트
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private ArrayList<Friend> parseXML(String xmlStr) throws XmlPullParserException, IOException {
	    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    XmlPullParser parser = factory.newPullParser();
	    factory.setNamespaceAware(true);

	    // Reader형식으로 읽어야 되기 때문에 StringReader로 변환후 데이터를 넣어준다.
	    parser.setInput(new StringReader ( xmlStr ));
		
		int eventType = -1;

		Friend friend = null;
		// 반환될 친구정보를 담을 리스트
		ArrayList<Friend> list = new ArrayList<Friend>();
		while(eventType != XmlResourceParser.END_DOCUMENT){	// 문서의 마지막이 아닐때까지
			if(eventType == XmlResourceParser.START_TAG){	// 이벤트가 시작태그면
				String strName = parser.getName();
				if(strName.equals("friend")){				// 친구태그 시작이면 
					friend = new Friend();
				}else if(strName.equals("id")){				// 아이디 저장			
					parser.next();	// text으로 이동
					friend.setId(parser.getText());	  
				}else if(strName.equals("time")){			// 친구맺은 시간	
					parser.next();	// text으로 이동
					friend.setTime(parser.getText());	  
					list.add(friend);
				}
			}
			eventType = parser.next();	// 다음이벤트로..
		}

		return list;
	}

}
