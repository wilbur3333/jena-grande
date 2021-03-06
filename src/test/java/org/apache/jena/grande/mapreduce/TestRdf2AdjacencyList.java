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

package org.apache.jena.grande.mapreduce;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.hadoop.util.ToolRunner;
import org.apache.jena.grande.Constants;
import org.junit.Test;
import org.openjena.atlas.io.IO;
import org.openjena.riot.Lang;
import org.openjena.riot.RiotLoader;

import cmd.rdf2adjacencylist;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class TestRdf2AdjacencyList {

	private static String output = "target/output" ;
	
    @Test public void test() throws Exception {
        String input = "src/test/resources/data2.nt" ;
        String[] args = new String[] {
                "-D", Constants.OPTION_OVERWRITE_OUTPUT + "=true", 
        		"-D", Constants.OPTION_RUN_LOCAL + "=true",
                input, 
                output
        };
        assertEquals ( 0, ToolRunner.run(new rdf2adjacencylist(), args) );

        FileManager fm = FileManager.get();
        Model m1 = fm.loadModel(input);
        Model m2 = null;
        String outputfile = output + "/part-r-00000";
        if ( Rdf2AdjacencyListReducer.useDefaultPrefixes ) {
        	StringBuilder sb = new StringBuilder();
        	for ( String prefix : Constants.defaultPrefixMap.getMapping().keySet() ) {
            	sb.append("@prefix ");
            	sb.append(prefix);
            	sb.append(": <");
            	sb.append(Constants.defaultPrefixMap.getMapping().get(prefix));
            	sb.append("> .\n");
        	}
        	sb.append (IO.readWholeFileAsUTF8(outputfile));
            Graph graph = RiotLoader.graphFromString(sb.toString(), Lang.TURTLE, "");
            m2 = ModelFactory.createModelForGraph(graph);
        } else {
        	m2 = ModelFactory.createDefaultModel();
            fm.readModel(m2, outputfile, "TURTLE");
        }
        
        m1.write(System.out, "TURTLE");
        
        assertTrue(m1.isIsomorphicWith(m2));
        
    }
	
}
