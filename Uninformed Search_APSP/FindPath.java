import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EventListener;
import java.util.Random;
import java.util.Scanner;


public class FindPath {
    // A utility function to find the vertex with minimum distance value, 
    // from the set of vertices not yet included in shortest path tree
    private int[] data = new int[1000000];//handle all the data from txt file
    private int[] startvertex = new int[100000];
    private int[] endvertex = new int[100000];
    private int[] distance = new int[100000];
    private int size;
    private int initsize = 2000;
    private int MAX_D = 999999;
    private int[][] graph = new int[initsize][initsize];
    private int[][] dist = new int[initsize][initsize];
    public int[][] next = new int[initsize][initsize];


    public FindPath(String file) throws IOException {

        Path path = Paths.get(file);
        Scanner scanner = new Scanner(path).useDelimiter("\\D");
        int i = 0;
        while(scanner.hasNext()){
            String line = scanner.next();
            if(!line.equals("")){
                data[i] = Integer.valueOf(line);
                i++;
            }
        }
        size = i / 3;

        int s, e, d;
        s = e = d = 0;
        for(int j = 0; j < i ; j++){
            if(j % 3 == 0){
                startvertex[s] = data[j];
                s++;
            }
            else if(j % 3 == 1){
                endvertex[e] = data[j];
                e++;
            }
            else{
                distance[d] = data[j];
                d++;
            }
        }
    }

    public void APSP(int SVertex, int EVertex){

        for(int i = 0; i < initsize; i++)
            for(int j = 0; j < initsize; j++){
                graph[i][j] = MAX_D; // initialize the graph
                next[i][j] = i;
            }

        for(int i = 0; i < initsize; i++) graph[this.startvertex[i]][this.endvertex[i]] = graph[endvertex[i]][startvertex[i]] = this.distance[i]; // use the data from input file

        for(int i = 0; i < initsize; i++)
            for(int j = 0; j < initsize; j++){
                dist[i][j] = graph[i][j];

                if (i == j)
                    next[i][j] = -1;
                else if (graph[i][j] != MAX_D)
                    next[i][j] = i;
            }

        for (int k = 0; k < initsize; k++)
        {
            // Pick all vertices as source one by one
            for (int i = 0; i < initsize; i++)
            {
                // Pick all vertices as destination for the
                // above picked source
                for (int j = 0; j < initsize; j++)
                {
                    // If vertex k is on the shortest path from
                    // i to j, then update the value of dist[i][j]
                    if (dist[i][k] + dist[k][j] < dist[i][j]){

                        dist[i][j] = dist[i][k] + dist[k][j];
                        next[i][j] = next[k][j];

                    }
                }
            }
        }
        PrintPath(SVertex, EVertex, next);


        return;
    }

    public void PrintPath(int v, int u, int[][]next){
        if (next[v][u] == v)
            return;

        PrintPath(v, next[v][u], next);
        System.out.println(next[v][u] + " ");

    }

    public static void main(String[] args) throws IOException {
        final long startTime =  System.nanoTime();
        int SVertex, EVertex;
        Random r =  new Random();
        if(args.length == 0) {
            SVertex = r.nextInt(100);
            EVertex = r.nextInt(100);
            //SVertex = 0; // you can manually input the data here
            //EVertex = 20;
            FindPath apsp = new FindPath("e.txt");

            System.out.println("the start vertex is: " + SVertex + " the end vertex is: " + EVertex);

            System.out.print("the path is:\n" + SVertex + "\n");
            apsp.APSP(SVertex, EVertex);
            System.out.print(EVertex + "\n");

            System.out.println("the total distance of this path : " + apsp.dist[SVertex][EVertex]);

            final long duration = System.nanoTime() - startTime;
            System.out.println("The running time for this algorithm is: " + duration/1000000 + "ms");
            return;
        }
        //below is for running on terminal

        SVertex = Integer.parseInt(args[0]);
        EVertex = Integer.parseInt(args[1]);
        String FileName = args[2];

        FindPath apsp = new FindPath(FileName);

        System.out.println("the start vertex is: " + SVertex + " the end vertex is: " + EVertex);
        System.out.print("the path is:\n" + SVertex + "\n");
        apsp.APSP(SVertex, EVertex);
        System.out.print(EVertex + "\n");
        System.out.println("the total distance of this path is: " + apsp.dist[SVertex][EVertex]);

        final long duration = System.nanoTime() -  startTime;
        System.out.println("The running time for this algorithm is: " + duration/1000000 + "ms");
        return;
    }
}