package com.example.demo;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	// �ؼ�����
	TextView textView1;
	RadioButton rad_stu;
	RadioButton rad_tea;
	EditText et_no;
	EditText et_pwd;
	RadioGroup radioGroup;
	Button bt_login;
	ProgressBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		rad_stu = (RadioButton) findViewById(R.id.rad_stu);
		rad_tea = (RadioButton) findViewById(R.id.rad_tea);
		et_no = (EditText) findViewById(R.id.et_no);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		bar = (ProgressBar) findViewById(R.id.progressBar1);
		bt_login = (Button) findViewById(R.id.Button1);

	}

	// �����½�Ĳ���
	public void login(View view) {

		// ������������ظ��ύ #########���������� ==!
		bt_login.setClickable(false);

		// ����ǿ���֤
		if (et_no.getText().toString().trim().isEmpty()
				|| et_pwd.getText().toString().trim().isEmpty()) {
			Toast.makeText(MainActivity.this, "ѧ�Ż����붼����Ϊ��Ŷ~", 0).show();
			bt_login.setClickable(true);
			return;
		}
		
		//�������ʾ�����Ľ�����
		bar.setIndeterminate(false);
		bar.setVisibility(View.VISIBLE);

		// ��ʼ���첽����
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "http://202.202.1.176:8080/_data/index_login.aspx";
		String logType = null;
		RequestParams params = new RequestParams();
		
		//Ҫpost�Ĳ�����������
		params.put("UserID", et_no.getText().toString().trim());
		params.put("PassWord", et_pwd.getText().toString().trim());
		
		//��½���͵�ѡ��
		if (rad_stu.isChecked()) {
			logType = "STU";//ѧ����½
		} else if (rad_tea.isChecked()) {
			logType = "TEA";//��ʦ��½
		}
		
		params.put("Sel_Type", logType);

		//��������
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				//����Ҫ���ݷ��ص����ݣ������ó��Ƿ��½�ɹ�~~~
				//############���񳬼�ɵ��##############
				try {
					if (!isLoginSuccess(new String(responseBody, "gb2312"))) {
						Toast.makeText(MainActivity.this,
								"����ඣ�������˺������~ " + statusCode, 0).show();
						
						//��������ˣ��ðѰ�ť���óɿ��Ե����״̬���ѽ�������Ϊ���ɼ�
						bt_login.setClickable(true);
						bar.setVisibility(View.INVISIBLE);
						return;
					}
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// �α�ҳ��url
				String selUrl = "http://202.202.1.176:8080/znpk/Pri_StuSel_rpt.aspx";

				// �ӷ���header�л�ȡcookieֵ
				String string = new String();
				for (Header h : headers) {
					if (h.getName().startsWith("Set")) {
						string = h.getValue();
					}
				}

				String[] s = string.split(";");

				Log.v("header", s[0]);

				AsyncHttpClient client1 = new AsyncHttpClient();

				// ���µ������header�����cookie
				client1.addHeader("Cookie", s[0]);

				//��Щ��������Ϊѡ�����ѡ��İ�~~~~~~~~~~~
				//###########����ɵ��Ҳ��############
				RequestParams params1 = new RequestParams();
				params1.put("px", "1");
				params1.put("rad", "on");
				params1.put("Sel_XNXQ", "20140");
				params1.put("sel_xn", "2014");
				params1.put("sel_xq", "0");
				params1.put("zfx_flag", "0");
				params1.put("zfx", "0");

				client1.post(selUrl, params1, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {

						
						try {
							String responseHtml = new String(responseBody,
									"gb2312");

							// ��תҳ��
							Intent intent = new Intent(MainActivity.this,
									newActivity.class);

							intent.putExtra("shedule", filterHtml(responseHtml));
							startActivity(intent);
							
							bt_login.setClickable(false);
							bar.setVisibility(View.INVISIBLE);
						} catch (UnsupportedEncodingException e) {

							e.printStackTrace();
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						Toast.makeText(MainActivity.this,
								"error " + statusCode, 0).show();
						
						bt_login.setClickable(false);
						bar.setVisibility(View.INVISIBLE);
					}
				});

			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this,
						"����ඣ�������˺������~ " + statusCode, 0).show();

			}
		});

	}

	// ���������html
	private String filterHtml(String source) {
		if (null == source) {
			return "";
		}
		StringBuffer sff = new StringBuffer();

		String html = source;
		Document doc = Jsoup.parse(html); // ��HTML������ص�doc��

		Elements links_class = doc.select("td");
		for (Element link : links_class) {
			sff.append(link.text());
		}
		return sff.toString();
	}

	private Boolean isLoginSuccess(String source) {
		if (null == source) {
			return false;
		}
		StringBuffer sff = new StringBuffer();

		String html = source;
		Document doc = Jsoup.parse(html); // ��HTML������ص�doc��

		Elements links_class = doc.select("#divLogNote");
		if (links_class.text().startsWith("���ڼ���Ȩ������")) {
			return true;
		}

		return false;
	}

}
