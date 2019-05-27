//Amit Charran
//Emergency Room Triage

//This project identifies the conditions in patients in a hospital (probably the ER)
//Then identifies which patients should be treated first under the "Triage Level" conditions that is given

// Maybe I'll add some future updates
// Some Ideas, create separate files
// Need to add some data structure to store patients, HashMap seems good for searching patients
// chainedHashMap is also pretty good
// maybe a Tree can work

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
public class EmergengyRoomTriage {
    //Use these to determine if any of these files are missing
    static boolean cardiacFile = true;
    static boolean cancerFile = true;
    static boolean neuroFile = true;
    static Instant start = Instant.now();
    static Instant end;

    public static void main(String[] args)
    {
        //create ArrayLists to hold information from file
        ArrayList<Patient> patients = new ArrayList<>();
        ArrayList<String> cardiacMeds = new ArrayList<>();
        ArrayList<String> cancerMeds = new ArrayList<>();
        ArrayList<String> neuroMeds = new ArrayList<>();

        //create variables to hold the file
//        File patientFile = new File(args[0]);
//        File cardiacMedsFile = new File(args[1]);
//        File cancerMedsFile = new File(args[2]);
//        File neuroMedsFile = new File(args[3]);

        // Use this if I am not using command line for output
        File patientFile = new File("patient.txt");
        File cardiacMedsFile = new File("cardiac.txt");
        File cancerMedsFile = new File("cancer.txt");
        File neuroMedsFile = new File("neuro.txt");


        //This methods places the info from the file into the ArrayLists
        infoFromFileToArrayList(patients, cardiacMeds, cancerMeds, neuroMeds,
                patientFile,cardiacMedsFile,cancerMedsFile,neuroMedsFile);

        //This method creates an ArrayList with the function
        //It sorts the patients depending on how severe the patients are
        //If the patient has a Cardiac Arrest, they immediately go to the front of the list
        ArrayList<Patient> finalList = priorityAlgorithm(patients,cardiacMeds,cancerMeds,neuroMeds);



        //This prints the patients to a file, numbering them from 1 to x(number of patients)
        //One is the most severe and x is the least severe
        for(int i = 0; i < finalList.size(); i++){
            end = Instant.now();
            File file = new File(String.valueOf(i + 1) + ".txt");
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(finalList.get(i).toString());
                //Print to file which files are missing
                if(cardiacFile == false){
                    writer.write("\n**cardiac.txt Not Found\n");
                }
                if(!cancerFile){
                    writer.write("\n**cancer.txt Not Found\n");
                }
                if(!neuroFile){
                    writer.write("\n**neuro.txt Not Found\n");
                }

                writer.close();
            }catch(Exception e){
                System.out.println(e.getMessage());
            }
        }


    }

    public static void infoFromFileToArrayList(ArrayList<Patient> patients, ArrayList<String> cardiacMeds, ArrayList<String> cancerMeds,
                             ArrayList<String> neuroMeds, File patientFile, File cardiacMedsFile, File cancerMedsFile,
                                    File neuroMedsFile){
        //create each individual file reader variables for the file I will use
        FileReader patientFileReader = null;
        FileReader cardiacFileReader = null;
        FileReader cancerFileReader = null;
        FileReader neuroFileReader = null;


        //Match files with the file reader
        try {
            patientFileReader = new FileReader(patientFile);
        }catch(FileNotFoundException e){
            System.out.println("Patient file " + e.getMessage());
            try{
            FileWriter writer = new FileWriter("MissingPatientFile.txt");
            writer.write("Cannot Find patient.txt File");
            }
            catch(Exception ex){
                System.out.println(ex.getMessage() + "----- Patient File missing");
            }
        }
        //If the files (except for patient.txt) is not found the code will add a nothing file
        //use that instead
        try{
            cardiacFileReader = new FileReader(cardiacMedsFile);
        }catch(FileNotFoundException e){
            cardiacFile = false;
            System.out.println("caridac.txt Not Found");
            cardiacFileReader = fileNotfound();
        }
        try {
            cancerFileReader = new FileReader(cancerMedsFile);
        }catch(FileNotFoundException e){
            System.out.println("cancer.txt Not Found");
            cancerFile = false;
            cancerFileReader = fileNotfound();
        }
        try {
            neuroFileReader = new FileReader(neuroMedsFile);
        }catch(FileNotFoundException e){
            System.out.println("neuro.txt Not Found");
            neuroFile = false;
            neuroFileReader = fileNotfound();
        }



        try{
            //use buffered reader to read from file line by line
            BufferedReader patientBufferedReader = new BufferedReader(patientFileReader);
            BufferedReader cardiacBufferedReader = new BufferedReader(cardiacFileReader);
            BufferedReader cancerBufferedReader = new BufferedReader(cancerFileReader);
            BufferedReader neuroBufferedReader = new BufferedReader(neuroFileReader);

            //Stores each line from the file
            String line;
            //reading from patient file

            while((line = patientBufferedReader.readLine()) != null){
                patients.add(lineToPatient(line));
            }
            //Meds
            while((line = cardiacBufferedReader.readLine()) != null){
                cardiacMeds.add(line);
            }
            while((line = cancerBufferedReader.readLine()) != null){
                cancerMeds.add(line);
            }
            while((line = neuroBufferedReader.readLine()) != null){
                neuroMeds.add(line);
            }

            //close all the readers
            patientFileReader.close();
            cancerFileReader.close();
            cardiacFileReader.close();
            neuroFileReader.close();
            patientBufferedReader.close();
            cancerBufferedReader.close();
            cardiacBufferedReader.close();
            neuroBufferedReader.close();

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    //Creates a random file if certain files are not found (cardiac.txt, cancer.txt, neuro.txt)
    public static FileReader fileNotfound(){
        FileReader ans = null;
        try{
        FileWriter writer = new FileWriter("nothing.txt");
        writer.write("");
        writer.close();
        ans = new FileReader("nothing.txt");

    }catch(Exception ex) {
        System.out.println("ex" + ex.getMessage());
    }
        return ans;

    }

    //Creating objects of Patient class and putting it into an array
    public static Patient lineToPatient(String line){

        Patient patient = new Patient();
        //puts all the data in the lines in the correct format for the patient class
        ArrayList<String> info = new ArrayList<String>(Arrays.asList(line.split(",")));
        for(int i = 0;i < info.size(); i++){
            info.set(i, info.get(i).trim());
        }

        patient.name = info.get(0);
        patient.age = Integer.valueOf(info.get(1));
        patient.gender = info.get(2);
        patient.complaint = info.get(3);
        patient.alertnessLevel = info.get(4).charAt(0);
        patient.heartRate = Double.valueOf(info.get(5));
        patient.bloodPressure = info.get(6);
        patient.respirationRate = Integer.valueOf(info.get(7));
        patient.temperature = Double.valueOf(info.get(8));
        String oxygenS = info.get(9);
        oxygenS = oxygenS.replace("%","");
        patient.oxygenSaturation = Integer.valueOf(oxygenS);
        if(!info.get(10).equals("-"))
        patient.painLevel = Integer.valueOf(info.get(10));

        StringBuilder meds = new StringBuilder("");
        for(int i = 11; i < info.size(); i++){
            if(i != info.size() -1) {
                meds.append(info.get(i) + ",");
            }else{
                meds.append(info.get(i));
            }
        }
        patient.medication = meds.toString();


        return patient;
    }


    //This function does most of the work
    //It shows which patients should go first, depending on how dire their condition is
    public static ArrayList<Patient> priorityAlgorithm(ArrayList<Patient> patients, ArrayList<String> cardiacMeds,
                                                ArrayList<String> cancerMeds, ArrayList<String> neuroMeds){
        ArrayList<Patient> ansList = new ArrayList<>();

        //Adds patients to list. PriorityNumber == Triage Level
        ArrayList<Patient> priority1 = inPriority1(patients);
        ArrayList<Patient> priority2 = inPriority2(patients);
        ArrayList<Patient> priority3 = inPriority3(patients);

        //gives the patients in each list a number (higher the number the more dire their situation)
        ArrayList<Integer> priority1Number = priorityNumber(priority1);
        ArrayList<Integer> priority2Number = priorityNumber(priority2);
        ArrayList<Integer> priority3Number = priorityNumber(priority3);

        //Sorts each list from most severe to least severe
        reOrderArray(priority1, priority1Number);
        reOrderArray(priority2, priority2Number);
        reOrderArray(priority3, priority3Number);


        //adds people from Triage level 1 first then people from Triage level 2... to the final list
        for(int i = 0; i < priority1.size(); i++){
            ansList.add(priority1.get(i));
        }
        for(int i = 0; i < priority2.size(); i++){
            ansList.add(priority2.get(i));
        }
        for(int i = 0; i < priority3.size(); i++){
            ansList.add(priority3.get(i));
        }


        //Check if any patients need a specialized doctor
        //This function is for output to file
        for(int i = 0; i < ansList.size(); i++) {
            checkSpecializedDoctor(ansList.get(i), cardiacMeds, cancerMeds, neuroMeds);
        }

        return ansList;
    }

    //matches patients to their correct specialized doctors
    public static void checkSpecializedDoctor(Patient patient,ArrayList<String> cardiacMeds,
                                       ArrayList<String> cancerMeds, ArrayList<String> neuroMeds){
       ArrayList<String> medList = patient.medList();
       if(medList.isEmpty()){
           return;
       }

       for(int i = 0; i < medList.size(); i++){
          if(cancerMeds.contains(medList.get(i))){
              patient.oncologist = true;
          }
          if(cardiacMeds.contains(medList.get(i))){
              patient.cardiologist = true;
          }
          if(neuroMeds.contains(medList.get(i))){
              patient.neurologist = true;
          }
       }

    }

    //Can maybe use a set instead of parallel arrays
    //Sorts the array so that patients in more dire situation is on top of the list
    public static void reOrderArray(ArrayList<Patient> patient, ArrayList<Integer> priorityNumber){
        //Do a better sorting later, either merge or quick
        for(int i = 0; i < priorityNumber.size(); i++){
            for(int j = 0; j < priorityNumber.size(); j++){
                if(priorityNumber.get(i) > priorityNumber.get(j)){
                    int t = priorityNumber.get(i);
                    priorityNumber.set(i, priorityNumber.get(j));
                    priorityNumber.set(j, t);

                    Patient p = patient.get(i);
                    patient.set(i, patient.get(j));
                    patient.set(j, p);
                }
            }
        }

    }

    //gives each patient a priority number. Higher the number the more dire their situation
    //If they have a cardiac arrest they are immediately at the top of the list b/c cardiac arrest is
    // practically near death
    public static ArrayList<Integer> priorityNumber(ArrayList<Patient> priorityList){
        ArrayList<Integer> answer = new ArrayList<>();
        for(int i = 0; i < priorityList.size(); i++){
            int num = 0;
            num = generatePriorityNumber(priorityList.get(i));
            answer.add(i,num);
        }
        return answer;
    }

    public static int generatePriorityNumber(Patient patient){
        int answer = 0;
        //temperature
        if(patient.temperature > 105){
            answer = answer + 3;
        }
        else {
            answer = answer + 1;
        }

        //oxygen saturation
        if(patient.oxygenSaturation < 90){
            answer = answer+ 3;
        }
        else if(patient.oxygenSaturation >= 90 && patient.oxygenSaturation < 95){
            answer = answer + 2;
        }
        else {
            answer = answer + 1;
        }

        //respiration rate
        if(patient.respirationRate > 6){
            answer = answer + 3;
        }
        else if(patient.respirationRate > 20 || patient.respirationRate < 12){
            answer = answer + 2;
        }
        else {
            answer = answer + 1;
        }

        //alertness level
        if(patient.alertnessLevel == 'U'){
            answer = answer + 3;
        }
        else if(patient.alertnessLevel == 'P' || patient.alertnessLevel == 'V'){
            answer = answer + 2;
        }
        else {
            answer = answer + 1;
        }

        //heart rate
        if(patient.heartRate == 0){
            answer = answer + 1000;
        }
        else if(patient.heartRate < 60 || patient.heartRate > 100){
            answer = answer + 2;
        }
        else {
            answer = answer + 1;
        }

        //Blood Pressure
        ArrayList<Integer> bloodPressure = BPtoArrayList(patient);
        if(bloodPressure.get(0) < 90 || bloodPressure.get(1) < 60){
            answer = answer + 3;
        }
        else if(bloodPressure.get(0) >= 140 || bloodPressure.get(1) >= 90){
            answer = answer + 2;
        }
        else {
            answer = answer + 1;
        }
        return answer;
    }

    //Creates list of patients that belongs in Triage level 1
    public static ArrayList<Patient> inPriority1(ArrayList<Patient> patients){
        ArrayList<Patient> patientsInPriority1 = new ArrayList<>();
        for(int i = 0; i < patients.size(); i++){
            if(hasPriority1(patients.get(i))){
                patients.get(i).triageLevel = 1;
                patientsInPriority1.add(patients.get(i));
            }
        }
        return patientsInPriority1;
    }
    //Creates list of patients that belongs in Triage level 2
    public static ArrayList<Patient> inPriority2(ArrayList<Patient> patients){
        ArrayList<Patient> patientsInPriority2 = new ArrayList<>();
        for(int i = 0; i < patients.size(); i++){
            if(hasPriority2(patients.get(i)) && !hasPriority1(patients.get(i))){
                patients.get(i).triageLevel = 2;
                patientsInPriority2.add(patients.get(i));
            }
        }
        return patientsInPriority2;
    }
    //Creates list of patients that belongs in Triage level 1
    public static ArrayList<Patient> inPriority3(ArrayList<Patient> patients){
        ArrayList<Patient> patientsInPriority3 = new ArrayList<>();
        for(int i = 0; i < patients.size(); i++){
            if(!hasPriority2(patients.get(i)) && !hasPriority1(patients.get(i))){
                patients.get(i).triageLevel = 3;
                patientsInPriority3.add(patients.get(i));
            }
        }
        return patientsInPriority3;
    }

    //Identifies if a patient belongs in Triage level 1
    public static boolean hasPriority1(Patient patient){
        if(patient.temperature > 105){
            return true;
        }
        if(patient.oxygenSaturation < 90){
            return true;
        }
        if(patient.respirationRate < 6){
            return true;
        }
        if(patient.alertnessLevel == 'U'){
            return true;
        }
        //need to do cardiac arrest, this is heart rate
        //cardiac arrest if heart stops beating
        if(patient.heartRate == 0){
            return true;
        }

        //hypotensive this is low blood pressure
        // BP is below 90/**** || ***/60
        ArrayList<Integer> bloodPressure = BPtoArrayList(patient);
        if(bloodPressure.get(0) < 90 || bloodPressure.get(1) < 60){
            return true;
        }

        return false;
    }

    //Identifies if a patient belongs in Triage level 2
    public static boolean hasPriority2(Patient patient){
        if(hasPriority1(patient)) return false;
        if(patient.alertnessLevel == 'P' || patient.alertnessLevel == 'V'){
            return true;
        }
        if(patient.oxygenSaturation >= 90 && patient.oxygenSaturation < 95){
            return true;
        }
        //heart rate < 60  == brady
        //heart rate > 100 == tachy
        if(patient.heartRate < 60 || patient.heartRate > 100){
            return true;
        }
        //respiration rate for tachy or brady
        //respiration rate < 12 == brady
        //respirtation rate > 20 == tachy
        if(patient.respirationRate < 12 || patient.respirationRate >20){
            return true;
        }

        //Hypertensive if Blood Pressure is more than 140/***  or ***/90.
        //Note pre-hypertention is 120-139/*** or 80/89
        ArrayList<Integer> bloodPressure = BPtoArrayList(patient);
        if(bloodPressure.get(0) >= 140 || bloodPressure.get(1) >= 90){
            return true;
        }
        return false;
    }

    //Identifies if a patient belongs in Triage level 3
    public static boolean hasPriority3(Patient patient){
        if(hasPriority1(patient) || hasPriority2(patient)){
            return false;
        }
        return true;
    }
    public static ArrayList<Integer> BPtoArrayList(Patient patient){
        ArrayList<String> c = new ArrayList<String>(Arrays.asList(patient.bloodPressure.split("/")));
        ArrayList<Integer> ans = new ArrayList<>();

        ans.add(Integer.parseInt(c.get(0)));
        ans.add(Integer.parseInt(c.get(1)));

        return ans;
    }

    //Patient class
    public static class Patient{
        protected String name;
        protected int age;
        protected String gender;
        protected String complaint;
        protected char alertnessLevel;
        protected double heartRate;
        protected String bloodPressure;
        protected double respirationRate;
        protected double temperature;
        protected int oxygenSaturation;
        protected int painLevel;
        protected String medication;
        protected boolean cardiologist;
        protected boolean oncologist;
        protected boolean neurologist;
        protected int triageLevel;

        public Patient(){
            this.name = "No name";
            this.age = -1;
            this.gender = "No gender specified";
            this.complaint = "No complaint specified";
            this.alertnessLevel = '\0';
            this.heartRate = -1;
            this.bloodPressure = "No blood pressure specified";
            this.respirationRate = -1;
            this.temperature = -1;
            this.oxygenSaturation = -1;
            this.painLevel = -1;
            this.medication = null;
            cardiologist = false;
            oncologist = false;
            neurologist = false;
            triageLevel = 0;
        }

        public Patient(String name, int age, String gender, String complaint, char alertnessLevel,
                       double heartRate, String bloodPressure, double respirationRate, double temperature,
                       int oxygenSaturation, int painLevel, String medication){
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.complaint = complaint;
            this.alertnessLevel = alertnessLevel;
            this.heartRate = heartRate;
            this.bloodPressure = bloodPressure;
            this.respirationRate = respirationRate;
            this.temperature = temperature;
            this.oxygenSaturation = oxygenSaturation;
            this.painLevel = painLevel;
            this.medication = medication;
            cardiologist = false;
            oncologist = false;
            neurologist = false;
            triageLevel = 0;

        }

        //get items in a medication and place them into an ArrayList
        public ArrayList<String> medList(){

            if(medication.equals("-")){
                return new ArrayList<>();
            }
            ArrayList<String> med = new ArrayList<String>(Arrays.asList(medication.split(",")));
            for(int i  = 0; i  < med.size(); i++){
                med.set(i,med.get(i).trim());
            }

            return med;

        }

        //Print function
        public String toString(){
            StringBuilder output= new StringBuilder();
            output.append(name + "\n" + age + ", " + gender +", " + complaint);

            output.append("\n" + "Triage Level: " +  triageLevel);

            String doctor = doctorOutput();
            if(!doctor.equals("-")){
                output.append("\n" + doctor);
            }
            long timeElapsed = Duration.between(start,end).toMillis();
            output.append("\nTime Spent Waiting: " + timeElapsed + "ms");

            String heartBeatOutPut = heartBeatOutput();
            output.append("\n" + heartBeatOutPut);

            String bloodPressureOutPut = bloodPressureOutput();
            output.append("\n" + bloodPressureOutPut);

            String respirationRateOutPut = respirationRateOutput();
            output.append("\n" + respirationRateOutPut);

            String temperatureOutPut = temperatureOutput();
            output.append("\n" + temperatureOutPut);

            String oxygenSaturationOutPut = oxygenSaturationOutput();
            output.append("\n" + oxygenSaturationOutPut);

            output.append("\n" +  medication);

            return output.toString();
        }

        public String oxygenSaturationOutput(){
            if(oxygenSaturation <= 95){
                return oxygenSaturation + "% Low";
            }else{
                return oxygenSaturation + "% Normal";
            }
        }

        public String temperatureOutput(){
            if(temperature >= 100.4){
                return temperature + " Yes";
            }else {
                return temperature + " No";
            }
        }

        public String bloodPressureOutput(){
            ArrayList<String> c = new ArrayList<String>(Arrays.asList(bloodPressure.split("/")));
            String ans = "";
            ArrayList<Integer> c2 = new ArrayList<>();
            c2.add(Integer.parseInt(c.get(0)));
            c2.add(Integer.parseInt(c.get(1)));

            if(c2.get(0) < 90 || c2.get(1) < 60){
                ans = bloodPressure + " Hypotensive";
            }
            else if(c2.get(0) >= 140 || c2.get(1) >= 90){
                ans = bloodPressure + " Hypertensive";
            }
            else {
                ans = "" + bloodPressure;
            }

            return ans;

        }

        public String respirationRateOutput(){
            String ans = "";

            if(respirationRate < 12){
                ans = (int)respirationRate + " Bradypnea";
            }
            else if(respirationRate > 20){
                ans = (int)respirationRate + " Tachypnea";
            } else {
                ans = "" + (int)respirationRate;
            }

            return ans;

        }

        public String heartBeatOutput(){
            String ans = "";
            if(heartRate == 0){
                ans = "" +(int)heartRate + " Cardiac Arrest";
            }
            else if(heartRate< 60 ){
                ans = "" +(int)heartRate + " Bradycardia";

            }
            else if(heartRate > 100){
                ans = "" +(int)heartRate + " Tachycardia";
            }
            else {
                ans = "" + (int)heartRate;
            }

            return ans;
        }
        public String doctorOutput(){
            if(cardiologist && neurologist && oncologist){
                return "Cardiologist, Neurologist, Oncologist";
            }
            if(cardiologist && neurologist){
                return "Cardiologist, Neurologist";
            }
            if(cardiologist && oncologist){
                return "Cardiologist, Neurologist";
            }
            if(neurologist && oncologist){
                return "Neurologist, Oncologist";
            }
            if(cardiologist){
                return "Cardiologist";
            }
            if (neurologist){
                return "Neurologist";
            }
            if (oncologist){
                return "Oncologist";
            }

            return "-";
        }


        //setters and getters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getComplaint() {
            return complaint;
        }

        public void setComplaint(String complaint) {
            this.complaint = complaint;
        }

        public char getAlertnessLevel() {
            return alertnessLevel;
        }

        public void setAlertnessLevel(char alertnessLevel) {
            this.alertnessLevel = alertnessLevel;
        }

        public double getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(double heartRate) {
            this.heartRate = heartRate;
        }

        public String getBloodPressure() {
            return bloodPressure;
        }

        public void setBloodPressure(String bloodPressure) {
            this.bloodPressure = bloodPressure;
        }

        public double getRespirationRate() {
            return respirationRate;
        }

        public void setRespirationRate(double respirationRate) {
            this.respirationRate = respirationRate;
        }

        public double getTemperature() {
            return temperature;
        }

        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        public int getOxygenSaturation() {
            return oxygenSaturation;
        }

        public void setOxygenSaturation(int oxygenSaturation) {
            this.oxygenSaturation = oxygenSaturation;
        }

        public int getPainLevel() {
            return painLevel;
        }

        public void setPainLevel(int painLevel) {
            this.painLevel = painLevel;
        }

        public String getMedication() {
            return medication;
        }

        public void setMedication(String medication) {
            this.medication = medication;
        }

        public boolean isCardiologist() {
            return cardiologist;
        }

        public void setCardiologist(boolean cardiologist) {
            this.cardiologist = cardiologist;
        }

        public boolean isOncologist() {
            return oncologist;
        }

        public void setOncologist(boolean oncologist) {
            this.oncologist = oncologist;
        }

        public boolean isNeurologist() {
            return neurologist;
        }

        public void setNeurologist(boolean neurologist) {
            this.neurologist = neurologist;
        }

        public int getTriageLevel() {
            return triageLevel;
        }

        public void setTriageLevel(int triageLevel) {
            this.triageLevel = triageLevel;
        }
    }
}
