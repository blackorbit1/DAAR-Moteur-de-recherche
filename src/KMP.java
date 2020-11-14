import java.util.ArrayList;
import java.util.HashMap;

public class KMP {
    public static int[] getCarryOver(String regex){
        int carryover[] = new int[regex.length() + 1];

        /* Etapes de construction

        1e boucle : le nb de lettres qu’on peut avoir en allant de cette lettre vers les lettres de gauche où le préfixe est égale au suffixe (où la fin est la lettre où on est)
        2e boucle : remplacer les 0 par des -1 si la lettre est la meme que la 1e du regex en regardant la case juste au dessus
        3e boucle : On regarde la case n°(ce qu’on a mis dans le carry over) et si c’est un -1 on remplace par un -1

        A noter : la premiere case n'est pas comptée et vaut toujours -1

        */

        carryover[0] = -1;

        // 1ere boucle
        for(int i = 1; i < regex.length(); i++){
            int longest_prefix_equal_suffix = 0;
            for(int j = 1; j < i; j++){
                if(regex.substring(0, j).equals(regex.substring(i-j, i))) longest_prefix_equal_suffix = j;
            }
            /*if(longest_prefix_equal_suffix != 1) erreur sujet */ carryover[i] = longest_prefix_equal_suffix;
        }

        // 2e boucle
        for(int i = 1; i < regex.length(); i++){
            if(carryover[i] == 0 && regex.charAt(i) == regex.charAt(0)) carryover[i] = -1;
        }

        // 3e boucle
        for(int i = 1; i < regex.length(); i++){
            if(carryover[i] != -1 && carryover[i] != 0 && carryover[carryover[i]] == -1) carryover[i] = -1;
        }

        return carryover;
    }

    public static String getRegexFromDFA(HashMap<Integer, ArrayList<ArrayList<Integer>>> dfa){
        // on recupere l'etat initial
        int etatI = -1;
        Integer etatCourant = -1;
        for(Integer k : dfa.keySet()) {
            // recherche de l'etat initial pour s'y placer
            if(dfa.get(k).get(257).get(0) == 1) {
                etatCourant = k;
                etatI = k;
                break;
            }
        }
        if(etatI == -1){
            System.err.println("Etat initial non trouvé !");
            return null;
        }

        ArrayList<Integer> lettres_regex = new ArrayList<Integer>();

        while(true){
            ArrayList<ArrayList<Integer>> noeud = dfa.get(etatCourant);
            //System.out.println("noeud : " + noeud);
            int nb_sortants = 0;
            for(int i = 0; i < 257; i++){
                if(nb_sortants > 1) return null;
                if(noeud.get(i) != null && noeud.get(i).size() > 0){
                    nb_sortants++;
                    lettres_regex.add(i);
                    etatCourant = noeud.get(i).get(0);
                }
            }
            if(nb_sortants == 0) return null; // dans le cas où le seul sortant serait un point
            if(noeud.get(258).get(0) == 1) break;
        }

        char [] regex = new char[lettres_regex.size()];
        for(int i = 0; i < lettres_regex.size(); i++){
            regex[i] = (char) lettres_regex.get(i).intValue();
        }

        return new String(regex);
    }

    public static boolean contient(String regex, int[] carryover, ArrayList<String> text) {
        for(int i = 0; i < text.size(); i++){
            String ligne = text.get(i);

            for(int position_texte = 0; position_texte < (ligne.length() - regex.length() + 1); position_texte++){
                boolean match = true;
                //System.out.print("\nposition texte : " + position_texte + " /// ");
                for(int position_regex = 0; position_regex < regex.length(); position_regex++){
                    //System.out.print(position_regex + " ");
                    if(regex.charAt(position_regex) != ligne.charAt(position_texte + position_regex)){
                        position_texte = (position_texte + position_regex) - carryover[position_regex] - 1;
                        match = false;
                        break;
                    }
                }
                if(match) return true;

            }
        }

        return false;
    }

    public static ArrayList<String> contientWithLignes(String regex, int[] carryover, ArrayList<String> text) {
        ArrayList<String> result = new ArrayList<>();

        for(int i = 0; i < text.size(); i++){
            String ligne = text.get(i);
            boolean match = true;


            for(int position_texte = 0; position_texte < (ligne.length() - regex.length() + 1); position_texte++){
                match = true;
                //System.out.print("\nposition texte : " + position_texte + " /// ");
                for(int position_regex = 0; position_regex < regex.length(); position_regex++){
                    //System.out.print(position_regex + " ");
                    if(regex.charAt(position_regex) != ligne.charAt(position_texte + position_regex)){
                        position_texte = (position_texte + position_regex) - carryover[position_regex] - 1;
                        match = false;
                        break;
                    }
                }
                if(match) break;

            }

            if(match) result.add(ligne);
        }

        return result;
    }

    public static HashMap<Integer, ArrayList<Couple>> contientWithLignesEtPos(String regex, int[] carryover, ArrayList<String> text) {
        HashMap<Integer, ArrayList<Couple>> result = new HashMap<>();

        for(int i = 0; i < text.size(); i++){
            String ligne = text.get(i);

            ArrayList<Couple> positions_succes = new ArrayList<>();

            for(int position_texte = 0; position_texte < (ligne.length() - regex.length() + 1); position_texte++){
                boolean match = true;
                //System.out.print("\nposition texte : " + position_texte + " /// ");
                for(int position_regex = 0; position_regex < regex.length(); position_regex++){
                    //System.out.print(position_regex + " ");
                    if(regex.charAt(position_regex) != ligne.charAt(position_texte + position_regex)){
                        position_texte = (position_texte + position_regex) - carryover[position_regex] - 1;
                        match = false;
                        break;
                    }
                }
                if(match) positions_succes.add(new Couple(position_texte, position_texte + regex.length()));

            }

            if(positions_succes.size() > 0) result.put(i, positions_succes);
        }

        return result;
    }


}

