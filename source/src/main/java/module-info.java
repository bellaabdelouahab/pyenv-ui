module com.github.bellaabdelouahab.pyenvcontroller.src {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.logging;

    opens com.github.bellaabdelouahab.pyenvcontroller.src to javafx.fxml;

    exports com.github.bellaabdelouahab.pyenvcontroller.src;
}