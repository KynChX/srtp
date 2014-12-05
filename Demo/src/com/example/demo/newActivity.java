package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class newActivity extends Activity {

	private ListView listView;
	TextView textView;
	String shedule;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ��intend��ȡ������
		Bundle bundle = getIntent().getExtras();
		shedule = bundle.getString("shedule");
		// Toast.makeText(this, filterHtml(shedule), 0).show();

		listView = new ListView(this);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, getData()));
		setContentView(listView);

	}

	private List<String> getData() {

		List<String> data = new ArrayList<String>();
		Elements elements = filterHtml(shedule);
		
		for (Element element : elements) {
			data.add(element.text());
		}
//		data.add("��������1");
//		data.add("��������2");
//		data.add("��������3");
//		data.add("��������4");

		return data;
	}

	// ���������html
	private Elements filterHtml(String source) {
		if (null == source) {
			return null;
		}
		StringBuffer sff = new StringBuffer();

		String html = source;
		Document doc = Jsoup.parse(html); // ��HTML������ص�doc��

		Elements links_class = doc.select("td[style*=21%]");
		// for (Element link : links_class) {
		// sff.append(link.text());
		// }
		// return sff.toString();
		return links_class;

	}

}
