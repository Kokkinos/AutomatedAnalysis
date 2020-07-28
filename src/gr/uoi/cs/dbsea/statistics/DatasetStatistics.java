package gr.uoi.cs.dbsea.statistics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.math3.stat.Frequency;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import org.apache.commons.math3.stat.descriptive.moment.Skewness;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Min;
import org.apache.commons.math3.stat.descriptive.summary.Sum;
import org.apache.commons.math3.util.Precision;

import com.sun.org.apache.xpath.internal.operations.Bool;

import gr.uoi.cs.dbsea.logger.Logger;

public class DatasetStatistics {

	private String TablePath;
	private String TableTitle;
	private String[] TableTitleRow;
	private int TableTitleSize;
	private int TableMeasurementSize;
	private String ColumnTitle;
	private String[] ColumnTitleRow;

	private int ColumnTitleSize;
	private int ColumnMeasurementSize;
	private double TableMeasurements[][];
	private Double TotalTables[];
	private Double TotalColumns[];

	private double ColumnMeasurements[][];
	private String ColumnPath;
	private List<String> tableRecords = new ArrayList<String>();
	private List<String> columnRecords = new ArrayList<String>();
	private FileWriter TableWriter;
	private FileWriter ColumnWriter;

	// statistics
	private double SkewnessTables[];
	private double SkewnessColumns[];

	private double STDTables[];// descriptive
	private double STDColumns[];

	private double MedianTables[];// descriptive
	private double MedianColumns[];

	private double MaxTables[];// rank
	private double MinTables[];

	private double MaxColumns[];// rank
	private double MinColumns[];

	private double SumTables[];// rank
	private double SumColumns[];

	private double AverageTables[];// rank
	private double AverageColumns[];

	private double ModeTables[];// rank user frequency for this one stat.Frequency
	private double ModeColumns[];
	private Double nan;
	DecimalFormat newFormat = new DecimalFormat("#.##");
	/////////////
	private int CF_byTables = 0;
	private int PF_byTables = 0;
	private int NF_byTables = 0;
	private int CF_byColumns = 0;
	private int PF_byColumns = 0;
	private int NF_byColumns = 0;
	
	private static LinkedHashMap<String,ArrayList<Double>> rulesAndTableAdherence = new LinkedHashMap<String,ArrayList<Double>>();
	private static LinkedHashMap<String,ArrayList<Double>> rulesAndColumnAdherence = new LinkedHashMap<String,ArrayList<Double>>();
	private static LinkedHashMap<String,ArrayList<Double>> tableAdherenceEvolution = new LinkedHashMap<String,ArrayList<Double>>();
	private static LinkedHashMap<String,ArrayList<Double>> columnAdherenceEvolution = new LinkedHashMap<String,ArrayList<Double>>();
	private static int setCounter1 = 0;
	private static int setCounter2 = 0;
	private static int setCounter3 = 0;
	private static int setCounter4 = 0;
 	private double cfPercentageForTables;
	private double pfPercentageForTables;
	private double nfPercentageForTables;
	private double cfPercentageForColumns;
	private double pfPercentageForColumns;
	private double nfPercentageForColumns;
	private FileWriter AiWriter;
	
	public DatasetStatistics(String tablePath, String columnPath) {
		TablePath = tablePath;
		ColumnPath = columnPath;
		nan = new Double(Double.NaN);
	
	}
	
