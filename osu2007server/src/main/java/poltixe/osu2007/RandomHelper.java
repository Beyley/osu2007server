package poltixe.osu2007;

import java.util.*;

public class RandomHelper {
    static String[] randomWords = { "aj", "akselo", "ambauxseksema", "amori", "anuso", "anusulo", "atleta", "bugri",
            "cico", "cxuri", "cxiesulino", "cxuro", "damne", "dauxra", "dika", "ekshibiciulo", "fek", "fekado",
            "fekajxo", "fekero", "feki", "feko", "fektruo", "fendo", "fikadi", "fikantino", "fikanto", "fikatino",
            "fikato", "fiki", "fikintino", "fikinto", "fikitino", "fikito", "fikontino", "fikonto", "fikotino",
            "fikoto", "fikuntino", "fikunto", "fikutino", "fikuto", "fingrumi", "forfikigxi", "forta", "frandzi",
            "frapeti", "frapi", "froti", "furzi", "furzo", "gadmeso", "geja", "glano", "hara", "harega", "hareta",
            "huj", "kacego", "kaceto", "kaco-sucxi", "kaco", "kacujo", "kaki", "kisi", "klabo", "kojonoj", "kondomo",
            "korpulenta", "kreteno", "kurba", "leki", "liki", "longa", "magra", "malcxastulino", "maldika", "mallonga",
            "malmola", "malpli", "malrapide", "malseka", "malstreta", "malvarma", "mam-pinto", "mamo", "masagxi",
            "masturbi", "merdo", "midzi", "mola", "mordegi", "mordeti", "mordi", "naz-muko", "onani", "onanigi", "ovoj",
            "patrinfikulo", "peniso", "picxo", "picxo-leki", "pisi", "pizango", "pli", "postajxo", "prepucio",
            "publikulino", "pubo", "pugo", "putinfilacxo", "putino", "rapide", "razita", "rektumo", "rigida", "ronda",
            "samseksema", "seka", "seksumi", "sid-vangoj", "skroto", "sodomii", "sodomiigi", "spili", "streta", "sucxi",
            "sxmaci", "sxpruci", "umbiliko", "vagineto", "vagino", "varma", "viando", "vipi", "volupta", "voluptama" };

    static Random r = new Random();

    public static String getRandomEsperantoWords() {
        String finalString = "";

        List<String> chosenWords = new ArrayList<String>();

        for (int i = 0; i < 20; i++) {
            int index = r.nextInt(randomWords.length);

            chosenWords.add(randomWords[index]);
        }

        for (String word : chosenWords) {
            finalString += word + " ";
        }

        return finalString;
    }
}
