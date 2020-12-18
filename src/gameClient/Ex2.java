package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;


/**
 * This class is the main class where all the actions and decisions of the objects in the game choose their next move.
 * As well as initializing the graph and all the corresponding objects, the actions of the agents to choose their
 * next target pokemon and deciding which path to take is implemented here.
 */
public class Ex2 implements Runnable {
	private MyFrame window;
	private Arena arena;
	public static game_service game;
	private static directed_weighted_graph graph;
	private static dw_graph_algorithms graph_algo = new DWGraph_Algo();
	private List<Agent> AgentsInGame = new ArrayList<>();
	private HashMap<Integer, edge_data> EdgeSrcList = new HashMap<>();
	private HashMap<Integer, HashMap<Integer, List<node_data>>> EveryPathList = new HashMap<>();
	private HashMap<Integer, HashMap<Integer, Double>> EveryPathDistList = new HashMap<>();
	private ArrayList<Integer> loopCheck = new ArrayList<>();
	private static long id;
	private static int level_number;
	private static boolean argsFlag;


	public static void main(String[] args) {

		argsFlag = false;
		if (args.length != 0) {
			id = Long.parseLong(args[0]);
			if (Integer.parseInt(args[1]) < 0 || Integer.parseInt(args[1]) > 23)
				throw new IllegalArgumentException("Levels are only in the range of [0,23], please try again.");
			else level_number = Integer.parseInt(args[1]);
			argsFlag = true;
		}
		Thread client = new Thread(new Ex2());
		client.start();
	}

