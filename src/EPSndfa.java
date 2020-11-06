import java.util.ArrayList;
import java.util.HashMap;

public class EPSndfa {
    //MACROS
    static final int CONCAT = Main.CONCAT;
    static final int ETOILE = Main.ETOILE;
    static final int ALTERN = Main.ALTERN;
    static final int PROTECTION = Main.PROTECTION;
    static final int PARENTHESEOUVRANT = Main.PARENTHESEOUVRANT;
    static final int PARENTHESEFERMANT = Main.PARENTHESEFERMANT;
    static final int DOT = Main.DOT;

    private static int node_counter = 0;

    public EPSndfa(){}

    public HashMap<Integer, ArrayList<Couple>> getEpsNDFA(RegExTree tree) throws Exception {
        // . = -(eps)-> o -(caractere)-> o -(eps)-> 
        // .abcd
/*
        (a|b)uyze

        /---> a uyze 
        \---> b uyze

              .
            /  \
            .   e
           / \
           .  z
          / \
          .  y
         / \
       (|)  u
       / \
       a  b
       
            .
        / \
        a  b

        -(start)-> 0i -(a)-> 1f 

        id a b c d ... epsilon init final
        0  1                     T    F
        1                        F    T

        
        -(start)-> 2i -(b)-> 3f 

        id a b c d ... epsilon init final
        2    3                   T    F 
        3                        F    T


        -(start)-> 0i -(a)-> 1 -(eps)-> 2 -(b)-> 3f 
        num col :        256    257  258 
        id a b c d ... epsilon init final 
        0  1                     T    F
        1                2       F    F
        2    3                   F    F 
        3                        F    T
        
            ArrayList<Couple> transition = new ArrayList<>();
            for(int i = 0; i < 257; i++) transition.add(null);
        */
        HashMap<Integer, ArrayList<Couple>> res = new HashMap<>();

        System.out.println("tree.root : " + tree.root);
        boolean c1 = (tree.root >= 0 && tree.root < 256);
        if( c1 || tree.root == DOT){
            ArrayList<Couple> etatInit = new ArrayList<>();
            for(int i = 0; i < 260; i++) etatInit.add(null);
            res.put(node_counter++, etatInit);
            // gestion du point comme caractere normal a cet etape
            etatInit.set((c1 ? tree.root : 259), new Couple(node_counter));
                  
            etatInit.set(257, new Couple(1));
            etatInit.set(258, new Couple(0));
            ArrayList<Couple> etatFinal = new ArrayList<>();
            for(int i = 0; i < 260; i++) etatFinal.add(null);
            etatFinal.set(258, new Couple(1));
            etatFinal.set(257, new Couple(0));
            res.put(node_counter++, etatFinal);      
            
        
        } else if(tree.root == CONCAT){
            
            if(tree.subTrees.size() != 2) throw new Exception("Nombre d'elements incorrect");

            HashMap<Integer, ArrayList<Couple>> resGauche = getEpsNDFA(tree.subTrees.get(0));
            HashMap<Integer, ArrayList<Couple>> resDroite = getEpsNDFA(tree.subTrees.get(1));

            Integer idEps = -1;
            for(Integer id : resDroite.keySet()){
                ArrayList<Couple> noeud = resDroite.get(id);
                // on cherche l'etat initial
                if(noeud.get(257).equals(new Couple(1))){
                    idEps = id;
                    noeud.set(257,new Couple(0));
                    break;
                }
            }
            
            for(Integer id : resGauche.keySet()){
                ArrayList<Couple> noeud = resGauche.get(id);
                // on recherche l'etat final                
                if(noeud.get(258).equals(new Couple(1))){
                    // retrait de l'etat final 
                    noeud.set(258, new Couple(0));
                    // ajout de l'epsilon transition vers le noeud initial de droite
                    noeud.set(256, new Couple(idEps));
                    break;
                }
            }

            res.putAll(resGauche);
            res.putAll(resDroite);

                    
        } else if(tree.root == ETOILE) {
            if(tree.subTrees.size() != 1) throw new Exception("Nombre d'elements incorrect");

            HashMap<Integer, ArrayList<Couple>> resFils = getEpsNDFA(tree.subTrees.get(0));
            
            
            Integer idInit = -1;
            Integer idFinal = -1;
            for(Integer id : resFils.keySet()){
                ArrayList<Couple> noeud = resFils.get(id);
                // on cherche l'etat initial
                if(noeud.get(257).equals(new Couple(1))){
                    idInit = id;
                    noeud.set(257,new Couple(0));
                }          
                // on cherche l'etat final
                if(noeud.get(258).equals(new Couple(1))){
                    idFinal = id;
                    noeud.set(258, new Couple(0));
                    
                }
            }

            
            ArrayList<Couple> etatInit = new ArrayList<>();
            for(int i = 0; i < 260; i++) etatInit.add(null);               
            etatInit.set(257, new Couple(1));
            etatInit.set(258, new Couple(0));
            res.put(node_counter++, etatInit);   
            ArrayList<Couple> etatFinal = new ArrayList<>();
            for(int i = 0; i < 260; i++) etatFinal.add(null);
            etatFinal.set(257, new Couple(0));
            etatFinal.set(258, new Couple(1));
            res.put(node_counter++, etatFinal); 

            
            etatInit.set(256, new Couple(idInit, node_counter - 1));
            resFils.get(idFinal).set(256, new Couple(idInit, node_counter - 1));

            res.putAll(resFils);
            /*

            id a b c d ... epsilon init final
            0  1                     T    F 
            1                        F    T

            id a b c d ... epsilon init final
            0  1                     F    F 
            1                0,3     F    F
            2                0,3     T    F
            3                        F    T

            */
        
        } else if(tree.root == ALTERN){
            /*

            id a b c d ... epsilon init final
            0  1                     T    F 
            1                        F    T

            id a b c d ... epsilon init final
            2    3                   T    F 
            3                        F    T  

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

            if(tree.subTrees.size() != 2) throw new Exception("Nombre d'elements incorrect");

            HashMap<Integer, ArrayList<Couple>> resGauche = getEpsNDFA(tree.subTrees.get(0));
            HashMap<Integer, ArrayList<Couple>> resDroite = getEpsNDFA(tree.subTrees.get(1));


            ArrayList<Couple> etatInit = new ArrayList<>();
            for(int i = 0; i < 260; i++) etatInit.add(null);               
            etatInit.set(257, new Couple(1));
            etatInit.set(258, new Couple(0));
            res.put(node_counter++, etatInit);   
            ArrayList<Couple> etatFinal = new ArrayList<>();
            for(int i = 0; i < 260; i++) etatFinal.add(null);
            etatFinal.set(257, new Couple(0));
            etatFinal.set(258, new Couple(1));
            res.put(node_counter++, etatFinal); 

            
            Integer idInitg = -1;
            Integer idFinalg = -1;
            for(Integer id : resGauche.keySet()){
                ArrayList<Couple> noeud = resGauche.get(id);
                // on cherche l'etat initial
                if(noeud.get(257).equals(new Couple(1))){
                    idInitg = id;
                    noeud.set(257,new Couple(0));
                }          
                // on cherche l'etat final
                if(noeud.get(258).equals(new Couple(1))){
                    idFinalg = id;
                    noeud.set(258, new Couple(0));
                    
                }
            }
            Integer idInitd = -1;
            Integer idFinald = -1;
            for(Integer id : resDroite.keySet()){
                ArrayList<Couple> noeud = resDroite.get(id);
                // on cherche l'etat initial
                if(noeud.get(257).equals(new Couple(1))){
                    idInitd = id;
                    noeud.set(257,new Couple(0));
                }          
                // on cherche l'etat final
                if(noeud.get(258).equals(new Couple(1))){
                    idFinald = id;
                    noeud.set(258, new Couple(0));
                    
                }
            }

            etatInit.set(256, new Couple(idInitg, idInitd));
            resGauche.get(idFinalg).set(256, new Couple(node_counter - 1));
            resDroite.get(idFinald).set(256, new Couple(node_counter - 1));


            res.putAll(resGauche);
            res.putAll(resDroite);

        } 
        

        return res;
    }

    public void printAutomatonMatrix(HashMap<Integer, ArrayList<Couple>> matrix){
        for(Integer id : matrix.keySet()){
            ArrayList<Couple> noeud = matrix.get(id);

            System.out.print(id + " : ");
            for(int i = 0; i < noeud.size(); i++){
                Couple couple = noeud.get(i);
                if(couple != null){
                    System.out.print("col_" + i + ":" + couple + " ");
                }
            }
            System.out.println("");
        }
    }







    
}
