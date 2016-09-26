import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Main {

    public static void main(String[] args) {

        int scaleFactor = 2;
        int numberOfLinesToGoBack = 3 * scaleFactor;

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

        BufferedImage originalStripe = new BufferedImage(decoder.getWidth(), stripSize, BufferedImage.TYPE_INT_RGB);

        int[] raster = new int[decoder.getWidth() * stripSize];

        int scalatedStripSize = stripSize * scaleFactor;
        int scalatedStripStart = 0;
        int scalatedStripEnd = scalatedStripSize;

        FileOutputStream outputFile;
        try {
            outputFile = new FileOutputStream("output.ppm");
        } catch (Exception e) {
            System.out.println("erro ao abrir o arquivo de saida: " + e.getMessage());
            return;
        }

        long startTime = System.nanoTime();

        PpmEncoder encoder = new PpmEncoder(outputFile, decoder.getWidth() * scaleFactor, decoder.getHeight() * scaleFactor);
        encoder.writeHeader();

        //imagem com apenas os pixels bons
        BufferedImage goodPixels = new BufferedImage(decoder.getWidth() * scaleFactor, (stripSize * scaleFactor) - numberOfLinesToGoBack, BufferedImage.TYPE_INT_RGB);
        int totalUsedMemory = 0;

        for (int i = 0; i <= 10; i++) {

            try {
                decoder.readRow(in, raster, stripSize);
            } catch (Exception e) {
                System.out.println("Não pode ler uma coluna: " + e.getMessage());
                return;
            }

            setPixels(originalStripe, decoder.getWidth(), stripSize, raster);
            BufferedImage resizedStripe = resize(originalStripe, decoder.getWidth() * scaleFactor, stripSize * scaleFactor);

            if (i == 0) {
                //transferPixels(resizedStripe, outputImage, scalatedStripStart, scalatedStripEnd, 0);


                transferGoodPixels(resizedStripe, goodPixels, 0);
            } else if (i < 10) {
                //transferPixels(resizedStripe, outputImage, scalatedStripStart, scalatedStripEnd - numberOfLinesToGoBack, numberOfLinesToGoBack);

                goodPixels = new BufferedImage(decoder.getWidth() * scaleFactor, scalatedStripEnd - numberOfLinesToGoBack - scalatedStripStart, BufferedImage.TYPE_INT_RGB);
                transferGoodPixels(resizedStripe, goodPixels, numberOfLinesToGoBack);
            } else {
                //transferPixels(resizedStripe, outputImage, scalatedStripStart, scalatedStripEnd, numberOfLinesToGoBack);

                goodPixels = new BufferedImage(decoder.getWidth() * scaleFactor, scalatedStripEnd - scalatedStripStart, BufferedImage.TYPE_INT_RGB);
                transferGoodPixels(resizedStripe, goodPixels, numberOfLinesToGoBack);
            }

            //volta as linhas da imagem original, para poder ter pixels a mais para processar
            try {
                if (i == 0) {
                    decoder.goBackNLines(numberOfLinesToGoBack);
                } else {
                    decoder.goBackNLines(numberOfLinesToGoBack / scaleFactor);
                }
            } catch (Exception e) {
                System.out.println("Erro ao voltar linhas " + e.getMessage());
                return;
            }

            if (i < 9) {
                scalatedStripStart += scalatedStripSize - numberOfLinesToGoBack;
                scalatedStripEnd += scalatedStripSize - numberOfLinesToGoBack;
            } else {
                //+ the 6 lines taken unecessarily on the last loop
                stripSize = decoder.getHeight() - scalatedStripEnd / scaleFactor + numberOfLinesToGoBack;
                scalatedStripStart = scalatedStripEnd - numberOfLinesToGoBack;
                scalatedStripEnd = decoder.getHeight() * scaleFactor;

                raster = new int[decoder.getWidth() * stripSize * scaleFactor];
                originalStripe = new BufferedImage(decoder.getWidth(), stripSize, BufferedImage.TYPE_INT_RGB);
            }

            long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            totalUsedMemory += usedMemory;
            System.out.println("memória usada: " + usedMemory);

            encoder.writeBufferedImage(goodPixels);
        }

        long estimatedTime = System.nanoTime() - startTime;

        System.out.println("Média de uso de memória: " + totalUsedMemory/11);
        System.out.println("Tempo em nano segundos: " + estimatedTime);

        /*try {
            File outputfile = new File("saved.png");
            ImageIO.write(outputImage, "png", outputfile);
        } catch (Exception e) {
            System.out.println("Erro ao gerar png: " + e.getMessage());
        }*/
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

    private static void transferGoodPixels(BufferedImage input, BufferedImage output, int offset) {

        if (offset > 0) {
            offset--; //fix for 0 started arrays
        }

        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                output.setRGB(x, y, input.getRGB(x, y + offset));
            }
        }

    }
}