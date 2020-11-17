import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
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
    // -m pour multiCPU

    // la seule condition d'utilisation est que la regex doit se situer avant le texte/fichier a lire

    // renvoie une liste des arguments :
    // 0 : position de la regex dans la liste des arguments
    // 1 : position du texte a lire dans la liste des arguments
    // 2 : 1 si on doit lire un fichier, 0 si on lit le texte en entree
    // 3 : 1 si verbose, 0 sinon
    // 4 : niveau de precision, dans {1,2,3}
    // 5 : choix d'execution sur plusieurs CPU ou non (par defaut non)

    private static int[] parseArgs(String[] args) throws Exception{
        int[] res = {0, 1, 0, 0, 1, 0, 0};

        boolean isREfound = false;
        boolean isTextfound = false;

        boolean seenF = false;
        boolean seenV = false;
        boolean seenP = false;
        boolean okP = false;
        boolean seenM = false;
        boolean seenK = false;

        if(args.length < 2 || args.length > 7) throw new Exception("nombre d'arguments incorrect");

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
                    } else if(eltL[j] == 'm') {
                        if(seenM) throw new Exception("argument M present plusieurs fois");
                        seenM = true;    
                        res[5] = 1; 
                    } else if(eltL[j] == 'p') {
                        if(seenP && okP) throw new Exception("argument P present plusieurs fois");
                        seenP = true;
                    } else if(eltL[j] == 'k') {
                        if(seenK) throw new Exception("argument K present plusieurs fois");
                        seenK = true;
                        res[6] = 1;
                    } else {
                        if(Character.isDigit(eltL[j])) {
                            int value = Character.getNumericValue(eltL[j]);
                            if(!seenP || value < 1 || value > 3) throw new Exception("argument numerique incorrect");
                            okP = true;
                            res[4] = value;
                        } else {
                            System.out.println(eltL[j]);
                            throw new Exception("argument invalide");
                        }
                    }
                }
            }
        }
        if(!isREfound || !isTextfound) throw new Exception("Regex et/ou Texte non trouve");
        return res;

    }


    public static void main(String [] args){

		RegExTree ret = null;
		
		

		int[] parsedArgs = new int[6];
		
		try {
			parsedArgs =  parseArgs(args);
		} catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		String regEx = args[parsedArgs[0]];
		String texte = args[parsedArgs[1]];
		boolean file = parsedArgs[2] == 1;
		boolean verbose = parsedArgs[3] == 1;
		int precisionLvl = parsedArgs[4];
		boolean MULTI_CPU = parsedArgs[5] == 1;
        boolean KMP = parsedArgs[6] == 1;


        System.err.println("multi : " + MULTI_CPU);
        //precisionLvl = 3;
        System.err.println("precisionlvl : " + precisionLvl);
		
		if(verbose) {
            System.out.println("\n=== === === 1e etape === === ===");
	        System.out.print("ASCII codes: ["+(int)regEx.charAt(0));
	        for (int i=1;i<regEx.length();i++) System.out.print(","+(int)regEx.charAt(i));
	        System.out.println("].");
		}
        ret = RegEx.getRegExTree(regEx);
        if(verbose) {
            System.out.println("Tree result: "+ret.toString());
        }
        HashMap<Integer,  ArrayList<ArrayList<Integer>>> res = new HashMap<>();
        try { 
            EPSndfa epsndfa = new EPSndfa();
            EPSdfa epsdfa = new EPSdfa();
            Reducdfa reducdfa = new Reducdfa();
            
            HashMap<Integer, ArrayList<Couple>> res0 = epsndfa.getEpsNDFA(ret);
            
            if(verbose) {
                System.out.println("\n=== === === 2e etape === === ===");
            	System.out.println("ndfa : ");
                epsndfa.printAutomatonMatrix_old(res0);
            }
            HashMap<Integer,  ArrayList<ArrayList<Integer>>> res1 = epsdfa.getEpsDFA(res0);
            if(verbose) {
                System.out.println("\n=== === === 3e etape === === ===");
	            System.out.println("dfa : ");
	            epsndfa.printAutomatonMatrix(res1);
            }
            res = reducdfa.getDFAReduit(res1);
            if(verbose) {
                System.out.println("\n=== === === 4e etape === === ===");
	            System.out.println("dfa reduit : ");
	            epsndfa.printAutomatonMatrix(res);
	            System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }

        Containsdfa containsdfa = new Containsdfa();  
        ArrayList<String> texte1 = new ArrayList<>();
        
        if(!file) {
        	texte1.add(texte);
        }
        else {
        	try {
				File textName = new File(texte);
			    Scanner reader = new Scanner(textName);
			    while (reader.hasNextLine()) {
					texte1.add(reader.nextLine());
			    }
			    reader.close();
			} catch (FileNotFoundException e) {
				System.err.println("File not found");
				System.exit(0);
			}
        }
        
        

        // === === === 5e etape === === === //            
        if(verbose) {
        	System.out.println("\n=== === === 5e etape === === ===");
        }

        boolean resultat_bool = false;
        ArrayList<String> resultat_lignes = new ArrayList<>();
        HashMap<Integer, ArrayList<Couple>> resultat_lignes_pos = new HashMap<>();


        int np = MULTI_CPU?Runtime.getRuntime().availableProcessors():1;
        ThreadGroup tg = new ThreadGroup("main");
        List<MultiCPUProcess> sims = new ArrayList<MultiCPUProcess>();

        ArrayList<Integer> sizes = new ArrayList<Integer>();
        int tmp = (int) (texte1.size()/np);
        for(int i = 0; i < np; i++) sizes.add(new Integer(tmp));
        for(int i = (texte1.size() - tmp * np ); i > 0; i--) sizes.set(i, sizes.get(i) + 1);

        ArrayList<Integer> positions = new ArrayList<>();
        
        for (int i=0, position = 0;i<np;i++) {
            sims.add(new MultiCPUProcess(tg, "PI"+i, new ArrayList<String>(texte1.subList(position, position + sizes.get(i))), precisionLvl, res, KMP));
            positions.add(position);
            position += sizes.get(i);
        }

        int i=0;
        while (i<sims.size()){
            if (tg.activeCount()<np){ // do we have available CPUs?
                MultiCPUProcess sim = sims.get(i);
                sim.start();
                i++;
            } else {
                try {Thread.sleep(10);} /*wait 0.1 second before checking again*/
                catch (InterruptedException e) {e.printStackTrace();}
            }
        }

        // on attend que tous les preocessus soient termin�s avant de continuer
        while(tg.activeCount()>0) { // wait for threads to finish
            try {Thread.sleep(10);}
            catch (InterruptedException e) {e.printStackTrace();}
        }
        // On recupere et concatene les r�sultats
        for (i=0;i<sims.size();i++) {
            MultiCPUProcess sim = sims.get(i);
            if(precisionLvl == 1) resultat_bool = resultat_bool || sim.getResultBool();
            if(precisionLvl == 2) resultat_lignes.addAll(sim.getResultatLignes());
            if(precisionLvl == 3) {
            	HashMap<Integer, ArrayList<Couple>> resLP_o = sim.getResultatLignesPos();
            	HashMap<Integer, ArrayList<Couple>> resLP = new HashMap<>();
            	for(Integer in : resLP_o.keySet()) {
            		ArrayList<Couple> tmpLP = resLP_o.get(in);
            		resLP.put(in + positions.get(i), tmpLP);
            	}
            	resultat_lignes_pos.putAll(resLP);
            }
        }


        if(verbose) {
        	System.out.println("le resultat : \n");
        }
        switch(precisionLvl){
        case 3: 
        	System.out.println(new Containsdfa().cWLEP_toString(resultat_lignes_pos, texte1));
        	break;
        case 2:
        	System.out.println(resultat_lignes);
        	break;
        default:
        	System.out.println(resultat_bool);       		
        	
        }


    }
    
}
