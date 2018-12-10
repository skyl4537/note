package com.bluecard.demo1;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EventListener;
import java.util.EventObject;

/**
 * 多线程下载器
 *
 * @author Tang
 *
 */
public final class MultiThreadDownloader {

    /**
     * 测试
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        final MultiThreadDownloader downloader = new MultiThreadDownloader(
        		"http://192.168.102.20:8081/nexus/content/shadows/central-m1/org.apache.poi/jars/poi-3.9-sources.jar"
        		,"C:/Users/123/Desktop/zheng_02/ceshi/poi-3.9-sources.jar"
        		,2048
        		,4
        		,null);
        downloader.setDownloadListener(new DownloadListener() {
            public void undoneDownload(DownloadEvent event) {
                System.out.println("下载未完成");
            }

            public void doneDownload(DownloadEvent event) {
                System.out.println("下载完成");
                long endTi = System.currentTimeMillis();
                System.out.println("结束时间："+endTi);
            }

            public void progressChange(DownloadEvent event) {
                System.out.println();
                MultiThreadDownloader downloader = (MultiThreadDownloader) event.getSource();
                System.out.println("下载进度：" + downloader.getProgress() + "%");
                System.out.println("已用时：" + downloader.getDoneTime() + "秒");
                //System.out.println("预计仍需用时：" + (downloader.getUndoneTime() / 1000) + "秒");
                System.out.println("下载速度为：" + (downloader.getDownloadSpeed() * 1000 / 1024 / 1024) + "MB/秒");
                System.out.println();
            }
        });
        downloader.startDownload();
    }

    /**
     * 多线程下载事件监听器
     */
    public static interface DownloadListener extends EventListener {
        /**
         * 下载完成的通知
         */
        void doneDownload(DownloadEvent event);

        /**
         * 下载未完成的通知
         */
        void undoneDownload(DownloadEvent event);

        /**
         * 下载进度改变的通知
         */
        void progressChange(DownloadEvent event);
    }

    /**
     * 多线程下载事件源
     */
    public static class DownloadEvent extends EventObject {

        private static final long serialVersionUID = 1L;

        public DownloadEvent(Object source) {
            super(source);
        }
    }

    /**
     * 文件下载的URL路径
     */
    private final String downloadPath;
    /**
     * 文件下载的URL
     */
    private final URL downloadUrl;

    /**
     * 保持文件到本地的文件路径
     */
    private final String savePath;
    /**
     * RandomAccessFile对象构建的模式，"rwd"表示此文件可读可写可删
     */
    private final String fileMode = "rwd";

    /**
     * 下载文件时，每次读取多少个字节
     */
    private final int bufferArrayInitialCapacity;

    /**
     * 启动多少个线程下载这个文件
     */
    private final int threadCount;

    /**
     * 文件总字节大小
     */
    private final long fileContentLength;

    /**
     * 已读取的字节大小
     */
    private long doneByteLength;
    /**
     * 剩余文件总字节大小
     */
    private long undoneByteLength;

    /**
     * 开始下载时的时间，单位：毫秒
     */
    private long beginDownloadTime;

    /**
     * 已完成所花费的时间，单位：毫秒
     */
    private long doneTime;
    /**
     * 预计剩余文件下载时间，单位：毫秒
     */
    private long undoneTime;

    /**
     * 下载速度，单位：字节/毫秒
     */
    private double downloadSpeed;

    /**
     * 下载进度，值在0~100之间，也就是0<=progress<=100
     */
    private int progress;

    /**
     * 是否暂停下载，如果为true则暂停下载，重新设为false则继续下载
     */
    private boolean isPause;

    /**
     * 是否关闭下载，如果为true，下载会被终止
     */
    private boolean isClose;

    /**
     * 下载是否完成
     */
    private boolean isDone;

    /**
     * 用来存放所有下载文件的线程
     */
    private final ShareEquallyDownloadThread[] downloadThreads;

    /**
     * 下载事件监听器
     */
    private DownloadListener downloadListener;
    /**
     * 下载事件源，事件源本来是需要每次创建的，但是这个是事件源什么属性都没有，所以为了节约内存只创建一个
     */
    private final DownloadEvent downloadEvent = new DownloadEvent(this);

    /**
     * @param downloadPath
     *            文件下载的URL路径
     * @param savePath
     *            保持文件到本地的文件路径
     * @throws IOException
     */
    public MultiThreadDownloader(String downloadPath, String savePath) throws IOException {
        this(downloadPath, savePath, 2048, 10, null);
    }

