package Servlets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemFactory;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

public class SaveImage {
	
	public String saveImage(HttpServletRequest request, HttpServletResponse response, String outFile) throws IOException {
		System.out.println("in save image");
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		boolean isMultipartContent = ServletFileUpload.isMultipartContent(request);
		if (!isMultipartContent) {
			out.println("You are not trying to upload<br/>");
			return "";
		}
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List<FileItem> fields = upload.parseRequest(new ServletRequestContext(request));
			Iterator<FileItem> it = fields.iterator();
			if (!it.hasNext()) {
				out.println("No fields found");
				return "";
			}
			while (it.hasNext()) {
				System.out.println("next");
				out.println("<tr>");
				String path = request.getServletContext().getRealPath("/");
				System.out.println(path);
				FileItem fileItem = it.next();
				BufferedImage imBuff = ImageIO.read(fileItem.getInputStream());
				String outputPath = path + outFile;
				File outputfile = new File(outputPath); 
				ImageIO.write(imBuff, "png", outputfile);
				return outputPath;
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		return "";
	}

}
