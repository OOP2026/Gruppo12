package model;

/**
 * Modello base di un utente autenticabile del sistema.
 * Conserva credenziali e comportamento comune alle specializzazioni.
 */
public class Utente {
    private String login;
    private String password;

    public Utente(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public boolean login(String login, String password) {
        return ( login.equals(this.login) && password.equals(this.password));
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return login;
    }
}
