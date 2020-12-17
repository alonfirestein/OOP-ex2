package gameClient;

import api.*;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * This class was created in order to create the GUI frame of the game, therefore visually creating the
 * graph (drawing the nodes and connecting the edges), as well as adding the agents and pokemon to the graph.
 */
public class MyFrame extends JFrame {
	private int _ind;
	private Arena arena;
	private gameClient.util.Range2Range _w2f;
	int time;
	double score;
	int moves;
	int level;


	MyFrame(String a) {
		super(a);
		int _ind = 0;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public void update(Arena ar) {
		this.arena = ar;
		updateFrame();
	}

	private void updateFrame() {
		Range rx = new Range(20,this.getWidth()-20);
		Range ry = new Range(this.getHeight()-10,150);
		Range2D frame = new Range2D(rx,ry);
		directed_weighted_graph g = arena.getGraph();
		_w2f = Arena.w2f(g,frame);
	}


	public void paint(Graphics g) {
		int w = this.getWidth();
		int h = this.getHeight();
		g.clearRect(0, 0, w, h);
		//updateFrame();
		Image image = new ImageIcon("images/arena.png").getImage();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		drawPokemons(g);
		drawGraph(g);
		drawAgents(g);
		drawInfo(g);
		drawGameInfo(g);

	}

	private void drawInfo(Graphics g) {
		List<String> str = arena.get_info();
		String dt = "none";
		for(int i=0;i<str.size();i++) {
			g.drawString(str.get(i)+" dt: "+dt,100,60+i*20);
		}
	}

	private void drawGraph(Graphics g) {
		directed_weighted_graph graph = arena.getGraph();
		for (node_data n : graph.getV()) {
			g.setColor(Color.red);
			drawNode(n, 5, g);
			for (edge_data e : graph.getE(n.getKey())) {
				g.setColor(Color.white);
				drawEdge(e, g);
			}
		}
	}

	private void drawPokemons(Graphics g) {
		List<CL_Pokemon> pokemonList = arena.getPokemons();
		Image pokemon1 = new ImageIcon("images/pikachu2.png").getImage();
		Image pokemon2 = new ImageIcon("images/pokemon1.png").getImage();
		Image pokemon3 = new ImageIcon("images/pokemon2.png").getImage();
		Image pokemon4 = new ImageIcon("images/pokemon3.png").getImage();
		Image pokemon5 = new ImageIcon("images/pokemon4.png").getImage();
		Image pokemon6 = new ImageIcon("images/pokemon7.png").getImage();

		if(pokemonList != null) {
			for (CL_Pokemon pokemon : pokemonList) {
				Point3D location = pokemon.getLocation();
				int r = 10;
				if (location != null) {
					geo_location fp = UpdateGraphInfoAfterResize(_w2f.world2frame(location),g);
					if (pokemon.getType() > 0 && (pokemon.getValue()<=10)) g.drawImage(pokemon3,(int)fp.x()-r, (int)fp.y()-r, 4*r, 4*r, null);
					if (pokemon.getType() > 0 && (pokemon.getValue()>10)) g.drawImage(pokemon5,(int)fp.x()-r, (int)fp.y()-r, 4*r, 4*r, null);
					//if (pokemon.getType() > 0 && (pokemon.getValue()>10)) g.drawImage(pokemon4,(int)fp.x()-r, (int)fp.y()-r, 4*r, 4*r, null);
					if (pokemon.getType() < 0 && (pokemon.getValue()<=8)) g.drawImage(pokemon6,(int)fp.x()-r, (int)fp.y()-r, 4*r, 4*r, null);
					if (pokemon.getType() < 0 && (pokemon.getValue()>8 && pokemon.getValue() <= 12)) g.drawImage(pokemon4,(int)fp.x()-r, (int)fp.y()-r, 4*r, 4*r, null);
					if (pokemon.getType() < 0 && (pokemon.getValue()>12 && pokemon.getValue() <= 14)) g.drawImage(pokemon1,(int)fp.x()-r, (int)fp.y()-r, 4*r, 4*r, null);
					else g.drawImage(pokemon2,(int)fp.x()-r, (int)fp.y()-r, 4*r, 4*r, null);
					//g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);
				}
			}
		}
	}

	private void drawAgents(Graphics g) {
		int r = 8;
		Image image = new ImageIcon("images/ash.png").getImage();
		for (CL_Agent agent : arena.getAgents()) {
                geo_location location = agent.getLocation();
                geo_location fp = UpdateGraphInfoAfterResize(_w2f.world2frame(location),g);
				g.drawImage(image,(int)fp.x()-r, (int)fp.y()-r, 3*r, 5*r, null);
                g.setColor(Color.black);
                g.drawString("" + agent.getID(), (int)fp.x()-r + r, (int)fp.y()-r - 10);
                //g.drawString("" + agent.getNextNode(), (int)fp.x()-r + 5, (int)fp.y()-r + 13);
            }
	}


	private void drawNode(node_data n, int r, Graphics g) {
		geo_location pos = n.getLocation();
		geo_location fp = UpdateGraphInfoAfterResize(_w2f.world2frame(pos),g);
		g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
		g.drawString(""+n.getKey(), (int)fp.x(), (int)fp.y()-4*r);

	}

	private void drawEdge(edge_data e, Graphics g) {
		directed_weighted_graph gg = arena.getGraph();
		geo_location s = gg.getNode(e.getSrc()).getLocation();
		geo_location d = gg.getNode(e.getDest()).getLocation();
		geo_location s0 = UpdateGraphInfoAfterResize(_w2f.world2frame(s),g);
		geo_location d0 = UpdateGraphInfoAfterResize(_w2f.world2frame(d),g);
		g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
		//	g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);

	}

	private geo_location UpdateGraphInfoAfterResize(geo_location location, Graphics g){
		double bounds_for_x = (g.getClipBounds().getWidth()/1000.0);
		double bounds_for_y = (g.getClipBounds().getHeight()/700.0);
		int new_x = (int)(location.x() * bounds_for_x);
		int new_y = (int)(location.y() * bounds_for_y);
		return new Point3D(new_x, new_y);
	}

	private void getServerData() {
		try {
			JSONObject line = new JSONObject(Ex2.game.toString());
			JSONObject server = line.getJSONObject("GameServer");
			level = server.getInt("game_level");
			score = server.getDouble("grade");
			moves = (int) server.getDouble("moves");
			time = (int) (Ex2.game.timeToEnd() / 1000);

		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void drawGameInfo(Graphics g) {
		getServerData();
		Font font = new Font("Helvetica", Font.BOLD, 15);
		g.setFont(font);
		g.setColor(Color.WHITE);
		String gameInfo = "Level: "+ level +"     Time Remaining: "+ time+"     Score: "+score+"     Moves: "+moves;
		g.drawString(gameInfo, (getWidth()/2)-190,getHeight()/20);
		int i = 0;
		for (CL_Agent agent : arena.getAgents()) {
			g.drawString("Agent: " +agent.getID()+"   Score: "+agent.getValue(), (getWidth()/20)-50,(getHeight()/20)+i);
			i+=20;
		}
		if (time == 3) {
			Font font2 = new Font("Helvetica", Font.BOLD, 80);
			g.setFont(font2);
			g.setColor(Color.WHITE);
			g.drawString("3", (getWidth()/2)-30, getHeight()/2);
		}
		if (time == 2) {
			Font font2 = new Font("Helvetica", Font.BOLD, 80);
			g.setFont(font2);
			g.setColor(Color.WHITE);
			g.drawString("2", (getWidth()/2)-30, getHeight()/2);
		}
		if (time == 1) {
			Font font2 = new Font("Helvetica", Font.BOLD, 80);
			g.setFont(font2);
			g.setColor(Color.WHITE);
			g.drawString("1", (getWidth()/2)-30, getHeight()/2);
		}
		if (time == 0) {
			Font font2 = new Font("Helvetica", Font.BOLD, 80);
			g.setFont(font2);
			g.setColor(Color.WHITE);
			g.drawString("Game Over", (getWidth()/3), getHeight()/2);
		}
	}



}