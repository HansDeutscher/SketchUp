package view;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import app.MainSketchup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import model.Translator;
import view.text.TextFileReader;

public class ContentLoader implements Initializable
{
	@FXML
	private Accordion acco;
	@FXML
	private TitledPane teoriePane;
	@FXML
	private ScrollPane teorieScroll;
	@FXML
	private ScrollPane praxeScroll;
	@FXML
	private ScrollPane spoilerScroll;
	@FXML
	private ScrollPane qaScroll;
	@FXML
	private ImageView logo;
	@FXML
	private ImageView backButton;
	@FXML
	private ImageView homeButton;
	@FXML
	private ImageView nextButton;
	@FXML
	private BorderPane border;
	@FXML
	private Button editButton;
	@FXML
	private Button saveButton;
	
	private TextFileReader reader = new TextFileReader();
	private Translator converter = new Translator();
	private int currentID;
	
	public void load(String ID) //vy�ist� star� a na�te nov� obsah podle ID okna
	{
		acco.setExpandedPane(teoriePane);	//otev�en� ��st je Teorie
		setCurrentID(ID);					//nastav�m moment�ln� ID
		check();							//zkontroluji, kter� tla��tka maj� b�t zobrazena
		erase();							//vyma�u v�echen star� obsah
		
		String text = reader.read(ID);		//na�tu si faktick� obsah ID.txt
		text = insertBreaks(text);			//vytvo��m funguj�c� zalomen�
		
		String teorie = "";
		try
		{
			teorie = cutOutSection(text, "/praxe/");		//najdu ��st, kter� n�le�� teorii
			text = cutText(text, teorie+"/praxe/");			//zkr�t�m text o ��st Teorie
		}
		catch(Exception e){errorOccured(ID, e, "/praxe/ was not found");}
		
		String praxe = "";
		try
		{
			praxe = cutOutSection(text, "/spoiler/");	//najdu ��st n�le��c� praxi
			text = cutText(text, praxe+"/spoiler/");	//vy��znu praxi
		}
		catch(Exception e){errorOccured(ID, e, "/spoiler/ was not found");}
		
		String spoiler = "";
		try
		{
			spoiler = cutOutSection(text,"/qa/");
			text = cutText(text, spoiler+"/qa/");
		}
		catch(Exception e){errorOccured(ID, e, "/qa/ was not found");}
		
		String qa = text;

		try
		{
			setContent(teorieScroll,teorie);
			setContent(praxeScroll,praxe);
			setContent(spoilerScroll,spoiler);
			setContent(qaScroll,qa);
		}
		catch(IllegalArgumentException e)
		{
			errorOccured(ID, e, "An image was not found!");
		}
	}

	private void check() //kontroluje, kter� tla��tka maj� b�t zobrazena
	{
		if(currentID<=1)
		{
			backButton.setVisible(false);
			if(!nextButton.isVisible())
			{
				nextButton.setVisible(true);
			}
		}
		else if(currentID<MainSketchup.getPocetLekci())
		{
			if(!backButton.isVisible())
			{
				backButton.setVisible(true);
			}
			if(!nextButton.isVisible())
			{
				nextButton.setVisible(true);
			}
		}
		else if (currentID>=MainSketchup.getPocetLekci())
		{
			nextButton.setVisible(false);
			if(!backButton.isVisible()){
				backButton.setVisible(true);
			}
		}
	}
	private void erase() //vyma�e obsah v�ech t�� ��st�
	{
		teorieScroll.setContent(null);
		praxeScroll.setContent(null);
		spoilerScroll.setContent(null);
		qaScroll.setContent(null);
	}
	private String insertBreaks(String text) //nahrad� m� nefunguj�c� zna�ky zalomen� t�mi funk�n�mi
	{
		text = text.replaceAll(" /n", " \n");
		return text;
	}
	private String insertBreaksVisible(String text)
	{
		text = text.replaceAll(" /n", " /n \n");
		return text;
	}
	private String cutOutSection(String text, String section) //vysekne danou ��st z cel�ho ID.txt po kl��ov� slovo section
	{
		int end = 0; 	//ozna�uje pozici, kde budu sekat
		while(!text.substring(end, end+section.length()).equals(section))
		{
			end++; //dokud nenaraz�m na hledan� v�raz, posouv�m postupn� end
		}
		String result = text.substring(0,end); //kdy� jsem na�el konec, text zde ust�ihnu
		return result;
	}
	private String cutText(String text, String section) //zkr�t� text o u� vy��znutou ��st
	{
		text = text.substring(section.length());
		return text;
	}

