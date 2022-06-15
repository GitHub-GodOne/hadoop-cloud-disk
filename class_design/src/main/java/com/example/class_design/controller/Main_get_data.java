package com.example.class_design.controller;
import com.example.class_design.dao.Path_infoDao;
import com.example.class_design.domain.Path_info;
import com.example.class_design.domain.User;
import com.example.class_design.service.GetCookie;
import com.example.class_design.service.HdfsUtiles;
import com.example.class_design.service.UserServicelmpl;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

@Controller
@CrossOrigin
public class Main_get_data {
    @Autowired
    UserServicelmpl userServicelmpl;

    @RequestMapping("/main")
    public String sent_table_data(Model mode, HttpServletRequest re){
        try {
            Path_infoDao tables_data = HdfsUtiles.list_catalog("/");
            System.out.println(tables_data);
            mode.addAttribute("get_datas", tables_data.getPath_infos());
            mode.addAttribute("path", tables_data.getPath_infos().get(0).getFiles1());
            mode.addAttribute("bring_path", "/");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return "redirect:/click?path="+GetCookie.getCook(re);
        }
    }

    @GetMapping("/click")
    public String click_show(@RequestParam("path")String path, Model mode, HttpServletRequest re){
        path = HdfsUtiles.Url_decode(path);
        String root_path = GetCookie.getCook(re);

        if(path.length()>=root_path.length()){
            if(! path.substring(0,root_path.length()).equals(root_path)){
                path = root_path + path;
            }
        }else {
            path = root_path + path;
        }
        try {
            if(!HdfsUtiles.getfs().exists(new Path(path))){
                mode.addAttribute("info_exists", "路径不存在!!!");
                return "hadoop";
            }
            Path_infoDao tables_data = HdfsUtiles.list_catalog(path);
            mode.addAttribute("get_datas", tables_data.getPath_infos());
            for(Path_info a:tables_data.getPath_infos()){
                if(a.getPath().substring(9).equals(path) && a.isIsfile()){
                    String name = path.substring(path.lastIndexOf('.')+1);
                    path = HdfsUtiles.Url_encode(path);
                    if("mp4".equals(name) || "flv".equals(name)) {
                        return "redirect:/setlook?path=" + path;
                    }else{
                        return "redirect:/look_some?path=" + path;
                    }
                }
            }
            mode.addAttribute("path", path);
            if (path.equals("/")){
                mode.addAttribute("bring_path", '/');
            }else{
                mode.addAttribute("bring_path", path+'/');
            }
        }catch (Exception e){
            e.printStackTrace();
        }
//        return "hadoop";
//        return "/click/?path="+HdfsUtiles.Url_encode(path);
        return "hadoop";
    }

    @GetMapping("/setlook")
    public String setlook(@RequestParam("path")String path, HttpServletRequest re, HttpServletResponse rs, Model model) throws Exception{
        model.addAttribute("path",path);
        model.addAttribute("type",path.substring(path.lastIndexOf('.')+1));
        return "player";
    }

    @GetMapping("/look")
    public String concon(@RequestParam("path")String path, HttpServletRequest re, HttpServletResponse rs, Model model) throws Exception{
        path = HdfsUtiles.Url_decode(path);
        String name="";
        try{
            name = path.substring(path.lastIndexOf('.')+1);
        }catch (Exception e){
            path = HdfsUtiles.Url_encode(path);
            return "redirect:/look_some/?path="+path;
        }
        HdfsUtiles.read(path,name, rs, re, true);
        return "redirect:/player.html";
    }

