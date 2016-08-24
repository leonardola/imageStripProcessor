/*import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;*/

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
        for (int x = 0; x < decoder.getWidth(); x++) {
            for (int y = 0; y < decoder.getHeight(); y++) {
                bufferedImage.setRGB(x, y, raster[i]);
                i++;
            }
        }

        BufferedImage resizedImage = resizeToPrint(bufferedImage, decoder.getWidth() * 2, decoder.getHeight()*2);

        try {
            File outputfile = new File("saved.png");
            ImageIO.write(resizedImage, "png", outputfile);
        } catch (Exception e) {
            System.out.println("Erro ao gerar png: " + e.getMessage());
        }

    }

    private static BufferedImage resizeToPrint(BufferedImage image, int finalWidth, int finalHeight) {

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



/*    public static void main(String[] args) {
        String type = "";
        String path = "image.ppm";

        PPMImage ppmImage;

        int[] raster;
        try {

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(path),
                            Charset.forName("UTF-8")));

            ppmImage = new PPMImage(path, reader);

            //p6
            type += (char) reader.read();
            type += (char) reader.read();

            ppmImage.setType(type);

            String buff = "";
            char singleChar;
            boolean stop = false;
            String[] data = new String[4];
            int i = 0;

            reader.read();//white space
            do {

                singleChar = (char) reader.read();
                if (singleChar != ' ' && singleChar != '\n') {
                    buff += singleChar;
                } else {
                    data[i] = buff;
                    System.out.println(buff);
                    buff = "";
                    i++;
                    if (i >= 3) {
                        stop = true;
                    }
                }

            } while (!stop);

            if(Integer.parseInt(data[2]) > 255){
                System.out.println("RGB Invalido");
                return;
            }

            ppmImage.setWidth(Integer.parseInt(data[0]));
            ppmImage.setHeight(Integer.parseInt(data[1]));

            raster = new int[ppmImage.getWidth() * ppmImage.getHeight() * 3];

            int c;
            i = 0;
            while ((c = reader.read()) != -1) {
                raster[i] = c;
                char character = (char) c;
                System.out.println(character);
                i++;
                // Do something with your character
            }
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
            return;
        }


        BufferedImage bufferedImage = new BufferedImage(ppmImage.getWidth(), ppmImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        bufferedImage.setRGB(0, 0, ppmImage.getWidth(), ppmImage.getHeight(), raster, 0,0);

        try{
            File outputfile = new File("saved.png");
            ImageIO.write(bufferedImage, "ppm", outputfile);
        }catch (Exception e){
            System.out.println("Erro ao gerar png: "+ e.getMessage());
        }

//        try{
//            RandomAccessFile f = new RandomAccessFile("image.ppm", "r");
//            byte[] b = new byte[(int)f.length()];
//            f.readFully(b);
//            System.out.println("asdf");
//        }catch (Exception e){
//            System.out.println("Error "+ e.getMessage());
//        }

    }
}*/
