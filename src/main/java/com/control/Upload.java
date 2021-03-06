package com.control;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Ray on 2018/3/16 0016.
 **/
public class Upload extends HttpServlet {

    //指定目录来存储上传的文件
    private static final String PATH = "f:/test";

    // 上传配置
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //检查是否为多媒体上传
        if(!ServletFileUpload.isMultipartContent(request)){
            //如果不是则停止
            PrintWriter printWriter = response.getWriter();
            printWriter.println("Error: 表单必须包含 enctype=multipart/form-data");
            return;
        }

        //配置上传参数(创建磁盘工厂)
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // 创建处理工具
        ServletFileUpload upload = new ServletFileUpload(factory);
        //设置最大文件上传值
        upload.setFileSizeMax(MAX_FILE_SIZE);
        //设置最大请求值(包含文件和表单数据)
        upload.setSizeMax(MAX_REQUEST_SIZE);

        //构造指定目录来存储上传的文件
        String uploadPath = PATH;

        //如果目录不存在则创建
        File uploadDir = new File(uploadPath);
        if(!uploadDir.exists()){
            uploadDir.mkdir();
        }

        try{
            //解析请求的内容提取文件数据
            List<FileItem> formItems = upload.parseRequest(request);

            if(formItems != null && formItems.size() > 0){
                //迭代表单数据
                for(FileItem item : formItems){
                    //处理不在表单的字段
                    if(!item.isFormField()){
                        String fileName = new File(item.getName()).getName();
                        String filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);
                        //在控制台输出文件的上传路径
                        System.out.println(filePath);
                        //保存文件到硬盘
                        item.write(storeFile);
                        request.setAttribute("info","文件上传成功!");
                    }
                }
            }
        }catch (Exception e){
            request.setAttribute("info","错误信息: " + e.getMessage());
        }
        //跳转到 messageupload.jsp
        request.getRequestDispatcher("messageupload.jsp").forward(request,response);
    }
}
