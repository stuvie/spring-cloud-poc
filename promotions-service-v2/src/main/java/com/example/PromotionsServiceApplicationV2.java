package com.example;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@SpringBootApplication
public class PromotionsServiceApplicationV2 {

	public static void main(String[] args) {
		SpringApplication.run(PromotionsServiceApplicationV2.class, args);
	}
	
	@Bean
    CommandLineRunner runner(ReservationRepository rr) {
        return args -> {

            rr.deleteAll();

            Arrays.asList("Buy One Get One Free,Free Shipping on All Orders Over $50,Free Socks with Every Order".split(","))
                    .forEach(x -> rr.save(new Promotion(x)));

            rr.findAll().forEach(System.out::println);
        };
    }
}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Promotion, Long> {

    @RestResource(path = "by-name")
    Collection<Promotion> findByName(@Param("name") String name);
}

@Entity
class Promotion {
	@Id
    @GeneratedValue
    private Long id;
	
	private String name;

	Promotion() {
    }

    public Promotion(String name) {
        this.name = name;
    }
    
    public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Promotion [id=" + id + ", name=" + name + "]";
	}
}

@RefreshScope
@RestController
class MessageRestController {

    @Value("${message}")
    private String message;

    @RequestMapping("/message")
    String message() {
        return this.message;
    }
}
