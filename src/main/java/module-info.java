module com.ipsim {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ipsim to javafx.fxml;
    exports com.ipsim;
}
