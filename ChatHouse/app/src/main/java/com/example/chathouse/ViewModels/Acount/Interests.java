package com.example.chathouse.ViewModels.Acount;

import java.util.ArrayList;

public class Interests {
    public enum Wellness
    {
        Nutrition ("Nutrition", 1),
        Outdoors  ("Outdoors", 2 << 0),
        Weights  ("Weights", 2 << 1),
        Veganism  ("Veganism", 2 << 2),
        Meditation  ("Meditation", 2 << 3),
        Fitness  ("Fitness", 2 << 4),
        Health  ("Health", 2 << 5),
        Psychedelics  ("Psychedelics", 2 << 6),
        Mindfulness  ("Mindfulness", 2 << 7),
        Medicine  ("Medicine", 2 << 8);

        private String stringValue;
        private int intValue;
        private Wellness(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Wellness X: Interests.Wellness.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }


    }

    public enum Identity
    {
        BIPOC ("BIPOC", 1),
        Black  ("Black", 2 << 0),
        BabyBoomers  ("BabyBoomers", 2 << 1),
        Latino  ("Latino", 2 << 2),
        Disabled  ("Disabled", 2 << 3),
        SouthAsian  ("SouthAsian", 2 << 4),
        GenX  ("GenX", 2 << 5),
        Indigenous  ("Indigenous", 2 << 6),
        EastAsian  ("EastAsian", 2 << 7),
        GenZ  ("GenZ", 2 << 8),
        LGBTQ ("LGBTQ", 2 << 9),
        Millennials ("Millennials", 2 << 10);

        private String stringValue;
        private int intValue;
        private Identity(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Identity X: Interests.Identity.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }

    }

    public enum Places
    {
        Paris ("Paris", 1),
        London  ("London", 2 << 0),
        LosAngeles  ("LosAngeles", 2 << 1),
        Nigeria  ("Nigeria", 2 << 2),
        Africa  ("Africa", 2 << 3),
        Atlanta  ("Atlanta", 2 << 4),
        India  ("India", 2 << 5),
        SanFrancisco  ("SanFrancisco", 2 << 6),
        NewYork  ("NewYork", 2 << 7);

