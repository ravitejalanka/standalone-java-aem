package com.sapient.DiffChecker;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.JcrUtils;

public class DiffClass {
	private HashMap<String, TreeSet<String>> hMap = new HashMap<String, TreeSet<String>>();
	StringBuffer sb = null;
	String key = null, value = null;

	public static void main(String[] args) throws Exception {
		try {
			DiffClass checker = new DiffClass();
			Repository repository = JcrUtils.getRepository("http://199.167.181.54/crx/server");
			SimpleCredentials cred = new SimpleCredentials("app_user", "Th3b!#p0bC@r".toCharArray());
			Session session = repository.login(cred);
			Node node = session.getNode("/content/lordabbett/en/global/biographies/thomas-ohalloran");
			checker.getPathRecursivlyWithoutRedesign(node);
			checker.writeDatatoCsv();
			System.out.println(checker.hMap);
		} catch (RepositoryException e) {
			System.out.println("Repository Exception Occured:::::" + e.getMessage());
		}
	}

	private void getPathRecursivlyWithoutRedesign(Node resNode) {
		try {
			NodeIterator nodeItr = resNode.getNodes();
			while (nodeItr.hasNext()) {
				Node child = nodeItr.nextNode();
				if (child.hasProperty("sling:resourceType")) {
					String propVal = child.getProperty("sling:resourceType").getValue().getString();
					if ((!propVal.contains("redesign")) && (propVal.contains("lordabbett"))) {
						if (hMap.get(propVal) == null) {
							hMap.put(propVal, new TreeSet<String>());
						}
						String path = child.getPath();
						String val = path.substring(path.indexOf("/jcr:content"), path.length());
						path = path.replace(val, "").trim();
						hMap.get(propVal).add(path);
						hMap.put(propVal, hMap.get(propVal));
						System.out.println(path);
					}
				}
				getPathRecursivlyWithoutRedesign(child);
			}
		} catch (RepositoryException e) {
			System.out
					.println("<<<<<<<<<<<<<Inside Catch Block with RepositoryException>>>>>>>>>>>>>>" + e.getMessage());
		} catch (Exception e) {
			System.out.println("<<<<<<<<<<<<<Inside Catch Block with Exception>>>>>>>>>>>>>>" + e.getMessage());
		}
	}

	public void writeDatatoCsv() {
		try (Writer writer = new FileWriter("D://thomas-ohalloran.csv")) {
			StringBuffer sb = new StringBuffer();
			sb.append("Component,Page Path");
			writer.append(sb.toString()).append("\n");
			for (Map.Entry<String, TreeSet<String>> entry : hMap.entrySet()) {
				for (String str : entry.getValue()) {
					sb.setLength(0);
					sb.append(entry.getKey()).append(",").append(str);
					writer.append(sb.toString()).append("\n");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace(System.err);
		}
	}

}
