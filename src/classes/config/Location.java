package classes.config;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Location {

	private String name;
	private int order;
	private HashMap<String, Device> devices = new HashMap<String, Device>();
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return this.order;
	}

	public void setOrder(int nr) {
		this.order = nr;
	}

	public Map<String, Device> getDevices() {
		return this.sort();
	}

	public void setDevices(HashMap<String, Device> devices) {
		this.devices = devices;
	}
	
	/* http://javarevisited.blogspot.com/2012/12/how-to-sort-hashmap-java-by-key-and-value.html */
    public <K extends Comparable, V extends Comparable> Map<String, Device> sort() {

        List<Map.Entry<String, Device>> entries = new LinkedList<Map.Entry<String, Device>>(this.devices.entrySet());
     
        Collections.sort(entries, new Comparator<Map.Entry<String, Device>>() {
            @Override
            public int compare(Entry<String, Device> o1, Entry<String, Device> o2) {
                return Integer.compare(o1.getValue().getOrder(), o2.getValue().getOrder());
            }
        });
     
        Map<String, Device> sortedMap = new LinkedHashMap<String, Device>();
     
        for(Map.Entry<String, Device> entry: entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
     
        return sortedMap;
    }
}