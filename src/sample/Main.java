package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ArrayList<Account> accountList = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            accountList.add(new Account(i, 100)) ;
        }

        String fileName = "account.dat";
        //Serialization
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))){
            for(Account account : accountList){
                out.writeObject(account);
            }
            System.out.println("Objects has been serialized");
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Account temp = null;
        //Deserialization
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))){
            for(int i = 0; i < 10; i++){
                temp = (Account) in.readObject();
                //System.out.println("Account number " +temp.getId() +" has balance of " +temp.getBalance());
            }
        }


        VBox mainPane = new VBox();
        mainPane.setSpacing(15);
        mainPane.setAlignment(Pos.CENTER);

        HBox enterAccountNumberPane = new HBox();
        enterAccountNumberPane.setAlignment(Pos.CENTER);
        enterAccountNumberPane.setSpacing(15);
        Label labelEnterAccountNumber = new Label("Enter an Account Number:");
        TextField textEnterAccountNumber = new TextField();
        enterAccountNumberPane.getChildren().addAll(labelEnterAccountNumber, textEnterAccountNumber);

        Button submitAccountNumber = new Button("Submit");
        mainPane.getChildren().addAll(enterAccountNumberPane, submitAccountNumber);

        submitAccountNumber.setOnAction(e -> {
            String accountNumber = textEnterAccountNumber.getText();
            textEnterAccountNumber.setText("");

            if(accountNumber.equals("")){
                VBox error = new VBox();
                error.setAlignment(Pos.CENTER);
                Label errorMessage = new Label("Empty Input Field");
                error.getChildren().add(errorMessage);

                Scene newScene = new Scene(error, 100, 100);
                Stage emptyInput = new Stage();
                emptyInput.setScene(newScene);
                emptyInput.show();
            }
            else{
                int index = -1;
                for(int i = 0; i < accountList.size(); i++){
                    if(accountList.get(i).toString().equals(accountNumber)){
                        index = i;
                    }
                }

                if(index == -1){
                    VBox registration = new VBox();
                    registration.setAlignment(Pos.CENTER);
                    registration.setSpacing(10);

                    Label welcome = new Label("Please create a new Account");
                    GridPane inputData = new GridPane();
                    inputData.setAlignment(Pos.CENTER);
                    registration.getChildren().addAll(welcome, inputData);

                    Label labelFirstName = new Label("First Name: ");
                    TextField textFirstName = new TextField();
                    inputData.add(labelFirstName,0,0);
                    inputData.add(textFirstName,1,0);

                    Label labelLastName = new Label("Last Name: ");
                    TextField textLastName = new TextField();
                    inputData.add(labelLastName, 0, 1);
                    inputData.add(textLastName,1, 1);

                    Label labelBalance = new Label("Balance Amount: ");
                    TextField textBalance = new TextField();
                    inputData.add(labelBalance, 0, 2);
                    inputData.add(textBalance, 1, 2);

                    Label labelPIN = new Label("PIN Number: ");
                    TextField textPIN = new TextField();
                    inputData.add(labelPIN, 0, 3);
                    inputData.add(textPIN, 1, 3);

                    Button register = new Button("Register");
                    registration.getChildren().add(register);


                    Scene newScene = new Scene(registration, 400,400);
                    Stage regStage = new Stage();
                    regStage.setScene(newScene);
                    regStage.show();
                    register.setOnAction(event -> {
                        String fName = textFirstName.getText();
                        String lName = textLastName.getText();
                        try{
                            int id = Integer.parseInt(accountNumber);
                            double balance = Double.parseDouble(textBalance.getText());
                            int pin = Integer.parseInt(textPIN.getText());
                            if(balance < 0 || pin < 0){
                                throw new Exception();
                            }
                            accountList.add(new Account(id, fName, lName, balance, pin));
                            VBox success = new VBox();
                            success.setSpacing(10);
                            Label labelSucess = new Label("New Account Created Successfully!");
                            Button btnSuccessOk = new Button("Ok");
                            btnSuccessOk.setPrefWidth(70);
                            success.setAlignment(Pos.CENTER);
                            success.getChildren().addAll(labelSucess, btnSuccessOk);
                            Scene successScene = new Scene(success, 200, 200);
                            regStage.setScene(successScene);
                            regStage.show();
                            btnSuccessOk.setOnAction(okEvent -> {
                                regStage.hide();
                            });
                        }
                        catch (Exception ex){
                            showError("Error", "Error creating Account object",
                                    "Could not create New Account with the dataset. Please verify the dataset and try again.");
                        }
                    });
                }
                else{
                    final int i = index;
                    NumberFormat formatter = new DecimalFormat("#0.00");
                    VBox account = new VBox();
                    account.setAlignment(Pos.CENTER);
                    account.setSpacing(10);

                    Label welcome = new Label("Welcome Account Number: " +accountList.get(i).getId());
                    Label options = new Label("What would you like to do?");

                    GridPane actionPanel = new GridPane();
                    actionPanel.setAlignment(Pos.CENTER);
                    actionPanel.setVgap(10);
                    actionPanel.setHgap(10);

                    Button btnBalance =  new Button("Check Balance");
                    Label txtBalance = new Label("Current balance: " +formatter.format(accountList.get(i).getBalance()));
                    actionPanel.add(btnBalance, 0, 0);
                    actionPanel.add(txtBalance, 1, 0);
                    btnBalance.setPrefWidth(100);
                    btnBalance.setOnAction(bal ->{
                        try{

                            txtBalance.setText("Current balance: " +formatter.format(accountList.get(i).getBalance()));
                        }
                        catch (Exception ex){
                            this.showError("Error", "Error in retriving Balance", "Couldnt not retrieve the current balance for the account");
                        }
                    });


                    Button btnWithdraw =  new Button("Withdraw");
                    TextField withdrawInput = new TextField();
                    actionPanel.add(btnWithdraw, 0, 1);
                    actionPanel.add(withdrawInput, 1, 1);
                    btnWithdraw.setPrefWidth(100);
                    btnWithdraw.setOnAction(wit ->{
                        try{
                            int wAmount = Integer.parseInt(withdrawInput.getText());
                            if(wAmount < 0){
                                throw new Exception();
                            }
                            if (accountList.get(i).getBalance() - wAmount >= 0) {
                                accountList.get(i).setBalance(accountList.get(i).getBalance() - wAmount);
                            }
                            else{
                                this.showError("Warning!!", "Can not withdraw", "You can not withdraw money more than your balance ");
                            }
                            withdrawInput.setText("");
                            txtBalance.setText("Current balance: " +formatter.format(accountList.get(i).getBalance()));
                        }
                        catch (Exception ex){
                            this.showError("Error", "Error in withdrawing money", "There was an error withdrawing money from the account");
                        }

                    });

                    Button btnDeposit =  new Button("Deposit");
                    TextField depositInput = new TextField();
                    actionPanel.add(btnDeposit, 0, 2);
                    actionPanel.add(depositInput, 1, 2);
                    btnDeposit.setPrefWidth(100);
                    btnDeposit.setOnAction(dep ->{
                        try{
                            int dAmount = Integer.parseInt(depositInput.getText());
                            if ( dAmount < 0 ) {
                                throw new Exception();
                            }
                            accountList.get(i).setBalance(accountList.get(i).getBalance() + dAmount);
                            depositInput.setText("");
                            txtBalance.setText("Current balance: " +formatter.format(accountList.get(i).getBalance()));
                        }
                        catch (Exception ex){
                            this.showError("Error", "Error in depositing money", "There was an error depositing money to the account");
                        }
                    });

                    Button btnExit =  new Button("Exit");
                    actionPanel.add(btnExit, 0, 3);
                    btnExit.setPrefWidth(100);
                    account.getChildren().addAll(welcome,options, actionPanel);

                    Scene newScene = new Scene(account, 400, 400);
                    Stage emptyInput = new Stage();
                    emptyInput.setScene(newScene);
                    emptyInput.show();
                    btnExit.setOnAction(ex -> {
                        primaryStage.hide();
                        emptyInput.hide();
                    });
                }
            }
        });

        primaryStage.setTitle("ATM");
        primaryStage.setScene(new Scene(mainPane, 500, 275));
        primaryStage.setResizable(false);
        primaryStage.show();

    }

    public void showError(String title, String header, String content){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
