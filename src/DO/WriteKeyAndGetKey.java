package DO;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;

import static tools.Parameter.M;
import static tools.Parameter.N;

public class WriteKeyAndGetKey {
    public static void write(String filePath) {
        PairingParameters pairingParameters = PairingFactory.getPairingParameters("jars/a.properties");
        Pairing pairing = PairingFactory.getPairing(pairingParameters);
        Element g = pairing.getG1().newRandomElement().getImmutable();//G群生成元

//        BigInteger sk = new BigInteger("9223372036854775809");
        Element sk = pairing.getZr().newRandomElement().getImmutable();
        Properties Prop = new Properties();
        Prop.setProperty("sk", Base64.getEncoder().encodeToString(sk.toBytes()));

        Element[] pk = new Element[1000000];
        for(int i = 0;i < pk.length;i++){

            long startTime1 = System.currentTimeMillis();
            Element temp = sk.powZn(pairing.getZr().newElement(i));
            long endTime1 = System.currentTimeMillis();

            long startTime2 = System.currentTimeMillis();
            pk[i] = g.powZn(temp);
            long endTime2 = System.currentTimeMillis();
            System.out.println((endTime1 - startTime1) + "  " + (endTime2 - startTime2));

            System.out.println(i + "th: " + pk[i]);
            Prop.setProperty("pk[" + i + "]", Base64.getEncoder().encodeToString(pk[i].toBytes()));
        }
        storePropToFile(Prop, filePath);
    }

    public static Element getSecretKey(String fileName) {
        PairingParameters pairingParameters = PairingFactory.getPairingParameters("jars/a.properties");
        Pairing pairing = PairingFactory.getPairing(pairingParameters);
        Properties keyProp = loadPropFromFile(fileName);
        String skString = keyProp.getProperty("sk");
        Element sk = pairing.getZr().newElementFromBytes(Base64.getDecoder().decode(skString)).getImmutable();
        return sk;
    }

    public static Element[] getPublicKey(String fileName) {
        PairingParameters pairingParameters = PairingFactory.getPairingParameters("jars/a.properties");
        Pairing pairing = PairingFactory.getPairing(pairingParameters);
        Properties keyProp = loadPropFromFile(fileName);
        Element[] pk = new Element[Math.max(M, N)];
        for (int i = 0; i < pk.length; i++) {
            String pkString = keyProp.getProperty("pk[" + i + "]");
            pk[i] = pairing.getG1().newElementFromBytes(Base64.getDecoder().decode(pkString)).getImmutable();
        }
        return pk;
    }


    public static void storePropToFile(Properties prop, String fileName) {
        try(FileOutputStream out = new FileOutputStream(fileName)) {
            prop.store(out, null);
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println(fileName + "save failed!");
            System.exit(-1);
        }
    }

    public static Properties loadPropFromFile(String fileName) {
        Properties prop = new Properties();
        try(FileInputStream in = new FileInputStream(fileName)) {
            prop.load(in);
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println(fileName + "load failed!");
            System.exit(-1);
        }
        return prop;
    }

    public static void main(String[] args) {
        String filePath = "./src/allKeys.txt";

//        long startTime = System.currentTimeMillis();
//        write(filePath);
//        long endTime = System.currentTimeMillis();
//        System.out.println(endTime - startTime);

        long startTime = System.currentTimeMillis();
        Element sk = getSecretKey(filePath);
        Element[] pk = getPublicKey(filePath);
        long endTime = System.currentTimeMillis();
        System.out.println(endTime - startTime);
    }
}
