/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**********       DOCNet        *******/

import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

import java.util.Random;
import java.util.Vector;

public class main2 {
    /*****************************************************************/
    /***************************Le programme principal ***************/
    /*****************************************************************/
    public static void main(String[] args) {

        former2 f = new former2();
        Vector source = new Vector ();
        Vector communities = new Vector();
        Vector nodeOverlap = new Vector();
        Vector Active = new Vector();
        Pair<Vector> p;
        Random rn = new Random();
        String reset = "\u001B[0m";
        String red   = "\u001B[31m";
        String green = "\u001B[32m";
        Graph g ;
        Vector Vertex ;
        Vector Edges ;
/*************************  le choix du graphe input**************************/

/*****************************************************************************************************************************************************************************************************************/
         g= f.lectureFichier6("FileRead/deblp_graph.txt");
//        g= f.lectureFichier3("FileRead/network.dat");
        System.out.println("le nombre des noeuds  " + g.getVertexCount());
         Vertex = new Vector(g.getVertices());
        System.out.println("le nombre des aretes  " + g.getEdgeCount());
         Edges = new Vector(g.getEdges());
        DijkstraDistance alg = new DijkstraDistance(g);
        Blocking b = new Blocking(alg);

/*****************************************************************************************************************************************************************************************************************/

        Communities com = new Communities(g,alg);
//        p = com.getcommunities(g);
//        communities = p.getFirst();
//        nodeOverlap = p.getSecond();
        communities = com.readCommunities();
        nodeOverlap = com.readNodeOverlap();
//        nodeOverlap = com.nodeOverlap(communities);
//        nodeOverlap=com.nodeOverlap2(communities,Vertex);

/*****************************************************************************************************************************************************************************************************************/
        System.out.println(green+" 10 % inital infected nodes  --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);

        System.out.println(red+"step 0 : Random Selection of Source Nodes --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        int bound1 = Math.round(Vertex.size()*10/100);
        while (source.size()<bound1){
            int r = rn.nextInt(g.getVertexCount());
            if(!source.contains(r)&& g.containsVertex(r)){
                source.add(r);
            }
        }
        System.out.println("source ("+source.size()+" nodes ) = ");

/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"step1 : Information Diffusion Model Before Proposed Model --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        long t1_step1 = java.lang.System.currentTimeMillis() ;
        Active = new Vector(b.IndependentCascadingModel(g,source));
        
        System.out.println("infected nodes ("+Active.size()+" nodes) = " );
        System.out.println("After Proposed Model");
        long t2_step1 = java.lang.System.currentTimeMillis() ;
        System.out.println("Proposed Model Finished in : "+ (t2_step1 - t1_step1) +" milli seconds" );


/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"Step2 : Communities Detection--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        System.out.println("communities" + communities.size());
        System.out.println("Overlapping nodes" + nodeOverlap.size());
        System.out.println(red+"Step3 : Block Overlapping Nodes--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        g = b.BlockNodes(nodeOverlap,g);

/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"Step3 : Block Important Edges in each Community--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        for (int i = 0; i < communities.size(); i++) {
            Vector community = (Vector) communities.elementAt(i);
            Vector infected = new Vector(b.getInfectedNodes(community, source));
            if(b.isInfectedCommunity(community,source)) {
                Graph gc = b.getGraphCommunity(community,g);
                Vector b_edges=  b.BlockEdges(gc,infected);
                g = b.RemoveEdges(g,b_edges);
            }
        }
/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"step4 : Information Diffusion Model After Proposed Model --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        System.out.println("le nombre des noeuds after  " + g.getVertexCount());
        System.out.println("le nombre des aretes after  " + g.getEdgeCount());
        long t1_step2 = java.lang.System.currentTimeMillis();
        Active = new Vector(b.IndependentCascadingModel(g,source));
        

        System.out.println("After Blocking Most Important Edges");
        System.out.println("infected nodes ("+Active.size()+" nodes) = " );
        long t2_step2 = java.lang.System.currentTimeMillis();
        System.out.println("Proposed Model Finished in : "+ (t2_step2 - t1_step2) +" milli seconds" );


        /*****************************************************************************************************************************************************************************************************************/
        System.out.println(green+" 30 % inital infected nodes  --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        g= f.lectureFichier6("FileRead/deblp_graph.txt");
        System.out.println("le nombre des noeuds  " + g.getVertexCount());
        Vertex = new Vector(g.getVertices());
        System.out.println("le nombre des aretes  " + g.getEdgeCount());
        Edges = new Vector(g.getEdges());
        System.out.println(red+"step 0 : Random Selection of Source Nodes --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        int bound2 = Math.round(g.getVertexCount()/100)*30;
        source = new Vector();
        while (source.size()<bound2){
            int r = rn.nextInt(g.getVertexCount());
            if(!source.contains(r)&&r>0){
                source.add(r);
            }
        }
        System.out.println("source ("+source.size()+" nodes ) = ");

/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"step1 : Information Diffusion Model Before Proposed Model --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        long t1_step1_2 = java.lang.System.currentTimeMillis() ;
        Active = new Vector(b.IndependentCascadingModel(g,source));
        
        System.out.println("infected nodes ("+Active.size()+" nodes) = " );
        System.out.println("After Proposed Model");
        long t2_step1_2 = java.lang.System.currentTimeMillis() ;
        System.out.println("Proposed Model Finished in : "+ (t2_step1_2 - t1_step1_2) +" milli seconds" );


/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"Step2 : Communities Detection--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        System.out.println("communities" + communities.size());
        System.out.println("Overlapping nodes" + nodeOverlap.size());
        System.out.println(red+"Step3 : Block Overlapping Nodes--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        g = b.BlockNodes(nodeOverlap,g);

/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"Step3 : Block Important Edges in each Community--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        for (int i = 0; i < communities.size(); i++) {
            Vector community = (Vector) communities.elementAt(i);
            Vector infected = new Vector(b.getInfectedNodes(community, source));
            if(b.isInfectedCommunity(community,source)) {
                Graph gc = b.getGraphCommunity(community,g);
                Vector b_edges=  b.BlockEdges(gc,infected);
                g = b.RemoveEdges(g,b_edges);
            }
        }
/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"step4 : Information Diffusion Model After Proposed Model --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        System.out.println("le nombre des noeuds after  " + g.getVertexCount());
        System.out.println("le nombre des aretes after  " + g.getEdgeCount());
        long t1_step2_2 = java.lang.System.currentTimeMillis();
        Active = new Vector(b.IndependentCascadingModel(g,source));
       

        System.out.println("After Blocking Most Important Edges");
        System.out.println("infected nodes ("+Active.size()+" nodes) = " );
        long t2_step2_2 = java.lang.System.currentTimeMillis();
        System.out.println("Proposed Model Finished in : "+ (t2_step2_2 - t1_step2_2) +" milli seconds" );

        /*****************************************************************************************************************************************************************************************************************/
        System.out.println(green+" 50 % inital infected nodes  --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        g= f.lectureFichier6("FileRead/deblp_graph.txt");
        System.out.println("le nombre des noeuds  " + g.getVertexCount());
        Vertex = new Vector(g.getVertices());
        System.out.println("le nombre des aretes  " + g.getEdgeCount());
        Edges = new Vector(g.getEdges());
        System.out.println(red+"step 0 : Random Selection of Source Nodes --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        int bound3 = Math.round(g.getVertexCount()/100)*50;
        source = new Vector();
        while (source.size()<bound3){
            int r = rn.nextInt(g.getVertexCount());
            if(!source.contains(r)&&r>0){
                source.add(r);
            }
        }
        System.out.println("source ("+source.size()+" nodes ) = ");

/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"step1 : Information Diffusion Model Before Proposed Model --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        long t1_step1_3 = java.lang.System.currentTimeMillis() ;
        Active = new Vector(b.IndependentCascadingModel(g,source));
        System.out.println("infected nodes ("+Active.size()+" nodes) = " );
        System.out.println("After Proposed Model");
        long t2_step1_3 = java.lang.System.currentTimeMillis() ;
        System.out.println("Proposed Model Finished in : "+ (t2_step1_3 - t1_step1_3) +" milli seconds" );


/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"Step2 : Communities Detection--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        System.out.println("communities" + communities.size());
        System.out.println("Overlapping nodes" + nodeOverlap.size());
        System.out.println(red+"Step3 : Block Overlapping Nodes--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        g = b.BlockNodes(nodeOverlap,g);

/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"Step3 : Block Important Edges in each Community--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        for (int i = 0; i < communities.size(); i++) {
            Vector community = (Vector) communities.elementAt(i);
            Vector infected = new Vector(b.getInfectedNodes(community, source));
            if(b.isInfectedCommunity(community,source)) {
                Graph gc = b.getGraphCommunity(community,g);
                Vector b_edges=  b.BlockEdges(gc,infected);
                g = b.RemoveEdges(g,b_edges);
            }
        }
/*****************************************************************************************************************************************************************************************************************/

        System.out.println(red+"step4 : Information Diffusion Model After Proposed Model --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------"+reset);
        System.out.println("le nombre des noeuds after  " + g.getVertexCount());
        System.out.println("le nombre des aretes after  " + g.getEdgeCount());
        long t1_step2_3 = java.lang.System.currentTimeMillis();
        Active = new Vector(b.IndependentCascadingModel(g,source));
        System.out.println("After Blocking Most Important Edges");
        System.out.println("infected nodes ("+Active.size()+" nodes) = " );
        long t2_step2_3 = java.lang.System.currentTimeMillis();
        System.out.println("Proposed Model Finished in : "+ (t2_step2_3 - t1_step2_3) +" milli seconds" );

    }
}