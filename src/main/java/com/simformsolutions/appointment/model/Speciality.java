package com.simformsolutions.appointment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Speciality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private int specialityId;

    private String title;
    @JsonIgnore
    @OneToMany(targetEntity = Doctor.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "specialityId", referencedColumnName = "specialityId")
    private List<Doctor> doctors;

    public Speciality(String title) {
        this.title = title.toLowerCase();
    }

    public void setDoctor(Doctor d) {
        doctors.add(d);
    }

    public Doctor retrieveLastDoctor() {
        if (getDoctors().size() != 0)
            return this.getDoctors().get(getDoctors().size() - 1);
        else
            return null;
    }

}