	private void errorOccured(String ID, Exception ex, String reason)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Exception Dialog");
		alert.setHeaderText("Error occured while loading "+ID+".txt file!");
		alert.setContentText(reason);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(expContent);
			
		alert.showAndWait();
	}
	
	/*
	 * Handlery tla��tek okna
	 */
	public void handleEditRequested(ActionEvent e)	//p�epne Q&A do m�du �prav
	{
		qaScroll.setContent(null);
		saveButton.setVisible(true);
		editButton.setOnAction(f -> handleCancel(f));
		editButton.setText("Cancel");
		
		String celyText = reader.read(Integer.toString(currentID));
		String qa = cutText(celyText,cutOutSection(celyText,"/qa/")+"/qa/");
		qa = insertBreaksVisible(qa);
		
		TextArea area = new TextArea(qa);
		area.setWrapText(true);
		area.setId("area");
		
		qaScroll.setContent(area);
	}
	public void handleSave(ActionEvent e)	//ulo�� zm�ny proveden� v Q&A
	{
		try
		{
			Node node = qaScroll.lookup("#area");
			TextArea area = (TextArea) node;
			reader.edit(Integer.toString(currentID), "/qa/", "",area.getText());
			loadJustQA();
		}
		catch(Exception ex)
		{
			errorOccured(Integer.toString(currentID), ex, "Failed while saving Q&A!");
		}
	}
	private void handleCancel(ActionEvent e)	//zahod� zm�ny proveden� v Q&A
	{
		loadJustQA();
	}
	private void loadJustQA()	//na�te jenom ��st QA
	{
		qaScroll.setContent(null);
		
		String text = reader.read(Integer.toString(currentID));
		String qa = cutText(text,cutOutSection(text,"/qa/")+"/qa/");
		qa = insertBreaks(qa);
		
		saveButton.setVisible(false);
		editButton.setOnAction(f -> handleEditRequested(f));
		editButton.setText("Edit");
		
		setContent(qaScroll,qa);
	}
	private void handleHomeRequested(MouseEvent e)
	{
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainPageView.fxml"));
			Parent root = loader.load();
			Scene scene = new Scene(root);
			
			MainPageController MPcontroller = loader.getController();
			MPcontroller.getAnchorPane().setPrefHeight(border.getHeight());
			MPcontroller.getAnchorPane().setPrefWidth(border.getWidth());
			
			MainSketchup.jeviste.setScene(scene);
		}
		catch(Exception ex)
		{
			System.out.println("An error occured while firing handleLogoClicked()");
		}
	}
	private void handleNextButton(MouseEvent e){	load(Integer.toString(currentID+1));}
	private void handleBackButton(MouseEvent e){	load(Integer.toString(currentID-1));}
	
	/*
	 * Getter/Setter
	 */
	
	public BorderPane getBorderPane()
	{
		return this.border;
	}
	private void setCurrentID(String ID) //setter moment�ln� zobrazen�ho ID okna
	{
		this.currentID = Integer.valueOf(ID);
	}
	private void setContent(ScrollPane pane, String text)
	{
		ArrayList<TextFlow> list = converter.convert(text);
		VBox box = new VBox();
		for(TextFlow flow:list)
		{
			box.getChildren().add(flow);
		}
		pane.setContent(box);
		pane.setVvalue(0);
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		logo.setOnMouseClicked(e -> handleHomeRequested(e));
		nextButton.setOnMouseClicked(e -> handleNextButton(e));
		homeButton.setOnMouseClicked(e -> handleHomeRequested(e));
		backButton.setOnMouseClicked(e -> handleBackButton(e));
	}
}
