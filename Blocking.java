import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;

import java.util.Collection;
import java.util.Vector;

public class Blocking {
    DijkstraDistance alg;

    public Blocking(){

    }
    public  Blocking(DijkstraDistance alg){
        this.alg=alg;
    }
    /********************* Blocking nodes *********************************************/
    public Graph BlockNodes (Vector nodes , Graph g) {
        /*****************************************************************************/
        System.out.println("le nombre des noeuds  " + g.getVertexCount());
        System.out.println("le nombre des aretes  " + g.getEdgeCount());
/*****************************************************************************/
        for (Object n: nodes) {
            if (g.getNeighborCount(n)>0){
            Vector neighbors = new Vector(g.getNeighbors(n));
            for (Object v:neighbors) {
                g.removeEdge(g.findEdge(n,v));
            }
        }}
        /*****************************************************************************/
        System.out.println("le nombre des noeuds  " + g.getVertexCount());
        System.out.println("le nombre des aretes  " + g.getEdgeCount());

        /*****************************************************************************/
        return g;
    }

    /********************* Remove Rumor Overlapping nodes from source *********************/
//    public Vector MAJSource ( Vector nodes , )
    /********************* Checks if a community is infected ****************************/

    public  boolean isInfectedCommunity(Vector com, Vector src) {
        for (int i = 0; i < src.size(); i++) {
            if(com.contains(src.elementAt(i))){
                return true;
            }
        }
        return false ;
    }
    /********************* Community Graph *********************************/
    public  Graph getGraphCommunity(Vector com, Graph g)  {
        Graph<Integer, String> g1 = null;
        g1 = new UndirectedSparseGraph<Integer, String>();
        Collection ee = new Vector<>() ;
        Object e ;
        Collection vv = new Vector<>() ;
        int nbe=0;
        for (int i = 0; i < com.size(); i++) {
            g1.addVertex(Integer.parseInt(String.valueOf(com.elementAt(i))));
            Collection v = g.getNeighbors(com.elementAt(i));
            vv.add(v);
            for (Object n:v) {
                e=g.findEdge(n,com.elementAt(i));
                if(e!=null){
                    nbe++;
                    g1.addEdge(String.valueOf(nbe),Integer.parseInt(String.valueOf(com.elementAt(i))),Integer.parseInt(String.valueOf(n)));
                }
            }
        }
        return g1 ;
    }
    /********************* Infected Nodes in a Community Graph ***************************/

    public Vector getInfectedNodes (Vector community , Vector source) {
        Vector infected = new Vector();
        for (int i = 0; i < source.size(); i++) {
            if(community.contains(source.elementAt(i))){
                infected.add(source.elementAt(i));
            }
        }
        return infected ;
    }
    /********************* Get all nodes degrees ****************************************/
    public  Vector NodeDegrees (Graph g) {
        Vector degres = new Vector<>();
        int deg;
        Collection c = g.getVertices();
        for (Object n : c) {
            deg = g.degree(n);
            degres.add(deg);
        }
        return degres ;
    }
    /********************* get the highest degree in the Graph **************************/

    public int HighestDegree(Vector degres){
        int highest_degree=(int) degres.elementAt(0);
        for (int i =0; i < degres.size(); i++) {
            if ((Integer)degres.elementAt(i)>highest_degree){
                highest_degree=(int)degres.elementAt(i);
            }}
        return  highest_degree;
    }
    /********************* get the degree of the Graph **********************************/

    public float GlobalDCentrality(Graph g) {
        // Variables Declaration
        int  ecart;
        long total_vertices,somme_degree=0;
        float graph_DC=0;  //graph degree centrality
        int highest_degree=HighestDegree(NodeDegrees(g));
//        System.out.println("the highest degree is:"+highest_degree);
        for(Object i:g.getVertices()){
            ecart=highest_degree-(g.degree(i));
            somme_degree=somme_degree+ecart;
        }
        total_vertices=(g.getVertexCount()-1)*(g.getVertexCount()-2);
        float d=((float)somme_degree/(float)total_vertices);
        graph_DC= (float) ((double)Math.round(d * 1000) / 1000);
//        System.out.println("degree centrality: "+ graph_DC);
        return graph_DC;
    }

    /********************* get the importance of an edge *********************************/
    public  float EdgeImportance(Graph g , Object u , Object v,float hg){
        int d1 = g.degree(u);
        int d2 = g.degree(v);
        float EI = (float) Math.min(d1 ,d2)/hg;
        return  EI ;
    }

