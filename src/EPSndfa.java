public class EPSndfa {



    public static ArrayList<ArrayList<Couple>> (RegExTree tree){
        return null;
    }







    private class Couple{
        public int first;
        public int second;
        private int nbElem;

        public Couple(){
            nbElem = 0;
        }
        public Couple(int i){
            first = i;
            nbElem = 1;
        }
        public Couple(int i, int j){
            first = i;
            second = j;
            nbElem = 2;
        }

        public void addElem(int elt){
            if(nbElem == 2) throw new Exception("Impossible d'ajouter un element");
            if(nbElem == 1){
                second = elt;
            } else {
                first = elt;
            }
            nbElem++;
        }
        
        public int getNbElem(){
            return nbElem;
        }
    }
}
