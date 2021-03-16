package ua.edu.sumdu.j2ee.ilchenkoYegor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import ua.edu.sumdu.j2ee.ilchenkoYegor.properties.RestProperties;

@SpringBootApplication(scanBasePackages = "ua.edu.sumdu.j2ee.ilchenkoYegor")
/*@ComponentScan(basePackages = "ua.edu.sumdu.j2ee.ilchenkoYegor"*//*.controller", "ua.edu.sumdu.j2ee.ilchenkoYegor.Interfaces", "ua.edu.sumdu.j2ee.ilchenkoYegor.parsers"}*//*)*/
@EnableConfigurationProperties(RestProperties.class)
public class DemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext c = SpringApplication.run(DemoApplication.class, args);

    }

}
