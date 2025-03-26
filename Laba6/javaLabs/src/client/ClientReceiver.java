/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javalabs.RecIntegral;
import static client.UDPClient.SERVER_PORT;
import static client.UDPClient.BUFFER_SIZE;
import static client.UDPClient.serverAddress;

/**
 *
 * @author Максим
 */
public class ClientReceiver implements Runnable {
    private final DatagramSocket clientSocket;

    public ClientReceiver(DatagramSocket clientSocket) {
        this.clientSocket = clientSocket;
    }
    
    @Override
    public void run() {
        try {
            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            
            // Пока запущен поток обрабатываем ответы от сервера
            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                clientSocket.receive(receivePacket);
                String answer = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(answer.replaceAll("\n", " "));
                
                // вот тут обработка
                processAnswer(answer);
            }
        } catch (Exception ex) {
            if (!clientSocket.isClosed() && !Thread.currentThread().isInterrupted()) {
                System.err.println(ex.getLocalizedMessage());
            }
        }
    }
    
    // Функция обработки ответов от сервера
    private void processAnswer(String answer) {
        String[] parts = answer.split("\n");
        String[] data = parts[1].split("\t");
        
        switch (parts[0]) {
            case "EXIT":
                // Если сервер разорвал соединение
                System.err.println("Server closed connection");
                Thread.currentThread().stop();
                break;
            case "CALC":
            {
                // Если сервер просит посчитать
                System.err.println("busy\\");
                try {
                    // Наш интеграл, данные берутся из ответа от сервера(data[0-2])
                    RecIntegral integral = new RecIntegral(Double.parseDouble(data[0]),
                            Double.parseDouble(data[1]),
                            Double.parseDouble(data[2]));
                    // Считаем ответ, там счет разделен на 3 потока
                    double result = integral.Result();
                    
                    // Посчитали, теперь готовим и отправляем результат серверу
                    String sendAnswer = "COMPL\n" + data[0] + "\t" + data[1] + "\t" + data[2] + "\t" + result;
                    byte[] sendData = sendAnswer.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                    clientSocket.send(sendPacket);
                    
                    System.err.println("free/\nresult: " + result);
                } catch (Exception ex) {
                    Logger.getLogger(ClientReceiver.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            case "OK":
                break;
            default:
                System.err.println("Got unknown response");
                break;
        }
    }
}
