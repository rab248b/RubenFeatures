import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SketchRec {
	List<String> fileList = new ArrayList<String>();

	/**
	 * 
	 * @param args The location of data folder.
	 */
	public static void main(String[] args) {

		SketchRec sketchRec = new SketchRec();
		PrintWriter pw = null;
		String outputFile = null;
		/** For creating sample or original result files in the workspace. **/
//		outputFile = "result\\"+"sample_result.csv";
//		outputFile = "result\\"+"result.csv";
		/** For creating result file using runnable jar. */
		outputFile = args[0]+"\\result.csv";
		try {
//			pw = new PrintWriter(new File("test_sample.csv"));
			pw = new PrintWriter(new File(outputFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("File cannot be created.");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Shape");
		sb.append(',');
		for (int i = 1; i < 14; i++) {
			sb.append("Feature" + i);
			sb.append(',');
		}
		sketchRec.walk(args[0]);
		String outputContext = "";
		outputContext = sb.toString();
		Iterator<String> fileIterator = sketchRec.fileList.iterator();
		while (fileIterator.hasNext()) {

			outputContext = outputContext + sketchRec.featureCalculate(new File(fileIterator.next()));
		}
		pw.write(outputContext);
		pw.close();
		System.out.println("done!");
	}

	public void walk(String path) {
		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;
		for (File f : list) {
			if (f.isDirectory()) {
				walk(f.getAbsolutePath());
			} else {
//				System.out.println("File:" + f.getAbsoluteFile());
				if (f.getAbsoluteFile().toString().endsWith(".json")) {
					fileList.add(f.getAbsolutePath().toString());
				}
			}
		}
	}

	String featureCalculate(File file) {
		StringBuilder sb = new StringBuilder();
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
			obj = parser.parse(new FileReader(file));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray pointList = (JSONArray) jsonObject.get("points");
		JSONObject row = null;
		String time;
		HashMap<String, Coordinate> map = new HashMap<>();
		Long x, y;
		for (int i = 0; i < pointList.size(); i++) {
			row = (JSONObject) pointList.get(i);
			time = (String) row.get("time");
			x = (Long) row.get("x");
			y = (Long) row.get("y");
			if (!map.containsKey(time)) {
				map.put(time, new Coordinate(x, y));
			}
		}
		Iterator<String> timeIterator = map.keySet().iterator();
		String timeKey;
		BigInteger[] keyArray = new BigInteger[map.size()];
		List<Long> xArray = new ArrayList<>();
		List<Long> yArray = new ArrayList<>();
		List<Long> deltaXArray = new ArrayList<Long>();
		List<Long> deltaYArray = new ArrayList<Long>();
		List<Long> deltaTimeArray = new ArrayList<Long>();
		int k = 0;
		while (timeIterator.hasNext()) {
			timeKey = timeIterator.next();
			keyArray[k] = new BigInteger(timeKey);
			k++;
		}
		Arrays.sort(keyArray);
		for (int i = 0; i <= keyArray.length - 1; i++) {
			xArray.add(map.get(keyArray[i].toString()).x);
			yArray.add(map.get(keyArray[i].toString()).y);
		}

		BigInteger t1, t2, t3;
		Long x1, x2, y1, y2;
		for (int i = 1; i <= keyArray.length - 1; i++) {
			t1 = keyArray[i - 1];
			t2 = keyArray[i];
			t3 = t2.subtract(t1);
			x2 = xArray.get(i - 1);
			x1 = xArray.get(i);
			y2 = yArray.get(i - 1);
			y1 = yArray.get(i);
			if (x2.equals(x1) && y2.equals(y1)) {
			} else {
				deltaXArray.add(x1 - x2);
				deltaYArray.add(y1 - y2);
				deltaTimeArray.add(t3.longValue());
			}
		}

		Double f1 = implementFeature1(map, keyArray);
		Double f2 = implementFeature2(map, keyArray);
		Double f3 = implementFeature3(xArray, yArray);
		Double f4 = implementFeature4(xArray, yArray);
		Double f5 = implementFeature5(map, keyArray);
		Double f6 = implementFeature6(map, keyArray);
		Double f7 = implementFeature7(map, keyArray);
		Double f8 = implementFeature8(deltaXArray, deltaYArray);
		Double f9 = implementFeature9(deltaXArray, deltaYArray);
		Double f10 = implementFeature10(deltaXArray, deltaYArray);
		Double f11 = implementFeature11(deltaXArray, deltaYArray);
		Double f12 = implementFeature12(deltaXArray, deltaYArray, deltaTimeArray);
		Double f13 = implementFeature13(keyArray);
		String shapeName =file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf("\\"));
		sb.append("\n");
//		sb.append(file.getAbsolutePath().substring(0,file.getAbsolutePath().lastIndexOf("\\") + 1));
		sb.append(shapeName.substring(shapeName.lastIndexOf("\\")+1));
//		System.out.println(shapeName.substring(shapeName.lastIndexOf("\\")+1));
		sb.append(',');
		sb.append(f1);
		sb.append(',');
		sb.append(f2);
		sb.append(',');
		sb.append(f3);
		sb.append(',');
		sb.append(f4);
		sb.append(',');
		sb.append(f5);
		sb.append(',');
		sb.append(f6);
		sb.append(',');
		sb.append(f7);
		sb.append(',');
		sb.append(f8);
		sb.append(',');
		sb.append(f9);
		sb.append(',');
		sb.append(f10);
		sb.append(',');
		sb.append(f11);
		sb.append(',');
		sb.append(f12);
		sb.append(',');
		sb.append(f13);
		// System.out.println(sb.toString());
		return sb.toString();

	}

	private static class Coordinate {
		long x;
		long y;

		Coordinate(long x1, long y1) {
			x = x1;
			y = y1;
		}

	}

	double implementFeature1(HashMap<String, Coordinate> map, BigInteger[] keyArray) {
		Coordinate coordinate1 = map.get(keyArray[0].toString());
		Coordinate coordinate2 = map.get(keyArray[2].toString());
		Long diffx = coordinate2.x - coordinate1.x;
		Long diffy = coordinate2.y - coordinate1.y;
		double f1 = (diffx) / Math.sqrt((Math.pow(diffx, 2) + Math.pow(diffy, 2)));
		return f1;

	}

	double implementFeature2(HashMap<String, Coordinate> map, BigInteger[] keyArray) {
		Coordinate coordinate1 = map.get(keyArray[0].toString());
		Coordinate coordinate2 = map.get(keyArray[2].toString());
		Long diffx = coordinate2.x - coordinate1.x;
		Long diffy = coordinate2.y - coordinate1.y;
		double f2 = (diffy) / Math.sqrt((Math.pow(diffx, 2) + Math.pow(diffy, 2)));
		return f2;

	}

	double implementFeature3(List<Long> xArray, List<Long> yArray) {
		Collections.sort(xArray);
		Collections.sort(yArray);
		double xsqr = Math.pow((xArray.get(xArray.size() - 1) - xArray.get(0)), 2);
		double ysqr = Math.pow((yArray.get(yArray.size() - 1) - yArray.get(0)), 2);
		double f3 = Math.sqrt((xsqr + ysqr));
		return f3;

	}

	double implementFeature4(List<Long> xArray, List<Long> yArray) {
		Collections.sort(xArray);
		Collections.sort(yArray);
		double num = yArray.get(yArray.size() - 1) - yArray.get(0);
		double den = xArray.get(xArray.size() - 1) - xArray.get(0);
		double f3 = Math.atan(num / den);
		return f3;

	}

	double implementFeature5(HashMap<String, Coordinate> map, BigInteger[] keyArray) {
		Coordinate coordinate1 = map.get(keyArray[0].toString());
		Coordinate coordinate2 = map.get(keyArray[keyArray.length - 1].toString());
		Long diffx = coordinate2.x - coordinate1.x;
		Long diffy = coordinate2.y - coordinate1.y;
		double f5 = Math.sqrt((Math.pow(diffx, 2) + Math.pow(diffy, 2)));
		return f5;

	}

	double implementFeature6(HashMap<String, Coordinate> map, BigInteger[] keyArray) {
		Coordinate coordinate1 = map.get(keyArray[0].toString());
		Coordinate coordinate2 = map.get(keyArray[keyArray.length - 1].toString());
		Long diffx = coordinate2.x - coordinate1.x;
		Long diffy = coordinate2.y - coordinate1.y;
		double f5 = Math.sqrt((Math.pow(diffx, 2) + Math.pow(diffy, 2)));
		double f6 = diffx / f5;
		return f6;

	}

	double implementFeature7(HashMap<String, Coordinate> map, BigInteger[] keyArray) {
		Coordinate coordinate1 = map.get(keyArray[0].toString());
		Coordinate coordinate2 = map.get(keyArray[keyArray.length - 1].toString());
		Long diffx = coordinate2.x - coordinate1.x;
		Long diffy = coordinate2.y - coordinate1.y;
		double f5 = Math.sqrt((Math.pow(diffx, 2) + Math.pow(diffy, 2)));
		double f7 = diffy / f5;
		return f7;

	}

	double implementFeature8(List<Long> deltaXArray, List<Long> deltaYArray) {
		double f8 = 0;
		for (int i = 0; i < deltaXArray.size(); i++) {
			// xsqr = Math.pow(deltaXArray.get(i), 2);
			// ysqr = Math.pow((double)deltaYArray.get(i), 2);
			f8 = f8 + Math.sqrt((Math.pow(deltaXArray.get(i), 2) + Math.pow(deltaYArray.get(i), 2)));
		}
		return f8;

	}

	double implementFeature9(List<Long> deltaXArray, List<Long> deltaYArray) {
		double num, den, f9 = 0;
		for (int i = 1; i < deltaXArray.size(); i++) {
			num = deltaYArray.get(i - 1) * deltaXArray.get(i) - deltaYArray.get(i) * deltaXArray.get(i - 1);
			den = deltaXArray.get(i - 1) * deltaXArray.get(i) + deltaYArray.get(i) * deltaYArray.get(i - 1);
			f9 = f9 + Math.atan(num / den);
		}
		return f9;

	}

	double implementFeature10(List<Long> deltaXArray, List<Long> deltaYArray) {
		double f10 = 0, num, den, result;
		for (int i = 1; i < deltaXArray.size(); i++) {
			num = deltaYArray.get(i - 1) * deltaXArray.get(i) - deltaYArray.get(i) * deltaXArray.get(i - 1);
			den = deltaXArray.get(i - 1) * deltaXArray.get(i) + deltaYArray.get(i) * deltaYArray.get(i - 1);
			result = Math.atan(num / den);
			f10 = f10 + Math.abs(result);
		}
		return f10;

	}

	double implementFeature11(List<Long> deltaXArray, List<Long> deltaYArray) {
		double f11 = 0, num, den, result;
		for (int i = 1; i < deltaXArray.size(); i++) {
			num = deltaYArray.get(i - 1) * deltaXArray.get(i) - deltaYArray.get(i) * deltaXArray.get(i - 1);
			den = deltaXArray.get(i - 1) * deltaXArray.get(i) + deltaYArray.get(i) * deltaYArray.get(i - 1);
			result = Math.atan(num / den);
			f11 = f11 + Math.pow(result, 2);
		}
		return f11;

	}

	double implementFeature12(List<Long> deltaXArray, List<Long> deltaYArray, List<Long> deltaTimeArray) {
		double f12 = 0, num, den;
		List<Double> vrms = new ArrayList<>();
		for (int i = 0; i < deltaXArray.size(); i++) {
			num = Math.pow(deltaXArray.get(i), 2) + Math.pow(deltaYArray.get(i), 2);
			den = Math.pow(deltaTimeArray.get(i), 2);
			vrms.add(num / den);
		}
		Collections.sort(vrms);
		f12 = vrms.get(vrms.size() - 1);
		return f12;

	}

	double implementFeature13(BigInteger[] keyArray) {
		double f13 = keyArray[keyArray.length - 1].subtract(keyArray[0]).doubleValue();
		return f13;

	}
}