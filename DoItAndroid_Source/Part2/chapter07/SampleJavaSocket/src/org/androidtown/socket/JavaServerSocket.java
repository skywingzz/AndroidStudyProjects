package org.androidtown.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Ç¥ÁØ ÀÚ¹Ù·Î ¸¸µç ¼­¹ö ¼ÒÄÏ
 * 
 * 1. Å¬¶óÀÌ¾ðÆ®·ÎºÎÅÍÀÇ ¿¬°á ¿äÃ»À» ¹ÞÀ¸¸é,
 * 2. Object µ¥ÀÌÅÍ¸¦ ¹ÞÀº ÈÄ,
 * 3. Object µ¥ÀÌÅÍ¿¡ ¹®ÀÚ¿­À» ºÙ¿©¼­ º¸³¿
 * 
 * @author Mike
 *
 */
public class JavaServerSocket {
	public static void main(String[] args) {
		
		try {
			ServerSocket server = new ServerSocket(11001);
			System.out.println("¼­¹ö ¼ÒÄÏÀÌ ¸¸µé¾îÁ³½À´Ï´Ù. Æ÷Æ® : 11001");
			
			while(true) {
				Socket aSocket = server.accept();
				System.out.println("Å¬¶óÀÌ¾ðÆ®¿Í ¿¬°áµÊ.");
				
				ObjectInputStream instream = new ObjectInputStream(aSocket.getInputStream());
				Object obj = instream.readObject();
				System.out.println("¹ÞÀº µ¥ÀÌÅÍ : " + obj);

				ObjectOutputStream outstream = new ObjectOutputStream(aSocket.getOutputStream());
				outstream.writeObject(obj + " from Server.");
				outstream.flush();
				System.out.println("º¸³½ µ¥ÀÌÅÍ : " + obj + " from Server.");
				
				aSocket.close();
				System.out.println("¼ÒÄÏ ´ÝÀ½.");
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
