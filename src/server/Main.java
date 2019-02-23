package server;

public class Main {
	public static void main(String[] args) {
		Window w = new Window();
		w.start();
		Server server = new Server();
		server.start();
	}
}
