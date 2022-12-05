1- depthFirstEdgeVisitorWithTerminationPredicate : is Depth first walk over edges.
The algorithm takes :
1.1- a termination function       : to decide when to stop the walk (we may want to loop for some requirements)
1.2- a result collection function : we may decide to collect path, nodes, weights all along the way
                                  or only at destination nodes.
                                  
- Revisiting vertices is allwed.
- Revisiting edges is not allowed.

2- shortestPath : we have implemented an A* with Queue and a map for distances.
   unlike DFS, this implementation is not generic.


------------------------

