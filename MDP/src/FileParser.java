import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FileParser {
    public int size;
    public double gamma;
    public double[] noise;
    public double[][] grid;
    public double[] noise_convert;

    public FileParser(String path){
        noise = new double[4];
        int k;

        String[] parameters = new String[3];//get the first three lines of input file as string
        String[][] convert = new String[][]{{""}, {""}, {"", "", "", "0"}};//the line for noise has 3-4 elements, so convert to a 2D array of String
        int i = 0;
        try {
            File file = new File(path);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                line = delNotation(line);
                if (i < 3) {//the first 3 lines are parameters
                    parameters[i] = line.trim();
                    i++;
                }

            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int j = 0; j < 3; j++) {
            convert[j] = parameters[j].split(",");//split the noise, store it to convert array
        }
        size = Integer.parseInt(convert[0][0]);
        gamma = Double.parseDouble(convert[1][0]);

        int noise_size = convert[2].length;


        for (k = 0; k < noise_size; k++) {//the size of noise could 3 or 4
            noise[k] = Double.parseDouble(convert[2][k]);
        }

        noise_convert = new double[4];

        if(noise_size == 3){

            noise_convert[0] = noise[0];
            noise_convert[1] = noise[1];
            noise_convert[2] = 0;
            noise_convert[3] = noise[2];
        }
        else if(noise_size == 4){
            noise_convert[0] = noise[0];
            noise_convert[1] = noise[1];
            noise_convert[2] = noise[3];
            noise_convert[3] = noise[2];
        }//this part if to convert the noise to a proper order as in this program it's different from the input file.



        grid = new double[size][size];

        // after knowing the size, read the file again to get the grid
        String[] temp = new String[size];//each line would be read as a string first
        String[][] gridConvert = new String[size][size];
        k = 0;
        int iteration = 0;//to skip the first 4 lines

        try{
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            if (iteration < 4) {
                String raw = scanner.nextLine();//skip
            } else {
                String raw = scanner.nextLine();
                raw = delNotation(raw);//delete notation
                temp[k] = raw.trim();
                k++;
            }
            iteration++;
        }
        scanner.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        k = 0;
        for (; k < size; k++) {
            gridConvert[k] = temp[k].split(",");//split the string
        }

        for (i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (gridConvert[i][j].equals("W")){
                    grid[i][j] = -10000;
                }
                else if (gridConvert[i][j].equals("X"))
                    grid[i][j] = 0;
                else
                    grid[i][j] = Double.parseDouble(gridConvert[i][j]);

            }
        }

    }



    public static String delNotation(String str){
        if(str.contains("#")){
            return str.substring(0,str.indexOf("#"));
        }
        return str;
    }

}
