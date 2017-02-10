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
public class CatalogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatalogServiceApplication.class, args);
	}
	
	@Bean
    CommandLineRunner runner(ReservationRepository rr) {
        return args -> {

            rr.deleteAll();

            Arrays.asList("$1549  Macbook Pro,$599  Mac Mini,$100  Magic Mouse,$199  Apple TV".split(","))
                    .forEach(x -> rr.save(new Catalog(x)));

            rr.findAll().forEach(System.out::println);
        };
    }

}

@RepositoryRestResource
interface ReservationRepository extends JpaRepository<Catalog, Long> {

    @RestResource(path = "by-name")
    Collection<Catalog> findByName(@Param("name") String name);
}

@Entity
class Catalog {
	@Id
    @GeneratedValue
    private Long id;
	
	private String name;

	Catalog() {
    }

    public Catalog(String name) {
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
		return "Catalog [id=" + id + ", name=" + name + "]";
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

