import javafx.event.*;
import javafx.scene.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.canvas.*;
import javafx.scene.layout.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.stage.*;
import java.util.*;
import javafx.scene.paint.Color;
import java.io.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

public class AI
{
   //this is for the image
   ImagePattern ptn;
   
   public AI()
   {
      Image img = new Image("bubble.png");  // you can do whatever, but it should be unique for your AI.
      ptn = new ImagePattern(img);
   }
   
   //give it a good name :). It will appear above you AI when I test it in class.
   public String getName()
   {
      return "Alina BOTT";
   }  
   
   
   Mood_GraphB theGraph;
   
   ////NOTE: this method is called in the Main. It is one of the AI's hooks.
   //setup code for your AI goes here.
   public void start(Level.LevelIterator currentLevel, double px, double py)
   {
      //reminder: px and py are the upper left coord of the player.
      //two classes you need to deal with:
      
      //Level.LevelIterator. It has two ways to get the tiles.
      //way #1 is iterate over all the tiles (in a non-specific order). See the graph's contructor for example usage
      //way #2 is to grab a specific tile from it's x and y. This is demonstrated in the graph's updateBreak method.
   
      //Level.TileWrapper. I wasn't going to let you have direct access to the tiles, so I package all the info for the tile into a "wrapper" class.
      //It has methods to get all the info you could want
      // getX - get X coord
      // getY - get Y coord
      // getName - get Tiletype's name
      // getIsStart - get if its the start tile
      // getIsEnd - get if its the goal tile
      // getIsBreak - get if it is a break tile
      // getBreakTimer - get the amount of time left on the break timer. -10 means not broken or not breakable. a num >= 0 means that is the time the tile has left.
      // getMaxBreakTimer - the Total time of the break timer.-10 means it doesn't break
      // getIsCollionable - whether a particular tile can be moved through.
   
      //creating a graph
      theGraph = new Mood_GraphB(currentLevel);
   
   }
   
   ////NOTE: this method is called in the Main. It is one of the AI's hooks.
   //code that runs before each tick goes here.
   public void runEachTick(Level.LevelIterator currentLevel, double px, double py, double xv, double yv, double jump, boolean isGrounded)
   {
      //reminder: px and py are the upper left coord of the player.
      //xv and yv is the current velocity
      //jump is how much "jump" will get added the next physics step (>0 is in the act of jumping)
      //isGrounded is whether the player is currently on the ground (i.e., will jump if you set jumpDown=true).
      //System.out.println("start tick");
      theGraph.updateBreak(currentLevel);
            
      Mood_Node whereIAm = theGraph.nodeAt(px,py);
      Mood_Node goalNode = theGraph.getGoal();
      
      if(whereIAm != null && goalNode != null)
      {
         ArrayList<Mood_Node> tempList = theGraph.dijkstra(whereIAm,goalNode);
         
         if(tempList.size()>1)
         {
            Mood_Node whereIWantToGoNext = tempList.get(tempList.size()-2);
            
            Mood_MovementType way = whereIAm.howGetTo(whereIWantToGoNext);

            if(way == Mood_MovementType.RIGHT)
            {
               aDown = false;
               dDown = true;
               jumpDown = false;          
            }
            else if(way == Mood_MovementType.LEFT)
            {
               aDown = true;
               dDown = false;
               jumpDown = false;               
            }
            else if(way == Mood_MovementType.JUMP && isGrounded)
            {
               boolean atRightEdge = T5_atRightEdge(whereIAm, px);
               boolean atLeftEdge = T5_atLeftEdge(whereIAm, px);
               
               if(!atRightEdge)
               {
                  aDown = false;
                  dDown = true;
                  jumpDown = true;
               }
               else if(!atLeftEdge)
               {
                  aDown = true;
                  dDown = false;
                  jumpDown = true;
               }
               else
               {
                  aDown = false;
                  dDown = false;
                  jumpDown = true;
               }
            }
            else
            {
               aDown = false;
               dDown = false;
               jumpDown = false;                 
            }
         
         }
      }
   }
   
