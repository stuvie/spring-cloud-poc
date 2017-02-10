package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@EnableDiscoveryClient
@RestController
@SpringBootApplication
public class SlowDownstreamServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SlowDownstreamServiceApplication.class, args);
	}
	
	@Autowired
	private Tracer tracer;
	
	@RequestMapping(value="/clearance/{cid}/items", method=RequestMethod.GET)
	public @ResponseBody ArrayList<String> getClearance(@PathVariable Integer cid)  throws InterruptedException {
		
		ArrayList<String> list = new ArrayList<String>();
		
		Span s = this.tracer.createSpan("lookupClearance");
		try {
			this.tracer.addTag("customerId", cid.toString());
			
			s.logEvent("clearance DB query started");
			
			Thread.sleep(444);
			
			
			list.add("$100  Ford Pinto");
			list.add("$299  24inch HDMI monitor");
			
			s.logEvent("clearance DB query completed");
		}
		finally {
			this.tracer.close(s);
		}
		
		return (list);
	}
}
