package controllers;

import play.*;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.*;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.*;

import models.*;

public class Application extends Controller {
    //Application entry point
	public static void index() {
        render();
    }
	
	public static void help() {
        render();
    }
	
    @Before
    static void addDefaults() {
        renderArgs.put("applicationTitle", Play.configuration.getProperty("application.title"));
        renderArgs.put("applicationBaseline", Play.configuration.getProperty("application.baseline"));
    }
    
    public static void singleSpeciesSearchForm(
    		@Required(message="Ensembl protein id is required") String protein_id){
    	renderSingleSpeciesResults(protein_id);
    }
    
    public static void renderSingleSpeciesResults(String protein_id){    	
    	List<Ortholog> orthologs = Ortholog.find("byProtein_id", protein_id).fetch();
    	//TODO: if there is more then one ortholog, question of choice?
    	Ortholog ortholog = orthologs.get(0);
    	List<Exon> exons = Exon.find("byRef_protein_idAndSpeciesAndSource", ortholog.ref_protein_id, ortholog.species, "blastn").fetch();
    	
    	//input file for jalview
    	String jalview_input_f = jalviewInputFileGenerator(ortholog.ref_protein_id, ortholog.species); 
    	render(exons, jalview_input_f);
    }
    
    public static void humanHomologSearchForm(
    		@Required(message="Ensembl protein id is required") String ref_protein_id, 
    		@Required(message="Species is required")String species){
    	renderHumanHomologSearch(ref_protein_id, species);
    }
    
    public static void renderHumanHomologSearch(String ref_protein_id, String species){
    	List<Ortholog> orthologs;
    	if (species.equalsIgnoreCase("all")) {
    		orthologs = Ortholog.find("byRef_protein_id", ref_protein_id).fetch();
		} else {
    		orthologs = Ortholog.find("byRef_protein_idAndSpecies", ref_protein_id, species).fetch();
		}
    	
    	//input file for jalview
    	String jalview_input_f = "/Resource/Ailuropoda_melanoleuca.afa";    	
    	render(orthologs, jalview_input_f);
    }
    
    public static List<List<Exon>> orthologSearch(String ref_protein_id, String species){
    	List<String> species_list;
    	if (species.equalsIgnoreCase("all")) {
    		species_list = findSpeciesWithOrthologs(ref_protein_id);
		} else {
			species_list = new ArrayList<String>();
			species_list.add(species);
		}
    	List<List<Exon>> species_exons = new ArrayList<List<Exon>>();
    	for (String spec : species_list) {
    		Ortholog ortholog = Ortholog.find("byRef_protein_idAndSpecies", ref_protein_id, spec).first();
        	List<Exon> exons = Exon.find("byRef_protein_idAndSpeciesAndSource", ref_protein_id, spec, "blastn").fetch();
        	species_exons.add(exons);
		}
    	return species_exons;
    }
    
    public static void getSpeciesForProtein(String ensembl_id){
    	renderJSON(findSpeciesWithOrthologs(ensembl_id));
    }
    
    public static List<String> findSpeciesWithOrthologs(String ref_protein_id){
    	List<Ortholog> orthologs = Ortholog.find("byRef_protein_id", ref_protein_id).fetch();
		List<String> species_with_orthologs   = new ArrayList<String>();
    	for (Ortholog ortholog : orthologs) {
    		species_with_orthologs.add(ortholog.species);
    	}
    	return species_with_orthologs;
    }

    public static void download_exon(String ensembl_id, String download_type){    	
    	System.out.println("DOWNLOAD!");
    	System.out.println(ensembl_id);
    		
    	FileInputStream inputStream = null;
    	String file_name = ensembl_id;
        try{
            //create temp file
            File tempFile = File.createTempFile( file_name, ".fa" );
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
    	    //Write Contents
            bw.write("This is the temporary file content");
    	    bw.close();
            
            inputStream = new FileInputStream( tempFile );

            //delete temp file
            tempFile.delete();

            //download the file as held in the inputstream
            //renderBinary( inputStream, file_name + ".fa" );
        }
        catch( Exception e ){
        	System.out.println(e);
        }
    }
    
    public static String jalviewInputFileGenerator(String ref_protein_id, String species){
    	//filename generator
    	String jalview_input_f = "IVANA.afa"; 
    	String perl_cmd = "perl";
    	String db_name	= "exolocator_db";
    	String output_path	= "/home/marioot/Eclipse/WebDev/Exolocator/Resource/";
    	String script_path	= "/home/marioot/Eclipse/WebDev/Exolocator/lib/alignment_assembler.pl";
   		String cmd = String.format("%s %s %s %s %s %s/%s", perl_cmd, script_path, db_name, ref_protein_id, species, output_path, jalview_input_f);
   		try{
   			System.out.println(cmd);
    		Runtime.getRuntime().exec(cmd);
    	}
    	catch( Exception e ){
            	System.out.println(e);
        }
    	return String.format("/%s/%s", "Resource", jalview_input_f);
    }
    
    public static void jalviewInputFileCleanup(){
    	System.out.println("jalviewInputFileCleanup ušao!");
    	File f1 = new File("/home/marioot/Eclipse/WebDev/Exolocator/Resource/XYZ.afa");
    	  boolean success = f1.delete();
    	  if (!success){
    	  System.out.println("Deletion failed.");
    	  }else{
    	  System.out.println("File deleted.");
    	  }
    }
}