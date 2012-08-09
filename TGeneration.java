//import java.text.*;

public class TGeneration {
	double TrafficProducedataNode[];   //// from a centroid
	double TrafficAttractedtoaNode[];    //// to a cnetroid
	int vertices;
	public double trip_gen_mutiplier=1.0;
	public float totaltrips=0;
	
	
	public TGeneration( DirectedGraph dgraph) {
		vertices=dgraph.Vertices() ;
		TrafficProducedataNode=new double [vertices];
		TrafficAttractedtoaNode=new double [vertices];
		for(int i=0;i<vertices;i++){
			TrafficProducedataNode[i]=TrafficAttractedtoaNode[i]=0;
		}
	}
	
	public void tripGeneration(DirectedGraph dgraph) {
		float total_production=0,total_attraction=0;	   

		//We're calculating AM peak trips generated and attracted by each place
		for(int i=0;i<vertices;i++){
			
			TrafficProducedataNode[i]=dgraph.triprate*dgraph.node_info [i][3];
			total_production+=TrafficProducedataNode[i];	
				
			TrafficAttractedtoaNode[i]=dgraph.triprate*dgraph.node_info [i][4];
			total_attraction+=TrafficAttractedtoaNode[i];		
			
		}
		
		if(total_production!=total_attraction){
			for(int i=0;i<vertices;i++){
				TrafficAttractedtoaNode[i]=TrafficAttractedtoaNode[i]*(total_production/total_attraction);
			}
		}
		totaltrips=total_production;
		//DecimalFormat myFormatter = new DecimalFormat("#######.00");			
		//System.out.print("\tTotal "+ myFormatter.format( (float)total_production/1000)+" thousand trips are produced by "+myFormatter.format(dgraph.juris_info[0][0]/1000) +" thousand households in the seven-county region in the morning peak hour.\n\n");
		//System.out.print(TrafficProducedataNode[1]+"!!\n");
	}
	
	
	
}