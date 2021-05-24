package mx.ucol.httpserver;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
  final Socket socket;

  public ClientHandler(Socket socket) {
    this.socket = socket;
  }

  public void run() {
    PrintWriter output = null;
    BufferedReader input = null;

    try {
      output = new PrintWriter(socket.getOutputStream(), true);
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      String responseHTML = "";
      File responseFile = null;

      String received;
      while ((received = input.readLine()) != null) {
        String requestArray[] = received.split(" ");

        if (requestArray[0].equals("GET")) {
          System.out.println("Resource: " + requestArray[1]);
          if (requestArray[1].equals("/") || requestArray[1].equals("/index.html")) {
            responseFile = new File ("./www/index.html");
          } else if (requestArray[1].equals("/about") || requestArray[1].equals("/about.html")) {
            responseFile = new File("./www/about.html");
          }

          Scanner scanner = new Scanner(responseFile);
          while (scanner.hasNextLine()) {
            responseHTML = responseHTML + scanner.nextLine();
          }
          scanner.close();

          int contentLength = responseHTML.length();

          // This line should not be modified just yet
          output.write("HTTP/1.1 200 OK\r\nContent-Length: " +
                  String.valueOf(contentLength) + "\r\n\r\n" + responseHTML);

          // We already sent the response, break the loop
          break;
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        output.close();
        input.close();
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}