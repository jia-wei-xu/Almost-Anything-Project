import java.util.HashSet;

//Car class to represent traffic on edges
public class Car 
{
    //Car position variables
    public Edge currentEdge;
    public boolean movingToB;
    public double progress;
    public double speed;
    public double x;
    public double y;

    //Car navigation variables
    public HashSet<Edge> visitedEdges = new HashSet<>();
    public Node goalNode;
    public Graph graph;

    //Constructor to randomly initialize car on an edge
    public Car(Edge edge) 
    {
        this.currentEdge = edge;
        this.movingToB = true;
        this.progress = 0.0;
        this.speed = Math.random() + 1.0;
        updatePosition();
    }

    //Movement logic for car
    public void move() 
    {
        //If no current edge, do nothing
        if (currentEdge == null) 
        {
            return;
        }
        
        //Move along current edge based on speed
        progress += speed / currentEdge.getDistance();
        
        //If reached end of edge, pick next edge to move on
        if (progress >= 1.0) 
        {
            progress = 0.0;
            Node targetNode;
            
            if (movingToB) 
            {
                targetNode = currentEdge.to;
            } 
            else 
            {
                targetNode = currentEdge.from;
            }
            
            if (!targetNode.isActive) 
            {
                movingToB = !movingToB;
                updatePosition();
                return;
            }
            
            visitedEdges.add(currentEdge);

            //Pick next edge based on weights
            Edge nextEdge = pickNextEdge(targetNode);
            
            //If no valid next edge, turn around
            if (nextEdge != null) 
            {
                currentEdge.cars.remove(this);
                currentEdge = nextEdge;
                currentEdge.cars.add(this);
                
                if (currentEdge.from == targetNode) 
                {
                    movingToB = true;
                } 
                else 
                {
                    movingToB = false;
                }
            } 
            else 
            {
                movingToB = !movingToB;
            }
        }
        
        updatePosition();
    }

    //Choose direction car goes based on weights
    private Edge pickNextEdge(Node node) 
    {
        int totalWeight = 0;
        
        //Calculate total weight of all possible edges
        for (Edge e : node.connections) 
        {
            if (e != null && e != currentEdge && !visitedEdges.contains(e)) 
            {
                Node targetNode = (e.from == node) ? e.to : e.from;
                if (targetNode.isActive) {
                    totalWeight += 1 + e.cars.size();
                }
            }
        }
        
        //If there are no valid edges, clear visited and return current edge (turn around)
        if (totalWeight == 0) 
        {
            visitedEdges.clear();
            return currentEdge;
        }
        
        int random = (int) (Math.random() * totalWeight);
        int count = 0;
        
        //Pick edge based on weighted random selection
        for (Edge e : node.connections) 
        {
            if (e != null && e != currentEdge && !visitedEdges.contains(e)) 
            {
                Node targetNode = (e.from == node) ? e.to : e.from;
                if (targetNode.isActive) {
                    count += 1 + e.cars.size();
                    if (random < count) 
                    {
                        visitedEdges.add(e);
                        return e;
                    }
                }
            }
        }
        
        return currentEdge;
    }


    //Update car's x and y position based on current edge and progress
    private void updatePosition() 
    {
        if (currentEdge == null) 
        {
            return;
        }
        
        Node startNode;
        Node endNode;
        
        if (movingToB) 
        {
            startNode = currentEdge.from;
            endNode = currentEdge.to;
        } 
        else 
        {
            startNode = currentEdge.to;
            endNode = currentEdge.from;
        }
        
        x = startNode.x + (endNode.x - startNode.x) * progress;
        y = startNode.y + (endNode.y - startNode.y) * progress;
    }
}
