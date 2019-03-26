package org.solr.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileUploadServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Log log = LogFactory.getLog(FileUploadServlet.class);

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		File dictFile = new File(request.getSession().getServletContext()
				.getRealPath("")
				+ File.separatorChar
				+ "WEB-INF"
				+ File.separatorChar
				+ "classes" + File.separatorChar + "dictionaries.dic");
		File destFile = new File(request.getSession().getServletContext()
				.getRealPath("")
				+ File.separatorChar
				+ "WEB-INF"
				+ File.separatorChar
				+ "classes"
				+ File.separatorChar
				+ "dictionaries"
				+ Calendar.getInstance().getTimeInMillis() + ".dic");
		FileUtils.moveFile(dictFile, destFile);
		request.setCharacterEncoding("utf-8");// 防止中文名乱码
		int sizeThreshold = 1024 * 6; // 缓存区大小

		long sizeMax = 1024 * 1024 * 2;// 设置文件的大小为2M
		final String allowExtNames = "dic";
		DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
		diskFileItemFactory.setRepository(dictFile.getParentFile());
		diskFileItemFactory.setSizeThreshold(sizeThreshold);
		ServletFileUpload servletFileUpload = new ServletFileUpload(
				diskFileItemFactory);
		servletFileUpload.setSizeMax(sizeMax);

		List<FileItem> fileItems = null;
		try {
			fileItems = servletFileUpload.parseRequest(request);

			for (FileItem fileItem : fileItems) {
				String filePath = fileItem.getName();
				if (filePath == null || filePath.trim().length() == 0)
					continue;
				// String
				// fileName=filePath.substring(filePath.lastIndexOf(File.separator)+1);
				String extName = filePath
						.substring(filePath.lastIndexOf(".") + 1);
				if (allowExtNames.indexOf(extName) != -1) {
					try {
						fileItem.write(dictFile);
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				} else {
					throw new FileUploadException("file type is not allowed");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}

		org.wltea.analyzer.dic.Dictionary dict = org.wltea.analyzer.dic.Dictionary
				.getSingleton();
		Set<String> oldWords = new HashSet<String>();
		String word = null;
		BufferedReader brOld = new BufferedReader(new InputStreamReader(
				new FileInputStream(destFile), "UTF-8"));
		try {
			while ((word = brOld.readLine()) != null) {
				oldWords.add(word.trim());
			}

		} finally {
			brOld.close();
		}

		Set<String> words = new HashSet<String>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(dictFile), "UTF-8"));
		try {
			while ((word = br.readLine()) != null) {
				words.add(word.trim());
			}
		} finally {
			br.close();
		}
		// 如果新的词库大于0，则先去除老词，再加入新词
		if (words.size() > 0) {
			dict.disableWords(oldWords);
			dict.addWords(words);
		}

	}
}
