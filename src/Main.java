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

        PpmDecoder decoder;

        try {
            decoder = new PpmDecoder(in);
        } catch (Exception e) {
            System.out.println("Não pode ler o cabeçalho: " + e.getMessage());
            return;
        }


        int stripSize = decoder.getHeight() / 10;
        //int module = decoder.getHeight() % 10;

        BufferedImage originalStripe = new BufferedImage(decoder.getWidth(), stripSize, BufferedImage.TYPE_INT_RGB);
        BufferedImage outputImage = new BufferedImage(decoder.getWidth(), decoder.getHeight(), BufferedImage.TYPE_INT_RGB);

        int[] raster = new int[decoder.getWidth() * stripSize];

        int scalatedStripSize = stripSize * 2;
        int scalatedStripStart = 0;
        int scalatedStripEnd = stripSize;

        for (int i = 0; i < 1; i++) {

            try {
                decoder.readRow(in, raster, stripSize);
            } catch (Exception e) {
                System.out.println("Não pode ler uma coluna: " + e.getMessage());
                return;
            }

            setPixels(originalStripe, decoder.getWidth(), stripSize, raster);
            BufferedImage resizedStripe = resize(originalStripe, decoder.getWidth() * 2, stripSize * 2);

            transferPixels(resizedStripe, outputImage, scalatedStripStart, scalatedStripEnd);

            scalatedStripStart = scalatedStripEnd;
            scalatedStripEnd += scalatedStripSize;
        }

        try {
            File outputfile = new File("saved.png");
            ImageIO.write(outputImage, "png", outputfile);
        } catch (Exception e) {
            System.out.println("Erro ao gerar png: " + e.getMessage());
        }
    }

    private static void setPixels(BufferedImage bufferedImage, int imageWidth, int stripSize, int[] raster) {
        int i = 0;
        for (int y = 0; y < stripSize; y++) {
            for (int x = 0; x < imageWidth; x++) {
                bufferedImage.setRGB(x, y, raster[i]);
                i++;
            }
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

    private static void transferPixels(BufferedImage input, BufferedImage output, int start, int end) {

        int inputY = 0;
        for (int y = start; y < end; y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                try{
                    output.setRGB(x, y, input.getRGB(x, inputY));
                }catch (Exception e){
                    System.out.println("ASDFADFA");
                }
            }
            inputY++;
        }
    }
}