	static {//private void initializeRulesAndTableAdherence() {
		ArrayList<Double> emptyList = new ArrayList<Double>();
		for (int i=0; i<4; i++) {
			emptyList.add(0.0);
		}
		rulesAndTableAdherence.put("UTC",emptyList);
		rulesAndTableAdherence.put("UPL",emptyList);
		rulesAndTableAdherence.put("SWL",emptyList);
		rulesAndTableAdherence.put("EWL",emptyList);
		rulesAndTableAdherence.put("UMW",emptyList);
		rulesAndTableAdherence.put("TIP",emptyList);
		rulesAndTableAdherence.put("SWC",emptyList);
		rulesAndTableAdherence.put("ACC",emptyList);
		rulesAndTableAdherence.put("ARW",emptyList);
		rulesAndTableAdherence.put("ACU",emptyList);
		rulesAndTableAdherence.put("AUS",emptyList);
		rulesAndTableAdherence.put("ASC",emptyList);
		rulesAndTableAdherence.put("AUD",emptyList);
		//rulesAndTableAdherence.put("AUV",emptyList);
		rulesAndTableAdherence.put("ACN",emptyList);
		
		for (String key : rulesAndTableAdherence.keySet()) {
			ArrayList<Double> tempEmptyList = new ArrayList<Double>();
			for (int i=0; i<4; i++) {
				tempEmptyList.add(0.0);
			}
			rulesAndTableAdherence.replace(key,tempEmptyList);
			
		}
		// rulesAndColumnAdherence
		ArrayList<Double> emptyList1 = new ArrayList<Double>();
		for (int i=0; i<4; i++) {
			emptyList1.add(0.0);
		}
		rulesAndColumnAdherence.put("UTC", emptyList1);
		rulesAndColumnAdherence.put("UPL", emptyList1);
		rulesAndColumnAdherence.put("SWL", emptyList1);
		rulesAndColumnAdherence.put("EWL", emptyList1);
		rulesAndColumnAdherence.put("UMW", emptyList1);
		rulesAndColumnAdherence.put("USP", emptyList1);
		rulesAndColumnAdherence.put("CIS", emptyList1);
		rulesAndColumnAdherence.put("ACC", emptyList1);
		rulesAndColumnAdherence.put("ARW", emptyList1);
		rulesAndColumnAdherence.put("ACU", emptyList1);
		rulesAndColumnAdherence.put("AUS", emptyList1);
		rulesAndColumnAdherence.put("ASC", emptyList1);
		rulesAndColumnAdherence.put("AUD", emptyList1);
		rulesAndColumnAdherence.put("AII", emptyList1);
		rulesAndColumnAdherence.put("DNC", emptyList1);
		rulesAndColumnAdherence.put("NBP", emptyList1);
		
		for (String key : rulesAndColumnAdherence.keySet()) {
			ArrayList<Double> tempEmptyList = new ArrayList<Double>();
			for (int i=0; i<4; i++) {
				tempEmptyList.add(0.0);
			}
			rulesAndColumnAdherence.replace(key,tempEmptyList);
		}
		////////////////////////////////////////////
		ArrayList<Double> emptyList2 = new ArrayList<Double>();
		for (int i=0; i<3; i++) {
			emptyList2.add(0.0);
		}
		tableAdherenceEvolution.put("UTC",emptyList2);
		tableAdherenceEvolution.put("UPL",emptyList2);
		tableAdherenceEvolution.put("SWL",emptyList2);
		tableAdherenceEvolution.put("EWL",emptyList2);
		tableAdherenceEvolution.put("UMW",emptyList2);
		tableAdherenceEvolution.put("TIP",emptyList2);
		tableAdherenceEvolution.put("SWC",emptyList2);
		tableAdherenceEvolution.put("ACC",emptyList2);
		tableAdherenceEvolution.put("ARW",emptyList2);
		tableAdherenceEvolution.put("ACU",emptyList2);
		tableAdherenceEvolution.put("AUS",emptyList2);
		tableAdherenceEvolution.put("ASC",emptyList2);
		tableAdherenceEvolution.put("AUD",emptyList2);
		//rulesAndTableAdherence.put("AUV",emptyList);
		tableAdherenceEvolution.put("ACN",emptyList2);
		
		for (String key : tableAdherenceEvolution.keySet()) {
			ArrayList<Double> tempEmptyList = new ArrayList<Double>();
			for (int i=0; i<3; i++) {
				tempEmptyList.add(0.0);
			}
			tableAdherenceEvolution.replace(key,tempEmptyList);
			
		}
		////////////////////////////////
		ArrayList<Double> emptyList3 = new ArrayList<Double>();
		for (int i=0; i<3; i++) {
			emptyList3.add(0.0);
		}
		columnAdherenceEvolution.put("UTC",emptyList3);
		columnAdherenceEvolution.put("UPL",emptyList3);
		columnAdherenceEvolution.put("SWL",emptyList3);
		columnAdherenceEvolution.put("EWL",emptyList3);
		columnAdherenceEvolution.put("UMW",emptyList3);
		columnAdherenceEvolution.put("USP",emptyList3);
		columnAdherenceEvolution.put("CIS",emptyList3);
		columnAdherenceEvolution.put("ACC",emptyList3);
		columnAdherenceEvolution.put("ARW",emptyList3);
		columnAdherenceEvolution.put("ACU",emptyList3);
		columnAdherenceEvolution.put("AUS",emptyList3);
		columnAdherenceEvolution.put("ASC",emptyList3);
		columnAdherenceEvolution.put("AUD",emptyList3);
		//rulesAndTableAdherence.put("AUV",emptyList);
		columnAdherenceEvolution.put("AII",emptyList3);
		columnAdherenceEvolution.put("DNC",emptyList3);
		columnAdherenceEvolution.put("NBP",emptyList3);
		for (String key : columnAdherenceEvolution.keySet()) {
			ArrayList<Double> tempEmptyList = new ArrayList<Double>();
			for (int i=0; i<3; i++) {
				tempEmptyList.add(0.0);
			}
			columnAdherenceEvolution.replace(key,tempEmptyList);
			
		}
	}
	
	public void FillMeasurements() {
		try {

			ReadMeasurements();
			TableTitle = tableRecords.get(0).replace(",,", ",");//pairnei thn 1h grammh ap to excel
			TableTitleRow = TableTitle.split(",");
			ColumnTitle = columnRecords.get(0).replace(",,", ",");

			ColumnTitleRow = ColumnTitle.split(",");

			TableTitleSize = TableTitle.split(",").length - 1;
			TableMeasurements = new double[tableRecords.size() - 1][TableTitleSize];//autos o pinakas krataei ta statistica apo oles tis version,
																					//kathe upopinakas einai ta statistica mias version
			ColumnTitleSize = ColumnTitle.split(",").length - 1;
			ColumnMeasurements = new double[columnRecords.size() - 1][ColumnTitleSize];//to idio kai autos gia ta columns 
			TableMeasurementSize = TableMeasurements.length;
			ColumnMeasurementSize = ColumnMeasurements.length;
			for (int i = 1; i < tableRecords.size(); i++) {//table.records.size()=84(versions)
				String[] rowMeasurements = tableRecords.get(i).split(",");//apotelesmata kathe grammhs-version
				for (int j = 0; j < rowMeasurements.length - 1; j++) {

					TableMeasurements[i - 1][j] = Precision
							.round(Double.parseDouble(rowMeasurements[j].replace(" ", "")), 2);
				}
			}

			for (int i = 1; i < columnRecords.size(); i++) {
				String[] rowMeasurements = columnRecords.get(i).split(",");
				for (int j = 0; j < rowMeasurements.length - 1; j++) {
					ColumnMeasurements[i - 1][j] = Precision
							.round(Double.parseDouble(rowMeasurements[j].replace(" ", "")), 2);
				}
			}
			//-------new--------//
			if (TablePath.substring(0,27).equals("Results\\SchemaLevelAnalysis")) {
				calculateAdherenceToTheOverallStyle();
				calculateTableAdherenceToEachConvention();
				calculateColumnAdherenceToEachConvention();
				calculateTableAdherenceEvolution();
				calculateColumnAdherenceEvolution();
			}
			WriteMeasurementsToFile();

		//TableWriter.flush();
		//	ColumnWriter.flush();

		} catch (Exception e) {
			
			Logger.Log(e);
			
		}
	}

	private void CreateStatisticTables(int tablesLength, int columnsLengths) {
		
		try {
			SkewnessTables = new double[tablesLength];
			SkewnessColumns = new double[columnsLengths];

			STDTables = new double[tablesLength];// descriptive
			STDColumns = new double[columnsLengths];

			MedianTables = new double[tablesLength];// descriptive
			MedianColumns = new double[columnsLengths];

			MaxTables = new double[tablesLength];// rank
			MinTables = new double[tablesLength];// rank
			MaxColumns = new double[columnsLengths];// rank
			MinColumns = new double[columnsLengths];

			SumTables = new double[tablesLength];// rank
			SumColumns = new double[columnsLengths];

			AverageTables = new double[tablesLength];// rank
			AverageColumns = new double[columnsLengths];

			ModeTables = new double[tablesLength];// rank user frequency for this one stat.Frequency
			ModeColumns = new double[columnsLengths];
		} catch (Exception e) {
			
			Logger.Log(e);
	
		}

	}

