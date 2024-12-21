module com.ipsim {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    requires java.xml;
    requires java.compiler;
    opens com.ipsim to javafx.fxml;
    exports com.ipsim;
    exports com.ipsim.interfaces;
    exports com.ipsim.components;
}