        private String stringValue;
        private int intValue;
        private Places(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Places X: Interests.Places.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum WorldAffairs
    {
        SocialIssues ("Social Issues", 1),
        Climate  ("Climate", 2 << 0),
        CurrentEvents  ("CurrentEvents", 2 << 1),
        Economics  ("Economics", 2 << 2),
        Markets  ("Markets", 2 << 3),
        Geopolitics  ("Geopolitics", 2 << 4),
        US_Politics  ("US Politics", 2 << 5);

        private String stringValue;
        private int intValue;
        private WorldAffairs(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.WorldAffairs X: Interests.WorldAffairs.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum Tech
    {
        AI ("AI", 1),
        DTC  ("DTC", 2 << 0),
        SaaS  ("SaaS", 2 << 1),
        Startups  ("Startups", 2 << 2),
        Engineering  ("Engineering", 2 << 3),
        AngelInvesting  ("Angel Investing", 2 << 4),
        VentureCapital  ("Venture Capital", 2 << 5),
        Product ("Product", 2 << 6),
        Crypto ("Crypto", 2 << 7),
        Marketing ("Marketing", 2 << 8),
        VR_AR ("VR AR", 2 << 9);

        private String stringValue;
        private int intValue;
        private Tech(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Tech X: Interests.Tech.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum HangingOut
    {
        Coworking ("Coworking", 1),
        Welcome_Newbies  ("Welcome Newbies", 2 << 0),
        LateNight  ("LateNight", 2 << 1),
        Bring_A_Drink  ("Bring A Drink", 2 << 2),
        MeetPeople  ("Meet People", 2 << 3),
        PTR  ("PTR", 2 << 4),
        ChillVibes  ("Chill Vibes", 2 << 5),
        wtf  ("wtf", 2 << 6);

        private String stringValue;
        private int intValue;
        private HangingOut(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.HangingOut X: Interests.HangingOut.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum KnowLedge
    {
        Education ("Education", 1),
        Physics  ("Physics", 2 << 0),
        Science  ("Science", 2 << 1),
        Psychology  ("Psychology", 2 << 2),
        Math  ("Math", 2 << 3),
        Biology  ("Biology", 2 << 4),
        TheFuture  ("The Future", 2 << 5),
        Philosophy  ("Philosophy", 2 << 6),
        History  ("History", 2 << 7),
        Space  ("Space", 2 << 8),
        Covid_19  ("Covid 19", 2 << 9);

        private String stringValue;
        private int intValue;
        private KnowLedge(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.KnowLedge X: Interests.KnowLedge.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum Hustle
    {
        ClubHouse ("ClubHouse", 1),
        PitchPractice  ("Pitch Practice", 2 << 0),
        Entrepreneurship  ("Entrepreneurship", 2 << 1),
        Stocks  ("Stocks", 2 << 2),
        RealEstate  ("Real Estate", 2 << 3),
        TikTok  ("TikTok", 2 << 4),
        Networking  ("Networking", 2 << 5),
        SmallBusiness  ("Small Business", 2 << 6),
        Instagram  ("Instagram", 2 << 7);

        private String stringValue;
        private int intValue;
        private Hustle(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Hustle X: Interests.Hustle.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum Sports
    {
        Basketball ("Basketball", 1),
        Football  ("Football", 2 << 0),
        Golf  ("Golf", 2 << 1),
        MMA  ("MMA", 2 << 2),
        Cycling  ("Cycling", 2 << 3),
        Baseball  ("Baseball", 2 << 4),
        Cricket  ("Cricket", 2 << 5),
        Tennis  ("Tennis", 2 << 6),
        Soccer  ("Soccer", 2 << 7),
        Formula1  ("Formula1", 2 << 8);

        private String stringValue;
        private int intValue;
        private Sports(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Sports X: Interests.Sports.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum Arts
    {
        Art ("Art", 1),
        Food_Drink  ("Food Drink", 2 << 0),
        Design  ("Design", 2 << 1),
        Books  ("Books", 2 << 2),
        Advertising  ("Advertising", 2 << 3),
        Dance  ("Dance", 2 << 4),
        Photography  ("Photography", 2 << 5),
        Sci_Fi  ("Sci_Fi", 2 << 6),
        Architecture  ("Architecture", 2 << 7),
        Beauty  ("Beauty", 2 << 8),
        Theater  ("Theater", 2 << 9),
        Fashion  ("Fashion", 2 << 10),
        Writing  ("Writing", 2 << 11),
        BurningMan  ("BurningMan", 2 << 12);

        private String stringValue;
        private int intValue;
        private Arts(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Arts X: Interests.Arts.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum Life
    {
        Grownuping ("Grownuping", 1),
        Support  ("Support", 2 << 0),
        Traveling  ("Traveling", 2 << 1),
        Weddings  ("Weddings", 2 << 2),
        Pregnancy  ("Pregnancy", 2 << 3),
        Relationships  ("Relationships", 2 << 4),
        Dating  ("Dating", 2 << 5),
        Parenting ("Parenting", 2 << 6);

        private String stringValue;
        private int intValue;
        private Life(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Life X: Interests.Life.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }

    }

    public enum Languages
    {
        Russian ("Russian", 1),
        Hindi  ("Hindi", 2 << 0),
        Indonesian  ("Indonesian", 2 << 1),
        German  ("German", 2 << 2),
        Spanish  ("Spanish", 2 << 3),
        Arabic  ("Arabic", 2 << 4),
        Portuguese  ("Portuguese", 2 << 5),
        French  ("French", 2 << 6),
        Mandarin  ("Mandarin", 2 << 7),
        Japanese  ("Japanese", 2 << 8);

        private String stringValue;
        private int intValue;
        private Languages(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }

        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Languages X: Interests.Languages.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum Entertainment
    {
        Performance ("Performance", 1),
        Music  ("Music", 2 << 0),
        Television  ("Television", 2 << 1),
        Podcasts  ("Podcasts", 2 << 2),
        Gaming  ("Gaming", 2 << 3),
        Fun  ("Fun", 2 << 4),
        Karaoke  ("Karaoke", 2 << 5),
        Variety  ("Variety", 2 << 6),
        Advice  ("Advice", 2 << 7),
        Anime_and_Manga  ("Anime_and_Manga", 2 << 8),
        Movies  ("Movies", 2 << 9),
        Trivia  ("Trivia", 2 << 10),
        Celebrities  ("Celebrities", 2 << 11),
        Storytelling  ("Storytelling", 2 << 12),
        Comedy ("Comedy", 2 << 13);

        private String stringValue;
        private int intValue;
        private Entertainment(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Entertainment X: Interests.Entertainment.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }

    public enum Faith
    {
        Islam ("Islam", 1),
        Buddhism  ("Buddhism", 2 << 0),
        Hinduism  ("Hinduism", 2 << 1),
        Christianity  ("Christianity", 2 << 2),
        Sprituality  ("Sprituality", 2 << 3),
        Taoism  ("Taoism", 2 << 4),
        Atheism  ("Atheism", 2 << 5),
        Sikhism  ("Sikhism", 2 << 6),
        Judaism  ("Judaism", 2 << 7),
        Angosticism  ("Angosticism", 2 << 8);

        private String stringValue;
        private int intValue;
        private Faith(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
        public static ArrayList<String> getArrayString(){
            ArrayList<String> test = new ArrayList<>();
            for (Interests.Faith X: Interests.Faith.values()
            ) {
                test.add(X.toString());
            };

            return test;
        }
    }
}