	@Override
	public void run() {

		int level_number = 0;
		if (!argsFlag) {
			ImageIcon EntryIcon = new ImageIcon(new ImageIcon("images/pokeball.png").getImage().getScaledInstance(120, 120, Image.SCALE_DEFAULT));
			String LogInMessage = "Welcome To The Pokemon Challenge!\n\nTo Save Your Progress Please Enter Your ID Number:\nOtherwise Enter 0.";
			String LevelMessage = "Which Level Would You Like To Simulate?";
			int id = Integer.parseInt((String) JOptionPane.showInputDialog(null, LogInMessage, "ID", JOptionPane.QUESTION_MESSAGE, EntryIcon, null, 0));
			int[] range = IntStream.iterate(0, n -> n <= 23, n -> n + 1).toArray();
			String[] Levels = Arrays.toString(range).split("[\\[\\]]")[1].split(", ");
			level_number = Integer.parseInt((String) JOptionPane.showInputDialog(null, LevelMessage, "Level", JOptionPane.INFORMATION_MESSAGE, EntryIcon, Levels, 0));
			game = Game_Server_Ex2.getServer(level_number); // you have [0,23] games
			game.login(id);
		}
		else {
			game = Game_Server_Ex2.getServer(level_number); // you have [0,23] games
			game.login(id);
		}
		graph = LoadJSONgraph(game.getGraph());
		init(game);
		game.startGame();
		window.setTitle("Ex2: Pokemon Challenge");
		int ind = 0;
		long dt = this.dtTime(level_number);
		double time = game.timeToEnd()/1000;
		while (game.isRunning()) {
			moveAgents(game, graph);

			try {
				if (ind % 1 == 0) { window.repaint(); }
				if (dtTime(level_number)==80 && time<25) dt = 100;
				Thread.sleep(dt);
				ind++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String res = game.toString();
		String[] ResultsArray = res.split(",");
		System.out.println(res + "\n");
		String score = "", moves = "", level = "", moves_num = "", length = "Game Length: ";
		for (String result : ResultsArray) {
			if (result.contains("grade")) score = "Your Final Score is: " + result.substring(8)+"\n";
			if (result.contains("moves")) {
				moves = "The total number of moves is: " + result.substring(8)+"\n";
				moves_num = result.substring(8);
			}
			if (result.contains("game_level")) level = "On Level: " + result.substring(13)+"\n";
		}
		double avg_moves = Integer.parseInt(moves_num)/time;
		boolean efficient_move_number = avg_moves<10;
		ImageIcon icon = new ImageIcon(new ImageIcon("images/charmander.gif").getImage().getScaledInstance(150, 120, Image.SCALE_DEFAULT));
		length += time + " seconds\n";
		String infoMessage = score+moves+level+length;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(5);
		System.out.println(infoMessage +"Average Moves Per Second: "+df.format(avg_moves)+"\nEfficient number of moves per second?: "+efficient_move_number+"\nGood Job!");
		String exitMessage = "Gotta catch 'em all!™\n";
		JOptionPane.showMessageDialog(null, exitMessage + infoMessage, "Exit", JOptionPane.PLAIN_MESSAGE, icon);
		System.exit(0);
	}


	private void init(game_service game) {

		graph = LoadJSONgraph(game.getGraph());
		graph_algo.init(graph);
		String pokemons = game.getPokemons();
		CollectPaths(graph);
		arena = new Arena();
		arena.setGraph(graph);
		arena.setPokemons(Arena.json2Pokemons(pokemons));
		window = new MyFrame("Ex2 - Pokemon Challenge");
		window.setSize(1000, 700);
		window.update(arena);
		//window.show();
		window.setVisible(true);
		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject server = line.getJSONObject("GameServer");
			int num_of_agents = server.getInt("agents");
			System.out.println(info);
			System.out.println(game.getPokemons());
			ArrayList<Pokemon> pokemonsList = Arena.json2Pokemons(game.getPokemons());
			int i = 0;
			for (Pokemon pokemon : pokemonsList) {
				Arena.updateEdge(pokemon, graph);
				if (i < num_of_agents) {
					game.addAgent(pokemon.get_edge().getSrc());
					EdgeSrcList.put(i, pokemon.get_edge());
					i++;
				}
			}
			for (node_data node : graph_algo.getGraph().getV()) {
				if (i < num_of_agents) {
					Agent agent = new Agent(graph_algo.getGraph(), node.getKey());
					AgentsInGame.add(agent);
					game.addAgent(node.getKey());
					i++;
				}
				break;
			}
			AgentsInGame = Arena.getAgents(game.getAgents(), graph_algo.getGraph());
			arena.setAgents(AgentsInGame);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

	}


	public void moveAgents(game_service game, directed_weighted_graph graph) {

		String move = game.move();
		List<Agent> AgentsList = Arena.getAgents(move, graph);
		arena.setAgents(AgentsList);
		String pokemonAsString = game.getPokemons();
		List<Pokemon> pokemonsList = Arena.json2Pokemons(pokemonAsString);
		arena.setPokemons(pokemonsList);
		for (Pokemon p : pokemonsList) {
			arena.updateEdge(p, graph);
		}
		for (Agent agent : AgentsList) {
			int dest;
			if (agent.get_curr_edge() == null) {
				if (EdgeSrcList.get(agent.getID()).getSrc() == agent.getSrcNode()) {
					dest = EdgeSrcList.get(agent.getID()).getDest();
				} else {
					dest = nextNode(agent);
				}
				loopCheck.add(dest);
				int lastDest = loopCheck.size() - 1;
				if (loopCheck.size() > 2 && (loopCheck.get(lastDest) == loopCheck.get(lastDest - 2)))
					dest = agent.getSrcNode();
				game.chooseNextEdge(agent.getID(), dest);
				int id = agent.getID();
				double val = agent.getValue();
				System.out.println("Agent: " + id + ", val: " + val + "   turned to node: " + dest);
			}
		}
	}


	private int nextNode(Agent agent) {

		double distance = Double.MAX_VALUE;
		for (Pokemon pokemon : arena.getPokemons()) {
			edge_data pokeEdge = pokemon.get_edge();
			double TempDistance = EveryPathDistList.get(agent.getSrcNode()).get(pokemon.get_edge().getSrc());
			if (TempDistance < distance && TempDistance != -1) {
				distance = TempDistance;
				if ((EdgeSrcList.containsValue(pokeEdge)) && (EdgeSrcList.get(agent.getID()) != pokeEdge)) {
					continue;
				}
				if (distance == 0) {
					return pokemon.get_edge().getDest();
				}
				EdgeSrcList.put(agent.getID(), pokemon.get_edge());
			}
		}
		List<node_data> NextNodeList = EveryPathList.get(agent.getSrcNode()).get(EdgeSrcList.get(agent.getID()).getSrc());
		if (NextNodeList.size() > 2) {
			List<node_data> TempNodesList =
					EveryPathList.get(NextNodeList.get(1).getKey()).get(EdgeSrcList.get(agent.getID()).getSrc());
			if (NextNodeList.get(0).getKey() == TempNodesList.get(1).getKey()) {
				return TempNodesList.get(2).getKey();
			}
		}
		return EveryPathList.get(agent.getSrcNode()).get(EdgeSrcList.get(agent.getID()).getSrc()).get(1).getKey();
	}


	public void CollectPaths(directed_weighted_graph graph) {

		graph_algo.init(graph);
		for (node_data node : graph.getV()) {
			EveryPathList.put(node.getKey(), new HashMap<>());
			EveryPathDistList.put(node.getKey(), new HashMap<>());

			for (node_data dest_node : graph.getV()) {
				List<node_data> path = graph_algo.shortestPath(node.getKey(), dest_node.getKey());
				double path_distance = graph_algo.shortestPathDist(node.getKey(), dest_node.getKey());
				EveryPathList.get(node.getKey()).put(dest_node.getKey(), path);
				EveryPathDistList.get(node.getKey()).put(dest_node.getKey(), path_distance);
			}
		}
	}


	private static directed_weighted_graph LoadJSONgraph(String GraphAsString) {

		graph = new DWGraph_DS();
		JsonParser parser = new JsonParser();
		JsonObject JSONgraph = parser.parse(GraphAsString).getAsJsonObject();
		JsonArray NodesInGraph = JSONgraph.get("Nodes").getAsJsonArray();
		JsonArray EdgesInGraph = JSONgraph.get("Edges").getAsJsonArray();

		for (JsonElement node : NodesInGraph) {
			String GraphLoc = node.getAsJsonObject().get("pos").getAsString();
			String[] GraphLocArray = GraphLoc.split(",");
			int key = node.getAsJsonObject().get("id").getAsInt();
			node_data NewNode = new NodeData(key);
			geo_location GeoLoc = new GeoLocation(Double.parseDouble(GraphLocArray[0]),
					Double.parseDouble(GraphLocArray[1]),
					Double.parseDouble(GraphLocArray[2]));
			NewNode.setLocation(GeoLoc);
			graph.addNode(NewNode);
		}
		for (JsonElement edge : EdgesInGraph) {
			int src = edge.getAsJsonObject().get("src").getAsInt();
			int dest = edge.getAsJsonObject().get("dest").getAsInt();
			double weight = edge.getAsJsonObject().get("w").getAsDouble();
			graph.connect(src, dest, weight);
		}
		return graph;
	}

	public int dtTime(int level_number) {
		int[] levels = {0,6,8,10,13,16,17,18,19};
		for (var level : levels) {
			if (level == level_number)  {
				return 120;
			}
		}
		if (level_number == 21) return 85;
		return 100;
	}


}

