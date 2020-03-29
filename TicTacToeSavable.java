import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Ellipse;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Author: Zack Sprague
 * Date: 3-26-18
 * Description: Simple GUI for playing Tic-Tac-Toe
 * */

public class TicTacToeSavable extends Application {
  // Indicate which player has a turn, initially it is the X player
  private char whoseTurn = 'X';

  // Create and initialize cell
  private Cell[][] cell =  new Cell[3][3];

  // Create and initialize a status label
  private Label lblStatus = new Label("X's turn to play");

  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
	// Create menu and menu items
	MenuBar menuBar = new MenuBar();    
	Menu menuFile = new Menu("File");
	menuBar.getMenus().addAll(menuFile);
	MenuItem menuItemNewGame = new MenuItem("New Game");
	MenuItem menuItemSaveAs = new MenuItem("Save As...");
	MenuItem menuItemLoadGame = new MenuItem("Load Game");
	MenuItem menuItemExit = new MenuItem("Exit");
	menuFile.getItems().addAll(menuItemNewGame, new SeparatorMenuItem(), 
			menuItemSaveAs, menuItemLoadGame, new SeparatorMenuItem(),
			menuItemExit);
	
	menuItemNewGame.setOnAction(e -> newGame());
	menuItemSaveAs.setOnAction(e -> saveAs(primaryStage));
	menuItemLoadGame.setOnAction(e -> loadGame(primaryStage));
    menuItemExit.setOnAction(e -> System.exit(0));
    
    menuItemNewGame.setAccelerator(
            KeyCombination.keyCombination("Ctrl+N"));
    menuItemSaveAs.setAccelerator(
    	    KeyCombination.keyCombination("Ctrl+S"));
    menuItemLoadGame.setAccelerator(
    	    KeyCombination.keyCombination("Ctrl+L"));
    menuItemExit.setAccelerator(
            KeyCombination.keyCombination("Ctrl+X"));

	// Pane to hold cells
	GridPane pane = new GridPane(); 
	for (int i = 0; i < 3; i++)
	  for (int j = 0; j < 3; j++)
		pane.add(cell[i][j] = new Cell(), j, i);

	BorderPane borderPane = new BorderPane();
	borderPane.setTop(menuBar);
	borderPane.setCenter(pane);
	borderPane.setBottom(lblStatus);

