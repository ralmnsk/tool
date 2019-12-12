package com.github.ralmnsk;


import com.github.ralmnsk.agregator.IAgregator;
import com.github.ralmnsk.convertor.IConvertor;
import com.github.ralmnsk.file.counter.IFileCounter;
import com.github.ralmnsk.file.counter.printer.FilePrinter;
import com.github.ralmnsk.file.counter.printer.IFilePrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.File;
import java.util.concurrent.Executor;

@Configuration
@SpringBootApplication
@EnableAsync
public class DemoApplication{
	@Autowired
	private IFileCounter fileCounter;

	public static void main(String[] args) {
//		SpringApplication.run(DemoApplication.class, args);
		ApplicationContext context=new AnnotationConfigApplicationContext(DemoApplication.class);
		IFilePrinter fp=context.getBean("filePrinter", FilePrinter.class);
		fp.print();
//		IAgregator agregator=(IAgregator)context.getBean("agregator");
//		agregator.getAgregatedList();
//		IConvertor convertor=(IConvertor)context.getBean("convertor");
//		File file=new File("C:\\Users\\iland\\IdeaProjects\\tool\\file0.log");
//		convertor.setFile(file);
//		convertor.convert();
//		System.out.println("");
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(fileCounter.getFiles().size());
		executor.setMaxPoolSize(fileCounter.getFiles().size());
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("ToolThread-");
		executor.initialize();
		return executor;
	}

}
