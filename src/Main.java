import java.util.Scanner;

public class Main {
    public static void main(String [] arg){

        // === === === 1e etape === === === // 
            
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
            
            RegExTree ret = RegEx.getRegExTree(regEx);

        }

        System.out.println("  >> ...");
        System.out.println("  >> Parsing completed.");
        System.out.println("Wait Mr. Anderson. .. processing ....");


        // === === === 2e etape === === === // 
        
        
    }
    
}
