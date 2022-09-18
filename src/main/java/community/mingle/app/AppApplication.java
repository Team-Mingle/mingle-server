package community.mingle.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class AppApplication {


	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

//	@PostConstruct
//	public void started(){
//		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
//	}

}
