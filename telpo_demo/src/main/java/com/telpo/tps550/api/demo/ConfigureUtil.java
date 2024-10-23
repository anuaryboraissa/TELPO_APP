package com.telpo.tps550.api.demo;

public class ConfigureUtil {

	/**
	 * 打印 = 1;
	 * 二维码识别 = 2;
	 * 磁条卡测试 = 3;
	 * 钱箱 = 4;
	 * 多功能读卡器（三合一）识别 = 5;
	 * IC卡测试 = 6;
	 * NFC测试 = 7;
	 * PSAM测试 = 8;
	 * 防拆测试 = 9;
	 * LED测试 = 10;
	 * 继电器测试 = 11;
	 * RS485/232测试 = 12;
	 * 韦根/门磁测试 = 13;
	 * 数码管测试 = 14;
	 * 系统接口测试 = 15;
	 * Can总线测试 = 16;
	 * GPIO测试 = 17；
	 * 串口测试 = 18；
	 * 简单小副屏测试（微笑屏） = 19；
	 * IO上/下电控制PowerControl = 20;
	 * 自助柜 = 21
	 * 传感器 = 22
	 */
//	public static final int[] S1 = new int[] {10,13,16};
	public static final int[] T20 = new int[] {2,7,8,10,11,12,13,15,16,20};
	public static final int[] T20P = new int[] {2,7,8,10,11,12,13,15};
	public static final int[] T10 = new int[]{2, 7, 8, 10, 11, 12, 13, 15, 20};
	public static final int[] T20B = new int[]{2, 7, 8, 10, 11, 12, 13, 15, 16, 20};
	public static final int[] S8G = new int[]{2, 6, 7, 8, 15, 20};
	public static final int[] M8 = new int[]{1, 2, 4, 7, 8, 9, 10, 12, 15, 18, 19};
	public static final int[] C1B = new int[] {1,4,10,12,17,18,14};
	public static final int[] C1P = new int[] {1,4,5,7,15,18};
	public static final int[] C11 = new int[] {4,7,12,15};
	public static final int[] TPS967M = new int[] {7,11,13,15,12,10};
	public static final int[] F10B = new int[] {5,7,10,11,13,15,12};
	public static final int[] TPS980B = new int[] {7,10,13,15,12};
	public static final int[] C1Pro = new int[] {1,4,18};
	public static final int[] TPS900 = new int[] {1,2,3,6,7,8};
	public static final int[] V502 = new int[] {10,20,22};
	public static final int[] COMMON = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22};
}