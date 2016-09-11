import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) {

        int numberOfLinesToGoBack = 3 * 2;

        FileInputStream in;

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
        int leftOverStripe = (decoder.getHeight() % 10) * 2;

        BufferedImage originalStripe = new BufferedImage(decoder.getWidth(), stripSize, BufferedImage.TYPE_INT_RGB);
        BufferedImage outputImage = new BufferedImage(decoder.getWidth() * 2, decoder.getHeight() * 2, BufferedImage.TYPE_INT_RGB);

        int[] raster = new int[decoder.getWidth() * stripSize];

        int scalatedStripSize = stripSize * 2;
        int scalatedStripStart = 0;
        int scalatedStripEnd = scalatedStripSize;

        //needs to process the left over pixels too
        for (int i = 0; i <= 10; i++) {

            try {
                decoder.readRow(in, raster, stripSize);
            } catch (Exception e) {
                System.out.println("Não pode ler uma coluna: " + e.getMessage());
                return;
            }

            setPixels(originalStripe, decoder.getWidth(), stripSize, raster);
            BufferedImage resizedStripe = resize(originalStripe, decoder.getWidth() * 2, stripSize * 2);


            if (i == 0) {
                transferPixels(resizedStripe, outputImage, scalatedStripStart, scalatedStripEnd, 0);
            }else if(i < 10){
                transferPixels(resizedStripe, outputImage, scalatedStripStart, scalatedStripEnd - numberOfLinesToGoBack, numberOfLinesToGoBack);
            }

            //volta as linhas da imagem original, para poder ter pixels a mais para processar
            try {
                if(i == 0){
                    decoder.goBackNLines(numberOfLinesToGoBack);
                }else{
                    decoder.goBackNLines(numberOfLinesToGoBack/2);
                }
            } catch (Exception e) {
                System.out.println("Erro ao voltar linhas " + e.getMessage());
                return;
            }

            if (i < 10) {
                scalatedStripStart += scalatedStripSize - numberOfLinesToGoBack;
                scalatedStripEnd += scalatedStripSize - numberOfLinesToGoBack;
                leftOverStripe += numberOfLinesToGoBack;
            } else {
                scalatedStripStart = scalatedStripEnd;
                scalatedStripEnd += leftOverStripe;
                stripSize = leftOverStripe;
            }

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

    private static void transferPixels(BufferedImage input, BufferedImage output, int start, int end, int offset) {
        int inputY = 0;

        if (offset > 0) {
            inputY = offset - 1;//fix for 0 started arrays
        }

        for (int y = start; y < end; y++) {
            for (int x = 0; x < input.getWidth(); x++) {
                output.setRGB(x, y, input.getRGB(x, inputY));
            }
            inputY++;
        }
    }
}