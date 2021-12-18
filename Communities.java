import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Vector;

public class Communities {
    Graph g ;
     Vector communities = new Vector();
     Vector nodeOverlap = new Vector();;
     DijkstraDistance alg;
     double temps1;

    public Communities( Graph g   , DijkstraDistance alg){
        this.g = g ;
        this.alg  =alg;
        this.temps1 = java.lang.System.currentTimeMillis() ;
    }
    public Communities(){}
    /*************************** Importances des noeuds***************/
    public float MNode_importance(Object v) {
        float coef;
        float imp;
        float comp = 0;
        float deg = (float) this.g.degree(v);
        Collection F = this.g.getNeighbors(v);
        for (Object d : F) {
            for (Object f : F) {
                if (f != d) {
                    comp = comp + this.g.findEdgeSet(d, f).size();
                }
            }
        }
        if (deg > 1) {
            coef = (2 * (comp / 2) / (deg * (deg - 1)));
        } else {
            coef = 0;
        }
        imp = coef * (deg);
        return imp;
    }

    /***************************Max des importances des noeuds***************/
    public Object Max(Vector V) {
        Object max = V.elementAt(0);
        float mnimpMax = MNode_importance(max);
        Object element;
        int size = V.size();
        for (int i = 1; i < size; i++) {
            element = V.elementAt(i);
            if (mnimpMax < MNode_importance(element)) {
                max = element;
                mnimpMax = MNode_importance(max);
            }
        }
        return max;
    }

    /************************ Max des appartenances  des noeuds a� une communaute***************/
    public Object MaxK(Vector V, Collection C) {
        Object max = V.elementAt(0);
        double appMax = App(max, C);
        double app = 0;
        int size = V.size();
        for (int i = 1; i < size; i++) {
            app = 1 / (dist_moy(V.elementAt(i), C) * facteur_pond(V.elementAt(i), C));
            if (appMax < app) {
                max = V.elementAt(i);
                appMax = App(max, C);
            }
        }
        return max;
    }

    /******************* Le plus court chemin entre deux noeuds *************/
    public int dist(Object vd, Object vf) {
        int d = alg.getDistance(vd, vf).intValue();
//        System.out.println("dist between "+ vd +","+vf +"est"+d);
        return d;
    }

    /***************************distance moyenne ***************/
    public float dist_moy(Object v, Collection C) {
        float d = 0;
        float df = 0;
        for (Object u : C) {
            d = d + (dist(v, u));
        }
        if (C.contains(v)) {
            df = d / (C.size() - 1);
        } else {
            df = d / (C.size());
        }
        return df;
    }


    /***************************facteur de ponderation***************/
    public float facteur_pond(Object v, Collection C) {
        float com = 0;
        Collection F = this.g.getNeighbors(v);

        for (Object nn : F) {

            if (C.contains(nn)) {
                com = com + 1;
            }
        }
        float fact = (this.g.degree(v)) / com;
        return fact;
    }

    /***************************  App ***************/
    public double App(Object v, Collection C) {
        double app = 1 / (dist_moy(v, C) * facteur_pond(v, C));
        return app;
    }


    /***************************compacite d'une communaute ***************/
    public Vector compacite(Vector comp, Collection C) {
        for (Object e : this.g.getEdges()) {
            Pair c = this.g.getEndpoints(e);
            if (C.contains(c.getFirst()) && C.contains(c.getSecond())) {
                comp.add(e);
            }
        }
        return comp ;
    }

    /***************************separabilite ***************/
    public Vector seperabilite(Vector sep, Collection C) {
        for (Object arg0 : C) {
            for (Object arg1 : this.g.getVertices()) {
                if ((arg0 != arg1) && (C.contains(arg1) == false)) {
                    sep.addAll(this.g.findEdgeSet(arg0, arg1));
                }
            }
        }
        return sep ;
    }

