package onight.osgi.ipojo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import lombok.val;

public class IPojoPurify {

	public static String purify(String str) {
		val javamap = new HashMap<String, String>();
		val scalamap = new HashMap<String, String>();
		val arr = str.split("(instance \\{)");
		val lastarr = arr[arr.length - 1];
		val f = lastarr.indexOf("}component");
		String lastinstance = "";
		String components = "";
		if (f > 0) {
			lastinstance = lastarr.substring(0, f + 1);
			components = lastarr.substring(f);
		}
		arr[arr.length - 1] = lastinstance;
		for (String x : arr) {
			val startIdx = x.indexOf("$component=\"");
			val endIdx = x.indexOf("\"", startIdx + "$component=\"".length());
			if (endIdx > startIdx && startIdx > 0) {
				val name = x.substring(startIdx + "$component=\"".length(), endIdx);
				if (name.endsWith("$")) {
					scalamap.put(name, x);
				} else {
					javamap.put(name, x);
				}
			}
		}
		for (String p : scalamap.keySet()) {
			String javakey = p.substring(0, p.length() - 1);
			if (javamap.containsKey(javakey)) {
				System.out.println("exist one:" + p);
				javamap.remove(javakey);
			}
		}
		StringBuffer sb = new StringBuffer();
		javamap.putAll(scalamap);
		Comparator<String> compare = new Comparator<String>(){

			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				return o1.compareTo(o2);
			}
			
		};
		String sortedKeys[]=new String[javamap.size()];
		Arrays.sort(javamap.keySet().toArray(sortedKeys),compare);
		
		for (String javav : sortedKeys) {
			sb.append("instance {").append(javamap.get(javav));
		}
		System.out.println("instance:" + sb);
		// println("component:" + components);
		javamap.clear();
		scalamap.clear();
		for (String x : components.split("\\}component")) {
			for (String p : x.split(" ")) {
				if (p.startsWith("$classname")) {
					String[] pp = p.split("=");
					if (pp.length == 2) {
						// println("name=:"+pp(1))
						if (pp[1].endsWith("$\"")) {
							scalamap.put(pp[1], x);
						} else {
							javamap.put(pp[1], x);
						}
					}
				}
			}
		}
		for (String p : scalamap.keySet()) {
			String javakey = p.substring(0, p.length() - 1);
			if (javamap.containsKey(javakey)) {
				System.out.println("exist com one:" + p);
				javamap.remove(javakey);
			}
		}

		StringBuffer sbcom = new StringBuffer();
		javamap.putAll(scalamap);
//		for (String javav : javamap.values()) {
//			sbcom.append("component").append(javav);
//		}
		sortedKeys=new String[javamap.size()];
		Arrays.sort(javamap.keySet().toArray(sortedKeys),compare);
		
		for (String javav : sortedKeys) {
			sbcom.append("component").append(javamap.get(javav));
		}
		
		System.out.println("sbcom=" + sbcom);
		sb.append(sbcom);
		System.out.println("purify:"+sb.toString());
		return sb.toString();
	}
}
