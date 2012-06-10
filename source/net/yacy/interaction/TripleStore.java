// Author: DL

package net.yacy.interaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import net.yacy.kelondro.logging.Log;
import net.yacy.search.Switchboard;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;


public class TripleStore {

	public static Model model = ModelFactory.createDefaultModel();

	public static ConcurrentHashMap<String, Model> privatestorage = null;

	public static String file;


	public static void Load (String filename) {

		Model tmp  = ModelFactory.createDefaultModel();

		Log.logInfo("TRIPLESTORE", "Loading from " + filename);

		try {
            InputStream in = FileManager.get().open(filename);

            // read the RDF/XML file
            tmp.read(in, null);
        }
        finally
        {
            	model = model.union(tmp);
        }


	}


	public static void Add (String rdffile) {

		Model tmp  = ModelFactory.createDefaultModel();


		try {
            @SuppressWarnings("deprecation")
			InputStream in = new StringBufferInputStream(rdffile);

            // read the RDF/XML file
            tmp.read(in, null);
        }
        finally
        {
            	model = model.union(tmp);
        }

	}

	public static void Save (String filename) {


    	FileOutputStream fout;
		try {


			fout = new FileOutputStream(filename);

			model.write(fout);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.logWarning("TRIPLESTORE", "Saving to " + filename+" failed");
		}



	}


	public static void initPrivateStores(Switchboard switchboard) {

		Log.logInfo("TRIPLESTORE", "Init private stores");

		if (privatestorage != null) privatestorage.clear();

		try {

			Iterator<de.anomic.data.UserDB.Entry> it = switchboard.userDB.iterator(true);

			while (it.hasNext()) {
				de.anomic.data.UserDB.Entry e = it.next();
				String username = e.getUserName();

				Log.logInfo("TRIPLESTORE", "Init " + username);

				String filename = new File(switchboard.getConfig("dataRoot", ""), "DATA/TRIPLESTORE").toString()+"/"+username+"_triplestore.rdf";

				Model tmp  = ModelFactory.createDefaultModel();

				Log.logInfo("TRIPLESTORE", "Loading from " + filename);

				try {
		            InputStream in = FileManager.get().open(filename);

		            // read the RDF/XML file
		            tmp.read(in, null);
		        }
		        finally
		        {
		        	privatestorage.put(username, tmp);

		        }

			}

			}
			catch (Exception anyex) {

			}



		// create separate model

	}

	public static void savePrivateStores(Switchboard switchboard) {

		if (privatestorage == null) return;

		for (Entry<String, Model> s : privatestorage.entrySet()) {

			String filename = new File(switchboard.getConfig("dataRoot", ""), "DATA/TRIPLESTORE").toString()+"/"+s.getKey()+"_triplestore.rdf";

			FileOutputStream fout;
			try {


				fout = new FileOutputStream(filename);

				s.getValue().write(fout);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.logWarning("TRIPLESTORE", "Saving to " + filename+" failed");
			}

		}
	}

}
