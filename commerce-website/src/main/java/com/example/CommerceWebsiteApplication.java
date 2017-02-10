package com.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@EnableZuulProxy
@EnableCircuitBreaker
@EnableDiscoveryClient
@SpringBootApplication
public class CommerceWebsiteApplication {

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(CommerceWebsiteApplication.class, args);
	}
}

@RefreshScope
@Controller
class HomeController {
	private final RestTemplate restTemplate;
	
	@Value("${message}")
	private String message;
	
	@Autowired
	public HomeController(@LoadBalanced RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	@RequestMapping("/")
	public String loadHome(Model model){
		List<String> inventory = restTemplate.exchange("http://catalog-service/catalogs",
				HttpMethod.GET, null,
				new ParameterizedTypeReference<Resources<Promotion>>() {
				})
			.getBody()
			.getContent()
			.stream()
			.map(Promotion::getName)
			.collect(Collectors.toList());
		
		ResponseEntity<String> clearance = restTemplate.getForEntity("http://catalog-service/catalog", String.class);
		
		model.addAttribute("inventory", inventory);
		model.addAttribute("clearance", clearance);
		model.addAttribute("message", message);
		return "home";
	}
	
	@RequestMapping("/admin")
	public String loadAdmin(Model model){
		model.addAttribute("message", message);
		return "admin";
	}
	
	@RequestMapping("/documentation")
	public String loadDocumentation(Model model){
		model.addAttribute("message", message);
		return "documentation";
	}
}

@RestController
@RequestMapping("/promotions")
class PromotionServiceApiGatewayRestController {

	private final RestTemplate restTemplate;
	
	@Autowired
	public PromotionServiceApiGatewayRestController(@LoadBalanced RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	public Collection<String> fallbackPromotions() {
		ArrayList<String> fallback = new ArrayList<String>();
		fallback.add("25% off, for a limited time only");
		return fallback;
	}
	
	@HystrixCommand(fallbackMethod = "fallbackPromotions")
	@RequestMapping(method = RequestMethod.GET, value = "/names")
	public Collection<String> names() {
		return this
				.restTemplate
				.exchange("http://promotions-service/promotions",
						HttpMethod.GET, null,
						new ParameterizedTypeReference<Resources<Promotion>>() {
						})
				.getBody()
				.getContent()
				.stream()
				.map(Promotion::getName)
				.collect(Collectors.toList());
	}
}

class Promotion {
	private String name;

	public String getName() {
		return name;
	}
}
