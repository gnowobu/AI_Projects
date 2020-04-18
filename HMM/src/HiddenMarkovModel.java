public class HiddenMarkovModel {
    public double[] C; // Initial distribution
    public double[][] B; // Observation/Emission matrix (probability)
    public double[][] A; // Transition matrix (state transition model)
    public int[] observation;

    public HiddenMarkovModel(double[] C, double[][] B, double[][] A){
        this.A = A;
        this.B = B;
        this.C = C;
        //this.observation = observation;
    }

    public HiddenMarkovModel(double[][] B, double[][] A){
        this.A = A;
        this.B = B;
    }

    public void setC(double[] c) {
        C = c;
    }

    public void modelSpecification(){
        System.out.println("States for this HHM: Dice1, Dice2, Dice3 (D1,D2,D3)");
        System.out.println("The transition matrix A: ");
        printMatrix(A);
        System.out.println("The emission matrix B: ");
        printMatrix(B);
        System.out.println("The given observation is: ");
        printObservation();

    }

    public void printMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++){
                System.out.printf("%.2f", matrix[i][j]);
                System.out.print(" ");
            }
            System.out.print("\n");
        }
    }

    public void printObservation(){
        for(int i = 0; i < observation.length; i++){
            System.out.print(observation[i] + " ");
        }
    }
}
