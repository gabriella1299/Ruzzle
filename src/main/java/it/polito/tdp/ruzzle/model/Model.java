package it.polito.tdp.ruzzle.model;

import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.ruzzle.db.DizionarioDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Model {
	private final int SIZE = 4;
	private Board board ;
	private List<String> dizionario ;
	private StringProperty statusText ;

	public Model() {
		this.statusText = new SimpleStringProperty() ;
		
		this.board = new Board(SIZE);
		DizionarioDAO dao = new DizionarioDAO() ;
		this.dizionario = dao.listParola() ;
		statusText.set(String.format("%d parole lette", this.dizionario.size())) ;
	
	}
	
	public void reset() {
		this.board.reset() ;
		this.statusText.set("Board Reset");
	}

	public Board getBoard() {
		return this.board;
	}

	public final StringProperty statusTextProperty() {
		return this.statusText;
	}
	

	public final String getStatusText() {
		return this.statusTextProperty().get();
	}
	

	public final void setStatusText(final String statusText) {
		this.statusTextProperty().set(statusText);
	}
	
	//Data una parola e' presente nella matrice?
	public List<Pos> trovaParola(String parola) {
		
		for(Pos p:board.getPositions()){//ritorna tutte le posizioni modellate dalla board
			//usiamo == e non equals perche abbiamo confronti tra char, dato primitivo
			if(board.getCellValueProperty(p).get().charAt(0) == parola.charAt(0)) { //parola.charAt(0) ritorna un char, 1^lettera della parola
				List<Pos> percorso=new ArrayList<Pos>();
				percorso.add(p);
				if(cerca(parola,1,percorso))
					return percorso;
			}
		}
		return null;//non ho trovato la parola sulla griglia
	
	}

	private boolean cerca(String parola, int livello, List<Pos> percorso) {
		
		//CASO TERMINALE
		if(livello==parola.length()) {
			return true;
		}
		
		Pos ultima=percorso.get(percorso.size()-1); //parto dall'ultima lettera riconosciuta ad ogni passo e guardo le lettere adiacenti
		List<Pos> adiacenti=board.getAdjacencies(ultima);
		for(Pos p:adiacenti) {
			   //non deve gia' essere stata usata la lettera--> guardo se e' contenuta in percorso
			if(!percorso.contains(p) && parola.charAt(livello)==board.getCellValueProperty(p).get().charAt(0)) {
				
				percorso.add(p);
				
				//USCITA RAPIDA
				if(cerca(parola,livello+1,percorso))
					return true;//inutile fare back. perche' parola ritrovata
				
				percorso.remove(percorso.size()-1);//BACKTRACKING
			}
		}
		
		
		return false;
	}

	public List<String> trovaTutte() { //richiama trovaParola per tutte le parole del dizionario
		List<String> tutte=new ArrayList<String>();
		for(String parola:this.dizionario) {
			parola=parola.toUpperCase();//nella board sono tutte maiuscole
			if(parola.length()>1) {//regola di ruzzle, la parola deve avere piu di una lettera
				if(this.trovaParola(parola)!=null) {
					tutte.add(parola);
				}
			}
		}
		return tutte;
	}
	
	public boolean parolaValida(String parola) {
		if(parola.length() > 1 && this.dizionario.contains(parola.toLowerCase()))
			return true;
		return false;
	}
	

}