    /***************************Maj compacite et seperabilite***************/
    public Pair MAJcompaciteSeperabilite(Vector compacit, Vector seperab, Collection c, Object n) {
        for (Object e : this.g.getNeighbors(n)) {
            Object lien = this.g.findEdge(e, n);
            if (c.contains(e)) {
                compacit.add(lien);
                seperab.remove(lien);
            }
            else seperab.add(lien);
        }
        return new Pair(compacit,seperab);
    }

    /***************************Indice de connectivite***************/
    public double IC(int compaSize, int sepSize) {
        double ic = (compaSize - sepSize) / Math.pow((compaSize + sepSize), 0.5);
        return ic;
    }

    /***************************noyau de communaute ***************/
    public Vector Noyau(Graph g ,Vector V, Object n) {
        V.addAll(this.g.getNeighbors(n));
        V.add(n);
        return V;
    }

    /***************************frontiere de communaute *********************************/
    public Vector Frontiere(Vector Frontiere, Collection C) {
        for (Object o : C) {
            Collection b = this.g.getNeighbors(o);
            for (Object d : b) {
                if (!C.contains(d) && !Frontiere.contains(d)) {
                    Frontiere.add(d);
                }
            }
        }
        return Frontiere;
    }

    /********************* mise a jour de frontiere plus ajout **************************/
    public Vector  MAJFrontiere(Vector k, Vector com, Object n) {
        k.remove(n);
        for (Object e : this.g.getNeighbors(n)) {
            if (!k.contains(e) && !com.contains(e)) {
                k.add(e);
            }
        }
        return k;
    }
    /***************************Detection des communautes *********************************/
    public Pair<Vector> getcommunities(Graph g){
        this.g=g;
        Vector K = new Vector();
        Vector VV = new Vector();
        int compt = 0;
        int p = 0;
        Vector Com;
        VV.addAll(this.g.getVertices());
        int compSize = 0;
        int sepSize = 0;
        int compSizecc = 0;
        int sepSizecc = 0;
        Object choix;
        Vector seperab;
        Vector copacit;
        Object choixN;
        while (VV.size() != 0) {
            seperab = new Vector();
            copacit = new Vector();
            Com = new Vector();
            choixN = Max(VV);  //le choix de centre
            Com = Noyau(g,Com, choixN);  //formation de noyau
            K = Frontiere(K, Com);   // construction de frontiere
            copacit=compacite(copacit, Com);
            seperab =seperabilite(seperab, Com);
            compSize = copacit.size();
            sepSize = seperab.size();
            boolean trv = true;
            while (trv && K.size()>0) {
                choix = MaxK(K, Com);
                Com.add(choix);
                compSizecc = compSize;
                sepSizecc = sepSize;
                Pair<Vector> cs = new  Pair (MAJcompaciteSeperabilite(copacit, seperab, Com, choix));
                compSize = cs.getFirst().size();
                sepSize = cs.getSecond().size();
                if (IC(compSizecc, sepSizecc) < IC(compSize, sepSize)) {
                    K= MAJFrontiere(K, Com, choix);
                } else
                {
                    Com.remove(choix);
                    K.clear();
                    trv = false;
                }
            }
            compt++;
            if (compt % 6 == 0) {
                alg.reset();
            }
            System.out.println("Community n°"+compt);
            communities.addElement(Com);
            VV.removeAll(Com);
        }
        alg = null;
        Vector vv = new Vector();
        try {
            PrintWriter fichout = new PrintWriter(new FileWriter("FileRead/resultat.txt")); //output de DOCNet dans fichier resulat
            for (int tt = 0; tt < communities.size(); tt++) {
                vv = (Vector) communities.elementAt(tt);
                String comm = "";
                for (int ttt = 0; ttt < vv.size(); ttt++) {
                    int s = new Integer((Integer) vv.elementAt(ttt)).intValue();
                    comm = comm + s + " ";
                }
                fichout.println(comm);
            }
            fichout.close();
        }
        catch (IOException exp) {
            System.out.println("un probleme de telechargement de fichier " + exp);
        }
        nodeOverlap=nodeOverlap(communities);
        double temps2 = (double) java.lang.System.currentTimeMillis();
        double tempsdexe = temps2 - temps1;
        System.out.println("Communities Detection Finished in : "+ tempsdexe+" milli seconds" );
        return  new Pair<Vector>(communities,nodeOverlap) ;
    }