	private void WriteMeasurementsToFile() {

		try {
			CreateStatisticTables(TableTitleSize, ColumnTitleSize);

			TableWriter = new FileWriter(TablePath.replace(".csv", "_Dataset_Stastics.csv"));
			ColumnWriter = new FileWriter(ColumnPath.replace(".csv", "_Dataset_Stastics.csv"));

			WriteCorrelations();

			WriteDescriptive();

			WriteMode();

			WriteAverage();

			WriteMax();

			WriteMin();
			
			//WriteAi();
			
			//WriteAdherenceToEachConvention();
			
			//WriteAdherenceEvolution();
			

			TableWriter.close();
			ColumnWriter.close();
		} catch (IOException e) {

			Logger.Log(e);

		}

	}

	private void WriteCorrelations() throws IOException {

		try {
			KendallsCorrelation tableKendall = new KendallsCorrelation(TableMeasurements);

			KendallsCorrelation columnKendall = new KendallsCorrelation(ColumnMeasurements);
			SpearmansCorrelation tableSpearman = new SpearmansCorrelation();
			SpearmansCorrelation columnSpearman = new SpearmansCorrelation();

			double[][] tableK = tableKendall.getCorrelationMatrix().getData();
			double[][] columnK = columnKendall.getCorrelationMatrix().getData();

			double[][] tableS = tableSpearman.computeCorrelationMatrix(TableMeasurements).getData();
			double[][] columnS = columnSpearman.computeCorrelationMatrix(ColumnMeasurements).getData();
			TableWriter.append("Kendall\n");
			TableWriter = writeCorrelation(tableK, TotalTables, TableTitle.replace(",Database Name", "").split(","),
					TableWriter);
			TableWriter.append("Spearman\n");
			TableWriter = writeCorrelation(tableS, TotalTables, TableTitle.replace(",Database Name", "").split(","),
					TableWriter);
			ColumnWriter.append("Kendall\n");
			ColumnWriter = writeCorrelation(columnK, TotalColumns, ColumnTitle.replace(",Database Name", "").split(","),
					ColumnWriter);
			ColumnWriter.append("Spearman\n");
			ColumnWriter = writeCorrelation(columnS, TotalColumns, ColumnTitle.replace(",Database Name", "").split(","),
					ColumnWriter);

	
			TableWriter.append("\n");
			ColumnWriter.append("\n");
		} catch (Exception e) {
	
			Logger.Log(e);

		}
	}

	
	private FileWriter writeCorrelation(double[][] correlationValues, Double[] Measurements, String title[],
			FileWriter writer) {
		try {
			double[] unboxed = Stream.of(Measurements).mapToDouble(Double::doubleValue).toArray();
			Map<Integer, Boolean> valuesIndexesNotNaN = new HashMap();
			for (int i = 0; i < correlationValues.length; i++) {
				int nanCounter = 0;
				for (int j = 0; j < correlationValues[i].length; j++) {
					if (Double.toString(correlationValues[i][j]).equals("NaN")) {
						nanCounter += 1;
					}
				}
				for (int j = 0; j < correlationValues[i].length; j++) {
					if (Double.toString(correlationValues[j][i]).equals("NaN")) {
						nanCounter += 1;
					}
				}
				if (nanCounter == correlationValues[i].length * 2 - 2) {
					valuesIndexesNotNaN.put(i, false);
				} else {
					valuesIndexesNotNaN.put(i, true);
				}
			}
			writer.append("Rules,");

			for (int i = 0; i < correlationValues.length; i++) {
				if (valuesIndexesNotNaN.get(i)) {
					writer.append(title[i]);
				}
				if (i < correlationValues.length - 1 && valuesIndexesNotNaN.get(i)) {
					writer.append(",");
				}
			}
			writer.append("\n");
			for (int i = 0; i < correlationValues.length; i++) {
				if (valuesIndexesNotNaN.get(i)) {
					writer.append(title[i] + ",");
				} else {
					continue;
				}
				for (int j = 0; j < correlationValues[i].length; j++) {
					// if (j<i) {
					// writer.append(",");
					// continue;
					// }
					if (i <= j) {
						writer.append(",");
						continue;
					}
					if (valuesIndexesNotNaN.get(j)) {
						if (Double.toString(correlationValues[j][i]).equals("NaN")) {
							writer.append("0");
						} else {
							if(i>0) {
								writer.append(Double.toString(Precision.round(correlationValues[i][j] , 2)));
							}
							else {
								writer.append(Double.toString(Precision.round(correlationValues[i][j] * unboxed[i], 2)));
							}
						}

						if (j < correlationValues[i].length - 1 && valuesIndexesNotNaN.get(i)) {
							writer.append(",");
						}
					}
				}
				writer.append("\n");
			}
		} catch (IOException e) {
		
			Logger.Log(e);

		}
		return writer;

	}

	private void WriteDescriptive() throws IOException {

		TableWriter.append("Measurement/Rule," + TableTitle.replace(",Database Name", "") + "\n");
		ColumnWriter.append("Measurement/Rule," + ColumnTitle.replace(",Database Name", "") + "\n");
		WriteSkewness();
		WriteMean();
		WriteSTD();

	}

	private void WriteSkewness() {
		try {
			Skewness skewness = new Skewness();

			for (int i = 0; i < TableTitleSize; i++) {
				double temp[] = new double[TableMeasurementSize];
				for (int j = 0; j < TableMeasurementSize; j++) {
					temp[j] = TableMeasurements[j][i];
				}

				SkewnessTables[i] = skewness.evaluate(temp);

			}
			for (int i = 0; i < ColumnTitleSize; i++) {
				double temp[] = new double[ColumnMeasurementSize];
				for (int j = 0; j < ColumnMeasurementSize; j++) {
					temp[j] = ColumnMeasurements[j][i];
				}
				SkewnessColumns[i] = skewness.evaluate(temp);

			}
			TableWriter.append("Skewness,");
			for (int i = 0; i < TableTitleSize; i++) {
				if (nan.equals(SkewnessTables[i])) {
					TableWriter.append("0");
				} else {
					TableWriter.append(Double.toString(Precision.round(SkewnessTables[i], 2)));
				}
				AppendTableComma(i);
			}
			TableWriter.append("\n");

			ColumnWriter.append("Skewness,");

			for (int i = 0; i < ColumnTitleSize; i++) {
				if (nan.equals(SkewnessColumns[i])) {
					ColumnWriter.append("0");
				} else {
					ColumnWriter.append(Double.toString(Precision.round(SkewnessColumns[i], 2)));
				}
				AppendColumnComma(i);
			}
			ColumnWriter.append("\n");

		} catch (Exception e) {
		
			Logger.Log(e);
	
		}
	}

