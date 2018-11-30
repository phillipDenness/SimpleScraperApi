package com.phillip.denness.scraper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
@Configuration
public class ConnectorConfig {
//
// @Bean
// public ServletWebServerFactory servletContainer() {
//
//  TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//
//   @Override
//   protected void postProcessContext(Context context) {
//    SecurityConstraint securityConstraint = new SecurityConstraint();
//    securityConstraint.setUserConstraint("CONFIDENTIAL");
//    SecurityCollection collection = new SecurityCollection();
//    collection.addPattern("/*");
//    securityConstraint.addCollection(collection);
//    context.addConstraint(securityConstraint);
//   }
//  };
//  tomcat.addAdditionalTomcatConnectors(redirectConnector());
//  return tomcat;
// }
//
// private Connector redirectConnector() {
//  Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
//  connector.setScheme("http");
//  connector.setPort(5000);
//  connector.setSecure(false);
//  connector.setRedirectPort(5001);
//  return connector;
// }

 @Bean
 public WebMvcConfigurer corsConfigurer() {
  return new WebMvcConfigurerAdapter() {
   @Override
   public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
   }
  };
 }
}