    @GetMapping("/look_some")
    @ResponseBody
    public String look_some(@RequestParam("path")String path, HttpServletRequest re, HttpServletResponse rs, Model model) throws Exception{
        path = HdfsUtiles.Url_decode(path);
        String name = path.substring(path.lastIndexOf('.')+1);
        Path File=new Path(path);
        FSDataInputStream in=HdfsUtiles.getfs().open(File);
        if("mp3".equals(name)){
            rs.setContentType("video/mp4");
        }
        else if("pdf".equals(name)){
            rs.setContentType("application/pdf");
        }
        else if("bmp".equals(name)){
            rs.setContentType("image/bmp");
        }
        else if("gif".equals(name)){
            rs.setContentType("image/gif");
        }
        else if("jpeg".equals(name)){
            rs.setContentType("image/jpeg");
        }else if("png".equals(name)){
            rs.setContentType("image/png");
        }
        else{
            rs.setContentType("text/plain; charset=utf-8");
        }
        List<Integer> size=HdfsUtiles.blks(HdfsUtiles.getfs(), path);
        OutputStream out=rs.getOutputStream();
        for (Integer i:size) {
            IOUtils.copyBytes(in, out, i);
        }
        out.close();
        return "";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("path")String path, HttpServletRequest re, HttpServletResponse rs) throws Exception{
        path = HdfsUtiles.Url_decode(path);
        HdfsUtiles.getfs().delete(new Path("hdfs://ns"+path));
        int count=0;
        for(char a:path.toCharArray()){
            if(a=='/'){
                count++;
                if (count > 1)
                    break;
            }
        }
        if(count==1){
            return "redirect:/";
        }
        return "redirect:/click/?path="+path.substring(0, path.lastIndexOf('/'));
    }

    @GetMapping("/create")
    public String create(@RequestParam("path")String path, HttpServletRequest re, HttpServletResponse rs) throws Exception{
        path = HdfsUtiles.Url_decode(path);
        HdfsUtiles.getfs().mkdirs(new Path(path));
        System.out.println(path);
        return "redirect:/click/?path="+path;
    }

    @GetMapping("/download")
    public String download(@RequestParam("path")String path, HttpServletRequest re, HttpServletResponse rs) throws Exception{
        path = HdfsUtiles.Url_decode(path);
        HdfsUtiles.read(path,"", rs, re,false);
        return "";
    }


    @PostMapping("/upload")
    public String upload(@RequestParam("path")String path, @RequestParam("file") MultipartFile srcFile[], RedirectAttributes redirectAttributes) throws Exception{
        path = HdfsUtiles.Url_decode(path);
        for (MultipartFile file : srcFile) {
            System.out.println(file.getOriginalFilename());
            Path File=new Path("hdfs://ns/"+path+'/'+file.getOriginalFilename());
            FSDataOutputStream out=HdfsUtiles.getfs().create(File);
            FileCopyUtils.copy(file.getInputStream(),out);
        }
        return "redirect:/click/?path="+path;
    }
    @RequestMapping("/login")
    public String login(String username, String password, Boolean rememberMe, Model model, HttpServletResponse rs, HttpServletRequest re){
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password, rememberMe);
        System.out.println("rememberMe:"+rememberMe);
        try{
            subject.login(token);
            Cookie cookie=new Cookie("cookie", username);
            cookie.setMaxAge(365*24*60*60);
            rs.addCookie(cookie);
            System.out.println("remember:"+subject.isRemembered()+"        "+token.isRememberMe());
            return "redirect:/main";
        }catch (UnknownAccountException e){
            model.addAttribute("msg","用户名不存在!!!");
            return "index";
        }catch (IncorrectCredentialsException e){
            model.addAttribute("msg","密码错误!!!");
            return "index";
        }
    }

    @RequestMapping("/noauth")
    @ResponseBody
    public String unauthorized(){
        return "未经授权无法访问此页面";
    }

    @RequestMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "index";
    }

    @RequestMapping("/regist")
    public String regist(String username, String password, Model model) {
        if(userServicelmpl.exixtUsername(username)){
            model.addAttribute("exit", "用户名已经存在");
            return "reset";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        userServicelmpl.insert_user(user);
        return "index";
    }
}
