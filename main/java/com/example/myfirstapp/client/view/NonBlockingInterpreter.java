package com.example.myfirstapp.client.view;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.net.InetSocketAddress;

import com.example.myfirstapp.client.net.ServerConnection;
import com.example.myfirstapp.client.net.CommunicationListener;
import com.example.myfirstapp.common.MessageException;
import java.util.logging.Level;
import java.util.logging.Logger;
import android.widget.Button;

/**
 * Reads and interprets user commands. The command interpreter will run in a separate thread, which
 * is started by calling the <code>start</code> method. Commands are executed in a thread pool, a
 * new prompt will be displayed as soon as a command is submitted to the pool, without waiting for
 * command execution to complete.
 */
public class NonBlockingInterpreter extends AppCompatActivity {

    private boolean receivingCmds = false;
    private ServerConnection server;

    TextView textView;
    Button joinButton;
    Button rockButton;
    Button paperButton;
    Button scissorButton;

    /**
     * Starts the interpreter. The interpreter will be waiting for user input when this method
     * returns. Calling <code>start</code> on an interpreter that is already started has no effect.
     */

    public NonBlockingInterpreter(TextView view, Button button /*, Button rock, Button paper, Button scissor*/) {
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        textView = view;
        joinButton = button;
        /* rockButton = rock;
        paperButton = paper;
        scissorButton = scissor;
        */
        server = new ServerConnection();
    }

    /**
     * Interprets and performs user commands.
     */
    public void nextCall(String msg) {
            try {
                UserInput userInput = new UserInput(msg);

                switch (userInput.getCmd()) {
                    case QUIT:
                        receivingCmds = false;
                        server.disconnect();
                        break;
                    case CONNECT:
                        server.addCommunicationListener(new ConsoleOutput());
                        server.connect(userInput.getParameter(0),
                                      Integer.parseInt(userInput.getParameter(1)));
                        break;
                    case USER:
                        server.sendUsername(userInput.getParameter(0));
                        break;
                    case JOIN:
                        server.sendJoin();
                        break;
                    case PLAY:
                        String choice = userInput.getParameter(0);
                            server.sendPlay(choice);
                            /*
                            rockButton.setVisibility(View.INVISIBLE);
                            paperButton.setVisibility(View.INVISIBLE);
                            scissorButton.setVisibility(View.INVISIBLE);
                            */
                        break;
                    default:
                        System.out.println("No such command!");
                }
            }  catch (NullPointerException npe) {

                System.out.println("To few arguments in request");
            }
            catch (Exception e) {
                System.out.println("Operation failed");
            }
    }

    private class ConsoleOutput implements CommunicationListener {
        
        @Override 
        public void recvdMsg(String msg){
            printToConsole(msg);
        }
        
        @Override
        public void connected(InetSocketAddress serverAddress){
            printToPhone("Connected to " + serverAddress.getHostName() + ":" + serverAddress.getPort());
        }
        
        @Override
        public void disconnected(){
            printToPhone("Disconnected from server.");
        }
        
        private void printToConsole(String msg) throws MessageException {

            String[] info = msg.split("##");
            try {
                if(info[1].compareTo("WAITCONNECT") == 0) {
                    printToPhone("Waiting for players to join game..");
                    //joinButton.setVisibility(View.INVISIBLE);

                }
                else if(info[1].compareTo("WAITROUND") == 0){
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NonBlockingInterpreter.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    printToPhone("Waiting for other players to make their choice..");
                }

                else if(info[1].compareTo("ALREADYJOINED") == 0) {
                    printToPhone("Already joined game, you cannot join again!");
                }

                else if(info[1].compareTo("WAITJOIN") == 0) {
                    printToPhone("Game already started, wait to join next round, be fast!");
                }
                else if(info[1].compareTo("PLAYERCHOICE") == 0) {
                    printToPhone(info[2] + " has made a choice.");
                }
                else if(info[1].compareTo("PLAYBLOCKED") == 0) {
                    printToPhone("You have already made a choice.");
                }
                else if(info[1].compareTo("PLAYERBLOCKED") == 0) {
                    printToPhone("You are not in the game and can not play, join or wait for next round.");
                }
                else if(info[1].compareTo("CHOICES") == 0) {
                    printToPhone("\n****************CHOICES***************** \n" +
                            info[2] + " picked: " + info[3] + "\n" +
                            info[4] + " picked: " + info[5] + "\n" +
                            info[6] + " picked: " + info[7] + "\n");
                }
                else if(info[1].compareTo("RESULT") == 0) {
                    printToPhone("*************SCOREBOARD************** \n" +
                            "The result for: " + info[2] + " round score: " + info[3] + " total score: " + info[4] + "\n" +
                            "The result for: " + info[5] + " round score: " + info[6] + " total score: " + info[7] + "\n" +
                            "The result for: " + info[8] + " round score: " + info[9] + " total score: " + info[10] + "\n" +
                            "You can join another round. \n");
                    //joinButton.setVisibility(View.VISIBLE);

                }
                else if (info[1].compareTo("USER") == 0) {
                    printToPhone(info[2] + " has joined the server.");
                }

                else if(info[1].compareTo("DISCONNECT") == 0) {
                    printToPhone(info[2] + " has left the game.");
                }
                else if(info[1].compareTo("NEWGAME") == 0) {
                    printToPhone("New game has started, Please choose to play rock, paper or scissor");
                    /*rockButton.setVisibility(View.VISIBLE);
                    paperButton.setVisibility(View.VISIBLE);
                    scissorButton.setVisibility(View.VISIBLE);*/
                }
                else if(info[1].compareTo("LOSTDATA") == 0) {
                    printToPhone("Message from server was lost.");
                }
            } catch(MessageException me) {
                printToPhone("Message from server could not be handled properly.");
            }
        }

        private void printToPhone(String msg) {
            textView.append(msg + "\n");
        }
    }
}
