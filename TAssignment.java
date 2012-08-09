
//import java.io.*;
import java.text.*;

public class TAssignment {
	public double x[];
	double xp[];	
	double glt[];//generalized link travel time
	
	
	double INF;
	int edges;
	double theta=0.2*60;//0.2 is for travel time in minutes; .2*60 is for hours
	//The dispersion parameter theta is set at 0.2, following Leurent's (1995) work on 
	//case studies in the Paris metropolitan area. This means that if one route is shorter by five 
	//minutes than another, then approximately three out of four drivers will choose the first road. 
		
		
	//variables used in dial's algorithm	
	int vertices;
	int startnode,endnode,ed,linkid;//temporary variables
	double lk[],w[],x_org[],x_total[];
	double sumW[],sumX[];
	//sumW stores the sum of link weights (w) for all the links entering a node. if it is the origin node, sumW=1
	//sumX will be used for backward pass
	int rank[];
	
	
	public TAssignment( DirectedGraph dgraph) {
		//System.out.print("!!theta="+theta/60+"\n\n");	

		INF=dgraph.INF ;
		vertices=dgraph.Vertices() ;//total nodes
		edges=dgraph.Edges() ;//total links
		x = new double[edges];	//a vector to store the link flows
		glt= new double[edges];//a temporary array to store the bpr travel time of links for each iteration of MSA
		xp =  new double [edges];//a temporary array to store the flow pattern derived in the previous iteration of MSA
		theta=dgraph.theta;
		
		for(int i=0; i<edges ; i++) {
			x[i] =xp[i]=0;
			glt[i]=dgraph.link_info [i][10];
		}
		
		lk=new double [edges];// link likelihood
		w=new double [edges]; //link weight used in forward pass
		//x=new float [edges];//total link flows summing up all o-d pairs

		sumW=new double [vertices];
		sumX=new double[vertices];
		
		x_total=new double [edges];
		x_org=new double [edges];//link flows calculated for a specific origin (centroid) node
		
		rank=new int [vertices];
		for (int p=0;p<vertices;p++){
			rank[p]=p+1;//store node numbers
		}
		
	}


