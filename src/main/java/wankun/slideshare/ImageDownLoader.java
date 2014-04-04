package wankun.slideshare;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class ImageDownLoader implements Callable<String> {

	private String URLName;
	private String target;

	public ImageDownLoader(String URLName, String target) {
		this.URLName = URLName;
		this.target = target;
	}

	/**
	 * 抓取网页上图片
	 * 
	 * @param URLName
	 * @param target
	 * @throws Exception
	 */
	public void getUrlImg() throws Exception {
		URL url = new URL(URLName);
		URLConnection urlconn = url.openConnection();
		HttpURLConnection httpconn = (HttpURLConnection) urlconn;
		// 设置下载超时20s
		httpconn.setConnectTimeout(20000);
		int HttpResult = httpconn.getResponseCode();
		if (HttpResult != HttpURLConnection.HTTP_OK) {
			System.out.print("fail");
			return;
		}
		BufferedInputStream bis = new BufferedInputStream(urlconn.getInputStream());
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(target));
		byte[] buffer = new byte[1024];
		int num = -1;
		while (true) {
			num = bis.read(buffer);
			if (num == -1) {
				bos.flush();
				break;
			}
			bos.flush();
			bos.write(buffer, 0, num);
		}
		bos.close();
		bis.close();
	}

	public String call() {
		try {
			if (!new File(target).exists()) {
				getUrlImg();
			}
			return URLName;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
