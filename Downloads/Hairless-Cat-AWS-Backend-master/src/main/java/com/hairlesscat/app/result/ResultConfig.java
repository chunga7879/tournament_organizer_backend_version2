//package com.hairlesscat.app.result;
//
//import com.hairlesscat.app.result.ResultRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.List;
//
//@Configuration
//public class ResultConfig {
//	@Bean(name = "result_init")
//	CommandLineRunner commandLineRunner(ResultRepository resultRepository) {
//		return args -> {
//			Result result1 = Result.builder()
//				.mid(1L)
//				.build();
//
//			resultRepository.saveAll(List.of(result1));
//		};
//	}
//}
