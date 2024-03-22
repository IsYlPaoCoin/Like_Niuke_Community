package com.nowcoder.community.community.controller;

import com.nowcoder.community.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
}
