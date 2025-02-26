package com.gsc.kixxhub.device.pumpa.common;

public class Show {

	/**
	 * @param dat
	 * @param size
	 * @param widthCnt
	 */
	public static void datas(byte[] dat, int size, int widthCnt) throws Exception {
		
		int	i=0, j, cnt=0;
		String str = new String();

		while (cnt<size) {

			for (j=0; cnt<size && j<widthCnt; j++) {

				if (j!=0 && j%10==0) System.out.printf ("  ");

				if (dat[cnt] <= 0x20) // Hex(Command)
					System.out.printf ("x%02X ", dat[cnt]);
				else // Ascii
					System.out.printf ("'%c' ", dat[cnt]);

				cnt++;
			}
			System.out.printf ("\n");
			i = i + j;
		}
	}
}

