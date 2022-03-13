package lk.ijse.dep8.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import lk.ijse.dep8.util.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CustomerManagementFormController {

    public TextField txtId;
    public TextField txtName;
    public TextField txtAddress;
    public TextField txtPicture;
    public TableView<Customer> tblCustomers;
    public TableColumn colId;
    public TableColumn colName;
    public TableColumn colAddress;
    public TableColumn colPicture;
    public TableColumn colOption;
    public Button btnBrowse;
    public Button btnSave;
    private Path filePath;

    public void initialize() {
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        TableColumn<Customer, ImageView> colPicture = (TableColumn<Customer, ImageView>) tblCustomers.getColumns().get(3);
        TableColumn<Customer, Button> lastCol = (TableColumn<Customer, Button>) tblCustomers.getColumns().get(4);

        colPicture.setCellValueFactory(param -> {
            byte[] picture = param.getValue().getPicture();
            ByteArrayInputStream bais = new ByteArrayInputStream(picture);

            ImageView imageView = new ImageView(new Image(bais));
            imageView.setFitHeight(75);
            imageView.setFitWidth(75);
            imageView.setVisible(true);
            return new ReadOnlyObjectWrapper<>(imageView);
        });

        lastCol.setCellValueFactory(param -> {
            Button btnDelete = new Button("Delete");
            btnDelete.setOnAction((event -> tblCustomers.getItems().remove(param.getValue())));
            return new ReadOnlyObjectWrapper<>(btnDelete);
        });

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedCustomer) -> {
            btnSave.setText(selectedCustomer == null ? "Save Customer" : "Update Customer");
            if (selectedCustomer == null) return;

            txtId.setText(selectedCustomer.getId());
            txtName.setText(selectedCustomer.getName());
            txtAddress.setText(selectedCustomer.getAddress());

            if (selectedCustomer.getPicture() != null) {
                txtPicture.setText("[PICTURE]");
            }

        });

        txtId.setText(generateNewId());
        createDir();
    }

    public void btnBrowseOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter
                ("Images", "*.jpeg", "*.jpg", "*.gif", "*.png", "*.bmp"));
        fileChooser.setTitle("Select an image");
        File file = fileChooser.showOpenDialog(btnBrowse.getScene().getWindow());
        txtPicture.setText(file != null ? file.getAbsolutePath() : "");
    }

    public void btnCancelOnAction(ActionEvent actionEvent) {
        resetControls();
    }

    public void btnSaveOnAction(ActionEvent actionEvent) throws IOException {
        validate();
        byte[] picture;
        if (!txtPicture.getText().equals("[PICTURE]")){
            picture = Files.readAllBytes(Paths.get(txtPicture.getText()));
        }else{
            picture = tblCustomers.getSelectionModel().getSelectedItem().getPicture();
        }
        if (btnSave.getText().equals("Save Customer")) {
            tblCustomers.getItems().add(new Customer(txtId.getText(), txtName.getText(), txtAddress.getText(), picture));
        } else {
            Customer customer = tblCustomers.getSelectionModel().getSelectedItem();
            customer.setName(txtName.getText());
            customer.setAddress(txtAddress.getText());
            customer.setPicture(picture);
        }
        tblCustomers.refresh();
        saveData();
        resetControls();
    }

    private void validate () {
        if (!txtId.getText().matches("C\\d{3}") || tblCustomers.getItems().stream().anyMatch( c -> c.getId().equalsIgnoreCase(txtId.getText()))) {
            txtId.requestFocus();
            txtId.selectAll();
            return;
        } else if (txtName.getText().trim().isEmpty()) {
            txtName.requestFocus();
            txtName.selectAll();
            return;
        } else if (txtAddress.getText().trim().isEmpty()) {
            txtAddress.requestFocus();
            txtAddress.selectAll();
            return;
        } else if (txtPicture.getText().isEmpty()) {
            btnBrowse.requestFocus();
            return;
        }
    }

    private void createDir() {
        try {
            String homeDirPath = System.getProperty("user.home");
            Path path = Paths.get(homeDirPath, "DEP","DatabaseFile");

            if (!Files.isDirectory(path)) {
                Files.createDirectory(path);
            }
            filePath = Paths.get(path.toString(), "Customers.dep8");
            System.out.println(Files.exists(filePath));

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveData(){
        try {
            List<Customer> customers = tblCustomers.getItems();
            OutputStream outputStream = Files.newOutputStream(filePath);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            for (Customer customer : customers) {
                objectOutputStream.writeObject(customer);
            }
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetControls () {
        txtId.clear();
        txtName.clear();
        txtAddress.clear();
        txtPicture.clear();
        btnSave.setText("Save Customer");
        txtId.setText(generateNewId());
    }

    private String generateNewId() {
        if (tblCustomers.getItems().isEmpty()) {
            return "C001";
        } else {
            ObservableList<Customer> customers = tblCustomers.getItems();
            int lastCustomerId = Integer.parseInt(customers.get(customers.size() - 1).getId().replace("C", ""));
            return String.format("C%03d", (lastCustomerId + 1));
        }
    }
}
