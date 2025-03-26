/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author Максим
 */
public class UDPClient {
    static final int SERVER_PORT = 9876,
                     BUFFER_SIZE = 1024;
    
    // Адрес нашего сервера - в локалке localhost
    static final String SERVER_ADDRESS = "localhost";
    static InetAddress serverAddress;
    
    
    public static void main(String[] args) {
        DatagramSocket clientSocket = null;
        
        try {
            clientSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            
            // Поток для обработки ответов от сервера, передаем сокет клиента
            Thread receiverThread = new Thread(new ClientReceiver(clientSocket));
            receiverThread.start();
            
            // буфер, чтобы отправить что нибудь серверу и он нас идентифицировал
            byte[] init = new byte[1024];
            // пакет для вышесказанного
            DatagramPacket sendInit = new DatagramPacket(init, init.length, serverAddress, SERVER_PORT);
            clientSocket.send(sendInit);
            
            // Чтение с консоли
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            
            System.out.println("Type 'exit' to quit.");
            
            // Цикл выполняется, пока жив поток для обработки ответов от сервера(на случай если сервер закроет соединение)
            while (receiverThread.isAlive()) {
                Thread.sleep(1000);
                
                String message = consoleInput.readLine();
                
                if (message.equalsIgnoreCase("exit")) {
                    // Отправляем серверу запрос, что мы закрываемся
                    String sendExit = "EXIT\n0\t0\t0\t0";
                    byte[] sendData = sendExit.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                    clientSocket.send(sendPacket);
                    
                    break;
                }
            }
        } catch (Exception ex) {
            System.err.println(ex.getLocalizedMessage());
        } finally {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
    }
}
