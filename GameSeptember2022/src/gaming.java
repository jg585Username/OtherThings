//welcome to rock paper scissors game
import java.util.Scanner;
public class gaming {
    public static void main(String[] args) {
        
        System.out.println("How to play the game: Input 1 for ROCK, 2 for SCISSORS, 3 for PAPER");
        
        Scanner scan = new Scanner(System.in);
        
        int wins = 0;
        int losses = 0;   
        int draws = 0;
        int roundCount = 1;
        
        String opponentChoice = null;
        String yourChoice = null;
        double player;
        
        for (int i = 0; i < 1000000; i++){ //the user can play continuosly up to one million times (basically forever)
            int random = (int) (Math.random() * 3) + 1; //for the computer to choose rock or paper or scissors (computers start at zero as one, so + 1)
            System.out.println("Round" + "\t" + roundCount);
            System.out.println("Input your choice:");
            if (random == 1){
                 opponentChoice = "rock";
            }
            if (random == 2){
                 opponentChoice = "paper";
            }
            if (random == 3){
                 opponentChoice = "scissors";
            }
            
            while (true) {
            try {
                player = scan.nextDouble(); //player input
                break; //to prevent doing catch forever
            } 
            catch (Exception wordError) {
                scan.next();
                System.out.println("Invalid input, you are not supposed to input letters!");  //can handle errors with strings
                System.out.println("Try again!");
            }
            
            }
            if (player == 1){
                yourChoice = "rock";
            }
            else if (player == 2){
                yourChoice = "paper";
            }
            else if (player == 3){
                yourChoice = "scissors";
            }
            
            else if (player == 0){ //if player wants to stop playing
                System.out.println("Thanks for playing");
                break; //ends loop
            }

            else { //handles invalid input (this one doesnt handle strings)
                System.out.println("Invalid input");
                roundCount--;
            }
            
            if (player == random){
                System.out.println("Draw!");
                draws++;
                System.out.println("You choose:" +  yourChoice + "\t Opponent chooses:" + opponentChoice);
                System.out.println("Wins:" + wins + "\t Losses:" + losses + "\t Draws:" + draws); //wins and losses tracker
                
            }
            if (player == 1 && random == 3 || player == 2 && random == 1 || player == 3 && random ==2){
                System.out.println("You win!");
                wins++;
                System.out.println("You choose:" +  yourChoice + "\t Opponent chooses:" + opponentChoice);
                System.out.println("Wins:" + wins + "\t Losses:" + losses + "\t Draws:" + draws);
                winStreak++;
            }
            if (player == 3 && random == 1 || player == 1 && random == 2 || player == 2 && random == 3){
                System.out.println("You lose!");
                losses++;
                System.out.println("You choose:" +  yourChoice + "\t Opponent chooses:" + opponentChoice);
                System.out.println("Wins:" + wins + "\t Losses:" + losses + "\t Draws:" + draws);
                winStreak = 0;
            }
            if (winStreak > 2){
                System.out.println("Winstreak!:" + winStreak);
            }

            roundCount++;
            System.out.println("-----✊🏻✋🏻✌️-------✊🏻✋🏻✌️----------✊🏻✋🏻✌️------✊🏻✋🏻✌️");

        }
        
  
    }
    
}
