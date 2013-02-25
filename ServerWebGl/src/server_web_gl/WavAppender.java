package server_web_gl;
import java.io.File;
import java.io.SequenceInputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class WavAppender {

	public static void fondiWav(File wavFile1, File wavFile2, File wavFinale) {

	    try {
		    AudioInputStream clip1 = AudioSystem.getAudioInputStream(wavFile1);
		    AudioInputStream clip2 = AudioSystem.getAudioInputStream(wavFile2);

		    AudioInputStream appendedFiles = 
                            new AudioInputStream(
                                new SequenceInputStream(clip1, clip2),     
                                clip1.getFormat(), 
                                clip1.getFrameLength() + clip2.getFrameLength());

		    AudioSystem.write(appendedFiles, 
                            AudioFileFormat.Type.WAVE, 
                            wavFinale);
		    
		    appendedFiles.close();
		    appendedFiles = null;
		    System.gc();
	    } catch (Exception e) {
		    e.printStackTrace();
	    }
    }
}