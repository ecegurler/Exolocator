package controllers;

import play.*;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.*;

import java.io.*;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import models.*;

public class Application extends Controller {
	// Application entry point

	public static void index() {
		render();
	}

	public static void help() {
		render();
	}

	public static void contactUs() {
		render();
	}

	public static void SpeciesProteinSequenceResult(String pro_id) {
		String DirectoryList = "";
		String s = null;
		String str = "";
		String percentage = "";
		String linkNo = "";
		String Final = "";
		String InputFile = "";
		String DatabaseFile = "";
		String BlastallResults = "";
		String BlastallSoftware = "";

		String CurrentDir = System.getProperty("user.dir");
		String Configuration = CurrentDir + "/conf/directories.conf";

		try {
			FileInputStream fstream = new FileInputStream(Configuration);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			int i = 0;
			while ((strLine = br.readLine()) != null) {
				if (i == 1)
					InputFile = strLine;

				else if (i == 3)
					DatabaseFile = strLine;

				else if (i == 5)
					BlastallResults = strLine;

				else if (i == 7)
					BlastallSoftware = strLine;

				i++;
			}

			in.close();

		} catch (IOException e) {
		}

		if (!FastaFormat(pro_id)) {
			Final = "Protein sequence should be in fasta format. Please control the sequence or try thr link below:";
			pro_id = "";
		} else if (!InDatabaseRange(pro_id)) {
			Final = "Protein sequence is not in the range of our database.Please control the sequence or try the link below:";
			pro_id = "";
		} else {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(
						InputFile));
				out.write(pro_id);
				out.close();
			} catch (IOException e) {
			}
			try {

				// -----------------------------------------------execute
				// blastall

				Runtime r = Runtime.getRuntime();
				Process p1 = r.exec(BlastallSoftware + " -p blastp -d "
						+ DatabaseFile + " -m 8 -o " + BlastallResults + " -i "
						+ InputFile);

				BufferedReader stdInput = new BufferedReader(
						new InputStreamReader(p1.getInputStream()));

				BufferedReader stdError = new BufferedReader(
						new InputStreamReader(p1.getErrorStream()));

				// read the output from the command
				// System.out.println("Here is the standard output of the command:\n");
				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}

