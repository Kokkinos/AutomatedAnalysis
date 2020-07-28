package gr.uoi.cs.dbsea.gui;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;

public class Chart {
	private File file;
	private ArrayList<String[]> percentages = new ArrayList<String[]>();
	DefaultCategoryDataset dataset;
	
	public Chart(File file) {
		this.file = file;
		dataset = new DefaultCategoryDataset();
	}
	
	private String getAbsolutePath() {
		return file.getAbsolutePath();
	}
	
	protected void printPath() {
		System.out.println(this.getAbsolutePath());
	}
	
	protected void readFile() throws IOException {
		BufferedReader csvReader = new BufferedReader(new FileReader(this.getAbsolutePath()));
		String row;
		
		while((row = csvReader.readLine()) != null) {
			percentages.add(row.split(","));
		}
	}
	
	private String extractNameOfTheFile() {
		String fullPath = getAbsolutePath();
		System.out.println(fullPath.substring(fullPath.lastIndexOf("\\")+1));
		String nameOfFile = fullPath.substring(fullPath.lastIndexOf("\\")+1);
		String nameOfFileSplitted = nameOfFile.substring(0, nameOfFile.indexOf("."));
		System.out.println(nameOfFileSplitted);
		return nameOfFileSplitted;
	}
	
	protected void createChart() {
		if(extractNameOfTheFile().equals("tableEvolutionAdherence") || extractNameOfTheFile().equals("columnEvolutionAdherence")){
			setDatasetForEvolutionOfSchmemata();
		}
		if(extractNameOfTheFile().equals("tableAdherenceToTheOverallStyle") || extractNameOfTheFile().equals("columnAdherenceToTheOverallStyle")){
			setDatasetForOverallStyle();
		}
		if(extractNameOfTheFile().equals("tableAdherenceToEachConvention") || extractNameOfTheFile().equals("columnAdherenceToEachConvention")){
			setDatasetForEachConvention();
		}
		
		
	}
	
	private void setDatasetForOverallStyle() {
		String firstIndicator = percentages.get(0)[0];
		String secondIndicator = percentages.get(0)[1];
		String thirdIndicator = percentages.get(0)[2];
		
		for (int i=1; i<percentages.size(); i++) {
			dataset.setValue(Double.parseDouble(percentages.get(i)[0]), firstIndicator, percentages.get(i)[3]);
			dataset.setValue(Double.parseDouble(percentages.get(i)[1]), secondIndicator, percentages.get(i)[3]);
			dataset.setValue(Double.parseDouble(percentages.get(i)[2]), thirdIndicator, percentages.get(i)[3]);
		}
		
		displayChart();
	}
	
	private void setDatasetForEvolutionOfSchmemata() {
		String firstIndicator = percentages.get(0)[1];
		String secondIndicator = percentages.get(0)[2];
		String thirdIndicator = percentages.get(0)[3];
		
		for (int i=1; i<percentages.size(); i++) {
			dataset.setValue(Double.parseDouble(percentages.get(i)[1]), firstIndicator, percentages.get(i)[0]);
			dataset.setValue(Double.parseDouble(percentages.get(i)[2]), secondIndicator, percentages.get(i)[0]);
			dataset.setValue(Double.parseDouble(percentages.get(i)[3]), thirdIndicator, percentages.get(i)[0]);
		}
		
		displayChart();
	}
	
	private void setDatasetForEachConvention() {
		String firstIndicator = percentages.get(0)[1];
		String secondIndicator = percentages.get(0)[2];
		String thirdIndicator = percentages.get(0)[3];
		String fourthIndicator = percentages.get(0)[4];
		

		for (int i=1; i<percentages.size(); i++) {
			dataset.setValue(Double.parseDouble(percentages.get(i)[1]), firstIndicator, percentages.get(i)[0]);
			dataset.setValue(Double.parseDouble(percentages.get(i)[2]), secondIndicator, percentages.get(i)[0]);
			dataset.setValue(Double.parseDouble(percentages.get(i)[3]), thirdIndicator, percentages.get(i)[0]);
			dataset.setValue(Double.parseDouble(percentages.get(i)[4]), fourthIndicator, percentages.get(i)[0]);
		}
		
		displayChart();
	}
	
	private void displayChart() {
		JFreeChart chart = ChartFactory.createBarChart(extractNameOfTheFile(), "Naming Conventions", "Percentages", dataset);
		CategoryPlot p = chart.getCategoryPlot();
		p.setRangeGridlinePaint(Color.black);
		ChartFrame frame = new ChartFrame("Bar chart for adherence percentages",chart);
		frame.setVisible(true);
		frame.setSize(450,350);
	}
}
