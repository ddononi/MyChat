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
 *	ģ�� ����Ʈ�� �����ش�.
 */
public class FriendsActivity extends BaseActivity  {
	private ProgressDialog progressDialog; 		// ������� ���̾�α�
	private ArrayList<Friend> mList = new ArrayList<Friend>();
	
	// ������Ʈ
	private ListView mFriendLV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend);

		// �����͸� �����õ��� ������� ���̾�α׸� ǥ��
		progressDialog = new ProgressDialog(this);
		progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		progressDialog.setMessage("�ε���...");		
		progressDialog.show();
		// ������Ʈ ��ŷ
		mFriendLV = (ListView)findViewById(R.id.list);
		mFriendLV.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(FriendsActivity.this, "dd", Toast.LENGTH_SHORT).show();
			}
		});
		// ������� ģ�� ����Ʈ�� �����´�.
		Thread XmlThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				// ģ�� xml String
				String friendXml = getFriendXmlList();
				try {
					mList = parseXML(friendXml);
					// ����� ����
					FriendsArrayAdapater adapter = new FriendsArrayAdapater(mList);
					mFriendLV.setAdapter(adapter);
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					// ���̾�αװ� ����� ������ ���̾� �α׸� �ݴ´�.
					if( progressDialog.isShowing() ){
						progressDialog.dismiss();	
					}
				}
			}
		});	
		XmlThread.start();
	}
	
	private String getFriendXmlList(){
		// �������� ģ�� ������ �����´�.
		URL url = null;
		HttpsURLConnection conn = null;
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			url = new URL(FRIEND_URL);
			conn = (HttpsURLConnection)url.openConnection();
			conn.setConnectTimeout(TIMEOUT);	// ���� Ÿ�Ӿƿ� �ð�
			conn.setReadTimeout(TIMEOUT);		// �б� Ÿ�Ӿƿ� �ð�
			// ���۸����� �����´�.
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while( true ){
				// ���پ� �д´�.
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
			// ������ �ݴ´�.
			try {
				br.close();
				conn.disconnect();
			} catch (IOException e) {}
		}
		return sb.toString();
	}

	/**
	 * xml������ ����ִ� ���ڿ��� �Ľ��Ͽ� list�� �־��ش�.
	 * @param xmlStr
	 * 	xml������ ���ڿ�
	 * @return
	 * 	Friend�� ���� ����Ʈ
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private ArrayList<Friend> parseXML(String xmlStr) throws XmlPullParserException, IOException {
	    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
	    XmlPullParser parser = factory.newPullParser();
	    factory.setNamespaceAware(true);

	    // Reader�������� �о�� �Ǳ� ������ StringReader�� ��ȯ�� �����͸� �־��ش�.
	    parser.setInput(new StringReader ( xmlStr ));
		
		int eventType = -1;

		Friend friend = null;
		// ��ȯ�� ģ�������� ���� ����Ʈ
		ArrayList<Friend> list = new ArrayList<Friend>();
		while(eventType != XmlResourceParser.END_DOCUMENT){	// ������ �������� �ƴҶ�����
			if(eventType == XmlResourceParser.START_TAG){	// �̺�Ʈ�� �����±׸�
				String strName = parser.getName();
				if(strName.equals("friend")){				// ģ���±� �����̸� 
					friend = new Friend();
				}else if(strName.equals("id")){				// ���̵� ����			
					parser.next();	// text���� �̵�
					friend.setId(parser.getText());	  
				}else if(strName.equals("time")){			// ģ������ �ð�	
					parser.next();	// text���� �̵�
					friend.setTime(parser.getText());	  
					list.add(friend);
				}
			}
			eventType = parser.next();	// �����̺�Ʈ��..
		}

		return list;
	}

}
