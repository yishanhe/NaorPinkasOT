
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.DSAPublicKey;

public class Main {

    public static void main(String[] args) {
        System.out.println("Test OT");


        int k = 10; // run OT k times
        int N = 2;  // 1-out-of-N OT

        KeyPairGenerator keyPairGenerator = null;

        try{
            keyPairGenerator = KeyPairGenerator.getInstance("DSA");
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        SecureRandom rnd = new SecureRandom();
        keyPairGenerator.initialize(1024,rnd);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        DSAPublicKey pub = (DSAPublicKey)keyPair.getPublic();

        BigInteger p = pub.getParams().getP();
        BigInteger q = pub.getParams().getG();
        BigInteger g = pub.getParams().getG();

        int l = 160;
        int sigma = l/8;


        // initiate the toSend using random bytes
        byte[][][] toSend = new byte[k][N][sigma];

        for (int i = 0; i < k; i++) {
            for (int j = 0; j < N; j++) {
                rnd.nextBytes(toSend[i][j]);
            }
        }


        // prepare md　
        MessageDigest md = null;

        // depend on k
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        // init sender
        BasePrimeOTS sender = new BasePrimeOTS(p,q,g,md,k,N,toSend);

        // init a selection vector using random int
        int[] selection = new int[k];
        for (int i = 0; i < k; i++) {
            selection[i] = rnd.nextInt(N);
            System.out.println(selection[i]);
        }

        BasePrimeOTR receiver = new BasePrimeOTR(p,q,g,selection,md,N);



        // start

        BigInteger[] cs = sender.getCs();

        BigInteger[] pk0s = receiver.preparePK0(cs);

        byte[][][] encrypted = new byte[k][N][sigma];

        sender.onReceivePK0s(pk0s,encrypted);

        byte[][] result = receiver.onReceiveEncByte(encrypted,cs);

        byte[][][] allresult = receiver.tryDecAll(encrypted,cs);

        for (int i = 0; i < k; i++) {

            if(!test(result[i],toSend[i][selection[i]])){
                System.out.println("Incorrect at bit "+i+": choice "+selection[i]);
                System.out.println(bytesToHex(result[i]) + " v.s " + bytesToHex(toSend[i][selection[i]]));

            }
        }

        // check all other are corrupted.
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < N; j++) {
                if(!test(result[i],toSend[i][j])){
                    System.out.println("Incorrect at bit "+i+": choice "+j);
//                    System.out.println(bytesToHex(result[i]) + " v.s " + bytesToHex(toSend[i][selection[i]]));

                } else {
                    System.out.println("Correct at bit "+i+": choice "+j);;
                }
            }
        }








    }

    private static boolean test(byte[] bs, byte[] bs2) {
        for(int i=0;i<bs.length;i++){
            if(bs[i]!=bs2[i])
                return false;
        }
        return true;
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


}
