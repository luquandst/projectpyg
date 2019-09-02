package com.zelin.test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class TestFreeMarker {
    public static void main(String[] args) {
        try {
            //1.创建配置类文件
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);
            //2.设置模板所在目录
            configuration.setDirectoryForTemplateLoading(new File("D:\\java\\project\\pinyougou-parent\\freemarkedemo\\src\\main\\resources"));
            //3.设置模板的字符集
            configuration.setDefaultEncoding("utf-8");
            //4.加载模板，创建一个新的模板对象
            Template template = configuration.getTemplate("test.ftl");
            //5.创建一个实体类对象,并往实体类对象中添加值
            Map map = new HashMap<>();
            map.put("name","张三");
            map.put("message","上午好");
            //6.创建一个writer对象，一般是创建filewriter对象
           Writer writer = new FileWriter(new File("D:\\test\\item\\test.html"));
            //7.调用模板的process方法生成模板
            template.process(map,writer);
            //8.关闭输出流对象
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        System.out.println("模板生成成功....");

    }
}
