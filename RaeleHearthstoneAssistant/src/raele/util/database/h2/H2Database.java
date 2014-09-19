package raele.util.database.h2;

import java.io.IOException;
import java.sql.SQLException;

import org.h2.tools.Server;

public class H2Database {
	
	private H2Database() {}
	
	private static Server server = null;
	
	public static void main(String[] args) throws IOException, SQLException {
		start();
		System.out.println("Server is running.\n<Enter any input to the console to stop the server.>");
		System.in.read();
		stop();
	}

	public static void start() throws SQLException {
		if (server == null)
		{
			server = Server.createTcpServer();
			server.start();
		}
	}

	public static void stop() {
		if (server != null) server.stop();
		server = null;
	}
	
	public static boolean isRunning() {
		return server != null && server.isRunning(false);
	}
	
	public static Server getServer() {
		return server;
	}

}
