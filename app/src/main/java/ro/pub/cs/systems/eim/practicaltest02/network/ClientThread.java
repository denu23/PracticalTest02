package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;

public class ClientThread extends Thread {

    private String address;
    private int port;
    private TextView currentTimeTextView;
    private static int totalClients;
    private int clientNum;

    private Socket socket;

    public ClientThread(
            String address,
            int port,
            TextView currentTimeTextView,
            int sameClient) {
        this.address = address;
        this.port = port;
        this.currentTimeTextView = currentTimeTextView;
        if (sameClient == 0)
            totalClients += 1;

        this.clientNum = totalClients;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!");
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader != null && printWriter != null) {
                printWriter.println(this.clientNum);
                printWriter.flush();
                String currentTimeInformation;
                currentTimeInformation = bufferedReader.readLine();
                if (currentTimeInformation != null) {
                    final String finalizedTimeInformation = currentTimeInformation;
                    currentTimeTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            currentTimeTextView.append(finalizedTimeInformation + "\n");
                        }
                    });
                }
            } else {
                Log.e(Constants.TAG, "[CLIENT THREAD] BufferedReader / PrintWriter are null!");
            }
            socket.close();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

}
