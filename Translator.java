package model;

import java.util.ArrayList;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.SubScene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import view.MediaPlayerController;

public class Translator 
{
	public ArrayList<TextFlow> convert(String zadani)
	{
		ArrayList<TextFlow> list = new ArrayList<>();
		
		TextFlow primaryFlow = new TextFlow();
		primaryFlow.setTextAlignment(TextAlignment.JUSTIFY);
		primaryFlow.setPadding(new Insets(20));
		primaryFlow.setLineSpacing(3);
		primaryFlow.setStyle("-fx-font-size: 18px;");
		
		list.add(primaryFlow);
		
		int last = 0;
		for(int i = 0; i<zadani.length()-6;i++)
		{
			if(zadani.substring(i,i+6).equals("<head ")) //	<head "text" size>
			{
				if(i>0)
				{
					Text text = new Text(zadani.substring(last, i));
					list.get(list.size()-1).getChildren().add(text);
				}
				
				int j = 0;
				String cast = zadani.substring(i);
				while(cast.charAt(j) != '>')
				{
					j++;
				}
				String proverovanyKus = cast.substring(0, j);
				TextFlow header = cutOutHeader(proverovanyKus);
				
				last = i+j+1;
				
				list.add(header);
				
				TextFlow newFlow = new TextFlow();
				newFlow.setTextAlignment(TextAlignment.JUSTIFY);
				newFlow.setPadding(new Insets(20));
				newFlow.setLineSpacing(3);
				newFlow.setStyle("-fx-font-size: 18px;");
				
				list.add(newFlow);
			}
			else if(zadani.substring(i,i+5).equals("<img ")) // <img /source/file.jpg width height>
			{
				Text text = new Text(zadani.substring(last, i));
				
				int j = 0;
				String proverovanyKousek = zadani.substring(i);
				while(proverovanyKousek.charAt(j) != '>')
				{
					j++;
				}
				String imgZadani = proverovanyKousek.substring(0, j);
				
				last = i+j+1;
				
				Image image = cutOutImage(imgZadani);
				ImageView imgView = new ImageView(image);
				list.get(list.size()-1).getChildren().addAll(text, imgView);
			}
			else if(zadani.substring(i,i+5).equals("<icn "))	// <icn /source/file.jpg width height text>
			{
				Text text = new Text(zadani.substring(last, i));
				list.get(list.size()-1).getChildren().add(text);
				
				int j = 0;
				String fragment = zadani.substring(i);
				while(fragment.charAt(j) != '>')
				{
					j++;
				}
				String finalFragment = fragment.substring(0, j);
				
				last = i+j+1;
				
				TextFlow flow = cutOutTextFlow(finalFragment);
				
				TextFlow newFlow = new TextFlow();
				newFlow.setTextAlignment(TextAlignment.JUSTIFY);
				newFlow.setPadding(new Insets(20));
				newFlow.setLineSpacing(3);
				newFlow.setStyle("-fx-font-size: 18px;");
				
				list.add(flow);
				list.add(newFlow);
			}
			else if(zadani.substring(i,i+5).equals("<vid ")) // <vid /source/vid.mp4 width height text>
			{
				Text text = new Text(zadani.substring(last,i));
				list.get(list.size()-1).getChildren().add(text);
				
				int j = 0;
				String fragment = zadani.substring(i);
				while(fragment.charAt(j) != '>')
				{
					j++;
				}
				String finalFragment = fragment.substring(0, j);
				
				last = i+j+1;
				
				TextFlow flow = cutOutVideoFlow(finalFragment);
				
				TextFlow newFlow = new TextFlow();
				newFlow.setTextAlignment(TextAlignment.JUSTIFY);
				newFlow.setPadding(new Insets(20));
				newFlow.setLineSpacing(3);
				newFlow.setStyle("-fx-font-size: 18px;");
				
				list.add(flow);
				list.add(newFlow);
			}
		}
		if(last != zadani.length())
		{
			Text lastText = new Text(zadani.substring(last));
			list.get(list.size()-1).getChildren().add(lastText);
		}
		return list;
	}
	private TextFlow cutOutVideoFlow(String zadani)	//<vid source width height textAbove>
	{
		TextFlow flow = new TextFlow();
		flow.setTextAlignment(TextAlignment.CENTER);
		
		int begin = 5;	//pøeskoèím "<icn "
		int end = 0;
		while(zadani.substring(begin).charAt(end) != ' ')
		{
			end++; //dojdu až na konec URL
		}
		String url = zadani.substring(begin,begin+end);
		begin += end+1; //zaènu za znakem, kde jsem poslednì skonèil
		end = 0;
		while(zadani.substring(begin).charAt(end) != ' ')
		{
			end++; //dojdu až na konec šíøky
		}
		double width = Double.parseDouble(zadani.substring(begin, begin+end));
		begin += end+1; //zaènu za šíøkou
		end = 0;
		while(zadani.substring(begin).charAt(end) != ' ')
		{
			end++;	//dojdu na konec výšky
		}
		double height = Double.parseDouble(zadani.substring(begin, begin+end));
		begin = begin+end+1;
		String popis = zadani.substring(begin)+" \n"; //za výškou už je jenom popis obrázku
		
		try
		{
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MediaPlayer.fxml"));
			Parent root = loader.load();
			MediaPlayerController cont = loader.getController();
			cont.setMedia(url,width,height);
		
			SubScene subScene = new SubScene(root,cont.getWidth(),cont.getHeight());
			
			Text text = new Text(popis);
			
			flow.getChildren().addAll(text,subScene);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		
		return flow;
	}
	private TextFlow cutOutTextFlow(String zadani) //	<icn source width height String>
	{
		TextFlow flow = new TextFlow();
		flow.setTextAlignment(TextAlignment.CENTER);
		
		int begin = 5;	//pøeskoèím "<icn "
		int end = 0;
		while(zadani.substring(begin).charAt(end) != ' ')
		{
			end++; //dojdu až na konec URL
		}
		String url = zadani.substring(begin,begin+end);
		begin += end+1; //zaènu za znakem, kde jsem poslednì skonèil
		end = 0;
		while(zadani.substring(begin).charAt(end) != ' ')
		{
			end++; //dojdu až na konec šíøky
		}
		double width = Double.parseDouble(zadani.substring(begin, begin+end));
		begin += end+1; //zaènu za šíøkou
		end = 0;
		while(zadani.substring(begin).charAt(end) != ' ')
		{
			end++;	//dojdu na konec výšky
		}
		double height = Double.parseDouble(zadani.substring(begin, begin+end));
		begin = begin+end+1;
		String popis = " \n"+zadani.substring(begin); //za výškou už je jenom popis obrázku
		
		Image img = new Image(url, width, height, true, true);
		ImageView imgView = new ImageView(img);
		Text text = new Text(popis);
		
		flow.getChildren().addAll(imgView, text);
		
		return flow;
	}
	private Image cutOutImage(String zadani) // <img source width height>
	{
		int begin = 5;
		int end = 0;
		while(zadani.substring(begin).charAt(end) != ' ')
		{
			end++;
		}
		String url = zadani.substring(begin,begin+end);
		begin += end+1;
		end = 0;
		while(!(zadani.substring(begin).charAt(end) == ' '))
		{
			end++;
		}
		double width = Double.parseDouble(zadani.substring(begin,begin+end));
		begin += end+1;
		double height = Double.parseDouble(zadani.substring(begin));
		Image img = new Image(url, width, height, true, true);
		return img;
	}
	private TextFlow cutOutHeader(String zadani) //	<head "text" size>
	{
		int skok = 6;
		int end = 0;
		while(zadani.substring(skok).charAt(end) != '"')
		{
			end++;
		}
		skok += end+1;
		end = 0;
		while(zadani.substring(skok).charAt(end) != '"')
		{
			end++;
		}
		String header = zadani.substring(skok, skok+end);
		int size = Integer.parseInt(zadani.substring(skok+end+2));
		
		TextFlow newFlow = new TextFlow();
		newFlow.setPadding(new Insets(20));
		newFlow.setTextAlignment(TextAlignment.CENTER);
		Text headerText = new Text(header);
		headerText.setStyle("-fx-font-size: "+size+"px;"
				+ "-fx-font-weight: bold;");
		newFlow.getChildren().add(headerText);
		
		return newFlow;
	}
}
