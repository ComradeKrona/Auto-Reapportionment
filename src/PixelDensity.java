import java.util.Arrays;

public class PixelDensity {

    private int population;
    private boolean inDistrict;
    private int districtNumber;

    public PixelDensity(){
        this(0);
    }

    public PixelDensity(int population){
        this.population = population;
        inDistrict = false;
        districtNumber = -1;
    }

    public int getPopulation() {
        return population;
    }

    public boolean isInDistrict() {
        return inDistrict;
    }

    public void setInDistrict(boolean inDistrict) {
        this.inDistrict = inDistrict;
    }

    public int getDistrictNumber() {
        return districtNumber;
    }

    public void setDistrictNumber(int districtNumber) {
        this.districtNumber = districtNumber;
    }

    public void setPopulation(int pop) {
        population = pop;
    }

    @Override
    public String toString() {
        return (this.districtNumber) + "";
    }
}