    /********************* Checks if an edg is important or not ***************************/
    public boolean isImportantEdge(float EI ,float GDC){
        return EI >= GDC;
    }
    /********************* Returns the destination node that have the best edge importance value ***************************/
    public Object MostImportantEdgeDestination ( Graph gc , Object n , Vector neighbors,float hg) {
        Object max_n = neighbors.elementAt(0);
        float max_EI = EdgeImportance(gc , n,neighbors.elementAt(0),hg);
        for (int i = 1; i < neighbors.size(); i++) {
            float EI = EdgeImportance(gc , n , neighbors.elementAt(i),hg);
            if (EI>max_EI){
                max_n=neighbors.elementAt(i);
                max_EI = EI;
            }
        }
        return max_n;
    }
    /********************* get the Set of Blocked Edges *********************************/
    public Pair<Vector> MostImportant(Graph gc , Vector sources ){
        float LF = 0;
        float gdc = GlobalDCentrality(gc);
        float hg=  HighestDegree(NodeDegrees(gc));
        Vector importantEdges = new Vector ();
        Vector importantNodes = new Vector();
        Vector community = new Vector(gc.getVertices());
        Vector infectedNodes = new Vector(getInfectedNodes(community, sources));
        for (int i = 0; i < infectedNodes.size(); i++) {
            if (gc.getNeighborCount(infectedNodes.elementAt(i))>0){
                Vector neighbors = new Vector( gc.getNeighbors(infectedNodes.elementAt(i)));
                Object max_node =neighbors.elementAt(0);
                float max_EI = EdgeImportance(gc , infectedNodes.elementAt(i),max_node,hg);
                Object max_edge = gc.findEdge(infectedNodes.elementAt(i),max_node) ;
                int max_j = 0 ;
              Vector b_edges = new Vector();
                for (int j = 1; j < neighbors.size(); j++) {
                    float EI = EdgeImportance(gc , infectedNodes.elementAt(i),neighbors.elementAt(j),hg);
                    if (isImportantEdge(EI, gdc)) {
                        b_edges.add(gc.findEdge(infectedNodes.elementAt(i),neighbors.elementAt(j)));
                        LF = LF + EI;
                    }
                    if(EI>max_EI){
                        max_EI = EI ;
                        max_node = neighbors.elementAt(j);
                        max_edge = gc.findEdge(infectedNodes.elementAt(i),neighbors.elementAt(j));
                    }

                }
                importantEdges.addAll(b_edges);
                importantNodes.add(max_node);
            }
        }
        if(LF>0){
            System.out.println("Block "+importantEdges.size());
            System.out.println("LF="+LF);
        }
        return new Pair<Vector>(importantEdges,importantNodes);
    }

    /********************* Information Diffusion Models : *********************************/

    /*********************************** ICM **********************************************/
    public Vector IndependentCascadingModel (Graph g , Vector s){
        float TIPI = 0;
        s=getInfectedNodes(new Vector(g.getVertices()),s);
        double gdc = GlobalDCentrality(g);
        float hg=  HighestDegree(NodeDegrees(g));

        Vector activeNodes  = new Vector(s);
        Vector newActivatedNodes = new Vector(s);
        while (newActivatedNodes.size()>0){
            TIPI=0;
            System.out.println("new activated nodes = " + newActivatedNodes.size());
            Vector newActivated = new Vector(newActivatedNodes);
            newActivatedNodes = new Vector();
            for (int i = 0; i < newActivated.size(); i++) {
                if( g.getNeighborCount(newActivated.elementAt(i))>0){
                Vector neighbors = new Vector(g.getNeighbors(newActivated.elementAt(i)));
                if(neighbors.size()>0){
                for (int j = 0; j < neighbors.size(); j++) {
                    if(Math.random()>=gdc){
                        if(!activeNodes.contains(neighbors.elementAt(j)) && !newActivatedNodes.contains(neighbors.elementAt(j))) {
                            TIPI = TIPI + EdgeImportance(g,newActivated.elementAt(i),neighbors.elementAt(j),hg);
                            newActivatedNodes.add(neighbors.elementAt(j));
                        }
                    }
                }

                }
            }
            }
            activeNodes = toMerge(activeNodes,newActivatedNodes);
            System.out.println("TIPI="+TIPI);
        }
        return activeNodes ;
    }

