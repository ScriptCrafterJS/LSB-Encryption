import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class EncodeLSB {
    private static String message;
    private static String binaryMessage;
    private static StringBuilder decodedBinaryMessage;

    public static void main(String[] args) {
        message = "Hello World";
        binaryMessage = convertMessageToBinary(message);
        System.out.println("Start Encoding...");
        encode();
        System.out.println("Message successfully encoded");
        System.out.println("Start Decoding...");
        String decodedBinaryMessage = decode();
        System.out.println("Decoded Message: " + decodedBinaryMessage);
    }

    public static String decode() {
        int binaryMessageCounter = 0;
        decodedBinaryMessage = new StringBuilder();
        int LSBposition = 0; // 0 endicates the most LSB and 1 the second most LSB and so on...
        try {
            File imageFile = new File("encoded_tree.png");
            BufferedImage image = ImageIO.read(imageFile);

            int width = image.getWidth();
            int height = image.getHeight();
            outerloop: while (binaryMessageCounter < binaryMessage.length()) {
                for (int y = 0; y < height; y += 2) {
                    for (int x = 0; x < width; x += 2) {

                        if (y < height && x < width) {
                            int pixel = getPixel(image, x, y);
                            int channelCounter = 0;
                            // traverse the channels to extract the bits from the pixel
                            while (channelCounter < 3) {
                                extractBitFromPixelToMessage(channelCounter, pixel, LSBposition);
                                channelCounter++;
                                binaryMessageCounter++;
                                // if we here reached the end of the binary message there's no need to continue
                                if (binaryMessageCounter == binaryMessage.length()) {
                                    break outerloop;
                                }
                            }
                        }
                        if (y + 1 < height && x + 1 < width) {
                            int pixel = getPixel(image, x + 1, y + 1);
                            int channelCounter = 0;
                            // traverse the channels to extract the bits from the pixel
                            while (channelCounter < 3) {
                                extractBitFromPixelToMessage(channelCounter, pixel, LSBposition);

                                channelCounter++;
                                binaryMessageCounter++;
                                // if we here reached the end of the binary message there's no need to continue
                                if (binaryMessageCounter == binaryMessage.length()) {
                                    break outerloop;
                                }
                            }
                        }
                        if (y < height && x + 1 < width) {
                            int pixel = getPixel(image, x + 1, y);
                            int channelCounter = 0;
                            // traverse the channels to extract the bits from the pixel
                            while (channelCounter < 3) {
                                extractBitFromPixelToMessage(channelCounter, pixel, LSBposition);

                                channelCounter++;
                                binaryMessageCounter++;
                                // if we here reached the end of the binary message there's no need to continue
                                if (binaryMessageCounter == binaryMessage.length()) {
                                    break outerloop;
                                }
                            }
                        }
                        if (y + 1 < height && x < width) {
                            int pixel = getPixel(image, x, y + 1);
                            int channelCounter = 0;
                            // traverse the channels to extract the bits from the pixel
                            while (channelCounter < 3) {
                                extractBitFromPixelToMessage(channelCounter, pixel, LSBposition);

                                channelCounter++;
                                binaryMessageCounter++;
                                // if we here reached the end of the binary message there's no need to continue
                                if (binaryMessageCounter == binaryMessage.length()) {
                                    break outerloop;
                                }
                            }
                        }
                    }
                }
                LSBposition++;
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return convertBinaryToMessage(decodedBinaryMessage.toString());
    }

    public static void extractBitFromPixelToMessage(int channelCounter, int pixel, int LSBposition) {
        int channelValue = getChannelValue(channelCounter, pixel);
        System.out.println(Integer.toBinaryString(channelValue));
        byte bit = (byte) extractBit(channelValue, LSBposition);
        decodedBinaryMessage.append(bit);
    }

    public static int extractBit(int channelValueExtracted, int LSBposition) {
        return (channelValueExtracted >> LSBposition) & 1;
    }

    public static void encode() {
        System.out.println("Binary message: " + binaryMessage);
        int binaryMessageCounter = 0; // this counter used for keeping track of the binaryMessage
        int LSBposition = 0; // 0 endicates the most LSB and 1 the second most LSB and so on...

        try {
            File imageFile = new File("50x33 tree.png");
            BufferedImage image = ImageIO.read(imageFile);

            int width = image.getWidth();
            int height = image.getHeight();

            outerloop: while (binaryMessageCounter < binaryMessage.length()) {
                for (int y = 0; y < height; y += 2) {
                    for (int x = 0; x < width; x += 2) {

                        int channelCounter = 0; // 0 == red, 1 == green, 2 == blue
                        int[] channels = new int[3];

                        // here we check for the main diagonal
                        if (y < height && x < width) {
                            int pixel = getPixel(image, x, y);
                            System.out.println("Channels Extracted");
                            while (channelCounter < 3) {
                                channels = extractColorComponents(channelCounter, pixel, LSBposition,
                                        binaryMessageCounter, channels);
                                // if we here reached the end of the binary message theres no need to continue
                                // to other color channels
                                if (binaryMessageCounter == binaryMessage.length()) {
                                    setPixel(channels, image, x, y);
                                    break outerloop;
                                }
                                channelCounter++; // to traverse the 3 channels
                                binaryMessageCounter++; // to traverse each bit in the binary message
                                if (binaryMessageCounter == binaryMessage.length()) {
                                    break outerloop;
                                }
                            }
                            setPixel(channels, image, x, y);
                            displayChannels(x, y, channels);
                        }
                        channelCounter = 0; // to reset for the next pixel
                        channels = new int[3];
                        if (y + 1 < height && x + 1 < width) {
                            int pixel = getPixel(image, x + 1, y + 1);
                            System.out.println("Channels Extracted");
                            while (channelCounter < 3) {
                                channels = extractColorComponents(channelCounter, pixel, LSBposition,
                                        binaryMessageCounter, channels);

                                if (binaryMessageCounter == binaryMessage.length()) {
                                    setPixel(channels, image, x + 1, y + 1);
                                    break outerloop;
                                }
                                channelCounter++;
                                binaryMessageCounter++;
                                if (binaryMessageCounter == binaryMessage.length()) {
                                    break outerloop;
                                }
                            }
                            setPixel(channels, image, x + 1, y + 1);
                            displayChannels(x + 1, y + 1, channels);
                        }
                        channelCounter = 0; // to reset for the next pixel
                        channels = new int[3];
                        // here we check for the second diagonal
                        if (y < height && x + 1 < width) {
                            int pixel = getPixel(image, x + 1, y);
                            System.out.println("Channels Extracted");
                            while (channelCounter < 3) {
                                channels = extractColorComponents(channelCounter, pixel, LSBposition,
                                        binaryMessageCounter, channels);

                                if (binaryMessageCounter == binaryMessage.length()) {
                                    setPixel(channels, image, x + 1, y);
                                    break outerloop;
                                }
                                channelCounter++;
                                binaryMessageCounter++;
                                if (binaryMessageCounter == binaryMessage.length()) {
                                    break outerloop;
                                }
                            }
                            setPixel(channels, image, x + 1, y);
                            displayChannels(x + 1, y, channels);
                        }
                        channelCounter = 0; // to reset for the next pixel
                        channels = new int[3];
                        if (y + 1 < height && x < width) {
                            int pixel = getPixel(image, x, y + 1);
                            System.out.println("Channels Extracted");
                            while (channelCounter < 3) {
                                channels = extractColorComponents(channelCounter, pixel, LSBposition,
                                        binaryMessageCounter, channels);

                                if (binaryMessageCounter == binaryMessage.length()) {
                                    setPixel(channels, image, x, y + 1);
                                    break outerloop;
                                }
                                channelCounter++;
                                binaryMessageCounter++;
                                if (binaryMessageCounter == binaryMessage.length()) {
                                    break outerloop;
                                }
                            }
                            setPixel(channels, image, x, y + 1);
                            displayChannels(x, y + 1, channels);
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

    public static int[] extractColorComponents(int channelCounter, int pixel, int LSBposition, int binaryMessageCounter,
            int[] channels) {
        // Extract the 3 color components (Red, Green, Blue) from the pixel
        int channelValueExtracted = getChannelValue(channelCounter, pixel);

        System.out.println(Integer.toBinaryString(channelValueExtracted));

        channels[channelCounter] = alterChannelValue(channelValueExtracted, LSBposition,
                binaryMessage.charAt(binaryMessageCounter));
        return channels;
    }

    public static void displayChannels(int x, int y, int[] channels) {
        System.out.println(
                "x: " + x + " y: " + y + ", modified channels: "
                        + Integer.toBinaryString(channels[0])
                        + " "
                        + Integer.toBinaryString(channels[1]) + " "
                        + Integer.toBinaryString(channels[2]));
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
            builder.append(String.format("%8s", Integer.toBinaryString(c)).replaceAll(" ", "0"));
        }
        return builder.toString();
    }

    public static String convertBinaryToMessage(String binaryMessage) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < binaryMessage.length(); i += 8) {
            String byteString = binaryMessage.substring(i, i + 8);
            char c = (char) Integer.parseInt(byteString, 2);
            builder.append(c);
        }
        return builder.toString();
    }

}
