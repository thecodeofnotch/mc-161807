package com.mojang.minecraft.level;

import java.util.Random;

public class PerlinNoiseFilter {

    private static final int FUZZINESS = 16;
    private final int octave;

    /**
     * Perlin noise generator
     *
     * @param octave The strength of the noise
     */
    public PerlinNoiseFilter(int octave) {
        this.octave = octave;
    }

    /**
     * Read random noise values with given amount
     *
     * @param width  Noise width
     * @param height Noise height
     * @return Noise map
     */
    public int[] read(int width, int height) {
        Random random = new Random();

        int[] table = new int[width * height];

        // Generate temporary table array with random values between -128 and +128 and multiplied with the fuzziness
        for (int step = width >> this.octave, y = 0; y < height; y += step) {
            for (int x = 0; x < width; x += step) {
                table[x + y * width] = (random.nextInt(256) - 128) * FUZZINESS;
            }
        }

        // Mutate values in table
        for (int step = width >> this.octave; step > 1; step /= 2) {
            int max = 256 * (step << this.octave);
            int halfStep = step / 2;

            // First mutation
            for (int y = 0; y < height; y += step) {
                for (int x = 0; x < width; x += step) {
                    // Get value at index
                    int value = table[x % width + y % height * width];

                    // Add step to create index for value
                    int stepValueX = table[(x + step) % width + y % height * width];
                    int stepValueY = table[x % width + (y + step) % height * width];
                    int stepValueXY = table[(x + step) % width + (y + step) % height * width];

                    // Combine values for new value
                    int mutatedValue = (value + stepValueY + stepValueX + stepValueXY) / 4 + random.nextInt(max * 2) - max;

                    // Update value in table
                    table[x + halfStep + (y + halfStep) * width] = mutatedValue;
                }
            }

            // Second mutation
            for (int y = 0; y < height; y += step) {
                for (int x = 0; x < width; x += step) {
                    // Get value at index
                    int value = table[x + y * width];

                    // Add step to create index for value
                    int stepValueX = table[(x + step) % width + y * width];
                    int stepValueY = table[x + (y + step) % width * width];

                    // Add step and half step to create index for value
                    int halfStepValueXPos = table[(x + halfStep & width - 1) + (y + halfStep - step & height - 1) * width];
                    int halfStepValueYPos = table[(x + halfStep - step & width - 1) + (y + halfStep & height - 1) * width];

                    // Add half step to create index for value
                    int halfStepValue = table[(x + halfStep) % width + (y + halfStep) % height * width];

                    // Combine values for new value
                    int mutatedValueX = (value + stepValueX + halfStepValue + halfStepValueXPos) / 4 + random.nextInt(max * 2) - max;
                    int mutatedValueY = (value + stepValueY + halfStepValue + halfStepValueYPos) / 4 + random.nextInt(max * 2) - max;

                    // Update values in table
                    table[x + halfStep + y * width] = mutatedValueX;
                    table[x + (y + halfStep) * width] = mutatedValueY;
                }
            }
        }

        // Create result array
        int[] result = new int[width * height];

        // Generate output values
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result[x + y * width] = table[x % width + y % height * width] / 512 + 128;
            }
        }

        return result;
    }

}
