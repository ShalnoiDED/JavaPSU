/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javalabs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javalabs.MainForm.clients;
import static javalabs.MainForm.frame;

/**
 *
 * @author Максим
 */
public class UDPServer {
    static final int SERVER_PORT = 9876,
                     BUFFER_SIZE = 1024,
                     THREADS_LIMIT = 100;
    DatagramSocket serverSocket;
    
    static double result = 0.0;
    public static ExecutorService threadPool;
    
    public boolean Start() throws RangeException {
        serverSocket = null;
        threadPool = Executors.newFixedThreadPool(THREADS_LIMIT);
        
        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
        } catch (Exception ex) {
            throw new RangeException(ex.getLocalizedMessage());
        }
        System.out.println("Server " + SERVER_PORT);
        return true;
    }
    
    public void Run() throws RangeException {
        byte[] receiveBuffer = new byte[BUFFER_SIZE];
        try {
            while (true) {
                // Пакет для получения запросов
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                // Ожидание запросов
                serverSocket.receive(receivePacket);
                
                // Узнаем порт, который отправил запрос
                int clientPort = receivePacket.getPort();
                // Обращаемся к словарю порт-обработчик клиента
                // если такого порта нет в словаре, значит клиент только присоединился
                if (!clients.containsKey(clientPort)) {
                    System.out.println(" + port: " + clientPort + "\t| ");
                    
                    // Создаем объект обработчика клиента, в конструктор передаем сокет сервера, адрес клиента и его порт
                    ClientHandler clientHandler = new ClientHandler(serverSocket, receivePacket.getAddress(), clientPort);
                    // В словарь добавляем нового клиента
                    clients.put(clientPort, clientHandler);
                    // Отображаем в окне изменение в кол-ве подкл. клиентов
                    frame.changeCounter();
                    // Добавляем в коллекцию потоков наш обработчик(он реализует интерф. Runnable)
                    threadPool.submit(clientHandler);
                } 
                // если такой порт есть в словаре, значит клиент уже подключен к серверу
                else {
                    // значит клиент что-то хочет от сервера, преобразуем массив байтов в строчку - получим запрос
                    String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("<- port: " + clientPort + "\t| " + request.replaceAll("\n", " "));
                    
                    // По порту получаем необходимый обработчик
                    ClientHandler handler = clients.get(clientPort);
                    // Вызываем метод обработки запроса
                    handler.processRequest(request);
                }
            }
        } catch (IOException ex) {
            throw new RangeException(ex.getLocalizedMessage());
        } finally {
            // Закрытие сокета сервера
            close();
        }
    }
    
    public void close() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }

        if (threadPool != null) {
            threadPool.shutdown();
        }
    }

    double Result(RecIntegral integral) {
        
        result = 0.0;
        // Делим отрезок интегрирования на равные куски для каждого клиента.
        double[] param = integral.GetResultFields();
        double length = (param[2] - param[1]) / clients.size();
        double start = param[1];
        
        for (ClientHandler client : clients.values()) {
            // Помечаем клиента, как занятого
            client.isBusy = true;
            // Отправляем клиенту данные
            client.sendAnswer("CALC\n" + start + "\t" + (start + length) + "\t" + param[3] + "\t0");
            start += length;
        }
        
        for (ClientHandler client : clients.values()) {
            // Ждем пока клиент посчитает
            while (client.isBusy) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        return result;
    }
}
