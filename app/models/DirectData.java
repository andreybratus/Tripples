package models;

/**
 * Model describing the data sent in case of Direct Search
 * 
 * 
 * @see {@link SearchEngine}
 */


public class DirectData {

public String shares;
public String url;
public String name;
/**3
 * Default constructor to initialize the data in the model.
 * @param shares
 * @param url
 */

public DirectData(String shares,String url, String name){
	this.shares = shares;
	this.url = url;
	this.name = name;
}

}
