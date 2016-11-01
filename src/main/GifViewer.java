package main;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * A simple image viewer created with the goals
 * 		- Allowing gifs to be played
 * 		- Be faster than Windows 10's Photos application
 * 
 * @author Jordan
 *
 */
public class GifViewer extends Application
{
	//properties
	private  Scene scene = null;
	private boolean ignoreNextPress = false;
	private File initialDirectory = new File(System.getProperty("user.dir"));	//set initial file directory to working directory
	private File lastOpened;	//variable to store last folder file was opened from while using the program
	private BorderPane borderPane;
	private ImageView imageView;
	private FileChooser.ExtensionFilter extFilters 
		=	new FileChooser.ExtensionFilter("Image Files", "*.gif", "*.jpg", "*.png", "*.bmp", "*.jpeg");		//supported image formats
	private static double SCENE_WIDTH = 600;
	private static double SCENE_HEIGHT = 400;
	
	/*
	 * (non-Javadoc)
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 * 
	 * Set up of BorderPane, handling of shortcuts, menu etc.
	 */
	@Override
	public void start(Stage primaryStage)
	{
		borderPane = new BorderPane();
		scene = new Scene(borderPane, SCENE_WIDTH, SCENE_HEIGHT);
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem openMenuItem = new MenuItem("Open");
		
		scene.setOnKeyReleased(new EventHandler<KeyEvent>()
				{
					public void handle(KeyEvent ke)
					{
						//shortcuts
						String text = ke.getText();
						if(!(ke.isControlDown() || ke.isMetaDown()))
						{
							if(text.equalsIgnoreCase("o") && ignoreNextPress)
							{
								ignoreNextPress = false;
								return;
							}
						}
						shortcuts(ke);
					}
				});
		
		openMenuItem.setOnAction(new EventHandler<ActionEvent>()
				{
					public void handle(ActionEvent event)
					{
						//open file
						openFile();
					}
				});
		MenuItem exitMenuItem = new MenuItem("Exit");
		exitMenuItem.setOnAction(new EventHandler<ActionEvent>()
				{
					//exit
					public void handle(ActionEvent event)
					{
						try 
						{
							//quit - exit
							stop();
							System.exit(0);
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				});
		//Add items to file menu	
		fileMenu.getItems().addAll(
				openMenuItem, 
				new SeparatorMenuItem(), 
				exitMenuItem);
		//add menus to bar
		menuBar.getMenus().add(fileMenu);
		//border pane set up
		borderPane.setTop(menuBar);
		//add css
		scene.getStylesheets().add("style.css");
		//set up primary stage
		primaryStage.setTitle("Image Viewer");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	/*
	 * Method that handles opening images using a FileChooser
	 */
	private void openFile()
	{
		String filePath = "";	//Initialise file path
		try 
		{
			FileChooser fc = new FileChooser();
			fc.setInitialDirectory(initialDirectory);
			fc.getExtensionFilters().add(extFilters);
			fc.setTitle("Select image to open");
			File selectedFile = fc.showOpenDialog(null);	//sets file to selected file in file chooser
			if(selectedFile != null)
			{
				filePath  = ("file:" + selectedFile.getAbsolutePath());	//turn filepath into URL
				lastOpened = new File (selectedFile.getAbsolutePath());
				Image openImage = new Image(filePath, 0, 0, true, true);	//creates new image - sets width to that of scene w/ smoothing and resizing
				 imageView = new ImageView(openImage);
				 imageView.fitWidthProperty().bind(scene.widthProperty());		//makes imageview width resize based on scene width
				 imageView.fitHeightProperty().bind(scene.heightProperty());	//makes imageview height resize based on scene height
				borderPane.setCenter(imageView);
				initialDirectory = new File(selectedFile.getParentFile().getAbsolutePath());
			}
			fc.setInitialDirectory(initialDirectory);
		} 
		catch(NullPointerException e)	//if no file is selected, the imageview / borderpane is set to the last opened image.
		{
			filePath  = ("file:" + lastOpened.getAbsolutePath());	//turn filepath into URL
			Image openImage = new Image(filePath, 0, 0, true, true);	//creates new image - sets width to that of scene w/ smoothing and resizing
			 imageView = new ImageView(openImage);
			 imageView.fitWidthProperty().bind(scene.widthProperty());		//makes imageview width resize based on scene width
			 imageView.fitHeightProperty().bind(scene.heightProperty());	//makes imageview height resize based on scene height
			borderPane.setCenter(imageView);
			initialDirectory = new File(lastOpened.getParentFile().getAbsolutePath());
		}
	}
	
	/*
	 * Method handling simple shortcuts
	 */
	private  void shortcuts(KeyEvent ke)
	{
		boolean ctrl = false;	//boolean for if ctrl is pressed
		String text = ke.getText();
		if(ke.isControlDown() || ke.isMetaDown())
		{
			ctrl =true;
		}
		if(ctrl && text.equalsIgnoreCase("o"))
		{
			//open file
			openFile();
			ignoreNextPress = true;
		}
	}
	
	//fall back method
	public static void main(String[] args) 
	{
		launch(args);
	}
}
