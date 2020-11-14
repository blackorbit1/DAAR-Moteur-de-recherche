import java.awt.RenderingHints.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.UnaryOperator;

public class Reducdfa {
    //MACROS
    static final int CONCAT = Main.CONCAT;
    static final int ETOILE = Main.ETOILE;
    static final int ALTERN = Main.ALTERN;
    static final int PROTECTION = Main.PROTECTION;
    static final int PARENTHESEOUVRANT = Main.PARENTHESEOUVRANT;
    static final int PARENTHESEFERMANT = Main.PARENTHESEFERMANT;
    static final int DOT = Main.DOT;


    public Reducdfa() {}
    
    public HashMap<Integer,  ArrayList<ArrayList<Integer>>> getDFAReduit(HashMap<Integer,  ArrayList<ArrayList<Integer>>> dfa) {

         HashMap<Integer, ArrayList<ArrayList<Integer>>> res = new HashMap<>();
         
         
         
		 Integer[] cleListe = new Integer[dfa.keySet().size()];
		 int ii = 0;
		 for(Integer elt : dfa.keySet()) {
			 cleListe[ii] = elt;
			 ii++;
		 }
         
         ArrayList<Integer> fusionnes = new ArrayList<Integer>();
         for(int i = 0; i < cleListe.length; i++) {        		 
        	 Integer cle = cleListe[i];
			 if(!fusionnes.contains(cle)) {
				 ArrayList<ArrayList<Integer>> ligne = dfa.get(cle);
				 res.put(cle, ligne);
	        	 for(int j = i; j < cleListe.length; j++) {
	        		 Integer cleJ = cleListe[j];
	        		 ArrayList<ArrayList<Integer>> ligne2 =  dfa.get(cleJ);
	        		 boolean c1 = true;
	        		 for(int k = 0; k < ligne.size(); k++) {
	        			 ArrayList<Integer> elt = ligne.get(k);
	        			 ArrayList<Integer> elt2 = ligne2.get(k);
	        			 if((elt == null && elt2 != null && elt2.size() > 0) || (elt != null && elt2 == null && elt.size() > 0)) {
	        				 c1 = false;
	        				 break;
	        			 }
	        			 if(elt == null && elt2 == null) continue;
	        			 if(elt.size() != elt2.size()) {
	        				 c1 = false;
	        				 break;
	        			 }
	        			 for(int i1 = 0; i1 < elt.size(); i1++) {
	        				 if(elt.get(i1) != elt2.get(i1)) {
	        					 c1 = false;
	        					 break;
	        				 }
	        			 }
	        		 }
	        		 if(c1) {
	        			 fusionnes.add(cleJ);
	        			 // les etats qui amenent a la ligne j voient leurs transitions
	        			 // vers j devenir des transitions vers i : recherche dans tout dfa et tout res
	        			 // des i, on les remplace par des i 
	        			 for(Integer in : dfa.keySet()) {
	        				 ArrayList<ArrayList<Integer>> ligneM = dfa.get(in); 
	        				 for(int jn = 0; jn < ligneM.size(); jn++) {
	        					 if(jn == 257 || jn == 258 || ligneM.get(jn) == null) continue;	        					 
	        					 ligneM.get(jn).replaceAll(new UnaryOperator<Integer>() {									
									@Override
									public Integer apply(Integer t) {
										if(t == cleJ)
											return cle;
										return t;
									}
								});
	        				 }
	        			 }
	        			 for(Integer in : res.keySet()) {
	        				 ArrayList<ArrayList<Integer>> ligneM = res.get(in); 
	        				 for(int jn = 0; jn < ligneM.size(); jn++) {
	        					 if(jn == 257 || jn == 258 || ligneM.get(jn) == null) continue;	        					 
	        					 ligneM.get(jn).replaceAll(new UnaryOperator<Integer>() {									
									@Override
									public Integer apply(Integer t) {
										if(t == cleJ)
											return cle;
										return t;
									}
								});
	        				 }
	        			 }
	        		 }
	        	 }				 
			 }
         }
         
         return res;
    }

    
    
	private ArrayList<Integer> getArrayList(int a){
        ArrayList<Integer> res = new ArrayList<>();
        res.add(a);
        return res;
    }

    private ArrayList<Integer> getArrayList(int a, int b){
        ArrayList<Integer> res = getArrayList(a);
        res.add(b);
        return res;
    }

}