   boolean atRightEdge;
   public boolean T5_atRightEdge(Mood_Node whereIAm, double px)
   {
      double playerX = whereIAm.getX();
      double playerY = whereIAm.getY();
      int tileSpaceX =(int) playerX/30;
      int tileSpaceY = (int)playerY/30;
      
      HashMap<String,String> isThereATileThere = theGraph.T5_getHashMap();
      
      if(isThereATileThere.get((tileSpaceX+1) + "_" + tileSpaceY) != null)
      {
         if(playerX != px)
         {
            if(playerX < px)
            {
               atRightEdge = false;
            }
         }
         else
         {
            atRightEdge = true;
         }
      }
      return atRightEdge;  
   }
      
    
   boolean atLeftEdge;
   public boolean T5_atLeftEdge(Mood_Node whereIAm, double px)
   {
      double playerX = whereIAm.getX();
      double playerY = whereIAm.getY();
      int tileSpaceX = (int)playerX/30;
      int tileSpaceY = (int)playerY/30;
      
      HashMap<String,String> isThereATileThere = theGraph.T5_getHashMap();
      
      if(isThereATileThere.get((tileSpaceX-1) + "_" + tileSpaceY) != null)
      {
         if(playerX != px)
         {
            if(playerX > px)
            {
               atLeftEdge = false;
            }
         }
         else
         {
            atLeftEdge = true;
         }
      }
      return atLeftEdge;
   }
   
   ////NOTE: this method is called in the Main. It is one of the AI's hooks.
   //whatever you want to draw should be put here.
   public void drawAIInfo(GraphicsContext gc)
   {
      theGraph.draw(gc);
   }
   

  
   //the game reads these three variables / methods to determine if the AI wishes to press the keys down. 
   protected boolean aDown;
   protected boolean dDown;
   protected boolean jumpDown;
   
   public boolean isADown()
   {
      return aDown;
   }
   
   public boolean isDDown()
   {
      return dDown;
   }
   
   public boolean isJumpDown()
   {
      return jumpDown;
   }
   
   
   //this method is called once the AI tries to jump in from GameObject. Will be called regardless of successful jump. Use isGrounded to determine if in the air.
   public void jumped()
   {
      jumpDown = false;
   }
   
   
   //this is used by a calling method to get the AI's image.
   public ImagePattern getFill()
   {
      return ptn;
   }
   
   //NOTE: this method is called in the Main. It is one of the AI's hooks. For if you want to use the mouse to debug
   public void clicked(int x, int y)
   {
      theGraph.clickPoint(x,y);
   }
   
   
   //I did these are inner classes, but you don't have to do. 
   // you must put your team name + underscore (like P1_ as a prefix to whatever your classes are)
   public class Mood_GraphB
   {
   
      ArrayList<Mood_Node> theNodes = new ArrayList<Mood_Node>();
      ArrayList<Mood_Node> breakNodes = new ArrayList<Mood_Node>();
      ArrayList<Mood_Node> gapNodes = new ArrayList<Mood_Node>();
      
      Mood_Node goal;
      
      HashMap<String,String> isThereATileThere;
   
