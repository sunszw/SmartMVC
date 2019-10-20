package com.study.controller;

import com.study.annotation.RequestMapping;

@RequestMapping("mvc")
public class HelloController {

    @RequestMapping("hello.do")
    public String hello() {
        //返回视图名
        return "hello";
    }

    @RequestMapping("demo01.do")
    public String demo01(){
        return "redirect:demo02.do";
    }

    @RequestMapping("/demo02.do")
    public String demo02(){
        return "demo02";
    }

}
