import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class Containsdfa {

	
    //MACROS
    static final int CONCAT = Main.CONCAT;
    static final int ETOILE = Main.ETOILE;
    static final int ALTERN = Main.ALTERN;
    static final int PROTECTION = Main.PROTECTION;
    static final int PARENTHESEOUVRANT = Main.PARENTHESEOUVRANT;
    static final int PARENTHESEFERMANT = Main.PARENTHESEFERMANT;
    static final int DOT = Main.DOT;


    public Containsdfa() {}
    
    private HashMap<Integer,  ArrayList<ArrayList<Integer>>> dfaInterne;
    private Integer etatI;
    
    public boolean contient(HashMap<Integer,  ArrayList<ArrayList<Integer>>> dfa, ArrayList<String> text) {
    	
    	dfaInterne = dfa; 
    	
    	etatI = -1;
    	Integer etatCourant = -1;
    	for(Integer k : dfa.keySet()) {
    		// recherche de l'etat initial pour s'y placer
    		if(dfa.get(k).get(257).get(0) == 1) {
    			etatCourant = k;
    			etatI = k;
    			break;
    		}    			
    	}
    	if(etatI == -1) {
    		System.out.println("Etat initial non trouve");
    	}
    	
    	for(String s0 : text) {
    		char[] s = s0.toCharArray();
    		if(checkLigne(s,etatCourant)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean checkLigne(char[] ligne, Integer etatCo) {
    	boolean cf = dfaInterne.get(etatCo).get(258).get(0) == 1;
    	boolean cni =  dfaInterne.get(etatCo).get(257).get(0) == 0;
    	if(cf && cni) return true;
    	int lens = ligne.length;
    	if(lens == 0) return (cf && cni);
		// col est la valeur ascii du caractere
		int col = (int) ligne[0];
		if(col > 255) return false;
		ArrayList<ArrayList<Integer>> courant = dfaInterne.get(etatCo);
		if(courant.get(col) != null) {
			boolean res0 = false;
			for(Integer in : courant.get(col))
				res0 = res0 || checkLigne(Arrays.copyOfRange(ligne, 1, lens), in);
			if(res0) return true;
		}
		if(courant.get(259) != null) {
			boolean res0 = false;
			for(Integer in : courant.get(259))
				res0 = res0 || checkLigne(Arrays.copyOfRange(ligne, 1, lens), in);
			if(res0) return true;
		}		
		return checkLigne(Arrays.copyOfRange(ligne, 1, lens), etatI);
		
    }
			
    public ArrayList<String> contientWithLignes(HashMap<Integer,  ArrayList<ArrayList<Integer>>> dfa, ArrayList<String> text) {
    	
    	dfaInterne = dfa; 
    	
    	etatI = -1;
    	Integer etatCourant = -1;
    	for(Integer k : dfa.keySet()) {
    		// recherche de l'etat initial pour s'y placer
    		if(dfa.get(k).get(257).get(0) == 1) {
    			etatCourant = k;
    			etatI = k;
    			break;
    		}    			
    	}
    	if(etatI == -1) {
    		System.out.println("Etat initial non trouve");
    	}
    	
    	ArrayList<String> res = new ArrayList<String>();
    	for(String s0 : text) {
    		char[] s = s0.toCharArray();
    		if(checkLigne(s,etatCourant)) {
    			res.add(s0);
    		}
    	}
    	return res;
    }
    
    
    // renvoie une map avec cles =  numeros de lignes 
    // et valeurs = liste des pos des regex trouvees (arrayList couple avec couple = (debut,fin)
    public HashMap<Integer, ArrayList<Couple>> contientWithLignesEtPos(HashMap<Integer, ArrayList<ArrayList<Integer>>> dfa, ArrayList<String> text) {
    	
    	dfaInterne = dfa; 
    	
    	etatI = -1;
    	Integer etatCourant = -1;
    	for(Integer k : dfa.keySet()) {
    		// recherche de l'etat initial pour s'y placer
    		if(dfa.get(k).get(257).get(0) == 1) {
    			etatCourant = k;
    			etatI = k;
    			break;
    		}    			
    	}
    	if(etatI == -1) {
    		System.out.println("Etat initial non trouve");
    	}
    	
    	HashMap<Integer, ArrayList<Couple>> res = new HashMap<Integer, ArrayList<Couple>>();
    	int i = 0;
    	for(String s0 : text) {
    		char[] s = s0.toCharArray();
    		isChecked = new boolean[s.length+1][s.length+1];
    		ArrayList<Couple> resTmp = checkLigneWithPos(s,etatCourant, 0, 0);
    		if(resTmp.size() > 0) {
    			res.put(i, resTmp);
    		}
    		i++;
    	}
    	return res;
    }		
    
    boolean[][] isChecked;
    
		
    private ArrayList<Couple> checkLigneWithPos(char[] ligne, Integer etatCo, int debut, int actu) {
    	ArrayList<Couple> res = new ArrayList<>();
    	if(isChecked[debut][actu]) {
    		return res;
    	}
    	isChecked[debut][actu] = true;
		ArrayList<ArrayList<Integer>> courant = dfaInterne.get(etatCo);
    	boolean cf = courant.get(258).get(0) == 1;
    	if(cf) {
    		res.add(new Couple(debut,actu));
    		return res;
    	}
    	int lens = ligne.length;
    	if(lens == 0) {
    		return res;
    	}
		// col est la valeur ascii du caractere
		int col = (int) ligne[0];
		if(col > 255) return res;
		// si on a une transition de l'etat courant de l'automate vers un autre etat
		// en passant par la lettre : on continue
		if(courant.get(col) != null) {
			for(Integer in : courant.get(col))
				res.addAll(checkLigneWithPos(Arrays.copyOfRange(ligne, 1, lens), in, debut, actu+1));
		}
		if(courant.get(259) != null) {
			for(Integer in : courant.get(259))
				res.addAll(checkLigneWithPos(Arrays.copyOfRange(ligne, 1, lens), in, debut, actu+1));
		}
		res.addAll(checkLigneWithPos(Arrays.copyOfRange(ligne, 1, lens), etatI, actu+1, actu+1));

		return res;
		
    }	

    public String cWL_toString(ArrayList<String> in) {
    	StringBuffer sb = new StringBuffer();
    	
    	for(String elt : in) {
    		sb.append(elt);
    		sb.append("\n");
    	}
    	
    	return sb.toString();
    }
    
    
    public String cWLEP_toString(HashMap<Integer, ArrayList<Couple>> res, ArrayList<String> in) {
    	StringBuffer sb = new StringBuffer();
    	
    	for(Integer i : res.keySet()) {
    		ArrayList<Couple> elt = res.get(i);    
    		elt.sort(new Comparator<Couple>() {
				@Override
				public int compare(Couple o1, Couple o2) {
					return o1.first - o2.first;
				}
			});
    		sb.append(in.get(i));
    		sb.append("\n");
    		for(int j = 0; j < elt.size(); j++) {
    			for(int k = 0; k < elt.get(j).first; k++) {
    				sb.append(" ");
    			}
    			for(int k = elt.get(j).first; k < elt.get(j).second; k++) {
    				sb.append("^");
    			}
        		sb.append("\n");    			
    		}
    	}
    	
    	return sb.toString();
    }   
    
    
    
    
    
    
    
    
    
    
    
    
    
}