      //creating the graph as we talked about in class.
      public Mood_GraphB(Level.LevelIterator graphToCreate)
      {
         isThereATileThere = new HashMap<String,String>();
      
         //this is soooo much nice than the previous code we did in class. Iterators are your friends :)
         graphToCreate.resetIterator();
         while(graphToCreate.hasNext())
         {
            Level.TileWrapper tw = graphToCreate.getNext();
            isThereATileThere.put(tw.getX()+"_"+tw.getY(),"YES!");
            
            
         }
      
         graphToCreate.resetIterator();
         while(graphToCreate.hasNext())
         {
            int x = 0;
            int y = 0;     
            boolean start;
            boolean end;
            boolean isBreak;
            boolean collides;
            double maxBreak;
            
            Level.TileWrapper tw = graphToCreate.getNext();
            {
               
               start = tw.getIsStart();
               end = tw.getIsEnd();
               isBreak = tw.getIsBreak();
               collides = tw.getIsCollisionable();             
               
               x = tw.getX();
               y = tw.getY();
               
               maxBreak = tw.getMaxBreakTimer();
            }
            
            /*
            
            //gap nodes
            for (int i = 2; i < 4; i++) {
               if ((isThereATileThere.get((x + i - 1) + "_" + y) == null) &&
                  (isThereATileThere.get((x + i) + "_" + y) != null)) {
        
                  if (!end && !start) {
                     int maxNodes = i - 1; // Number of additional nodes to add for each `i`
            
                     for (int j = 30; j < 180; j += 30) {
                         for (int k = 1; k <= maxNodes; k++) {
                           theNodes.add(new Mood_Node(x * 30 + k * 30, y * 30 - j));
                           gapNodes.add(new Mood_Node(x * 30 + k * 30, y * 30 - j));
                           }
                     }
                  }
               }
            } */

            
            if(isThereATileThere.get(x+"_"+(y-1))== null)
            {
               if(!end && !start) //inside this if statement creates 6 nodes above solely ground tiles
               {
                  theNodes.add(new Mood_Node(x*30, y*30-30));
                  //this adds five additional nodes above the tile checking to make sure there is no tile
                  for(int i=1;i<6;i++)
                  {
                     if(isThereATileThere.get(x+"_"+(y-(i+1)))== null)
                     {
                        theNodes.add(new Mood_Node(x*30, y*30-((i+1)*30)));
                     }
                     else
                     {
                        break;
                     }
                  }
               }
               else if(end || start) // inside this if statement handles the creation of nodes in start & goal cases but also 5 nodes above them
               {
                  theNodes.add(new Mood_Node(x*30,y*30));
                  //this adds five additional nodes above the start and goal tiles checking to make sure there is no tile
                  for(int i=0;i<5;i++)
                  {
                     if(isThereATileThere.get(x+"_"+(y-(i+1)))== null)
                     {
                        theNodes.add(new Mood_Node(x*30, y*30-((i+1)*30)));
                     }
                     else
                     {
                        break;
                     }
                  } 
               }
               
               //keep track of a list of break nodes as well
               if(isBreak)
               {
                   theNodes.add(new Mood_Node(x*30, y*30));
                   breakNodes.add(new Mood_Node(x*30, y*30));
                   theNodes.get(theNodes.size()-1).setBreakMax(maxBreak);
               }
               else
               {
                  theNodes.get(theNodes.size()-1).setBreakAmount(-9); //-10 means not stepped on but breakable (according to my game) and -9 here means not breakable 
               
               }
            } 
            else if(start || end || isBreak || !collides)
            {
               if (!collides) {
                  theNodes.add(new Mood_Node(tw.getX()*30, tw.getY()*30));
               }else if(isBreak)
               {
                   theNodes.add(new Mood_Node(x*30, y*30));
                   breakNodes.add(new Mood_Node(x*30, y*30));
                   theNodes.get(theNodes.size()-1).setBreakMax(maxBreak);
               }
               else
               {
               theNodes.add(new Mood_Node(x*30, y*30));
               }
            }
            
            if(end)
            {
               goal = theNodes.get(theNodes.size()-1);
            }
         }   
         
         int cap = theNodes.size();
         
         boolean breakTileUnderGoal = false;
         for(int i=0; i<breakNodes.size();i++)
         {
            Mood_Node breaknode = breakNodes.get(i);
            if((goal.getX() == breaknode.getX()) && (goal.getY()+30) == breaknode.getY())
            {
                    //means there is a break node under the goal
                    breakTileUnderGoal = true;
            }   
         }      
      
         //N^2, could be better. Sort the nodes first by either x or y and just do the nodes that are nearby in the list.
         for(int i=0;i<theNodes.size();i++)
         {
            for(int j=i+1;j<theNodes.size();j++)
            {
               if(i != j)
               {
                  Mood_Node n1 = theNodes.get(i);
                  Mood_Node n2 = theNodes.get(j);
                  
                  //boolean to check if n1 or n2 is a break node
                  //because if it's a break node then we do not want the have it make horizontal connections 
                  boolean isBreakNode = false;
                  for(int k=0; k<breakNodes.size(); k++)
                  {
                      Mood_Node breaknode = breakNodes.get(k);
                      if(n1.getX() == breaknode.getX() && n1.getY() == breaknode.getY())
                      {
                           isBreakNode = true;
                      }
                      
                      if(n2.getX() == breaknode.getX() && n2.getY() == breaknode.getY())
                      {
                           isBreakNode = true;
                      }           
                  }
               
                  double d = Math.sqrt((n1.getX()-n2.getX())*(n1.getX()-n2.getX()) + (n1.getY()-n2.getY())*(n1.getY()-n2.getY()));
                  if(d<35)
                  {
                     if(n1.getX() < n2.getX() && n1.getY() == n2.getY() && isBreakNode == false) //this is when n1 is to the left of n2 on the same y plane
                     {
                        n1.addMovementType(Mood_MovementType.RIGHT);
                        n2.addMovementType(Mood_MovementType.LEFT);
                        n1.addConnection(n2);
                        n2.addConnection(n1);
                     
                        n1.addMovementTime(1);
                        n2.addMovementTime(1);
                           
                     }
                     else if (n1.getX() > n2.getX() && n1.getY() == n2.getY() && isBreakNode == false) //this is when n1 is to the right of n2 on the same y plane
                     {
                        n1.addMovementType(Mood_MovementType.LEFT);
                        n2.addMovementType(Mood_MovementType.RIGHT);   
                           
                        n1.addConnection(n2);
                        n2.addConnection(n1);
                     
                        n1.addMovementTime(1);
                        n2.addMovementTime(1);                 
                     }
                     else if(n1.getX() == n2.getX() && n1.getY() > n2.getY()) //handles when n1 is above n2 but on the same x plane
                     {
                        if(isBreakNode == true && breakTileUnderGoal == true)
                        {
                        
                        }
                        else
                        {
                           n1.addMovementType(Mood_MovementType.JUMP);
                           n2.addMovementType(Mood_MovementType.NONE);   
                              
                           n1.addConnection(n2);
                           n2.addConnection(n1);
                        
                           n1.addMovementTime(2);
                           n2.addMovementTime(2);
                        } 
                     }
                     else if(n1.getX() == n2.getX() && n1.getY() < n2.getY()) //handles when n1 is below n2 but on the same x plane
                     {
                        n1.addMovementType(Mood_MovementType.JUMP);
                        n2.addMovementType(Mood_MovementType.NONE);   
                           
                        n1.addConnection(n2);
                        n2.addConnection(n1);
                     
                        n1.addMovementTime(2);
                        n2.addMovementTime(2); 
                     }
                     
                     /*
                     for (Mood_Node gapNode : gapNodes) {
                        if (n1.getX() == n2.getX() && n1.getY() < n2.getY() && isBreakNode == false && gapNode.getX() == n1.getX() && gapNode.getY() == n1.getY()) //this is when n1 is to the right of n2 on the same y plane
                        {
                           n1.addMovementType(Mood_MovementType.JUMP);
                           n2.addMovementType(Mood_MovementType.NONE);   
                              
                           n1.addConnection(n2);
                           n2.addConnection(n1);
                        
                           n1.addMovementTime(2);
                           n2.addMovementTime(2);                 
                        } 
                        if (n1.getX() > n2.getX() && n1.getY() == n2.getY() && isBreakNode == false && gapNode.getX() == n1.getX() && gapNode.getY() == n1.getY()) //this is when n1 is to the right of n2 on the same y plane
                        {
                           n1.addMovementType(Mood_MovementType.RIGHT);
                           n2.addMovementType(Mood_MovementType.LEFT);   
                              
                           n1.addConnection(n2);
                           n2.addConnection(n1);
                        
                           n1.addMovementTime(2);
                           n2.addMovementTime(2);                 
                        }
                     } */

                  } 
               } 
            } 
         }
      
      }
      
