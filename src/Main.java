import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Main {

    public static void main(String[] args) {

        FileInputStream in;
        FileOutputStream ou;
        PpmDecoder decoder;
        PpmEncoder encoder;

        int scaleFactor = 2;

        try {
            in = new FileInputStream("image.ppm");
            decoder = new PpmDecoder(in);
            ou = new FileOutputStream("out.ppm");
            encoder = new PpmEncoder(ou, decoder.getWidth() * scaleFactor, decoder.getHeight() * scaleFactor);
        } catch (Exception e) {
            System.out.println("erro ao abrir a imagem :" + e.getMessage());
            return;
        }


        int[] raster = new int[decoder.getWidth() * decoder.getHeight()];

        try {
            decoder.readRow(in, raster, decoder.getHeight());
        } catch (Exception e) {
            System.out.println("Não pode ler uma coluna");
        }

        BufferedImage bufferedImage = new BufferedImage(decoder.getWidth(), decoder.getHeight(), BufferedImage.TYPE_INT_RGB);


        int i = 0;
        for (int y = 0; y < decoder.getHeight(); y++) {
            for (int x = 0; x < decoder.getWidth(); x++) {
                bufferedImage.setRGB(x, y, raster[i]);
                i++;
            }
        }

        BufferedImage resizedImage = resize(bufferedImage, decoder.getWidth() * 2, decoder.getHeight()*2);

        encoder.writeHeader();
        encoder.writeBufferedImage(resizedImage);

        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        System.out.println("memória usada: " + usedMemory);
    }

    private static BufferedImage resize(BufferedImage image, int finalWidth, int finalHeight) {

        return Scalr.resize(
                image,
                Scalr.Method.ULTRA_QUALITY,
                Scalr.Mode.FIT_EXACT,
                finalWidth,
                finalHeight,
                Scalr.OP_ANTIALIAS
        );
    }
}