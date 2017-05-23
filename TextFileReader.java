package view.text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class TextFileReader 
{
	public String read(String TextFileID)
	{		
		String obsah = "";
		try
		{
			File probe = new File("sources");
			BufferedReader reader = new BufferedReader(new FileReader(new File(probe.getAbsolutePath()+"/"+TextFileID+".txt")));
			
			String line = null;
			
			while((line = reader.readLine()) != null)
			{
				obsah+=line;
			}
			reader.close();
		} 
		catch (IOException e) 
		{
			errorOccured(TextFileID, e, "Failed to load and or read the text file.");
		}
		return obsah;
	}
	public void edit(String TextFileID,String start,String stop,String newText)
	{
		try
		{
			File probe = new File("sources");
			BufferedReader reader = new BufferedReader(new FileReader(new File(probe.getAbsolutePath()+"/"+TextFileID+".txt")));

			File tmp = new File("tmp.txt");
		    BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		    
		    String line = reader.readLine();
		    while(!line.startsWith(start)) //opisuji to, co má zùstat stejné
		    {
		    	bw.write(line);
		    	bw.newLine();
		    	line = reader.readLine();
		    }
		    bw.write(line); //opíšu start, který je zrovna vybrán
		    bw.newLine();
		    bw.write(newText);	//pøidám to, co je rozdílné
		    bw.newLine();
		    if(stop != "")	//pokud mám nìjaký konec ... zatím neotestováno
		    {
		    	line = reader.readLine();
		    	while(!line.startsWith(stop))
		    	{
		    		line = reader.readLine();
		    	}
		    	while(line != null)
		    	{
		    		bw.write(line);
		    		line = reader.readLine();
		    	}
		    }
		    bw.flush();
		    bw.close();
		    reader.close();
		    
		    File oldOne = new File(probe.getAbsolutePath()+"/"+TextFileID+".txt");
		    oldOne.delete();
		    tmp.renameTo(new File(probe.getAbsolutePath()+"/"+TextFileID+".txt"));
		}
		catch(Exception ex)
		{
			errorOccured(TextFileID, ex, "Failed to save changes to text file!");
			ex.printStackTrace();
		}
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
}
