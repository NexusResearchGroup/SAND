
import java.io.*;

public class TDistribution {
	double TrafficProducedataNode[];   //// at a network node
	double TrafficAttractedtoaNode[];    //// at a network node
	double Attraction[],oldAttraction[],newAttraction[];   ////// the resulting attraction function for each node
	double denom[];    //// its the resulting denominator in the gravity model.......denom[i] = sum(j) (Attraction[j] * f(i, j))
	
	public TDistribution( DirectedGraph dgraph) {
	}
	
	public void tripDistribution(DirectedGraph dgraph, TGeneration tgen, DijkstrasAlgo dalgo) {
		
		int vertices = dgraph.Vertices();
		TrafficProducedataNode=new double [vertices];   //// at a network centroid
		TrafficAttractedtoaNode=new double [vertices];    //// at a network centroid
		TrafficProducedataNode=tgen.TrafficProducedataNode;
		TrafficAttractedtoaNode=tgen.TrafficAttractedtoaNode;
		Attraction=new double[vertices];
		oldAttraction=new double[vertices];
		newAttraction=new double[vertices];
		denom=new double[vertices];

			
		///////   Trip_Distribution
			float error = 2;
			
			//System.out.println("Starting Trip Distribution");
						
			for(int i=0; i<vertices; i++)
				Attraction[i] = newAttraction[i] = TrafficAttractedtoaNode[i];
			int iterationCounter = 0;
			
			while(error> 0.1) {
				
				iterationCounter++;
				error = 0;		
				
				//// step 1:   set denom =0; and oldattraction = newattraction
				for(int i=0; i<vertices; i++) {
					denom[i] = 0;
					oldAttraction[i] = newAttraction[i];
					newAttraction[i] = 0;
					//System.out.print(oldAttraction[i] + "  ");
				}
				//System.out.println();

				//// step 2:  calculate the new Attractions using the previous oldAttraction
				for(int i=0; i<vertices; i++)
					{
						if(oldAttraction[i]>0)
						Attraction[i] = TrafficAttractedtoaNode[i]*Attraction[i]/oldAttraction[i];
			
					} 
				
				//// step 3:  calculate denom with the new Attractions
				for(int i=0; i<vertices; i++)
					for(int j=0; j<vertices; j++)
						if(dalgo.pLabel(i+1, j+1,dgraph)>0 )
						denom[i] +=Attraction[j]*Math.exp( -dgraph.coeff*dalgo.pLabel(i+1, j+1,dgraph) ); 
					
				//// step 4:  calculate newAttraction using denom and Attraction
				for(int j=0; j<vertices; j++) {
					for(int i=0; i<vertices; i++)
						if( denom[i]>0 && dalgo.pLabel(i+1, j+1,dgraph)>0 )
							newAttraction[j] += TrafficProducedataNode[i]*Math.exp( -dgraph.coeff*dalgo.pLabel(i+1, j+1,dgraph) )/denom[i];
					newAttraction[j] *= Attraction[j];
				}
				
				//// step 5:  calculate error. error = square root of sum of squares of deviation rom previous results
				for(int i=0; i<vertices; i++)
					error += Math.pow( (oldAttraction[i] - newAttraction[i] ),  2);					
				error = (float)Math.sqrt( error);
				System.out.print("\tIteration "+iterationCounter+": Error= "+error+"\n");
		}
		
		for(int i=0;i<vertices;i++){
			for (int j=0;j<vertices;j++){
				if(denom[i]>0 && dalgo.pLabel(i+1, j+1,dgraph)>0 )
					{
						dgraph.ODMatrix[i][j]= (float) (TrafficProducedataNode[i] * Attraction[j]*Math.exp(-dgraph.coeff*dalgo.pLabel(i+1, j+1,dgraph))/denom[i] );

					}
				else
					dgraph.ODMatrix[i][j]=0;
				
			}
		}

	}

