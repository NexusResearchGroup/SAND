
public class DijkstrasAlgo {
	
	public int vertices; // the total number of nodes
	public int s[][];//permanent nodes,vector;the first dimension is the nodenumber of Origin, the second dimension is number of element
	public int vs[][];//remaining nodes,array;first dimension the same, the second dimesnion is the number of node
	public double d[][];//generalized cost,array;same with vs
	public double d0[][];//O-D travel time from the preceding iteration
	public int pi[][];//predecessor,array; same with vs
	public float valueofTime;
	double INF;
	
	public DijkstrasAlgo(DirectedGraph dg) {
		INF=dg.INF ;
		vertices = dg.Vertices();
		s=new int[vertices][vertices];
		vs=new int[vertices][vertices];
		d=new double[vertices][vertices];
		d0=new double[vertices][vertices];
		pi=new int[vertices][vertices];
	}
	
	public void dijkstrasalgo(DirectedGraph  dgraph) {
		
		//finding the shortest path...
		//the permanent elements along the shortest path are stored into s vector, element by element
		//the status of each node (permanent:-1/available:0/unavailabe:1) are stored in vs
		// the generalized cost(distance) from the origin to each node are  stored in d
		//the precedecessor of each node on the shortest path is stored in pi
		//System.out.print("\n* Dijkstra's Algorithm running...one period per 10 centroids\n");
		System.out.print("	Dijkstra's Algorithm running...0%");
		for(int ithNode=0;ithNode<vertices;ithNode++){//given an origin node...
			
//			if((ithNode+1)%48==0){
//				System.out.print(".");
//				if((ithNode+1)%240==0){
//					System.out.print((ithNode+1)/12+"%");
//				}	
//			}
			
			for(int j=0;j<vertices;j++)
				{	//initializing..
					d0[ithNode][j]=d[ithNode][j];
					s[ithNode][j]=-1;vs[ithNode][j]=1;d[ithNode][j]=INF;pi[ithNode][j]=-1;
				}	
			//the first element..				
			s[ithNode][0]=ithNode+1;
			d[ithNode][ithNode]=0;	
			vs[ithNode][ithNode]=-1;
			
			for(int jthElement=0;jthElement<vertices-1;jthElement++){
				//in each step, we will find the following element of the jth element along the shortest path)
				 
					int previousNode=s[ithNode][jthElement];
					int NoofLinks=dgraph.NoofLinks(previousNode);
					for(int kthLink=0;kthLink<NoofLinks;kthLink++){
						int EndNodeNumber=dgraph.EndNodeNumbers( previousNode, kthLink+1 );
						//if(EndNodeNumber<1||EndNodeNumber>vertices)System.out.print("Wrong!"+EndNodeNumber+"\t"+previousNode+"\t"+(kthLink+1)+"\n");
						//relaxing (make the unavailable nodes connected to the recent permanent elment available)...
						if(vs[ithNode][EndNodeNumber-1]!=-1)	// if the node has not been labelled permanent
							{
								double glt=dgraph.link_info [(int)dgraph.linkID [previousNode-1][ kthLink]-1][10];
								if(true){
									if(d[ithNode][EndNodeNumber-1]>d[ithNode][previousNode-1]+glt)
										//if the node is unavailabe, the statement will be true for sure
										//if the node has already avaible, this statement compared the previous cost and the cost through previous node
									
										{					
											//if the cost is smaller through the previous node
											//we will change the cost corresponding to the node as well as its predecessor
											d[ithNode][EndNodeNumber-1]=d[ithNode][previousNode-1]+glt;
											pi[ithNode][EndNodeNumber-1]=previousNode;
											vs[ithNode][EndNodeNumber-1]=0;
										}	
									//System.out.println(EndNodeNumber+"\n"+d[ithNode][EndNodeNumber-1]);
									
								}
							}
							 
					}

	
					//find the next permanent element
					//among all the available nodes,we will find the one with smallest cost and label it as the next permanent
				double dtemp=1.0E99;
				int nodetemp=-1;
				
				for(int j=0;j<vertices;j++){
					if(vs[ithNode][j]==0)
					{
						if(dtemp>d[ithNode][j]){
								
							dtemp=d[ithNode][j];
							nodetemp=j+1;
									
						}	
						
						else if(dtemp==d[ithNode][j])
							{
								if(j<nodetemp-1)
									nodetemp=j+1;									
							}
							
					}
				}				
				
				s[ithNode][jthElement+1]=nodetemp;
				if(nodetemp>=1)
					vs[ithNode][nodetemp-1]=-1;
				else
					break;
			}	
									
		}
		
		System.out.print("\n");	
	}			

