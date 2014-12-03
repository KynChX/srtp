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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	TextView textView1;
	RadioButton rad_stu;
	RadioButton rad_tea;
	EditText et_no;
	EditText et_pwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView1 = (TextView) findViewById(R.id.textView1);
		rad_stu = (RadioButton) findViewById(R.id.rad_stu);
		rad_tea = (RadioButton) findViewById(R.id.rad_tea);
		et_no = (EditText) findViewById(R.id.et_no);
		et_pwd = (EditText) findViewById(R.id.et_pwd);
		
	}

	public void login(View view) {

		AsyncHttpClient client = new AsyncHttpClient();
		String url = "http://202.202.1.176:8080/_data/index_login.aspx";

		RequestParams params = new RequestParams();
		params.put("UserID", "20126388");
		params.put("PassWord", "235710");
		params.put("Sel_Type", "STU");

		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {

				// 课表页面
				String selUrl = "http://202.202.1.176:8080/znpk/Pri_StuSel_rpt.aspx";

				// 从返回header中获取cookie值
				String string = new String();
				for (Header h : headers) {
					if (h.getName().startsWith("Set")) {
						string = h.getValue();
					}
				}

				String[] s = string.split(";");

				Log.v("header", s[0]);

				AsyncHttpClient client1 = new AsyncHttpClient();

				// 在新的请求的header中添加cookie
				client1.addHeader("Cookie", s[0]);

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

							textView1.setText(filterHtml(responseHtml));
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
					}
				});

			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				Toast.makeText(MainActivity.this,
						"出错喽，检查下账号密码吧~ " + statusCode, 0).show();

			}
		});
	}

	//在这里解析html
	private String filterHtml(String source) {
		if (null == source) {
			return "";
		}
		StringBuffer sff = new StringBuffer();

		String html = source;
		Document doc = Jsoup.parse(html); // 把HTML代码加载到doc中
		
		Elements links_class = doc.select("td"); 
		for (Element link : links_class) {
			sff.append(link.text());
		}
		return sff.toString();
	}

}
