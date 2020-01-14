package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebPage {

	public static Document get(String url) {
		Document doc = null;
		try {
			doc = Jsoup.connect(url).ignoreHttpErrors(true).execute().parse();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(url);
		} 
		return doc;
	}

}