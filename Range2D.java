package assignments.ex2.ex2_sol;

import java.util.ArrayList;

public class Range2D {

    private Index2D _start;
    private Index2D _end;

    public Range2D (Index2D a, Index2D b){

    }

    public ArrayList<Index2D> getCells (){
        ArrayList<Index2D> Cells = new ArrayList<>();

        //pick the right range of cells to add, independently to their insertion order.
        int minX = Math.min(_end.getX(), _start.getX());
        int maxX = Math.max(_end.getX(), _start.getX());
        int minY = Math.min(_end.getY(), _start.getY());
        int maxY = Math.max(_end.getY(), _start.getY());

        for ()// gets all the relevant cells into the list

        return Cells;
    }

}
