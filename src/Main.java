import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) {

        InputStream in;

        try {
            in = new FileInputStream("image.ppm");
        } catch (Exception e) {
            System.out.println("erro ao abrir a imagem :" + e.getMessage());
            return;
        }

        PpmDecoder decoder = new PpmDecoder(in);

        try {
            decoder.readHeader(in);
        } catch (Exception e) {
            System.out.println("Não pode ler o cabeçalho");
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

        try {
            File outputfile = new File("saved.png");
            ImageIO.write(resizedImage, "png", outputfile);
        } catch (Exception e) {
            System.out.println("Erro ao gerar png: " + e.getMessage());
        }

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