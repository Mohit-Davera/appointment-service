package com.simformsolutions.doctorappointmentsystem.service;

import com.simformsolutions.doctorappointmentsystem.Projection.DoctorInter;
import com.simformsolutions.doctorappointmentsystem.dto.UserAppointmentDTO;
import com.simformsolutions.doctorappointmentsystem.enums.AppointmentStatus;
import com.simformsolutions.doctorappointmentsystem.model.Appointment;
import com.simformsolutions.doctorappointmentsystem.model.Doctor;
import com.simformsolutions.doctorappointmentsystem.model.Schedule;
import com.simformsolutions.doctorappointmentsystem.model.User;
import com.simformsolutions.doctorappointmentsystem.repository.DoctorRepository;
import com.simformsolutions.doctorappointmentsystem.repository.ScheduleRepository;
import com.simformsolutions.doctorappointmentsystem.repository.SpecialityRepository;
import com.simformsolutions.doctorappointmentsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SpecialityRepository specialityRepository;

    public Doctor checkSchedule(List<Doctor> doctors){
       List<Doctor> freeDoctors = doctors.stream().filter(
               doctor -> !scheduleRepository.existsByDoctor(doctor)).toList();

       Iterator<Doctor> iterator =  doctors.iterator();
        List<Schedule> schedules;
       while(iterator.hasNext()){
           Doctor d =iterator.next();
           if(scheduleRepository.existsByDoctor(d)) {
               schedules = scheduleRepository.findByDoctor(d);
               schedules.stream().forEach(schedule -> System.out.println(schedule.toString()));
           }
       }
       return null;
    }

    public Object bookAppointment(Appointment appointment,int userId){
        String title = appointment.getSpeciality().toLowerCase();
        if(!specialityRepository.existsByTitle(title)){
            return "Enter Appropriate Specialty";
        }
        List<DoctorInter> doctorInterList =  doctorRepository.findDoctorsWithSpeciality(specialityRepository.findByTitle(title).getSpecialityId());
        List<Doctor> doctors = doctorInterList.stream().map(DoctorInter::getDoctorId).map(doctorRepository::findById).filter(Optional::isPresent).map(Optional::get).toList();
        Doctor scheduledDoctor = checkSchedule(doctors);
        Doctor d = doctors.get(0);
        Optional<User> opUser = userRepository.findById(userId);

        //Will LOGIC to find free slot

        appointment.setDate(LocalDate.now());
        appointment.setTime(d.getEntryTime());
        appointment.setStatus(AppointmentStatus.BOOKED);
        if(opUser.isPresent())
        {
            User user = opUser.get();
            user.setAppointment(appointment);
            d.setAppointments(appointment);
            Schedule s = new Schedule(appointment.getTime(),appointment.getDate(),d,user,appointment);

            scheduleRepository.save(s);
            return new UserAppointmentDTO(appointment,d);
        }
        return "User Not Found";
    }
}