	public void dijkstrasalgo(DirectedGraph  dgraph, double genLinkTime[]) {
		
		//finding the shortest path...
		//the permanent elements along the shortest path are stored into s vector, element by element
		//the status of each node (permanent:-1/available:0/unavailabe:1) are stored in vs
		// the generalized cost(distance) from the origin to each node are  stored in d
		//the precedecessor of each node on the shortest path is stored in pi
		//System.out.print("\n* Dijkstra's Algorithm running...one period per 10 centroids\n");
		//System.out.print("Iterations...\n");
//		for(int i=0;i<dgraph.Edges();i++){
//			if((i+1)%1000==0)System.out.print(genLinkTime [i]+"\t");
//		}
//		System.out.print("\n");

		System.out.print("	Dijkstra's Algorithm running...0%");

		for(int ithNode=0;ithNode<vertices;ithNode++){//given an origin node...
			

			for(int j=0;j<vertices;j++)
				{	//initializing..
					s[ithNode][j]=-1;vs[ithNode][j]=1;d[ithNode][j]=INF;pi[ithNode][j]=-1;
				}	
			//the first element..				
			s[ithNode][0]=ithNode+1;
			d[ithNode][ithNode]=0;	
			vs[ithNode][ithNode]=-1;
			
			for(int jthElement=0;jthElement<vertices-1;jthElement++){
				//in each step, we will find the following element of the jth element along the shortest path)
				 
					int previousNode=s[ithNode][jthElement];
					int NoofLinks=dgraph.NoofLinks(previousNode);
					for(int kthLink=0;kthLink<NoofLinks;kthLink++){
						int EndNodeNumber=dgraph.EndNodeNumbers( previousNode, kthLink+1 );
						//if(EndNodeNumber<1||EndNodeNumber>vertices)System.out.print("Wrong!"+EndNodeNumber+"\t"+previousNode+"\t"+(kthLink+1)+"\n");
						//relaxing (make the unavailable nodes connected to the recent permanent elment available)...
						if(vs[ithNode][EndNodeNumber-1]!=-1)	// if the node has not been labelled permanent
							{
								double glt=genLinkTime[(int)dgraph.linkID [previousNode-1][ kthLink]-1];
								if(true){
									if(d[ithNode][EndNodeNumber-1]>d[ithNode][previousNode-1]+glt)
										//if the node is unavailabe, the statement will be true for sure
										//if the node has already avaible, this statement compared the previous cost and the cost through previous node
									
										{					
											//if the cost is smaller through the previous node
											//we will change the cost corresponding to the node as well as its predecessor
											d[ithNode][EndNodeNumber-1]=d[ithNode][previousNode-1]+glt;
											pi[ithNode][EndNodeNumber-1]=previousNode;
											vs[ithNode][EndNodeNumber-1]=0;
										}	
									//System.out.println(EndNodeNumber+"\n"+d[ithNode][EndNodeNumber-1]);
									
								}
							}
							 
					}

	
					//find the next permanent element
					//among all the available nodes,we will find the one with smallest cost and label it as the next permanent
				double dtemp=1.0E99;
				int nodetemp=-1;
				
				for(int j=0;j<vertices;j++){
					if(vs[ithNode][j]==0)
					{
						if(dtemp>d[ithNode][j]){
								
							dtemp=d[ithNode][j];
							nodetemp=j+1;
									
						}	
						
						else if(dtemp==d[ithNode][j])
							{
								if(j<nodetemp-1)
									nodetemp=j+1;									
							}
							
					}
				}				
				
				s[ithNode][jthElement+1]=nodetemp;
				if(nodetemp>=1)
					vs[ithNode][nodetemp-1]=-1;
				else
					break;
			}	
				
					
		}
	
		System.out.print("\n");	
	}			

	////////////////////////////////
	

	public double pLabel(int node1, int node2, DirectedGraph dg) {//return the generanized cost of the shortest path between node1 and node2 including the access cost
		double retValue = 0;
		if((node1>=1 &&  node1 <=dg.Vertices())  && (node2>=1 &&  node2<=dg.Vertices()) ) {
			//if(this.d[node1-1][node2-1]==10E50)return(-1);
			if(node1==node2){
				return(dg.intracost);
			}
			else{
				retValue=this.d[node1-1][node2-1]+2*dg.intracost;
			}
		}
		else 
			System.out.println("!");
		return retValue;
			
	}
	
		
		
}