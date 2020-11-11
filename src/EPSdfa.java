import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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


        // creation du tableau repr�sentant l'automate d�terministe en utilisant des listes
        // au lieu de couple pour les aretes sortantes
        HashMap<Integer, ArrayList<ArrayList<Integer>>> ndfa_sortant = new HashMap<>();
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
            ndfa_sortant.put(ligne_num, newLine);
        }


        // on cr�e un nouveau tableau avec les noeuds entrant au lieu de sortant afin de faciliter
        // la creation d'un automate deterministe sans epsilon
        HashMap<Integer, ArrayList<ArrayList<Integer>>> ndfa_entrant = new HashMap<>();

        // creation de toutes les lignes de ndfa_entrant
        for(Integer in : ndfa_sortant.keySet()){
            // construction d'une ligne du tableau
            ArrayList<ArrayList<Integer>> tmp = new ArrayList<>();
            for(int i = 0; i < 260; i++) tmp.add(null);
            ndfa_entrant.put(in, tmp);

            // par defaut, ni initial ni final
            tmp.set(258, getArrayList(0));
            tmp.set(257, getArrayList(0));

            if(ndfa_sortant.get(in).get(257).equals(getArrayList(1))){ // etat initial
                tmp.set(257, getArrayList(1));
            } if(ndfa_sortant.get(in).get(258).equals(getArrayList(1))){ // etat final
                tmp.set(258, getArrayList(1));
            }
        }

        // remplissage des lignes de ndfa_entrant
        for(Integer in : ndfa_sortant.keySet()){
            ArrayList<ArrayList<Integer>> ligneN = ndfa_sortant.get(in);
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



        System.out.println("\nndfa entrant : ");
        (new EPSndfa()).printAutomatonMatrix(ndfa_entrant);


        // selection des noeuds "importants" pour le resultat
        HashMap<Integer, ArrayList<ArrayList<Integer>>> result = new HashMap<>();
        for(Integer ligne_num : ndfa_sortant.keySet()) {
            ArrayList<ArrayList<Integer>> newLine = new ArrayList<>();
            ArrayList<ArrayList<Integer>> line_entrant = ndfa_entrant.get(ligne_num);
            ArrayList<ArrayList<Integer>> line_sortant = ndfa_sortant.get(ligne_num);
            boolean c1 = false;
            for(int i = 0; i < line_entrant.size(); i++) {
                if(line_entrant.get(i) != null && (i < 256 || i > 258)) {
                    c1 = true;
                    break;
                }
            }
            if (line_entrant.get(257).get(0) == 1) c1 = true;
            if(c1) {
                for(int i = 0; i < 260; i++) newLine.add(null);
                newLine.set(258, line_sortant.get(258));
                newLine.set(257, line_sortant.get(257));
                result.put(ligne_num, newLine);
            }
        }

        // creation de la matrice de transitions
        int nbNoeuds = ndfa_sortant.keySet().size();
        int[][] transiMatrix = new int[nbNoeuds][nbNoeuds];

        for(Integer ligne_num : ndfa_sortant.keySet()) {
            for(int i = 0; i < ndfa_sortant.get(ligne_num).size(); i++) {
                if(i == 257 || i == 258) continue;
                ArrayList<Integer> transiListe = ndfa_sortant.get(ligne_num).get(i);
                if(transiListe != null) {
                    for(Integer elt : transiListe) {
                        transiMatrix[ligne_num][elt] = 1;
                    }
                }
            }
        }
        for(int i = 0; i < nbNoeuds; i++) {
            transiMatrix[i][i] = 1;
        }

        // remplissage des transitions du resultat
        for(Integer ligne_num : result.keySet()) {
            HashMap<Integer,Integer> transis = getTransis(ligne_num, transiMatrix, ndfa_sortant);
            for(Integer etatArr : transis.keySet()) {
                Integer transi = transis.get(etatArr);
                ArrayList<Integer> elt = result.get(ligne_num).get(transi);
                if(elt == null) {
                    result.get(ligne_num).set(transi, new ArrayList<Integer>());
                    elt = result.get(ligne_num).get(transi);
                }
                elt.add(etatArr);
            }
        }

        // recuperation des etats finaux dans le resultat
        for(Integer ligne_num : result.keySet()) {
            result.get(ligne_num).get(258).set(0, getFinal(ligne_num, transiMatrix, ndfa_sortant) ? 1 : 0);
        }

        /*
        nous : papier
		0 : 1
		1 : 2
		2 : 4
		3 : 5
		4 : 7
		5 : 8
		6 : 6
		7 : 9
		8 : 0
		9 : 3

        */
        /* depart : creation de la matrice de transitions
         * 1) creation de result en ne considerant que les lignes "importantes" : celles qui
         * ont au moins une transition non eps entrante
         * 2) remplissage de result :
         * on regarde pour chacune de ses transitions sortantes
         * 		parcours recursif de toutes les transitions sortantes
         * on renvoie la liste des etats auxquels on arrive en parcourant chaque transition
         * sortante, en ne considerant que les etats importants
         *
         * transport de l'info sur l'etat final : parcours recursif de toutes les eps transitions, si une
         * amene a un etat final, on remonte l'info et on devient final
         *
         *
         */



        System.out.println("\ndfa : ");
        (new EPSndfa()).printAutomatonMatrix(result);


        return result;

    }

    private boolean getFinal(Integer ligne_num, int[][] transiMatrix,
                             HashMap<Integer, ArrayList<ArrayList<Integer>>> ndfa_sortant) {
        int sumTM = 0;
        for(int i : transiMatrix[ligne_num]) sumTM += i;
        if(sumTM == 1) {
            return ndfa_sortant.get(ligne_num).get(258).get(0) == 1;
        }

        for(int i = 0; i < transiMatrix.length; i++) {
            if (transiMatrix[ligne_num][i] == 1 && i != ligne_num){
                if(ndfa_sortant.get(i).get(258).get(0) == 1) return true;
                if(ndfa_sortant.get(i).get(256) == null) continue;
                ArrayList<Integer> ndfasi = ndfa_sortant.get(i).get(256);
                for(int j = 0; j < ndfasi.size(); j++) {
                    if(ndfasi.get(j) != ligne_num) {
                        boolean res0 = getFinal(ndfasi.get(j), transiMatrix, ndfa_sortant);
                        if(res0) return true;
                    }
                }
            }
        }

        return false;
    }

    private HashMap<Integer, Integer> getTransis(Integer ligne_num, int[][] transiMatrix,
                                                 HashMap<Integer, ArrayList<ArrayList<Integer>>> ndfa_sortant) {
        HashMap<Integer,Integer> res = new HashMap<>();
        for(int i = 0; i < transiMatrix.length; i++) {
            if (transiMatrix[ligne_num][i] == 1){
                int val = -1;
                int key = -1;
                for(int j = 0; j < ndfa_sortant.get(ligne_num).size(); j++) {
                    if(j == 257 || j == 258) continue;
                    if(ndfa_sortant.get(ligne_num).get(j) != null) {
                        for(Integer elt : ndfa_sortant.get(i).get(j)) {
                            val = j;
                            key = elt;
                            if(val == 256 ) {
                                if(ligne_num != i)
                                    res.putAll(getTransis(i, transiMatrix, ndfa_sortant));
                            } else {
                                res.put(key, val);
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