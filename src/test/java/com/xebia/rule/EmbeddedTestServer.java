package com.xebia.rule;

import org.junit.rules.ExternalResource;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class EmbeddedTestServer extends ExternalResource {

	Server server;
	int port = 8080;

	@Override
	protected void before() throws Throwable {
		server = new Server(port);
		server.addHandler(new WebAppContext("src/main/webapp", "/"));
		server.start();
	}

	@Override
	protected void after() {
		try {
			server.stop();
		} catch (Throwable t) {}
	}

	public String uri() {
		return "http://localhost:" + port;
	}
}
