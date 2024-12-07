package com.ipsim.components;

public class ALU {

    public static long add(long a, long b) {
        return a + b;
    }
    public static long sub(long a, long b) {
        return a - b;
    }
    public static long mul(long a, long b) {
        return a * b;
    }
    public static long div(long a, long b) {
        return a / b;
    }
    public static long mod(long a, long b) {
        return a % b;
    }
    public static long and(long a, long b) {
        return a & b;
    }
    public static long or(long a, long b) {
        return a | b;
    }
    public static long xor(long a, long b) {
        return a ^ b;
    }
    public static long not(long a) {
        return ~a;
    }
    public static long shl(long a, long b) {
        return a << b;
    }
    public static long shr(long a, long b) {
        return a >> b;
    }
}
