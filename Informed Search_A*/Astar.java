import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Astar {
    private int[] data = new int[500000];//handle all the data from txt file
    private int[] startvertex = new int[100000];
    private int[] endvertex = new int[100000];
    private int[] distance = new int[100000];
    private int[][] graph = new int[10000][10000];
    private int MAX_D = 9999;

    private Node[] List = new Node[10000];//store the node, in order to put in priority queue

    private int size;
    private int size2;


    public Astar(String file, String file2) throws IOException {

        Path path = Paths.get(file);
        Scanner scanner = new Scanner(path).useDelimiter("\\D");
        int p = 0;
        while (scanner.hasNext()) {
            String line = scanner.next();
            if (!line.equals("")) {
                data[p] = Integer.valueOf(line);
                p++;
            }
        }
        this.size = p / 3;

        int s, e, d;
        s = e = d = 0;
        for (int j = 0; j < p; j++) {// get the existing edges
            if (j % 3 == 0) {
                startvertex[s] = data[j];
                s++;
            } else if (j % 3 == 1) {
                endvertex[e] = data[j];
                e++;
            } else {
                distance[d] = data[j];
                d++;
            }
        }

        Path path2 = Paths.get(file2);
        Scanner scanner2 = new Scanner(path2).useDelimiter("\\D");
        p = 0;
        while (scanner2.hasNext()) {
            String line2 = scanner2.next();
            if (!line2.equals("")) {
                data[p] = Integer.parseInt(line2);
                p++;
            }
        }
        size2 = p / 2;
        int k1 = 0;
        for (int k = 0; k < p; k++) { // record vertices and which square they are
            if (k % 2 != 0) {
                List[k1] = new Node(k1, data[k]);
                k1++;
            }
        }

        for (int i = 0; i < size2; i++)
            for (int j = 0; j < size2; j++) {
                graph[i][j] = MAX_D; // initialize the graph
            }

        for (int i = 0; i < size; i++) {
            graph[startvertex[i]][endvertex[i]] = graph[endvertex[i]][startvertex[i]] = distance[i]; // use the data from input file
        }
        for (int i = 0; i < size2; i++)
            for (int j = 0; j < size2; j++) {
                if (graph[i][j] != MAX_D) List[i].addNeighbour(List[j], graph[i][j]);
            }
    }

    static class Edge {
        public final int cost;
        public final Node target;

        public Edge(Node target, int cost) {
            this.target = target;
            this.cost = cost;
        }
    }

    static class Node {
        public final int num;
        public final int pos;

        // cost so far to reach destination
        public int g_cost = Integer.MAX_VALUE;
        // total estimated cost of path through current node
        public int f_cost = Integer.MAX_VALUE;
        // estimated cost from this node to destination
        //public int h_cost;

        public ArrayList<Edge> adjacency = new ArrayList<>();
        public Node parent;

        public Node(int num, int pos) {
            this.num = num;
            this.pos = pos;
        }

        public void addNeighbour(Node neighbour, int cost) {
            Edge edge = new Edge(neighbour, cost);
            adjacency.add(edge);
        }

    }


    public void AStarSearch(Node source, Node destination) {
        int dist = 0;
        source.g_cost = 0;
        source.f_cost = Heuristic(source.pos, destination.pos); // because g_cost is 0 for the source node
        PriorityQueue<Node> frontier = new PriorityQueue<>(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Double.compare(o1.f_cost, o2.f_cost);
            }
        });
        frontier.add(source);
        int numberOfSteps = 0;
        while (!frontier.isEmpty()) {
            numberOfSteps++;
            // get the node with minimum distance
            Node current = frontier.poll();
            if (current.num == destination.num) {
                break;
            }
            // check all the neighbours
            for (Edge edge : current.adjacency) {
                Node neigh = edge.target;
                int cost = edge.cost;
                int new_g_cost = current.g_cost + cost;
                int new_f_cost = Heuristic(neigh.pos, destination.pos) + new_g_cost; // the f value
                if (new_f_cost < neigh.f_cost) {
                    neigh.parent = current;
                    neigh.g_cost = new_g_cost;
                    neigh.f_cost = new_f_cost;
                    if (frontier.contains(neigh)) {
                        frontier.remove(neigh);
                    }
                    frontier.add(neigh);
                }
            }
        }
        System.out.println("Number of steps: " + numberOfSteps + "\n" + "the total distance of this path: " + destination.g_cost);

    }

    public int Heuristic(int Square1, int Square2) {
        int i1, j1, i2, j2;
        i1 = Square1 / 10;
        j1 = Square1 % 10;
        i2 = Square2 / 10;
        j2 = Square2 % 10;
        return (int)Math.sqrt((j2 - j1) * (j2 - j1) + (i2 - i1) * (i2 - i1));

    }


    public static void printPath(Node target){
        List<Node> path = new ArrayList<Node>();

        for(Node node = target; node!=null; node = node.parent){
            path.add(node);
        }

        Collections.reverse(path);
        System.out.println("the path is: ");

        for(Node node:path){
            System.out.println(node.num + " ");
        }

        //return path;
    }



    public static void main(String[] args) throws IOException {
        final long startTime = System.nanoTime();
        Random r = new Random();

        if (args.length == 0) { // manual input or use random number when there's no input from command
            int a = 2;//r.nextInt(100);
            int b = 14;//r.nextInt(100);
            System.out.println("the start vertex is: " + a + " the end vertex is: " + b);
            Astar t = new Astar("e.txt", "v.txt");
            t.AStarSearch(t.List[a], t.List[b]);
            printPath(t.List[b]);
            final long duration = System.nanoTime() - startTime;
            System.out.println("The running time for this algorithm is: " + duration/1000000 + "ms");
            return;
        }

        if(args.length != 4) System.out.println("please check your input format, you can find it in README file");
        int source = Integer.parseInt(args[0]);
        int destination = Integer.parseInt(args[1]);
        String file1 = args[2];
        String file2 = args[3];
        Astar Test = new Astar(file1,file2);

        System.out.println("the start vertex is: " + source + " the end vertex is: " + destination);
        Test.AStarSearch(Test.List[source],Test.List[destination]);
        printPath(Test.List[destination]);
        final long duration = System.nanoTime() - startTime;
        System.out.println("The running time for this algorithm is: " + duration/1000000 + "ms");


    }
}



