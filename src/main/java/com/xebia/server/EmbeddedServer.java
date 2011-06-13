package com.xebia.server;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class EmbeddedServer {

	static Server server;
	static int port = 8080;

	public static void main(String args[]) throws Exception {
		server = new Server(port);
		server.addHandler(new WebAppContext("src/main/webapp", "/"));
		server.start();
	}
}