      public HashMap<String,String> T5_getHashMap()
      {
         return isThereATileThere;
      }
      
      public Mood_Node getGoal()
      {
         return goal;
      }
      
      public double distance(double x1, double y1, double x2, double y2)
      {
         double d = Math.sqrt((x1 - x2)*(x1-x2) + (y1-y2)*(y1-y2));
         return d;  
      }
      
      public Mood_Node nodeAt(double x, double y)
      {
         int shortestIndex = 0;
         double shortestDistance = distance(x,y,theNodes.get(0).getX(),theNodes.get(0).getY());
         
         for(int i=1;i<theNodes.size();i++)
         {
            double t;
            if((t = distance(x,y,theNodes.get(i).getX(),theNodes.get(i).getY())) < shortestDistance)
            {
               shortestIndex = i;
               shortestDistance = t;
            }
         }
         
         return theNodes.get(shortestIndex);
      }
      
      
      //drawing the graph
      public void draw(GraphicsContext gc)
      {
         for(int i=0;i<theNodes.size();i++)
         {
            theNodes.get(i).draw(gc);
         }
      }
      
      public void removeNodeFromGraph(Mood_Node theNode)
      {
         //remove the node from the main list
         for(int i=0;i<theNodes.size();i++)
         {
            if(theNodes.get(i) == theNode)
            {
               theNodes.remove(i);
               break;
            }
         }
         
         //remove the connections
         theNode.destroy();
      }
      
