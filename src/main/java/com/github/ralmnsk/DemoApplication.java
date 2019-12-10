package com.github.ralmnsk;


import com.github.ralmnsk.agregator.IAgregator;
import com.github.ralmnsk.convertor.IConvertor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@SpringBootApplication
public class DemoApplication{
	public static void main(String[] args) {
//		SpringApplication.run(DemoApplication.class, args);
		ApplicationContext context=new AnnotationConfigApplicationContext(DemoApplication.class);
		IAgregator agregator=(IAgregator)context.getBean("agregator");
		agregator.getAgregatedList();
//		IConvertor convertor=(IConvertor)context.getBean("convertor");
//		File file=new File("C:\\Users\\iland\\IdeaProjects\\tool\\file0.log");
//		convertor.setFile(file);
//		convertor.convert();
		System.out.println("*");
	}

}
