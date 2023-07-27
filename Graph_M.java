import java.util.*;
import java.io.*;


public class Main
{
    public class Vertex
    {
        HashMap<String, Integer> stationMap = new HashMap<>(); 
    }

    static HashMap<String, Vertex> vertices;      //we create a hashmap called vertices to store the string and the vertex no

    public Main()
    {
        vertices = new HashMap<>();
    }

    public int numVetex()
    {
        return this.vertices.size();
    }

    public boolean containsVertex(String sname)   //boolean function return s true or false
    {
        return this.vertices.containsKey(sname);  //if true it returns the station name
    }

    public void addVertex(String sname)
    {
        Vertex verteX = new Vertex();
        vertices.put(sname, verteX);
    }

    public void removeVertex(String sname)
    {
        Vertex verteX = vertices.get(sname);
        ArrayList<String> keys = new ArrayList<>(verteX.stationMap.keySet());

        for (String key : keys)
        {
            Vertex neighbourverteX = vertices.get(key);
            neighbourverteX.stationMap.remove(sname);
        }

        vertices.remove(sname);
    }

    public int numEdges()
    {
        ArrayList<String> keys = new ArrayList<>(vertices.keySet());
        int count = 0;

        for (String key : keys)
        {
            Vertex verteX = vertices.get(key);
            count = count + verteX.stationMap.size();
        }

        return count / 2;
    }

    public boolean containsEdge(String sname1, String sname2)
    {
        Vertex verteX1 = vertices.get(sname1);
        Vertex verteX2 = vertices.get(sname2);

        if (verteX1 == null || verteX2 == null || !verteX1.stationMap.containsKey(sname2)) {
            return false;
        }

        return true;
    }

    public void addEdge(String sname1, String sname2, int value)  //we add an edge, to find the distance between sname1 & sname2
    {
        Vertex verteX1 = vertices.get(sname1);
        Vertex verteX2 = vertices.get(sname2);

        if (verteX1 == null || verteX2 == null || verteX1.stationMap.containsKey(sname2)) {  //if we do not enter the correct station
            return;
        }

        verteX1.stationMap.put(sname2, value);
        verteX2.stationMap.put(sname1, value);
    }

    public void removeEdge(String sname1, String sname2)
    {
        Vertex verteX1 = vertices.get(sname1);
        Vertex verteX2 = vertices.get(sname2);

        //check if the vertices given or the edge between these vertices exist or not
        if (verteX1 == null || verteX2 == null || !verteX1.stationMap.containsKey(sname2)) {
            return;
        }

        verteX1.stationMap.remove(sname2);
        verteX2.stationMap.remove(sname1);
    }

    public void display_Map()
    {
        System.out.println("\t Kolkata Metro Map");
        System.out.println("\t************->");
        System.out.println("*******************************-\n");
        ArrayList<String> keys = new ArrayList<>(vertices.keySet());

        for (String key : keys)
        {
            String str = key + " =>\n";
            Vertex verteX = vertices.get(key);
            ArrayList<String> verteXstationMap = new ArrayList<>(verteX.stationMap.keySet());

            for (String neighbour : verteXstationMap)
            {
                str = str + "\t" + neighbour + "\t";
                if (neighbour.length()<16)
                    str = str + "\t";
                if (neighbour.length()<8)
                    str = str + "\t";
                str = str + verteX.stationMap.get(neighbour) + "\n";
            }
            System.out.println(str);
        }
        System.out.println("\t------------------");
        System.out.println("---------------------------------------------------\n");

    }

