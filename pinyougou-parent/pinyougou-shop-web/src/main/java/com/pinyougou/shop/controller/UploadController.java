package com.pinyougou.shop.controller;

import com.pinyougou.util.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")//注入
    private String FILE_SERVER_URL;//文件服务器地址

    @RequestMapping("/upload")
    public Result upload(MultipartFile file){

        //获取文件扩展名
        try {
            String originalFilename = file.getOriginalFilename();
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");

            //调用工具类
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);

            String url = FILE_SERVER_URL + path;
            System.out.println(url);
            //http://192.168.25.133/group1/M00/00/00/wKgZhV1ntkqAfD9hAAFe0SaR4Ek154.jpg
            return new Result(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败");
        }

    }
}
