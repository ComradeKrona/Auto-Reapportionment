import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.ArrayList;

public class InfoGUI extends JPanel {


    private int width, height;
    private int tableWidth = 450, tableHeight = 63;
    private JTable jt;
    private JFrame jf;
    private FindDistricts dists;


    public InfoGUI(FindDistricts dists) {
        this.dists = dists;
        jf = new JFrame();;
        setWidth(400);
        setHeight(dists.dists.size()*18);
        jf.setTitle("Info Table - based off: " + dists.population + " population");
        jf.setVisible(true);
        jf.add(createTable());
        jf.setSize(width, height);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    public JTable createTable(){
        ArrayList<Integer> columns = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            columns.add(i);
        }

        //jt = new JTable(addInfo(), columns.toArray());
        jt = new JTable(addInfo(), columns.toArray()){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);
                int rendererWidth = component.getPreferredSize().width;
                TableColumn tableColumn = getColumnModel().getColumn(column);
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
                return component;
            }
        };
        jt.setPreferredScrollableViewportSize(new Dimension(tableWidth, tableHeight));
        jt.setFillsViewportHeight(true);
        jt.setGridColor(Color.BLACK);
        jt.setShowGrid(true);

        return jt;
    }

    public String[][] addInfo(){
        ArrayList<ArrayList<String>> rows = new ArrayList<>();

        for(int i = 0; i < dists.dists.size(); i++) {
            ArrayList<String> values = new ArrayList<String>();
            String[] info = {"District " + i, "Percentage of Pop: " + dists.percentage.get(i)[0], "Percentage of Land: " + dists.percentage.get(i)[1]};
            for (int k = 0; k < info.length; k++) {
                values.add(info[k]);
            }
            rows.add(values);
        }

        String[][] array = new String[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            ArrayList<String> row = rows.get(i);
            array[i] = row.toArray(new String[row.size()]);
        }
        return array;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
