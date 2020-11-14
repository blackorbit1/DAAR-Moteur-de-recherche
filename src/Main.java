import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;

public class Main {
    static final int CONCAT = 0xC04CA7;
    static final int ETOILE = 0xE7011E;
    static final int ALTERN = 0xA17E54;
    static final int PROTECTION = 0xBADDAD;

    static final int PARENTHESEOUVRANT = 0x16641664;
    static final int PARENTHESEFERMANT = 0x51515151;
    static final int DOT = 0xD07;

    // par defaut, utilise 2 arguments : regex et string, cherche
    // si la string contient la regex, renvoie true/false ou 1/0 pour bash

    // options : -f pour lire un fichier
    // -v pour afficher toutes les etapes
    // -pX pour la precision, x dans {1,2,3}

    // la seule condition d'utilisation est que la regex doit se situer avant le texte/fichier a lire

    // renvoie une liste des arguments :
    // 0 : position de la regex dans la liste des arguments
    // 1 : position du texte a lire dans la liste des arguments
    // 2 : 1 si on doit lire un fichier, 0 si on lit le texte en entree
    // 3 : 1 si verbose, 0 sinon
    // 4 : niveau de precision, dans {1,2,3}

    private static int[] parseArgs(String[] args) throws Exception{
        int[] res = {0, 1, 0, 0, 1};

        boolean isREfound = false;
        boolean isTextfound = false;

        boolean seenF = false;
        boolean seenV = false;
        boolean seenP = false;
        boolean okP = false;

        if(args.length < 2 || args.length > 5) throw new Exception("nombre d'arguments incorrect");

        for(int i = 0; i < args.length; i++) {
            char[] eltL = args[i].toCharArray();
            if(eltL[0] != '-') {
                if(!isREfound) {
                    res[0] = i;
                    isREfound = true;
                } else {
                    if(!isTextfound) {
                        res[1] = i;
                        isTextfound = true;
                    } else {
                        throw new Exception("arguments invalides");
                    }
                }
            } else {
                for(int j = 1; j < eltL.length; j++) {
                    if(eltL[j] == 'f') {
                        if(seenF) throw new Exception("argument F present plusieurs fois");
                        seenF = true;
                        res[2] = 1;
                    } else if(eltL[j] == 'v') {
                            if(seenV) throw new Exception("argument V present plusieurs fois");
                            seenV = true;
                            res[3] = 1;
                    } else if(eltL[j] == 'p') {
                            if(seenP && okP) throw new Exception("argument P present plusieurs fois");
                            seenP = true;
                    } else {
                        if(Character.isDigit(eltL[j])) {
                            int value = Character.getNumericValue(eltL[j]);
                            if(!seenP || value < 1 || value > 3) throw new Exception("argument numerique incorrect");
                            okP = true;
                            res[4] = value;
                        } else {
                            throw new Exception("argument invalide");
                        }
                    }
                }
            }
        }

        return res;

    }

    public static void main(String [] arg){

        // === === === 1e etape === === === //
        System.out.println("\n\n=== === === 1e etape === === ===");

        RegExTree ret = null;
            
        System.out.println("Welcome to Bogota, Mr. Thomas Anderson.");
        String regEx = "";
        if (arg.length!=0) {
            regEx = arg[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("  >> Please enter a regEx: ");
            regEx = scanner.next();
        }
        System.out.println("  >> Parsing regEx \""+regEx+"\".");
        System.out.println("  >> ...");
        
        if (regEx.length()<1) {
            System.err.println("  >> ERROR: empty regEx.");
        } else {
            System.out.print("  >> ASCII codes: ["+(int)regEx.charAt(0));
            for (int i=1;i<regEx.length();i++) System.out.print(","+(int)regEx.charAt(i));
            System.out.println("].");
            
            ret = RegEx.getRegExTree(regEx);

        }

        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");
        System.out.println("Wait Mr. Anderson. .. processing ....");


        // === === === 2e etape === === === //
        System.out.println("\n\n=== === === 2e etape === === ===");

        HashMap<Integer, ArrayList<Couple>> ndfa = null;

        try { 
            EPSndfa epsndfa = new EPSndfa();
            ndfa = epsndfa.getEpsNDFA(ret);
            epsndfa.printAutomatonMatrix_old(ndfa);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // === === === 3e etape === === === //
        System.out.println("\n\n=== === === 3e etape === === ===");

        HashMap<Integer, ArrayList<ArrayList<Integer>>> dfa = null;

        try {
            dfa = new EPSdfa().getEpsDFA(ndfa);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\ndfa : ");
        (new EPSndfa()).printAutomatonMatrix(dfa);


        // === === === 4e etape === === === //
        System.out.println("\n\n=== === === 4e etape === === ===");

        HashMap<Integer, ArrayList<ArrayList<Integer>>> reducsed_dfa = null;

        try {
            reducsed_dfa = new Reducdfa().getDFAReduit(dfa);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\ndfa : ");
        (new EPSndfa()).printAutomatonMatrix(reducsed_dfa);


        // === === === 5e etape === === === //

        ArrayList<String> texte = new ArrayList<>();
        texte.add("Bonjour à vous");

        System.out.println(new Containsdfa().contientWithLignesEtPos(reducsed_dfa, texte));

        texte.add("Avec cette ligne ça devrait marcher");

        System.out.println(new Containsdfa().contientWithLignesEtPos(reducsed_dfa, texte));




        // === === test carry over === === //

        String regex = "mamamia";

        int [] carry_over = KMP.getCarryOver(regex);

        System.out.print("[");
        for(int i = 0; i < carry_over.length; i++){
            System.out.print(carry_over[i]);
            if(i < (carry_over.length - 1)) System.out.print(", ");
        }
        System.out.print("]\n");

        regex = "mamaome";
        carry_over = KMP.getCarryOver(regex);

        System.out.print("[");
        for(int i = 0; i < carry_over.length; i++){
            System.out.print(carry_over[i]);
            if(i < (carry_over.length - 1)) System.out.print(", ");
        }
        System.out.print("]\n");

        regex = "mamaaaaaaome";
        carry_over = KMP.getCarryOver(regex);

        System.out.print("[");
        for(int i = 0; i < carry_over.length; i++){
            System.out.print(carry_over[i]);
            if(i < (carry_over.length - 1)) System.out.print(", ");
        }
        System.out.print("]\n");

        regex = "mammmmmmmome";
        carry_over = KMP.getCarryOver(regex);

        System.out.print("[");
        for(int i = 0; i < carry_over.length; i++){
            System.out.print(carry_over[i]);
            if(i < (carry_over.length - 1)) System.out.print(", ");
        }
        System.out.print("]\n");


    }
    
}
