/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalabs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import static javalabs.MainForm.clients;
import static javalabs.MainForm.frame;
import static javalabs.UDPServer.result;

/**
 *
 * @author Максим
 */
public class ClientHandler implements Runnable {
    public boolean isRunning = true,
                   isBusy = false;
    private final DatagramSocket serverSocket;
    private final InetAddress clientAddress;
    private final int clientPort;

    public ClientHandler(DatagramSocket serverSocket, InetAddress clientAddress, int clientPort) {
            this.serverSocket = serverSocket;
            this.clientAddress = clientAddress;
            this.clientPort = clientPort;
    }

    @Override
    public void run() {
        System.out.println(" v port: " + clientPort + "\t| ");

        // При подключении клиенту отправляется приветственный ответ
        sendAnswer("OK\n0\t0\t0\t0");

        // Цикл работы потока, если running станет false, то обработчик завершит работу
        while (isRunning) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println(" x port: " + clientPort + "\t| ");
    }
    
    public void stop() {
        // При закрытии приложения всем клиентам будет отправлен такой запрос
        sendAnswer("EXIT\n0\t0\t0\t0");
    }
    
    // Обработка запросов
    public void processRequest(String request) {
        String[] part = request.split("\n");
        String[] data = part[1].split("\t");
        
        switch (part[0]) {
            case "EXIT":
                // Если клиент разорвал соединение
                disconnect();
                break;
            case "COMPL":
                // Если клиент закончил считать
                result += Double.parseDouble(data[3]);
                isBusy = false;
                break;
            default:
                System.err.println(" ? port: " + clientPort + "\t| " + request.replaceAll("\n", " "));
                break;
        }
    }
    
    // Отправка ответа клиенту по его адресу и порту
    public void sendAnswer(String answer) {
        try {
            byte[] answerBuffer = answer.getBytes();
            DatagramPacket answerPacket = new DatagramPacket(
                    answerBuffer, answerBuffer.length, clientAddress, clientPort);
            serverSocket.send(answerPacket);
            
            System.out.println("-> port: " + clientPort + "\t| " + answer.replaceAll("\n|\t", "\t"));
        } catch (IOException e) {
            System.err.println("Error sending response to " + clientPort + ": " + e.getMessage());
        }
    }
    
    // Отключение клиента от сервера
    private void disconnect() {
        System.out.println(" - port: " + clientPort + "\t| ");
        // Удаляем из словаря
        clients.remove(clientPort);
        // Меняем кол-во клиентов в окне
        frame.changeCounter();
        // Останавливаем цикл в run()
        isRunning = false;
    }
}
