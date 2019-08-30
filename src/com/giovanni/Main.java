package com.giovanni;


import weka.core.Instances;
import weka.core.converters.CSVSaver;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    private static final String OUT_FOLDER = "discr";

    public static void main(String[] args) {
        try {
            List<Path> files = getFilesFromFolder(Paths.get(args[0]));

            for (Path file : files) {
                Instances train = new DataSource(file.toAbsolutePath().toString()).getDataSet();
                System.out.println("processing: " + file.getFileName());

                Discretize discFilter = new Discretize();
                discFilter.setInputFormat(train);
                discFilter.setOptions(new String[] {
                        "-M", "-1.0",
                        "-B", "3", // num. of bins
                        "-Y",
                        "-R", "first-last",
                        "-unset-class-temporarily"
                });

                Instances discTrain = Filter.useFilter(train, discFilter);

                File outFile = Paths.get(Main.OUT_FOLDER,"d_" + file.getFileName()).toFile();
                writeCsv(outFile, discTrain);

                System.out.println("done");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static List<Path> getFilesFromFolder(Path parentFolder) throws IOException {
        List<Path> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(parentFolder)) {
            paths.forEach(filePath -> {
                String fileName = filePath.getFileName().toString();
                if (fileName.startsWith("user_") && fileName.endsWith(".csv")) {
                    files.add(filePath.toAbsolutePath());
                }
            });
        }

        return files;
    }

    private static void writeCsv(File outFile, Instances dataInstances) throws IOException {
        CSVSaver csvSaver = new CSVSaver();
        csvSaver.setInstances(dataInstances);
        csvSaver.setFile(outFile);
        csvSaver.writeBatch();
    }
}
