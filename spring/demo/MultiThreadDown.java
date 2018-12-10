package com.example.blue.util;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @Description: 多线程-断点下载
 * @author BlueCard
 * @date 2018年8月1日 下午7:22:37
 */
public class MultiThreadDown {
	private static final Logger log = LoggerFactory.getLogger(MultiThreadDown.class);

	private static int finishedThreadCount = 0;// 已下载完成的线程个数

	private String serverPath;
	private String localPath;
	private int threadCount = 4;

	public MultiThreadDown(String serverPath, String localPath, int threadCount) {
		this.serverPath = serverPath;
		this.localPath = localPath;
		this.threadCount = threadCount;
	}

	public void download() {
		HttpURLConnection conn;
		RandomAccessFile raf = null;
		try {
			conn = (HttpURLConnection) new URL(serverPath).openConnection();
			conn.setReadTimeout(5 * 1000);
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() == 200) {
				// 1.获得服务器上源文件的大小
				int fileSize = conn.getContentLength();
				log.info("download---fileSize: {}", fileSize);

				// 2.使用 RandomAccessFile 创建一个和源文件大小相同的临时文件
				File file = new File(localPath);
				if (!file.exists()) {
					file.mkdirs();
				}
				// rwd -> 立刻写入,不经过磁盘缓存
				raf = new RandomAccessFile(new File(file, getFileNameByPath(serverPath)), "rwd");
				raf.setLength(fileSize);

				// 3.计算每个线程下载的开始位置和结束为止
				int blockSize = fileSize / threadCount;

				for (int threadId = 0; threadId < threadCount; threadId++) {
					int startIndex = threadId * blockSize;
					int endIndex = (threadId + 1) * blockSize - 1;
					
					if (threadId == threadCount - 1) {// 最后一个线程的结束位置为文件总长度
						endIndex = fileSize - 1;
					}

					// 4.开启线程,按照计算出来的开始结束位置开始下载数据
					log.info("download---thread{} 计划 下载范围: {}->{}", threadId, startIndex, endIndex);
					new Thread(new DownloadThread(threadId, startIndex, endIndex)).start();
				}
			}
		} catch (Exception e) {
			log.error("Exception---MultiThreadDown: ", e);
		} finally {
			try {
				if (null != raf) {
					raf.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class DownloadThread implements Runnable {

		private int threadId;
		private int startIndex;
		private int endIndex;

		public DownloadThread(int threadId, int startIndex, int endIndex) {
			this.threadId = threadId;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			FileInputStream fis = null;
			RandomAccessFile raf = null, praf = null;
			try {
				// 1.如果已存在 [进度文件],则读取上次的下载进度,置为新的下载起始点
				File progressFile = new File(localPath, "data" + threadId);
				praf = new RandomAccessFile(progressFile, "rwd");
				
				if (progressFile.exists()) {
					String line = praf.readLine();
					if (!TextUtils.isEmpty(line)) {
						int lastIndex = Integer.parseInt(line);
						log.info("DownloadThread---thread{} 已下载: {}", threadId, lastIndex);

						this.startIndex = lastIndex - 1;// 新的下载起始点(从0开始,即减1)
					}
				}

				// 2.再次连接服务器,请求部分数据
				conn = (HttpURLConnection) new URL(serverPath).openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5 * 1000);
				conn.setReadTimeout(5 * 1000);

				conn.setRequestProperty("Range", "bytes=" + startIndex + "-" + endIndex);
				conn.connect();
				log.info("DownloadThread---thread{} 实际 下载范围: {}->{}", threadId, startIndex, endIndex);

				// 3.请求成功,写入目标文件
				if (conn.getResponseCode() == 206) {// 请求部分数据成功的响应码是 206
					raf = new RandomAccessFile(new File(localPath, getFileNameByPath(serverPath)), "rwd");// 打开写入到目标文件的输出流
					raf.seek(startIndex);// 把写入目标文件的输出位置移至 startIndex
					InputStream is = conn.getInputStream();

					byte[] b = new byte[1024 * 4];
					int len = 0;
					int total = 0;
					while ((len = is.read(b)) != -1) {
						raf.write(b, 0, len);
						total += len;

						// 4.每次读取流里数据之后,同步把当前线程下载的总进度写入[进度文件]
						praf.seek(0);
						praf.write((startIndex + total + "").getBytes("UTF-8"));
					}
					finishedThreadCount++;
					log.info("DownloadThread---thread{} 下载完毕!!!", threadId);

					// progressFile.delete();

					// 5.删除[进度文件] -> 当所有线程都下载完毕后,再去删除[进度文件]
					// --->为了防止: 线程0下载完毕,删除0号进度文件,但此时与服务器断开! 再次启动下载时,0号线程还得重新下载
					synchronized (serverPath) {
						if (threadCount == finishedThreadCount) {
							for (int i = 0; i < finishedThreadCount; i++) {
								File f = new File(localPath, "data" + i);
								f.delete();
								log.info("DownloadThread---thread{} 删除进度文件: {}", i, f.getName());
							}
							finishedThreadCount = 0;
						}
					}
				} else {
					log.info("DownloadThread---服务器不支持多线程下载! 响应码: {}", conn.getResponseCode());
				}
			} catch (Exception e0) {
				log.error("Exception---DownloadThread: Thread" + threadId + ": ", e0);
			} finally {
				try {
					if (null != fis) {
						fis.close();
					}
					if (null != raf) {
						raf.close();
					}
					if (null != praf) {
						praf.close();
					}
				} catch (Exception e1) {
				}
			}
		}
	}

	private String getFileNameByPath(String path) {
		return path.substring(path.lastIndexOf("/") + 1);
	}
}

#多线程下载原理
	//客户端要下载一个文件, 首先请求服务器,服务器将这个文件传送给客户端,客户端保存到本地, 完成了一个下载的过程.
	//多线程下载的思想是客户端开启多个线程同时下载,每个线程只负责下载文件的一部分, 当所有线程下载完成的时候,文件下载完毕. 
	//并不是线程越多下载越快, 与网络环境有很大的关系
	//在同等的网络环境下,多线程下载速度要高于单线程.
	//多线程下载占用资源比单线程多,相当于用资源换取速度
	//java代码实现多线程下载

#代码的思路:
	//首先要获取要下载文件的大小
	//在磁盘上使用RandomAccessFile 这个类在磁盘上创建一个大小一样的文件,将来将数据写入这个文件.
	//为每个线程分配下载任务. 内容包括线程现在文件的开始位置和结束位置.这里面有一点数学知识,代码中有备注.
	//启动下载线程
	//判断有没有保存上次下载的临时文件.
	//在启动线程下载的时候保存下载的位置信息
	//下载完毕后删除当前线程产生的临时文件
