import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;

public class Util {

	 /**
     * Convert an array of 4 bytes into an integer.
     *
     * @param  b The byte array to be converted
     */
	public static int byteArrayToInt(byte[] b) 
	{
	    return   b[3] & 0xFF |
	            (b[2] & 0xFF) << 8 |
	            (b[1] & 0xFF) << 16 |
	            (b[0] & 0xFF) << 24;
	}
	 /**
     * Convert an integer into a byte array of 4 bytes.
     *
     * @param  a The integer to be converted
     */
	public static byte[] intToByteArray(int a)
	{
	    return new byte[] {
	        (byte) ((a >>> 24) & 0xFF),
	        (byte) ((a >>> 16) & 0xFF),   
	        (byte) ((a >>> 8) & 0xFF),   
	        (byte) (a & 0xFF)
	    };
	}
	

	// xores two byte arrays of the same length, and store the result in the
	// first one. The two byte arrays are left aligned. If the second array is bigger than the first array,
	// then all bytes after bytes[eHash.length-1] are lost. 
	public static void xor(byte[] eHash, byte[] bytes) {
		// TODO Auto-generated method stub
		int length;
		if(eHash.length<=bytes.length)
			length=eHash.length;
		else
			length=bytes.length;
		for (int i = 0; i < length; i++) {
			eHash[i] = (byte) (eHash[i] ^ bytes[i]);
		}

	}
	
	 /**
     * Get the ith bit in the selection string. Return true if the bit is set to 1, otherwise
     * return false
     *
     * @param  i index of bit to get (start from 0).
     */
	public static boolean getBit(int i, int leadingZeroes, byte[] str){
		return (str[(i+leadingZeroes) >>> 3] & (1 << (7-((i+leadingZeroes) & 7)))) != 0;
	    
	}
	
	public static byte[] getByteArrayByLength(int len){
		int r = len%8;
		if(r==0){
			return new byte[len/8];
		}
		else{
			return new byte[len/8+1];
		}
	}
	
	/**
	 * for a byte array that encodes a len-bit string, how many unused bits at the beginning of the array.
	 * @param len
	 * @return
	 */
	
	public static int getLeadingZeroes(int len){
		int r = len%8;
		
		if(r==0){
			return 0;
		}
		else{
			return 8-r;
		}
	}
	
	public static boolean compareByteArrays(byte[] ba1, byte[] ba2){
		if(ba1.length!=ba2.length)
			return false;
		for(int i=0;i<ba1.length;i++){
			if(ba1[i]!=ba2[i])
				return false;
		}
		return true;
	}
	
	public static byte[] concatenate(byte[] ba1, byte[] ba2){
		byte[] res= new byte[ba1.length+ba2.length];
		System.arraycopy(ba1, 0, res, 0, ba1.length);
		System.arraycopy(ba2, 0, res, ba1.length, ba2.length);
		return res;
	}
	
	public static Object getObjectFromFile(String str) {
		// TODO Auto-generated method stub
		Object o = null;
		try {
			FileInputStream fileIn = new FileInputStream(str);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			o = in.readObject();
			in.close();
			fileIn.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return o;
	}

	public static byte[][] randomIntSet(int n) {
		// TODO Auto-generated method stub
		HashSet<Integer> set = new HashSet<Integer>();
		byte[][] out = new byte[n][];
		Random rnd = new SecureRandom();
		// initialise set
		for (int i = 0; i < n; i++) {
			Integer e;
			do {
				e = rnd.nextInt();
			} while (set.contains(e));
			set.add(e);
			out[i]= Util.intToByteArray(e);
		}
		return out;
	}
}
