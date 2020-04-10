public class State {
    public int i,j;
    public double current_utility;
    public double new_utility;
    boolean terminal;
    public int index;
    public int action;
    public double reward;

    public State(int i, int j, double current_utility, boolean terminal){
        this.i = i;
        this.j = j;
        this.current_utility = current_utility;
        this.terminal = terminal;
        this.index = -1;
        this.action = -1;
        this.reward = 0;

    }


}