    /********************* overlapped nodes *********************************************/
    public Vector nodeOverlap(Vector communities) {
        // Returns the nodes that exists in more than one community
        System.out.println(communities.size());
        nodeOverlap = new Vector();
        for (int i = 0; i < communities.size(); i++) {
            Vector Collect1 = (Vector) communities.elementAt(i);
            for (int j = i + 1; j < communities.size(); j++) {
                Vector Collect2 = (Vector) communities.elementAt(j);
                Vector Collect3 = new Vector( Collect1);
                Vector Collect4 =new Vector( Collect2);
                Collect4.retainAll(Collect3);
                for (int k=0 ; k<Collect4.size();k++){
                    if(!nodeOverlap.contains((Collect4.elementAt(k)))){
                        nodeOverlap.add(Collect4.elementAt(k));
                    }
                }
//                for (int jj = 0; jj < Collect1.size(); jj++) {
//                    for (int jjj = 0; jjj < Collect2.size(); jjj++) {
//                        if ((Collect1.elementAt(jj).equals(Collect2.elementAt(jjj))) && !nodeOverlap.contains(Collect1.elementAt(jj))) {
//                            nodeOverlap.add(Collect1.elementAt(jj));
////                            System.out.println(Collect1.elementAt(jj) + " added ");
//                        }
//                    }
//                }
            }
        }
        System.out.println("overlapping Nodes :"+nodeOverlap.size());

        // Save the overlapping nodes in the file : Nodeoverlap.txt
        try {
            PrintWriter fichout = new PrintWriter(new FileWriter("FileRead/Nodeoverlap.txt"));
            for (int ttt = 0; ttt < nodeOverlap.size(); ttt++) {
                int s = new Integer((Integer) nodeOverlap.elementAt(ttt)).intValue();
                fichout.println(s);
            }
            fichout.close();
        }
        catch (IOException exp) {
            System.out.println("Un probleme dans le telechargement de fichier  " + exp);
        }
        return nodeOverlap ;
    }
    public Vector nodeOverlap2(Vector communities , Vector vertices){
        nodeOverlap = new Vector();
        for (int i = 0; i < vertices.size(); i++) {
            int nb = 0;
            int com_i=0;
            while ((nb < 2) && (com_i < communities.size())) {
                Vector community = (Vector) communities.elementAt(com_i);
                if( community.contains(vertices.elementAt(i)) && !nodeOverlap.contains(vertices.elementAt(i))){nb++;}
                else{com_i++;}
            }
            if (nb >= 2) {nodeOverlap.add(vertices.elementAt(i));}
        }
        System.out.println("overlapping Nodes :"+nodeOverlap.size());
        try {
            PrintWriter fichout = new PrintWriter(new FileWriter("FileRead/Nodeoverlap.txt"));
            for (int ttt = 0; ttt < nodeOverlap.size(); ttt++) {
                int s = new Integer((Integer) nodeOverlap.elementAt(ttt)).intValue();
                fichout.println(s);
            }
            fichout.close();
        }
        catch (IOException exp) {
            System.out.println("Un probleme dans le telechargement de fichier  " + exp);
        }
        return nodeOverlap ;
    }

    public Vector readCommunities() {
        Vector communities = new Vector();
        Vector<String> lignes ;
        former2 f = new former2();
        lignes = f.lectureFichier7("FileRead/resultat.txt");
        for (int i = 0; i < lignes.size(); i++) {
            String[]  l= lignes.elementAt(i).split(" ");
            Vector community = new Vector();
            for (int j = 0; j < l.length; j++) {
                if(!l[j].equals("")){
               community.add(Integer.parseInt(l[j])) ;}
            }
            if(community.size()>0){
            communities.add(community);}
        }
       return communities ;
    }
    public Vector readNodeOverlap(){
        Vector nodes ;
        former2 f = new former2();
        nodes = f.lectureFichier5("FileRead/Nodeoverlap.txt");
        return nodes ;
    }
}
