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
}