      public void updateBreak(Level.LevelIterator currentLevel)
      {
         //loop over all break nodes
         for(int i=0;i<breakNodes.size();i++)
         {
            //might have been better to leave the nodes in tileSpace.
            //I had to figure out what was wrong with my math. 
            //System.out.println(breakNodes.get(i).getX()/30+" "+(breakNodes.get(i).getY()+30)/30);
            
            //get a particular breakNode's tile wrapper.
            Level.TileWrapper tw = currentLevel.getSpecificTile(breakNodes.get(i).getX()/30,(breakNodes.get(i).getY()+30)/30);
            
            if(tw == null) //so the node no longer exsits. this means it broke.
            {
               removeNodeFromGraph(breakNodes.get(i));
               breakNodes.remove(i);
               i--;
            }
            else //otherwise update the timer.
            {
               breakNodes.get(i).setBreakAmount(tw.getBreakTimer());
            }
         }
      }
      
      
      
      //takes in tilespace points
      public void clickPoint(int x, int y)
      {
      
         //this is because the the nodes are the upper left coords and the x and y are the center.
         x-=15; 
         y-=15; 
         
         for(int i=0;i<theNodes.size();i++)
         {
            Mood_Node n1 = theNodes.get(i);
         
            double d = Math.sqrt((n1.getX()-x)*(n1.getX()-x) + (n1.getY()-y)*(n1.getY()-y));
         
         
            //if distance is within 20 px of the clicked tile.  
            if(d < 20)
            {
               
               
               if(start == null)
               {
                  start = n1;
                  n1.clicked(1);
               }
               else if(end == null)
               {
                  end = n1;
                  n1.clicked(0);
                  
                  dijkstra(start,end);
               }
               else
               {
                  n1.clicked(-1);
               }
            }
         }
      }
      
      
      Mood_Node start=null;
      Mood_Node end = null;
      
