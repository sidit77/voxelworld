package com.github.sidit77.voxelworld.system.input;

import java.util.HashMap;

public enum MouseButton {

    Left       (0x0),
    Right      (0x1),
    Middle     (0x2),
    Button4    (0x3),
    Button5    (0x4),
    Button6    (0x5),
    Button7    (0x6),
    Button8    (0x7);

    private static HashMap<Integer, MouseButton> buttons = new HashMap<>();
    static {
        for(MouseButton button : values()){
            buttons.put(button.getMouseButtonCode(), button);
        }
    }
    public static MouseButton getMouseButtonFromMouseButtonCode(int buttoncode){
        return buttons.getOrDefault(buttoncode, null);
    }

    private int id;
    MouseButton(int id){
        this.id = id;
    }
    public int getMouseButtonCode(){
        return id;
    }

}