	public void tripDistribution(DirectedGraph dgraph, TGeneration tgen, double estimatedPLabel[][]) {
		
		int vertices = dgraph.Vertices();
		TrafficProducedataNode=new double [vertices];   //// at a network centroid
		TrafficAttractedtoaNode=new double [vertices];    //// at a network centroid
		TrafficProducedataNode=tgen.TrafficProducedataNode;
		TrafficAttractedtoaNode=tgen.TrafficAttractedtoaNode;
		Attraction=new double[vertices];
		oldAttraction=new double[vertices];
		newAttraction=new double[vertices];
		denom=new double[vertices];
		
		///////   Trip_Distribution
			float error = 2;
			
			//System.out.println("Starting Trip Distribution");
						
			for(int i=0; i<vertices; i++)
				Attraction[i] = newAttraction[i] = TrafficAttractedtoaNode[i];
			int iterationCounter = 0;
			
			while(error> 0.1) {
				
				iterationCounter++;
				error = 0;		
				
				//// step 1:   set denom =0; and oldattraction = newattraction
				for(int i=0; i<vertices; i++) {
					denom[i] = 0;
					oldAttraction[i] = newAttraction[i];
					newAttraction[i] = 0;
					//System.out.print(oldAttraction[i] + "  ");
				}
				//System.out.println();

				//// step 2:  calculate the new Attractions using the previous oldAttraction
				for(int i=0; i<vertices; i++)
					{
						if(oldAttraction[i]>0)
						Attraction[i] = TrafficAttractedtoaNode[i]*Attraction[i]/oldAttraction[i];
			
					} 
				
				//// step 3:  calculate denom with the new Attractions
				for(int i=0; i<vertices; i++)
					for(int j=0; j<vertices; j++)
						if(estimatedPLabel[i][j]>0 && Attraction[j]>0)
						denom[i] +=Attraction[j]*Math.exp( -dgraph.coeff*estimatedPLabel[i][j]); 
					
				//// step 4:  calculate newAttraction using denom and Attraction
				for(int j=0; j<vertices; j++) {
					for(int i=0; i<vertices; i++)
						if( denom[i]>0 && estimatedPLabel[i][j]>0 )
							newAttraction[j] += TrafficProducedataNode[i]*Math.exp( -dgraph.coeff*estimatedPLabel[i][j] )/denom[i];
					newAttraction[j] *= Attraction[j];
				}
				
				//// step 5:  calculate error. error = square root of sum of squares of deviation rom previous results
				for(int i=0; i<vertices; i++)
					error += Math.pow( (oldAttraction[i] - newAttraction[i] ),  2);					
				error = (float)Math.sqrt( error);
		}
		

	}

	public void printODMatrix(DirectedGraph dgraph){
		
		PrintWriter flowoutput=null;
		int vertices=dgraph.Vertices() ;	
		try
			{
				flowoutput=new PrintWriter(new FileOutputStream("odmatrix.txt"));
			}				  		
		catch(IOException e)
			{
				System.out.print("Error opening the files!");
				System.exit(0);
			
			}
		flowoutput.print(vertices*vertices+"\n");
		for(int i=0;i<vertices;i++){
			
			for(int j=0;j<vertices;j++){
				if(dgraph.ODMatrix[i][j]>0){
					flowoutput.print((i+1)+"\t");
					flowoutput.print((j+1)+"\t");
					flowoutput.print(dgraph.ODMatrix[i][j]+"\n");

				}
			}
			//flowoutput.print("\n");
		}
		flowoutput.print("\n");

		flowoutput.close();
		System.out.print("ODMatrix created.\n");

	}	
	
	public void printODCost(DirectedGraph dgraph,DijkstrasAlgo dalgo, Automata ca){
		
		
		//for (int i=0;i<dgraph.Vertices() ;i++){
		//	System.out.print(ca.AverAccessDistance(i)+"\t");
		//}
		//System.out.print("AverDistance\n");
		
		PrintWriter flowoutput=null;
		int vertices=dgraph. Vertices() ;	
		try
			{
				flowoutput=new PrintWriter(new FileOutputStream("odcost.txt"));
			}				  		
		catch(IOException e)
			{
				System.out.print("Error opening the files!");
				System.exit(0);
			
			}
		for(int i=0;i<vertices;i++){
			
			for(int j=0;j<dgraph.Vertices() ;j++){
				
				flowoutput.print((i+1)+"\t");
				flowoutput.print((j+1)+"\t");
				flowoutput.print(dalgo.pLabel( i+1,j+1,dgraph)+"\n");
			}
			//flowoutput.print("\n");
		}
		flowoutput.print("\n");

		flowoutput.close();
		System.out.print("ODCost created.\n");

	}	
	
}