import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EncodeLSB {
    public static void main(String[] args) {
        String message = "Welcome";
        encode(message);
    }

    public static void encode(String message) {
        String binaryMessage = convertMessageToBinary(message);
        int BMCounter = 0;// this counter used for keeping track of the binaryMessage
        try {
            // Step 1: Load the image
            File imageFile = new File("tree.jpg"); // Replace with your image path
            BufferedImage image = ImageIO.read(imageFile);

            // Step 2: Get image dimensions
            int width = image.getWidth();
            int height = image.getHeight();

            // Step 3: Loop through each pixel
            // the position is the LSB to be changed in the channel (0 == first LSB, 1 ==
            // second LSB, ...)
            int position = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    int pixel = getPixel(image, x, y);

                    int[] updatedPixel = updatePixel(pixel, position, binaryMessage, BMCounter);

                    setPixel(updatedPixel, image, x, y);

                    if (BMCounter == binaryMessage.length()) {
                        return;
                    }
                }
            }
            position++;
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void setPixel(int[] channels, BufferedImage image, int x, int y) {
        int newPixelValue = (channels[0] << 16) | (channels[1] << 8) | channels[2];
        image.setRGB(x, y, newPixelValue);
    }

    public static int[] updatePixel(int pixel, int position, String binaryMessage,
            int counter) {
        int channelCounter = 0;// 0 == red, 1 == green, 2 == blue
        int[] channels = new int[3];
        while (channelCounter < 3) {
            // Step 4: Extract the Red, Green, Blue values from the pixel
            int channelValueExtracted = getChannelValue(channelCounter, pixel);

            System.out.println(Integer.toBinaryString(channelValueExtracted));

            channels[channelCounter] = alterChannelValue(channelValueExtracted, position,
                    binaryMessage.charAt(counter));

            channelCounter++;
            counter++;
            if (counter == binaryMessage.length()) {
                break;
            }
        }
        return channels;
    }

    public static int getPixel(BufferedImage image, int x, int y) {
        return image.getRGB(x, y);
    }

    public static int getChannelValue(int channelNumber, int pixel) {
        switch (channelNumber) {
            case 0:
                return (pixel >> 16) & 0xff;
            case 1:
                return (pixel >> 8) & 0xff;
            case 2:
                return pixel & 0xff;
            default:
                System.out.println("Please enter channel name from the following: red or green or blue");
        }
        return 0;
    }

    public static int alterChannelValue(int channel, int position, char modifyingBit) {
        // if the modifying bit is 0 means i want to set this possition in the channel
        // to 0
        // otherwise i want to set it to 1
        switch (modifyingBit) {
            case '0':
                return channel & ~(1 << position);
            case '1':
                return channel | (1 << position);
            default:
                System.out.println("Please enter eithr '1' or '0' as a modifyingBit");
        }
        return 0;
    }

    public static String convertMessageToBinary(String message) {
        StringBuilder builder = new StringBuilder();
        for (char c : message.toCharArray()) {
            builder.append(Integer.toBinaryString(c - '0'));
        }
        return builder.toString();
    }
}
