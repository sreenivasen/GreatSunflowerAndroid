package org.greatsunflower.android;

public class SQLiteObservations  {
	  private int id;
	  private String pollinator;
	  private String flowername;

	  public int getId() {
	    return id;
	  }

	  public void setId(int id) {
	    this.id = id;
	  }

	  public String getPollinator() {
	    return pollinator;
	  }

	  public void setPollinator(String pollinator) {
	    this.pollinator = pollinator;
	  }
	  
	  public String getFlowerName() {
		  return flowername;
	  }
	  
	  public void setFlowerName(String flowername){
		  this.flowername = flowername;
	  }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return pollinator;
	  }
	} 