	private void AppendColumnComma(int i) {
		if (i < ColumnTitleSize - 1) {
			try {
				ColumnWriter.append(",");
			} catch (IOException e) {
				Logger.Log(e);
			}
		}
	}

	private void AppendTableComma(int i) {
		if (i < TableTitleSize - 1) {
			try {
				TableWriter.append(",");
			} catch (IOException e) {
				Logger.Log(e);
			}
		}
	}

	private void WriteMean() {

		try {
			Median median = new Median();
			ColumnWriter.append("Median,");
			TableWriter.append("Median,");

			for (int i = 0; i < TableTitleSize; i++) {
				double temp[] = new double[TableMeasurementSize];
				for (int j = 0; j < TableMeasurementSize; j++) {
					temp[j] = TableMeasurements[j][i];
				}
				MedianTables[i] = median.evaluate(temp);

			}
			for (int i = 0; i < ColumnTitleSize; i++) {
				double temp[] = new double[ColumnMeasurementSize];
				for (int j = 0; j < ColumnMeasurementSize; j++) {
					temp[j] = ColumnMeasurements[j][i];
				}
				MedianColumns[i] = median.evaluate(temp);
			}

			for (int i = 0; i < TableTitleSize; i++) {
				TableWriter.append(Double.toString(Precision.round(MedianTables[i], 2)));
				AppendTableComma(i);
			}
			TableWriter.append("\n");

			for (int i = 0; i < ColumnTitleSize; i++) {
				ColumnWriter.append(Double.toString(Precision.round(MedianColumns[i], 2)));
				AppendColumnComma(i);
			}
			ColumnWriter.append("\n");

		} catch (Exception e) {
			Logger.Log(e);
		}
	}

	private void WriteSTD() {
		try {
			ColumnWriter.append("Standard Deviation,");
			TableWriter.append("Standard Deviation,");

			StandardDeviation std = new StandardDeviation();

			for (int i = 0; i < TableTitleSize; i++) {
				double temp[] = new double[TableMeasurementSize];
				for (int j = 0; j < TableMeasurementSize; j++) {
					temp[j] = TableMeasurements[j][i];
				}

				STDTables[i] = std.evaluate(temp);
			}
			for (int i = 0; i < ColumnTitleSize; i++) {
				double temp[] = new double[ColumnMeasurementSize];
				for (int j = 0; j < ColumnMeasurementSize; j++) {
					temp[j] = ColumnMeasurements[j][i];
				}

				STDColumns[i] = std.evaluate(temp);
			}

			for (int i = 0; i < TableTitleSize; i++) {
				TableWriter.append(Double.toString(Precision.round(STDTables[i], 2)));
				AppendTableComma(i);
			}
			TableWriter.append("\n");

			for (int i = 0; i < ColumnTitleSize; i++) {
				ColumnWriter.append(Double.toString(Precision.round(STDColumns[i], 2)));
				AppendColumnComma(i);
			}
			ColumnWriter.append("\n");

		} catch (Exception e) {
			Logger.Log(e);
		}
	}

	private void WriteAverage() {
		try {
			Sum sum = new Sum();
			ColumnWriter.append("Average,");
			TableWriter.append("Average,");

			for (int i = 0; i < TableTitleSize; i++) {
				double temp[] = new double[TableMeasurementSize];
				for (int j = 0; j < TableMeasurementSize; j++) {
					temp[j] = TableMeasurements[j][i];
				}
				AverageTables[i] = sum.evaluate(temp) / TableMeasurementSize;
			}
			for (int i = 0; i < ColumnTitleSize; i++) {
				double temp[] = new double[ColumnMeasurementSize];
				for (int j = 0; j < ColumnMeasurementSize; j++) {
					temp[j] = ColumnMeasurements[j][i];
				}
				AverageColumns[i] = sum.evaluate(temp) / ColumnMeasurementSize;
			}

			for (int i = 0; i < TableTitleSize; i++) {
				TableWriter.append(Double.toString(Precision.round(AverageTables[i], 2)));
				AppendTableComma(i);
			}
			TableWriter.append("\n");

			for (int i = 0; i < ColumnTitleSize; i++) {
				ColumnWriter.append(Double.toString(Precision.round(AverageColumns[i], 2)));
				AppendColumnComma(i);
			}
			ColumnWriter.append("\n");

		} catch (Exception e) {
			Logger.Log(e);
		}
	}

	private void WriteMax() {

		try {
			Max max = new Max();
			ColumnWriter.append("Max,");
			TableWriter.append("Max,");

			for (int i = 0; i < TableTitleSize; i++) {
				double temp[] = new double[TableMeasurementSize];
				for (int j = 0; j < TableMeasurementSize; j++) {
					temp[j] = TableMeasurements[j][i];
				}

				MaxTables[i] = max.evaluate(temp);
			}
			for (int i = 0; i < ColumnTitleSize; i++) {
				double temp[] = new double[ColumnMeasurementSize];
				for (int j = 0; j < ColumnMeasurementSize; j++) {
					temp[j] = ColumnMeasurements[j][i];
				}

				MaxColumns[i] = max.evaluate(temp);

			}

			for (int i = 0; i < TableTitleSize; i++) {
				TableWriter.append(Double.toString(Precision.round(MaxTables[i], 2)));
				AppendTableComma(i);
			}
			TableWriter.append("\n");

			for (int i = 0; i < ColumnTitleSize; i++) {
				ColumnWriter.append(Double.toString(Precision.round(MaxColumns[i], 2)));
				AppendColumnComma(i);
			}
			ColumnWriter.append("\n");

		} catch (Exception e) {
			Logger.Log(e);
		}

	}

