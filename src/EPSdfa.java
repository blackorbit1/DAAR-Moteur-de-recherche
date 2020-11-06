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


        /*
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
        */
        







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


        // on crée un nouveau tableau avec les noeuds entrant au lieu de sortant afin de faciliter
        // la creation d'un automate deterministe sans epsilon
        HashMap<Integer, ArrayList<ArrayList<Integer>>> ndfa_entrant = new HashMap<>();

        // creation de toutes les lignes de ndfa_entrant
        for(Integer in : result.keySet()){
            // construction d'une ligne du tableau
            ArrayList<ArrayList<Integer>> tmp = new ArrayList<>();
            for(int i = 0; i < 260; i++) tmp.add(null);
            ndfa_entrant.put(in, tmp);

            // par defaut, ni initial ni final
            tmp.set(258, getArrayList(0));
            tmp.set(257, getArrayList(0));
            
            if(result.get(in).get(257).equals(getArrayList(1))){ // etat initial
                tmp.set(257, getArrayList(1));
            } if(result.get(in).get(258).equals(getArrayList(1))){ // etat final
                tmp.set(258, getArrayList(1));    
            }
        }

        // remplissage des lignes de ndfa_entrant
        for(Integer in : result.keySet()){
            ArrayList<ArrayList<Integer>> ligneN = result.get(in);
            for(int i = 0; i < ligneN.size(); i++){
                // les etats initiaux et finaux ne correspondent pas a des transitions
                if(i == 257 || i == 258) continue;
                ArrayList<Integer> elt = ligneN.get(i);
                if(elt != null && elt.size() > 0){ 

                    ndfa_entrant.get(elt.get(0)).set(i ,getArrayList(in)); 

                    if(elt.size() == 2) ndfa_entrant.get(elt.get(1)).set(i ,getArrayList(in));
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

        
        
        boolean condWhile = false;
        do { 
            // parcours de toutes les lignes            
            for(Integer ligne_num : ndfa.keySet()){
                ArrayList<ArrayList<Integer>> ligne_en = ndfa_entrant.get(ligne_num); // tableau des entrees
                ArrayList<ArrayList<Integer>> ligne_so = result.get(ligne_num); // tableau des sorties

                // que des eps en entree = 
                // nombre de colonnes non null == 3 (car eps + init + final) et col 256 non null
                boolean c1 = (ligne_en.get(256) != null);
                int nbNotNull = 0; 
                for(ArrayList<Integer> c : ligne_en) if (c != null) nbNotNull++;
                boolean c2 = nbNotNull == 3;
                if(c1 && c2){ // uniquement des transitions epsilon en entree
                    if(ligne_en.get(258).get(0) == 1){ // si etat final 
                        // les etats en entree deviennent finaux 
                        ArrayList<Integer> elt = ligne_en.get(256); // on met 1 dans la liste 258 de l'etat en entree, la liste n'a que 0 pour le moment
                        for(int i = 0; i < elt.size(); i++){         
                            // gestion dans la liste sortant               
                            result.get(elt.get(i)).get(258).set(0, 1);       
                            // gestion dans la liste entrants     
                            ndfa_entrant.get(elt.get(i)).get(258).set(0, 1);
                        }
                    } else {
                        // les sorties sont ajoutees aux etats en entree
                        HashMap<Integer, ArrayList<Integer>> mapSorties = new HashMap<>();
                        for(int i = 0; i < ligne_so.size(); i++){
                            if(i == 257 || i == 258) continue;
                            ArrayList<Integer> elt = ligne_so.get(i);
                            if(elt != null){
                                // map de toutes les sorties
                                mapSorties.put(i, result.get(ligne_num).get(i));
                            }
                        } 
                        ArrayList<Integer> entrants = new ArrayList<>();
                        ArrayList<Integer> elt = ligne_en.get(256);
                        for(Integer in : elt){ // on va dans tous les noeuds entrants (via epsilon)
                            for(Integer inM : mapSorties.keySet()){ // on ajoute dans le noeud toutes les sorties de tous les noeuds que contient mapSorties (toutes les sorties du noeud à supprimer)
                                
                                // il faut remplacer dans entrants, les sortantes du noeud en cours
                                // dans le result on transfere les transitions sortantes en les donnants à nos transitions entrantes
                                // il faut aussi faire l'inverse, donner nos transitions entrantes aux recepteurs de nos transitions sortantes
                                
                                // si on a a -(eps)-> b -(c)-> c 
                                // on aura a -(c)-> c 
                                // pour le moment 'a' possede l'info qu'il doit aller vers c avec une transi c
                                // mais 'c' n'a pas l'info que sa transition c arrive par 'a' et plus par 'b'
                                
                                // on va dire à a, au lieu de pointer sur b maintenant tu pointes sur c
                                try{ // si le noeud qu'on compresse a des sortants epsilon, on doit les garder mais 
                                // supprimer l'info que le noeud in a une sortie epsilon vers le noeud en cours de compresson
                                    result.get(in).get(inM).addAll(mapSorties.get(inM));
                                    result.get(in).get(256).remove(ligne_num);

                                    // la il faut regarder pour toutes nos transitions sortantes (nous = ligne_num)
                                    // on va au noeud au bout de la transition, on se retire de sa liste de transitions entrantes
                                    // a la colonne de la transition qu'on vient d'emprunter
                                    // puis on ajoute le noeud entrant actuel (in) à la place
                                    // on doit se placer à la ligne correspondant à la valeur de mapSorties.get(inM)
                                    for(Integer a_modifier : mapSorties.get(inM)){
                                        ndfa_entrant.get(a_modifier).get(inM).remove(ligne_num);
                                        ndfa_entrant.get(a_modifier).get(inM).add(in);
                                        // ce qu'on fait est bon je pense, mais pas suffisant car il reste du epsilon
                                    }
                                    
                                } catch (NullPointerException e){
                                    System.err.println("erreur : "+ligne_num+" "+in+" "+inM+" "+mapSorties.get(inM));
                                    e.printStackTrace();
                                    System.exit(1);
                                }
                                // on ajoute les nouvelles sorties aux etats qui sont en entree 
                                // donc je vois pas ce qu'on doit modifier dans entrants
                                
                                /*
                                bah du coup faut print le resultat final
                                */
                            }
                        }
                    }
                    result.remove(ligne_num);
                    ndfa_entrant.remove(ligne_num);

                }
            }
            condWhile = false;
            for(Integer in : result.keySet()){
                if(result.get(in).get(256).size() > 0){
                    //condWhile = true;
                }
            }
        } while (condWhile);


        
        System.out.println("\n\n");
        (new EPSndfa()).printAutomatonMatrix(result);


        return result; 
        
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