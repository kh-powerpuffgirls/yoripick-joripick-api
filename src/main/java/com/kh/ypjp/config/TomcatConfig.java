package com.kh.ypjp.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
	
	@Override
    public void customize(TomcatServletWebServerFactory factory) {
        // HTTP 8081 포트 추가
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(8081);
        factory.addAdditionalTomcatConnectors(connector);
    }
	
}
