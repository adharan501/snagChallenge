import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadCSVFile {
	private static int countOfRowsIngested = 0;
	private static int countOfRowsNotIngested = 0;

	public static Map<String, List<String[]>> getQueryMap(File file, String qColumnName) throws IOException {
		String line = null;
		boolean firstRow = true;
		int numberOfColumns = 0;
		Map<String, List<String[]>> qColumnMap = new HashMap<String, List<String[]>>();

		BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
		int qColumnRowNumber = -1;
		while ((line = bufferedReader.readLine()) != null) {
			line = line.replace("\"", "");
			String[] row = line.split(",");
			if (firstRow) {
				numberOfColumns = row.length;
				for (int i = 0; i < row.length; i++) {
					if (row[i].equals(qColumnName)) {
						qColumnRowNumber = i;
					}
					System.out.printf("%-20s", row[i]);
				}
				System.out.println();
				firstRow = false;
			} else {
				printRow(row);
				if (qColumnRowNumber == -1) {
					throw new RuntimeException("Column not found");
				}
				if(row.length != numberOfColumns){
					countOfRowsNotIngested++;
				}else{
					updateQColumnMap(qColumnMap, row, qColumnRowNumber);
					countOfRowsIngested++;	
				}
			}
		}

		bufferedReader.close();
		return qColumnMap;
	}

	private static void printRow(String[] row) {
		for (int i = 0; i < row.length; i++) {
			System.out.printf("%-20s", row[i]);
		}
		System.out.println();
	}

	private static void updateQColumnMap(Map<String, List<String[]>> qColumnMap, String[] row, int qColumnRowNumber) {
		String qColumn = row[qColumnRowNumber];
		if (qColumnMap.containsKey(qColumn)) {
			qColumnMap.get(qColumn).add(row);
		} else {
			List<String[]> rowList = new ArrayList<String[]>();
			rowList.add(row);
			qColumnMap.put(qColumn, rowList);
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			File file = new File("parseCSV.csv");
			Map<String, List<String[]>> qColumnMap = getQueryMap(file, "region");
			System.out.println(qColumnMap.get("AU"));
		} catch (IOException ex) {
			// read input
			ex.printStackTrace();
		} catch (RuntimeException ex) {
			// TODO: handle exception
			ex.printStackTrace();

		}

		System.out.println("\ncount of rows ingested: " + countOfRowsIngested);
		System.out.println("count of rows not ingested:" + countOfRowsNotIngested);
	}
}