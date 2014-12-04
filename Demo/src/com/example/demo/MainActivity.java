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

	// 控件声明
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

	// 点击登陆的操作
	public void login(View view) {

		// 避免误操作，重复提交 #########这里有问题 ==!
		bt_login.setClickable(false);

		// 输入非空验证
		if (et_no.getText().toString().trim().isEmpty()
				|| et_pwd.getText().toString().trim().isEmpty()) {
			Toast.makeText(MainActivity.this, "学号或密码都不能为空哦~", 0).show();
			bt_login.setClickable(true);
			return;
		}
		
		//点击后显示滚动的进度条
		bar.setIndeterminate(false);
		bar.setVisibility(View.VISIBLE);

		// 初始化异步请求
		AsyncHttpClient client = new AsyncHttpClient();
		String url = "http://202.202.1.176:8080/_data/index_login.aspx";
		String logType = null;
		RequestParams params = new RequestParams();
		
		//要post的参数放在这里
		params.put("UserID", et_no.getText().toString().trim());
		params.put("PassWord", et_pwd.getText().toString().trim());
		
		//登陆类型的选择
		if (rad_stu.isChecked()) {
			logType = "STU";//学生登陆
		} else if (rad_tea.isChecked()) {
			logType = "TEA";//老师登陆
		}
		
		params.put("Sel_Type", logType);

		//发送请求
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				//这里要根据返回的内容，解析得出是否登陆成功~~~
				//############好像超级傻逼##############
				try {
					if (!isLoginSuccess(new String(responseBody, "gb2312"))) {
						Toast.makeText(MainActivity.this,
								"出错喽，检查下账号密码吧~ " + statusCode, 0).show();
						
						//如果出错了，得把按钮设置成可以点击的状态，把进度条设为不可见
						bt_login.setClickable(true);
						bar.setVisibility(View.INVISIBLE);
						return;
					}
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				// 课表页面url
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

				//这些参数得作为选项可以选择的啊~~~~~~~~~~~
				//###########超级傻逼也是############
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

							// 跳转页面
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
						"出错喽，检查下账号密码吧~ " + statusCode, 0).show();

			}
		});

	}

	// 在这里解析html
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

	private Boolean isLoginSuccess(String source) {
		if (null == source) {
			return false;
		}
		StringBuffer sff = new StringBuffer();

		String html = source;
		Document doc = Jsoup.parse(html); // 把HTML代码加载到doc中

		Elements links_class = doc.select("#divLogNote");
		if (links_class.text().startsWith("正在加载权限数据")) {
			return true;
		}

		return false;
	}

}