    /**
     * @param downloadPath
     *            文件下载的URL路径
     * @param savePath
     *            保持文件到本地的文件路径
     * @param threadCount
     *            启动多少个线程下载这个文件
     * @throws IOException
     */
    public MultiThreadDownloader(String downloadPath, String savePath, int threadCount) throws IOException {
        this(downloadPath, savePath, 2048, threadCount, null);
    }

    /**
     * @param downloadPath
     *            文件下载的URL路径
     * @param savePath
     *            保持文件到本地的文件路径
     * @param bufferArrayInitialCapacity
     *            每次读取字节的长度
     * @param threadCount
     *            启动多少个线程下载这个文件
     * @param downloadListener
     *            下载监听器
     * @throws IOException
     */
    public MultiThreadDownloader(String downloadPath, String savePath, DownloadListener downloadListener) throws IOException {
        this(downloadPath, savePath, 2048, 10, downloadListener);
    }

    /**
     * @param downloadPath
     *            文件下载的URL路径
     * @param savePath
     *            保持文件到本地的文件路径
     * @param bufferArrayInitialCapacity
     *            每次读取字节的长度
     * @param threadCount
     *            启动多少个线程下载这个文件
     * @param downloadListener
     *            下载监听器
     * @throws IOException
     */
    public MultiThreadDownloader(String downloadPath, String savePath, int bufferArrayInitialCapacity, int threadCount, DownloadListener downloadListener)
            throws IOException {

        this.downloadPath = downloadPath;
        this.savePath = savePath;
        this.bufferArrayInitialCapacity = bufferArrayInitialCapacity;
        this.threadCount = threadCount;
        this.downloadListener = downloadListener;

        downloadThreads = new ShareEquallyDownloadThread[threadCount];
        downloadUrl = new URL(downloadPath);

        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("URL:" + downloadPath + " responseCode is " + responseCode);
        }

