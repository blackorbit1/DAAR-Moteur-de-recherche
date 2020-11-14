import java.util.ArrayList;
import java.util.HashMap;

public class MultiCPUProcess extends Thread {
    private ArrayList<String> texte;
    private String choice;

    HashMap<Integer, ArrayList<ArrayList<Integer>>> dfa;

    private boolean resultat_bool;
    private ArrayList<String> resultat_lignes;
    private HashMap<Integer, ArrayList<Couple>> resultat_lignes_pos;


    boolean getResultBool() {
        return resultat_bool;
    }
    ArrayList<String> getResultatLignes() {
        return resultat_lignes;
    }
    HashMap<Integer, ArrayList<Couple>> getResultatLignesPos(){
        return resultat_lignes_pos;
    }

    MultiCPUProcess (ThreadGroup tg, String name, ArrayList<String> texte, String choice, HashMap<Integer, ArrayList<ArrayList<Integer>>> dfa) {
        super(tg,name);

        this.texte = texte;
        this.choice = choice;

        this.dfa = dfa;
    }
    public void run() {
        //String regex = "mama";
        String regex = KMP.getRegexFromDFA(dfa);

        if(regex != null){
            int [] kmp = KMP.getCarryOver(regex);
            if(choice.equals("resultat_bool")) resultat_bool = KMP.contient(regex, kmp, texte);
            if(choice.equals("resultat_lignes")) resultat_lignes = KMP.contientWithLignes(regex, kmp, texte);
            if(choice.equals("resultat_lignes_pos")) resultat_lignes_pos = KMP.contientWithLignesEtPos(regex, kmp, texte);
        } else {
            if(choice.equals("resultat_bool")) resultat_bool = new Containsdfa().contient(dfa, texte);
            if(choice.equals("resultat_lignes")) resultat_lignes = new Containsdfa().contientWithLignes(dfa, texte);
            if(choice.equals("resultat_lignes_pos")) resultat_lignes_pos = new Containsdfa().contientWithLignesEtPos(dfa, texte);
        }
    }
}
