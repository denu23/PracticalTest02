package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.ClientInformation;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                PrintWriter printWriter = Utilities.getWriter(socket);
                BufferedReader bufferedReader = Utilities.getReader(socket);
                if (bufferedReader != null && printWriter != null) {
                    HashMap<Integer, String> data = serverThread.getData();
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                    int clientNum = Integer.parseInt(bufferedReader.readLine());
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    String currentTime = httpClient.execute(httpGet, responseHandler);
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Client number: " + clientNum);

                    DateFormat format = new SimpleDateFormat("yyyyy-mm-dd'T'hh:mm:ss'Z'");
                    try {
                        if (currentTime != null) {
                            String lastTime = data.get(clientNum);
                            if (lastTime == null) {
                                serverThread.setData(clientNum, currentTime);
                                printWriter.println(currentTime);
                                printWriter.flush();
                            } else {
                                Date lastDate = format.parse(lastTime);
                                Date currentDate = format.parse(currentTime);
                                Log.e(Constants.TAG, "[COMMUNICATION THREAD]!" + lastDate);
                                Log.e(Constants.TAG, "[COMMUNICATION THREAD]!" + currentDate);

                                if (currentDate.getTime() - lastDate.getTime() > 60 * 1000) {
                                    serverThread.setData(clientNum, currentTime);
                                    printWriter.println(currentTime);
                                    printWriter.flush();
                                }
                                else {
                                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Under 60 seconds for request!");
                                }
                            }
                        } else {
                                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
                }
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        } else {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
        }
    }

}
