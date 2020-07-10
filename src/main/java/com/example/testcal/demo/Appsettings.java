package com.example.testcal.demo;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Getter
@Setter
@Entity
@Table(name = "Appsettings")

public class Appsettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long settingsId;
    String keyname;
    Long keyvalue;

    public Long getSettingsId() {
        return settingsId;
    }

    public void setSettingsId(Long settingsId) {
        this.settingsId = settingsId;
    }

    public String getKeyname() {
        return keyname;
    }

    public void setKeyname(String keyname) {
        this.keyname = keyname;
    }

    public Long getKeyvalue() {
        return keyvalue;
    }

    public void setKeyvalue(Long keyvalue) {
        this.keyvalue = keyvalue;
    }
    
}