	private void WriteMin() {

		try {
			Min min = new Min();
			TableWriter.append("Min,");
			ColumnWriter.append("Min,");

			for (int i = 0; i < TableTitleSize; i++) {
				double temp[] = new double[TableMeasurementSize];
				for (int j = 0; j < TableMeasurementSize; j++) {
					temp[j] = TableMeasurements[j][i];
				}
				MinTables[i] = min.evaluate(temp);

			}
			for (int i = 0; i < ColumnTitleSize; i++) {
				double temp[] = new double[ColumnMeasurementSize];
				for (int j = 0; j < ColumnMeasurementSize; j++) {
					temp[j] = ColumnMeasurements[j][i];
				}
				MinColumns[i] = min.evaluate(temp);

			}

			for (int i = 0; i < TableTitleSize; i++) {
				TableWriter.append(Double.toString(Precision.round(MinTables[i], 2)));
				AppendTableComma(i);
			}
			TableWriter.append("\n");

			for (int i = 0; i < ColumnTitleSize; i++) {
				ColumnWriter.append(Double.toString(Precision.round(MinColumns[i], 2)));
				AppendColumnComma(i);
			}
			ColumnWriter.append("\n");

		} catch (Exception ex) {
			System.out.println("WriteMin Exception : " + ex.getMessage());
		}

	}

	private void WriteMode() {
		try {
			Frequency max = new Frequency();

			ColumnWriter.append("Mode,");
			TableWriter.append("Mode,");
			double maxFrequencyValue = 0;

			for (int i = 0; i < TableTitleSize; i++) {

				Map<Double, Integer> frequencyOfValues = new HashMap();
				for (int j = 0; j < TableMeasurementSize; j++) {
					if (frequencyOfValues.containsKey(TableMeasurements[j][i])) {
						frequencyOfValues.replace(TableMeasurements[j][i],
								frequencyOfValues.get(TableMeasurements[j][i]) + 1);
					} else {
						frequencyOfValues.put(TableMeasurements[j][i], (Integer) 0);
					}
				}
				int maxFrequency = 0;
				for (int j = 0; j < TableMeasurementSize; j++) {
					if (frequencyOfValues.getOrDefault(TableMeasurements[j][i], 1) > maxFrequency) {
						maxFrequency = frequencyOfValues.get(TableMeasurements[j][i]);
						maxFrequencyValue = TableMeasurements[j][i];
					}
				}
				ModeTables[i] = maxFrequencyValue;
				// StatUtils.mode(temp, 0, 1)[0];
			}
			for (int i = 0; i < ColumnTitleSize; i++) {

				Map<Double, Integer> frequencyOfValues = new HashMap();
				for (int j = 0; j < ColumnMeasurementSize; j++) {
					if (frequencyOfValues.containsKey(ColumnMeasurements[j][i])) {
						frequencyOfValues.replace(ColumnMeasurements[j][i],
								frequencyOfValues.get(ColumnMeasurements[j][i]) + 1);
					} else {
						frequencyOfValues.put(ColumnMeasurements[j][i], 0);
					}
				}
				int maxFrequency = 0;
				for (int j = 0; j < ColumnMeasurementSize; j++) {
					if (frequencyOfValues.get(ColumnMeasurements[j][i]) > maxFrequency) {
						maxFrequency = frequencyOfValues.get(ColumnMeasurements[j][i]);
						maxFrequencyValue = ColumnMeasurements[j][i];
					}
				}
				ModeColumns[i] = maxFrequencyValue;
			}

			for (int i = 0; i < TableTitleSize; i++) {
				TableWriter.append(Double.toString(Precision.round(ModeTables[i], 2)));
				AppendTableComma(i);
			}
			TableWriter.append("\n");

			for (int i = 0; i < ColumnTitleSize; i++) {
				ColumnWriter.append(Double.toString(Precision.round(ModeColumns[i], 2)));
				AppendColumnComma(i);
			}
			ColumnWriter.append("\n");

		} catch (Exception e) {
			Logger.Log(e);
		}

	}

	private void ReadMeasurements() {
		try {
			ArrayList<Double> totalTables = new ArrayList<Double>();
			ArrayList<Double> totalColumns = new ArrayList<Double>();
			BufferedReader reader = new BufferedReader(new FileReader(TablePath));
			String line;
			int index = 0;
			while ((line = reader.readLine()) != null) {
				if(line.contains("NaN")) {
					continue;
				}
				tableRecords.add(line);
				if (index > 0 && line.split(",").length > 1) {
					try {
						totalTables.add(Double.parseDouble(line.split(",")[0]));
					} catch (Exception ex) {
						System.out.println("Exception in ReadMeasurements (tables)" + ex.getMessage());
					}
				}
				index += 1;

			}
			reader.close();

			reader = new BufferedReader(new FileReader(ColumnPath));
			index = 0;
			while ((line = reader.readLine()) != null) {
				if(line.contains("NaN")) {
					continue;
				}
				columnRecords.add(line);
				if (index > 0 && line.split(",").length > 1) {
					try {
						totalColumns.add(Double.parseDouble(line.split(",")[0]));
					} catch (Exception ex) {
						System.out.println("Exception in ReadMeasurements (columns)"+ex.getMessage());
					}
				}
				index += 1;
			}
			reader.close();
			TotalTables = new Double[totalTables.size()];
			TotalTables = totalTables.toArray(TotalTables);

			TotalColumns = new Double[totalColumns.size()];
			TotalColumns = totalColumns.toArray(TotalColumns);

		} catch (Exception e) {
			Logger.Log(e);

		}
	}
	
	
	