	public void trafficassignment(DirectedGraph dgraph, DijkstrasAlgo dal) {
		dgraph.updateLinkInfo() ;
		//All-or-nothing assignment 	
		
		/*
		int tempNode=0,leadingNode=0, followingNode=0;		
				
		for(int i=0; i<dgraph.Centroids (); i++) {	//// for each node of the graph as the origin of the shortest path
			for(int j=dgraph.Vertices()-1;j>=1; j--) {      
				//// for each element from the end of the shortest path
				//// find its previously connected permanent node along the shortest path until the orign ithNode is reached
				followingNode=dal.s[i][j];  //Node Number of the element in the permanent vector
				//System.out.println(followingNode+"follow\n");	
				if(followingNode<1){
				}
					
				else{
					leadingNode=dal.pi[i][followingNode-1];// the predecessor,which is DIRECTLY connected to the node 
					
					
					do
					{
						
						int K=-1;
					a:	for(int k=0;k<dgraph.NoofLinks(leadingNode);k++){
							if(dgraph.EndNodeNumbers( leadingNode, k+1 )==followingNode)
								{K=k;break a;} 
								
						}
						
						//System.out.println("i+1="+(i+1)+" following="+followingNode+" leadingNode="+leadingNode+" K="+K);
							
							x[dgraph.linkID [leadingNode-1][K]-1]+= dgraph.ODMatrix (i+1,dal.s[i][j]); ////
							//if(period==0)System.out.print("traffic="+TrafficonaLink[leadingNode-1].access(K)+"\tODMatrix="+ODMatrix(i, dalgo.s[i][j]-1, (float)dalgo.pLabel(i+1, dalgo.s[i][j]) )+"\tdenom="+denom[i]+"\n");
							followingNode=leadingNode;
							leadingNode=dal.pi[i][followingNode-1];							
						

					}	
					while(followingNode!=i+1);
						
				}
			}
						
		}
		
		System.out.print("F:\t");
		for(int p=0; p<dgraph.Edges() ; p++) {
			System.out.print(x[p]+"\t");
		}			
		System.out.print("\n");
		*/
		
		
	//Stochastic User Equilibrium (SUE)
			
		//initialization
		//int edges=dgraph.Edges() ;
		int startnode,endnode;//temporary variables
		//DijkstrasAlgo dal;		
		//dal=new DijkstrasAlgo(dgraph);
		
		//Specify convergence requirement
		int iternum=0,maxiternum=200;
		double errorcrit=0.5;		
		
		
		double error[];
		error=new double[maxiternum+1]; // a vector to store the error term (dependent on definition),showing the trend of convergence
		for(int i=0; i<=maxiternum ; i++) {
			error[i]=0;
		}

		//change2
		for(int i=0; i<edges ; i++) {
			x[i]=dgraph.link_info [i][7];
			//if((i+1)%1000==0)System.out.print(x [i]+"\t");
			x[i]=Math.round( 100*x[i])/100;
			//if((i+1)%1000==0)System.out.print(x[i]+"\t");
		}		
		System.out.print("\n");
		
		//change3
		for (int p=0;p<vertices;p++){
			rank[p]=p+1;//store node numbers
		}
		
		//System.out.print("	Initialization finished\n");
		//Refer to Sheffi's book p.327
		//0) Stochastic network loading based on a set of initial travel times
		
		//System.out.print("Network loading before MSA...\n");
		
		int linkno=-1;			
					
		do {		
		
			iternum=iternum+1;
			//System.out.print("\nMSA: Iteration "+ iternum+"\n");
			//0) store the flow pattern at the begin of a MSA iteration
			for(int p=0; p<edges; p++) {
				xp[p]=x[p];
			}
			
			//1) update link travel times
			//According to the BPR function,link travel time=free flow travel time*(1+0.15*(flow/capacity)**4)
			for(int p=0; p<edges; p++) {
				double vcratio=0;
				if(glt[p]<INF && dgraph.link_info [p][6]!=0){
					vcratio=x[p]/dgraph.link_info [p][6];
					if(dgraph.link_info[p][5]!=0)
					{
						glt[p]=(dgraph.link_info[p][4]/dgraph.link_info[p][5])*(1+0.15*Math.pow( vcratio,4.0))+dgraph.link_info[p][8]/dgraph.vot;

						//if(vcratio<=1)bprtt[p]=(dgraph.link_info[p][4]/dgraph.link_info[p][5])*(1+0.15*Math.pow( vcratio,1.0));
						//else if(vcratio>1)bprtt[p]=(dgraph.link_info[p][4]/dgraph.link_info[p][5])*(1+0.15*Math.pow( vcratio,0.5));

					}
					else glt[p]=INF;
					
					if(glt[p]>dgraph.threshold_tt)glt[p]=dgraph.threshold_tt;
				}
			}		

			// 2)  perform a new stochastic network loading procedure based on updated link travel times.
			//find the new flow pattern
				//dal.dijkstrasalgo(dgraph,bprtt);
				x=DialsAlgo(dgraph,dal.d,glt);	

			// 3) move			
				for(int p=0; p<edges; p++) {
					double diff=x[p]-xp[p];
					startnode=(int)dgraph.link_info [p][1];
					endnode=(int)dgraph.link_info [p][2];
					//if(iternum<=3&&(startnode==190||endnode==190||startnode==204||endnode==204||startnode==3131||endnode==3131||startnode==3133||endnode==3133))System.out.print((p+1)+"\t"+startnode+"\t"+endnode+"\t"+x[p]+"\n");
					if(Math.abs(diff)>error[iternum]){error[iternum]=Math.abs( diff);linkno=p;}
					int k=iternum%100;
					if (k==0)k=100;
					x[p]=xp[p]+(diff/(double)(k));
					
				}
			//4) convergence criterion: if convergence is attained, stop; if not, set n=n+1 and go to step 1)
			System.out.print("	MSA Iteration "+iternum+": Error="+error[iternum]+"\t"+(linkno+1)+"\n\n");
			
		}while(error[iternum]>errorcrit && iternum<maxiternum);

		//replace the link_info array with the resulted flow pattern (x)
		
		for(int p=0; p<edges; p++) {
			dgraph.link_info [p][7]=(float)x[p];
			//System.out.print((p+1)+"\t"+dgraph.link_info [p][7]+"\n");
		}		

		//update the BPR travel time for each link
		dgraph.updateLinkInfo() ;
		
		
	}


