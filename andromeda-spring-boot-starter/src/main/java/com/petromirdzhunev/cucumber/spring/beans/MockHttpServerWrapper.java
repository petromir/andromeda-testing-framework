package com.petromirdzhunev.cucumber.spring.beans;

import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A Spring component managing MockServer integration by exposing the sever port.
 */
@Component
public class MockHttpServerWrapper {

	@Value("${mock.http-server.port}")
	private Integer mockHttpServerPort;

	private ClientAndServer delegate;

	public void start() {
		if (delegate == null) {
			delegate = ClientAndServer.startClientAndServer(mockHttpServerPort);
		}
	}

	public void reset() {
		delegate.reset();
	}

	public ClientAndServer delegate() {
		return delegate;
	}
}