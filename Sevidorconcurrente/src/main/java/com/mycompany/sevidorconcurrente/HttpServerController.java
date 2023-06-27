package com.mycompany.sevidorconcurrente;

import java.net.Socket;
import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HttpServerController implements Runnable {
    private Socket clientSocket;
    private String path;
    private PrintWriter out;
    private String fileType;
    private String outputLine;
    private String file;
    private List<String> tipoTexto;
    private List<String> tipoImg;

    public HttpServerController(Socket clientSocket, String path) {
        this.clientSocket = clientSocket;
        this.path = path;
        fillingLists();
    }

    private void fillingLists() {
        tipoTexto = new ArrayList<String>();
        tipoImg = new ArrayList<String>();
        tipoTexto.add(".html");
        tipoTexto.add(".css");
        tipoTexto.add(".js");
        tipoImg.add(".jpg");
        tipoImg.add(".png");
    }

    @Override
    public void run() {
        try {
            writingFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writingFile() throws IOException {
        file = path;
        boolean flag = true;
        StringBuffer stringBuffer = new StringBuffer();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        for (String l : tipoTexto) {
            if (file.contains(l)) {
                fileType = file.split("\\.")[1];
                try {
                    creatingText();
                } catch (Exception e) {
                    stringBuffer = error404(stringBuffer);
                    System.out.println(e);
                }
                out.println();
                out.println(stringBuffer.toString());
            }
        }
        for (String i : tipoImg) {
            if (file.contains(i)) {
                fileType = file.split("\\.")[1];
                try{
                    creatingImg();
                }catch (Exception e){
                    stringBuffer = error404(stringBuffer);
                    System.out.println(e);
                }
            }
        }
    }

    private void creatingText() throws IOException {
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        outputLine = "HTTP/1.1 200 OK\r\n";
        outputLine += "Content-Type: text/" + fileType + "\r\n";
        outputLine += "\r\n";
        outputLine += new String(Files.readAllBytes(Paths.get("Sevidorconcurrente/resources" + file)), StandardCharsets.UTF_8);
        out.println(outputLine);
        out.close();
        clientSocket.close();
    }

    private void creatingImg() {
        try {
            DataOutputStream binary = new DataOutputStream(clientSocket.getOutputStream());
            File image = new File("Sevidorconcurrente/resources" + file);
            FileInputStream filein = new FileInputStream(image);
            byte[] imageData = new byte[(int) image.length()];
            filein.read(imageData);
            filein.close();
            binary.writeBytes("HTTP/1.0 200 OK\r\n");
            binary.writeBytes("Content-Type: image/" + fileType + "\r\n");
            binary.writeBytes("Content-Length: " + imageData.length);
            binary.writeBytes("\r\n\r\n");
            binary.write(imageData);
            binary.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public StringBuffer error404(StringBuffer stringBuffer) {

        outputLine = "HTTP/1.1 404 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<title>Title of the document</title>\n" + "</head>"
                + "<body>"
                + "ERROR 404"
                + "</body>"
                + "</html>";
        stringBuffer = new StringBuffer();
        stringBuffer.append(outputLine);
        return stringBuffer;
    }

}