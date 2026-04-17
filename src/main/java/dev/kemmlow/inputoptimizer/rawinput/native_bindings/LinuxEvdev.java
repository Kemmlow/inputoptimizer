package dev.kemmlow.inputoptimizer.rawinput.native_bindings;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

public interface LinuxEvdev extends Library {
    LinuxEvdev INSTANCE = Native.load("c", LinuxEvdev.class);

    int open(String path, int flags);
    int close(int fd);
    int read(int fd, byte[] buf, int count);
    int ioctl(int fd, long request, byte[] arg);

    int O_RDONLY = 0;
    int O_NONBLOCK = 2048;

    @Structure.FieldOrder({"tv_sec", "tv_usec", "type", "code", "value"})
    class InputEvent extends Structure {
        public long tv_sec;
        public long tv_usec;
        public short type;
        public short code;
        public int value;

        public static final int SIZE = 24;
    }

    short EV_REL = 0x02;
    short EV_KEY = 0x01;
    short REL_X = 0x00;
    short REL_Y = 0x01;
    short REL_WHEEL = 0x08;
    short REL_HWHEEL = 0x06;
    short BTN_LEFT = 0x110;
    short BTN_RIGHT = 0x111;
    short BTN_MIDDLE = 0x112;
    short BTN_SIDE = 0x113;
    short BTN_EXTRA = 0x114;
}