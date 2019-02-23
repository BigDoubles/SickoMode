package server;

/**
 * Class to format messages for users
 * */

public class MessageSender {
	
	//Sends a message as a chat
	public static String sendAsChat(String msg){
		return ("$CHAT " + msg);
	}
}
