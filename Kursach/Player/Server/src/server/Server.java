package server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Server {
    private static final int RECEIVE_PORT = 17;// Порт для приема сообщений от клиента
    private static final int SEND_PORT = 26;// Порт для отправки сообщений клиенту
    private static final int FILE_PORT = 28;// Порт для передачи файлов клиенту
    

    private static ArrayList<Song> mainArray;

    public static class Song {
        public String fileName; //имя файла
        public String filePath; //путь к файлу
        public long fileSize; //размер файла в байтах
        public long duration; //продолжительность в секундах

        public Song(String fileName, String filePath, long fileSize, long duration) {
            this.fileName = fileName;
            this.filePath = filePath;
            this.fileSize = fileSize;
            this.duration = duration;
        }
    }

    public static void main(String[] args) {
        mainArray = new ArrayList<>();
        loadSongs();// Загрузка информации о песнях
        
        System.out.println("Server started.");
        System.out.println("Songs on server:");
        
        for (Song song : mainArray) {
            System.out.println(song.fileName);
        }

        try (DatagramSocket receiveSocket = new DatagramSocket(RECEIVE_PORT);
            DatagramSocket sendSocket = new DatagramSocket()) {

            byte[] buffer = new byte[256];

            // Бесконечный цикл для приема запросов от клиентов
            while (true) {
                DatagramPacket requestPacket = new DatagramPacket(buffer, buffer.length);
                receiveSocket.receive(requestPacket);
                String message = new String(requestPacket.getData(), 0, requestPacket.getLength());
                InetAddress clientAddress = requestPacket.getAddress();

                // Обработка запроса от клиента
                handleRequest(message, clientAddress, sendSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для загрузки информации о песнях
    private static void loadSongs() {
        File directoryPath = new File("Songs");
        File[] filesList = directoryPath.listFiles();
        for (File file : filesList) {
            try {
                AudioFileFormat aff = AudioSystem.getAudioFileFormat(file.getAbsoluteFile());
                Map<String, Object> param = aff.properties();
                long paramTime = (long) param.get("duration");
                long time = (long) Math.round(paramTime / 1000000);

                // Добавление информации о песне в массив
                mainArray.add(new Song(file.getName(), file.getAbsolutePath(), file.length(), time));
            } catch (UnsupportedAudioFileException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Метод для обработки запроса от клиента
    private static void handleRequest(String message, InetAddress clientAddress, DatagramSocket sendSocket) {
        String[] parts = message.split("~");

        switch (parts[0]) {
            case "AddSong":
                sendSongListName(clientAddress, sendSocket);
                break;
            case "PlaySong":
                sendRequestedSong(parts[1], clientAddress, sendSocket);
                break;
            default:
                break;
        }
    }

    // Метод для отправки списка песен клиенту через sendMessage
    private static void sendSongListName(InetAddress clientAddress, DatagramSocket sendSocket) {
        StringBuilder message = new StringBuilder();
        for (Song song : mainArray) {
            message.append(song.fileName).append("~");
        }
        sendMessage(message.toString(), clientAddress, sendSocket);
    }

    // Метод для отправки запрошенной песни клиенту
    private static void sendRequestedSong(String songName, InetAddress clientAddress, DatagramSocket sendSocket) {
        for (Song song : mainArray) {
            if (song.fileName.equals(songName)) {
                try (Socket fileSocket = new Socket(clientAddress, FILE_PORT);
                    DataOutputStream outFile = new DataOutputStream(fileSocket.getOutputStream());
                    FileInputStream fileInputStream = new FileInputStream(song.filePath)) {

                    System.out.println("Sending song: " + song.fileName);
                    // Отправляем размер файла и его продолжительность
                    outFile.writeInt((int) song.fileSize);
                    outFile.writeInt((int) song.duration);
                    // Отправляем содержимое файла
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outFile.write(buffer, 0, bytesRead);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private static void sendMessage(String message, InetAddress clientAddress, DatagramSocket sendSocket) {
        try {
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, SEND_PORT);
            sendSocket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
