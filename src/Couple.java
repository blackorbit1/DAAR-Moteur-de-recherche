class Couple{
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

    public void addElem(int elt) throws Exception{
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

    @Override
    public boolean equals(Object o1){
        if (this == o1) return true;
        if (o1 == null) return false;
        if (this.getClass() != o1.getClass()) return false;
        if (this.nbElem != ((Couple) o1).nbElem) return false;
        if (this.nbElem == 0) return true;
        if (this.nbElem > 1 && this.second != ((Couple) o1).second) return false;
        if (this.first != ((Couple) o1).first) return false; 
        return true ;
    }

    @Override
    public String toString(){
        return "["+(nbElem > 0 ? String.valueOf(first) : "")+(nbElem > 1 ? "," + String.valueOf(second) : "")+"]";
    }
}