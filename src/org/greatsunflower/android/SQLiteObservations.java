package org.greatsunflower.android;

public class SQLiteObservations  {
	  private int id;
	  private String pollinator;
	  private String flowername;
	  private String imagepath;
	  private String createdDatetTime;
	  private int session;
	  private int sessionId;
	  private String startDateTime;
	  private String endDateTime;
	  private int isSubmitted;
	  private String observationType;
	  private String middle_level;
	  private String common_name;
	  private String visitor_genus;
	  private String visitor_species;
	  private int pollinator_count;
	  
	  private String plant_family;
	  private String plant_genus;
	  private String plant_species;
	  private String plant_var_subspecies;
	  
	  public void setPlantVarSubSpecies(String plant_var_subspecies){
		  this.plant_var_subspecies = plant_var_subspecies;
	  }
	  
	  public String getPlantVarSubSpecies(){
		  return plant_var_subspecies;
	  }
	  
	  public void setPlantSpecies(String plant_species){
		  this.plant_species = plant_species;
	  }
	  
	  public String getPlantSpecies(){
		  return plant_species;
	  }
	  
	  
	  public void setPlantGenus(String plant_genus){
		  this.plant_genus = plant_family;
	  }
	  
	  public String getPlantGenus(){
		  return plant_genus;
	  }
	  
	  public void setPlantFamily(String plant_family){
		  this.plant_family = plant_family;
	  }
	  
	  public String getPlantFamily(){
		  return plant_family;
	  }
	  
	  public void setPollinatorCount(int count){
		  this.pollinator_count = count;
	  }
	  
	  public int getPollinatorCount(){
		  return pollinator_count;
	  }
	  
	  public void setVisitorSpecies(String visitor_species){
		  this.visitor_species = visitor_species;
	  }
	  
	  public String getvisitorSpecies(){
		  return visitor_species;
	  }
	  
	  public void setVisitorGenus(String visitor_genus){
		  this.visitor_genus = visitor_genus;
	  }
	  
	  public String getVisitorGenus(){
		  return visitor_genus;
	  }
	  
	  public void setCommonName(String common_name){
		  this.common_name = common_name;
	  }
	  
	  public String getCommonName(){
		  return common_name;
	  }
	  
	  public void setMiddleLevel(String middle_level){
		  this.middle_level = middle_level;
	  }
	  
	  public String getMiddleLevel(){
		  return middle_level;
	  }

	  public int getId() {
	    return id;
	  }

	  public void setId(int id) {
	    this.id = id;
	  }
	  
	  public void setIsSubmitted(String isSubmitted){
		  this.isSubmitted = Integer.valueOf(isSubmitted);
	  }
	  
	  public int getIsSubmitted(){
		  return isSubmitted;
	  }
	  
	  public void setObservationType(String observationType){
		  this.observationType = observationType;
	  }
	  
	  public String getObservationType(){
		  return observationType;
	  }
	  
	  public void setEndDateTime(String endDateTime){
		  this.endDateTime = endDateTime;
	  }
	  
	  public String getEndDateTime(){
		  return endDateTime;
	  }
	  
	  public void setStartDateTime(String startDateTime){
		  this.startDateTime = startDateTime;
	  }
	  
	  public String getStartDateTime(){
		  return startDateTime;
	  }
	  
	  public void setSessionId(String sessionId){
		  this.sessionId = Integer.valueOf(sessionId);
	  }
	  
	  public int getSessionId(){
		  return sessionId;
	  }
	  
	  public String getImagePath(){
		 return imagepath;
	  }
	  
	  public void setImagePath(String imagepath){
		  this.imagepath = imagepath;
	  }
	  
	  public String getCreatedDateTime(){
		  return createdDatetTime;
	  }
	  
	  public void setCreatedDateTime(String createdDt){
		  this.createdDatetTime = createdDt;
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
	  
	  public int getSession() {
		  return session;
	  }
	  
	  public void setSession(int session){
		  this.session = session;
	  }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return pollinator;
	  }
	} 
