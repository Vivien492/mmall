package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FtpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("iFileService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    public String upload(MultipartFile file,String path){
        String fileName = file.getOriginalFilename();
        //extension name
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);

        //A --> a.abc
        //one day B upload another file named a.abc too, then A's upload will be lost.so here we use UUID.
        String uploadFileName = UUID.randomUUID()+"."+fileExtensionName;
        logger.info("start to upload file,file name:{},path:{},new file name:{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if ( !fileDir.exists() ){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File  targetFile = new File(path,uploadFileName);

        try {
            file.transferTo(targetFile);
//            already upload success
            //    upload targetFile to ftp server
            FtpUtil.uploadFile(Lists.<File>newArrayList(targetFile)); //guava
            //already upload to ftp server
            // after upload to ftp server , delete the file below directory "upload"
            targetFile.delete();
        } catch (IOException e) {
            logger.error("upload file error",e);
            return null;
        }
        return targetFile.getName();
    }

//    test
//    public static void main(String[] args) {
//        String file = "abc.sfa";
//        System.out.println(file.substring(file.lastIndexOf(".")+1));
//    }
}