      ArrayList<Mood_Node> path = new ArrayList<Mood_Node>();
      
      int runCounter=0;
      
      public ArrayList<Mood_Node> dijkstra(Mood_Node start, Mood_Node end)
      {
         //unweighted dijkstra
         Mood_Node current = start;
         //LinkedList<Mood_Node> myQueue = new LinkedList<Mood_Node>(); //use priority queue in weighted dijsktra
         
         PriorityQueue<Mood_Node> priorityQueue = new PriorityQueue<>();
         
         priorityQueue.add(current);
         
         int currentRun = runCounter++;
         current.setLastUsed(runCounter);
         current.setBackPointer(null);
         current.setDistance(0);
         
         while(priorityQueue.size()>0 && current != end)
         {
            current = priorityQueue.poll(); //get and remove first element.
            
            for(int i=0;i<current.getSize();i++)
            {
               Mood_Node temp = current.get(i);
               if(temp.getLastUsed() != runCounter)
               {
                  
                  temp.setLastUsed(runCounter);
                  temp.setBackPointer(current);
                  temp.setDistance(current.getDistance()+current.getEdgeWeight(i));
                  
                  priorityQueue.add(temp);
                  
                  //System.out.println(temp.getX()+" "+temp.getY() +" "+temp.getDistance());
                  //set distance in weighted dijkstra
               }
               else
               {
                  //have to check if the path distance is < current distance in weighted dijkstra
                  if(temp.getDistance() > current.getDistance()+current.getEdgeWeight(i))
                  {
                     temp.setDistance(current.getDistance()+current.getEdgeWeight(i));
                     temp.setBackPointer(current);
                     priorityQueue.remove(temp);
                     priorityQueue.add(temp);
                     //System.out.println(temp.getX()+" "+temp.getY() +" "+temp.getDistance());
                  }
               }
            }
         }
         path.clear();
        
         //trace back back in the graph. I put a little safety code in place.
         current = end;
         
         if(end != null)
         {
            
            while(current != start && current != null)    
            {
               path.add(current);
               current.clicked(2);
               current = current.getBackPointer();
            }
            if(current != null)
            {
               path.add(current);
            }
            else
            {
               //System.out.println("Current is null in AI.java");
            }
                 
         }
         else
         {
            //System.out.println("End is null in AI.java");
         }
         
         return path;
      }
   }
   
   public enum Mood_MovementType {LEFT,RIGHT,NONE,JUMP};
   
   public class Mood_Node implements Comparable<Mood_Node>
   {
      //connections between nodes
      ArrayList<Mood_Node> connections = new ArrayList<Mood_Node>();
      ArrayList<Mood_MovementType> howToMove = new ArrayList<Mood_MovementType>();
      ArrayList<Double> timeToMove = new ArrayList<Double>();
   
      int x,y;
      
      //for IDs...
      int id;
      static int idgen=0;
      
      double currentBreakAmount=0; //in my program -10 on a tile means not broken or not breakable (use tw.getIsBreak() to deteremine the diff). -9 in my implementaion means not breakable. and a positive number is how much time is left
      double maxBreakAmount=0;
      
      Mood_Node backPointer=null;
      double distance=0;
      
      
      
      public Mood_Node(int _x, int _y)
      {
         x = _x;
         y = _y;
         
         id = idgen++;
      }
      
      public String toString()
      {
         String toString = ("Node at : " + x + " " + y);
         return toString;
      }
      
      public double getDistance()
      {
         return distance;
      }
      public void setDistance(double  dist)
      {  
         distance = dist;
      }
      
      public int compareTo(Mood_Node other) {
         return Double.compare(this.distance, other.distance);
      }
      
      public int getX()
      {
         return x;
      }
      
      public int getY()
      {
         return y;
      }
      
      //name of a tile is x_y
      public String getName()
      {
         return x+"_"+y;
      }
   
