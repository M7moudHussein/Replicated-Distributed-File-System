package master_server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MasterServerClient implements MasterServerClientInterface{

	public static final String REPLICA_SERVERS_DATA_FILE = "repServers.txt";
	public static final int NUMBER_OF_FILE_REPLICAS = 3;
	private List<ReplicaLoc> replicas;
	private Map<String, FileDistribution> file_distributations_map;
	Thread heartbeat_thread;
	private boolean isTerminated = false;

	
	private Registry registry;
	
	
	long time_stamp = 0;
	long transaction_id = 0;
	
	
	public void startReplicaServers() throws IOException {
		replicas = new ArrayList<ReplicaLoc>();
		FileReader fr = new FileReader(new File(REPLICA_SERVERS_DATA_FILE));
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(fr);
		
		String line = br.readLine();
		for(; line != null ; line = br.readLine()) {
			line = line.trim();
			if(line.isEmpty()) 
				continue;
			
			String []data = line.split(",");
			
			if(data.length > 3) 
				throw new RuntimeException("There is line has more than one comma");
			
			String ip = data[0];
			String dir = data.length == 2 ? data[1]: "~";
			int port = data.length == 3 ? Integer.valueOf(data[1]): 8080;
			
			// starting each replica (call start in replica interface or using command)
			replicas.add(new ReplicaLoc(ip, dir, port));
		}
		br.close();
		fr.close();
	}
	
	public void heartBeatChecking() {
		while(!isTerminated) {
//			for(ReplicaLoc rep: replicas) {
//				// ssh or rmi 
//			}
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public MasterServerClient(int port, String lookup) throws IOException, 
						AlreadyBoundException, InterruptedException {
		file_distributations_map = new HashMap<String, FileDistribution>();
		startReplicaServers();
		if(replicas.size() < NUMBER_OF_FILE_REPLICAS)
			throw new RuntimeException("Not Sufficient number of replicas");
		
		RemoteServer remoteServer = (RemoteServer) UnicastRemoteObject.exportObject(this, port);
		registry = LocateRegistry.createRegistry(port);
		registry.bind(lookup, remoteServer);
		
		heartbeat_thread = new Thread(() -> {
			heartBeatChecking();
		});
		
		
		heartbeat_thread.join();
	}
	
	
	@Override
	public ReplicaLoc[] read(String file_name) throws FileNotFoundException, IOException, RemoteException {
		if(!file_distributations_map.containsKey(file_name))
			throw new FileNotFoundException();
		
		++time_stamp;
		return file_distributations_map.get(file_name).getReplicas();
	}

	private void createFile(String file_name) {
		Random random = new Random();
		ReplicaLoc []file_replicas = new ReplicaLoc[NUMBER_OF_FILE_REPLICAS];
		Set<Integer> taken_replica = new HashSet<Integer>();
		for(int i = 0; i < NUMBER_OF_FILE_REPLICAS ; ++i) {
			int cur_idx = random.nextInt(replicas.size());
			while(taken_replica.contains(cur_idx))
				cur_idx = random.nextInt(replicas.size());
			
			file_replicas[i] = this.replicas.get(cur_idx);
			taken_replica.add(cur_idx);
		}
		
		file_distributations_map.put(file_name, new FileDistribution(file_replicas[0], file_replicas));
	}
	
	@Override
	public WriteMsg write(String file_name, FileContent data) throws RemoteException, IOException {
		
		++time_stamp;
		
		if(!file_distributations_map.containsKey(file_name)) {
			createFile(file_name);
		}
		
		
		return new WriteMsg(++transaction_id, time_stamp, file_distributations_map.get(file_name).getPrimaryRep());
	}
	
	
	public void exit() {
		isTerminated = true;
	}

}
