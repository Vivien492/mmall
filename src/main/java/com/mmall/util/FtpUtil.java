package com.mmall.util;

import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class FtpUtil {
    public static final Logger logger  = LoggerFactory.getLogger(FtpUtil.class);

    private static String ftpIp = PropertiesUtil.getProperty("ftp.server.ip");
    private static String ftpUser = PropertiesUtil.getProperty("ftp.user");
    private static String ftpPass = PropertiesUtil.getProperty("ftp.pass");

    private String ip;
    private int port;
    private String user;
    private String password;
    private FTPClient ftpClient;

    public FtpUtil(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public static boolean uploadFile(List<File> fileList) throws IOException {
        FtpUtil ftpUtil = new FtpUtil(ftpIp,21,ftpUser, ftpPass);
        logger.info("start to connect ftp server");
        boolean result = ftpUtil.uploadFile("img",fileList);
        logger.info("stop upload , the result is {}");
        return result;
    }

    private boolean uploadFile(String remotePath,List<File> fileList) throws IOException {
        boolean uploaded = true;
        FileInputStream fileInputStream = null;
        // connect to ftp server
        if ( !connectServer(ip,port,user,password) ){ uploaded = false; }
        try {
            ftpClient.changeWorkingDirectory(remotePath);
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            for (File file :
                    fileList) {
                fileInputStream = new FileInputStream(file);
                ftpClient.storeFile(file.getName(),fileInputStream);
            }
        } catch (IOException e) {
             logger.error("upload file error",e);
        }finally {
            fileInputStream.close();
            uploaded = false;
            ftpClient.disconnect();
        }
        return uploaded;
    }

    private boolean connectServer(String ip,int port,String user,String password){
        boolean isSuccess = false;
        ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            isSuccess = ftpClient.login(user,password);
        } catch (IOException e) {
            logger.error("connect to ftp server error",e);
        }
        return isSuccess;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }
}
