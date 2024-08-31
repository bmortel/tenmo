package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.text.NumberFormat;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.*;

/**
 * The main application class for the TEnmo money transfer system.
 */
public class App {

    // Base URL for the API
    private static final String API_BASE_URL = "http://localhost:8080/";

    // Service instances
    private final ConsoleService consoleService = new ConsoleService();
    private final AccountService accountService = new AccountService();
    private final TransferService transferService = new TransferService();
    private final UserService userService = new UserService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    // Current user information
    private AuthenticatedUser currentUser;
    private int currUserId;
    private String currUserName;
    private Account currUserAccount;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    /**
     * Starts the application by printing a greeting, handling login, and displaying
     * the main menu.
     */
    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            this.currUserId = currentUser.getUser().getId();
            this.currUserName = currentUser.getUser().getUsername();
            this.currUserAccount = accountService.getAccountByUserId(currUserId);
            mainMenu();
        }
    }

    /**
     * Handles the login menu options.
     */
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    /**
     * Handles the user registration process.
     */
    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    /**
     * Handles the user login process.
     */
    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
        accountService.setCurrentUser(currentUser);
        transferService.setAuthUser(currentUser);
        userService.setAuthUser(currentUser);
    }

    /**
     * Displays the main menu and handles user selections.
     */
    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

    /**
     * Displays the current account balance.
     */
    private void viewCurrentBalance() {
        BigDecimal balance = accountService.getBalanceByUserId();
        if (balance == null) {
            System.err.println("Error: Balance is null");
        } else {
            try {
                System.out.println(
                        "Your current account balance is: " + NumberFormat.getCurrencyInstance().format(balance));
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Cannot format balance - " + e.getMessage());
            }
        }
    }

    /**
     * Displays the transfer history for the current user.
     */
    private void viewTransferHistory() {
        int menuSelection = -1;
        Transfer[] transfers = transferService.getTransfersByUserId(currUserId);
        while (menuSelection != 0) {
            if (transfers == null || transfers.length == 0) {
                System.out.println("No past transfers");
                break;
            } else {
                menuSelection = transferHistoryMenu(transfers);
            }
        }
    }

    private void viewPendingRequests() {

        int menuSelection = -1;
        while (menuSelection != 0) {
            Transfer[] transfers = transferService.getPendingTransfers((currUserAccount.getAccountId()));
            if (transfers == null || transfers.length == 0) {
                System.out.println("No transfers to approve or reject");
                break;
            } else {
                menuSelection = pendingRequestsMenu(transfers);
            }
            if (menuSelection != 0) {
                approveOrReject(menuSelection);
                menuSelection = 0;
            }
        }
    }

    /**
     * Method to prompt the user for the amount to send and complete the transfer.
     * Validates the amount, user balance, and updates the account balances
     * accordingly.
     */
    private void sendBucks() {

        int menuSelection = -1;
        while (menuSelection != 0) {
            User[] users = userService.getUsers();
            if (users.length == 1) {
                System.out.println("No other users found");
                return;
            }
            printUsers(users);
            menuSelection = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel):");
            if (menuSelection == 0) {
                return;
            } else if (menuSelection == currentUser.getUser().getId()) {
                System.err.println("Cannot send TE bucks to self");
            } else if (!foundUser(menuSelection, users)) {
                System.err.println("Invalid ID");
            } else {
                getAmountAndSend(menuSelection);
                menuSelection = 0;
            }

        }

    }

    /**
     * Method to prompt the user for the amount to request and create a transfer
     * request.
     * Validates the amount and creates the transfer request to the selected user.
     */
    private void requestBucks() {

        int menuSelection = -1;
        while (menuSelection != 0) {
            User[] users = userService.getUsers();
            if (users.length == 1) {
                System.out.println("No other users found");
                break;
            }
            printUsers(users);
            menuSelection = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel):");
            if (menuSelection == 0) {
                return;
            } else if (menuSelection == currentUser.getUser().getId()) {
                System.err.println("Cannot request TE bucks from self");
            } else if (!foundUser(menuSelection, users)) {
                System.err.println("Invalid ID");
            } else {
                getAmountAndRequest(menuSelection);
                menuSelection = 0;
            }

        }

    }

    /**
     * Method to initiate the process of sending TE bucks to another user.
     * Displays a list of users to select from and prompts for the ID of the user to
     * send to.
     * Handles validation of user input before sending the request.
     */
    private void getAmountAndSend(int userId) {

        BigDecimal amount = null;
        amount = consoleService.promptForBigDecimal("Enter amount: ");
        if (amount.doubleValue() <= 0) {
            System.err.println("Amount cannot be zero or negative");
        } else if (accountService.getBalanceByUserId().compareTo(amount) < 0) {
            System.err.println("Insufficient balance");
        } else {
            Account otherAccount = accountService.getAccountByUserId(userId);
            Transfer newTransfer = new Transfer(2, 2, currUserAccount.getAccountId(), otherAccount.getAccountId(),
                    amount);
            transferService.createTransfer(newTransfer);
            currUserAccount
                    .setBalance(currUserAccount.getBalance().subtract(amount));
            otherAccount.setBalance(otherAccount.getBalance().add(amount));
            accountService.updateAccountBalance(currUserAccount);
            accountService.updateAccountBalance(otherAccount);
        }
    }

    /**
     * Method to initiate the process of requesting TE bucks from another user.
     * Displays a list of users to select from and prompts for the ID of the user to
     * request from.
     * Handles validation of user input before sending the request.
     */
    private void getAmountAndRequest(int userId) {
        // Code implementation for getting amount and making a transfer request

        BigDecimal amount = null;
        amount = consoleService.promptForBigDecimal("Enter amount: ");
        if (amount.doubleValue() <= 0) {
            System.err.println("Amount cannot be zero or negative");
        } else {
            Account otherAccount = accountService.getAccountByUserId(userId);
            Transfer newTransfer = new Transfer(1, 1, otherAccount.getAccountId(), currUserAccount.getAccountId(),
                    amount);
            transferService.createTransfer(newTransfer);
        }
    }

    /**
     * Displays a menu of transfer history and allows the user to select a
     * transfer to view details.
     * Prints transfer details if a valid transfer ID is provided.
     */
    private int transferHistoryMenu(Transfer[] transfers) {
        int menuSelection;
        System.out.println("-----------------------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID         From/To                  Amount          Status");
        System.out.println("-----------------------------------------------------------");
        for (Transfer transfer : transfers) {
            String toOrFrom = null;
            String otherPerson = null;
            if (userService.getUserByAccountId(transfer.getAccountFrom()).equals(currUserName)) {
                toOrFrom = "To:";
                otherPerson = userService.getUserByAccountId(transfer.getAccountTo());
            } else {
                toOrFrom = "From:";
                otherPerson = userService.getUserByAccountId(transfer.getAccountFrom());
            }
            String leftPad = "%-10d %s %-18s %-16s %s%n";
            System.out.printf(leftPad, transfer.getTransferId(), toOrFrom, otherPerson,
                    NumberFormat.getCurrencyInstance().format(transfer.getAmount()),
                    getStatusDesc(transfer.getTransferStatusId()));

        }
        System.out.println("---------");
        menuSelection = consoleService
                .promptForMenuSelection("\nPlease enter transfer ID to view details (0 to cancel): ");
        if (menuSelection != 0 && !findAndPrintTransfer(menuSelection)) {
            System.err.println("\nInvalid input");
        }
        return menuSelection;
    }

    /**
     * Finds and prints the details of a specific transfer based on the
     * transfer ID.
     * Returns true if the transfer is found and printed, false otherwise.
     */
    private boolean findAndPrintTransfer(int transferId) {
        Transfer transfer = transferService.getTransferById(transferId);
        if (transfer != null) {
            System.out.println("--------------------------------------------\r\n" + //
                    "Transfer Details\r\n" + //
                    "--------------------------------------------");
            System.out.println("Id: " + transfer.getTransferId());
            System.out.println("From: " + userService.getUserByAccountId(transfer.getAccountFrom()));
            System.out.println("To: " + userService.getUserByAccountId(transfer.getAccountTo()));
            System.out.println("Type: " + getTypeDesc(transfer.getTransferTypeId()));
            System.out.println("Status: " + getStatusDesc(transfer.getTransferStatusId()));
            System.out.println("Amount: " + NumberFormat.getCurrencyInstance().format(transfer.getAmount()));
            consoleService.pause();
            return true;
        }
        return false;
    }

    /**
     * Display a menu of pending transfer requests and allow the user to
     * approve or reject a request.
     */
    private int pendingRequestsMenu(Transfer[] transfers) {
        // Code implementation for displaying pending transfer requests menu

        int menuSelection;
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID         To                        Amount");
        System.out.println("-------------------------------------------");

        for (Transfer transfer : transfers) {
            String leftPad = "%-10d %-20s%s%n";
            System.out.printf(leftPad, transfer.getTransferId(),
                    userService.getUserByAccountId(transfer.getAccountTo()),
                    NumberFormat.getCurrencyInstance().format(transfer.getAmount()));
        }
        System.out.println("---------");
        menuSelection = consoleService
                .promptForMenuSelection("Please enter transfer ID to approve/reject (0 to cancel):");
        return menuSelection;
    }

    /**
     * Method to approve or reject a specific transfer request.
     * Updates the transfer status and account balances accordingly.
     */
    private void approveOrReject(int menuSelection) {
        Transfer transferToApproveOrReject = transferService.getTransferById(menuSelection);
        if (transferToApproveOrReject == null || transferToApproveOrReject.getTransferStatusId() != 1) {
            System.err.println("\nInvalid input");
        } else {
            Account otherAccount = accountService.getAccountById(transferToApproveOrReject.getAccountTo());
            System.out.println("1: Approve\r\n" + //
                    "2: Reject\r\n" + //
                    "0: Don't approve or reject\r\n" + //
                    "---------\r\n");
            int choice = consoleService.promptForMenuSelection("Please choose an option:");
            if (choice == 1) {
                if (accountService.getBalanceByUserId().compareTo(transferToApproveOrReject.getAmount()) < 0) {
                    System.err.println("Insufficent balance to complete transfer");
                } else {
                    transferToApproveOrReject.setTransferStatusId(2);
                    currUserAccount
                            .setBalance(currUserAccount.getBalance().subtract(transferToApproveOrReject.getAmount()));
                    otherAccount.setBalance(otherAccount.getBalance().add(transferToApproveOrReject.getAmount()));
                    accountService.updateAccountBalance(currUserAccount);
                    accountService.updateAccountBalance(otherAccount);
                    transferService.updateTransfer(transferToApproveOrReject);

                }
            }
            if (choice == 2) {
                transferToApproveOrReject.setTransferStatusId(3);
                transferService.updateTransfer(transferToApproveOrReject);
            }
        }
    }

    /**
     * Method to check if a user with a specific ID exists in the provided array of
     * users.
     * Returns true if the user is found, false otherwise.
     */
    private boolean foundUser(int userId, User[] users) {
        boolean found = false;
        for (User user : users) {
            if (user.getId() == userId) {
                found = true;
            }
        }
        return found;
    }

    /**
     * Gets the description of a transfer type based on the type ID.
     */
    private String getTypeDesc(int id) {
        String type = null;
        if (id == 1) {
            type = "Request";
        } else {
            type = "Send";
        }
        return type;
    }

    /**
     * Gets the description of a transfer status based on the status ID.
     */
    private String getStatusDesc(int id) {
        String status = null;
        if (id == 1) {
            status = "Pending";
        } else if (id == 2) {
            status = "Approved";
        } else if (id == 3) {
            status = "Rejected";
        }
        return status;
    }

    /**
     * Prints the list of users
     * 
     * @param users
     */
    public void printUsers(User[] users) {
        System.out.println("-------------------------------------------\r\n" + //
                "Users\r\n" + //
                "ID          Name\r\n" + //
                "-------------------------------------------");
        for (User user : users) {
            String leftPad = "%-10d %s%n";
            if (!user.equals(currentUser.getUser())) {
                System.out.printf(leftPad, user.getId(), user.getUsername());
            }
        }
        System.out.println("---------");
    }
}
