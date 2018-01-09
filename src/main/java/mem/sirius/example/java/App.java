package mem.sirius.example.java;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Zdes bil ya
@SpringBootApplication
public class App {
    public static int port = 0;
    public static final String myip = "http://memsproblems.000webhostapp.com/ac/";
    public static final String mem_dir = "/home/azat/memes";

    public static void main(String[] args) {
        port = Integer.parseInt(System.getenv("$PORT") != null ? System.getenv("$PORT") : "5000");
        System.out.println("Port is :" + port);
        SpringApplication.run(App.class, args);
    }

    @RestController
    public static class MemeController {
        @RequestMapping("/")
        public String string() {
            return "kek";
        }
    }


    @Configuration
    public static class ServletConfig {
        @Bean
        public EmbeddedServletContainerCustomizer containerCustomizer() {
            return new EmbeddedServletContainerCustomizer() {
                @Override
                public void customize(ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {
                    configurableEmbeddedServletContainer.setPort(port);
                }
            };
        }
    }
}
