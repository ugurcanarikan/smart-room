package application;
	
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.RecognizeOptions;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechRecognitionResults;
import com.sun.deploy.net.HttpResponse;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import sun.net.www.http.HttpClient;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//BorderPane root = (BorderPane)FXMLLoader.load(getClass().getClass().getClassLoader().getResource("ui_layout.fxml"));
			BorderPane root = FXMLLoader.load(getClass().getResource("/sample.fxml"));

			Scene scene = new Scene(root,800,600);
			//scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
            launch(args);
            SpeechToText service = new SpeechToText();
            service.setUsernameAndPassword("c26521b4-64ef-4a9a-b632-26c0de1105bf", "EWgpz7Kkkpoz");
            //String filePath = "file:" + new java.io.File("").getAbsolutePath() + "/data/RecordAudio.wav";
			String filePath = new java.io.File("").getAbsolutePath() + "/data/RecordAudio.wav";

			filePath = filePath.replace("\\", "/");
			File audio = new File(filePath);

            //see http://www.ibm.com/smarterplanet/us/en/ibmwatson/developercloud/apis/#!/speech-to-text/recognizeSession
            //for parameter explanation

            Map<String, Object> params = new HashMap<String, Object>();

            RecognizeOptions options = new RecognizeOptions.Builder()
                    .audio(audio)
                    .contentType(HttpMediaType.AUDIO_WAV)
                    .build();

            SpeechRecognitionResults transcript = service.recognize(options).execute();
			System.out.println(transcript.getResults());
            String trs = transcript.toString();
            trs = trs.substring((trs.indexOf("transcript") + 12));
            trs = trs.substring(0, trs.indexOf(","));
            trs= trs.substring(trs.indexOf("\"") + 1, trs.lastIndexOf("\""));

            try {
                String urlParameters = "message=" + trs;
                String request = "https://smartroomx.eu-gb.mybluemix.net/sound";
                URL url = new URL(request);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                connection.setRequestProperty("charset", "utf-8");
                connection.setRequestProperty("Content-Length","" + Integer.toString(urlParameters.getBytes().length));
                connection.setUseCaches(false);
                System.out.println("Connected to the url");
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                System.out.println("Sending message");
                wr.writeBytes(urlParameters);
                System.out.println("Sent");
                int code = connection.getResponseCode();
                System.out.println(code);
                wr.flush();
                wr.close();
                connection.disconnect();

            }
            catch(Exception e){
                System.out.println(e);
                System.out.println("Post exception");
            }
        }
        catch(Exception e){
		    System.out.println(e);
		    System.out.println("Speech to text exception");
        }
	}
}
