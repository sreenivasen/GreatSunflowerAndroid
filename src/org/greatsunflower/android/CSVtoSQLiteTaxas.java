package org.greatsunflower.android;

public class CSVtoSQLiteTaxas {

	private int id;
	private String taxa = null;
	private String middle_level = null;
	private String common_name = null;
	private String visitor_genus = null;
	private String visitor_species = null;
	private String plant_family = null;
	private String plant_genus = null;
	private String plant_species = null;
	private String var_subspecies = null;

	public String getVarSubSpecies() {
		return var_subspecies;
	}

	public void setVarSubSpecies(String var_subspecies) {
		this.var_subspecies = var_subspecies;
	}
	
	public String getPlantGenus() {
		return plant_genus;
	}

	public void setPlantGenus(String plant_genus) {
		this.plant_genus = plant_genus;
	}
	
	public String getPlantSpecies() {
		return plant_species;
	}

	public void setPlantSpecies(String plant_species) {
		this.plant_species = plant_species;
	}
	
	public String getPlantFamily() {
		return plant_family;
	}

	public void setPlantFamily(String plant_family) {
		this.plant_family = plant_family;
	}
	
	public String getVisitorGenus() {
		return visitor_genus;
	}

	public void setVisitorGenus(String visitor_genus) {
		this.visitor_genus = visitor_genus;
	}
	
	public String getVisitorSpecies() {
		return visitor_species;
	}

	public void setVisitorSpecies(String visitor_species) {
		this.visitor_species = visitor_species;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTaxa() {
		return taxa;
	}

	public void setTaxa(String taxa) {
		this.taxa = taxa;
	}

	public String getMiddleLevel() {
		return middle_level;
	}

	public void setMiddleLevel(String middle_level) {
		this.middle_level = middle_level;
	}

	public String getCommonName() {
		return common_name;
	}

	public void setCommonName(String common_name) {
		this.common_name = common_name;
	}

}