	double[] DialsAlgo(DirectedGraph dgraph, double d[][],double bprtt[]){
		for(int p=0; p<edges; p++) {
			x_total[p]= 0;
		}
		System.out.print("	Dial's Algorithm running...0%");
		//Dial's algorithm
		for(int i=0;i<dgraph.Vertices() ;i++){
//			if((i+1)%48==0){
//				System.out.print(".");
//				if((i+1)%240==0){
//					System.out.print((i+1)/12+"%");
//				}	
//			}
			int origin=i+1;

			//calculate link likelihoods
			
			for(int p=0; p<edges; p++) {
				lk[p] =w[p]=x_org [p]=0;
				
				startnode=(int)dgraph.link_info [p][1];
				endnode=(int) dgraph.link_info [p][2];
				  if(d[origin-1][startnode-1]<d[origin-1][endnode-1] && d[origin-1][endnode-1]<INF)//dalgo.d[][] stores the O-D travel time cost
					  lk[p]=(float) Math.exp( theta*(d[origin-1][endnode-1]-d[origin-1][startnode-1]-bprtt[p]));				  
				  else lk[p]=0;
			}
			
			/*
			System.out.print("lk:\t");
				for(int p=0; p<edges; p++) {
					System.out.print(lk[p]+"\t");
				}			
				System.out.print("\n");
			*/
			
			//Forward pass
				//Sort vertices ascendingly according to their distances to the origin node (i.e.,dalgo.d[origin-1][nd-1]))
				
				for(int p=0;p<vertices-1;p++){
					for(int q=p+1;q<vertices;q++){
						if(d[origin-1][rank[p]-1]>d[origin-1][rank[q]-1]){
							int temp=rank[p];
							rank[p]=rank[q];
							rank[q]=temp;
						}
					}
				}
				
				//Calculate link weights 
				//( This is the most time-consuming part, maybe because it really calculates 8,000*20,000 times for TC network)
				for (int p=0;p<vertices;p++){
					sumW[p]=sumX[p]=0;
				}
				/*less efficient code
				sumW[origin-1]=1;					
				for(int p=1;p<vertices;p++){//rank[0] must be the origin node, and it doesn't need to be examined
					ed=rank[p];//node to be examined					
					for(int q=0; q<edges; q++) {
						startnode=(int)dgraph.link_info [q][1];
						endnode=(int) dgraph.link_info [q][2];
						if (endnode==ed){// if the examined link enters the examined node							
							w[q]=lk[q]*sumW[startnode-1];
							sumW[endnode-1]+=w[q];
						}	
					}
				}
				*/
				
				sumW[origin-1]=1;					
				for(int p=1;p<vertices;p++){//rank[0] must be the origin node, and it doesn't need to be examined
					ed=rank[p];//node to be examined

						for(int q=1;q<=dgraph.endnodeTolinks[ed-1][0];q++ ){
							linkid=dgraph.endnodeTolinks[ed-1][q];
							startnode=(int)dgraph.link_info[linkid-1][1]; 		
							w[linkid-1]=lk[linkid-1]*sumW[startnode-1];
							sumW[ed-1]+=w[linkid-1];							
						}

					
				}		
				/*		
			System.out.print("w:\t");
				for(int p=0; p<edges; p++) {
					System.out.print(w[p]+"\t");
				}			
				System.out.print("\n");
								
			//System.out.print("2\t");
				
			System.out.print( "\nsum of weights:\t");	
			for(int p=0; p<vertices; p++) {
				System.out.print(sumW[p]+"\t");
			}
			System.out.print( "\n");
			*/	
				
				
			//Backward pass								
				/*	
				for(int p=0; p<edges; p++) {
					x_org [p]=0;
				}			
				*/
				for(int p=vertices-1;p>0;p--){
					ed=rank[p];
					
						if(sumW[ed-1]!=0){
							double temp=dgraph.ODMatrix(origin,ed)+sumX[ed-1];
							if(temp!=0){
								for(int q=1;q<=dgraph.endnodeTolinks[ed-1][0];q++ ){
									linkid=dgraph.endnodeTolinks[ed-1][q];
									if(w[linkid-1]!=0){
										startnode=(int)dgraph.link_info[linkid-1][1]; 	
								
										x_org[linkid-1]=temp*w[linkid-1]/sumW[ed-1];
										sumX[startnode-1]+=x_org[linkid-1];
									}
								}

								/*//less efficient code
								for(int q=0; q<edges; q++) {
									startnode=(int)dgraph.link_info [q][1];
									endnode=(int) dgraph.link_info [q][2];
									if (endnode==ed && w[q]!=0){// if the examined link enters the examined node
										x_org[q]=temp*w[q]/sumW[ed-1];
										sumX[startnode-1]+=x_org[q];
									}	
								}							
								*/	
							}
						}

						
				}
			/*	
			System.out.print( "\nsum of flows:\t");	
			for(int p=0; p<vertices; p++) {
				System.out.print(sumX[p]+"\t");
			}
			System.out.print( "\n");	
			*/
			//System.out.print("x_org:\n");		
			
			for(int p=0; p<edges; p++) {
				x_total[p]+=x_org[p];
				//System.out.print(x_org[p]+"\t");
			}	
		
		}
		System.out.print("\n");
		return x_total;
	}
	

}