	private void calculateAdherenceToTheOverallStyle() {
		double[] lastVersionTableStatistics = this.TableMeasurements[this.TableMeasurements.length-1];
		double[] lastVersionColumnStatistics = this.ColumnMeasurements[this.ColumnMeasurements.length-1];
		for (int i=7; i < lastVersionTableStatistics.length; i++) { 
			if (i == 20) {//auv
				continue;
			}
			System.out.println(lastVersionTableStatistics[i]);
			if (lastVersionTableStatistics[i] == 1) {
				//this is a completely followed convention
				this.CF_byTables += 1;
				
			}
			else if (lastVersionTableStatistics[i] == 0) {
				//this is a completely ignored convention
				this.NF_byTables += 1;
			}
			else {
				//this is a partially followed convention
				this.PF_byTables += 1;
			}
			
			
		}
		for (int i=7;i < lastVersionColumnStatistics.length; i++) {
			if (i == 20) {
				continue;	
			}
			if (lastVersionColumnStatistics[i] == 1) {
				this.CF_byColumns += 1;
			}
			else if (lastVersionColumnStatistics[i] == 0) {
				this.NF_byColumns += 1;
			}
			else {
				this.PF_byColumns += 1;
			}
		}
		
		calculateOverallTableAdherencePercentageForLastVersion();
		calculateOverallColumnAdherencePercentageForLastVersion();
	
	}
	
	
	private void calculateOverallTableAdherencePercentageForLastVersion () {
		double[] lastVersionTableStatistics = this.TableMeasurements[this.TableMeasurements.length-1];
		int tableRulesMultitude = lastVersionTableStatistics.length - 9; //mhpws den einai 9
		cfPercentageForTables = 100.0 * ((double)this.CF_byTables/tableRulesMultitude); 
		pfPercentageForTables = 100.0 * ((double)this.PF_byTables/tableRulesMultitude);  
		nfPercentageForTables = 100.0 * ((double)this.NF_byTables/tableRulesMultitude);  
		WriteTableAdherenceToTheOverallStyle();
	}
	
	private void calculateOverallColumnAdherencePercentageForLastVersion() {
		double[] lastVersionColumnStatistics = this.ColumnMeasurements[this.ColumnMeasurements.length-1];
		int columnRulesMultitude = lastVersionColumnStatistics.length - 9;
		cfPercentageForColumns = 100.0 * ((double)this.CF_byColumns/columnRulesMultitude);
		pfPercentageForColumns = 100.0 * ((double)this.PF_byColumns/columnRulesMultitude);
		nfPercentageForColumns = 100.0 * ((double)this.NF_byColumns/columnRulesMultitude);
		WriteColumnAdherenceToTheOverallStyle();
	}
	
