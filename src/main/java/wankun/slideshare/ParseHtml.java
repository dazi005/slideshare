package wankun.slideshare;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ParseHtml {

	public static HttpClient getHttpClient() {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		String proxyHost = "127.0.0.1";
		int proxyPort = 8087;
		String userName = "";
		String password = "";
		httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(userName, password));
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, proxy);
		return httpClient;
	}

	private String htmldata = "";

	public ParseHtml() {
		try {
			// Get HTML
			HttpClient client = getHttpClient();
			HttpGet get = new HttpGet(SlideShare.SD_URL);
			HttpResponse res = client.execute(get);

			if (res.getEntity() != null) {
				htmldata = EntityUtils.toString(res.getEntity());
			}

			EntityUtils.consume(res.getEntity());
			client.getConnectionManager().shutdown();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析HTML返回结果，分析出Img URL地址
	 * 
	 * @return
	 */
	public String parseTitle() {
		if (htmldata != null && !"".equals(htmldata)) {
			Document doc = Jsoup.parse(htmldata);
			Elements titleElements = doc.select("div.title>h1.h-slideshow-title");
			return titleElements.text();
		} else
			return "notitle";
	}
	
	/**
	 * 解析HTML返回结果，分析出Img URL地址
	 * 
	 * @return
	 */
	public Map<String, String> parseImgUrl() {
		Map<String, String> imgMap = new LinkedHashMap<String, String>();

		if (htmldata != null && !"".equals(htmldata)) {
			Document doc = Jsoup.parse(htmldata);
			Elements linksElements = doc.select("div.slide_container>div.slide>img");
			// 以上代码的意思是 找id为“page”的div里面 id为“content”的div里面 id为“main”的div里面
			// class为“left”的div里面 id为“recommend”的div里面ul里面li里面a标签

			for (Element ele : linksElements) {
				// imglist.add(ele.attr("data-normal"));
				String imgurl = ele.attr("data-full");
				String[] uriArray = imgurl.split("/");
				String filename = uriArray[uriArray.length - 1].substring(0, uriArray[uriArray.length - 1].indexOf("?"));
				imgMap.put(imgurl, filename);

			}
		}

		return imgMap;
	}
}