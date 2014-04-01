package com.microsoft.hadoop.smallfilesmerge;

import java.io.*;
import java.util.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class CombineDirectoryInputFormat extends InputFormat<IntWritable, Text> {
	@Override
	public RecordReader<IntWritable, Text> createRecordReader(
			InputSplit split,
			TaskAttemptContext context)
					throws IOException, InterruptedException {
		return new DirectoryFileNameRecordReader();
	}

	@Override
	public List<InputSplit> getSplits(JobContext context)
			throws IOException,
			InterruptedException {
		Configuration job = context.getConfiguration();
		ArrayList<InputSplit> ret = new ArrayList<InputSplit>();
		Path[] inputPaths = CombineDirectoryConfiguration.getInputDirectoryPath(job);
		int numNameHashSplits = CombineDirectoryConfiguration.getNumNameHashSplits(job);
		for (Path inputPath : inputPaths) {
			FileStatus[] list = inputPath.getFileSystem(job).listStatus(inputPath);
			for (FileStatus current : list) {
				if (current.isDir()) {
					for (int i = 0; i < numNameHashSplits; i++) {
						ret.add(new CombineDirectoryInputSplit(current.getPath().toString(), i));
					}
				}
			}
		}
		return ret;
	}
}