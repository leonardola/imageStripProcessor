import java.io.BufferedReader;

/**
 * Created by leonardoalbuquerque on 22/08/16.
 */
public class PPMImage {

    private int height;
    private int width;
    private String type;
    private String path;
    BufferedReader reader;

    public PPMImage(String path, BufferedReader reader){
        this.path = path;
        this.reader = reader;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
