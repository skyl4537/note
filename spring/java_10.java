0.HttpClient框架
1.多线程断点续传



#---------------------------------------------------------------------------------------------
0.HttpClient框架
	/**
	 * GET请求
	 * @param uri 请求uri
	 * @param map 请求参数 (当请求参数拼接在uri后面时,可传null)
	 * @return 请求结果
	 */
	public static String httpClientGet(String uri, Map<String, String> map) {
		String res = "";

		CloseableHttpClient hc = null;
		CloseableHttpResponse hr = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(uri);
			if (null != map && map.size() > 0) {// 非 (参数拼接在地址上)
				ArrayList<NameValuePair> nvps = new ArrayList<>();
				for (Entry<String, String> entry : map.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				uriBuilder.addParameters(nvps);
			}
			HttpGet hg = new HttpGet(uriBuilder.build());

			hc = HttpClients.createDefault();
			hr = hc.execute(hg);
			if (null != hr && 200 == hr.getStatusLine().getStatusCode()) {
				HttpEntity entity = hr.getEntity();
				res = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != hc) {
					hc.close();
				}

				if (null != hr) {
					hr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}	

	/**
	 * POST请求
	 * @param uri 请求uri
	 * @param map 请求参数
	 * @return 请求结果
	 */
	public static String httpClientPost(String uri, Map<String, String> map) {
		String res = "";

		CloseableHttpClient hc = null;
		CloseableHttpResponse hr = null;
		try {
			URIBuilder uriBuilder = new URIBuilder(uri);
			ArrayList<NameValuePair> nvps = new ArrayList<>();
			if (null != map && map.size() > 0) {
				for (Entry<String, String> entry : map.entrySet()) {
					nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}
				uriBuilder.addParameters(nvps);
			}

			HttpPost hp = new HttpPost(uriBuilder.build());
			hp.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));

			hc = HttpClients.createDefault();
			hr = hc.execute(hp);
			if (null != hr && 200 == hr.getStatusLine().getStatusCode()) {
				HttpEntity entity = hr.getEntity();
				res = EntityUtils.toString(entity, "UTF-8");
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != hc) {
					hc.close();
				}

				if (null != hr) {
					hr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	
1.多线程断点续传
	public class MultiThreadDownload {
		private static final Logger log = LoggerFactory.getLogger(MultiThreadDownload.class);

		private static int finishedThreadCount = 0;// 已下载完成的线程个数

		private String serverPath;
		private String localPath;
		private int threadCount = 4;

		public MultiThreadDownload(String serverPath, String localPath, int threadCount) {
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
				log.error("Exception---MultiThreadDownload: ", e);
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