    public void display_Stations()
    {
        System.out.println("\n***********************************************************************\n");
        ArrayList<String> keys = new ArrayList<>(vertices.keySet());
        int i=1;
        for(String key : keys)
        {
            System.out.println(i + ". " + key);
            i++;
        }
        System.out.println("\n***********************************************************************\n");
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean hasPath(String sname1, String sname2, HashMap<String, Boolean> processed)
    {
        // DIR EDGE
        if (containsEdge(sname1, sname2)) {
            return true;
        }

        //MARK AS DONE
        processed.put(sname1, true);

        Vertex verteX = vertices.get(sname1);
        ArrayList<String> stationMap = new ArrayList<>(verteX.stationMap.keySet());

        //TRAVERSE THE stationMap OF THE VERTEX
        for (String neighbour : stationMap)
        {

            if (!processed.containsKey(neighbour))
                if (hasPath(neighbour, sname2, processed))
                    return true;
        }

        return false;
    }


    private class DijkstraPair implements Comparable<DijkstraPair>
    {
        String sname;
        String psf;
        int cost;

        @Override
        public int compareTo(DijkstraPair o)
        {
            return o.cost - this.cost;
        }
    }

    public int dijkstra(String src, String des, boolean nan)
    {
        int val = 0;
        ArrayList<String> ans = new ArrayList<>();
        HashMap<String, DijkstraPair> map = new HashMap<>();

        Heap<DijkstraPair> heap = new Heap<>();

        for (String key : vertices.keySet())
        {
            DijkstraPair np = new DijkstraPair();
            np.sname = key;
            //np.psf = "";
            np.cost = Integer.MAX_VALUE;

            if (key.equals(src))
            {
                np.cost = 0;
                np.psf = key;
            }

            heap.add(np);
            map.put(key, np);
        }

        //keep removing the pairs while heap is not empty
        while (!heap.isEmpty())
        {
            DijkstraPair rp = heap.remove();

            if(rp.sname.equals(des))
            {
                val = rp.cost;
                break;
            }

            map.remove(rp.sname);

            ans.add(rp.sname);

            Vertex v = vertices.get(rp.sname);
            for (String neighbour : v.stationMap.keySet())
            {
                if (map.containsKey(neighbour))
                {
                    int oc = map.get(neighbour).cost;
                    Vertex k = vertices.get(rp.sname);
                    int nc;
                    if(nan)
                        nc = rp.cost + 120 + 40*k.stationMap.get(neighbour);
                    else
                        nc = rp.cost + k.stationMap.get(neighbour);

                    if (nc < oc)
                    {
                        DijkstraPair gp = map.get(neighbour);
                        gp.psf = rp.psf + neighbour;
                        gp.cost = nc;

                        heap.updatePriority(gp);
                    }
                }
            }
        }
        return val;
    }

    private class Pair
    {
        String sname;
        String psf;
        int min_dis;
        int min_time;
    }

    public String Get_Minimum_Distance(String src, String dst)
    {
        int min = Integer.MAX_VALUE;
        //int time = 0;
        String ans = "";
        HashMap<String, Boolean> processed = new HashMap<>();
        LinkedList<Pair> stack = new LinkedList<>();

        // create a new pair
        Pair sp = new Pair();
        sp.sname = src;
        sp.psf = src + "  ";
        sp.min_dis = 0;
        sp.min_time = 0;

        // put the new pair in stack
        stack.addFirst(sp);

        // while stack is not empty keep on doing the work
        while (!stack.isEmpty())
        {
            // remove a pair from stack
            Pair rp = stack.removeFirst();

            if (processed.containsKey(rp.sname))
            {
                continue;
            }

            // processed put
            processed.put(rp.sname, true);

            //if there exists a direct edge b/w removed pair and destination vertex
            if (rp.sname.equals(dst))
            {
                int temp = rp.min_dis;
                if(temp<min) {
                    ans = rp.psf;
                    min = temp;
                }
                continue;
            }

            Vertex rpverteX = vertices.get(rp.sname);
            ArrayList<String> stationMap = new ArrayList<>(rpverteX.stationMap.keySet());

            for(String neighbour : stationMap)
            {
                // process only unprocessed stationMap
                if (!processed.containsKey(neighbour)) {

                    // make a new pair of neighbour and put in queue
                    Pair np = new Pair();
                    np.sname = neighbour;
                    np.psf = rp.psf + neighbour + "  ";
                    np.min_dis = rp.min_dis + rpverteX.stationMap.get(neighbour);
                    //np.min_time = rp.min_time + 120 + 40*rpverteX.stationMap.get(neighbour);
                    stack.addFirst(np);
                }
            }
        }
        ans = ans + Integer.toString(min);
        return ans;
    }


    public String Get_Minimum_Time(String src, String dst)
    {
        int min = Integer.MAX_VALUE;
        String ans = "";
        HashMap<String, Boolean> processed = new HashMap<>();
        LinkedList<Pair> stack = new LinkedList<>();

        // create a new pair
        Pair sp = new Pair();
        sp.sname = src;
        sp.psf = src + "  ";
        sp.min_dis = 0;
        sp.min_time = 0;

        // put the new pair in queue
        stack.addFirst(sp);

        // while queue is not empty keep on doing the work
        while (!stack.isEmpty()) {

            // remove a pair from queue
            Pair rp = stack.removeFirst();

            if (processed.containsKey(rp.sname))
            {
                continue;
            }

            // processed put
            processed.put(rp.sname, true);

            //if there exists a direct edge b/w removed pair and destination vertex
            if (rp.sname.equals(dst))
            {
                int temp = rp.min_time;
                if(temp<min) {
                    ans = rp.psf;
                    min = temp;
                }
                continue;
            }

            Vertex rpverteX = vertices.get(rp.sname);
            ArrayList<String> stationMap = new ArrayList<>(rpverteX.stationMap.keySet());

            for (String neighbour : stationMap)
            {
                // process only unprocessed stationMap
                if (!processed.containsKey(neighbour)) {

                    // make a new pair of neighbour and put in queue
                    Pair np = new Pair();
                    np.sname = neighbour;
                    np.psf = rp.psf + neighbour + "  ";
                    //np.min_dis = rp.min_dis + rpverteX.stationMap.get(neighbour);
                    np.min_time = rp.min_time + 120 + 40*rpverteX.stationMap.get(neighbour);
                    stack.addFirst(np);
                }
            }
        }
        Double minutes = Math.ceil((double)min / 60);
        ans = ans + Double.toString(minutes);
        return ans;
    }

    public ArrayList<String> get_Interchanges(String str)
    {
        ArrayList<String> arr = new ArrayList<>();
        String res[] = str.split("  ");
        arr.add(res[0]);
        int count = 0;
        for(int i=1;i<res.length-1;i++)
        {
            int index = res[i].indexOf('~');
            String s = res[i].substring(index+1);

            if(s.length()==2)
            {
                String prev = res[i-1].substring(res[i-1].indexOf('~')+1);
                String next = res[i+1].substring(res[i+1].indexOf('~')+1);

                if(prev.equals(next))
                {
                    arr.add(res[i]);
                }
                else
                {
                    arr.add(res[i]+" ==> "+res[i+1]);
                    i++;
                    count++;
                }
            }
            else
            {
                arr.add(res[i]);
            }
        }
        arr.add(Integer.toString(count));
        arr.add(res[res.length-1]);
        return arr;
    }

    public static void Create_Metro_Map(Main g)
    {
        g.addVertex("Dakshineshwar");
        g.addVertex("Dum Dum");
        g.addVertex("Belgachhia");
        g.addVertex("Shyambazaar");
        g.addVertex("SovaBazaar Sutanuti");
        g.addVertex("Girish Park");
        g.addVertex("Mahatma Gandhi Road");
        g.addVertex("Central");
        g.addVertex("Chandni Chowk");
        g.addVertex("Esplanade");
        g.addVertex("Park Street");
        g.addVertex("Maidan");
        g.addVertex("Rabindra Sadan");
        g.addVertex("Netaji Bhavan");
        g.addVertex("Jatin Das Park");
        g.addVertex("Kalighat");
        g.addVertex("Rabindra Sarobar");
        g.addVertex("Mahanayak Uttam Kumar");
        g.addVertex("Netaji");
        g.addVertex("Masterda Surya Sen");
        g.addVertex("Gitanjali");
        g.addVertex("Kabi Nazrul");
        g.addVertex("Shahid Khudiram");
        g.addVertex("Kavi Subhash");
        
        g.addEdge("Dakshineshwar", "Dum Dum", 8);
        g.addEdge("Dum Dum", "Belgachhia", 10);
        g.addEdge("Belgachhia", "Shyambazaar", 8);
        g.addEdge("Belgachhia", "SovaBazaar Sutanuti", 6);
        g.addEdge("SovaBazaar Sutanuti", "Girish Park", 9);
        g.addEdge("Girish Park", "Mahatma Gandhi Road", 7);
        g.addEdge("Mahatma Gandhi Road", "Central", 6);
        g.addEdge("Chandni Chowk", "Park Street", 15);
        g.addEdge("Park Street", "Maidan", 6);
        g.addEdge("Maidan", "SovaBazaar Sutanuti", 7);
        g.addEdge("SovaBazaar Sutanuti", "Maidan", 1);
        g.addEdge("Maidan", "Rabindra Sadan", 2);
        g.addEdge("Rabindra Sadan", "Netaji Bhavan", 5);
        g.addEdge("Maidan", "Jatin Das Park", 2);
        g.addEdge("Jatin Das Park", "Kalighat", 7);
        g.addEdge("Kalighat", "Rabindra Sarobar", 8);
        g.addEdge("Girish Park", "Mahanayak Uttam Kumar", 2);
        g.addEdge("Netaji", "Mahanayak Uttam Kumar", 2);
        g.addEdge("Netaji", "Masterda Surya Sen", 3);
    }

    public static String[] printCodelist()
    {
        System.out.println("List of station along with their codes:\n");
        ArrayList<String> keys = new ArrayList<>(vertices.keySet());
        int i=1,j=0,m=1;
        StringTokenizer stname;
        String temp="";
        String codes[] = new String[keys.size()];
        char c;
        for(String key : keys)
        {
            stname = new StringTokenizer(key);
            codes[i-1] = "";
            j=0;
            while (stname.hasMoreTokens())
            {
                temp = stname.nextToken();
                c = temp.charAt(0);
                while (c>47 && c<58)
                {
                    codes[i-1]+= c;
                    j++;
                    c = temp.charAt(j);
                }
                if ((c<48 || c>57) && c<123)
                    codes[i-1]+= c;
            }
            if (codes[i-1].length() < 2)
                codes[i-1]+= Character.toUpperCase(temp.charAt(1));

            System.out.print(i + ". " + key + "\t");
            if (key.length()<(22-m))
                System.out.print("\t");
            if (key.length()<(14-m))
                System.out.print("\t");
            if (key.length()<(6-m))
                System.out.print("\t");
            System.out.println(codes[i-1]);
            i++;
            if (i == (int)Math.pow(10,m))
                m++;
        }
        return codes;
    }

    public static void main(String[] args) throws IOException
    {
        Main g = new Main();
        Create_Metro_Map(g);

        System.out.println("\n\t\t\t****WELCOME TO THE METRO APP*****");
        // System.out.println("\t\t\t\t~~LIST OF ACTIONS~~\n\n");
        // System.out.println("1. LIST ALL THE STATIONS IN THE MAP");
        // System.out.println("2. SHOW THE METRO MAP");
        // System.out.println("3. GET SHORTEST DISTANCE FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
        // System.out.println("4. GET SHORTEST TIME TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
        // System.out.println("5. GET SHORTEST PATH (DISTANCE WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
        // System.out.println("6. GET SHORTEST PATH (TIME WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
        // System.out.print("\nENTER YOUR CHOICE FROM THE ABOVE LIST : ");
        BufferedReader inp = new BufferedReader(new InputStreamReader(System.in));
        // int choice = Integer.parseInt(inp.readLine());
        //STARTING SWITCH CASE
        while(true)
        {
            System.out.println("\t\t\t\t~~LIST OF ACTIONS~~\n\n");
            System.out.println("1. LIST ALL THE STATIONS IN THE MAP");
            System.out.println("2. SHOW THE METRO MAP");
            System.out.println("3. GET SHORTEST DISTANCE FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
            System.out.println("4. GET SHORTEST TIME TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
            System.out.println("5. GET SHORTEST PATH (DISTANCE WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
            System.out.println("6. GET SHORTEST PATH (TIME WISE) TO REACH FROM A 'SOURCE' STATION TO 'DESTINATION' STATION");
            System.out.println("7. EXIT THE MENU");
            System.out.print("\nENTER YOUR CHOICE FROM THE ABOVE LIST (1 to 7) : ");
            int choice = -1;
            try {
                choice = Integer.parseInt(inp.readLine());
            } catch(Exception e) {
                // default will handle
            }
            System.out.print("\n***********************************************************\n");
            if(choice == 7)
            {
                System.exit(0);
            }
            switch(choice)
            {
                case 1:
                    g.display_Stations();
                    break;

                case 2:
                    g.display_Map();
                    break;

                case 3:
                    ArrayList<String> keys = new ArrayList<>(vertices.keySet());
                    String codes[] = printCodelist();
                    System.out.println("\n1. TO ENTER SERIAL NO. OF STATIONS\n2. TO ENTER CODE OF STATIONS\n3. TO ENTER NAME OF STATIONS\n");
                    System.out.println("ENTER YOUR CHOICE:");
                    int ch = Integer.parseInt(inp.readLine());
                    int j;

                    String st1 = "", st2 = "";
                    System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
                    if (ch == 1)
                    {
                        st1 = keys.get(Integer.parseInt(inp.readLine())-1);
                        st2 = keys.get(Integer.parseInt(inp.readLine())-1);
                    }
                    else if (ch == 2)
                    {
                        String a,b;
                        a = (inp.readLine()).toUpperCase();
                        for (j=0;j<keys.size();j++)
                            if (a.equals(codes[j]))
                                break;
                        st1 = keys.get(j);
                        b = (inp.readLine()).toUpperCase();
                        for (j=0;j<keys.size();j++)
                            if (b.equals(codes[j]))
                                break;
                        st2 = keys.get(j);
                    }
                    else if (ch == 3)
                    {
                        st1 = inp.readLine();
                        st2 = inp.readLine();
                    }
                    else
                    {
                        System.out.println("Invalid choice");
                        System.exit(0);
                    }

                    HashMap<String, Boolean> processed = new HashMap<>();
                    if(!g.containsVertex(st1) || !g.containsVertex(st2) || !g.hasPath(st1, st2, processed))
                        System.out.println("THE INPUTS ARE INVALID");
                    else
                        System.out.println("SHORTEST DISTANCE FROM "+st1+" TO "+st2+" IS "+g.dijkstra(st1, st2, false)+"KM\n");
                    break;

                case 4:
                    System.out.print("ENTER THE SOURCE STATION: ");
                    String sat1 = inp.readLine();
                    System.out.print("ENTER THE DESTINATION STATION: ");
                    String sat2 = inp.readLine();

                    HashMap<String, Boolean> processed1= new HashMap<>();
                    System.out.println("SHORTEST TIME FROM ("+sat1+") TO ("+sat2+") IS "+g.dijkstra(sat1, sat2, true)/60+" MINUTES\n\n");
                    break;

                case 5:
                    System.out.println("ENTER THE SOURCE AND DESTINATION STATIONS");
                    String s1 = inp.readLine();
                    String s2 = inp.readLine();

                    HashMap<String, Boolean> processed2 = new HashMap<>();
                    if(!g.containsVertex(s1) || !g.containsVertex(s2) || !g.hasPath(s1, s2, processed2))
                        System.out.println("THE INPUTS ARE INVALID");
                    else
                    {
                        ArrayList<String> str = g.get_Interchanges(g.Get_Minimum_Distance(s1, s2));
                        int len = str.size();
                        System.out.println("SOURCE STATION : " + s1);
                        System.out.println("SOURCE STATION : " + s2);
                        System.out.println("DISTANCE : " + str.get(len-1));
                        System.out.println("NUMBER OF INTERCHANGES : " + str.get(len-2));
                        //System.out.println(str);
                        System.out.println("~~~~~~~~~~~~~");
                        System.out.println("START  ==>  " + str.get(0));
                        for(int i=1; i<len-3; i++)
                        {
                            System.out.println(str.get(i));
                        }
                        System.out.print(str.get(len-3) + "   ==>    END");
                        System.out.println("\n~~~~~~~~~~~~~");
                    }
                    break;

                case 6:
                    System.out.print("ENTER THE SOURCE STATION: ");
                    String ss1 = inp.readLine();
                    System.out.print("ENTER THE DESTINATION STATION: ");
                    String ss2 = inp.readLine();

                    HashMap<String, Boolean> processed3 = new HashMap<>();
                    if(!g.containsVertex(ss1) || !g.containsVertex(ss2) || !g.hasPath(ss1, ss2, processed3))
                        System.out.println("THE INPUTS ARE INVALID");
                    else
                    {
                        ArrayList<String> str = g.get_Interchanges(g.Get_Minimum_Time(ss1, ss2));
                        int len = str.size();
                        System.out.println("SOURCE STATION : " + ss1);
                        System.out.println("DESTINATION STATION : " + ss2);
                        System.out.println("TIME : " + str.get(len-1)+" MINUTES");
                        System.out.println("NUMBER OF INTERCHANGES : " + str.get(len-2));
                        //System.out.println(str);
                        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                        System.out.print("START  ==>  " + str.get(0) + " ==>  ");
                        for(int i=1; i<len-3; i++)
                        {
                            System.out.println(str.get(i));
                        }
                        System.out.print(str.get(len-3) + "   ==>    END");
                        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    }
                    break;
                default:  //If switch expression does not match with any case,
                    //default statements are executed by the program.
                    //No break is needed in the default case
                    System.out.println("Please enter a valid option! ");
                    System.out.println("The options you can choose are from 1 to 6. ");

            }
        }

    }
}
