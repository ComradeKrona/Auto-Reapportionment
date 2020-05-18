import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;

public class Main{

    public static final int arrayMax =  250;
    public static final int districtMax = 99;

    public static void main(String args[]){
        //Initializing Frame
        JFrame frame=new JFrame("Auto Reapportionment");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setResizable(false);

        //Input Panel
        JPanel inputPanel=new JPanel();
        Dimension textField=new Dimension(100,25);

        //State Information
        JLabel stateText=new JLabel("State");
        JTextField stateSearch=new JTextField(30);
        stateSearch.setPreferredSize(textField);

        //District Information
        JLabel districtText=new JLabel("Districts");
        JTextField districtSearch=new JTextField(2);
        districtSearch.setPreferredSize(textField);

        //2D Array Size Information
        JLabel arrayText=new JLabel("2D Array Size");
        JTextField arraySearch=new JTextField(7);
        arraySearch.setPreferredSize(textField);

        //Text Area - Where Image will be Displayed
        JTextArea textArea = new JTextArea();

        //Output Panel
        JPanel outputPanel=new JPanel();

        //Error Information
        JLabel errorText=new JLabel();

        outputPanel.add(errorText);

        //Start Button
        JButton start=new JButton("Start");
        start.addActionListener(new ActionListener(){

            //When the Button is Pressed
            @Override
            public void actionPerformed(ActionEvent e){
                errorText.setText("");

                String[]input = new String[]{stateSearch.getText(),districtSearch.getText(),arraySearch.getText()};

                //Needed for image file location
                for(int i = 0; i < input[0].length(); i++){
                    if (input[0].charAt(i) == ' '){
                        input[0] = input[0].substring(0, i) + "_" + input[0].substring(i+1);
                    }
                }

                System.out.println(Arrays.toString(input));

                //Correct District Input
                if(Integer.parseInt(input[1]) > districtMax){
                    errorText.setText("Number of Districts must be greater than 0 and less than " + districtMax);
                    return;
                }

                //Correct Array Input
                if(Integer.parseInt(input[2]) > arrayMax){
                    errorText.setText("2D Array Size must be greater than 0 and equal or less than " + arrayMax);
                    return;
                }

                //Correct State Input
                try {
                     String path = new File("/states/" + input[0] + ".png").getPath();
                }catch (Exception f) {
                    errorText.setText("'" + input[0] + "' was not found in the database, please make sure you type it in correctly");
                    return;
                }

                Integer[][] populationDistribution = new Integer[Integer.parseInt(input[2])][Integer.parseInt(input[2])];

                try {
                     populationDistribution = new PopulationDistributionProducer(input[0], Integer.parseInt(input[2])).getMap();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }

                FindDistricts districts = new FindDistricts(populationDistribution, Integer.parseInt(input[1]));
                PixelDensity[][] districtMap = districts.pixelMap;

                frame.remove(textArea);

                String[] rows = new String[districtMap.length];

                for(int i = 0; i < districtMap.length; i++){
                    rows[i] = i + "";
                }

                Integer[][] newDistrictMap = new Integer[districtMap.length][districtMap[0].length];

                for(int i = 0; i < districtMap.length; i++){
                    for(int k = 0; k < districtMap[i].length; k++){
                        newDistrictMap[i][k] = districtMap[i][k].getDistrictNumber();
                    }
                }

                //info GUI
                InfoGUI gui = new InfoGUI(districts);
                frame.setLocationRelativeTo(gui);

                Integer[][] finalPopulationDistribution1 = populationDistribution;
                JTable display = new JTable(newDistrictMap, rows){

                    @Override
                    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                        Component component = super.prepareRenderer(renderer, row, column);
                        int rendererWidth = component.getPreferredSize().width;
                        TableColumnModel tableColumn = getColumnModel();

                        for(int i = 0; i < finalPopulationDistribution1.length; i++){
                            tableColumn.getColumn(i).setMinWidth(0);
                           tableColumn.getColumn(i).setPreferredWidth(525 / finalPopulationDistribution1.length);
                        }

                        //tableColumn.setPreferredWidth(525 / finalPopulationDistribution1.length);
                        return component;
                    }
                };

                display.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                display.setRowHeight(525 / populationDistribution.length);
                display.setRowMargin(1);
                //display.setShowGrid(false);

                display.setGridColor(Color.BLACK);

                display.setFillsViewportHeight(true);

                TreeMap<Integer, Color> colorMap = new TreeMap<>();

                for (int i = 0; i < newDistrictMap.length; i++) {
                    for (int k = 0; k < newDistrictMap[i].length; k++) {
                        int row = i;
                        int column = k;

                        Integer[][] finalPopulationDistribution = populationDistribution;

                        display.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                            @Override
                            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                                final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                                int cellValue = Integer.parseInt(table.getValueAt(row, column).toString());

                                boolean blacken = true;

                                if(finalPopulationDistribution[column][row] != 0) {
                                    blacken = false;
                                }

                                    /*try {
                                        if ((Integer.parseInt(table.getValueAt(row+1, column).toString()) != 0) && (Integer.parseInt(table.getValueAt(row-1, column).toString()) != 0)){
                                            blacken = false;
                                        }
                                    } catch (java.lang.ArrayIndexOutOfBoundsException e){

                                    }
                                    try {
                                        if ((Integer.parseInt(table.getValueAt(row, column+1).toString()) != 0) && (Integer.parseInt(table.getValueAt(row, column-1).toString()) != 0)){
                                            blacken = false;
                                        }
                                    } catch (java.lang.ArrayIndexOutOfBoundsException e){

                                    } */

                                    if (!blacken) {
                                        if (colorMap.get(cellValue) == null) {
                                            colorMap.put(cellValue, new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
                                        }

                                        c.setBackground(colorMap.get(cellValue));
                                    } else {
                                        c.setBackground(Color.black);
                                    }

                                return c;
                            }
                        });
                    }
                }

                frame.getContentPane().add(BorderLayout.CENTER, display);

                //Display Output Code at Bottom
                try {
                    errorText.setText(input[0] + " " + input[1] + " ("+ stateTreeMap().get(input[0]) + ")");
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        //Adding Items to Panel
        inputPanel.add(stateText);
        inputPanel.add(stateSearch);
        inputPanel.add(districtText);
        inputPanel.add(districtSearch);
        inputPanel.add(arrayText);
        inputPanel.add(arraySearch);
        inputPanel.add(start);

        //Adding Items to Frame
        frame.getContentPane().add(BorderLayout.NORTH,inputPanel);
        frame.getContentPane().add(BorderLayout.CENTER,textArea);
        frame.getContentPane().add(BorderLayout.SOUTH,outputPanel);
        frame.setVisible(true);

    }

    public static TreeMap stateTreeMap() throws FileNotFoundException {
        TreeMap<String, Integer> stateDistrictSize = new TreeMap<>();

        Scanner reader = new Scanner(new File ("stateDistrictFile"));

        while(reader.hasNextLine()){
            String[] line = reader.nextLine().split(" ");
           // System.out.println(Arrays.toString(line));

            stateDistrictSize.put(line[0], Integer.parseInt(line[1]));
        }

        return stateDistrictSize;
    }
}

class MyTableCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Color getBackground() {
        return super.getBackground();
    }
}