    /*********************************** PIC **********************************************/
    public Vector PeerToPeerICM (Graph g , Vector s){
        float TIPI=0;
        float hg=  HighestDegree(NodeDegrees(g));
        s=getInfectedNodes(new Vector(g.getVertices()),s);
        Vector activeNodes  = new Vector(s);
        Vector newActivatedNodes = new Vector(s);
        while (newActivatedNodes.size()!=0){
            TIPI=0;
            System.out.println("new activated nodes = " + newActivatedNodes.size());
            Vector newActivated = new Vector(newActivatedNodes);
            newActivatedNodes = new Vector();
            for (int i = 0; i < newActivated.size(); i++) {
                if( g.getNeighborCount(newActivated.elementAt(i))>0){
                    Vector neighbors = new Vector(g.getNeighbors(newActivated.elementAt(i)));
                    if(neighbors.size()>0){
                        for (int j = 0; j < neighbors.size(); j++) {
                            Object node = MostImportantEdgeDestination(g,newActivated.elementAt(i),neighbors,hg);
                            if(!activeNodes.contains(node) && !newActivatedNodes.contains(node)) {
                                TIPI = TIPI + EdgeImportance(g,newActivated.elementAt(i),neighbors.elementAt(j),hg);
                                newActivatedNodes.add(node);
                            }
                        }
                    }
                }
            }
            System.out.println("TIPI="+TIPI);
            activeNodes = toMerge(activeNodes,newActivatedNodes);
        }
        return activeNodes ;
    }

    /*********************************** LTM **********************************************/
    public Vector LinearThresholdModel (Graph g , Vector s){
        float TIPI = 0;
        float hg=  HighestDegree(NodeDegrees(g));
        s=getInfectedNodes(new Vector(g.getVertices()),s);
        Communities com = new Communities(g,alg);
        Vector activeNodes  = new Vector(s);
        Vector newActivatedNodes = new Vector(s);
        float GDC = GlobalDCentrality(g);
        Vector Vertices = new Vector(g.getVertices());
        while (newActivatedNodes.size()!=0) {
            TIPI=0;
            System.out.println("new activated nodes = " + newActivatedNodes.size());
            Vector newActivated = new Vector(newActivatedNodes);
            newActivatedNodes = new Vector();
            for (int i = 0; i < Vertices.size(); i++) {
                if (!activeNodes.contains(Vertices.elementAt(i))) {
                    float t_weight = 0;
                    if (g.getNeighborCount(Vertices.elementAt(i)) > 0) {
                        Vector Neighbors = new Vector(g.getNeighbors(Vertices.elementAt(i)));
                        for (int j = 0; j < Neighbors.size(); j++) {
                            if(activeNodes.contains(Neighbors.elementAt(j))){
                                t_weight+=1/(float)g.degree(Neighbors.elementAt(j));
                            }
                        }
                    if(t_weight>GDC){
                        newActivatedNodes.add(Vertices.elementAt(i));
                        for (int j = 0; j < Neighbors.size(); j++) {
                            if(activeNodes.contains(Neighbors.elementAt(j))){
                                TIPI = TIPI + EdgeImportance(g,Neighbors.elementAt(j),Vertices.elementAt(i),hg);
                            }
                        }
                    }
                    }
                }
            }
            System.out.println("TIPI="+TIPI);
            activeNodes = toMerge(activeNodes,newActivatedNodes);
        }
        return activeNodes;
    }
    public Vector BlockEdges (Graph gc, Vector s ) {
        Vector edges = MostImportant(gc,s).getFirst();
        return edges;
    }
    public Graph RemoveEdges (Graph g , Vector edges ){
        for (int i = 0; i < edges.size(); i++) {
            g.removeEdge(edges.elementAt(i));
        }
        return g;
    }
    public Vector toMerge(Vector v , Vector t){
        for (int i = 0; i < t.size(); i++) {
            if(!v.contains(t.elementAt(i))){
                v.add(t.elementAt(i));
            }
        }
        return v ;
    }
}
/*    public Graph CommunityPeerToPeerICM (Graph gc , Vector s,Graph g){
        Vector activeNodes  = new Vector(s);
        Vector newActivatedNodes = new Vector(s);
        while (newActivatedNodes.size()!=0){
            Vector mostImportantNodes = MostImportant(gc,newActivatedNodes).getSecond();
            newActivatedNodes = new Vector();
            for (int i = 0; i < mostImportantNodes.size(); i++) {
                if (!activeNodes.contains(mostImportantNodes.elementAt(i))) {
                    newActivatedNodes.add(mostImportantNodes.elementAt(i));
                }
            }
            activeNodes.addAll(newActivatedNodes);
            Vector e = BlockEdges(gc,s);
            g=RemoveEdges(g,s);
        }
        return g ;
 }*/