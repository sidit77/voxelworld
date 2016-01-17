package com.github.sidit77.voxelworld.system.input;

import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

public enum Key {

    Space         (0x20),
    Apostrophe    (0x27),
    Comma         (0x2C),
    Minus         (0x2D),
    Period        (0x2E),
    Slash         (0x2F),
    Number0       (0x30),
    Number1       (0x31),
    Number2       (0x32),
    Number3       (0x33),
    Number4       (0x34),
    Number5       (0x35),
    Number6       (0x36),
    Number7       (0x37),
    Number8       (0x38),
    Number9       (0x39),
    Semicolon     (0x3B),
    Equal         (0x3D),
    A             (0x41),
    B             (0x42),
    C             (0x43),
    D             (0x44),
    E             (0x45),
    F             (0x46),
    G             (0x47),
    H             (0x48),
    I             (0x49),
    J             (0x4A),
    K             (0x4B),
    L             (0x4C),
    M             (0x4D),
    N             (0x4E),
    O             (0x4F),
    P             (0x50),
    Q             (0x51),
    R             (0x52),
    S             (0x53),
    T             (0x54),
    U             (0x55),
    V             (0x56),
    W             (0x57),
    X             (0x58),
    Y             (0x59),
    Z             (0x5A),
    LeftBracket   (0x5B),
    Backslash     (0x5C),
    RightBracket  (0x5D),
    GraveAccent   (0x60),
    World1        (0xA1),
    World2        (0xA2),
    Escape        (0x100),
    Enter         (0x101),
    Tab           (0x102),
    Backspace     (0x103),
    Insert        (0x104),
    Delete        (0x105),
    Right         (0x106),
    Left          (0x107),
    Down          (0x108),
    Up            (0x109),
    PageUp        (0x10A),
    PageDown      (0x10B),
    Home          (0x10C),
    End           (0x10D),
    CapsLock      (0x118),
    ScrollLock    (0x119),
    NumLock       (0x11A),
    PrintScreen   (0x11B),
    Pause         (0x11C),
    F1            (0x122),
    F2            (0x123),
    F3            (0x124),
    F4            (0x125),
    F5            (0x126),
    F6            (0x127),
    F7            (0x128),
    F8            (0x129),
    F9            (0x12A),
    F10           (0x12B),
    F11           (0x12C),
    F12           (0x12D),
    F13           (0x12E),
    F14           (0x12F),
    F15           (0x130),
    F16           (0x131),
    F17           (0x132),
    F18           (0x133),
    F19           (0x134),
    F20           (0x135),
    F21           (0x136),
    F22           (0x137),
    F23           (0x138),
    F24           (0x139),
    F25           (0x13A),
    Kp0           (0x140),
    Kp1           (0x141),
    Kp2           (0x142),
    Kp3           (0x143),
    Kp4           (0x144),
    Kp5           (0x145),
    Kp6           (0x146),
    Kp7           (0x147),
    Kp8           (0x148),
    Kp9           (0x149),
    KpDecimal     (0x14A),
    KpDivide      (0x14B),
    KpMultiply    (0x14C),
    KpSubtract    (0x14D),
    KpAdd         (0x14E),
    KpEnter       (0x14F),
    KpEqual       (0x150),
    LeftShift     (0x154),
    LeftControl   (0x155),
    LeftAlt       (0x156),
    LeftSuper     (0x157),
    RightShift    (0x158),
    RightControl  (0x159),
    RightAlt      (0x15A),
    RightSuper    (0x15B),
    Menu          (0x15C);

    private static HashMap<Integer, Key> keys = new HashMap<>();
    static {
        for(Key key : values()){
            keys.put(key.getKeyCode(), key);
        }
    }
    public static Key getKeyFromKeyCode(int keycode){
        return keys.getOrDefault(keycode, null);
    }

    private int id;
    Key(int id){
        this.id = id;
    }
    public int getKeyCode(){
        return id;
    }
    public String getName(){
        return GLFW.glfwGetKeyName(id,0);
    }
}
