import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

public class PpmEncoder {

    private static final int numberOfSubPixels = 3;
    private FileOutputStream outputFile;
    private int height, width;

    public PpmEncoder(FileOutputStream outputFile, int width, int height) {
        this.outputFile = outputFile;
        this.height = height;
        this.width = width;
    }

    public void writeHeader() {
        String header = "P6 \n" + this.width + " " + this.height + " 255 \n";
        byte[] bytes = header.getBytes(Charset.forName("ASCII"));

        try {
            this.outputFile.write(bytes);
            this.outputFile.flush();
        } catch (Exception e) {
            System.out.println("Não pode escrever o cabeçalho " + e.getMessage());
            System.exit(1);
        }
    }

    public void writeBufferedImage(BufferedImage image) {

        byte[] pixels = new byte[image.getWidth() * image.getHeight() * numberOfSubPixels];

        int i = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int pixel = image.getRGB(x, y);

                byte r = (byte) ((pixel) & 0xFF);
                byte g = (byte) ((pixel >> 8) & 0xFF);
                byte b = (byte) ((pixel >> 16) & 0xFF);

                pixels[i] = g;
                pixels[i + 1] = r;
                pixels[i + 2] = b;
                i += 3;
            }
        }

        try{
            this.outputFile.write(pixels);
        } catch (Exception e) {
            System.out.println("Não pode escrever a imagem");
            System.exit(1);
        }
    }
}