        fileContentLength = connection.getContentLengthLong();
        if (fileContentLength < 0) {
            throw new IOException("URL:" + downloadPath + " download file content length less than 0!");
        }
        if (fileContentLength == 0) {
            throw new IOException("URL:" + downloadPath + " download file content length equals 0!");
        }

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(savePath, fileMode)) {
            randomAccessFile.setLength(fileContentLength);// 指定创建的文件的长度
        } catch (IOException e) {
            throw e;
        } finally {
            connection.disconnect();
        }

        // 平均每一个线程下载的文件的大小。
        long threadShareEquallyByteLength = fileContentLength / threadCount;

        for (int i = 0; i < threadCount; i++) {

            // Java中都是包前不包后的原则，所以endIndex不用减1
            long beginIndex = i * threadShareEquallyByteLength;

            // 防止均分有余数，余数部分的文件字节交给最后一个线程
            long endIndex = i < (threadCount - 1) ? beginIndex + threadShareEquallyByteLength : fileContentLength;

            downloadThreads[i] = new ShareEquallyDownloadThread(beginIndex, endIndex);
        }
    }

    /**
     * 负责下载和保存文件中的一部分的线程
     */
    private final class ShareEquallyDownloadThread extends Thread {

        private final long beginIndex;
        private final long endIndex;
        private boolean isDone;
        private HttpURLConnection connection;
        private RandomAccessFile randomAccessFile;

        public ShareEquallyDownloadThread(long beginIndex, long endIndex) {
            this.beginIndex = beginIndex;
            this.endIndex = endIndex;
        }

        @Override
        public void run() {
            try {
                connection = (HttpURLConnection) downloadUrl.openConnection();

                connection.setRequestProperty("Range", "bytes=" + beginIndex + "-" + endIndex);// 下载一部分文件，包前不包后

                try (InputStream inputStream = connection.getInputStream(); RandomAccessFile randomAccessFile = new RandomAccessFile(savePath, fileMode)) {
                    this.randomAccessFile = randomAccessFile;
                    randomAccessFile.seek(beginIndex);// 跳至指定的文件位置

                    byte[] bufferBytes = new byte[bufferArrayInitialCapacity];

                    int readByteLength = inputStream.read(bufferBytes);// 读

                    while (readByteLength > 0 && !isClose) {

                        if (isPause) {// 暂停
                            continue;
                        }

                        randomAccessFile.write(bufferBytes, 0, readByteLength);// 写

                        doneTime = System.currentTimeMillis() - beginDownloadTime;// 统计用时

                        // 每次将读取的的字节长度累加记录下来
                        doneByteLength += readByteLength;
                        undoneByteLength = fileContentLength - doneByteLength;

                        // 计算出已下载的文件占总文件的百分比
                        int percentCompleted = (int) (doneByteLength * 100.0 / fileContentLength);
                        percentCompleted = Math.min(Math.max(percentCompleted, 0), 100);
                        updateProgress(percentCompleted);// 更新进度

                        readByteLength = inputStream.read(bufferBytes);// 继续读
                    }

                    isDone = true;

                    if (downloadListener != null && isDone()) {
                        if (isClose || isPause) {
                            downloadListener.undoneDownload(downloadEvent);
                        } else {
                            downloadListener.doneDownload(downloadEvent);
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    connection.disconnect();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * 强制销毁连接和关闭文件流
         */
        public void destroyConnectionAndCloseStream() {
            if (connection != null) {
                connection.disconnect();
                try {
                    connection.getInputStream().close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 计算下载速度和剩余下载时间
     */
    private void updateDownloadSpeedAndUndoneTime() {
        downloadSpeed = doneByteLength / doneTime * 1.0;
        undoneTime = (long) (undoneByteLength / downloadSpeed);
    }

    /**
     * 更新进度
     *
     * @param progress
     */
    private void updateProgress(int progress) {
        if (this.progress != progress) {
            this.progress = progress;

            updateDownloadSpeedAndUndoneTime();

            if (downloadListener != null) {
                downloadListener.progressChange(downloadEvent);
            }
        }
    }

    /**
     * 开始下载
     */
    public void startDownload() {
        beginDownloadTime = System.currentTimeMillis();
        for (int i = 0; i < downloadThreads.length; i++) {
            downloadThreads[i].start();
        }
        System.out.println("开始下载时间："+beginDownloadTime);
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    /**
     * 获取文件总字节大小
     *
     * @return
     */
    public long getFileContentLength() {
        return fileContentLength;
    }

    public String getSavePath() {
        return savePath;
    }

    public int getBufferArrayInitialCapacity() {
        return bufferArrayInitialCapacity;
    }

    public int getThreadCount() {
        return threadCount;
    }

    /**
     * 文件下载是否全部完成
     *
     * @return
     */
    public boolean isDone() {
        if (isDone) {
            return isDone;
        }
        for (int i = 0; i < downloadThreads.length; i++) {
            if (!downloadThreads[i].isDone) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获得下载进度，值在0~100之间，也就是0<=progress<=100
     *
     * @return
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 暂停下载
     */
    public void pauseDownload() {
        this.isPause = true;
    }

    /**
     * 恢复下载
     */
    public void restoreDownload() {
        this.isPause = false;
    }

    public boolean isPause() {
        return isPause;
    }

    /**
     * 关闭下载，下载会被终止
     */
    public void closeDownload() {
        if (isClose) {
            return;
        }
        isClose = true;
        for (int i = 0; i < downloadThreads.length; i++) {
            downloadThreads[i].destroyConnectionAndCloseStream();
        }
    }

    public boolean isClose() {
        return isClose;
    }

    public URL getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * 获取已读取的字节大小
     *
     * @return
     */
    public long getDoneByteLength() {
        return doneByteLength;
    }

    /**
     * 获取剩余文件总字节大小
     *
     * @return
     */
    public long getUndoneByteLength() {
        return undoneByteLength;
    }

    /**
     * 获取已完成所花费的时间，单位：毫秒
     *
     * @return
     */
    public long getDoneTime() {
        return doneTime;
    }

    /**
     * 获取剩余文件下载时间，单位：毫秒
     *
     * @return
     */
    public long getUndoneTime() {
        return undoneTime;
    }

    /**
     * 获取下载速度，单位：字节/毫秒
     *
     * @return
     */
    public double getDownloadSpeed() {
        return downloadSpeed;
    }

    /**
     * 获取下载事件监听器
     *
     * @return
     */
    public DownloadListener getDownloadListener() {
        return downloadListener;
    }

    /**
     * 设置下载事件监听器
     *
     * @return
     */
    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }
   /* 开始下载时间：1532505197664
    * 结束时间：        1532505197997
    * 
    * */
}