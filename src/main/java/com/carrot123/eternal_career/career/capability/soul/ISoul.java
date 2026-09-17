package com.carrot123.eternal_career.career.capability.soul;

public interface ISoul {
    int getSoul();
    void setSoul(int value);
    void addSoul(int amount, int maxSoul);
    boolean consumeSoul(int amount);
}
