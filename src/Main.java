import java.util.Scanner;
import java.util.HashMap;
import java.io.File;
import java.io.FileNotFoundException;
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
  					}
  					else {
  						throw new Exception("arguments invalides");
  					}
  				}
  			}
  			else {
  				for(int j = 1; j < eltL.length; j++) {
  					if(eltL[j] == 'f') {
  						if(seenF) throw new Exception("argument F present plusieurs fois");
  						seenF = true;
  						res[2] = 1;
  					}
  					else {
  						if(eltL[j] == 'v') {
  							if(seenV) throw new Exception("argument V present plusieurs fois");
  							seenV = true;
  							res[3] = 1;
  						}
  						else {
  							if(eltL[j] == 'p') {
  								if(seenP && okP) throw new Exception("argument P present plusieurs fois");
  								seenP = true;  								
  							}
  							else {
  								if(Character.isDigit(eltL[j])) {
  									int value = Character.getNumericValue(eltL[j]);
  									if(!seenP || value < 1 || value > 3) throw new Exception("argument numerique incorrect");
  									okP = true;
  									res[4] = value;
  								}
  								else {
  									throw new Exception("argument invalide");
  								}
  							}
  						}
  					}
  				}
  			}
  		}
  		
    	return res;
  		
    }
    
    // 0 : position de la regex dans la liste des arguments
    // 1 : position du texte a lire dans la liste des arguments
    // 2 : 1 si on doit lire un fichier, 0 si on lit le texte en entree
    // 3 : 1 si verbose, 0 sinon
    // 4 : niveau de precision, dans {1,2,3} 
    
    public static void main(String [] args){

        // === === === 1e etape === === === // 

		RegExTree ret = null;
		
		

		int[] parsedArgs = new int[5];
		
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
		
		if(verbose) {
	        System.out.print("  >> ASCII codes: ["+(int)regEx.charAt(0));
	        for (int i=1;i<regEx.length();i++) System.out.print(","+(int)regEx.charAt(i));
	        System.out.println("].");
		}
        ret = RegEx.getRegExTree(regEx);
        HashMap<Integer,  ArrayList<ArrayList<Integer>>> res = new HashMap<>();
        try { 
            EPSndfa epsndfa = new EPSndfa();
            EPSdfa epsdfa = new EPSdfa();
            Reducdfa reducdfa = new Reducdfa();
            
            HashMap<Integer, ArrayList<Couple>> res0 = epsndfa.getEpsNDFA(ret);
            
            if(verbose) {
            	System.out.println("\nndfa : ");
                epsndfa.printAutomatonMatrix_old(res0);
            }
            HashMap<Integer,  ArrayList<ArrayList<Integer>>> res1 = epsdfa.getEpsDFA(res0);
            if(verbose) {
	            System.out.println("\ndfa : ");
	            epsndfa.printAutomatonMatrix(res1);
            }
            res = reducdfa.getDFAReduit(res1);
            if(verbose) {
	            System.out.println("\ndfa reduit : ");
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
        
        
        switch(precisionLvl) {
        case 3:        	
        	//System.out.println(containsdfa.contientWithLignesEtPos(res, texte1));
        	System.out.println(containsdfa.cWLEP_toString(containsdfa.contientWithLignesEtPos(res, texte1),texte1));
        	break;
        case 2:
        	System.out.println(containsdfa.cWL_toString(containsdfa.contientWithLignes(res, texte1)));
        	break;
        default:
        	System.out.println(containsdfa.contient(res, texte1) ? 1 : 0);
        }
        
        
        
        
		/*
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

        try { 
            EPSndfa epsndfa = new EPSndfa();
            HashMap<Integer, ArrayList<Couple>> res0 = epsndfa.getEpsNDFA(ret);
            System.out.println("\nndfa sortant : ");
            epsndfa.printAutomatonMatrix_old(res0);
            HashMap<Integer,  ArrayList<ArrayList<Integer>>> res1 = (new EPSdfa()).getEpsDFA(res0);
            HashMap<Integer,  ArrayList<ArrayList<Integer>>> res = (new Reducdfa()).getDFAReduit(res1);
            System.out.println("\ndfa reduit : ");
            epsndfa.printAutomatonMatrix(res);
            if(regEx.equals("a|bc*") || true) {
	            String t0A = "Sorr ros serr";	String t0B = "Ross sous terr";
	            String t1A = "So ros serr";		String t1B = "Rass sous terr";
	            String t2A = "Sa ros sarr";		String t2B = "Ross sous terr";
	            String t3A = "So ras serr";		String t3B = "bcccccccccccccc";
	            String t4A = "So ros serr";		String t4B = "cccccc";       
	            ArrayList<String> t = new ArrayList<>();
	            t.add(t0A); t.add(t0B);
	            System.out.print((new Containsdfa()).contient(res, t)+"\t"); // false
	            System.out.print((new Containsdfa()).contientWithLignes(res, t)+"\t"); // []
	            System.out.println((new Containsdfa()).contientWithLignesEtPos(res, t)); // []
	            t.clear(); t.add(t1A); t.add(t1B);
	            System.out.print((new Containsdfa()).contient(res, t)+"\t"); // true 
	            System.out.print((new Containsdfa()).contientWithLignes(res, t)+"\t"); // [1]
	            System.out.println((new Containsdfa()).contientWithLignesEtPos(res, t)); // [1]
	            t.clear(); t.add(t2A); t.add(t2B);
	            System.out.print((new Containsdfa()).contient(res, t)+"\t"); // true;
	            System.out.print((new Containsdfa()).contientWithLignes(res, t)+"\t"); // [0]
	            System.out.println((new Containsdfa()).contientWithLignesEtPos(res, t)); // [0]
	            t.clear(); t.add(t3A); t.add(t3B);
	            System.out.print((new Containsdfa()).contient(res, t)+"\t"); // true
	            System.out.print((new Containsdfa()).contientWithLignes(res, t)+"\t"); // [0,1]
	            System.out.println((new Containsdfa()).contientWithLignesEtPos(res, t)); // [0,1]
	            t.clear(); t.add(t4A); t.add(t4B);
	            System.out.print((new Containsdfa()).contient(res, t)+"\t"); // false
	            System.out.print((new Containsdfa()).contientWithLignes(res, t)+"\t"); // []
	            System.out.println((new Containsdfa()).contientWithLignesEtPos(res, t)); // []
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        
        
    }
    
}