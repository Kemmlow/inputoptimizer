package dev.kemmlow.inputoptimizer.rawinput.platform;

public final class VKeyToGlfw {
    private VKeyToGlfw() {}

    public static int convert(short vkey) {
        return switch (vkey & 0xFFFF) {
            case 0x41 -> 65;
            case 0x42 -> 66;
            case 0x43 -> 67;
            case 0x44 -> 68;
            case 0x45 -> 69;
            case 0x46 -> 70;
            case 0x47 -> 71;
            case 0x48 -> 72;
            case 0x49 -> 73;
            case 0x4A -> 74;
            case 0x4B -> 75;
            case 0x4C -> 76;
            case 0x4D -> 77;
            case 0x4E -> 78;
            case 0x4F -> 79;
            case 0x50 -> 80;
            case 0x51 -> 81;
            case 0x52 -> 82;
            case 0x53 -> 83;
            case 0x54 -> 84;
            case 0x55 -> 85;
            case 0x56 -> 86;
            case 0x57 -> 87;
            case 0x58 -> 88;
            case 0x59 -> 89;
            case 0x5A -> 90;
            case 0x30 -> 48;
            case 0x31 -> 49;
            case 0x32 -> 50;
            case 0x33 -> 51;
            case 0x34 -> 52;
            case 0x35 -> 53;
            case 0x36 -> 54;
            case 0x37 -> 55;
            case 0x38 -> 56;
            case 0x39 -> 57;
            case 0x20 -> 32;
            case 0x0D -> 257;
            case 0x1B -> 256;
            case 0x08 -> 259;
            case 0x09 -> 258;
            case 0x10 -> 340;
            case 0x11 -> 341;
            case 0x12 -> 342;
            case 0x14 -> 280;
            case 0x25 -> 263;
            case 0x26 -> 265;
            case 0x27 -> 262;
            case 0x28 -> 264;
            case 0x2D -> 260;
            case 0x2E -> 261;
            case 0x24 -> 268;
            case 0x23 -> 269;
            case 0x21 -> 266;
            case 0x22 -> 267;
            case 0x70 -> 290;
            case 0x71 -> 291;
            case 0x72 -> 292;
            case 0x73 -> 293;
            case 0x74 -> 294;
            case 0x75 -> 295;
            case 0x76 -> 296;
            case 0x77 -> 297;
            case 0x78 -> 298;
            case 0x79 -> 299;
            case 0x7A -> 300;
            case 0x7B -> 301;
            case 0xBB -> 61;
            case 0xBD -> 45;
            case 0xDB -> 91;
            case 0xDD -> 93;
            case 0xDC -> 92;
            case 0xBA -> 59;
            case 0xDE -> 39;
            case 0xBC -> 44;
            case 0xBE -> 46;
            case 0xBF -> 47;
            case 0xC0 -> 96;
            default -> -1;
        };
    }
}