				// read any errors from the attempted command
				// System.out.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null) {
					System.out.println(s);
				}

				// ---------------------------------------------------------------------------

				try {

					FileInputStream fstream = new FileInputStream(
							BlastallResults);

					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(in));
					String strLine;

					if ((strLine = br.readLine()) != null) {
						str += System.getProperty("line.separator") + strLine;
					}
					String parts[] = str.split("	");
					pro_id = parts[1];
					percentage = parts[2] + "%";
					linkNo = parts[3];
					// System.out.println (str);
					// pro_id = str;
					in.close();

				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
				}
			} catch (IOException e) {
				System.out.println("exception happened - here's what I know: ");
				e.printStackTrace();
				// System.exit(-1);
			}
			if (!percentage.equals("") && !linkNo.equals("")) {
				Final = "The best match is "
						+ pro_id
						+ " with "
						+ percentage
						+ " similarity and "
						+ linkNo
						+ " link alignment. Result is inserted into the search box. ";
			} else {
				Final = "Protein sequence should be in fasta format. Please control the sequence and try again.";
				pro_id = "";
			}
		}

		render(pro_id, percentage, linkNo, Final);

	}

	public static void HumanProteinSequenceResult(String human_pro_id) {

		String s = null;
		String str = "";
		String percentage = "";
		String linkNo = "";
		String Final = "";
		String InputFile = "";
		String DatabaseFile = "";
		String BlastallResults = "";
		String BlastallSoftware = "";

		String CurrentDir = System.getProperty("user.dir");
		String Configuration = CurrentDir + "/conf/directories.conf";

		try {
			FileInputStream fstream = new FileInputStream(Configuration);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			int i = 0;
			while ((strLine = br.readLine()) != null) {
				if (i == 1)
					InputFile = strLine;

				else if (i == 3)
					DatabaseFile = strLine;

				else if (i == 5)
					BlastallResults = strLine;

				else if (i == 7)
					BlastallSoftware = strLine;

				i++;
			}

			in.close();

		} catch (IOException e) {
		}

		if (!FastaFormat(human_pro_id)) {
			Final = "Protein sequence should be in fasta format. Please control the sequence and try again.";
			human_pro_id = "";
		} else if (!InDatabaseRange(human_pro_id)) {
			Final = "Protein sequence is not in the range of our database.Please control the sequence and try again.";
			human_pro_id = "";
		} else {
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(
						InputFile));
				out.write(human_pro_id);
				out.close();
			} catch (IOException e) {
			}
			try {

				// -----------------------------------------------execute
				// blastall

				Runtime r = Runtime.getRuntime();
				Process p1 = r.exec(BlastallSoftware + " -p blastp -d "
						+ DatabaseFile + " -m 8 -o " + BlastallResults + " -i "
						+ InputFile);

				BufferedReader stdInput = new BufferedReader(
						new InputStreamReader(p1.getInputStream()));

				BufferedReader stdError = new BufferedReader(
						new InputStreamReader(p1.getErrorStream()));

				while ((s = stdInput.readLine()) != null) {
					System.out.println(s);
				}

				while ((s = stdError.readLine()) != null) {
					System.out.println(s);
				}

				// ---------------------------------------------------------------------------

				try {

					FileInputStream fstream = new FileInputStream(
							BlastallResults);

					DataInputStream in = new DataInputStream(fstream);
					BufferedReader br = new BufferedReader(
							new InputStreamReader(in));
					String strLine;

					if ((strLine = br.readLine()) != null) {
						str += System.getProperty("line.separator") + strLine;
					}
					String parts[] = str.split("	");
					human_pro_id = parts[1];
					percentage = parts[2] + "%";
					linkNo = parts[3];
					// System.out.println (str);
					// human_pro_id = str;
					in.close();

				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
				}
			} catch (IOException e) {
				System.out.println("exception happened - here's what I know: ");
				e.printStackTrace();
				// System.exit(-1);
			}
			if (!percentage.equals("") && !linkNo.equals("")) {
				Final = "The best match is "
						+ human_pro_id
						+ " with "
						+ percentage
						+ " similarity and "
						+ linkNo
						+ " link alignment. Result is inserted into the search box. ";
			} else {
				Final = "Protein sequence should be in fasta format. Please control the sequence and try again.";
				human_pro_id = "";
			}
		}

		render(human_pro_id, percentage, linkNo, Final);
	}

	@Before
	static void addDefaults() {
		renderArgs.put("applicationTitle", Play.configuration
				.getProperty("application.title"));
		renderArgs.put("applicationBaseline", Play.configuration
				.getProperty("application.baseline"));
	}

	public static void SearchByProteinSequence() {
		render();
	}

	public static void singleSpeciesSearchForm(
			@Required(message = "Ensembl protein id is required") String protein_id) {
		renderSingleSpeciesResults(protein_id);
	}

	public static void renderSingleSpeciesResults(String protein_id) {
		List<Ortholog> orthologs = Ortholog.find("byProtein_id", protein_id)
				.fetch();
		// TODO: if there is more then one ortholog, question of choice?
		Ortholog ortholog = orthologs.get(0);
		List<Exon> exons = Exon.find("byRef_protein_idAndSpeciesAndSource",
				ortholog.ref_protein_id, ortholog.species, "sw_gene").fetch();
		String species = ortholog.species;
		// input file for jalview
		String jalview_input_f = jalviewInputFileGenerator(
				ortholog.ref_protein_id, ortholog.species);
		render(exons, species, jalview_input_f);
	}

	public static void humanHomologSearchForm(
			@Required(message = "Ensembl protein id is required") String ref_protein_id,
			@Required(message = "Species is required") String species) {
		renderHumanHomologSearch(ref_protein_id, species);
	}

	public static void renderHumanHomologSearch(String ref_protein_id,
			String species) {
		List<Ortholog> orthologs;
		if (species.equalsIgnoreCase("all")) {
			orthologs = Ortholog.find("byRef_protein_id", ref_protein_id)
					.fetch();
		} else {
			orthologs = Ortholog.find("byRef_protein_idAndSpecies",
					ref_protein_id, species).fetch();
		}
		// input file for jalview
		String jalview_input_f = String.format("/Best_MSA/%s.afa",
				ref_protein_id);
		render(orthologs, jalview_input_f);
	}

	public static List<List<Exon>> orthologSearch(String ref_protein_id,
			String species) {
		List<String> species_list;
		if (species.equalsIgnoreCase("all")) {
			species_list = findSpeciesWithOrthologs(ref_protein_id);
		} else {
			species_list = new ArrayList<String>();
			species_list.add(species);
		}
		List<List<Exon>> species_exons = new ArrayList<List<Exon>>();
		for (String spec : species_list) {
			Ortholog ortholog = Ortholog.find("byRef_protein_idAndSpecies",
					ref_protein_id, spec).first();
			List<Exon> exons = Exon.find("byRef_protein_idAndSpeciesAndSource",
					ref_protein_id, spec, "blastn").fetch();
			species_exons.add(exons);
		}
		return species_exons;
	}

	public static void getSpeciesForProtein(String ensembl_id) {
		renderJSON(findSpeciesWithOrthologs(ensembl_id));
	}

	public static List<String> findSpeciesWithOrthologs(String ref_protein_id) {
		List<Ortholog> orthologs = Ortholog.find("byRef_protein_id",
				ref_protein_id).fetch();
		List<String> species_with_orthologs = new ArrayList<String>();
		for (Ortholog ortholog : orthologs) {
			species_with_orthologs.add(ortholog.species);
		}
		return species_with_orthologs;
	}

	public static void download_exon(String ensembl_id, String species,
			String download_type) {
		/*
		 * Method invoked when downloading a FASTA file.
		 */
		FileInputStream inputStream = null;
		try {
			if (download_type.equals("N")) {
				// Get contents
				List<Exon> exons = Exon.find("bySourceAndEnsembl_idAndSpecies",
						"sw_gene", ensembl_id, species).fetch();
				String file_name = String.format("%s_%s_%s", ensembl_id,
						species, download_type);

				// create temp file
				File tempFile = File.createTempFile(file_name, ".fa");

				BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

				// Write Contents
				bw.write(String.format(">sw_gene_%s_%s\n", exons.get(0).start,
						exons.get(0).start));
				bw.write(exons.get(0).sequence);
				bw.close();

				inputStream = new FileInputStream(tempFile);

				// delete temp file
				tempFile.delete();

				// download the file as held in the inputstream
				renderBinary(inputStream, file_name + ".fa");
			} else {
				// Get contents
				List<ExonAlignmentPiece> exon_alignment_pieces = ExonAlignmentPiece
						.find("byRef_exon_idAndSpecies", ensembl_id, species)
						.fetch();
				String file_name = String.format("%s_%s_%s", ensembl_id,
						species, download_type);

				// create temp file
				File tempFile = File.createTempFile(file_name, ".fa");

				BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));

				// Write Contents
				for (ExonAlignmentPiece element : exon_alignment_pieces) {
					bw.write(String.format(">%s_%s\n", element.ref_prot_start,
							element.ref_prot_stop));
					bw.write(String.format("%s\n", element.spec_protein_seq));
				}
				bw.close();

				inputStream = new FileInputStream(tempFile);

				// delete temp file
				tempFile.delete();

				// download the file as held in the inputstream
				renderBinary(inputStream, file_name + ".fa");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static String jalviewInputFileGenerator(String ref_protein_id,
			String species) {
		// filename generator
		String perl_cmd = "perl";
		String db_name = "exolocator_db";
		String output_path = "";
		String script_path = "";
		String jalview_input_f = "";

		String CurrentDir = System.getProperty("user.dir");
		String Configuration = CurrentDir + "/conf/directories.conf";

		try {
			FileInputStream fstream = new FileInputStream(Configuration);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			int i = 0;
			while ((strLine = br.readLine()) != null) {
				if (i == 9)
					output_path = strLine;
				if (i == 11)
					script_path = strLine;

				i++;
			}

			in.close();

		} catch (IOException e) {
		}
		try {
			File jalview_input_file = File.createTempFile("jalview_", ".afa",
					new File(output_path));
			jalview_input_f = jalview_input_file.getName();
			String cmd = String.format("%s %s %s %s %s %s/%s", perl_cmd,
					script_path, db_name, ref_protein_id, species, output_path,
					jalview_input_f);
			// System.out.println(cmd);
			Runtime.getRuntime().exec(cmd);
		} catch (Exception e) {
			System.out.println(e);
		}
		return String.format("/%s/%s", "Resource", jalview_input_f);
	}

	public static void jalviewInputFileCleanup(String file_path) {
		String output_path = "";
		String CurrentDir = System.getProperty("user.dir");
		String Configuration = CurrentDir + "/conf/directories.conf";

		try {
			FileInputStream fstream = new FileInputStream(Configuration);

			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			int i = 0;
			while ((strLine = br.readLine()) != null) {
				if (i == 13)
					output_path = strLine;

				i++;
			}

			in.close();

		} catch (IOException e) {
		}

		// System.out.println(output_path + file_path);
		File f1 = new File(output_path + file_path);
		// boolean success = f1.delete();
		boolean success = true;
		if (!success) {
			// System.out.println("Deletion failed.");
		} else {
			// System.out.println("File deleted.");
		}
	}

	// -------------------------------------------------------------
	public static boolean FastaFormat(String pro_id) {
		if (pro_id.length() != 0 && pro_id.substring(0, 1).equals(">")) {
			String temp = "";

			Scanner scanner = new Scanner(pro_id);
			if (scanner.hasNextLine()) {
				scanner.nextLine();
				while (scanner.hasNextLine()) {
					temp += scanner.nextLine()
							+ System.getProperty("line.separator");
				}
				pro_id = temp;
			}
		}
		for (int i = 0; i < pro_id.length(); i++) {
			char temp = pro_id.charAt(i);
			if ((int) temp != (10) && (int) temp != (13)
					&& ((int) temp < 65 || (int) temp > 90)) {
				return false;
			}
		}
		return true;
	}

	// ------------------------------------------------------
	public static boolean InDatabaseRange(String pro_id) {
		if (pro_id.length() > 33423 || pro_id.length() < 7) {
			return false;
		}
		return true;
	}
}
