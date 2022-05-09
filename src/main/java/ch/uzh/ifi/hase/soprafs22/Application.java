package ch.uzh.ifi.hase.soprafs22;

import ch.uzh.ifi.hase.soprafs22.constant.Gender;
import ch.uzh.ifi.hase.soprafs22.entity.User;
import ch.uzh.ifi.hase.soprafs22.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;


@RestController
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    // boot the app
    System.out.println("Starting application...");
    SpringApplication.run(Application.class, args);
  }

  /*
    @Bean
    public CommandLineRunner demoData(UserRepository userRepository) {
        return (args -> {
            User user1 = new User();
            user1.setName("Adam");
            user1.setPassword("1234");
            user1.setGender(Gender.MALE);
            user1.setBirthday(new Date());
            userRepository.save(user1);
        });
    }


  @Bean
  public CommandLineRunner run(UserRepository userRepository){
      return(args -> {
          insertUser(userRepository);
          System.out.println(userRepository.findAll());
      });
  }

  private void insertUser(UserRepository userRepository){
      // user1
      User user1 = new User();
      user1.setName("Adam");
      user1.setPassword("1234");
      user1.setGender(Gender.MALE);
      user1.setBirthday(new Date());
      userRepository.save(user1);

  }*/

  @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public String helloWorld() {
    return "The application is running.";
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*").exposedHeaders("token");
      }
    };
  }
}
