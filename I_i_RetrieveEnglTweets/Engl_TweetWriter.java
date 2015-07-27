/*
 * @(#)EnglishTweetWriter.java   1.0   Jun 01, 2015
 *
 * Copyright (c) 2011-2014 Portland State University.
 * Copyright (c) 2013-2014 University of Konstanz.
 *
 * This software is the proprietary information of the above-mentioned institutions.
 * Use is subject to license terms. Please refer to the included copyright notice.
 */
package kn.uni.inf.niagarino.mtsandbox;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;

import edu.pdx.cs.niagarino.PhysicalQueryPlan;
import edu.pdx.cs.niagarino.PhysicalQueryPlan.OperatorType;
import edu.pdx.cs.niagarino.operator.Derive;
import edu.pdx.cs.niagarino.operator.Operator;
//import edu.pdx.cs.niagarino.operator.Print;
import kn.uni.inf.niagarino.mtsandbox.Print;
import edu.pdx.cs.niagarino.operator.Scan;
import edu.pdx.cs.niagarino.operator.Selection;
import edu.pdx.cs.niagarino.stream.Attribute;
import edu.pdx.cs.niagarino.stream.Schema;
import kn.uni.inf.niagarino.operator.LambdaPredicate;
import kn.uni.inf.niagarino.util.LanguageDerivationFunction;
import kn.uni.inf.niagarino.util.StringFunction;

/**
 * Class for selecting english tweets and writing to disk.
 * 
 * @author Andreas Weiler &lt;andreas.weiler@uni.kn&gt;
 * @version 1.0
 *
 * Edited by Mathew Teoh, June-July 2015
 */
public class Engl_TweetWriter {
   
   /** Schema of tuples in test data set. */
   // I changed this so that the createion date output format would match the input for another script. 
   // This is the line that is different: new Attribute("creationdate", String.class)
   // Before, "String" was "Date".
  
   private static final Schema TWITTER_SCHEMA = new Schema(1, new Attribute("tweetid", Long.class), 
	         new Attribute("creationdate", String.class), new Attribute("content", String.class), new Attribute("retweeted",
	               Boolean.class), new Attribute("retweet_count", Integer.class), new Attribute("coords", String.class),
	               new Attribute("source", String.class), new Attribute("urls", String.class), new Attribute("media",
	               String.class), new Attribute("place", String.class), new Attribute("replytostatus",
	               String.class), new Attribute("replytouserid", String.class), new Attribute("replytousername",
	               String.class), new Attribute("user_id", Long.class), new Attribute("user_name", String.class),
	               new Attribute("user_screenname", String.class), new Attribute("user_creationdate", String.class),
	               new Attribute("user_language", String.class), new Attribute("user_statusescount", Integer.class),
	               new Attribute("user_followerscount", Integer.class), new Attribute("user_location", String.class),
	               new Attribute("user_description", String.class), new Attribute("user_friendscount", Integer.class),
	               new Attribute("user_timezone", String.class), new Attribute("user_listedcount", Integer.class),
	               new Attribute("user_utcoffset", String.class));
	   
	
   /** File that contains test data set. */ // not used here since we will construct the file name based on what appears in the range
//   private static final String STREAM_FILE = "/Users/internship/Desktop/internship/data/2012_04_01_01.zip";
//   private static final String STREAM_FILE = "/Users/internship/Desktop/internship/data/t.csv.zip";
//   private static final String STREAM_FILE = "/Users/internship/Desktop/internship/data/2015_05_30_20.zip";
   