      public void addConnection(Mood_Node toAdd)
      {
         connections.add(toAdd);
      }
      
      public void addMovementType(Mood_MovementType toAdd)
      {
         howToMove.add(toAdd);
      }
      
      public void addMovementTime(double d)
      {
         timeToMove.add(d);
      }
      
      //for keeping track of break tiles.
      public void setBreakAmount(double d)
      {
         currentBreakAmount = d;
      }
      
      public void setBreakMax(double d)
      {
         maxBreakAmount = d;
      }
      
      //these methods were for dijsktra
      public int getSize()
      {
         return connections.size();
      }
      
      int lastUsed = -1;
      
      public int getLastUsed()
      {
         return lastUsed;
      }
      
      public void setLastUsed(int val)
      {
         lastUsed = val;
      }
      boolean inQueue = false;
      
      public Mood_Node get(int i)
      {
         return connections.get(i);
      }
      
      Color fillColor = Color.YELLOW;
      
      boolean isClicked = false;
      
      //clicked method to change colors. this is really for debugging
      public void clicked(int option)
      {
         isClicked = true;
         fillColor = new Color(0,1,0,1);
         
         if(option == 0)
         {
            fillColor = Color.PINK;
         }
         if(option == 1)
         {
            fillColor = Color.PURPLE;
         }
         if(option == 2)
         {
            fillColor = Color.BLUE;
         }
      }
   
      public void draw(GraphicsContext gc)
      {
         //if(isClicked)
         {
            //draw all this nodes's connections. NOTE: my implementation doesn't remove connections from each node when a node breaks.
            for(int i=0;i<connections.size();i++)
            {
               if(howToMove.get(i) == Mood_MovementType.LEFT)
               {
                  gc.setStroke(Color.BLUE);
               }
               else if(howToMove.get(i) == Mood_MovementType.RIGHT)
               {
                  gc.setStroke(Color.RED);
               }
               else if(howToMove.get(i) == Mood_MovementType.JUMP)
               {
                  gc.setStroke(Color.CYAN);
               }
               else if(howToMove.get(i) == Mood_MovementType.NONE)
               {
                  gc.setStroke(Color.GREEN);
               }
            
               
               gc.setLineWidth(3);
               gc.strokeLine(x+8+7,y+8+7,connections.get(i).x+8+7,connections.get(i).y+8+7);
            }
         }
         
         if(currentBreakAmount == -10)
         { //-10 means not stepped on or not breakable. You can do getIsBreak() from the tw if you want.
            gc.setFill(fillColor);
         }
         else if(currentBreakAmount == -9)
         {
            gc.setFill(fillColor); //someoen really wanted Cyan, don't remember who.
         }
         else
         {
            gc.setFill(Color.BLACK.interpolate(Color.WHITE,currentBreakAmount/maxBreakAmount)); //color based on break amount %
         }
         
         gc.setFill(fillColor);
         gc.fillOval(x+8,y+8,14,14);
      }
      
      
      
      public void setBackPointer(Mood_Node theThing)
      {
         backPointer = theThing;
      }
      
      public Mood_Node getBackPointer()
      {
         return backPointer;
      }
      
      public Mood_MovementType howGetTo(Mood_Node other)
      {
         for(int i=0;i<connections.size();i++)
         {
            if(connections.get(i)==other)
            {
               return howToMove.get(i);
            }
         }
         
         return Mood_MovementType.NONE;
      }
      
      //this method should remove all the connections from corresponding arrayLists
      public void destroy()
      {
         for(int i=0;i<connections.size();i++)
         {
            Mood_Node temp = connections.get(i);
            if(temp == this)
            {
               temp.connections.remove(i);
               temp.howToMove.remove(i);
               temp.timeToMove.remove(i);
            }
         }
      }
      
      public double getEdgeWeight(int i)
      {
         return timeToMove.get(i);
      }
   }
}