package kr.ac.kookmin.cs.svm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;

public class FileManager {
	// 기본 폴더 위치 & 파일명
	public static final String SVM_FILE_FOLDER = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/MotionDiary";
	
	
	// 멀티 클래스용
	public static final String SVM_TRAIN_MODEL_FILE = SVM_FILE_FOLDER
			+ "/svm_train_model.txt";
	public static final String RESTORE_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/restore_motion_data.txt";
	public static final String MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/motion_data.txt";
	public static final String SCALED_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/scaled_motion_data.txt";
	
	
	
	// 원클래스용
	public static final String STANDING_SVM_TRAIN_MODEL_FILE = SVM_FILE_FOLDER
			+ "/standing_svm_train_model.txt";
	public static final String WALKING_SVM_TRAIN_MODEL_FILE = SVM_FILE_FOLDER
			+ "/walking_svm_train_model.txt";
	public static final String RUNNING_SVM_TRAIN_MODEL_FILE = SVM_FILE_FOLDER
			+ "/running_svm_train_model.txt";
	
	public static final String STANDING_RESTORE_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/standing_restore_motion_data.txt";
	public static final String WALKING_RESTORE_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/walking_restore_motion_data.txt";
	public static final String RUNNING_RESTORE_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/running_restore_motion_data.txt";
	
	public static final String STANDING_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/stading_motion_data.txt";
	public static final String WALKING_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/walking_motion_data.txt";
	public static final String RUNNING_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/running_motion_data.txt";
	
	public static final String STANDING_SCALED_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/standing_scaled_motion_data.txt";
	public static final String WALKING_SCALED_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/walking_scaled_motion_data.txt";
	public static final String RUNNING_SCALED_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/running_scaled_motion_data.txt";
	
	public static final String CLASSIFICATION_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/classify_motion_data.txt";
	public static final String SCALED_CLASSIFICATION_MOTION_DATA_FILE = SVM_FILE_FOLDER
			+ "/scaled_classification_motion_data.txt";

	// 학습 Input 파일 생성
	public void makeInputFile(String inputFileName,
			ArrayList<LearningData> data, String label) throws IOException {
		// 초기 파일 생성
		initFile(inputFileName);

		BufferedWriter out = new BufferedWriter(new FileWriter(inputFileName,
				true));

		int size = data.size();
		LearningData learningData;
		for (int i = 0; i < size; i++) {

			learningData = data.get(i);
			// 파일 작성
			out.write(label);

			out.write(" 1:" + learningData.Average);
			out.write(" 2:" + learningData.AverageDeviation);
			out.write(" 3:" + learningData.Rms);
			out.write(" 4:" + learningData.StandardDeviation);
			out.write(" 5:" + learningData.Median);
			out.write(" 6:" + learningData.MaxMin);
			// out.write(" 12:" + data.get(i).Sum);
			// out.write(" 13:" + data.get(i).Variance);
			out.newLine();
		}

		out.close();

	}

	public void makeInputFile(String inputFileName, LearningData data)
			throws IOException {
		// 초기 파일 생성
		initFile(inputFileName);

		BufferedWriter out = new BufferedWriter(new FileWriter(inputFileName));
		out.write("1");
		// 파일 작성
		out.write(" 1:" + data.Average);
		out.write(" 2:" + data.AverageDeviation);
		out.write(" 3:" + data.Rms);
		out.write(" 4:" + data.StandardDeviation);
		out.write(" 5:" + data.Median);
		out.write(" 6:" + data.MaxMin);

		out.newLine();

		out.close();

	}

	// 초기 파일 생성
	public static void initFile(String fileName) throws IOException {
		File file = new File(fileName);
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
	}
}
