package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.service.AlphaService;
import com.nowcoder.community.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * ClassName: AlphaController
 * Package: com.nowcoder.community.community.controller
 * Description:
 *
 * @Author 杨理
 * @Create 2024/3/20 18:06
 * @Version 1.0
 */
@Controller
//controller处理浏览器请求  ，
// 接下来调用业务组件  处理当前业务   ，
// 业务组件会调用Dao  系统数据库
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;

    //127.0.0.1:8088/community/alpha/hello
    @RequestMapping("/hello")
    @ResponseBody
    public String saveHello(){
        return "Hello";
    }

    //127.0.0.1:8088/community/alpha/data
    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    //在SpringMVC  下  ，怎样获取 请求对象、 响应对象
    //127.0.0.1:8088/community/alpha/http
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        // 获取请求  数据
        System.out.println(request.getMethod());
        //GET
        System.out.println(request.getServletPath());
        ///alpha/http
        // 得到 所有请求行的 key
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + ":" + value);
            //host:127.0.0.1:8088
        }
        //http://127.0.0.1:8088/community/alpha/http?code=123
        System.out.println(request.getParameter("code"));
        //123

        //返回响应数据   ==>浏览器返回   牛客网   一级标题
        response.setContentType("text/html;charset=utf-8");
        try(PrintWriter writer =response.getWriter()){
            writer.write("<h1>牛客网</h1>");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    // GET 请求
    // http://127.0.0.1:8088/community/alpha/students?current=29 &limit=20
    @RequestMapping(path="/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name ="current",required = false,defaultValue = "1") int current,
            @RequestParam(name ="limit",required = false,defaultValue = "10") int limit){
        System.out.println(current);
        //29
        System.out.println(limit);
        //20
        return "some students";
    }

    //根据学生id 查询一个学生
    // http://127.0.0.1:8088/community/alpha/student/12
    @RequestMapping(path = "/student/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id) {
        System.out.println(id);
        //12
        return "one student";
    }

    //浏览器想服务器提交数据  =》 POST
    @RequestMapping(path="/student",method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        //猪猪
        System.out.println(age);
        //24
        return "success";
    }

    //如何向浏览器响应数据？  =》响应动态HTML数据
    //http://127.0.0.1:8088/community/alpha/teacher
    //写法一
    @RequestMapping(path = "/teacher",method = RequestMethod.GET)
    //如果返回HTML, 则不加@ResponseBody
    public ModelAndView getTeacher() {
        ModelAndView mav = new ModelAndView();
        mav.addObject("name","张三");
        mav.addObject("age",30);
        //设置模板名称
        mav.setViewName("/demo/view");
        return mav;
    }

    //写法2
    //http://127.0.0.1:8088/community/alpha/school
    @RequestMapping(path = "/school",method = RequestMethod.GET)
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",209);
        return "/demo/view";
    }

    //3.  通常在异步请求中    响应JSON数据
    //  异步请求  =》 当前网页不动，它敲敲的访问服务器
    // Java 对象 ==》 JSON 字符串  ==》 JS 对象
    //http://127.0.0.1:8088/community/alpha/emp
    @RequestMapping(path = "/emp",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getEmp() {
        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",24);
        emp.put("salary",8000.00);
        return emp;
    }
    @RequestMapping(path = "/emps",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getEmps() {
        List<Map<String,Object>> list = new ArrayList<>();

        Map<String,Object> emp = new HashMap<>();
        emp.put("name","张三");
        emp.put("age",24);
        emp.put("salary",8000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","张wu");
        emp.put("age",27);
        emp.put("salary",12000.00);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name","张liu");
        emp.put("age",32);
        emp.put("salary",17000.00);
        list.add(emp);


        return list;
    }

    //01-- cookie 示例
    //http://127.0.0.1:8080/community/alpha/cookie/set
    @RequestMapping(path = "/cookie/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookie(HttpServletResponse response){
        //01.创建 cookie
        // 每一个cookie  只能存一组  字符串
        Cookie cookie = new Cookie("code", CommunityUtil.generateUUID());
        //02.设置cookie 生效范围
        // 告诉浏览器   指定哪些路径  才会发送 cookie
        cookie.setPath("/community/alpha");
        // 设置cookie 的有效时间
        // cookie默认  存到浏览器的内存里边  关闭  即消失
        // 一旦设置了生存时间  她会存到硬盘里边   长期有效   直到超过这个时间  才会无效
        cookie.setMaxAge(60 * 10);
        // 发送cookie
        response.addCookie(cookie);

        // 浏览器 response-Header
        //Set-Cookie:
        //code=4fbda04b85bd413cbdcaabd18ec81442; Max-Age=600; Expires=Mon, 25-Mar-2024 12:01:00 GMT; Path=/community/alpha

        return "set cookie";
    }

    //http://127.0.0.1:8080/community/alpha/cookie/get
    @RequestMapping(path = "/cookie/get", method = RequestMethod.GET)
    @ResponseBody
    // 获取 Cookie 中（key）的 code
    public String getCookie(@CookieValue("code") String code) {

        System.out.println(code);
        return "get cookie";
    }


    //02--session示例
    //http://127.0.0.1:8080/community/alpha/session/set
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    //由于 session 一直存放在服务端 , 所以里面存放什么数据都可以  《-》cookie中只能存 响应数据
    public String setSession(HttpSession session){
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return "set session";
    }
    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    //由于 session 一直存放在服务端 , 所以里面存放什么数据都可以  《-》cookie中只能存 响应数据
    public String getSession(HttpSession session){
        System.out.println(session.getAttribute("id"));
        //1
        System.out.println(session.getAttribute("name"));
        //test
        return "get session";
    }

    // ajax 示例
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    // 异步请求 不刷新网页  向浏览器 发送json字符串
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJSONString(0,"操作成功");
    }



}
