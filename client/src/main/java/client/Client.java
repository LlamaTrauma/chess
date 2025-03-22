package client;

public class Client {
    public static enum preLoginPrompt {
        HELP,
        QUIT,
        LOGIN,
        REGISTER
    }

    public static enum handleInputReturnFlag {
        CONTINUE,
        EXIT,
        QUIT
    }

    Client () {}

    public handleInputReturnFlag handlePreLoginInput(String input) {
        preLoginPrompt prompt;
        try {
            String command = input.split("\\s+")[0];
            prompt = preLoginPrompt.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            System.out.println("Invalid input\n");
            return handleInputReturnFlag.CONTINUE;
        }
        switch (prompt) {
            case HELP:
                handlePreLoginHelp();
                return handleInputReturnFlag.CONTINUE;
                break;
            case QUIT:
                System.out.println("goodbye");
                return handleInputReturnFlag.QUIT;
                break;
            case LOGIN:
                boolean error = handleLogin(input);
                if (error) {
                    return handleInputReturnFlag.EXIT;
                } else {
                    return handleInputReturnFlag.CONTINUE;
                }
                break;
            case REGISTER:
                boolean error = handleRegister(input);
                if (error) {
                    return handleInputReturnFlag.EXIT;
                } else {
                    return handleInputReturnFlag.CONTINUE;
                }
                break;
        }
    }

    public boolean handleLogin(String input) {
        String[] args = input.split("\\s+");
        if (args.length < 4) {
            System.out.println("Too few arguments provided\n");
            return false;
        } else if (args.length > 4) {
            System.out.println("Too many arguments provided\n");
            return false;
        }
        String username = args[1];
        String password = args[2];
        String email = args[3];
        return true;
    }

    public void handlePreLoginHelp() {
        System.out.println("register <username> <password> <email>\n");
        System.out.println("login <username> <password>\n");
        System.out.println("quit\n");
        System.out.println("help\n");
    }
}
