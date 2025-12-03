package assignments.ex2.ex2_sol;

import java.util.ArrayList;

public class Range2D {

    private Index2D _first;
    private Index2D _second;

    public Range2D (Index2D a, Index2D b){

        _first = new CellEntry(a.getX(), a.getY());
        _second = new CellEntry(b.getX(), b.getY());

    }
    //returns an array of the relevant indexes
    public ArrayList<Index2D> getCells (){
        ArrayList<Index2D> Cells = new ArrayList<>();

        //pick the right range of cells to add, independently to their insertion order.
        int minX = Math.min(_second.getX(), _first.getX());
        int maxX = Math.max(_second.getX(), _first.getX());
        int minY = Math.min(_second.getY(), _first.getY());
        int maxY = Math.max(_second.getY(), _first.getY());

        for (int i = minX ; i <= maxX; i++) {       // gets all the relevant cells into the list
            for(int j = minY ; j<= maxY ; j++){
                Index2D tempCell = new CellEntry(i,j);
                Cells.add(tempCell);
            }
        }
        return Cells;
    }

    @Override
    public String toString() {
        String ans = "";
        for (Index2D i: this.getCells()){
            ans += "("+i.getX()+","+ i.getY()+") | ";
        }
        ans = ans.substring(0,ans.length()-3);
        return ans;
    }
}