	private void WriteTableAdherenceToTheOverallStyle() {
		String tablePathSubString1 = TablePath.substring(0,8);
		//String tablePathSubString2 = TablePath.substring(28);
 		//File file = new File("C:\\Users\\giwrg\\Desktop\\Diploma\\Results\\AdherencePercentages\\"+ tablePathSubString.replace(".csv", "adherence_to_the_Overall_Style.csv"));
		//File file = new File(tablePathSubString1 +"\\AdherencePercentages\\" + tablePathSubString2.replace(".csv", "adherence_to_the_Overall_Style.csv"));
		File file = new File(tablePathSubString1 + "\\AdherencePercentages\\tableAdherenceToTheOverallStyle.csv");
		file.getParentFile().mkdirs();
		try {
			if (!file.exists()) {
				file.createNewFile();
				AiWriter = new FileWriter(file);
				AiWriter.append("cfPercentage,");
				AiWriter.append("pfPercentage,");
				AiWriter.append("nfPercentage,");
				AiWriter.append("DatabaseName\n");
				AiWriter.append(Double.toString(cfPercentageForTables)+",");
				AiWriter.append(Double.toString(pfPercentageForTables)+",");
				AiWriter.append(Double.toString(nfPercentageForTables)+",");	
				String secondLine = tableRecords.get(1).replace(",,", ",");
				String[] secondLineRow = secondLine.split(",");
				String schemaTitle = secondLineRow[secondLineRow.length-1];
				AiWriter.append(schemaTitle);
				AiWriter.close();
			}
			else {
				System.out.println("Check the file");
				AiWriter = new FileWriter(file,true);
				AiWriter.append("\n");
				AiWriter.append(Double.toString(cfPercentageForTables)+",");
				AiWriter.append(Double.toString(pfPercentageForTables)+",");
				AiWriter.append(Double.toString(nfPercentageForTables)+",");
				String secondLine = tableRecords.get(1).replace(",,", ",");
				String[] secondLineRow = secondLine.split(",");
				String schemaTitle = secondLineRow[secondLineRow.length-1];
				AiWriter.append(schemaTitle);
				AiWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//TODO
	private void WriteColumnAdherenceToTheOverallStyle(){
		String columnPathSubString1 = ColumnPath.substring(0,8);
		//String columnPathSubString2 = ColumnPath.substring(28);
 		//File file = new File("C:\\Users\\giwrg\\Desktop\\Diploma\\Results\\AdherencePercentages\\"+ tablePathSubString.replace(".csv", "adherence_to_the_Overall_Style.csv"));
		//File file1 = new File(columnPathSubString1 +"\\AdherencePercentages\\" + columnPathSubString2.replace(".csv", "adherence_to_the_Overall_Style.csv"));
		File file1 = new File(columnPathSubString1 + "\\AdherencePercentages\\columnAdherenceToTheOverallStyle.csv");
		file1.getParentFile().mkdirs();
		try {
			if (!file1.exists()) {
				file1.createNewFile();
				AiWriter = new FileWriter(file1);
				AiWriter.append("cfPercentage,");
				AiWriter.append("pfPercentage,");
				AiWriter.append("nfPercentage,");
				AiWriter.append("DatabaseName\n");
				AiWriter.append(Double.toString(cfPercentageForColumns)+",");
				AiWriter.append(Double.toString(pfPercentageForColumns)+",");
				AiWriter.append(Double.toString(nfPercentageForColumns)+",");	
				String secondLine = columnRecords.get(1).replace(",,", ",");
				String[] secondLineRow = secondLine.split(",");
				String schemaTitle = secondLineRow[secondLineRow.length-1];
				AiWriter.append(schemaTitle);
				AiWriter.close();
			}
			else {
				System.out.println("Check the file");
				AiWriter = new FileWriter(file1,true);
				AiWriter.append("\n");
				AiWriter.append(Double.toString(cfPercentageForColumns)+",");
				AiWriter.append(Double.toString(pfPercentageForColumns)+",");
				AiWriter.append(Double.toString(nfPercentageForColumns)+",");
				String secondLine = columnRecords.get(1).replace(",,", ",");
				String[] secondLineRow = secondLine.split(",");
				String schemaTitle = secondLineRow[secondLineRow.length-1];
				AiWriter.append(schemaTitle);
				AiWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void calculateTableAdherenceToEachConvention() {
		setCounter1 += 1;
		double[] lastVersionStatistics = this.TableMeasurements[this.TableMeasurements.length-1];
		int ruleCounter = 7;
		 //to 20 einai to auv prepei na mhn to diabazw
		for (Map.Entry<String, ArrayList<Double>> entry : rulesAndTableAdherence.entrySet()) {
			if (ruleCounter != lastVersionStatistics.length) {
				if (ruleCounter == 20) {
					ruleCounter++;
					continue;
				}
				double adherencePercentageOfRule = 100.0 * lastVersionStatistics[ruleCounter];
				if (adherencePercentageOfRule >= 75.0) {
					entry.getValue().set(0,(entry.getValue().get(0) + 1.0));
				}
				if (adherencePercentageOfRule >= 50.0 && adherencePercentageOfRule < 75.0) {
					entry.getValue().set(1,(entry.getValue().get(1) + 1.0));
				}
				if (adherencePercentageOfRule >= 25.0 && adherencePercentageOfRule < 50.0) {
					entry.getValue().set(2,(entry.getValue().get(2) + 1.0));
				}
				if (adherencePercentageOfRule < 25.0) {
					entry.getValue().set(3,(entry.getValue().get(3) + 1.0));	
				}
				
			}
			ruleCounter++;
		}
		WriteTableAdherenceToEachConvention();
		
	}
	
	private void calculateColumnAdherenceToEachConvention() {
		setCounter2 += 1;
		double[] lastVersionStatistics = this.ColumnMeasurements[this.ColumnMeasurements.length-1];
		int ruleCounter = 7;
		 //to 20 einai to auv prepei na mhn to diabazw
		
		for (Map.Entry<String, ArrayList<Double>> entry : rulesAndColumnAdherence.entrySet()) {
			if (ruleCounter != lastVersionStatistics.length) {
				if (ruleCounter == 20) {
					ruleCounter++;
					continue;
				}
				double adherencePercentageOfRule = 100.0 * lastVersionStatistics[ruleCounter];
				if (adherencePercentageOfRule >= 75.0) {//sthn epomenh epanalipsi th paei na parei to 100 kai oxi to 1 gi auto bgazei ta perierga
					entry.getValue().set(0,(entry.getValue().get(0) + 1.0));//isws thn diairesh na thn bgalw apo dw
				}
				if (adherencePercentageOfRule >= 50.0 && adherencePercentageOfRule < 75.0) {
					entry.getValue().set(1,(entry.getValue().get(1) + 1.0));
				}
				if (adherencePercentageOfRule >= 25.0 && adherencePercentageOfRule < 50.0) {
					entry.getValue().set(2,(entry.getValue().get(2) + 1.0));
				}
				if (adherencePercentageOfRule < 25.0) {
					entry.getValue().set(3,(entry.getValue().get(3) + 1.0));	
				}
				
			}
			ruleCounter++;
		}
		WriteColumnAdherenceToEachConvention();
		
	}
	
	private void WriteTableAdherenceToEachConvention() {
		String tablePathSubString = TablePath.substring(0,8);
		File file2 = new File(tablePathSubString + "\\AdherencePercentages\\tableAdherenceToEachConvention.csv");
		file2.getParentFile().mkdirs();
		try {
			if (!file2.exists()) {
				file2.createNewFile();
				AiWriter = new FileWriter(file2);
				AiWriter.append("Rule Name,");
				AiWriter.append("AH,");
				AiWriter.append("AMH,");
				AiWriter.append("AML,");
				AiWriter.append("AL\n");
				for (Map.Entry<String, ArrayList<Double>> entry : rulesAndTableAdherence.entrySet()) {
					AiWriter.append(entry.getKey()+",");
					AiWriter.append(Double.toString((entry.getValue().get(0)/setCounter1)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(1)/setCounter1)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(2)/setCounter1)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(3)/setCounter1)*100.0)+",");
					AiWriter.append("\n");
					//tha balw kai to database name
				}
				AiWriter.close();
			}
			else {
				System.out.println("Check the file");
				AiWriter = new FileWriter(file2);
				AiWriter.append("Rule Name,");
				AiWriter.append("AH,");
				AiWriter.append("AMH,");
				AiWriter.append("AML,");
				AiWriter.append("AL\n");
				for (Map.Entry<String, ArrayList<Double>> entry : rulesAndTableAdherence.entrySet()) {
					AiWriter.append(entry.getKey()+",");
					AiWriter.append(Double.toString((entry.getValue().get(0)/setCounter1)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(1)/setCounter1)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(2)/setCounter1)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(3)/setCounter1)*100.0)+",");
					AiWriter.append("\n");
					//tha balw kai to database name
				}
				AiWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void WriteColumnAdherenceToEachConvention() {
		String tablePathSubString = TablePath.substring(0,8);
		File file3 = new File(tablePathSubString + "\\AdherencePercentages\\columnAdherenceToEachConvention.csv");
		file3.getParentFile().mkdirs();
		try {
			if (!file3.exists()) {
				file3.createNewFile();
				AiWriter = new FileWriter(file3);
				AiWriter.append("Rule Name,");
				AiWriter.append("AH,");
				AiWriter.append("AMH,");
				AiWriter.append("AML,");
				AiWriter.append("AL\n");
				for (Map.Entry<String, ArrayList<Double>> entry : rulesAndColumnAdherence.entrySet()) {
					AiWriter.append(entry.getKey()+",");
					AiWriter.append(Double.toString((entry.getValue().get(0)/setCounter2)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(1)/setCounter2)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(2)/setCounter2)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(3)/setCounter2)*100.0)+",");
					AiWriter.append("\n");
					//tha balw kai to database name
				}
				AiWriter.close();
			}
			else {
				System.out.println("Check the file");
				AiWriter = new FileWriter(file3);
				AiWriter.append("Rule Name,");
				AiWriter.append("AH,");
				AiWriter.append("AMH,");
				AiWriter.append("AML,");
				AiWriter.append("AL\n");
				for (Map.Entry<String, ArrayList<Double>> entry : rulesAndColumnAdherence.entrySet()) {
					AiWriter.append(entry.getKey()+",");
					AiWriter.append(Double.toString((entry.getValue().get(0)/setCounter2)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(1)/setCounter2)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(2)/setCounter2)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(3)/setCounter2)*100.0)+",");
					AiWriter.append("\n");
					//tha balw kai to database name
				}
				AiWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void calculateTableAdherenceEvolution() {
		setCounter3 += 1;
		double[] lastVersionStatistics = this.TableMeasurements[this.TableMeasurements.length-1];
		double[] firstVersionStatistics = this.TableMeasurements[0];
		int ruleCounter = 7;
		
		for (Map.Entry<String, ArrayList<Double>> entry : tableAdherenceEvolution.entrySet()) {
			if (ruleCounter != lastVersionStatistics.length) {
				if (ruleCounter == 20) {
					ruleCounter++;
					continue;
				}
				double adherenceEvolutionOfRule = 100.0 * (lastVersionStatistics[ruleCounter]-firstVersionStatistics[ruleCounter]);
				if (adherenceEvolutionOfRule > 0.0) {
					entry.getValue().set(0,(entry.getValue().get(0) + 1.0));
				}
				if (adherenceEvolutionOfRule == 0.0) {
					entry.getValue().set(1,(entry.getValue().get(1) + 1.0));
				}
				if (adherenceEvolutionOfRule < 0.0 ) {
					entry.getValue().set(2,(entry.getValue().get(2) + 1.0));
				}	
			}
			ruleCounter++;
		}
		WriteTableEvolutionAdherence();
	}
	
