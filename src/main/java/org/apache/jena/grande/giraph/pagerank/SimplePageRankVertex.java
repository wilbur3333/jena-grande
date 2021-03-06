/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jena.grande.giraph.pagerank;

import org.apache.giraph.graph.EdgeListVertex;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimplePageRankVertex extends EdgeListVertex<Text, DoubleWritable, NullWritable, DoubleWritable> {

	private static final Logger log = LoggerFactory.getLogger(SimplePageRankVertex.class); 
	public static final int NUM_ITERATIONS = 30;

	@Override
	public void compute(Iterable<DoubleWritable> msgIterator) {
		log.debug("{}#{} - compute(...) vertexValue={}", new Object[] { getId(), getSuperstep(), getValue() });

		if (getSuperstep() >= 1) {
			double sum = 0;
			for ( DoubleWritable msg : msgIterator ) {
				sum += msg.get();
			}
			DoubleWritable vertexValue = new DoubleWritable( (0.15f / getTotalNumVertices()) + 0.85f * sum );
			setValue(vertexValue);
		}

		if (getSuperstep() < NUM_ITERATIONS) {
			long edges = getNumEdges();
			sendMessageToAllEdges(new DoubleWritable(getValue().get() / edges));
		} else {
			voteToHalt();
		}
	}

}
