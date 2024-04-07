package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.annotation.LoginRequired;
import com.nowcoder.community.community.entity.User;
import com.nowcoder.community.community.service.UserService;
import com.nowcoder.community.community.util.CommunityUtil;
import com.nowcoder.community.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * ClassName: UserController
 * Package: com.nowcoder.community.community.controller
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/28 19:22
 * @Version 1.0
 */
@Controller
@RequestMapping("/user")
public class UserController {

    // 声明变量 接受路径值
    @Value("${community.path.upload}")
    private String uploadPath;

    // 声明变量 接受域名
    @Value("${community.path.domain}")
    private String domain;

    // 声明变量 获取项目的访问路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    // 获取当前用户
    @Autowired
    private HostHolder hostHolder;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @RequestMapping(path = "/setting",method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    // 声明model 携带数据
    public String uploadHeader(MultipartFile headerImage, Model model){ //headerimage: StandardMultipartHttpServletReequest$StandardMultipartFile@7720    model: size =0

        if(headerImage == null){
            model.addAttribute("error","您还没有选择图片");
            return "/site/setting";
        }
        // 暂存 文件后缀
        String fileName = headerImage.getOriginalFilename(); // fileName: "5fe84251d9f149d69b4a3a997c01823ff.png"

        // 从最后一个  点的索引 往后截取
        String suffix = fileName.substring(fileName.lastIndexOf(".")); // suffis:".png"
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确"); //model: size = 0
            return "/site/setting";
        }

        // 生成 随机的 文件名
        //5fe84251d9f149d69b4a3a9970c01823f.png
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件的 存放路径 uploadPath: C:/niuke/upload  dest: C:/niuke/upload/5fe84251d9f149d69b4a3a9970c01823f.png
        File dest = new File(uploadPath + "/" +fileName);
        //将当前文件内容 写入到目标 文件当中
        try {
            //存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：" + e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发生异常",e);
        }

        // 更新 当前用户 的头像路径 （web访问路径）
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        // user: "User{id=150, username='phdvb', passweord='04125197c0858631c87150df77bef9e4
        // hostHolder: HostHolder@7726
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(),headerUrl);
        // 更新成功后 回首页
        return "redirect:/index";
    }

    // 向浏览器 响应的是  二进制的数据
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName , HttpServletResponse response){
        // 服务器 存放路径
        fileName = uploadPath + "/" +fileName;
        // 向浏览器 输出 图片
        // 从最后一个  点的索引 往后截取
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        // 响应 图片
        response.setContentType("image/" + suffix);
        // 响应的 是二进制 ，需要用到字节流
        try(
            //创建文件的 输入流
            //注： 输出流 SpringMvc 会帮助我们自动关闭
            //    输入流 是我们自己创建的 需要自己手动关闭
            FileInputStream fis = new FileInputStream(fileName);
            OutputStream os = response.getOutputStream();
            ) {
            byte[] buffer = new byte[1024];
            int b=0;

            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }
}