	// Create a scene and place it in the stage
	Scene scene = new Scene(borderPane, 450, 350);
	primaryStage.setTitle("TicTacToe"); // Set the stage title
	primaryStage.setScene(scene); // Place the scene in the stage
	primaryStage.show(); // Display the stage   
  }
  
  /** Create new game */
  public void newGame() {
	for (int i = 0; i < 3; i++)
	  for (int j = 0; j < 3; j++) {
		cell[i][j].setToken(' ');
		cell[i][j].getChildren().clear();
	  }
    whoseTurn = 'X';
    lblStatus.setText("X's turn");
  }
  
  /** Save a current game */
  private void saveAs(Stage primaryStage) {
    FileChooser fileChooser = new FileChooser();
	fileChooser.setInitialDirectory(new File("."));
	fileChooser.setTitle("Enter file name");
	fileChooser.getExtensionFilters().add(
	              new ExtensionFilter("Tic-Tac-Toe files", "*.ttt"));
	File selectedFile = fileChooser.showSaveDialog(primaryStage);
	if (selectedFile != null) {
	    try ( // Create an output stream for file object.dat 
	   	      ObjectOutputStream output =
	    		  new ObjectOutputStream(new FileOutputStream(selectedFile));) {
		  output.writeChar(whoseTurn);
		  char[][] cellStates = new char[3][3];
		  for (int i = 0; i < 3; i++)
		    for (int j = 0; j < 3; j++)
		      cellStates[i][j] = cell[i][j].getToken();
		  output.writeObject(cellStates);
		}
		catch (IOException ex) {
		  ex.printStackTrace();
		}
	}
  }

  /** Load a previous game */
  private void loadGame(Stage primaryStage) {
	FileChooser fileChooser = new FileChooser();
	fileChooser.setInitialDirectory(new File("."));
	fileChooser.setTitle("Enter file name");
	fileChooser.getExtensionFilters().add(
		         new ExtensionFilter("Tic-Tac-Toe files", "*.ttt"));
	File selectedFile = fileChooser.showOpenDialog(primaryStage);
	if (selectedFile != null)
	try {
	  try ( // Create an input stream for the file we want to load
	        ObjectInputStream input =
	   	        new ObjectInputStream(new FileInputStream(selectedFile)); ) {
	    whoseTurn = input.readChar();
	    lblStatus.setText(whoseTurn + "'s turn");
	    char[][] cellStates = (char[][])(input.readObject());
	    for (int i = 0; i < 3; i++)
	      for (int j = 0; j < 3; j++)
	    	cell[i][j].setToken(cellStates[i][j]);
	  }
	}
	catch (Exception ex) {
	  ex.printStackTrace();
	}
  }

  
  
  /** Determine if the cell are all occupied */
  public boolean isFull() {
    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 3; j++)
        if (cell[i][j].getToken() == ' ')
          return false;

    return true;
  }

  /** Determine if the player with the specified token wins */
  public boolean isWon(char token) {
    for (int i = 0; i < 3; i++)
      if (cell[i][0].getToken() == token
          && cell[i][1].getToken() == token
          && cell[i][2].getToken() == token) {
        return true;
      }

    for (int j = 0; j < 3; j++)
      if (cell[0][j].getToken() ==  token
          && cell[1][j].getToken() == token
          && cell[2][j].getToken() == token) {
        return true;
      }

    if (cell[0][0].getToken() == token 
        && cell[1][1].getToken() == token        
        && cell[2][2].getToken() == token) {
      return true;
    }

    if (cell[0][2].getToken() == token
        && cell[1][1].getToken() == token
        && cell[2][0].getToken() == token) {
      return true;
    }

    return false;
  }

  /** An inner class for a cell */
  public class Cell extends Pane {
    // Token used for this cell
    private char token = ' ';

    public Cell() {
      setStyle("-fx-border-color: black"); 
      this.setPrefSize(800, 800);
      this.setOnMouseClicked(e -> handleMouseClick());
    }

    /** Return token */
    public char getToken() {
      return token;
    }

    /** Set a new token */
    public void setToken(char c) {
      token = c;
      this.getChildren().clear();
      
      if (token == 'X') {
        Line line1 = new Line(10, 10, 
          this.getWidth() - 10, this.getHeight() - 10);
        line1.endXProperty().bind(this.widthProperty().subtract(10));
        line1.endYProperty().bind(this.heightProperty().subtract(10));
        Line line2 = new Line(10, this.getHeight() - 10, 
          this.getWidth() - 10, 10);
        line2.startYProperty().bind(
          this.heightProperty().subtract(10));
        line2.endXProperty().bind(this.widthProperty().subtract(10));
        
        // Add the lines to the pane
        this.getChildren().addAll(line1, line2); 
      }
      else if (token == 'O') {
        Ellipse ellipse = new Ellipse(this.getWidth() / 2, 
          this.getHeight() / 2, this.getWidth() / 2 - 10, 
          this.getHeight() / 2 - 10);
        ellipse.centerXProperty().bind(
          this.widthProperty().divide(2));
        ellipse.centerYProperty().bind(
            this.heightProperty().divide(2));
        ellipse.radiusXProperty().bind(
            this.widthProperty().divide(2).subtract(10));        
        ellipse.radiusYProperty().bind(
            this.heightProperty().divide(2).subtract(10));   
        ellipse.setStroke(Color.BLACK);
        ellipse.setFill(Color.WHITE);
        
        getChildren().add(ellipse); // Add the ellipse to the pane
      }
    }

    /* Handle a mouse click event */
    private void handleMouseClick() {
      // If cell is empty and game is not over
      if (token == ' ' && whoseTurn != ' ') {
        setToken(whoseTurn); // Set token in the cell

        // Check game status
        if (isWon(whoseTurn)) {
          lblStatus.setText(whoseTurn + " won! The game is over");
          whoseTurn = ' '; // Game is over
        }
        else if (isFull()) {
          lblStatus.setText("Draw! The game is over");
          whoseTurn = ' '; // Game is over
        }
        else {
          // Change the turn
          whoseTurn = (whoseTurn == 'X') ? 'O' : 'X';
          // Display whose turn
          lblStatus.setText(whoseTurn + "'s turn");
        }
      }
    }
  }
  
  /**
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}