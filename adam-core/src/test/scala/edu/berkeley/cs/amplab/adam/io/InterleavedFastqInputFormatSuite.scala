/*
 * Copyright (c) 2013. Regents of the University of California
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.berkeley.cs.amplab.adam.rdd

import edu.berkeley.cs.amplab.adam.io._;
import edu.berkeley.cs.amplab.adam.util.SparkFunSuite
import edu.berkeley.cs.amplab.adam.rdd.AdamContext._
import org.apache.spark.rdd.RDD
import edu.berkeley.cs.amplab.adam.avro.ADAMRecord
import parquet.filter.UnboundRecordFilter
import org.scalatest.exceptions.TestFailedException
import org.apache.hadoop.io.Text;


class InterleavedFastqInputFormatSuite extends SparkFunSuite {
	(1 to 4) foreach { testNumber =>
		val inputName = "interleaved_fastq_sample%d.fq".format(testNumber);
		val expectedOutputName = inputName + ".output";
		val expectedOutputPath = ClassLoader.getSystemClassLoader.getResource(expectedOutputName).getFile;
		val expectedOutputData = scala.io.Source.fromFile(expectedOutputPath).mkString;

		sparkTest("interleaved FASTQ hadoop reader: %s->%s".format(inputName, expectedOutputName)) {
			def ifq_reader: RDD[(Void, Text)] = {
				val path = ClassLoader.getSystemClassLoader.getResource(inputName).getFile
				sc.newAPIHadoopFile(path,
						    classOf[InterleavedFastqInputFormat],
						    classOf[Void],
						    classOf[Text])
			}

			val ifq_reads = ifq_reader.collect()

			val testOutput = new StringBuilder();

			ifq_reads.foreach(pair => {
					testOutput.append(">>>interleaved fastq record start>>>\n");
					testOutput.append(pair._2);
					testOutput.append("<<<interleaved fastq record end<<<\n");
				});

			assert(testOutput.toString() == expectedOutputData);
			// System.out.println("%s result:\n".format(inputName) + testOutput.toString());
		}
	}
}

