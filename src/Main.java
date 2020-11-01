import java.util.Scanner;

public class Main {
    static final int CONCAT = 0xC04CA7;
    static final int ETOILE = 0xE7011E;
    static final int ALTERN = 0xA17E54;
    static final int PROTECTION = 0xBADDAD;

    static final int PARENTHESEOUVRANT = 0x16641664;
    static final int PARENTHESEFERMANT = 0x51515151;
    static final int DOT = 0xD07;
    public static void main(String [] arg){

        // === === === 1e etape === === === // 

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
            epsndfa.printAutomatonMatrix(epsndfa.getEpsNDFA(ret));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
    }
    
}
