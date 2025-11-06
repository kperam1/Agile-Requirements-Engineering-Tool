package com.agile.requirements.view;

/**
 * FXML View Enum
 * Defines all FXML views in the application
 */
public enum FxmlView {
    
    SIGNUP {
        @Override
        public String getTitle() {
            return "Agile RE Tool - Sign Up";
        }
        
        @Override
        public String getFxmlFile() {
            return "/fxml/Signup.fxml";
        }
        
        @Override
        public double getWidth() {
            return 500;
        }
        
        @Override
        public double getHeight() {
            return 700;
        }
    },
    
    LOGIN {
        @Override
        public String getTitle() {
            return "Agile RE Tool - Login";
        }
        
        @Override
        public String getFxmlFile() {
            return "/fxml/Login.fxml";
        }
        
        @Override
        public double getWidth() {
            return 400;
        }
        
        @Override
        public double getHeight() {
            return 500;
        }
    };
    
    public abstract String getTitle();
    public abstract String getFxmlFile();
    public abstract double getWidth();
    public abstract double getHeight();
}
