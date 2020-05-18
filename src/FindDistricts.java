import java.awt.*;
import java.util.*;

public class FindDistricts {

    private int[] sumsArr;
    private int numDists;
    private int pplInDist;
    public int population;
    public int size;
    private Integer[][] map;
    public Map<Integer, String[]> percentage;

    public PixelDensity[][] pixelMap;

    private District[] districts = new District[numDists];

    public ArrayList<District> dists = new ArrayList<>();


    public FindDistricts(Integer[][] mapArr, int districts) {

        numDists = districts;

        map = mapArr;

        size = mapArr.length;

        createSumsArr(mapArr);

    }


    class Coord {

         int x, y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class District {
        private Coord topLeft;
        private Coord topRight;
        private Coord bottomLeft;
        private Coord bottomRight;
        private int pop;
        private Rectangle rect;


        //kept for the half dists we might need DONT USE
        public District(Coord topLeft, Coord topRight, Coord bottomLeft, Coord bottomRight) {
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
        }

        public District(Coord topLeft, Coord topRight, Coord bottomLeft, Coord bottomRight, int pop) {
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
            this.pop = pop;
            rect = new Rectangle(topLeft.x, topLeft.y, topRight.x - topLeft.x, bottomLeft.y - topLeft.y);
        }

        public int[] getTopLeft() {
            return new int[]{topLeft.x, topLeft.y};
        }

        public int[] getTopRight() {
            return new int[]{topRight.x, topRight.y};
        }

        public int[] getBottomLeft() {
            return new int[]{bottomLeft.x, bottomLeft.y};
        }

        public int[] getBottomRight() {
            return new int[]{bottomRight.x, bottomRight.y};
        }

        public Rectangle getRectangle() {
            return rect;
        }

        public int getPop() {
            return pop;
        }

        @Override
        public String toString() {
            return "" + Arrays.toString(getTopLeft()) + " " +
                    Arrays.toString(getTopRight()) + " " +
                    Arrays.toString(getBottomLeft()) + " " +
                    Arrays.toString(getBottomRight()) + " Pop: " +
                    getPop();
        }
    }

    /**
     * Creates an array of the sums of the colums in the 2d array so that searching for 20% is quicker
     * @param mapArr
     */
    private void createSumsArr(Integer[][] mapArr) {

        sumsArr = new int[mapArr.length];

        for (int i = 0; i < sumsArr.length; i++) {
            int sum = 0;
            for (int j = 0; j < mapArr[i].length; j++) {
                sum += mapArr[i][j];
            }
            sumsArr[i] = sum;
            pplInDist += sum;
        }

        population = pplInDist;
        pplInDist = pplInDist/numDists;

        //checkForBigColumns(mapArr);

        findColDists(mapArr);
    }


    //left to right finds districts with size close enough to perfect
    // if one is too big, next one will be made smaller
    //last is autofilled with the rest of the state

    private void checkForBigColumns(int[][] mapArr) {
        for (int i = 0; i < sumsArr.length; i++) {
            if (sumsArr[i] > pplInDist) {
                int sum = 0;
                for (int j = 0; j < mapArr.length; j++) {
                    sum += mapArr[i][j];
                    if (sum > pplInDist) {
                        for (int k = 0; k < mapArr[i].length; k++) {
                            sum -= mapArr[i][k];
                            if (sum < pplInDist) {
                                dists.add(new District(new Coord(i,k), new Coord(i,k), new Coord(i,j), new Coord(i,j)));
                            }
                        }
                    }
                }
            }
        }
    }

    private void findColDists(Integer[][] mapArr) {

        int sum = 0;
        int startCol = 0;
        int nextDistSize = pplInDist;
        int distsMade = 0;
        int popUsed = 0;

        for (int i = 0; i < sumsArr.length; i++) {

            sum += sumsArr[i];

            if (sum > nextDistSize) {
                dists.add( new District(
                                new Coord(startCol,0),
                                new Coord(i + 1,0),
                                new Coord(startCol, mapArr[startCol].length),
                                new Coord(i, mapArr[i].length),
                                sum));
                distsMade++;
                popUsed += sum;
                nextDistSize = pplInDist*2 - sum;
                sum = 0;
                startCol = i + 1;
                if (distsMade == numDists - 1) {
                    dists.add( new District(
                            new Coord(i + 1,0),
                            new Coord(sumsArr.length,0),
                            new Coord(i + 1, mapArr[i].length),
                            new Coord(sumsArr.length - 1, mapArr[sumsArr.length - 1].length),
                            population - popUsed));
                }
            }
        }
        printDists();
        printAnalysis();
        printPixelMap();
    }

    private void printDists() {
        for (int i = 0; i < dists.size(); i++) {
            System.out.println(dists.get(i));
        }
    }

    private void printAnalysis() {
        percentage = new HashMap<>();

        System.out.println("POPULATION: " + population);
        for (int i = 0; i < dists.size(); i++) {
            System.out.println("---------- DISTRICT " + i + " ----------");
            System.out.print("PERCENTAGE OF POPULATION: ");
                System.out.printf("%2.00f",(double)dists.get(i).getPop()/population*100);
                System.out.println("%");
            System.out.print("PERCENTAGE OF LAND: ");
                System.out.printf("%1.00f", (double)(size*(dists.get(i).getTopRight()[0] - dists.get(i).getTopLeft()[0]))/(size*size) * 100);
                System.out.println("%");
            percentage.put(i, new String[]{String.format("%2.00f",(double)dists.get(i).getPop()/population*100) + "%" , String.format("%1.00f", (double)(size*(dists.get(i).getTopRight()[0] - dists.get(i).getTopLeft()[0]))/(size*size) * 100) + "%"});
        }

    }

    private void printPixelMap() {
        //System.out.println("\n\n\n\n\n");

        PixelDensity[][] mapOfPixels = new PixelDensity[size][size];
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                mapOfPixels[i][j] = new PixelDensity(this.map[i][j]);
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {

                mapOfPixels[i][j].setDistrictNumber(inbounds(i, j));

            }
        }

//        for(int i = 0; i < mapOfPixels.length; i++) { // prints final map with districts
//            System.out.println(Arrays.toString(mapOfPixels[i]));
//        }

        pixelMap = mapOfPixels;

    }

    private int inbounds(int row, int col) {
        for (int i = 0; i < dists.size(); i++) {
            if (dists.get(i).getRectangle().contains(col,row)) {
                return i;
            }
        }
        return 111;
    }
}

