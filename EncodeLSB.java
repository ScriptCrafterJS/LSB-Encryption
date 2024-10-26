import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EncodeLSB {
    public static void main(String[] args) {
        String message = "Hi";
        System.out.println("Start Encoding...");
        encode(message);
        System.out.println("Message successfully encoded");
        System.out.println("Start Decoding...");
        String decodedMessage = decode(message);
        System.out.println("Decoded Message: " + decodedMessage);
    }

    public static String decode(String message) {
        String binaryMessage = convertMessageToBinary(message);
        int binaryMessageCounter = 0;
        StringBuilder decodedMessage = new StringBuilder();
        int LSBposition = 0; // 0 endicates the most LSB and 1 the second most LSB and so on...
        try {
            File imageFile = new File("encoded_tree.png");
            BufferedImage image = ImageIO.read(imageFile);

            int width = image.getWidth();
            int height = image.getHeight();
            outerloop: while (binaryMessageCounter < binaryMessage.length()) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = getPixel(image, x, y);
                        int channelCounter = 0;
                        // traverse the channels to extract the bits from the pixel
                        while (channelCounter < 3) {
                            int channelValue = getChannelValue(channelCounter, pixel);
                            System.out.println(Integer.toBinaryString(channelValue));
                            byte bit = (byte) extractBit(channelValue, LSBposition);
                            decodedMessage.append(bit);

                            channelCounter++;
                            binaryMessageCounter++;
                            // System.out.println(binaryMessageCounter + " " + binaryMessage.length());

                            // if we here reached the end of the binary message there's no need to continue
                            if (binaryMessageCounter == binaryMessage.length()) {
                                break outerloop;
                            }
                        }
                    }
                }
                LSBposition++;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return decodedMessage.toString();
    }

    public static int extractBit(int channelValueExtracted, int LSBposition) {
        return (channelValueExtracted >> LSBposition) & 1;
    }

    public static void encode(String message) {
        String binaryMessage = convertMessageToBinary(message);
        System.out.println("Binary message: " + binaryMessage);
        int binaryMessageCounter = 0; // this counter used for keeping track of the binaryMessage
        int LSBposition = 0; // 0 endicates the most LSB and 1 the second most LSB and so on...

        try {
            File imageFile = new File("50x33 tree.png");
            BufferedImage image = ImageIO.read(imageFile);

            int width = image.getWidth();
            int height = image.getHeight();

            outerloop: while (binaryMessageCounter < binaryMessage.length()) {
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = getPixel(image, x, y);

                        int channelCounter = 0; // 0 == red, 1 == green, 2 == blue
                        int[] channels = new int[3];
                        System.out.println("Channels Extracted");
                        while (channelCounter < 3) {
                            // Extract the 3 color components (Red, Green, Blue) from the pixel
                            int channelValueExtracted = getChannelValue(channelCounter, pixel);

                            System.out.println(Integer.toBinaryString(channelValueExtracted));

                            channels[channelCounter] = alterChannelValue(channelValueExtracted, LSBposition,
                                    binaryMessage.charAt(binaryMessageCounter));

                            channelCounter++; // to traverse the 3 channels
                            binaryMessageCounter++; // to traverse each bit in the binary message
                            // if we here reached the end of the binary message theres no need to continue
                            if (binaryMessageCounter == binaryMessage.length()) {
                                setPixel(channels, image, x, y);

                                break outerloop;
                            }
                        }

                        System.out.println(
                                "x: " + x + " y: " + y + ", modified channels: " + Integer.toBinaryString(channels[0])
                                        + " "
                                        + Integer.toBinaryString(channels[1]) + " "
                                        + Integer.toBinaryString(channels[2]));
                        setPixel(channels, image, x, y);

                        if (binaryMessageCounter == binaryMessage.length()) {
                            System.out.println("Message successfully encoded");
                            break outerloop;
                        }
                    }
                }
                LSBposition++;
                // if the current bit being altered is closer to the left side of the byte then
                // we alter the user that it may cause a loss of quality
                if (LSBposition > 7) {
                    System.out.println(
                            "The message you are trying to hide is taking too much space in the image and that may result in a loss of quality");
                    break;
                }
            }
            // output the new image (stego file)
            ImageIO.write(image, "png", new File("encoded_tree.png"));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void setPixel(int[] channels, BufferedImage image, int x, int y) {
        int newPixelValue = (channels[0] << 16) | (channels[1] << 8) | channels[2];
        image.setRGB(x, y, newPixelValue);
    }

    public static int getPixel(BufferedImage image, int x, int y) {
        return image.getRGB(x, y);
    }

    public static int getChannelValue(int channelNumber, int pixel) {
        // 0 == red, 1 == green, 2 == blue
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

    public static int alterChannelValue(int channel, int LSBposition, char modifyingBit) {
        // if the modifying bit is 0 means i want to set this possition in the channel
        // to 0
        // otherwise i want to set it to 1
        switch (modifyingBit) {
            case '0':
                return channel & ~(1 << LSBposition);
            case '1':
                return channel | (1 << LSBposition);
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
