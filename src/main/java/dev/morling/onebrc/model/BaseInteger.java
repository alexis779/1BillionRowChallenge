package dev.morling.onebrc.model;

public class BaseInteger {
    private int power = 1;
    private int sum = 0;

    public int sum() {
        return sum;
    }
    public void sum(final int sum) {
        this.sum = sum;
    }

    public int power() {
        return power;
    }
    public void power(final int power) {
        this.power = power;
    }
}