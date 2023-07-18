import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;
import java.time.LocalDate;

public class EntretienControllers {
    @FXML
    private TableView<Entretien> tableview;
    @FXML
    private TableColumn<Entretien, Integer> num_entrer;
    @FXML
    private TableColumn<Entretien, Integer> num_service;
    @FXML
    private TableColumn<Entretien, String> immatriculation;
    @FXML
    private TableColumn<Entretien, String> noms;
    @FXML
    private TableColumn<Entretien, Date> date;

    @FXML
    private TextField num_entrer_field;
    @FXML
    private TextField num_service_field;
    @FXML
    private TextField immatriculation_field;
    @FXML
    private TextField noms_field;
    @FXML
    private DatePicker date_field;

    @FXML
    private void initialize() {
        num_entrer.setCellValueFactory(new PropertyValueFactory<>("numEntrer"));
        num_service.setCellValueFactory(new PropertyValueFactory<>("numService"));
        immatriculation.setCellValueFactory(new PropertyValueFactory<>("immatriculation"));
        noms.setCellValueFactory(new PropertyValueFactory<>("noms"));
        date.setCellValueFactory(new PropertyValueFactory<>("date"));

        tableview.setItems(getEntretienList());
    }

    private ObservableList<Entretien> getEntretienList() {
        ObservableList<Entretien> entretiens = FXCollections.observableArrayList();
        String url = "jdbc:mysql://localhost:3306/votre_base_de_donnees";
        String utilisateur = "root";
        String motDePasse = "";

        try (Connection connexion = DriverManager.getConnection(url, utilisateur, motDePasse);
             Statement statement = connexion.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM entretient")) {

            while (resultSet.next()) {
                int numEntrer = resultSet.getInt("numentre");
                int numService = resultSet.getInt("numserv");
                String immatriculation = resultSet.getString("matvoit");
                String noms = resultSet.getString("nomcli");
                Date date = resultSet.getDate("dateentre");

                Entretien entretien = new Entretien(numEntrer, numService, immatriculation, noms, date);
                entretiens.add(entretien);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entretiens;
    }

    @FXML
    private void enregistrer_btn() {
        int numEntrer = Integer.parseInt(num_entrer_field.getText());
        int numService = Integer.parseInt(num_service_field.getText());
        String immatriculation = immatriculation_field.getText();
        String noms = noms_field.getText();
        LocalDate date = date_field.getValue();

        // Créez un nouvel objet Entretien avec les valeurs saisies dans le formulaire
        Entretien nouvelEntretien = new Entretien(numEntrer, numService, immatriculation, noms, Date.valueOf(date));

        // Ajoutez le nouvel entretien à la liste des entretiens affichés dans le TableView
        tableview.getItems().add(nouvelEntretien);

        // Enregistrez également le nouvel entretien dans la base de données
        enregistrerEntretienDansBaseDeDonnees(nouvelEntretien);

        // Effacez les champs du formulaire après l'enregistrement
        effacerChampsDuFormulaire();
    }

    private void enregistrerEntretienDansBaseDeDonnees(Entretien entretien) {
        String url = "jdbc:mysql://localhost:3306/votre_base_de_donnees";
        String utilisateur = "root";
        String motDePasse = "";

        try (Connection connexion = DriverManager.getConnection(url, utilisateur, motDePasse);
             PreparedStatement preparedStatement = connexion.prepareStatement(
                     "INSERT INTO entretient (numentre, numserv, matvoit, nomcli, dateentre) VALUES (?, ?, ?, ?, ?)")) {

            preparedStatement.setInt(1, entretien.getNumEntrer());
            preparedStatement.setInt(2, entretien.getNumService());
            preparedStatement.setString(3, entretien.getImmatriculation());
            preparedStatement.setString(4, entretien.getNoms());
            preparedStatement.setDate(5, entretien.getDate());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void effacerChampsDuFormulaire() {
        num_entrer_field.clear();
        num_service_field.clear();
        immatriculation_field.clear();
        noms_field.clear();
        date_field.setValue(null);
    }

    public static class Entretien {
        private int numEntrer;
        private int numService;
        private String immatriculation;
        private String noms;
        private Date date;

        public Entretien(int numEntrer, int numService, String immatriculation, String noms, Date date) {
            this.numEntrer = numEntrer;
            this.numService = numService;
            this.immatriculation = immatriculation;
            this.noms = noms;
            this.date = date;
        }

        public int getNumEntrer() {
            return numEntrer;
        }

        public int getNumService() {
            return numService;
        }

        public String getImmatriculation() {
            return immatriculation;
        }

        public String getNoms() {
            return noms;
        }

        public Date getDate() {
            return date;
        }
    }
}

