package lv.venta.fidi.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Curated Latvian cinema / DVD titles by IMDb id. Used before machine translation so UI shows proper names.
 */
public final class KnownMovieTitlesLv {

    private static final Map<String, String> BY_IMDB = new HashMap<>();

    static {
        p("tt0111161", "Ceļš uz brīvību");
        p("tt0068646", "Krusttēvs");
        p("tt0468569", "Tumsas bruņinieks");
        p("tt0109830", "Forrests Gamps");
        p("tt0137523", "Kaujas klubs");
        p("tt0133093", "Matrica");
        p("tt1375666", "Sāksme");
        p("tt0816692", "Starpzvaigžņu");
        p("tt0110912", "Krāpīgs stāsts");
        p("tt0108052", "Šindlera saraksts");
        p("tt0050083", "12 dusmīgi vīri");
        p("tt0167260", "Gredzenu pavēlnieks: Atgriešanās karalis");
        p("tt0120737", "Gredzenu pavēlnieks: Gredzenu brālība");
        p("tt0167261", "Gredzenu pavēlnieks: Divi torņi");
        p("tt0167263", "Gredzenu pavēlnieks: Atgriešanās karalis");
        p("tt0060196", "Labs, slikts, ļauns");
        p("tt0903747", "Breaking Bad");
        p("tt0126029", "Šreks");
        p("tt0298148", "Šreks 2");
        p("tt0413267", "Šreks Trešais");
        p("tt0892791", "Šreks: ilgi un laimīgi");
        p("tt1392190", "Mad Makss: dusmu ceļš");
        p("tt7286456", "Džokers");
        p("tt1853728", "Django atbrīvotais");
        p("tt4154756", "Atriebēji: Bezgalības karš");
        p("tt4154796", "Atriebēji: Noslēgums");
        p("tt2380307", "Koko");
        p("tt0472033", "Trons: Mantojums");
        p("tt1630029", "Ratatuj");
        p("tt1825683", "Duna");
        p("tt1099212", "Galaktikas sargi");
        p("tt0482571", "Zaļā jūdze");
        p("tt0114369", "Septiņi");
        p("tt0102926", "Jēru klusēšana");
        p("tt0172495", "Gladiators");
        p("tt0120815", "Lietus vīrs");
        p("tt2582802", "Īstā detektīva 1. sezona");
        p("tt0118799", "Amēlija no Montmartra");
        p("tt0110357", "Karalis Lauva");
        p("tt0266543", "Meklājot Nemo");
        p("tt0317705", "Lieliskie");
        p("tt0114709", "Rotaļlietu stāsts");
        p("tt0435761", "Rotaļlietu stāsts 3");
        p("tt0099685", "Labie puiši");
        p("tt0209144", "Sestā izjūta");
        p("tt0114814", "Parastie aizdomās turamie");
        p("tt1345836", "Tumsas bruņinieka atdzimšana");
        p("tt1130884", "Šatera sala");
        p("tt0086250", "Asinis uz asfalta");
        p("tt1832382", "Nakts mednieks");
        p("tt0361748", "Nežēlīgie");
        p("tt10872600", "Spēlētājs Nr. 1");
        p("tt0076759", "Zvaigžņu kari");
        p("tt6751668", "Parazīts");
        p("tt0103064", "Terminatoris 2: sprieduma diena");
        p("tt5074352", "Stīvs Džobss");
        p("tt7131622", "Zaļā grāmata");
        p("tt0112573", "Brīnišķīgais smadzenēs");
        p("tt0093058", "Bezdibenis");
        p("tt0110413", "Leon");
        p("tt0993846", "Volstrītas vilks");
        p("tt0073486", "Lidojums pār dzeguzligzdu");
        p("tt0407887", "Šerloks Holmss");
        p("tt8503618", "Šerloks Holmss: Ēnas spēle");
        p("tt6966692", "Mazā sieviete");
        p("tt5027774", "Trīs bilbordi uz robežas Ebingas, Misūri");
        p("tt1160419", "Duna: Otrā daļa");
        p("tt4154664", "Kapteinis Marvela");
        p("tt9362722", "Īss stāsts par garo mūžu");
        p("tt0118715", "Lielais Lebovskis");
        p("tt2278388", "Grand Budapestas viesnīca");
        p("tt0454921", "Harijs Poters un Filozofu akmens");
        p("tt1201607", "Harijs Poters un Noslēpumu kambaris");
        p("tt10272386", "Harijs Poters un Azkabanas gūsteknis");
        p("tt1187043", "500 dienu ar Samantu");
        p("tt0064116", "Par dolāru pāris asaru");
        p("tt0105695", "Nežēlīgā četruņa");
        p("tt1403865", "Īstā drosmība");
        p("tt2404435", "Nekas viņu nespēs apturēt");
        p("tt0054047", "Par desmit centiem papildu");
        p("tt0071230", "Par pāris dolāriem vairāk");
        p("tt0120689", "No debesīm līdz ellē");
        p("tt0372784", "Betmens: sākums");
        p("tt1877830", "Betmens");
        p("tt0088763", "Atpakaļ nākotnē");
        p("tt0082971", "Indiana Džonss: pazudušā šķīvja meklējumos");
        p("tt0369610", "Juras pasaule");
        p("tt0398286", "Sapinušies");
        p("tt0245429", "Čihiro skaņu pasaulē");
        p("tt0347149", "Haulla brīnumainā pils");
        p("tt1981115", "Tors: Tumsas pasaule");
        p("tt2395427", "Atriebēji: Ultrona laikmets");
        p("tt0848228", "Atriebēji");
        p("tt2015381", "Galaktikas sargi");
        p("tt5726616", "Sauc mani savā vārdā");
        p("tt2582846", "Vaina mūsu zvaigznēs");
        p("tt0332280", "Piezīmju grāmatiņa");
        p("tt0105236", "Rezervuāra suņi");
        p("tt0120586", "Amerikāņu istorija X");
        p("tt0405094", "Citu dzīves");
        p("tt0910970", "Volī-ī");
        p("tt1049413", "Augšup");
        p("tt1979376", "Rotaļlietu stāsts 4");
        p("tt2096673", "Iekšējā pasaule");
        p("tt2948372", "Dvēsele");
        p("tt4633694", "Zirnekļcilvēks: Jaunā pasaule");
        p("tt2382320", "Nav laika mirt");
        p("tt0478970", "Skudrucilvēks");
        p("tt6723592", "Tenets");
        p("tt0120382", "Trumana šovs");
        p("tt0107048", "Svētdiena kā ikvienu dienu");
        p("tt0264464", "Noķer mani, ja vari");
        p("tt8579674", "1917");
        p("tt2119532", "Heksova grēda");
        p("tt0078788", "Apokalipse šodien");
        p("tt1856101", "Blade Runner 2049");
        p("tt0083658", "Blade Runner");
        p("tt1371111", "Rītdiena atnāk pilnā sparā");
        p("tt3498820", "Kapteinis Amerika: Pilsoņu karš");
        p("tt3896198", "Galaktikas sargi 2");
        p("tt3521164", "Vaiana");
        p("tt2948356", "Zootropole");
        p("tt0844471", "Mākoņains ar iespēju uz gaļas bumbām lietu");
        p("tt0095327", "Kapu pļāvējs");
        p("tt0180093", "Sapnis par Reikviemu");
        p("tt0095765", "Kino paradīze");
        p("tt0434409", "V kā vendeta");
        p("tt2267998", "Pazudusī");
        p("tt1170358", "Hobits: Negaidīts ceļojums");
        p("tt0903624", "Hobits: Smauga postaža");
        p("tt2310332", "Hobits: Piecu armiju kauja");
        p("tt0043014", "Saulrieta bulvāris");
        p("tt0053221", "Rio Bravo");
        p("tt0062622", "Batčs Kasidijs un Sandansas puisis");
        p("tt0081505", "Spīdums");
        p("tt0070047", "Ekscorcists");
        p("tt1457767", "Konžerings");
        p("tt1179904", "Insidiuss");
        p("tt7784604", "Mantojums");
        p("tt5052448", "Izglābies");
        p("tt1396484", "Tas");
        p("tt0087800", "Murgi Elmstrītā");
        p("tt0078748", "Svešinieks");
        p("tt0120601", "Būt Džonam Malkovičam");
        p("tt1051906", "Neredzamais cilvēks");
        p("tt3387520", "Raganas");
        p("tt2717822", "Tas seko");
        p("tt4912910", "Misija neiespējama: Norēķins");
        p("tt0266697", "Nogalināt Bilu 1");
        p("tt0166924", "Mulholandas ceļš");
        p("tt0117571", "Kliedziens");
        p("tt0387564", "Zāģis");
        p("tt0073195", "Žoklis");
        p("tt0325980", "Karību jūras pirāti: Melnās pērles lāsts");
        p("tt0338013", "Mūžīgā saules mirdzība");
        p("tt2106476", "Medības");
        p("tt0090605", "Svešinieki");
        p("tt0119488", "L.A. konfidenciāli");
        p("tt1517268", "Alise Brīnumzemē");
    }

    private static void p(String imdbId, String titleLv) {
        BY_IMDB.put(imdbId.toLowerCase(Locale.ROOT), titleLv);
    }

    public static String titleOrNull(String imdbId) {
        if (imdbId == null || imdbId.isBlank()) {
            return null;
        }
        return BY_IMDB.get(imdbId.trim().toLowerCase(Locale.ROOT));
    }

    private KnownMovieTitlesLv() {
    }
}