	private void calculateColumnAdherenceEvolution() {
		setCounter4 += 1;
		double[] lastVersionStatistics = this.ColumnMeasurements[this.ColumnMeasurements.length-1];
		double[] firstVersionStatistics = this.ColumnMeasurements[0];
		int ruleCounter = 7;
		
		for (Map.Entry<String, ArrayList<Double>> entry : columnAdherenceEvolution.entrySet()) {
			if (ruleCounter != lastVersionStatistics.length) {
				if (ruleCounter == 20) {
					ruleCounter++;
					continue;
				}
				double adherenceEvolutionOfRule = 100.0 * (lastVersionStatistics[ruleCounter]-firstVersionStatistics[ruleCounter]);
				if (adherenceEvolutionOfRule > 0.0) {
					entry.getValue().set(0,(entry.getValue().get(0) + 1.0));
				}
				if (adherenceEvolutionOfRule == 0.0) {
					entry.getValue().set(1,(entry.getValue().get(1) + 1.0));
				}
				if (adherenceEvolutionOfRule < 0.0 ) {
					entry.getValue().set(2,(entry.getValue().get(2) + 1.0));
				}	
			}
			ruleCounter++;
		}
		WriteColumnEvolutionAdherence();
	}

	
	private void WriteTableEvolutionAdherence() {
		String tablePathSubString = TablePath.substring(0,8);
		File file3 = new File(tablePathSubString + "\\AdherencePercentages\\tableEvolutionAdherence.csv");
		file3.getParentFile().mkdirs();
		try {
			if (!file3.exists()) {
				file3.createNewFile();
				AiWriter = new FileWriter(file3);
				AiWriter.append("Rule Name,");
				AiWriter.append("AI,");
				AiWriter.append("AS,");
				AiWriter.append("AD\n");
				for (Map.Entry<String, ArrayList<Double>> entry : tableAdherenceEvolution.entrySet()) {
					AiWriter.append(entry.getKey()+",");
					AiWriter.append(Double.toString((entry.getValue().get(0)/setCounter3)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(1)/setCounter3)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(2)/setCounter3)*100.0)+",");
					AiWriter.append("\n");
					//tha balw kai to database name
				}
				AiWriter.close();
			}
			else {
				System.out.println("Check the file");
				AiWriter = new FileWriter(file3);
				AiWriter.append("Rule Name,");
				AiWriter.append("AI,");
				AiWriter.append("AS,");
				AiWriter.append("AD\n");
				for (Map.Entry<String, ArrayList<Double>> entry : tableAdherenceEvolution.entrySet()) {
					AiWriter.append(entry.getKey()+",");
					AiWriter.append(Double.toString((entry.getValue().get(0)/setCounter3)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(1)/setCounter3)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(2)/setCounter3)*100.0)+",");
					AiWriter.append("\n");
					//tha balw kai to database name
				}
				AiWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void WriteColumnEvolutionAdherence() {
		String columnPathSubString = TablePath.substring(0,8);
		File file4 = new File(columnPathSubString + "\\AdherencePercentages\\columnEvolutionAdherence.csv");
		file4.getParentFile().mkdirs();
		try {
			if (!file4.exists()) {
				file4.createNewFile();
				AiWriter = new FileWriter(file4);
				AiWriter.append("Rule Name,");
				AiWriter.append("AI,");
				AiWriter.append("AS,");
				AiWriter.append("AD\n");
				for (Map.Entry<String, ArrayList<Double>> entry : columnAdherenceEvolution.entrySet()) {
					AiWriter.append(entry.getKey()+",");
					AiWriter.append(Double.toString((entry.getValue().get(0)/setCounter4)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(1)/setCounter4)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(2)/setCounter4)*100.0)+",");
					AiWriter.append("\n");
					//tha balw kai to database name
				}
				AiWriter.close();
			}
			else {
				System.out.println("Check the file");
				AiWriter = new FileWriter(file4);
				AiWriter.append("Rule Name,");
				AiWriter.append("AI,");
				AiWriter.append("AS,");
				AiWriter.append("AD\n");
				for (Map.Entry<String, ArrayList<Double>> entry : columnAdherenceEvolution.entrySet()) {
					AiWriter.append(entry.getKey()+",");
					AiWriter.append(Double.toString((entry.getValue().get(0)/setCounter4)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(1)/setCounter4)*100.0)+",");
					AiWriter.append(Double.toString((entry.getValue().get(2)/setCounter4)*100.0)+",");
					AiWriter.append("\n");
					//tha balw kai to database name
				}
				AiWriter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
