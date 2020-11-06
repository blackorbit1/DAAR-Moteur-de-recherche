import java.util.ArrayList;
import java.util.HashMap;

public class EPSdfa {
    //MACROS
    static final int CONCAT = Main.CONCAT;
    static final int ETOILE = Main.ETOILE;
    static final int ALTERN = Main.ALTERN;
    static final int PROTECTION = Main.PROTECTION;
    static final int PARENTHESEOUVRANT = Main.PARENTHESEOUVRANT;
    static final int PARENTHESEFERMANT = Main.PARENTHESEFERMANT;
    static final int DOT = Main.DOT;

    private static int node_counter = 0;

    public EPSdfa(){}

    public HashMap<Integer,  ArrayList<ArrayList<Integer>>> getEpsDFA(HashMap<Integer, ArrayList<Couple>> ndfa) throws Exception {



        // on crée un nouveau tableau avec les noeuds entrant au lieu de sortant afin de faciliter
        // la creation d'un automate deterministe sans epsilon
        HashMap<Integer, ArrayList<Couple>> ndfa_entrant = new HashMap<>();

        // creation de toutes les lignes de ndfa_entrant
        for(Integer in : ndfa.keySet()){
            // construction d'une ligne du tableau
            ArrayList<Couple> tmp = new ArrayList<>();
            for(int i = 0; i < 260; i++) tmp.add(null);
            ndfa_entrant.put(in, tmp);

            // par defaut, ni initial ni final
            tmp.set(258, new Couple(0));
            tmp.set(257,new Couple(0));
            
            if(ndfa.get(in).get(257).equals(new Couple(1))){ // etat initial
                tmp.set(257,new Couple(1));
            } if(ndfa.get(in).get(258).equals(new Couple(1))){ // etat final
                tmp.set(258, new Couple(1));    
            }
        }

        // remplissage des lignes de ndfa_entrant
        for(Integer in : ndfa.keySet()){
            ArrayList<Couple> ligneN = ndfa.get(in);
            for(int i = 0; i < ligneN.size(); i++){
                // les etats initiaux et finaux ne correspondent pas a des transitions
                if(i == 257 || i == 258) continue;
                Couple elt = ligneN.get(i);
                if(elt != null){
                    ndfa_entrant.get(elt.first).set(i ,new Couple(in)); 
                    if(elt.getNbElem() == 2) ndfa_entrant.get(elt.second).set(i ,new Couple(in));
                }
            }
        } 

        System.out.print("\n\n");
        (new EPSndfa()).printAutomatonMatrix(ndfa_entrant);  



        /*
        id a b c d ... epsilon init final
        0  1                     F    F 
        1                 5      F    F
        2    3                   F    F
        3                 5      F    F
        4                 0,2    T    F
        5                        F    T


        id a b c d ... epsilon init final
        1                        F    T
        3                        F    T
        4  1 3                   T    F
        

        un etat avec uniquement des eps-transition en entree disparait,
        1) s'il n'est pas final, les transitions qu'il a en sortie sont transposées
        sur les noeuds entrants qu'il avait
        2) s'il est final, il transmet son statut aux noeuds entrants qu'il avait
        */

        // parcours de toutes les lignes
        // verification des transitions en entree, si les seules sont des epsilon (col 256)
        //      verif si etat final
        //          si oui : les etats en entree deviennent finaux
        //          si non : ses sorties sont ajoutees aux etats en entree
        //      il disparait (pas d'ajout au nouveau tableau)

        
        // creation du tableau représentant l'automate déterministe en utilisant des listes
        // au lieu de couple pour les aretes sortantes
        HashMap<Integer, ArrayList<ArrayList<Integer>>> result = new HashMap<>();
        for(Integer ligne_num : ndfa.keySet()) {
            ArrayList<ArrayList<Integer>> newLine = new ArrayList<>();
            for(Couple elt : ndfa.get(ligne_num)) {
                ArrayList<Integer> sorties = new ArrayList<>();
                if(elt != null){
                    sorties.add(elt.first);
                    if(elt.getNbElem() == 2) sorties.add(elt.second);
                }
                newLine.add(sorties);
            }
            result.put(ligne_num, newLine);
        }
        
          
        // parcours de toutes les lignes            
        for(Integer ligne_num : ndfa.keySet()){
            ArrayList<Couple> ligne_en = ndfa_entrant.get(ligne_num); // tableau des entrees
            ArrayList<Couple> ligne_so = ndfa.get(ligne_num); // tableau des sorties

            // que des eps en entree = 
            // nombre de colonnes non null == 3 (car eps + init + final) et col 256 non null
            boolean c1 = (ligne_en.get(256) != null);
            int nbNotNull = 0; 
            for(Couple c : ligne_en) if (c != null) nbNotNull++;
            boolean c2 = nbNotNull == 3;
            if(c1 && c2){ // uniquement des transitions epsilon en entree
                if(ligne_en.get(258).first == 1){ // si etat final 
                    // les etats en entree deviennent finaux 
                    Couple elt = ligne_en.get(256); // on met 1 dans la liste 258 de l'etat en entree, la liste n'a que 0 pour le moment
                    result.get(elt.first).get(258).set(0, 1);
                    if(elt.getNbElem() == 2) result.get(elt.second).get(258).set(0, 1);
                }else{
                    // les sorties sont ajoutees aux etats en entree
                    
                    for(int i = 0; i < ligne_so.size(); i++){
                        if(i == 257 || i == 258) continue;
                        Couple elt = ligne_so.get(i);
                        if(elt != null){
                            
                        }
                    }
                }
            }
        }


        return result;
        
    }


 

}