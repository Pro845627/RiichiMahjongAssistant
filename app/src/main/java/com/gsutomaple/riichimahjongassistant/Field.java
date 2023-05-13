package com.gsutomaple.riichimahjongassistant;

import com.example.riichimahjongassistant.R;

public enum Field {
    East(1), South(2), West(3), North(4);
    private final int number;
    private Field(int number){
        this.number = number;
    }
    public int colorId(){
        switch (number){
            case 1: return R.color.colorEast;
            case 2: return R.color.colorSouth;
            case 3: return R.color.colorWest;
            case 4: return R.color.colorNorth;
        }
        return R.color.black;
    }
    public int wordId(){
        switch (number){
            case 1: return R.string.east;
            case 2: return R.string.south;
            case 3: return R.string.west;
            case 4: return R.string.north;
        }
        return R.string.north;
    }
    public Field Next(){
        return toField(number == 4 ? 1 : number + 1);
    }
    public int getNumber(){
        return number;
    }
    public static Field toField(int number){
        switch (number) {
            case 1:
                return East;
            case 2:
                return South;
            case 3:
                return West;
            case 4:
                return North;
        }
        return null;
    }
}
