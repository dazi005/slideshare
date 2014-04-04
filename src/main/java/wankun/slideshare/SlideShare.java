package wankun.slideshare;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import org.apache.http.client.ClientProtocolException;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.usermodel.SlideShow;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class SlideShare {
	// http://www.slideshare.net/hortonworks/integration-of-hive-and-hbase-12805463#
	// private static final String SD_URL =
	// "http://www.slideshare.net/ProphetsAgency/trends-in-interactive-design-2013";

	// private static final String SD_URL = "http://www.baidu.com/";

	public static final String SD_URL = "http://www.slideshare.net/hortonworks/integration-of-hive-and-hbase-12805463";
	public static final String OUT_PUT_FOLDER = "E:\\tmpdir\\";

	ExecutorService pool = Executors.newFixedThreadPool(10);

	public static void main(String[] args) throws Exception {
		new SlideShare().service();
	}

	public void service() throws ClientProtocolException, IOException, InterruptedException, ExecutionException, DocumentException {
		ParseHtml parse = new ParseHtml();
		Map<String, String> imgmap = parse.parseImgUrl();
		String title = parse.parseTitle();

		Map<String, String> imgmap_down = new ConcurrentHashMap<String, String>();
		imgmap_down.putAll(imgmap);
		while (imgmap_down.size() > 0) {
			Set<String> urls = imgmap.keySet();
			List<Future> fulist = new ArrayList<Future>();
			for (String url : urls) {
				System.out.println("url:" + url + " \t filename:" + imgmap.get(url));
				Future fu = pool.submit(new ImageDownLoader(url, SlideShare.OUT_PUT_FOLDER + imgmap_down.get(url)));
				fulist.add(fu);
			}

			for (Future fu : fulist) {
				String url = (String) fu.get();
				imgmap_down.remove(url);
			}
		}

		pool.shutdown();
//		createPDF(imgmap);
		createPPT(imgmap,title);

	}

	public void createPPT(Map<String, String> imgmap,String title) throws MalformedURLException, IOException, DocumentException {
		String OUTPUT = OUT_PUT_FOLDER+title+".ppt";
		// 构建PPT
		SlideShow _slideShow = new SlideShow();
		
		if(imgmap.size()<=0)
			return ;
		
		BufferedImage image0 = ImageIO.read(new File(OUT_PUT_FOLDER + imgmap.values().iterator().next()));
		_slideShow.setPageSize(new Dimension(image0.getWidth(), image0.getHeight()));
		
		Collection<String> c = imgmap.values();
		Iterator<String> it = c.iterator();
		while (it.hasNext()) {
			// 创建幻灯片
			Slide _slide = _slideShow.createSlide();
			
			String filename = it.next();
			File file = new File(OUT_PUT_FOLDER + filename);
			BufferedImage image = ImageIO.read(file);
			// 设置图片类型
			int pic_type = -1;
			if (filename.indexOf(".png") != -1) {
				pic_type = Picture.PNG;
			} else {
				pic_type = Picture.JPEG;
			}
			
			// 新置入图片索引位置
			int newIndex = _slideShow.addPicture(file, pic_type);
			// 根据索引位置 , 创建图片对象
			Picture pic = new Picture(newIndex);
			// 设置图片显示位置
			pic.setAnchor(new java.awt.Rectangle(0,0, image.getWidth(), image.getHeight()));

			// 将图片放入幻灯片
			_slide.addShape(pic);
			
		}
		// 输出PPT文件
		_slideShow.write(new FileOutputStream(new File(OUTPUT)));

	}
	
//	public void createPDF(Map<String, String> imgmap) throws MalformedURLException, IOException, DocumentException {
//		// Create PDF
//		Rectangle rect = new Rectangle(PageSize.A4.rotate());
//		Document document = new Document(rect);
//		PdfWriter.getInstance(document, new FileOutputStream(OUT_PUT_FOLDER + PDF_NAME));
//		document.open();
//
//		Collection<String> c = imgmap.values();
//		Iterator<String> it = c.iterator();
//		while (it.hasNext()) {
//			String filename = it.next();
//			document.newPage();
//			Image img = Image.getInstance(OUT_PUT_FOLDER + filename);
//			img.setAlignment(Image.MIDDLE);
//			
//			img.scaleAbsolute(842,595);
//			System.out.println(filename);
//			document.add(img);
//		}
//
//		document.close();
//
//	}
	
        
   

}