   // i put this in the main method so that it can be done more than once in the same run
//   /** Stems for the input and output files*/
//   private static final String EVENT_stem = "boston_bombing";
//   private static final String DATE_stem = "2013_04_15_22";
////   private static final String STREAM_FILE_stem = "/Volumes/TwitterData/2013/2013_02/2013_02_15_";
//   private static final String STREAM_FILE_stem = "/Volumes/TwitterData/"+DATE_stem.substring(0,4)+"/"+DATE_stem.substring(0,7)+"/"+DATE_stem.substring(0,11);
////   private static final String OUTPUT_FILE_stem = "/Users/internship/Desktop/internship/chandanStuff/twitter-events/scripts/dest/chelyabinsk_meteor/2013_02_15_";
//   private static final String OUTPUT_FILE_stem = "/Users/internship/Desktop/internship/chandanStuff/twitter-events/scripts/dest/"+EVENT_stem+"/"+DATE_stem.substring(0,11); 
//   
//   /** Suffixes for the input and output files*/
//   private static int hour_stem = Integer.parseInt(DATE_stem.substring(11,13));
//   private static int startHour = hour_stem-1;
//   private static int endHour = hour_stem+1;
   
   
   /** Output file of method. */ // not used here since we will construct the file name based on what appears in the range
//  private static final String OUTPUT_FILE = "/Users/internship/Desktop/internship/data/2012_04_01_01_en.csv";
//   private static final String OUTPUT_FILE = "/Users/internship/Desktop/internship/data/t.csv";
//  private static final String OUTPUT_FILE = "/Users/internship/Desktop/internship/data/2015_05_30_20_en.csv";

   
   /**
    * Main method.
    * @param args arguments
    */
   public static void main(final String[] args) {
      try {
    	  long startTime = System.nanoTime();

        //  ==============================
    	  
    	  // The values below are MANUALLY set. Since this file will fetch English tweets, you will need to specify which files in the Twitter data storage
        // need to be accessed. The arrays below let you search tweets over multiple time intervals if you have more than one time interval you'd like to fetch 
        // English tweets. In each of the arrays below, the nth element describes the nth time interval you are searching over.

        // Suppose you know (for testing purposes) the actual event that occurred in the time period over which you are searching the tweets, you can put it
        // in eventArr. If not, just specify the date in the format "YYYY_MM_DD". The nth entry in eventArr will be the directory name containing the tweets of 
        // the nth time interval you are searching over. For example, "2015_06_01" means look at a time period within June 1 2015.
    	  String[] eventArr = {"2015_06_01"};

        // The dateArr array contains the specific hour (along with specific year, month, and day) of the time period over which you are fetching English tweets.
        // The format is "YYYY_MM_DD_HH". For example, "2015_06_01_02" means that the time interval of interest contains the second hour of June 1 2015.
    	  String[] dateArr = {"2015_06_01_02"};

        // What if you want your time interval to have multiple hours of tweets? Arrays hoursBefore and hoursAfter specify how many hours before and after 
        // the time specified in dateArr you would like to collect tweets. Following the example above, if you specify "1" in hoursBefore and 2 in hoursAfter,
        // you would be collecting tweets from the hours 01:00, 02:00, 03:00, 04:00 from the date June 1 2015.
        String[] hoursBefore = {"1"}
        String[] hoursAfter = {"2"}

        //  ==============================
    	  
    	  if(eventArr.length != dateArr.length){
    		  System.out.println("Warning: array lengths not equal!");
    	  }
    	  
    	  for(int eventInd = 0; eventInd < eventArr.length; eventInd++){
    		  final String EVENT_stem = eventArr[eventInd]; 
      	  final String DATE_stem = dateArr[eventInd]; 
        	  


          //  ==============================
      	  
          // The following are MANUALLY set
          // Depending on where the twitter data is stored, you may have to change this
      	  final String STREAM_FILE_stem = "/Volumes/TwitterData/"+DATE_stem.substring(0,4)+"/"+DATE_stem.substring(0,7)+"/"+DATE_stem.substring(0,11);
          // Depending on where you want the English tweets to be outputted, you can change this path
      	  final String OUTPUT_FILE_stem = "/Users/internship/Desktop/internship/SigmoidEventDetection/I_i_EnglTweet/"+EVENT_stem+"/"+DATE_stem.substring(0,11); 
          // This directory below needs to be the same as the directory part of OUTPUT_FILE_stem
          File eventDir=new File("/Users/internship/Desktop/internship/SigmoidEventDetection/I_i_EnglTweet/"+EVENT_stem);

          //  ==============================

          // Make sure the output directory exists
          eventDir.mkdir();

      	  /** Suffixes for the input and output files*/
      	  int hour_stem = Integer.parseInt(DATE_stem.substring(11,13));
      	  int startHour = hour_stem-Integer.parseInt(hoursBefore);
      	  int endHour = hour_stem+Integer.parseInt(hoursAfter);
      	  
      	  for(int fileHour = startHour; fileHour <= endHour; fileHour++){
      		  String fileHourStr = Integer.toString(fileHour);
      		  if(fileHour < 10){
      			  fileHourStr="0"+fileHourStr;
      		  }
      		  
      		  String STREAM_FILE = STREAM_FILE_stem + fileHourStr + ".zip";
    			  String OUTPUT_FILE = OUTPUT_FILE_stem + fileHourStr + "_en.csv";
    			  
    			  System.out.println("Input: "+ STREAM_FILE);
    			  System.out.println("Output: "+ OUTPUT_FILE);
      		  
      		  new Engl_TweetWriter(STREAM_FILE,OUTPUT_FILE);
        	  System.out.println("successful!");
        	  
        	  long endTime = System.nanoTime();
        	  System.out.println("Took "+(endTime - startTime) + " ns"); 
      	  }
    	  }
    	  
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   
   /**
    * Default constructor.
    */
   public Engl_TweetWriter(String STREAM_FILE, String OUTPUT_FILE) throws Exception {
	   // code that mathew entered
	  int counter=0;
	  System.out.println("Step "+ ++counter); // 1
	  
	  
      // scan file
      final Operator scan = new Scan(TWITTER_SCHEMA, STREAM_FILE, "\t");
      
      // code that mathew entered
      System.out.println("Step "+ ++counter); // 2
      
      // derive language
      final Operator languageDerivation = new Derive(scan.getOutputSchema(), new LanguageDerivationFunction(2));
      
      // code that mathew entered
      System.out.println("Step "+ ++counter); // 3
      
      // select only english
      StringFunction<Boolean> englishCheck = check -> { return check.equals("en"); };
      final Operator selectEnglish = new Selection(languageDerivation.getOutputSchema(), new LambdaPredicate(englishCheck, 26));
      
      // code that mathew entered
      System.out.println("Step "+ ++counter); // 4
      
      // print
      PrintStream output = new PrintStream(new File(OUTPUT_FILE));    	  
      
      final Operator printer = new Print(selectEnglish.getOutputSchema(), false, output);
      
      // code that mathew entered
      System.out.println("Step "+ ++counter); // 5
      
      // physical query plan
      final PhysicalQueryPlan plan = new PhysicalQueryPlan(false);
      plan.addOperator(scan, OperatorType.SOURCE);
      plan.addOperator(languageDerivation);
      plan.addOperator(selectEnglish);
      plan.addOperator(printer, OperatorType.SINK);
      // add streams
      plan.addStream(scan, languageDerivation);
      plan.addStream(languageDerivation, selectEnglish);
      plan.addStream(selectEnglish, printer);
      
      // code that mathew entered
      System.out.println("Step "+ ++counter); // 6
      
      // execute plan
      plan.execute();
      
      
   }
}
