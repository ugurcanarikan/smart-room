package application;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.*;

//import javax.sound.*;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.omg.CORBA.portable.OutputStream;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SampleController {

	public void recordVoice2() throws UnsupportedAudioFileException, IOException {

		AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
		TargetDataLine microphone;
		AudioInputStream audioInputStream;
		SourceDataLine sourceDataLine;
		try {
			microphone = AudioSystem.getTargetDataLine(format);

			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			microphone = (TargetDataLine) AudioSystem.getLine(info);
			microphone.open(format);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int numBytesRead;
			int CHUNK_SIZE = 1024;
			byte[] data = new byte[microphone.getBufferSize() / 5];
			microphone.start();

			int bytesRead = 0;

			try {
				while (bytesRead < 100000) { // Just so I can test if recording
												// my mic works...
					numBytesRead = microphone.read(data, 0, CHUNK_SIZE);
					bytesRead = bytesRead + numBytesRead;
					System.out.println(bytesRead);
					out.write(data, 0, numBytesRead);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			byte audioData[] = out.toByteArray();
			// Get an input stream on the byte array
			// containing the data
			InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
			audioInputStream = new AudioInputStream(byteArrayInputStream, format, audioData.length / format.getFrameSize());
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(format);
			sourceDataLine.start();
			int cnt = 0;
			byte tempBuffer[] = new byte[10000];
			
			
			
			try {
				while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
					if (cnt > 0) {
						// Write data to the internal buffer of
						// the data line where it will be
						// delivered to the speaker.
						sourceDataLine.write(tempBuffer, 0, cnt);
					} // end if
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			InputStream bais = new ByteArrayInputStream(tempBuffer);
			AudioInputStream ais = AudioSystem.getAudioInputStream(bais);
			FileOutputStream os = new FileOutputStream(new File("sound.mp3"));
			int k = AudioSystem.write(ais, AudioFileFormat.Type.WAVE, os);
			
			
			// Block and wait for internal buffer of the
			// data line to empty.
			sourceDataLine.drain();
			sourceDataLine.close();
			microphone.close();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	
	public void recordVoice() {
		final SoundRecorder recorder = new SoundRecorder();

		// creates a new thread that waits for a specified
		// of time before stopping
		Thread stopper = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(4000);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				recorder.finish();
			}
		});

		stopper.start();

		// start recording
		recorder.start();
	}
	
	public void listenVoice() {
		{
			final MediaPlayer musicplayer;
			String filePath = "file:///" + new java.io.File("").getAbsolutePath() + "/data/RecordAudio.wav";
			filePath = filePath.replace("\\", "/");
			Media mp3MusicFile = new Media(filePath);
			musicplayer = new MediaPlayer(mp3MusicFile);
			musicplayer.setAutoPlay(true);
			musicplayer.setVolume(0.5);
			musicplayer.setOnEndOfMedia(new Runnable() {
				public void run() {
					musicplayer.seek(Duration.ZERO);
				}
			});
		}
	}

}
