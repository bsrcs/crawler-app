package crawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;

public class MainApp {

	static String PAGE_URL = "https://ilan.gov.tr/kategori-arama?currentPage=<PAGE_NUMBER>&npdab=on&type=21628";
	static String LINK_PREFIX = "https://ilan.gov.tr/";
	static String PAGE_SELECTOR = "#left-filters > div:nth-child(2) > div.column.col-18 > section.top-ad-list > ul:nth-child(4)";
	static String DETAIL_SELECTOR = "#kunye > div > div > div.column.col-16 > div > div";

	public static void main(String[] args) throws IOException, InterruptedException {

		ArrayList<String> detailPageLinks = new ArrayList<String>();

		for (int i = 1; i <= 20; i++) {
			String pageNumber = Integer.toString(i);
			// create urls for every webpage by replacing the dummy 'PAGE_NUMBER' with page
			// number
			String url = PAGE_URL.replace("<PAGE_NUMBER>", pageNumber);
			// add all the links from a single webpage
			ArrayList<String> list = extractLinksFromWebPage(url);
			detailPageLinks.addAll(list);
		}
		//call the extract detail method for all the detail page links in the list 'detailPageLinks'
		String data = "";
		for (String detailPageLink : detailPageLinks) {
			String temp = getDataFromDetailPage(detailPageLink);
			temp = temp + "\n";
			System.out.println(data);
			//keep on adding output of all detail page
			data = data + temp;
		}
		//write string to the file data.txt using apache commons io framework
		FileUtils.writeStringToFile(new File("data.txt"), data);
		System.out.println("data extracted successfully!");
	}

	// deals with a page
	private static ArrayList<String> extractLinksFromWebPage(String url) throws IOException {

		ArrayList<String> linksFoundForPage = new ArrayList<String>();

		Document webPage = WebPage.get(url);

		/*
		 * Although the select method always return a list of all matches found by the
		 * given selector in our case we have only one part of the web page where our
		 * links are present so we get list of only one element inside it
		 */
		Elements matchedElements = webPage.select(PAGE_SELECTOR);
		Element linkSectionOfTheWebPage = matchedElements.get(0);
		linksFoundForPage.addAll(extractLinks(linkSectionOfTheWebPage));
		return linksFoundForPage;
	}

	// deals with the part inside a page
	private static ArrayList<String> extractLinks(Element currentElement) {

		ArrayList<String> linksFoundForElement = new ArrayList<String>();
		Elements linkOfElement = currentElement.select("a[href]");

		for (Element link : linkOfElement) {
			String httpLink = LINK_PREFIX + link.attr("href");
			linksFoundForElement.add(httpLink);
		}
		return linksFoundForElement;
	}

	private static String getDataFromDetailPage(String url) throws IOException, InterruptedException {
		String detail = "";
		Document detailPage = WebPage.get(url);
		Elements matchedElements = detailPage.select(DETAIL_SELECTOR);
		for (Element e : matchedElements) {
			Elements tableElements = e.getAllElements();
			for (Element tableElement : tableElements) {
				detail = detail + tableElement.ownText();
			}
		}
		return detail;
	}

}