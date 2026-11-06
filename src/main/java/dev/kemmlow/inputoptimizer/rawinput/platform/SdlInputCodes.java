package dev.kemmlow.inputoptimizer.rawinput.platform;

/**
 * Minecraft 26.3 replaced GLFW with SDL3, so key binds and mouse buttons now use SDL
 * scancodes and SDL button numbers instead of the old GLFW constants.
 *
 * The raw input engines keep reporting the GLFW style values they have always used, and
 * everything is translated here right before it is handed to the vanilla input pipeline.
 */
public final class SdlInputCodes {
    private SdlInputCodes() {}

    /**
     * @return the SDL scancode for a GLFW key code, or {@code -1} if the key has no SDL equivalent.
     */
    public static int scancodeFromGlfw(int glfwKey) {
        return switch (glfwKey) {
            case 65 -> 4;
            case 66 -> 5;
            case 67 -> 6;
            case 68 -> 7;
            case 69 -> 8;
            case 70 -> 9;
            case 71 -> 10;
            case 72 -> 11;
            case 73 -> 12;
            case 74 -> 13;
            case 75 -> 14;
            case 76 -> 15;
            case 77 -> 16;
            case 78 -> 17;
            case 79 -> 18;
            case 80 -> 19;
            case 81 -> 20;
            case 82 -> 21;
            case 83 -> 22;
            case 84 -> 23;
            case 85 -> 24;
            case 86 -> 25;
            case 87 -> 26;
            case 88 -> 27;
            case 89 -> 28;
            case 90 -> 29;
            case 49 -> 30;
            case 50 -> 31;
            case 51 -> 32;
            case 52 -> 33;
            case 53 -> 34;
            case 54 -> 35;
            case 55 -> 36;
            case 56 -> 37;
            case 57 -> 38;
            case 48 -> 39;
            case 257 -> 40;
            case 256 -> 41;
            case 259 -> 42;
            case 258 -> 43;
            case 32 -> 44;
            case 45 -> 45;
            case 61 -> 46;
            case 91 -> 47;
            case 93 -> 48;
            case 92 -> 49;
            case 59 -> 51;
            case 39 -> 52;
            case 96 -> 53;
            case 44 -> 54;
            case 46 -> 55;
            case 47 -> 56;
            case 280 -> 57;
            case 290 -> 58;
            case 291 -> 59;
            case 292 -> 60;
            case 293 -> 61;
            case 294 -> 62;
            case 295 -> 63;
            case 296 -> 64;
            case 297 -> 65;
            case 298 -> 66;
            case 299 -> 67;
            case 300 -> 68;
            case 301 -> 69;
            case 260 -> 73;
            case 268 -> 74;
            case 266 -> 75;
            case 261 -> 76;
            case 269 -> 77;
            case 267 -> 78;
            case 262 -> 79;
            case 263 -> 80;
            case 264 -> 81;
            case 265 -> 82;
            case 341 -> 224;
            case 340 -> 225;
            case 342 -> 226;
            case 343 -> 227;
            case 345 -> 228;
            case 344 -> 229;
            case 346 -> 230;
            case 347 -> 231;
            default -> -1;
        };
    }

    /**
     * @return the SDL button number for a GLFW mouse button.
     */
    public static int mouseButtonFromGlfw(int glfwButton) {
        return switch (glfwButton) {
            case 0 -> 1;
            case 1 -> 3;
            case 2 -> 2;
            default -> glfwButton >= 3 ? glfwButton + 1 : glfwButton;
        };
    }
}
