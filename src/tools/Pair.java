package tools;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class Pair {
	static PairingParameters pairingParameters = PairingFactory.getPairingParameters("jars/a.properties");
	
	public static Pairing pairing = PairingFactory.getPairing(pairingParameters);
	public static Field Zr = pairing.getZr();
}

