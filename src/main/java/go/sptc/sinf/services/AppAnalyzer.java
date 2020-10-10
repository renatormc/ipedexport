/*
 * Copyright 2012-2014, Luis Filipe da Cruz Nassif
 * 
 * This file is part of Indexador e Processador de Evidências Digitais (IPED).
 *
 * IPED is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IPED is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IPED.  If not, see <http://www.gnu.org/licenses/>.
 */
package go.sptc.sinf.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.util.Version;

// import dpf.sp.gpinf.indexer.Versao;
// import dpf.sp.gpinf.indexer.config.AdvancedIPEDConfig;
// import dpf.sp.gpinf.indexer.config.ConfigurationManager;
// import dpf.sp.gpinf.indexer.process.IndexItem;
// import dpf.sp.gpinf.indexer.process.task.HashTask;
// import dpf.sp.gpinf.indexer.process.task.PhotoDNATask;
// import org.apache.lucene.util.Version;

/*
 * Define analizadores, tokenizadores implicitamente, de indexação específicos para cada propriedade, 
 */
public class AppAnalyzer {

    private static int maxTokenLengh = 20;
    private static boolean  filterNonLatinChars = false;
    private static boolean  convertCharsToAscii = true;

    public enum HASH {
        MD5("md5"),
        SHA1("sha-1"), 
        SHA256("sha-256"),
        SHA512("sha-512"),
        EDONKEY("edonkey");

        private String name;

        HASH(String val) {
            this.name = val;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static Analyzer get() {
        Map<String, Analyzer> analyzerPerField = new HashMap<String, Analyzer>();
        analyzerPerField.put("category", new StandardASCIIAnalyzer(Version.LUCENE_4_9, true));
        analyzerPerField.put("id", new KeywordAnalyzer());
        analyzerPerField.put("ftkId", new KeywordAnalyzer());
        analyzerPerField.put("parentId", new KeywordAnalyzer());
        analyzerPerField.put("created", new KeywordAnalyzer());
        analyzerPerField.put("modified", new KeywordAnalyzer());
        analyzerPerField.put("accessed", new KeywordAnalyzer());
        analyzerPerField.put("evidenceUUID", new KeywordAnalyzer());
        
        StandardASCIIAnalyzer hashAnalyzer = new StandardASCIIAnalyzer(Version.LUCENE_4_9, false);
        hashAnalyzer.setMaxTokenLength(Integer.MAX_VALUE);
        analyzerPerField.put(HASH.MD5.toString(), hashAnalyzer);
        analyzerPerField.put(HASH.EDONKEY.toString(), hashAnalyzer);
        analyzerPerField.put(HASH.SHA1.toString(), hashAnalyzer);
        analyzerPerField.put(HASH.SHA256.toString(), hashAnalyzer);
        analyzerPerField.put(HASH.SHA512.toString(), hashAnalyzer);
        analyzerPerField.put("photoDNA", hashAnalyzer);

        StandardASCIIAnalyzer defaultAnalyzer = new StandardASCIIAnalyzer(Version.LUCENE_4_9, false);
        defaultAnalyzer.setMaxTokenLength(maxTokenLengh);
        defaultAnalyzer.setFilterNonLatinChars(filterNonLatinChars);
        defaultAnalyzer.setConvertCharsToAscii(convertCharsToAscii);
        return new PerFieldAnalyzerWrapper(defaultAnalyzer, analyzerPerField);
    }

}
