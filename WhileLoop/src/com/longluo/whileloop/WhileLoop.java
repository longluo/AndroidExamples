package com.longluo.whileloop;

public class WhileLoop {

	public static void main(String args[]) {
		System.out.println("begin...");
		boolean bLoop = false;
		boolean bEnd = true;
		boolean bOn = bLoop || bEnd;
		
		System.out.println("if: bLoop=" + bLoop + " bEnd=" + bEnd + " bOn = " + bOn);
		
		while (true) {
			if (bOn) {
				try {
					System.out.println("if: bLoop=" + bLoop + " bEnd=" + bEnd + " bOn = " + bOn);
				} catch (Exception ex) {
					System.out.println("Exception: bLoop=" + bLoop + " bEnd=" + bEnd + " bOn = " + bOn);
				}
				
				break;
			}
			
			System.out.println("End: bLoop=" + bLoop + " bEnd=" + bEnd + " bOn = " + bOn);
		}
	}
}
