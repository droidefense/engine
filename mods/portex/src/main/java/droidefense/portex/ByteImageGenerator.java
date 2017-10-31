package droidefense.portex;

import com.github.katjahahn.tools.visualizer.Visualizer;
import com.github.katjahahn.tools.visualizer.VisualizerBuilder;

import java.io.File;
import java.io.IOException;

public class ByteImageGenerator {

    private static final Visualizer visualizer = new VisualizerBuilder().build();

    private final byte[] data;
    private final File f;
    private final String imageOutputPath;

    public ByteImageGenerator(File f, String imageOutputPath) {
        this.f = f;
        this.imageOutputPath = imageOutputPath;
        data = null;
    }

    public ByteImageGenerator(byte[] data, String imageOutputPath) {
        this.data = data;
        this.imageOutputPath = imageOutputPath;
        this.f = null;
    }

    public void generate() throws IOException {
        // load the PE file data
        if(this.f!=null && this.f.exists() && this.f.canRead()){
            if(imageOutputPath!=null){
                //generate image from file f
                visualizer.writeImage(this.f, new File(imageOutputPath));
            }
        }
    }
}
