import java.util.HashSet;

public class Car 
{
    public Edge currentEdge;
    public boolean movingToB;
    public double progress;
    public double speed;
    public double x;
    public double y;
    public HashSet<Edge> visitedEdges = new HashSet<>();
    public Node goalNode;
    public Graph graph;

//spawn traffic
    public Car(Edge edge) 
    {
        this.currentEdge = edge;
        this.movingToB = true;
        this.progress = 0.0;
        this.speed = Math.random() * 2.0 + 1.0;
        updatePosition();
    }

//movement logic
    public void move() 
    {
        if (currentEdge == null) 
        {
            return;
        }
        
        progress += speed / currentEdge.getDistance();
        
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
            Edge nextEdge = pickNextEdge(targetNode);
            
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

//choose direction car goes based on weights
    private Edge pickNextEdge(Node node) 
    {
        int totalWeight = 0;
        
        // Calculate total weight of all possible edges
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
        
        // If there are no valid edges, clear visited and return current edge (turn around)
        if (totalWeight == 0) 
        {
            visitedEdges.clear();
            return currentEdge;
        }
        
        int random = (int) (Math.random() * totalWeight);
        int count = 0;
        
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
