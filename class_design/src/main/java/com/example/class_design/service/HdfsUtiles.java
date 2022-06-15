package com.example.class_design.service;
import com.example.class_design.dao.Path_infoDao;
import com.example.class_design.domain.Path_info;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class HdfsUtiles{
    private static FileSystem fs=null;
    static {
        try {
            Configuration conf = new Configuration();     //读取配置文件
            fs = FileSystem.get(conf);                    //获取客户端，文件系统
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static FileSystem getfs() throws Exception {
        return fs;
    }

    public static Path_infoDao list_catalog(String path) throws Exception
    {
        Path s_path = new Path(path);
        Path_infoDao path_infoDao = new Path_infoDao();
        DecimalFormat df = new DecimalFormat("#.000");
        if(fs.exists(s_path)) {
            for (FileStatus status : fs.listStatus(s_path)) {
                String temp = status.getPath().toString().substring(9);
                String pretemp = temp.substring(0, temp.lastIndexOf('/')+1);
                String a = df.format(status.getLen()/1024.0/1024.0);
                if(a.equals(".000")){
                    a = "0 B";
                }else if(a.charAt(0)=='.')
                    a = df.format(status.getLen()/1024.0)+" KB";
                else
                    a += " MB";
                path_infoDao.getPath_infos().add(new Path_info(pretemp, temp.substring(temp.lastIndexOf('/')+1), status.isFile(), a, status.getPath().toString()));
            }
        }
        return path_infoDao;
    }

    public static List<Integer> blks(FileSystem fs, String path) throws Exception{
        Path p = new Path(path);
        FileStatus ifile = fs.getFileStatus(p);   //获取文件信息
        BlockLocation[] blks = fs.getFileBlockLocations(ifile, 0, ifile.getLen());   //获取块信息
        List<Integer> sum = new ArrayList<>();
        for (BlockLocation b:blks) {
            sum.add((int) b.getLength());
        }
        return sum;
    }

    public static void read(String path,String name, HttpServletResponse rs, HttpServletRequest re, boolean flag) throws Exception{
        Path File=new Path(path);
        FSDataInputStream in=HdfsUtiles.getfs().open(File);
        if(flag){
            if("mp4".equals(name)) {
                rs.setContentType("video/mp4");
                show_mp4_and_flv(path, rs, re, in);
            }else if("flv".equals(name)){
                show_mp4_and_flv(path, rs, re, in);
            }
            return;
        }else{
            rs.setContentType("application/octet-stream");
            rs.setHeader("Content-disposition", String.format("attachment;filename=\"%s\"", path.substring(path.lastIndexOf('/'))));
        }
        List<Integer> size=HdfsUtiles.blks(HdfsUtiles.getfs(), path);
        OutputStream out=rs.getOutputStream();
        for (Integer i:size) {
            IOUtils.copyBytes(in, out, i);
        }
        out.close();
    }
    //断电续传
    public static void show_mp4_and_flv(String path, HttpServletResponse rs, HttpServletRequest re, FSDataInputStream in) throws Exception{
        rs.setHeader("Accept-Ranges", "bytes"); //表示支持断电续传
        long p = 0L;
        long contentLength = 0L;
        int rangeSwitch = 0; // 0,从头开始的全文下载；1,从某字节开始的（bytes=27000-）
        long fileLength=0;
        String rangBytes = "";
        List<Integer> size=HdfsUtiles.blks(HdfsUtiles.getfs(), path);
        for (Integer i:size) {
            fileLength += i;
        }
        String range = re.getHeader("Range");
        if (range != null && range.trim().length() > 0 && !"null".equals(range)) {
//                    200是OK（一切正常），206是Partial Content（服务器已经成功处理了部分内容），
//                    416 Requested Range Not Satisfiable（对方（客户端）发来的Range 请求头不合理）。
            rs.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);
            rangBytes = range.replaceAll("bytes=", "");
            if (rangBytes.endsWith("-")) { // bytes=270000-
                rangeSwitch = 1;
                p = Long.parseLong(rangBytes.substring(0, rangBytes.indexOf("-")));
                contentLength = fileLength - p; // 客户端请求的是270000之后的字节（包括bytes下标索引为270000的字节）
            }
        } else {
            contentLength = fileLength;
        }
        rs.setHeader("Content-Length", new Long(contentLength).toString());
        OutputStream out=rs.getOutputStream();
        if (rangeSwitch == 1) {  // 1,从某字节开始的（bytes=27000-）；2,从某字节开始到某字节结束的（bytes=27000-39000）
            String contentRange = new StringBuffer("bytes ").append(new Long(p).toString()).append("-")
                    .append(new Long(fileLength - 1).toString()).append("/")
                    .append(new Long(fileLength).toString()).toString();
            rs.setHeader("Content-Range", contentRange);
            long start=p;
//            IOUtils.copyBytes(in, out, start, fileLength-p);
            IOUtils.skipFully(in, p);
            try {
                IOUtils.copyBytes(in, out, (int) (fileLength - p));
            }catch (Exception e){
                out.close();
                return;
            }
        } else {
            String contentRange = new StringBuffer("bytes ").append("0-").append(fileLength - 1).append("/")
                    .append(fileLength).toString();
            rs.setHeader("Content-Range", contentRange);
            for (Integer i:size) {
                IOUtils.copyBytes(in, out, i);
            }
        }
        out.close();
    }

    public static String Url_decode(String path) {
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    public static String Url_encode(String path) {
        try {
            path = java.net.URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }
}
